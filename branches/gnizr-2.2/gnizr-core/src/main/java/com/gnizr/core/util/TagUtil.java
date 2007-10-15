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
package com.gnizr.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gnizr.core.vocab.MachineTags;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagsParser;

public class TagUtil {

		
	public static <T extends Tag> Map<String,T> toTagMap(List<T> tags){
		Map<String,T> map = new HashMap<String,T>();
		if(tags != null){
			for(Iterator<T> it = tags.iterator(); it.hasNext();){
				T tag = it.next();
				map.put(tag.getLabel(),tag);
			}
		}
		return map;
	}
	
	public static boolean isPrefixedUserTag(String tag){
		if(tag == null) throw new NullPointerException("tag is null");
		TagsParser parse = new TagsParser(tag);
		List<MachineTag> result = parse.getMachineTags();
		if(result != null && result.size() == 1){
			MachineTag mt = result.get(0);
			String ns = mt.getNsPrefix();
			String pred = mt.getPredicate();
			if(ns == null || ns.equals(MachineTags.NS_GNIZR)){
				if(MachineTags.TAG_PRED.equals(pred)){
					return true;
				}
			}		
		}
		return false;
	}

	public static UserTag parsePrefixedUserTag(String tag) {
		if(isPrefixedUserTag(tag) == false){
			return null;
		}
		TagsParser parse = new TagsParser(tag);
		List<MachineTag> result = parse.getMachineTags();
		if(result != null && result.size() == 1){
			MachineTag mt = result.get(0);
			String value = mt.getValue();
			String[] parseResult = value.split("/");
			if(parseResult != null){
				if(parseResult.length == 2){		
					return new UserTag(parseResult[0],parseResult[1]);
				}else if(parseResult.length == 1){
					return new UserTag(null,parseResult[0]);
				}
			}
		}
		return null;
	}
	
	public static String makeSafeTagString(String tag){
		if(tag != null){
			String t = tag.trim();
			t = t.replace("/$","");
			t = t.replaceAll("\\\\","");
			t = t.replaceAll("[&%\\?]","");			
			t = t.replaceAll("\\s+","_");
			return t;
		}
		return tag;
	}
	
	public static String mapUnderscoreToSpace(String tag){
		if(tag != null){
			String t = tag.trim().replaceAll("_"," ");
			return t;
		}
		return tag;
	}
	
	public static List<String> sortUniqueTags(List<String> unsortedTags){
		List<String> sorted = new ArrayList<String>();
		for(String t : unsortedTags){
			t = t.trim();
			if(sorted.contains(t) == false){
				sorted.add(t);
			}
		}
		return sorted;
	}
	
	public static List<String> systemTags(List<String> tags){
		List<String> sysTags = new ArrayList<String>();
		for(String t : tags){
			t = t.trim();
			if(isSystemTag(t)){
				sysTags.add(t);
			}
		}
		return sysTags;
	}
	
	public static boolean isSystemTag(String tag){
		if(tag.matches("folder:.*|gn:folder=.*")){
			return true;
		}else if(tag.matches("subscribe:this|gn:subscribe=this")){
			return true;
		}else if(tag.matches("for:.*|gn:for=.*")){
			return true;
		}
		return false;
	}
}
