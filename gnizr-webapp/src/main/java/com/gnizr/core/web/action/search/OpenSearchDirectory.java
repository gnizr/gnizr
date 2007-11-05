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
package com.gnizr.core.web.action.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gnizr.core.web.action.AbstractLoggedInUserAction;

public class OpenSearchDirectory extends AbstractLoggedInUserAction {

	private static final String OPEN_SEARCH_NS = "http://a9.com/-/spec/opensearch/1.1/";
	private static final String ELM_SHORT_NAME = "ShortName";
	private static final String ELM_DESCRIPTION = "Description";
	private static final String ELM_TAGS = "Tags";
	private static final String ELM_URL = "Url";
	private static final String ATT_TEMPLATE = "template";
	private static final String ATT_TYPE = "type";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3059854998950870761L;

	private static final Logger logger = Logger
			.getLogger(OpenSearchDirectory.class);

	private List<OpenSearchService> services;

	public OpenSearchDirectory(String[] serviceDescriptionUrl) {
		services = new ArrayList<OpenSearchService>();
		if (serviceDescriptionUrl != null) {
			for (String url : serviceDescriptionUrl) {
				OpenSearchService srv = readServiceDescription(url);
				if (srv != null) {
					logger
							.info("Add OpenSearch Service: "
									+ srv.getShortName());
					services.add(srv);
				}
			}
		}
	}

	private OpenSearchService readServiceDescription(String url){
		OpenSearchService aService = null;
		String sname = null;
		String dsp = null;
		String tags = null;
		String urlpttn = null;
		String type = null;
		try {
			DocumentBuilder domBldr = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = domBldr.parse(url);
			if (doc != null) {
				Element root = doc.getDocumentElement();
				NodeList nl = root.getElementsByTagName(
						ELM_SHORT_NAME);
				if (nl != null && nl.getLength() > 0) {
					sname = nl.item(0).getFirstChild().getNodeValue();
				}
				nl = root.getElementsByTagName(ELM_DESCRIPTION);
				if (nl != null && nl.getLength() > 0) {
					dsp = nl.item(0).getFirstChild().getNodeValue();
				}
				nl = root.getElementsByTagName(ELM_TAGS);
				if (nl != null && nl.getLength() > 0) {
					tags = nl.item(0).getFirstChild().getNodeValue();
				}
				nl = root.getElementsByTagName(ELM_URL);
				if (nl != null && nl.getLength() > 0) {
					Node urlNode = null;
					for(int i = 0; i < nl.getLength(); i++){						
						urlNode = nl.item(i);
						NamedNodeMap attmap = urlNode.getAttributes();
						Node typeNode = attmap.getNamedItem(ATT_TYPE);
						if (typeNode != null) {
							type = typeNode.getNodeValue();
							if(type.equals("text/xml")){
								break;
							}else{
								urlNode = null;
							}
						}
					}
					if(urlNode != null){
						NamedNodeMap attmap = urlNode.getAttributes();
						Node templateNode = attmap.getNamedItem(ATT_TEMPLATE);
						if(templateNode != null){
							urlpttn = templateNode.getNodeValue();
						}
					}
				}				
			}
		} catch (ParserConfigurationException e) {
			logger.error("Internal error: unable to create DOM parser", e);
		} catch (SAXException e) {
			logger.error("Error reading OpenSearch Service Description XML: "
					+ url, e);
		} catch (IOException e) {
			logger.error("Error reading OpenSearch Service Description XML: "
					+ url, e);
		}
		
		if(urlpttn != null){
			aService = new OpenSearchService();
			aService.setServiceUrlPattern(urlpttn);
			aService.setDescription(dsp);
			aService.setShortName(sname);
			aService.setType(type);
			if(tags != null){
				aService.setTags(tags.split(" "));
			}
			if(urlpttn.contains("{startPage}")){
				aService.setSupportsPageBased(true);
			}
			if(urlpttn.contains("{startIndex}")){
				aService.setSupportsIndexBased(true);
			}
		}
		return aService;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}

	@Override
	protected String go() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OpenSearchService> getServices() {
		return services;
	}

}
