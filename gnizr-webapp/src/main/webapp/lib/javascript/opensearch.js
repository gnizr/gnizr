
/* HTML & CSS related global variables */
var resultTilesTableID = 'resultTiles';
var resultTilesRowID = 'resultTilesRow';
var selectSearchServicesULID = 'selectSearchServices';
var entryFocusedClass = 'resultEntryFocused';
var saveResultLinkClass = 'saveLink';
var linkActionClass = 'linkAction';
var viewDetailClass = 'viewDetail'
var viewDetailLinkClass = 'viewDetailLink';

/* Cookie related global variables */
var chckSrvCookieName = 'rememberCheckedServices';

/** OpenSearch service objects */
var services = new Array();

var imagePathUrl = '';
var searchManager = null;
var queryTerm = null;
var proxyUrl = null;
var postUrl = null;
var bookmarkDetailUrl = null;
var loggedInUser = null;
var loadingImg = '/loading-bar-blue.gif';
var checkedServicesMap = {};

function readCheckedServicesFromCookie(){
    checkedServicesMap = {};
    var v = CookieUtil.readCookie(chckSrvCookieName);
    // format: [service_name_1]:[1|0],[service_name_2]:[1|0]...
    if(v != null){
      var chkStatus = v.split(',');
      for(var i = 0; i < chkStatus.length; i++){
         var aChkStatus = chkStatus[i].split(':');
         var srvName = aChkStatus[0];
         var srvStat = aChkStatus[1];
         checkedServicesMap[srvName] = srvStat;      
      }      
    }    
}

function writeCheckedServicesToCookie() {
    var cookieValue = '';
    var items = MochiKit.Base.items(checkedServicesMap);
    for(var i = 0; i < items.length; i++){
        var t = items[i];
        var srvName = t[0];
        var srvStat = t[1];
        cookieValue = cookieValue + srvName + ':' + srvStat;
        if(i+1 < items.length){
            cookieValue = cookieValue + ',';
        }                
    }
    MochiKit.Logging.log('writeCheckedServicesToCookie: cookieValue=' + cookieValue);
    CookieUtil.eraseCookie(chckSrvCookieName);
    CookieUtil.createCookie(chckSrvCookieName,cookieValue,0);
}

ResizeTile = {
    tileSeperator: null,
    tileSeperatorPos: null,
    mouseMove: null,
    mouseDown: null,
    tileWidthOffset: null,
    
    calcTileWidthOffset: function(tileCurWidth,curMousePos){
        var draggedToX = curMousePos.x;
        var draggedFromX = ResizeTile.tileSeperatorPos.x;
        var offset = draggedToX - draggedFromX;
        if (offset == 0){
            return 0;
        }else if(offset < 0){
            if((offset * -1) > tileCurWidth){
                return (tileCurWidth * -1);
            }
        }else if(offset > tileCurWidth){
            return tileCurWidth;
        }
        return offset;
    },
    
    getLeftTileElement: function(tileSpElm){
        var tileElm = tileSpElm.previousSibling;
        while(tileElm.nodeType == 3){
            // if TEXT_NODE, get the previousSibling;
            tileElm = tileElm.previousSibling;
        }
        return tileElm;    
    },
    
    getRightTileElement: function(tileSpElm){
        var tileElm = tileSpElm.nextSibling;
        while(tileElm.nodeType == 3){
            // if TEXT_NODE, get the nextSibling;
            tileElm = tileElm.nextSibling;
        }
        return tileElm;    
    },  
      
    calcMaxTileWidth: function(){
        //var resultTilesElm = MochiKit.DOM.getElement(resultTilesTableID);
        //var resultTilesElmDim = MochiKit.Style.getElementDimensions(resultTilesElm);          
        //MochiKit.Logging.log('resultTiles table width: ' + resultTilesElmDim.w);  
        
        var leftTileElm = ResizeTile.getLeftTileElement(ResizeTile.tileSeperator);
        var rightTileElm = ResizeTile.getRightTileElement(ResizeTile.tileSeperator);
        
        var leftTileElmDim = MochiKit.Style.getElementDimensions(leftTileElm);                 
        var rightTileElmDim = MochiKit.Style.getElementDimensions(rightTileElm);        
        return leftTileElmDim.x + rightTileElmDim.x;                      
    },
    
    dragStart: function(e){
        e.stop();
        ResizeTile.tileSeperator = e.src();
        ResizeTile.tileSeperatorPos = MochiKit.Style.getElementPosition(ResizeTile.tileSeperator);
        MochiKit.Logging.log('tileSeperator [' + ResizeTile.tileSeperator.id + '] has position ' 
                              + ResizeTile.tileSeperatorPos);
        ResizeTile.mouseMove = MochiKit.Signal.connect(document,'onmousemove', ResizeTile.dragging);
        ResizeTile.mouseDown = MochiKit.Signal.connect(document,'onmouseup',ResizeTile.dragStop);                              
    },
    
    dragging: function(e){
        e.stop();        
        var mousePos = e.mouse().page;
        MochiKit.Logging.log("mouse pos: " + mousePos.x + " tileSepPos: " + ResizeTile.tileSeperatorPos.x);
        var maxWidth = ResizeTile.calcMaxTileWidth();        
        ResizeTile.tileWidthOffset = ResizeTile.calcTileWidthOffset(maxWidth,mousePos);
        MochiKit.Logging.log('move towards: ' + ResizeTile.tileWidthOffset);     
    },
    
    dragStop: function(e){
        MochiKit.Logging.log('dragStop is called');       
        var tileElm = ResizeTile.getLeftTileElement(ResizeTile.tileSeperator);      
        var tileElmDim = MochiKit.Style.getElementDimensions(tileElm);                 
        tileElmDim.w = tileElmDim.w + ResizeTile.tileWidthOffset;                    
        MochiKit.Style.setElementDimensions(tileElm,tileElmDim);
        MochiKit.Logging.log('new dim: ' + tileElmDim);        

        MochiKit.Signal.disconnect(ResizeTile.mouseMove);
        MochiKit.Signal.disconnect(ResizeTile.mouseDown);
    }
};

