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
package com.gnizr.ext.delicious;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.AccountStatus;

public class DeliciousImportApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Missing required arguments.");
			System.exit(1);
		}
		Properties prpt = new Properties();
		prpt.load(new FileInputStream(args[0]));
		String dbdrv = prpt.getProperty("gnizr.db.driver");
		String dbUrl = prpt.getProperty("gnizr.db.url");
		String dbUser = prpt.getProperty("gnizr.db.username");
		String dbPass = prpt.getProperty("gnizr.db.password");
		String gnizrUser = prpt.getProperty("gnizr.import.user");
		String gnizrPassword = prpt.getProperty("gnizr.import.password");
		String gnizrEmail = prpt.getProperty("gnizr.import.email");
		String gnizrFullname = prpt.getProperty("gnizr.import.fullname");
		String deliUser = prpt.getProperty("gnizr.import.delicious.user");
		String deliPassword = prpt
				.getProperty("gnizr.import.delicious.password");

		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName(dbdrv);
		datasource.setUrl(dbUrl);
		datasource.setUsername(dbUser);
		datasource.setPassword(dbPass);

		GnizrDao gnizrDao = GnizrDao.getInstance(datasource);

		UserManager userManager = new UserManager(gnizrDao);
		BookmarkManager bookmarkManager = new BookmarkManager(gnizrDao);
		FolderManager folderManager = new FolderManager(gnizrDao);
		
		User gUser = new User();
		gUser.setUsername(gnizrUser);
		gUser.setPassword(gnizrPassword);
		gUser.setFullname(gnizrFullname);
		gUser.setEmail(gnizrEmail);
		gUser.setAccountStatus(AccountStatus.ACTIVE);
		gUser.setCreatedOn(GnizrDaoUtil.getNow());

		DeliciousImport deliciousImport = new DeliciousImport(deliUser,
				deliPassword, gUser, userManager, bookmarkManager, folderManager, true);
		ImportStatus status = deliciousImport.doImport();
		System.out.println("Del.icio.us Import Status: ");
		System.out.println("- total number: " + status.getTotalNumber());
		System.out.println("- number added: " + status.getNumberAdded());
		System.out.println("- number updated: "+ status.getNumberUpdated());
		System.out.println("- number failed: " + status.getNumberError());
	}

}
