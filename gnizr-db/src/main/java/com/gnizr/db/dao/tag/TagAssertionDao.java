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
package com.gnizr.db.dao.tag;

import java.io.Serializable;
import java.util.List;

import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;


public interface TagAssertionDao extends Serializable {

	public int createTagAssertion(TagAssertion asrt);

	public TagAssertion getTagAssertion(int id);

	public boolean deleteTagAssertion(int id);

	public boolean updateTagAssertion(TagAssertion asrt);

	public List<TagAssertion> findTagAssertion(User user);

	public List<TagAssertion> findTagAssertion(User user, UserTag subjectTag,
			TagProperty tagPrpt, UserTag objectTag);

	/** 
	 * Finds a list of <code>Tag</code> that are related to the input <code>tag</code>.
	 * @param tag find tags related to this tag. Must not be <code>null</code>. 
	 * @param maxCount find no more than <code>maxCount</code> number of tags
	 */
	public List<Tag> findRelatedTags(Tag tag, int maxCount);
	
	public boolean createTagAssertion(TagAssertion[] asrt);
	
	/**
	 * Deletes a user's RDF type assertions that have <code>object</code> 
	 * <code>classTag</code>
	 * 
	 * @param user the user who made those assertions
	 * @param classTag the object tag
	 * @return <code>false</code> if unable to perform this delete operation. Otherwise,
	 * returns <code>true</code>.
	 */
	public boolean deleteRDFTypeClassAssertion(User user, UserTag classTag);
	
	public boolean deleteSKOSRelatedAssertion(User user, UserTag subjectTag, UserTag[] objectTag);
	
	public boolean deleteSKOSBroaderAssertion(User user, UserTag subjectTag, UserTag[] objectTag);
	
	public boolean deleteSKOSNarrowerAssertion(User user, UserTag subjectTag, UserTag[] objectTag);
	
	public boolean deleteRDFTypeAssertion(User user, UserTag subjectTag, UserTag[] objectTag);

	public boolean deleteRDFTypeAssertion(User user, UserTag[] subjectTag, UserTag objectTag);
	
}
