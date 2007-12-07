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
package com.gnizr.db.dao.folder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FoldersParser {

	private static final Logger logger = Logger.getLogger(FoldersParser.class);
	
	private String parseString;
	private List<String> folderNames;
	
	public FoldersParser(String foldernames){
		logger.debug("new FolderParser: foldernames="+foldernames);
		this.parseString = foldernames;
		doParse();
	}
	
	private void doParse(){
		folderNames = new ArrayList<String>();		
		if(parseString != null){
			Scanner scanner = new Scanner(parseString.trim()).useDelimiter(",");
			while(scanner.hasNext()){
				String fn = scanner.next().trim();
				folderNames.add(fn);
			}
		}
	}
	
	public List<String> getFolderNames(){
		return folderNames;
	}
	 
}
