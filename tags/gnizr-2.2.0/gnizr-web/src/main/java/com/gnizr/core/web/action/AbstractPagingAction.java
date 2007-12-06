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
package com.gnizr.core.web.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.interceptor.SessionAware;

public abstract class AbstractPagingAction extends AbstractAction implements
		SessionAware {

	protected static final Logger logger = Logger.getLogger(AbstractPagingAction.class);

	protected Map session;

	protected int page;

	protected int perPageCount;
	
	protected int computeOffset(int pageNum) {
		int offset = 0;
		if (pageNum == 1) {
			return offset;
		} else if (pageNum > 1) {
			offset = (int) (pageNum - 1) * getPerPageCount();
		}
		return offset;
	}

	@SuppressWarnings("unchecked")
	public int getPerPageCount() {
		if (perPageCount > 0) {
			return perPageCount;
		} else if (isSessionMode() == true) {
			Integer curPPC = (Integer) session.get(SessionConstants.PAGE_COUNT);
			if (curPPC != null && curPPC > 0) {
				return curPPC;
			}
		}
		return 10;
	}
	
	@SuppressWarnings("unchecked")
	protected void setNextPageNum(int curPage, int totalPage) {
		int nextPage = 0;
		if (totalPage >= 0) {
			if (curPage > 0 && curPage < totalPage) {
				nextPage = curPage + 1;
			}
		}
		if (isSessionMode() == true) {
			session.put(SessionConstants.NEXT_PAGE_NUM, nextPage);
		}
	}

	@SuppressWarnings("unchecked")
	protected void setPreviousPageNum(int curPage, int totalPage) {
		int prevPage = 0;
		if (totalPage >= 0) {
			if (curPage > 0 && curPage <= totalPage) {
				prevPage = curPage - 1;
			}
		}
		if (isSessionMode() == true) {
			session.put(SessionConstants.PREVIOUS_PAGE_NUM, prevPage);
		}
	}

	@SuppressWarnings("unchecked")
	protected void setTotalNumOfPages(int np) {
		if (isSessionMode() == true) {
			if (np >= 0) {
				session.put(SessionConstants.PAGE_TOTAL_COUNT, np);
			} else {
				session.put(SessionConstants.PAGE_TOTAL_COUNT, 0);
			}
		}
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public int getPage() {
		if (page <= 0) {
			page = 1;
		}
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@SuppressWarnings("unchecked")
	public void setPerPageCount(int count) {
		perPageCount = count;
	}

	@SuppressWarnings("unchecked")
	protected void initPagingAction() throws Exception {
		if (isSessionMode() == true) {
			if (perPageCount > 0) {
				session.put(SessionConstants.PAGE_COUNT, perPageCount);
			} else {
				Integer curPPC = (Integer) session
						.get(SessionConstants.PAGE_COUNT);
				if (curPPC == null || curPPC <= 0) {
					session.put(SessionConstants.PAGE_COUNT, 10);
				}
			}
		}
	}
	
	protected int computeMaxPageNumber(int perPageCount, int totalCount){
		int max = 1;
		if(totalCount > 0){
			if(perPageCount > 0){
				int tnp = totalCount / perPageCount;
				if ((totalCount % perPageCount) > 0) {
					tnp++;
				}
				if (tnp > 1) {
					max = tnp;
				}
			}
		}
		return max;
	}
}
