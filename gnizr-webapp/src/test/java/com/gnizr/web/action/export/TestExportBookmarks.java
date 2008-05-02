package com.gnizr.web.action.export;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.web.junit.GnizrWebappTestBase;

public class TestExportBookmarks extends GnizrWebappTestBase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestExportBookmarks.class.getResourceAsStream("/TestExportBookmarks-input.xml"));
	}

}
