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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

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

	private UpdateIndexWorker worker;
	
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
		if (profile.isOverwrite() == true) {
			logger.info("Overwriting the existing index store, if it exists.");
			File f = new File(profile.getDirectoryPath());
			try {
				boolean isOkay = deleteDir(f);
				logger.debug("Delete is okay? " + isOkay);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		documentQueue = new LinkedBlockingQueue<Request>();
		worker = new UpdateIndexWorker();
		workerThread = new Thread(worker);		
		workerThread.setDaemon(true);
		workerThread.start();
	}
	
	/**
	 * Checks whether the internal index thread is performing 
	 * any active indexing. This is an estimated result.
	 * @return <code>true</code> if the the internal thread
	 * is performing indexing. Returns <code>false</code> otherwise.
	 */
	public boolean isIndexProcessActive(){
		if(worker.getEstimatedWorkLoad() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Gets an estimated count of the 
	 * workload of the internal index thread.
	 * Use with caution. This number is only an estimated number.
	 * @return workload count
	 */
	public int getIndexProcessWorkLoad(){
		if(worker.getEstimatedWorkLoad() > 0){
			return worker.getEstimatedWorkLoad();
		}
		return 0;
	}
	
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}
	
	/**
	 * Checks whether the thread that is responsible for adding,
	 * deleting and updating bookmark index is still alive.
	 * 
	 * @return <code>true</code> if the worker thread that
	 * performs index modification is still alive. Returns <code>false</code>,
	 * otherwise.
	 */
	public boolean isActive(){
		return workerThread.isAlive();
	}
	
	/**
	 * Cleans up the internal resources used by this class instance.
	 * <p><b>IMPORTANT</b>: it's necessary to call this method if
	 * the client wants to this class to be properly unloaded from
	 * the JVM.</p> 
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
	
	public Document findLeadDocument(String urlHash){
		IndexReader reader = null;
		TermDocs termDocs = null;
		Document leadDoc = null;
		try{
			boolean exists = IndexReader.indexExists(profile.getDirectoryPath());
			if(exists == true){
				reader = IndexReader.open(profile.getDirectoryPath());
				Term key = new Term(DocumentCreator.FIELD_URL_MD5,urlHash);
				termDocs = reader.termDocs(key);					
				boolean found = false;
				while(termDocs.next() && found == false){
					int pos = termDocs.doc();
					// use FieldSelector for more efficient loading of Fields.
					// load only what's needed to determine a leading document
					Document d = reader.document(pos, new FieldSelector(){
						private static final long serialVersionUID = 1426724242925499003L;
						public FieldSelectorResult accept(String field) {
							if(field.equals(DocumentCreator.FIELD_INDEX_TYPE)){
								return FieldSelectorResult.LOAD_AND_BREAK;
							}else{
								return FieldSelectorResult.NO_LOAD;
							}
						}						
					});
					String[] values = d.getValues(DocumentCreator.FIELD_INDEX_TYPE);
					if(values != null){
						List<String> vList = Arrays.asList(values);
						if(vList.contains(DocumentCreator.INDEX_TYPE_LEAD) == true){
							leadDoc = reader.document(pos);
							found = true;
						}
					}
				}				
			}
		}catch(Exception e){
			logger.error("FindLeadDocument failed to find doc: " + urlHash + ", exception=" + e);
		}finally{
			try{
			if(termDocs != null){
				termDocs.close();
			}
			if(reader != null){
				reader.close();
			}
			}catch(Exception e){
				logger.error("FindLeadDocument can't close reader or termDocs: " + e);
			}
		}
		return leadDoc;
	}
	
	public Document findNonLeadDocument(String urlHash){
		IndexReader reader = null;
		TermDocs termDocs = null;
		Document leadDoc = null;
		try{
			boolean exists = IndexReader.indexExists(profile.getDirectoryPath());
			if(exists == true){
				reader = IndexReader.open(profile.getDirectoryPath());
				Term key = new Term(DocumentCreator.FIELD_URL_MD5,urlHash);
				termDocs = reader.termDocs(key);		
				boolean found = false;
				while(termDocs.next() && found == false){
					int pos = termDocs.doc();
					// use FieldSelector for more efficient loading of Fields.
					// load only what's needed to determine a leading document
					Document d = reader.document(pos, new FieldSelector(){
						private static final long serialVersionUID = 1426724242925499003L;
						public FieldSelectorResult accept(String field) {
							if(field.equals(DocumentCreator.FIELD_INDEX_TYPE)){
								return FieldSelectorResult.LOAD_AND_BREAK;
							}else{
								return FieldSelectorResult.NO_LOAD;
							}
						}
						
					});
					String[] values = d.getValues(DocumentCreator.FIELD_INDEX_TYPE);
					if(values != null){		
						List<String> vList = Arrays.asList(values);
						if(vList.contains(DocumentCreator.INDEX_TYPE_LEAD) == false){
							leadDoc = reader.document(pos);
							found = true;
						}
					}else{
						leadDoc = reader.document(pos);
						found = true;
					}
				}
			}
		}catch(Exception e){
			logger.error("FindLeadDocument failed to find doc hash: " + urlHash + ", exception=" + e);
		}finally{
			try{
			if(termDocs != null){
				termDocs.close();
			}
			if(reader != null){
				reader.close();
			}
			}catch(Exception e){
				logger.error("FindLeadDocument can't close reader or termDocs: " + e);
			}
		}
		return leadDoc;
	}
	
	
	private class UpdateIndexWorker implements Runnable {
		private List<Request> partialList = new ArrayList<Request>();
		int estimateWorkLoad = 0;
		public void run() {		
			boolean stopRunning = false;
			while (true && stopRunning == false) {				
				try {					
					Request d = documentQueue.take();
					partialList.add(d);
					documentQueue.drainTo(partialList);
					logger.debug("UpdateIndexWorker: drainedTo partialList # of request :" + partialList.size());
					if (partialList.isEmpty() == false) {
						estimateWorkLoad = partialList.size();
						logger.debug("Updating total # of doc: " + estimateWorkLoad);			
						for(Request req : partialList){							
							Document doc = req.doc;
							if(POISON_DOC.equals(doc)){
								logger.debug("Terminate UpdateIndexWorker.");
								stopRunning = true;
							}else{		
								if(req.type == ADD){
									doAdd(doc);
								}else if(req.type == UPD){
									doUpdate(doc);
								}else if(req.type == DEL){
									doDelete(doc);
								}							
							}
							estimateWorkLoad--;
						}												
					}
				} catch (InterruptedException e) {
					logger.debug("UpdateIndexWorker is interrupted.");
					break;
				}
				partialList.clear();
				estimateWorkLoad = 0;
			}
		}
		
		public int getEstimatedWorkLoad(){
			return estimateWorkLoad; 
		}
		
		private void doAdd(Document doc){
			if(doc == null){
				throw new NullPointerException("Can't add document to the index. Doc is NULL");
			}
			String urlHash = doc.get(DocumentCreator.FIELD_URL_MD5);
			if(urlHash == null){
				throw new NullPointerException("Can't add document to the index. Doc is missing URL hash. doc:" + doc);
			}
			Document leadDoc = findLeadDocument(urlHash);
			if(leadDoc == null){
				doc = DocumentCreator.addIndexTypeLead(doc);
			}
			IndexWriter writer = null;
			try{			
				writer = new IndexWriter(profile.getDirectoryPath(), new StandardAnalyzer());				
				writer.addDocument(doc);
			}catch(Exception e){
				logger.error("Can't add documen to the index. Doc = " + doc + ", exception = " + e);
			}finally{
				if(writer != null){
					try {
						writer.close();
					} catch (Exception e){
						logger.error(e);
					}
				}
			}
		}
		
		private void doDelete(Document doc){
			if(doc == null){
				throw new NullPointerException("Can't delete document from the index. Doc is NULL");
			}
			IndexWriter writer = null;
			try{
				writer = new IndexWriter(profile.getDirectoryPath(), new StandardAnalyzer());
				Term t = new Term(DocumentCreator.FIELD_BOOKMARK_ID,doc.get(DocumentCreator.FIELD_BOOKMARK_ID));
				writer.deleteDocuments(t);
			}catch(Exception e){
				logger.error("Can't add documen to the index. Doc = " + doc + ", exception = " + e);
			}finally{
				if(writer != null){
					try {
						writer.close();
					} catch (Exception e){
						logger.error(e);
					}
				}
			}
			String urlHash = doc.get(DocumentCreator.FIELD_URL_MD5);
			if(urlHash == null){
				throw new NullPointerException("Can't delete document from the index. Doc is missing URL hash. doc:" + doc);
			}
			Document leadDoc = findLeadDocument(urlHash);
			if(leadDoc == null){
				Document nonleadDoc = findNonLeadDocument(urlHash);
				if(nonleadDoc != null){
					nonleadDoc = DocumentCreator.addIndexTypeLead(nonleadDoc);
					doUpdate(nonleadDoc);
				}
			}
		}
		
		private void doUpdate(Document doc){
			if(doc == null){
				throw new NullPointerException("Can't update document in the index. Doc is NULL");
			}
			String urlHash = doc.get(DocumentCreator.FIELD_URL_MD5);
			if(urlHash == null){
				throw new NullPointerException("Can't update document in the index. Doc is missing URL hash. doc:" + doc);
			}
			IndexWriter writer = null;
			try{
				writer = new IndexWriter(profile.getDirectoryPath(), new StandardAnalyzer());
				Term t = new Term(DocumentCreator.FIELD_BOOKMARK_ID,doc.get(DocumentCreator.FIELD_BOOKMARK_ID));
				writer.updateDocument(t, doc);
			}catch(Exception e){
				logger.error("Can't add documen to the index. Doc = " + doc + ", exception = " + e);
			}finally{
				if(writer != null){
					try {
						writer.close();
					} catch (Exception e){
						logger.error(e);
					}
				}
			}	
		}				
	}
	
	
}
