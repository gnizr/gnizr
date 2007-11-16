var resultTilesTableID = 'resultTiles';
var resultTilesRowID = 'resultTilesRow';
var selectSearchServicesULID = 'selectSearchServices';

/** OpenSearch service objects */
var services = new Array();
var imagePathUrl = '';
var searchManager = null;
var queryTerm = null;
var proxyUrl = null;
var loggedInUser = null;
var loadingImg = '/loading-bar-blue.gif';

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
    
    function appendOneResult(anEntry){
        //MochiKit.Logging.log('e'+i+':'+anEntry.link+','+anEntry.title);
        var title = anEntry.title;
        var summary = '';
        if(MochiKit.Base.isUndefinedOrNull(anEntry.summary) == false){                                      
            summary = anEntry.summary;
            if(summary.length > 250){
                summary = summary.substring(0,250) + '...';
            }
           summary = summary.escapeHTML();
         }
         var link = anEntry.link;
         var entryElm = MochiKit.DOM.LI(null,
            MochiKit.DOM.A({'class':'entryTitle','href':link,'target':'_blank'},title),
            MochiKit.DOM.P(null,summary) 
          );              
         MochiKit.DOM.appendChildNodes(resultElm,entryElm);
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
    if(MochiKit.Base.isUndefinedOrNull(proxyUrl) == false){        
        if(MochiKit.Base.isUndefinedOrNull(this.searchDataUrl) == false){
            this.resultTile.showLoadingDialog();
            var callUrl = proxyUrl + encodeURIComponent(this.searchDataUrl);
            MochiKit.Logging.log('AJAX call: ' + callUrl);
            var d = MochiKit.Async.loadJSONDoc(callUrl);
            d.addCallbacks(onSucceed,onFailed);            
        }else{
            MochiKit.Logging.log('No more results in the searh stream. Halt.');
        }
    }else{
        alert('Internal Error: proxy URL is undefined');    
    }    
}

SearchExecutor.prototype.terminate = function(){
    if(MochiKit.Base.isUndefinedOrNull(this.fetch) == false){
        MochiKit.Signal.disconnect(this.fetch);
        MochiKit.Logging.log('Disconnected the fetch signal of service ID:' + this.service.id);
    }
}

SearchExecutor.prototype.getNextSearchQuery = function(){
    var pStr = decodeURIComponent(this.service.serviceUrl).split('?');
    MochiKit.Logging.log('getNextSearchQuery: split serviceUrl => ' + pStr);
    var baseUrl = pStr[0];  
    // in case, a service considers {searchTerms} as part of the URL path
    baseUrl = baseUrl.replace("{searchTerms}",encodeURIComponent(this.qs)); 
      
    var qTerms = MochiKit.Base.parseQueryString(pStr[1]);
    var qTerms2 = {}; // needed bec parseQueryString include '&' as a key
    for(key in qTerms){
        if(qTerms[key] == '{count}'){
            qTerms2[key] = 10;
            MochiKit.Logging.log('replace {count} with 10');
        }else if(qTerms[key] == '{searchTerms}'){
            qTerms2[key] = this.qs;
            MochiKit.Logging.log('replace {searchTerms} with ' + qTerms2[key]);
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

function ResultTile(service){
    this.service = service;
    this.searchExec = null;
    this.seperatorDrag = null;
    this.loadingElm = null;
    this.tileElm = null;
    this.tileContentElm = null;
    this.tileResultMetaElm = null;
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

ResultTile.prototype.destroy = function(){
    MochiKit.DOM.removeElement(this.getSeperatorId());
    MochiKit.Signal.disconnect(this.seperatorDrag);  
    if(MochiKit.Base.isUndefinedOrNull(this.searchExec) == false){
        this.searchExec.terminate();
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
         MochiKit.DOM.removeElement(tileId);     
         var tileSpId = aSrv.resultTile.getSeperatorId();
         var tileSpElm = MochiKit.DOM.getElement(tileSpId);
         if(MochiKit.Base.isUndefinedOrNull(tileSpElm) == false){
            aSrv.resultTile.destroy();        
         }else{
            var resultRowElm = MochiKit.DOM.getElement(this.searchResultRowId);
            var lastTileSpId = resultRowElm.lastChild.id;  
            var lastTileElmId = lastTileSpId.replace('SP','');
            var lastTile = this.findResultTile(lastTileElmId);
            lastTile.destroy();
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
    var services = MochiKit.Base.values(this.serviceMap);
    for(var i = 0; i < services.length; i++){
        if(services[i].resultTile.getId() == tileElmId){
            return services[i].resultTile;
        }
    }
    return null;
}

function setupSearchServiceSelection(){    
    function handleSearchServiceSelection(e){
        if(e.src().checked == true){
            searchManager.showService(this.id);
        }else{
            searchManager.hideService(this.id);
        }
    }
    for(var i = 0; i < services.length; i++){
        var aSrv = services[i]; 
        var slcElm =  MochiKit.DOM.INPUT({'type':'checkbox','name':aSrv.id},null);               
        var aSrvElm = MochiKit.DOM.LI(null,slcElm,aSrv.shortName);
        MochiKit.DOM.appendChildNodes(selectSearchServicesULID,aSrvElm);   
        slcElm.checked = aSrv.defaultEnabled;
        MochiKit.Signal.connect(slcElm,'onclick',aSrv,handleSearchServiceSelection);        	        
    }
}

/**
 * initializePage
 */
function initializePage() {
   //MochiKit.LoggingPane.createLoggingPane(true);
   setupSearchServiceSelection();
   searchManager = new SearchManager(resultTilesRowID,services);
   /*
   MochiKit.Signal.connect('t1sp', 'onmousedown',ResizeTile.dragStart);
   MochiKit.Signal.connect('t2sp', 'onmousedown',ResizeTile.dragStart);
   MochiKit.Signal.connect('t3sp', 'onmousedown',ResizeTile.dragStart);
   */
}

MochiKit.Signal.connect(window,'onload',initializePage);