
ClosePopup = {
    initializePage : function(){
        setTimeout('window.close()',3000);
        MochiKit.DOM.setNodeAttribute('closeme','href','javascript:ClosePopup.closeNow()');
    },    
    closeNow: function(){
        window.close();
    }
}

MochiKit.Signal.connect(window,'onload',ClosePopup.initializePage);