function SearchExecutor(service,resultTile){
    this.service = service;   
    this.resultTile = resultTile;   
    this.qs = decodeURIComponent(queryTerm);
    this.startPage = -1;
    this.startIndex = -1;
    this.totalFetched = 0;
    this.totalResult = 0;    
    this.searchDataUrl = this.getNextSearchQuery();    
    this.fetchMoreDataLinkElm = MochiKit.DOM.A({'class':'fetchMoreDataLink system-link','href':'#'},'show more');    
    this.resultListElm = MochiKit.DOM.OL({'class':'resultList'},null);
    MochiKit.DOM.appendChildNodes(this.resultTile.getTileContentNode(),this.resultListElm, this.fetchMoreDataLinkElm);
    
    this.fetchMoreData();
    
    // don't forget to disconnect this signal.
    this.fetch = MochiKit.Signal.connect(this.fetchMoreDataLinkElm,'onclick',this,'fetchMoreData');    
}

SearchExecutor.prototype.fetchMoreData = function(){    
    var resultElm = this.resultListElm;
    var resultTile = this.resultTile;     
    var searchExec = this;   
    
    function appendNoMatchingResult(result){
    	 var noMatchElm = MochiKit.DOM.P({'class':'search-result-message'},'No matching results were found!');
    	 MochiKit.DOM.replaceChildNodes(resultTile.tileContentElm,noMatchElm);
    }
    
    function appendOneResult(anEntry){
        //MochiKit.Logging.log('e'+i+':'+anEntry.link+','+anEntry.title);
        var title = anEntry.title;
        var summary = '';
        if(MochiKit.Base.isUndefinedOrNull(anEntry.summary) == false){                                      
            summary = anEntry.summary;
            if(summary.length > 250){
                summary = summary.substring(0,250) + '...';
            }
           //summary = summary.escapeHTML();
         }
         var link = anEntry.link;
         var editLinkElm = '';
         // setup the hock for injecting a quick-save or edit-bookmark link
         if(MochiKit.Base.isUndefinedOrNull(loggedInUser) == false){
            editLinkElm = MochiKit.DOM.SPAN({'class':'invisible ' + linkActionClass},
               MochiKit.DOM.A({'href':'#','class':'system-link ' + saveResultLinkClass},'')); 
         }
         // setup the hack for injecting a view-bookmark-detail link
         var id = parseBookmarkId(anEntry.id);
         var viewDetailElm = '';
         if(id > 0){
         	MochiKit.Logging.log('Found gnizr bookmark id:' + id);
         	var dtlUrl = bookmarkDetailUrl + '/' + id;
         	viewDetailElm = MochiKit.DOM.SPAN({'class':'invisible ' + viewDetailClass},
         	   MochiKit.DOM.A({'href':dtlUrl,'target':'_blank','class':'system-link ' + viewDetailLinkClass},'view'));
         }         
         var summaryElm = MochiKit.DOM.P({'class':'entryDescription notes'});
         summaryElm.innerHTML = summary.escapeHTML();
         var entryElm = MochiKit.DOM.LI(null,
            MochiKit.DOM.A({'class':'entryTitle link-title','href':link,'target':'_blank'},title),
            editLinkElm,
            ' ',
            viewDetailElm,
			summaryElm            
          );                                  
         MochiKit.DOM.appendChildNodes(resultElm,entryElm); 
         resultTile.notifyResultEntryCreated(entryElm);      
    }
    
    function updateResultMeta(result){      
        if(searchExec.totalFetched > 0){
            var msg = 'Showing 1 - ' + searchExec.totalFetched ;
            if(searchExec.totalResults > 0 && searchExec.totalResults != Infinity){
                msg = msg + ' of ' + searchExec.totalResults;
            }else {
                msg = msg + ' of many';
            }
            var resultMetaElm = resultTile.getTileResultMetaElm();
            var metaDataElm = MochiKit.DOM.P({'class':'resultMeta'},msg);
            MochiKit.DOM.replaceChildNodes(resultMetaElm,metaDataElm);
        }
    }
        
    var onSucceed = function(result){        
        resultTile.hideLoadingDialog();        
        MochiKit.Logging.log('got result: size= ' + result.entries.length);  
        if(MochiKit.Base.isUndefinedOrNull(result.entries) == false){
            var entries = result.entries;
            for(var i = 0; i < entries.length; i++){
                appendOneResult(entries[i]);
                searchExec.totalFetched++;
            }
            if(entries.length == 0){
            	appendNoMatchingResult(result);
            }
        }
        if(MochiKit.Base.isUndefinedOrNull(result.startIndex) == false){
            searchExec.startIndex = result.startIndex;
            MochiKit.Logging.log('From Result: startIndex = ' + searchExec.startIndex);
        }
        if(MochiKit.Base.isUndefinedOrNull(result.itemsPerPage) == false){
            searchExec.itemsPerPage = result.itemsPerPage;
            MochiKit.Logging.log('From Result: itemsPerPage = ' + searchExec.itemsPerPage);
        }
        if(MochiKit.Base.isUndefinedOrNull(result.totalResults) == false){
            searchExec.totalResults = result.totalResults;
            MochiKit.Logging.log('From Result: totalResults = ' + searchExec.totalResults);
        }
        if(searchExec.totalResults > 0 && (
            searchExec.startIndex + searchExec.itemsPerPage) < searchExec.totalResults){
            MochiKit.Logging.log('updating searchDataUrl');  
            searchExec.searchDataUrl = searchExec.getNextSearchQuery();             
        }else if(MochiKit.Base.isUndefinedOrNull(result.startIndex) == true &&
                 MochiKit.Base.isUndefinedOrNull(result.itemsPerPage) == true &&
                 MochiKit.Base.isUndefinedOrNull(result.totalResults) == true){
            searchExec.startIndex = result.totalFetched;
            searchExec.totalResults = Infinity; // some random neg number;
            MochiKit.Logging.log('updating searchDataUrl');  
            searchExec.searchDataUrl = searchExec.getNextSearchQuery(); 
        }else{
            searchExec.searchDataUrl = null;
            MochiKit.DOM.removeElement(searchExec.fetchMoreDataLinkElm);
        }    
        updateResultMeta(result);
                
    };
    var onFailed = function(result){
       resultTile.hideLoadingDialog();  
       MochiKit.Logging.log('fetch data failed. ' + result);
    }    
    MochiKit.Logging.log('try to fetchMoreData from : ' + this.searchDataUrl);        
      
    if(MochiKit.Base.isUndefinedOrNull(this.searchDataUrl) == false){
    	var callUrl = null;
    	if(this.service.type == 'synd'){
    		if(MochiKit.Base.isUndefinedOrNull(proxyUrl) == false){
    			callUrl =  proxyUrl + encodeURIComponent(this.searchDataUrl);	
    		}else{
    	   		alert('Internal Error: proxy URL is undefined');  
	    	}
    	}else if(this.service.type == 'gn-json'){
    		callUrl = this.searchDataUrl;
	    }else{
    		MochiKit.Logging.log('Unsupport OpenSearch result type: neither gn-json or synd');
	    }
	    if(MochiKit.Base.isUndefinedOrNull(callUrl) == false){
    		this.resultTile.showLoadingDialog();
   			MochiKit.Logging.log('AJAX call: ' + callUrl);
        	var d = MochiKit.Async.loadJSONDoc(callUrl);
        	d.addCallbacks(onSucceed,onFailed);            
	    }
    }else{
    	MochiKit.Logging.log('No more results in the searh stream. Halt.');
    }
}

