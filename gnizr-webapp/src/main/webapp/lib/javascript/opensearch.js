var resultTilesTableID = 'resultTiles';
var resultTilesRowID = 'resultTilesRow';
var selectSearchServicesULID = 'selectSearchServices';

/** OpenSearch service objects */
var services = new Array();
var imagePathUrl = '';
var searchManager = null;
var queryTerm = null;
var proxyUrl = null;

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

function parseOpenSearchResult(atomXml){
    MochiKit.Logging.log('here: ' + atomXml);   
    var resultMap = {};
    if(MochiKit.Base.isUndefinedOrNull(atomXml) == true){
        return resultMap;
    }
    var title = parseStringOne(atomXml,'title');
    MochiKit.Logging.log('title: ' + title);
    var link = parseStringOne(atomXml,'link','href');
    MochiKit.Logging.log('link: ' + link);
    var itemsPerPage = parseIntegerOne(atomXml,'opensearch:itemsPerPage',null,0); 
    MochiKit.Logging.log('itemsPerPage: ' + itemsPerPage);
    var totalResults = parseIntegerOne(atomXml,'totalResults',null,0);
    MochiKit.Logging.log('totalResults: ' + totalResults);
    var startIndex = parseIntegerOne(atomXml,'startIndex',null,-1);
    MochiKit.Logging.log('startIndex: ' + startIndex);    
};

function parseIntegerOne(dom,tag,attr,def){
    var value = parseStringOne(dom,tag,attr);
    MochiKit.Logging.log('v='+value);
    if(MochiKit.Base.isUndefinedOrNull(value)){
        if(MochiKit.Base.isUndefinedOrNull(def) == false){
            return def;
        }else{
            return 0;
        }
    }else{
        return value;
    }
}
function parseStringOne(dom,tag,attr){
    var elm = MochiKit.DOM.getFirstElementByTagAndClassName(tag,null,dom);
    if(MochiKit.Base.isUndefinedOrNull(elm) == false){        
        if(MochiKit.Base.isUndefinedOrNull(attr) == false){
            return MochiKit.DOM.getNodeAttribute(elm,attr);
        }else{
            MochiKit.Logging.log('node value: ' + elm.firstChild.nodeValue);
            return elm.firstChild.nodeValue;
        }
    }
    return null;
}


function SearchExecutor(service,contentNode){
    this.service = service;   
    this.contentNode = contentNode;
    this.qs = decodeURIComponent(queryTerm);
    this.startPage = -1;
    this.startIndex = -1;
    this.totalResult = 0;    
    this.searchDataUrl = this.getNextSearchQuery();    
    this.fetchMoreDataLinkElm = MochiKit.DOM.A({'href':'#'},'more');    
    this.resultListElm = MochiKit.DOM.OL({'class':'resultList'},null);
    MochiKit.DOM.appendChildNodes(contentNode,this.resultListElm, this.fetchMoreDataLinkElm);
  
    // don't forget to disconnect this signal.
    this.fetch = MochiKit.Signal.connect(this.fetchMoreDataLinkElm,'onclick',this,'fetchMoreData');    
}

