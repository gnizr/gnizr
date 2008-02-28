package com.gnizr.core.search;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * This class provides the implementation for building 
 * the search index for bookmark search. 
 * 
 * @author Harry Chen
 *
 */
public class SearchIndexManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8598646761200259815L;

	private static final Logger logger = Logger.getLogger(SearchIndexManager.class);
	
	private IndexStoreProfile profile;

	private LinkedBlockingQueue<Document> documentQueue;

	private Thread workerThread;

	// used to signal workerThread that this class instance is shutting down.
	private static final Document POISON_DOC = new Document();
	
	/**
	 * Initializes this class instance and outputs errors in the log file if 
	 * the <code>profile</code> is <code>null</code>.
	 */
	public void init(){
		if(profile == null){
			logger.error("Can't initialize search index database -- profile undefined.");
			// quit. as soon as 
			return;
		}
		File dir = null;
		if(profile.getDirectoryPath() == null){
			logger.error("Undefined search index database directory: null");
		}else{
			dir = new File(profile.getDirectoryPath());
			try{
				if(dir.exists() == true && dir.isDirectory() == false){
					logger.error("Defined search index databse path is not a directory: " + profile.getDirectoryPath());
					return;
				}				
			}catch(Exception e){
				logger.error(e.toString());
			}
		}
		documentQueue = new LinkedBlockingQueue<Document>();
		workerThread = new Thread(new UpdateIndexWorker());
		workerThread.start();
	}
	
	/**
	 * Cleans up the internal resources used by this class instance.
	 * 
	 */
	public void destroy() {
		if(workerThread != null){
			try{
				if(workerThread.isAlive() == true){
					requestIndexUpdate(POISON_DOC);
				}
			}catch(Exception e){
				logger.error("SearchIndexManager destory(): " + e);
			}
		}
	}
	
	/**
	 * Returns the profile used for configuring this class instance.
	 * @return current profile
	 */
	public IndexStoreProfile getProfile() {
		return profile;
	}

	/**
	 * Sets the profile used for configuring the class instance.
	 * @param profile profile to use
	 */
	public void setProfile(IndexStoreProfile profile) {
		this.profile = profile;
	}
	
	/**
	 * Adds a <code>Document</code> to the queue of documents to be
	 * updated in the search index database. The update request 
	 * is performed in an asynchronous fashion. There is no guarantee 
	 * that the update will be completed right 
	 * after this method has been called.
	 *  
	 * @param doc a document to be updated.
	 *
	 * @throws CorruptIndexException 
	 * @throws LockObtainFailedException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void requestIndexUpdate(Document doc) throws CorruptIndexException,
			LockObtainFailedException, IOException, InterruptedException {
		if (doc != null) {
			documentQueue.put(doc);
		}
	}

	private class UpdateIndexWorker implements Runnable {
		public void run() {
			while (true) {
				try {
					List<Document> partialList = new ArrayList<Document>();
					Document d = documentQueue.take();
					partialList.add(d);
					documentQueue.drainTo(partialList);
					logger.debug("UpdateIndexWorker: drainedTo partialList # of doc :" + partialList.size());
					if (partialList.isEmpty() == false) {
						logger.debug("Updating total # of doc index: " + partialList.size());
						doUpdate(partialList);					
					}
				} catch (InterruptedException e) {
					break;
				} 
			}
		}
		
		private void doUpdate(List<Document> docs){
			// TODO: 
		}
	}
	
	
}