SearchExecutor.prototype.terminate = function(){
    if(MochiKit.Base.isUndefinedOrNull(this.fetch) == false){
        MochiKit.Signal.disconnect(this.fetch);
        MochiKit.Logging.log('SearchExecutor: terminate: Disconnected the fetch signal of service ID:' + this.service.id);
    }   
}

SearchExecutor.prototype.getNextSearchQuery = function(){
    var pStr = decodeURIComponent(this.service.serviceUrl).split('?');
    MochiKit.Logging.log('getNextSearchQuery: split serviceUrl => ' + pStr);
    MochiKit.Logging.log("pStr[0]: baseUrl=" + pStr[0]);  
    MochiKit.Logging.log("pStr[1]:" + pStr[1]); 
    var baseUrl = pStr[0];  
    // in case, a service considers {searchTerms} as part of the URL path
    baseUrl = baseUrl.replace("{searchTerms}",encodeURIComponent(this.qs));     
    var qTerms = MochiKit.Base.parseQueryString(pStr[1]);    
    var qTerms2 = {}; // needed bec parseQueryString include '&' as a key
    for(key in qTerms){
    	MochiKit.Logging.log("qTerms[" +key + "] = " +qTerms[key]);
        if(qTerms[key] == '{count}'){
            qTerms2[key] = 10;
            MochiKit.Logging.log('replace {count} with 10');
        }else if(qTerms[key] == '{searchTerms}'){
            qTerms2[key] = this.qs;
            MochiKit.Logging.log('replace {searchTerms} with ' + qTerms2[key]);
        }else if(qTerms[key].indexOf('{searchTerms}') != -1){	    
	        qTerms2[key] = qTerms[key].replace(/\{searchTerms\}/,this.qs);
	        MochiKit.Logging.log('repalcing qTerms using rule 2: ' + qTerms2[key] + ", qs = " + this.qs);
        }else if(qTerms[key] == '{startIndex}'){ 
            if(this.startIndex <= 0){
                this.startIndex = 1;
            }else{
                this.startIndex = this.totalFetched;
            }
            qTerms2[key] = this.startIndex;
            MochiKit.Logging.log('replace {startIndex} with ' + qTerms2[key]);
        }else if(qTerms[key] == '{startPage}'){
            if(this.startPage <= 0){
                this.startPage = 1;
            }else{
                this.startPage++;
            }
            qTerms2[key] = this.startPage;
            MochiKit.Logging.log('replace {startPage} with ' + qTerms2[key]);
        }else if(qTerms[key] == '{loggedInUser}'){
            if(MochiKit.Base.isUndefinedOrNull(loggedInUser) == false){
                qTerms2[key] = loggedInUser;
                MochiKit.Logging.log('replace {loggedInUser} with ' + qTerms2[key]);
            }
        }else if(key != '&amp;' && key != '&'){            
            qTerms2[key] = qTerms[key];
        }else{
            MochiKit.Logging.log('unprocessed key: ' + key);
        }
        
    }
    for(k in qTerms2){
        MochiKit.Logging.log('k=' + k + ',v='+qTerms2[k]);
    }
    var nxtQuery = baseUrl + '?' +MochiKit.Base.queryString(qTerms2);
    MochiKit.Logging.log('Return from getNextSearchQuery=' + nxtQuery);
    return nxtQuery;
}