SearchExecutor.prototype.fetchMoreData = function(){    
    var resultElm = this.resultListElm;
    var onSucceed = function(result){
        MochiKit.Logging.log('got result: size= ' + result.entries.length);  
        if(MochiKit.Base.isUndefinedOrNull(result.entries) == false){
            var entries = result.entries;
            for(var i = 0; i < entries.length; i++){
               MochiKit.Logging.log('e'+i+':'+entries[i].link+','+entries[i].title);
               var entryElm = MochiKit.DOM.LI(null,MochiKit.DOM.SPAN(null,entries[i].title));
               MochiKit.DOM.appendChildNodes(resultElm,entryElm);
            }
        }
    };
    var onFailed = function(result){
        MochiKit.Logging.log('fetch data failed. ' + result);
    }
    
    MochiKit.Logging.log('try to fetchMoreData from : ' + this.searchDataUrl);
    //var d = MochiKit.Async.doSimpleXMLHttpRequest(this.searchDataUrl);
    if(MochiKit.Base.isUndefinedOrNull(proxyUrl) == false){
        var callUrl = proxyUrl + encodeURIComponent(this.searchDataUrl);
        MochiKit.Logging.log('AJAX call: ' + callUrl);
        var d = MochiKit.Async.loadJSONDoc(callUrl);
        d.addCallbacks(onSucceed,onFailed);            
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
    var qTerms = MochiKit.Base.parseQueryString(pStr[1]);
    var qTerms2 = {}; // needed bec parseQueryString include '&' as a key
    for(key in qTerms){
        if(qTerms[key] == '{searchTerms}'){
            qTerms2[key] = this.qs;
            MochiKit.Logging.log('replace {searchTerms} with ' + qTerms2[key]);
        }else if(qTerms[key] == '{startPage}'){
            if(this.startPage <= 0){
                this.startPage = 1;
            }else{
                this.startPage++;
            }
            qTerms2[key] = this.startPage;
            MochiKit.Logging.log('replace {startPage} with ' + qTerms2[key]);
        }else if(key.match(/(\&amp\;|\&\#38\;|\&#x26;|\&)/) == false){            
            qTerms2[key] = qTerms[key];
        }
    }
    for(k in qTerms2){
        MochiKit.Logging.log('k=' + k + ',v='+qTerms2[k]);
    }
    var nxtQuery = baseUrl + '?' +MochiKit.Base.queryString(qTerms2);
    MochiKit.Logging.log('Return from getNextSearchQuery=' + nxtQuery);
    return nxtQuery;
}

function ResultTile(service){
    this.service = service;
    this.searchExec = null;
    this.seperatorDrag = null;
}

ResultTile.prototype.createHTMLElement = function(parentNode){       
   var tileCntElm = MochiKit.DOM.DIV({'class':'tileContent'});
   var tileElm = MochiKit.DOM.TD({'id':this.getId()},
      MochiKit.DOM.DIV({'class':'tileTitle'},this.service.shortName),
      tileCntElm); 
   MochiKit.DOM.appendChildNodes(parentNode,tileElm);
   this.searchExec = new SearchExecutor(this.service,tileCntElm);
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
    for(var i = 0; i < services.length; i++){
        var aSrv = services[i];
        this.serviceMap[aSrv.id] = aSrv;
        MochiKit.Logging.log('SearchManager: serviceMap added: ' + this.serviceMap[aSrv.id].shortName);
        var resultTile = new ResultTile(aSrv);
        this.serviceMap[aSrv.id].resultTile = new ResultTile(aSrv);           
        this.serviceMap[aSrv.id].resultTile.createHTMLElement(this.searchResultRowId);             
        if((i+1) < services.length){
            this.serviceMap[aSrv.id].resultTile.createSeperatorHTMLElement(this.searchResultRowId);
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
        var slcElm =  MochiKit.DOM.INPUT({'type':'checkbox','name':aSrv.id,'checked':'true'},null);        
        MochiKit.Signal.connect(slcElm,'onclick',aSrv,handleSearchServiceSelection);        	
        var aSrvElm = MochiKit.DOM.LI(null,slcElm,aSrv.shortName);
        MochiKit.DOM.appendChildNodes(selectSearchServicesULID,aSrvElm);        
    }
}

/**
 * initializePage
 */
function initializePage() {
   MochiKit.LoggingPane.createLoggingPane(true);
   setupSearchServiceSelection();
   searchManager = new SearchManager(resultTilesRowID,services);
   /*
   MochiKit.Signal.connect('t1sp', 'onmousedown',ResizeTile.dragStart);
   MochiKit.Signal.connect('t2sp', 'onmousedown',ResizeTile.dragStart);
   MochiKit.Signal.connect('t3sp', 'onmousedown',ResizeTile.dragStart);
   */
}

MochiKit.Signal.connect(window,'onload',initializePage);