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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

/**
 * Creates and manages the search index of bookmarks. Search index is created using Lucene API.
 * The implementation for changing search index (i.e., add, delete or update a bookmark index) 
 * is executed in the separate internal thread.  
 * 
 * @author Harry Chen
 * @since 2.4.0
 *
 */
public class SearchIndexManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8598646761200259815L;

	private static final Logger logger = Logger.getLogger(SearchIndexManager.class);
	
	private SearchIndexProfile profile;

	private LinkedBlockingQueue<Request> documentQueue;

	private Thread workerThread;

	private UpdateIndexWorker worker;
	
	// used to signal workerThread that this class instance is shutting down.
	private static final Document POISON_DOC = new Document();
	
	private boolean forceIndexReset;

	private File indexDirectory;
	
	private static final String INDEX_DIR = "bmarks-idx";
	
	/**
	 * Creates an instance of this class. If a
	 * search index database exists, use it as it. 
	 */
	public SearchIndexManager(){
		this.forceIndexReset = false;
	}
	
	/**
	 * Creates an instance of this class and optionally defines
	 * whether or not to force
	 * an existing search index database should be cleared.
	 * 
	 * @param resetIndex <code>true</code> if an existing search index
	 * database should be cleared upon initialization. 
	 */
	public SearchIndexManager(boolean resetIndex){
		this.forceIndexReset = resetIndex;
	}
	
	/**
	 * Initializes this class instance and outputs errors in the log file if 
	 * the <code>profile</code> is <code>null</code>. Forces the index database to be cleared
	 * if the <code>resetIndex</code> is set to <code>true</code> in the constructor. Starts the internal
	 * thread for managing the search index database.
	 */
	public void init(){
		if(profile == null){
			logger.error("Can't initialize search index database -- profile undefined.");
			// quit. as soon as 
			return;
		}
		
		if(profile.getSearchIndexDirectory() == null){
			logger.error("Undefined search index database directory: null");
			throw new NullPointerException("Search index directory is undefined in the configuration file.");
		}else{
			File dir = new File(profile.getSearchIndexDirectory());
			if(dir.exists() && dir.isDirectory() == false){
				logger.error("Defined path is not a directory. Path: " + dir.toString());
				throw new RuntimeException("Defined search index directory is not a system directory.");
			}
			indexDirectory = new File(profile.getSearchIndexDirectory(),INDEX_DIR);			
		}
		
		if (forceIndexReset == true) {
			logger.info("Overwriting the existing index store, if it exists.");
			createEmptyIndexDirectory();
		}
		
		documentQueue = new LinkedBlockingQueue<Request>();
		worker = new UpdateIndexWorker();
		workerThread = new Thread(worker);		
		workerThread.setDaemon(true);
		workerThread.start();
	}
	
	private void createEmptyIndexDirectory(){		
		IndexWriter writer = null;
		try{
			writer = new IndexWriter(indexDirectory,DocumentCreator.createDocumentAnalyzer(),true);
		} catch (Exception e){
			logger.error("Unable to reset the search index. Path " + indexDirectory.toString(),e);
		}finally{
			if(writer != null){
				try {
					writer.optimize();
					writer.close();
				} catch (Exception e){
					logger.error(e);
				}
			}
		}
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
	public SearchIndexProfile getProfile() {
		return profile;
	}

	/**
	 * Sets the profile used for configuring the class instance.
	 * @param profile profile to use
	 */
	public void setProfile(SearchIndexProfile profile) {
		this.profile = profile;
	}
	
	/**
	 * Appends a <code>Document</code> to the queue of documents to be
	 * updated in the search index database. The update request 
	 * is performed in an asynchronous fashion. There is no guarantee 
	 * that the update will be completed right 
	 * after this method has been called.
	 *  
	 * @param doc a document to be updated.
	 * @throws InterruptedException 
	 *
	 */
	public void updateIndex(Document doc) throws InterruptedException {
		if (doc != null) {
			documentQueue.put(new Request(doc,UPD));
		}
	}
	
	/**
	 * Appends a <code>Document</code> to the queue of documents to be
	 * added in the search index database. The add request 
	 * is performed in an asynchronous fashion. There is no guarantee 
	 * that the add will be completed right 
	 * after this method has been called.
	 *  
	 * @param doc a document to be added.
	 * @throws InterruptedException 
	 *
	 */
	public void addIndex(Document doc) throws InterruptedException{
		if(doc != null){
			documentQueue.put(new Request(doc,ADD));
		}
	}

	/**
	 * Appends a <code>Document</code> to the queue of documents to be
	 * deleted from the search index database. The delete request 
	 * is performed in an asynchronous fashion. There is no guarantee 
	 * that the delete will be completed right 
	 * after this method has been called.
	 *  
	 * @param doc a document to be deleted.
	 * @throws InterruptedException 
	 *
	 */
	public void deleteIndex(Document doc) throws InterruptedException{
		if(doc != null){
			documentQueue.put(new Request(doc,DEL));
		}
	}
	
	/**
	 * Instructs this class to clear all index records
	 * from the existing search index database as soon as possible. 
	 * This request is executed in an asynchronous fashion. There
	 * is no guarantee that this request can be completed right after
	 * the call.
	 * @throws InterruptedException
	 */
	public void resetIndex() throws InterruptedException {
		documentQueue.put(new Request(null,RST));
	}
	
	private static final int ADD = 1;
	private static final int DEL = 2;
	private static final int UPD = 3;
	private static final int RST = 4;
	
	
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
	
	/**
	 * Finds the representative bookmark document for a ginve URL hash.  
	 * 
	 * @param urlHash a URL MD5 Hash.
	 * 
	 * @return a Lucene document of the representative bookmark
	 */
	public Document findLeadDocument(String urlHash){
		IndexReader reader = null;
		TermDocs termDocs = null;
		Document leadDoc = null;
		try{
			boolean exists = IndexReader.indexExists(indexDirectory);
			if(exists == true){
				reader = IndexReader.open(indexDirectory);
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
	
	/**
	 * Finds a non-representative bookmark document for a given URL hash.  
	 * 
	 * @param urlHash a URL MD5 Hash.
	 * 
	 * @return a Lucene document of a non-representative bookmark
	 */
	public Document findNonLeadDocument(String urlHash){
		IndexReader reader = null;
		TermDocs termDocs = null;
		Document leadDoc = null;
		try{
			boolean exists = IndexReader.indexExists(indexDirectory);
			if(exists == true){
				reader = IndexReader.open(indexDirectory);
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
		
		public UpdateIndexWorker(){
			estimateWorkLoad = documentQueue.size();
		}
		
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
							if(doc != null && POISON_DOC.equals(doc)){
								logger.debug("Terminate UpdateIndexWorker.");
								stopRunning = true;
							}else{		
								if(req.type == ADD && doc != null){
									doAdd(doc);
								}else if(req.type == UPD && doc != null){
									doUpdate(doc);
								}else if(req.type == DEL && doc != null){
									doDelete(doc);
								}else if(req.type == RST){
									doReset();
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
		
		private void doReset(){
			createEmptyIndexDirectory();
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
				Analyzer analyzer = DocumentCreator.createDocumentAnalyzer();
				writer = new IndexWriter(indexDirectory,analyzer);				
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
				Analyzer analyzer = DocumentCreator.createDocumentAnalyzer();
				writer = new IndexWriter(indexDirectory, analyzer);
				Term t = new Term(DocumentCreator.FIELD_BOOKMARK_ID,doc.get(DocumentCreator.FIELD_BOOKMARK_ID));
				writer.deleteDocuments(t);			
			}catch(Exception e){
				logger.error("Can't add documen to the index. Doc = " + doc + ", exception = " + e);
			}finally{
				if(writer != null){
					try {
						writer.optimize();
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
				Analyzer analyzer = DocumentCreator.createDocumentAnalyzer();
				writer = new IndexWriter(indexDirectory,analyzer);
				Term t = new Term(DocumentCreator.FIELD_BOOKMARK_ID,doc.get(DocumentCreator.FIELD_BOOKMARK_ID));
				writer.updateDocument(t, doc);			
			}catch(Exception e){
				logger.error("Can't add documen to the index. Doc = " + doc + ", exception = " + e);
			}finally{
				if(writer != null){
					try {
						writer.optimize();
						writer.close();
					} catch (Exception e){
						logger.error(e);
					}
				}
			}	
		}				
	}

	/**
	 * Returns the directory where the search index database is stored
	 * @return the <code>File</code> that represents the storage location 
	 * of the index. Even if the return value 
	 * is not <code>null</code>, there is no guarantee that this directory exists in
	 * the system.
	 * @return the directory of the search index database. 
	 */
	public File getIndexDirectory() {
		return indexDirectory;
	}
	
}