function getResultTilesTableDim(){
    return MochiKit.Style.getElementDimensions('resultTiles');
}

function getEqualTileWidth(numOfTiles){
    var d = getResultTilesTableDim();
    var w = d.w / numOfTiles;
    MochiKit.Logging.log('equal tile width: ' + w + ', ' + numOfTiles + ' tiles, table width ' + d.w);
}

function LinkEntry(linkEntryElement){
    this.title = null;
    this.url = null;
    this.dsp = null;
    if(linkEntryElement != null){
        var hrefAElm = MochiKit.DOM.getFirstElementByTagAndClassName(null,'entryTitle',linkEntryElement);
        MochiKit.Logging.log('LinkEntry: constructor: hrefAElm = ' + hrefAElm);
        if(hrefAElm != null){
            this.title = hrefAElm.innerHTML;
            MochiKit.Logging.log('LinkEntry: constructor: this.title = ' + this.title);
            this.url = hrefAElm.href;
            MochiKit.Logging.log('LinkEntry: constructor: this.url = ' + this.url);
        }
        var dspElm = MochiKit.DOM.getFirstElementByTagAndClassName(null,'entryDescription',linkEntryElement);
        if(dspElm != null){
            this.dsp = dspElm.innerHTML;
            MochiKit.Logging.log('LinkEntry: constructor: this.dsp = ' + this.dsp);
        }
    }
}

