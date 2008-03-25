function openDialog(dialogId, contentElm) {
	if(MochiKit.DOM.hasElementClass(contentElm,'overlay-dialog') == false){
		MochiKit.DOM.addElementClass(contentElm,'overlay-dialog');
	}
	var closeElm = MochiKit.DOM.DIV({'class':'overlay-dialog-close'},
	   MochiKit.DOM.A({'class':'system-link', 'href':'#',
	      'onclick':'closeDialog("' + dialogId + '")'},'Close'));
	
	MochiKit.DOM.appendChildNodes(contentElm,closeElm);
	var overlayElm = MochiKit.DOM.DIV({'class':'overlay','id':dialogId},contentElm);
	var bodyElm = MochiKit.DOM.getFirstElementByTagAndClassName('BODY');
	MochiKit.DOM.appendChildNodes(bodyElm,overlayElm);
}

function closeDialog(dialogId){
	MochiKit.DOM.removeElement(dialogId);
}
