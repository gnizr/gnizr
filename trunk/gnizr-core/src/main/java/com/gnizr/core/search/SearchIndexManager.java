/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.core.search;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

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

	private LinkedBlockingQueue<Request> documentQueue;

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
		documentQueue = new LinkedBlockingQueue<Request>();
		workerThread = new Thread(new UpdateIndexWorker());		
		workerThread.setDaemon(true);
		workerThread.start();
	}
	
	public boolean isActive(){
		return workerThread.isAlive();
	}
	
	/**
	 * Cleans up the internal resources used by this class instance.
	 * 
	 */
	public void destroy() {
		if(workerThread != null){
			try{
				if(workerThread.isAlive() == true){
					addIndex(POISON_DOC);
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
	 * @throws InterruptedException 
	 *
	 * @throws InterruptedException
	 */
	public void updateIndex(Document doc) throws InterruptedException {
		if (doc != null) {
			documentQueue.put(new Request(doc,UPD));
		}
	}
	
	public void addIndex(Document doc) throws InterruptedException{
		if(doc != null){
			documentQueue.put(new Request(doc,ADD));
		}
	}

	public void deleteIndex(Document doc) throws InterruptedException{
		if(doc != null){
			documentQueue.put(new Request(doc,DEL));
		}
	}
	
	private static final int ADD = 1;
	private static final int DEL = 2;
	private static final int UPD = 3;
	
	private class Request {
		Document doc;
		int type;
		
		public Request(Document doc, int type){
			this.doc = doc;
			this.type = type;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("req:"+type + ",doc:"+doc);
			return sb.toString();
		}
	}
	
	
	private class UpdateIndexWorker implements Runnable {
		public void run() {
			boolean stopRunning = false;
			while (true && stopRunning == false) {				
				try {
					List<Request> partialList = new ArrayList<Request>();
					Request d = documentQueue.take();
					partialList.add(d);
					documentQueue.drainTo(partialList);
					logger.debug("UpdateIndexWorker: drainedTo partialList # of request :" + partialList.size());
					if (partialList.isEmpty() == false) {
						logger.debug("Updating total # of doc: " + partialList.size());			
						for(Request req : partialList){							
							Document doc = req.doc;
							if(POISON_DOC.equals(doc)){
								logger.debug("Terminate UpdateIndexWorker.");
								stopRunning = true;
							}else{								
								processRequest(req);
							}
						}												
					}
				} catch (InterruptedException e) {
					logger.debug("UpdateIndexWorker is interrupted.");
					break;
				}
			}
		}
		
		public void processRequest(Request req){
			IndexWriter writer = null;
			try{
				writer = new IndexWriter(profile.getDirectoryPath(),new StandardAnalyzer());
				if(req.type == ADD){
					writer.addDocument(req.doc);
				}else if(req.type == DEL || req.type == UPD){
					String bmid = req.doc.get(DocumentCreator.FIELD_BOOKMARK_ID);
					Term term = new Term(DocumentCreator.FIELD_BOOKMARK_ID,bmid);
					if(req.type == DEL){
						writer.deleteDocuments(term);
					}else{
						writer.updateDocument(term, req.doc);
					}
				}
			}catch(Exception e){
				logger.error("Unable to process request to modify index: " + req.toString());
			}finally{
				if(writer != null){
					try {
						writer.close();
					} catch (CorruptIndexException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		
	}
	
	
}
