var showHideNotesClass = 'showOrHideNotes';
var postClass = 'post';
var notesClass = 'notes';
var prvwNotesClass = 'prvwNotes';
var extNotesClass = 'extNotes';
var showMoreClass = 'showMoreNotes';
var showLessClass = 'showLessNotes';
var showNotesIconName = 'icon_zoom_in.png';
var hideNotesIconName = 'icon_zoom_out.png';
var pageContentsId = "contents";

function initializePage(){	
	var postElms = MochiKit.DOM.getElementsByTagAndClassName('LI',postClass,pageContentsId);
	if(postElms != null){
		for(var i = 0; i < postElms.length; i++){			
			var postElm = postElms[i];
			var showMoreElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',showMoreClass,postElm);
			var showLessElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',showLessClass,postElm);
			if(showMoreElm != null && showLessElm != null){
				var id = MochiKit.DOM.getNodeAttribute(postElm,'id');
				MochiKit.DOM.setNodeAttribute(showMoreElm,'href','javascript:showNotes(\''+ id +'\');');
				MochiKit.DOM.setNodeAttribute(showLessElm,'href','javascript:hideNotes(\''+ id +'\');');
			}			
		}
	}
}

function showNotes(postId){
	var postElm = MochiKit.DOM.getElement(postId);	
	var prvwNotesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',prvwNotesClass,postElm);	
	var extNotesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',extNotesClass,postElm);	
	var showHideElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',showHideNotesClass,postElm);
	var showHideImgElm = MochiKit.DOM.getFirstElementByTagAndClassName('IMG',null,showHideElm);	
	if(prvwNotesElm != null && extNotesElm != null){
		MochiKit.DOM.addElementClass(prvwNotesElm,'invisible');
		
		// this hack is needed to get browsers to reload <iframe/> 
		var c = extNotesElm.innerHTML;
		extNotesElm.innerHTML = '';
		MochiKit.DOM.removeElementClass(extNotesElm,'invisible');		
		extNotesElm.innerHTML = c;
	}
}


function hideNotes(postId){
	var postElm = MochiKit.DOM.getElement(postId);
	var prvwNotesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',prvwNotesClass,postElm);	
	var extNotesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',extNotesClass,postElm);	
	var showHideElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',showHideNotesClass,postElm);
	var showHideImgElm = MochiKit.DOM.getFirstElementByTagAndClassName('IMG',null,showHideElm);	
	if(prvwNotesElm != null && extNotesElm != null){
		MochiKit.DOM.addElementClass(extNotesElm,'invisible');
		MochiKit.DOM.removeElementClass(prvwNotesElm,'invisible');
	}
}

MochiKit.Signal.connect(window,'onload',initializePage);