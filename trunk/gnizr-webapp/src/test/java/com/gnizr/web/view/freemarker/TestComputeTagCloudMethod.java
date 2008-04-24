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
package com.gnizr.web.view.freemarker;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.tag.TagDao;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.SimpleHash;

public class TestComputeTagCloudMethod extends GnizrWebappTestBase {

	private TagDao tagDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagDao = getGnizrDao().getTagDao();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExec() throws Exception{
		List<Tag> tags = tagDao.findTag(50,TagDao.SORT_ALPH);
		
		List<Object> args = new ArrayList<Object>();		
		CollectionModel cm = new CollectionModel(tags,new BeansWrapper());
		args.add(cm);
		
		ComputeTagCloudMethod method = new ComputeTagCloudMethod();
		SimpleHash map = (SimpleHash)method.exec(args);
		assertEquals(6,map.size());
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestComputeTagCloudMethod.class.getResourceAsStream("/TestComputeTagCloudMethod-input.xml"));	
	}

}