function QuickSave(linkEntryElement){    
    this.callback = null;  
    this.entryElement = linkEntryElement;
    this.linkEntry = new LinkEntry(this.entryElement);  
    this.linkActionElement = null;  
    if(this.entryElement != null){
      this.linkActionElement = MochiKit.DOM.getFirstElementByTagAndClassName('SPAN',linkActionClass,this.entryElement);      
      var saveLnkElm = MochiKit.DOM.getFirstElementByTagAndClassName('A',saveResultLinkClass,this.linkActionElement);    
      if(MochiKit.Base.isUndefinedOrNull(saveLnkElm) == true){
          saveLnkElm = MochiKit.DOM.A({'class':'system-link ' + saveResultLinkClass,'href':'#'});
          MochiKit.DOM.replaceChildNodes(this.linkActionElement,saveLnkElm);         
      }
      var svOrEdtLabel = saveLnkElm.innerHTML;
      if(svOrEdtLabel == 'save'|| svOrEdtLabel == 'edit'){
          this.connectOnClick(saveLnkElm,svOrEdtLabel);
      }else{
          if(MochiKit.Base.isUndefinedOrNull(fetchBookmarkUrl) == false){
              var bmarkQS = MochiKit.Base.queryString({'url':this.linkEntry.url});
              var d = MochiKit.Async.loadJSONDoc(fetchBookmarkUrl+'?'+bmarkQS);
              var qs = this;
              var fetchBmarkOkay = function(data){
                 if(data != -1){
			        saveLnkElm.innerHTML = 'edit';			        
	       		 }else{
					saveLnkElm.innerHTML = 'save';								
			     } 			     
			     qs.connectOnClick(saveLnkElm,saveLnkElm.innerHTML);			    
              }
              var fetchBmarkFailed = function(err){
        	       MochiKit.Logging.log('get bookmark failed: ' + err);
		      }
           	  d.addCallbacks(fetchBmarkOkay,fetchBmarkFailed);
          }
       }       
    }   
}

QuickSave.prototype.destroy = function(){
    if(this.callback != null){
        MochiKit.Signal.disconnect(this.callback);
        MochiKit.Logging.log('QuickSave: destroy: disconnected onclick callback');
    }
}

