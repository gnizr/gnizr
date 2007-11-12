String.prototype.escapeHTML = function () {                                       
  return(                                                                 
    this.replace(/&/g,'&amp;').                                         
         replace(/>/g,'&gt;').                                           
         replace(/</g,'&lt;').                                           
         replace(/"/g,'&quot;')                                         
  );                                                                     
};

String.prototype.unescapeHTML = function () {                                       
  return(                                                                 
    this.replace(/&amp;/g,'&').                                         
         replace(/&gt;/g,'>').
         replace(/&lt;/g,'<').
         replace(/&quot;/g,'"')                                     
  );                                                                     
};

function toTagline(tagArray){
	var tagline = '';
	if(tagArray){
		for(var i = 0; i < tagArray.length; i++){
			if(tagArray[i] != ' ' && tagArray[i] != ''){	
				var tag = tagArray[i].unescapeHTML();
				tagline  = tagline + tag;
				if(i < (tagArray.length - 1)){
					tagline = tagline + ' ';			
				}
			}			
		}
	}
	return tagline;
}

/**
 * Returns the CSS class name for the input tag count. 
 * This function implements the same logic in
 * "getTagPopularityStyle" function in freemarker lib
 * /lib/web/util-func-lib.ftl
 */
function getTagPopularityStyle(tagCnt){	
	if(tagCnt >= 0 && tagCnt < 7){
		return "not-popular";
	}else if(tagCnt >=7 && tagCnt < 10){
		return "popula";
	}else if(tagCnt >= 10 && tagCnt < 15){
		return "very-popular";
	}else if(tagCnt >= 15){
		return "ultra-popular";
	}
	return "";
}

function toFolderMachineTag(tag){
	var folderName = tag.replace(/\s/g,'_');
	return 'folder:'+folderName;
}
