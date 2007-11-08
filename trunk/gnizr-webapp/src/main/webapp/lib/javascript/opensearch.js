var resultTilesTableID = 'resultTiles';
var resultTilesRowID = 'resultTilesRow';
var selectSearchServicesULID = 'selectSearchServices';

/** OpenSearch service objects */
var services = new Array();
var imagePathUrl = '';
var searchManager = null;

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

function ResultTile(service){
    this.service = service;
}

ResultTile.prototype.createHTMLElement = function(parentNode){
   var tileElm = MochiKit.DOM.TD({'id':this.getId()},
          this.service.shortName); 
   MochiKit.DOM.appendChildNodes(parentNode,tileElm);
}

ResultTile.prototype.createSeperatorHTMLElement = function(parentNode){
    var spElm = MochiKit.DOM.TD({'id':this.getSeperatorId(),'class':'tileSeperator'},
      MochiKit.DOM.IMG({'src':imagePathUrl+'/drag-resize.jpg'},null)
    );    
    MochiKit.DOM.appendChildNodes(parentNode,spElm);
    this.seperatorDrag = MochiKit.Signal.connect(this.getSeperatorId(), 'onmousedown',ResizeTile.dragStart);        
    return spElm;
}

ResultTile.prototype.destorySeperator = function(){
    MochiKit.DOM.removeElement(this.getSeperatorId());
    MochiKit.Signal.disconnect(this.seperatorDrag);    
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
            aSrv.resultTile.destorySeperator();        
         }else{
            var resultRowElm = MochiKit.DOM.getElement(this.searchResultRowId);
            var lastTileSpId = resultRowElm.lastChild.id;  
            var lastTileElmId = lastTileSpId.replace('SP','');
            var lastTile = this.findResultTile(lastTileElmId);
            lastTile.destorySeperator();
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