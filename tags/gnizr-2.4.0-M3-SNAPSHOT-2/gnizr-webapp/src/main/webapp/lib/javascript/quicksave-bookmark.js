var saveBmarkLinkClass = "save-bmark-link";
var editBmarkLinkClass = "edit-bmark-link";
var bmarkLinkClass = "bmark-link";
var bmarkTitleClass = "bmark-title";
var postClass = "post";
var notesClass = "notes";
var shrNotesClass = "shrtNotes";
var extNotesClass = "extNotes";
var tagClass = "tag";
var pageContentsId = "contents";
var postActionsClass = "post-actions";
var quickSaveMsgClass = "qsave-message";

var saveBookmarkUrl = "";
var fetchBookmarkUrl = "";

function setSaveBookmarkUrl(url){
	saveBookmarkUrl = url;
}

function setFetchBookmarkUrl(url){
	fetchBookmarkUrl = url;
}

function initializePage(){	
	//MochiKit.LoggingPane.createLoggingPane(true);
	var postElms = MochiKit.DOM.getElementsByTagAndClassName('LI',postClass,pageContentsId);
	if(postElms != null){
		for(var i = 0; i < postElms.length; i++){			
			var postElm = postElms[i];
			var sveBmrkElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',saveBmarkLinkClass,postElm);
			if(sveBmrkElm != null){				
				var id = MochiKit.DOM.getNodeAttribute(postElm,'id');
				MochiKit.DOM.setNodeAttribute(sveBmrkElm,'href','javascript:qsave(\''+ id +'\');');			
			}			
		}
	}
}

function removeQuickSaveLink(postId){
	var postElm = MochiKit.DOM.getElement(postId);
	var sveBmrkElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',saveBmarkLinkClass,postElm);
	MochiKit.DOM.removeElement(sveBmrkElm);
}

function drawQuickSaveProgressMessage(postId,message,imgSrc){
	var postElm = MochiKit.DOM.getElement(postId);
	var postActionsElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',postActionsClass,postElm);		
	if(postActionsElm != null){		
		var msgContent = new Array();
		if(message != null){
			msgContent.push(message);
		}
		if(imgSrc != null){		
			var imgElm = MochiKit.DOM.IMG({'src':imgSrc});
			msgContent.push(imgElm);
		}
		var msgElm = MochiKit.DOM.SPAN({'class':quickSaveMsgClass},msgContent);
		var curMsgElm = MochiKit.DOM.getFirstElementByTagAndClassName('SPAN',quickSaveMsgClass,postActionsElm);
		if(curMsgElm == null){
			MochiKit.DOM.appendChildNodes(postActionsElm,msgElm);
		}else{
			MochiKit.DOM.swapDOM(curMsgElm,msgElm);
		}
	}	
}

function checkOkayToSaveAndDoSave(bmark, doSaveFunction){
	if(bmark != null && fetchBookmarkUrl != ""){
		var bmarkQS = MochiKit.Base.queryString({'url':bmark.url});			
		var d = MochiKit.Async.loadJSONDoc(fetchBookmarkUrl+'?'+bmarkQS);
		MochiKit.Logging.log('about to save: ' + keys(bmark));
		var fetchBmrkOkay = function(data){
			if(data != -1){
				var b = confirm('A bookmark of the same URL exists in your acount. ' +
						'Save again will override the previous bookmark. Are you sure?');
				if(b == true){
					doSaveFunction(bmark);
				}
			}else{
				doSaveFunction(bmark);				
			}
		}
		var fetchBmrkFailed = function(err){
			alert('get bookmark failed: ' + err);
		}
		d.addCallbacks(fetchBmrkOkay,fetchBmrkFailed);
	}	
}


function qsave(postId){
	var doSave = function(bookmark2save){
		removeQuickSaveLink(postId);
		drawQuickSaveProgressMessage(postId,'saving ...');		
		var bmarkQS = MochiKit.Base.queryString(bookmark2save);	
		var d = MochiKit.Async.loadJSONDoc(saveBookmarkUrl+'?'+bmarkQS);	
		var savedOkay = function(data){
			if(data != -1){
				//alert('save okay! id=' + data.id);
				drawQuickSaveProgressMessage(postId,'saved');
			}
		};
		var savedFailed = function(err){
			alert('save bookmark failed: ' + err);
		};
		d.addCallbacks(savedOkay,savedFailed);			
	};
		
	var postElm = MochiKit.DOM.getElement(postId);	
	if(postElm != null && saveBookmarkUrl != ""){		
		var bmark = getBookmark(postElm);			
		checkOkayToSaveAndDoSave(bmark,doSave);
	}
}

function getBookmark(postElm){
	var bmark = {};
	var bmarkLinkElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',bmarkLinkClass,postElm);
	bmark['url'] = MochiKit.DOM.getNodeAttribute(bmarkLinkElm,'href');
	var bmarkTitleElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',bmarkTitleClass,postElm);	
	bmark['title'] = MochiKit.DOM.scrapeText(bmarkTitleElm);
	var shrtNotesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',shrNotesClass,postElm);
	var notesElm = MochiKit.DOM.getFirstElementByTagAndClassName('DIV',extNotesClass,postElm);	
	if(notesElm != null){		  
		//MochiKit.Logging.log('notes in HTML: ' + notesElm.innerHTML);
  		bmark['notes'] = MochiKit.DOM.scrapeText(notesElm);
	}	
	var tagElms = MochiKit.DOM.getElementsByTagAndClassName('A',tagClass,postElm);
	if(tagElms != null){
		bmark['tags'] = '';
		for(var i = 0; i < tagElms.length; i++){
			bmark['tags'] += MochiKit.DOM.scrapeText(tagElms[i]) + ' ';		
		}
	}	
	return bmark;
}

MochiKit.Signal.connect(window,'onload',initializePage);