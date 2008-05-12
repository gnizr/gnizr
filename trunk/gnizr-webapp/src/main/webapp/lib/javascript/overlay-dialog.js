function OverlayDialog(dialogId){
	this.contentSrcUrl = null;	
	this.isDialogVisible = false;
	this.dialogId = dialogId;
		
	var closeElm = MochiKit.DOM.SPAN({'class':'system-link'},'[X] Close');	
	MochiKit.Signal.connect(closeElm,'onclick',this,'closeDialog');
	this.dialogMenuElm = MochiKit.DOM.DIV({'class':'dialog-menu'},closeElm);
	this.dialogContentElm = MochiKit.DOM.DIV({'class':'dialog-content'});
	this.dialogElm = MochiKit.DOM.DIV({'class':'overlay-dialog'},
   		 		                      this.dialogMenuElm,
	            		              this.dialogContentElm);	            		              	
	this.overlayElm = MochiKit.DOM.DIV({'class':'overlay invisible','id':dialogId},
									  this.dialogElm);	
	var bodyElm = MochiKit.DOM.getFirstElementByTagAndClassName('BODY');
	MochiKit.DOM.appendChildNodes(bodyElm,this.overlayElm);
	MochiKit.Logging.log('appended overlaydialog [' + this.dialogId + '] to the BODY');
}

OverlayDialog.prototype.closeDialog = function(){
	MochiKit.DOM.addElementClass(this.overlayElm,'invisible');
	this.isDialogVisible = false;
	if(this.dialogContentElm != null){		
		this.dialogContentElm.innerHTML = '';
	}	
}

OverlayDialog.prototype.isOpen = function(){
	return this.isDialogVisible;
}

OverlayDialog.prototype.openDialog = function(dataUrl){	
	var myDialog = this;
	var gotData = function(data){
		MochiKit.Logging.log('fetched data to be displayed in the overlay-dialog.');
		//MochiKit.Logging.log('data = ' + data);		
		myDialog.dialogContentElm.innerHTML = data.responseText;
		
		var vpPos =  MochiKit.Style.getViewportPosition();		
		MochiKit.Logging.log('viewpoint position: ' + vpPos);
		
		MochiKit.Logging.log('current margin-top: ' + MochiKit.Style.getStyle(myDialog.dialogElm,'margin-top'));
		MochiKit.DOM.removeElementClass(myDialog.overlayElm,'invisible');

		this.isDialogVisible = true;	
	};
	var fetchDataFailed = function(data){
		MochiKit.Logging.log('failed to fetch data to be displayed in the overlay-dialog');
	}
	
	var d = MochiKit.Async.doSimpleXMLHttpRequest(dataUrl);	
	d.addCallbacks(gotData,fetchDataFailed);
}

/*
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
*/