QuickSave.prototype.connectOnClick = function (saveOrEditNode, actionLabel){ 
     MochiKit.Logging.log('actionlabel: ' + actionLabel + ', saveOrEditNode: ' + saveOrEditNode + ', linkEntry: ' + this.linkEntry);
     function doSave(e){         
        e.stop();
        var saveLoadingElm = MochiKit.DOM.SPAN(null,'saving...');       
        function onSucc(data){
            saveLoadingElm.innerHTML = 'done!';
        }
        function onErr(data){
        	alert(data);
            saveLoadingElm.innerHTML = 'error!';
        }      
        MochiKit.DOM.replaceChildNodes(this.linkActionElement,saveLoadingElm);
        var linkEntry = this.linkEntry;     
        SaveLinkUtil.ajaxSave(saveBookmarkUrl,linkEntry.url,linkEntry.title,onSucc,onErr);       
     }
    
     function doEdit(e){
        e.stop();          
        if(postUrl != null){
            var linkEntry = this.linkEntry;
            SaveLinkUtil.popUpEdit(postUrl,linkEntry.url,linkEntry.title);
        }
     }   
     
    if(this.callback == null){
        if(actionLabel == 'save'){
            this.callback = MochiKit.Signal.connect(saveOrEditNode,'onclick',this,doSave);                   
            MochiKit.Logging.log('QuickSave: connectOnClick: connected onclick callback');
        }else if(actionLabel == 'edit'){
            this.callback = MochiKit.Signal.connect(saveOrEditNode,'onclick',this,doEdit);    
            MochiKit.Logging.log('QuickSave: connectOnClick: will connect to edit callback');
        }
    }
} 
   
function ResultTile(service){
    this.service = service;
    this.searchExec = null;
    this.seperatorDrag = null;
    this.loadingElm = null;
    this.tileElm = null;
    this.tileContentElm = null;
    this.tileResultMetaElm = null;   
    this.listeners = new Array();
}

ResultTile.prototype.getTileContentNode = function(){
    return this.tileContentElm;   
}

ResultTile.prototype.getTileResultMetaElm = function(){
    return this.tileResultMetaElm;
}

ResultTile.prototype.showLoadingDialog = function(){
    var dim = MochiKit.Style.getElementDimensions(this.tileElm);
    var pos = MochiKit.Style.getElementPosition(this.tileElm);
    MochiKit.Logging.log('dim: ' + dim + ', pos: ' + pos);
    pos = new MochiKit.Style.Coordinates((pos.x+20),(pos.y+40));   
    MochiKit.Logging.log('new pos: ' + pos);
    this.loadingElm = MochiKit.DOM.IMG({'src':imagePathUrl+loadingImg},null);       
    MochiKit.DOM.appendChildNodes(MochiKit.DOM.currentDocument().body,this.loadingElm); 
    MochiKit.Style.setStyle(this.loadingElm,{'position':'absolute'});   
    MochiKit.Style.setElementPosition(this.loadingElm,pos);
    MochiKit.Logging.log('loadingelm pos: ' + getElementPosition(this.loadingElm));
}

ResultTile.prototype.hideLoadingDialog = function(){    
    if(MochiKit.Base.isUndefinedOrNull(this.loadingElm) == false){
        MochiKit.DOM.removeElement(this.loadingElm);
    }    
    MochiKit.Logging.log('hide loading dialog');
}

ResultTile.prototype.createHTMLElement = function(parentNode){     
   this.tileResultMetaElm = MochiKit.DOM.DIV({'class':'resultMeta'});
   this.tileContentElm = MochiKit.DOM.DIV({'class':'tileContent'},this.tileResultMetaElm);
   this.tileElm = MochiKit.DOM.TD({'id':this.getId()},
      MochiKit.DOM.DIV({'class':'tileTitle'},
      MochiKit.DOM.SPAN({'class':'searchShortName'},this.service.shortName)),
      this.tileContentElm); 
   MochiKit.DOM.appendChildNodes(parentNode,this.tileElm);  
   
   this.searchExec = new SearchExecutor(this.service,this);
}

ResultTile.prototype.createSeperatorHTMLElement = function(parentNode){
    var spElm = MochiKit.DOM.TD({'id':this.getSeperatorId(),'class':'tileSeperator'},
      MochiKit.DOM.IMG({'src':imagePathUrl+'/drag-resize.jpg'},null)
    );    
    MochiKit.DOM.appendChildNodes(parentNode,spElm);
    this.seperatorDrag = MochiKit.Signal.connect(this.getSeperatorId(), 'onmousedown',ResizeTile.dragStart);        
    return spElm;
}



