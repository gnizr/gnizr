var overlayDialogBox = null;

function initBookmarkQuickView(){
	MochiKit.LoggingPane.createLoggingPane(true);		
	overlayDialogBox = new OverlayDialog('quickview');
}

function viewBookmarkInDialogBox(bookmarkId){
	overlayDialogBox.openDialog(bmUrl+bookmarkId);
}

MochiKit.Signal.connect(window,'onload',initBookmarkQuickView);



