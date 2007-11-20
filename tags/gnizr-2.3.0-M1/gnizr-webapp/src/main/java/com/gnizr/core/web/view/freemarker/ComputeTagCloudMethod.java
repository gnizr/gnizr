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
package com.gnizr.core.web.view.freemarker;

import java.util.HashMap;
import java.util.List;

import com.gnizr.db.dao.TagLabel;

import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * argument: 
 * 1: a sequence of TagLabel
 * 2: the total number of different font size that tag cloud will have (optional. default 6);
 * 3: a sequnce of regular expression used to filter out tags that should exclude from the tag cloud (optional)
 * 
 * @author harryc
 *
 */
public class ComputeTagCloudMethod implements TemplateMethodModelEx{

	
	public Object exec(List args) throws TemplateModelException {
		if(args.size() < 1 || args.size() > 3){
			throw new TemplateModelException("Wrong number of arguments");
		}
		SimpleHash tagCloudHash = new SimpleHash(new HashMap<String,Integer>());	
		
		List<TagLabel> tags = getTagLabelFromArg1(args.get(0));
		int numOfFontSize = 6;
		if(args.size() > 1){
			numOfFontSize = getNumOfFontSize(args.get(1));
		}
		/*
		if(args.size() > 2){
			String[] filter = getFilterPattern(args.get(2));
		}
		*/
		
		int[] maxMin = getMaxMin(tags);
		int max = maxMin[0];
		int min = maxMin[1];
		double[] threshold = compThreshold(max, min, numOfFontSize);
		for(TagLabel aTag : tags){
			boolean fontSetFlag = false;
			for (int i = 0; i < numOfFontSize; i++) {
				if (fontSetFlag == false) {						
					if (aTag.getCount() <= threshold[i]) {
						tagCloudHash.put(aTag.getLabel(),i+1);
						fontSetFlag = true;
					}
				} else {
					break;
				}
			}
		}		
		return tagCloudHash;
	}
	
	@SuppressWarnings("unused")
	private String[] getFilterPattern(Object arg3){
		return null;
	}
	
	private int getNumOfFontSize(Object arg2){
		int n = 6;
		if(arg2 != null && arg2 instanceof SimpleNumber){
			int nn = ((SimpleNumber)arg2).getAsNumber().intValue();
			if(nn > 0){
				n = nn;
			}
		}
		return n;
	}
	
	private double[] compThreshold(int max, int min, int numOfFontSizes) {
		double delta = (double) (max - min) / numOfFontSizes;
		double[] th = new double[numOfFontSizes];
		for (int i = 0; i < numOfFontSizes; i++) {
			th[i] = min + (i + 1) * delta;
		}
		return th;
	}
	

	@SuppressWarnings("unchecked")
	private List<TagLabel> getTagLabelFromArg1(Object arg1){		
		if(arg1 instanceof BeanModel){
			BeanModel m = (BeanModel)arg1;
			List list = (List)m.getWrappedObject();
			return list;
		}
		return null;
	}
	
	private int[] getMaxMin(List<TagLabel> tags){
		int[] vMaxMin = new int[2];
		vMaxMin[0] = Integer.MIN_VALUE; // max: start with the smallest possible int
		vMaxMin[1] = Integer.MAX_VALUE; // min: start with the largest possible int
		for(TagLabel tag : tags){
			if(tag.getCount() > vMaxMin[0]){
				vMaxMin[0] = tag.getCount();
			}
			if(tag.getCount() < vMaxMin[1]){
				vMaxMin[1] = tag.getCount();		
			}
		}
		return vMaxMin;
	}
	

}