ResultTile.prototype.notifyResultEntryCreated = function(entryNode){
    function entryFocused(e){
      var elm = e.src();
      elm.quicksave = new QuickSave(elm);
      MochiKit.DOM.addElementClass(elm,entryFocusedClass);              
      var invisibleElms = MochiKit.DOM.getElementsByTagAndClassName(null,'invisible',elm);       
      for(var i = 0; i < invisibleElms.length; i++){
          var ivElm = invisibleElms[i];
          MochiKit.DOM.removeElementClass(ivElm,'invisible');
          MochiKit.DOM.addElementClass(ivElm,'visible');
      }
    }
    
    function entryUnfocused(e){
      var elm = e.src();
      MochiKit.DOM.removeElementClass(elm,entryFocusedClass);
      if(MochiKit.Base.isUndefinedOrNull(elm.quicksave) == false){
          elm.quicksave.destroy();
      }
      var visibleElms = MochiKit.DOM.getElementsByTagAndClassName(null,'visible',elm);       
      for(var i = 0; i < visibleElms.length; i++){
          var vElm = visibleElms[i];
          MochiKit.DOM.removeElementClass(vElm,'visible');
          MochiKit.DOM.addElementClass(vElm,'invisible');
      }   
    }
    
    if(MochiKit.Base.isUndefinedOrNull(entryNode) == false){
        var ln1 = MochiKit.Signal.connect(entryNode,'onmouseenter',entryFocused);
        var ln2 = MochiKit.Signal.connect(entryNode,'onmouseleave',entryUnfocused);
    }
    this.listeners.push(ln1,ln2);
    MochiKit.Logging.log('ResultTile : notifyResultEntryCreated: created 2x listeners. new listeners length: ' 
    + this.listeners.length);
}

ResultTile.prototype.destroySeperator = function(){
	var tileSpElm = MochiKit.DOM.getElement(this.getSeperatorId());
    if(MochiKit.Base.isUndefinedOrNull(tileSpElm) == false){
        MochiKit.DOM.removeElement(this.getSeperatorId());
        MochiKit.Signal.disconnect(this.seperatorDrag);          
    }    
}

ResultTile.prototype.destroy = function(){
    if(this.tileElm != null){
        MochiKit.DOM.removeElement(this.tileElm);        
    }
   	this.destroySeperator();
    if(MochiKit.Base.isUndefinedOrNull(this.searchExec) == false){
        this.searchExec.terminate();
    }  
    var i = 0;
    while(this.listeners.length > 0){
        var ln = this.listeners.pop();
        MochiKit.Signal.disconnect(ln);
        MochiKit.Logging.log('ResultTile : destroy: disconnected listener ' + i++);
    }
}

ResultTile.prototype.getId = function(){
    return 'tile'+this.service.id;
}

ResultTile.prototype.getSeperatorId = function(){
    return 'SP'+this.getId();
}

function SearchManager(searchResultRowId, services){
    this.searchResultRowId = searchResultRowId;   
    this.serviceMap = {};
    
    var numTilesShown = 0;
    for(var i = 0; i < services.length; i++){
        if(services[i].defaultEnabled == true){
            numTilesShown++;
        }    
    }       
    var spCnt = 0;
    for(var i = 0; i < services.length; i++){        
        var aSrv = services[i];
        this.serviceMap[aSrv.id] = aSrv;        
        MochiKit.Logging.log('SearchManager: serviceMap added: ' + this.serviceMap[aSrv.id].shortName);
        var resultTile = new ResultTile(aSrv);
        this.serviceMap[aSrv.id].resultTile = new ResultTile(aSrv);     
        if(this.serviceMap[aSrv.id].defaultEnabled == true){        
            this.serviceMap[aSrv.id].resultTile.createHTMLElement(this.searchResultRowId);             
            if(spCnt < (numTilesShown-1)){
                this.serviceMap[aSrv.id].resultTile.createSeperatorHTMLElement(this.searchResultRowId);
                spCnt++;
            }
        }                                     
   }  
}

SearchManager.prototype.hideService = function(serviceId){
     var aSrv = this.serviceMap[serviceId];
     if(MochiKit.Base.isUndefinedOrNull(aSrv) == false){
         var tileId = aSrv.resultTile.getId();
         MochiKit.Logging.log('trying to remove element: ' + tileId);
         // do clean up
         aSrv.resultTile.destroy();                   
		 var resultRowElm = MochiKit.DOM.getElement(this.searchResultRowId);        	
		 if(resultRowElm.childNodes.length > 0){
		 	MochiKit.Logging.log('resultRowElm childNodes length: ' + resultRowElm.childNodes.length);
		 	var lastTileId = resultRowElm.lastChild.id;
		 	MochiKit.Logging.log('lastTileId=' + lastTileId);
		 	var lastTile = this.findResultTile(lastTileId);
		 	MochiKit.Logging.log('lastTile : ' + lastTile);
		 	if(MochiKit.Base.isUndefinedOrNull(lastTile) == false){
		 		lastTile.destroySeperator();		 		
		 	}
		 }		
     }else{
         MochiKit.Logging.log('attempt to remove an undefined service: ' + aSrv.shortName);
     }
}

