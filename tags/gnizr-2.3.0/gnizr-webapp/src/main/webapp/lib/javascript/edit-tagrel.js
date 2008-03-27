var editToolsDIVId='editTools';
var tagCloudDIVId='tagCloud';
var userTagsOLId='userTags';
var tagCloudClassId='tag-cloud';
var tagIdPrefix='ti_';
var editTagsTextAreaId='editValue';

/** A map of all tags that the user currently use to label bookmarks */
var tags = {};

/** An array of tags currently present in the INPUT text area of 'editTagsTextAreaid' */
var inputTags = new Array();

function loadUserTags(data){
	tags = data;
}

function initializePage(){
    //MochiKit.LoggingPane.createLoggingPane(true);
	createTagSelectionBlock(tags);
    colorSelectedTags();	
    monitorTaglineChanges();
}

function createTagSelectionBlock(tagData){
	var userTagListElm = MochiKit.DOM.getElement(userTagsOLId); 
	for(var tag in tagData){
	    var tagId = tagIdPrefix + tag;
	    var tagLabel = tag.unescapeHTML();
	    var tagLink = MochiKit.DOM.A({'id':tagId,'href':'javascript:taggle(\''+ tagLabel+'\')'});
	    var tagCount = tagData[tag];
	    MochiKit.DOM.addElementClass(tagLink,getTagPopularityStyle(tagCount) + " a"+tagCount);
	    tagLink.innerHTML = tagLabel;
	    MochiKit.DOM.appendChildNodes(userTagListElm,MochiKit.DOM.LI(null,tagLink),'\n');
	}
}

function colorSelectedTags(){
	doUncolorAllTags();
	doReadTagline();
	for(var i = 0; i < inputTags.length; i++){		
		doChangeTagColor(inputTags[i]);
	}
}

function doChangeTagColor(tag){
    var tagId = tagIdPrefix + tag.escapeHTML();		
	var tagHref = MochiKit.DOM.getElement(tagId);		
	if(tagHref){
		if(MochiKit.DOM.hasElementClass(tagHref,'selected') == false){			
			MochiKit.DOM.addElementClass(tagHref,'selected');			
		}else{			
			MochiKit.DOM.removeElementClass(tagHref,'selected');
		}	
	}
}

function doUncolorAllTags(){
	var tagLinks = MochiKit.DOM.getElementsByTagAndClassName('A','selected');
	for(var i = 0; i < tagLinks.length; i++){
		MochiKit.DOM.removeElementClass(tagLinks[i],'selected');
	}
}

function doReadTagline(){
	// reads existing bookmark tags into bookmarkTags
	var tagsInput = MochiKit.DOM.getElement(editTagsTextAreaId)
	// removes the leading and trailing white spaces
	var tagline = strip(tagsInput.value);	
	var ltags = tagline.split(' ');
	inputTags = new Array();			
	for(var i = 0; i < ltags.length; i++){
		if(getInCurrentInputTagsPos(ltags[i]) == -1){
			inputTags.push(ltags[i]);
		}
	}
}

function doWriteTagline(){	
	var tagsInput = MochiKit.DOM.getElement(editTagsTextAreaId);	
	var tagline = toTagline(inputTags);
	tagsInput.value = tagline;
}

function getInCurrentInputTagsPos(taggledTag){
	for(var i = 0; i < inputTags.length; i++){		
		if(taggledTag == inputTags[i]){						
			return i;
		}
	}
	return -1;
}

function taggle(tag){
	doReadTagline();
	doSwapTag(tag);
	doChangeTagColor(tag);
	doWriteTagline();
}

function doSwapTag(tag){		
	var curPos = getInCurrentInputTagsPos(tag);
	if(curPos == -1){
		inputTags.unshift(tag);
	}else{
		inputTags.splice(curPos,1);
	}	
}

function monitorTaglineChanges(){
	var changed = function(e){
		MochiKit.Logging.log('key ' + e.key().code + ', ' + e.key().string);
		colorSelectedTags();
	};
	MochiKit.Signal.connect(editTagsTextAreaId,'onkeyup',changed);
}


MochiKit.Signal.connect(window,'onload',initializePage);