package com.gnizr.web.action.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.web.action.AbstractAction;

public class SearchQuerySuggest extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070010776104912157L;
	private static final Logger logger = Logger
			.getLogger(SearchQuerySuggest.class);

	private String q;
	private Map<String, String> keywords = new HashMap<String, String>();
	private String[] defaultsList = { "web", "web blog", "web program",
			"webber", "class:facility", "class:facility_north" };

	public Map<String, String> getKeywords() {
		return keywords;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("Start SearchTermSuggestion. q = " + getQ()
				+ " keywords size: " + keywords.size());
		if (q != null) {
			for (String k : defaultsList) {
				String qt = q.trim();
				if (k.equalsIgnoreCase(qt) == false && k.startsWith(q)) {
					keywords.put(k, k);
				}
			}
		}
		return SUCCESS;
	}

}