SearchManager.prototype.showService = function(serviceId){
    var aSrv = this.serviceMap[serviceId];
    if(MochiKit.Base.isUndefinedOrNull(aSrv) == false){
        var tileId = aSrv.resultTile.getId();
        var tile = MochiKit.DOM.getElement(tileId);
        if(MochiKit.Base.isUndefinedOrNull(tile) == true){
            var resultRowElm = MochiKit.DOM.getElement(this.searchResultRowId);
            if(resultRowElm.childNodes.length > 0){
                var lastTileId = resultRowElm.lastChild.id;
                var lastTile = this.findResultTile(lastTileId);
                if(MochiKit.Base.isUndefinedOrNull(lastTile) == false){
                     lastTile.createSeperatorHTMLElement(this.searchResultRowId);
                }
            }
            aSrv.resultTile.createHTMLElement(this.searchResultRowId);           
        }           
    }else{
        MochiKit.Logging.log('attempt to remove an undefined service: ' + aSrv.shortName);
    }
}

SearchManager.prototype.findResultTile = function(tileElmId){
	var id = tileElmId;
	// if matches a Separator ID, extract the substring
	if(id.match("SP")){
		id = id.substr(2);
	}
    var services = MochiKit.Base.values(this.serviceMap);
    for(var i = 0; i < services.length; i++){
    	MochiKit.Logging.log('findResultTile: checking ID: ' + services[i].resultTile.getId());
        if(services[i].resultTile.getId() == id){
            return services[i].resultTile;
        }
    }
    return null;
}

function setupSearchServiceSelection(){    
    function handleSearchServiceSelection(e){
        if(e.src().checked == true){            
            searchManager.showService(this.id);          
            checkedServicesMap[this.id] = 1;               
        }else{
            searchManager.hideService(this.id);
            checkedServicesMap[this.id] = 0;    
        }
        MochiKit.Logging.log('setupSearchServiceSelection: checkedServicesMap[\'' + this.id +'\']='
                             + checkedServicesMap[this.id]);
        writeCheckedServicesToCookie();                   
    }
    readCheckedServicesFromCookie();
    for(var i = 0; i < services.length; i++){
        var aSrv = services[i]; 
        var slcElm =  MochiKit.DOM.INPUT({'type':'checkbox','name':aSrv.id},null);               
        var aSrvElm = MochiKit.DOM.LI(null,slcElm,aSrv.shortName);
        MochiKit.DOM.appendChildNodes(selectSearchServicesULID,aSrvElm);   
        // if configuratoin exists in the cookie, override the default values from 
        // the server
        if(MochiKit.Base.isUndefinedOrNull(checkedServicesMap[aSrv.id]) == false){
            if(checkedServicesMap[aSrv.id] == 1){
               aSrv.defaultEnabled = true;
            }else{
               aSrv.defaultEnabled = false;
            }
        }
        slcElm.checked = aSrv.defaultEnabled;
        if(slcElm.checked == true){
            checkedServicesMap[aSrv.id] = 1;
        }else{
            checkedServicesMap[aSrv.id] = 0;
        }
        MochiKit.Logging.log('setupSearchServiceSelection: checkedServicesMap[\'' + aSrv.id +'\']='
                             + checkedServicesMap[aSrv.id]);
        MochiKit.Signal.connect(slcElm,'onclick',aSrv,handleSearchServiceSelection);        	                   
    }
    writeCheckedServicesToCookie();
    
}

/**
 * initializePage
 */
function initializePage() {
   //MochiKit.LoggingPane.createLoggingPane(true);
   setupSearchServiceSelection();
   searchManager = new SearchManager(resultTilesRowID,services); 
}

MochiKit.Signal.connect(window,'onload',initializePage);