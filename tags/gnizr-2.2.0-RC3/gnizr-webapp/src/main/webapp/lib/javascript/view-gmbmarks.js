var selectBookmarkDIVId = 'selectBookmark';
var mapDIVId = 'map';
var bookmarkListTBODYId = 'bookmarkList';
var slctBmrkOptDIVId = 'selectBookmarkOpt';
var shwAllPlcMrkAId = 'showAllPlacemarks';
var hdeAllPlcMrkAId = 'hideAllPlacemarks';
var shwPlcMrkInputClass = 'showPlacemark';
var bmrkDspDIVId = 'bookmarkDescription';
var leftFtDIVId = 'leftFooter';
var loadingDIVId = 'loading';

var bookmarks = new Array();
var bookmarksMap = {};
var maxPageNumber = 1;
var bmarkTotalNum = 0;
var curPageNumber = 1;
var pagingSrvUrl = null;
var getGMarkerSrvUrl = null; 
var getMarkerIconSrvUrl = null;
var userTaggedBookmarksUrl = null;
var edtBookmarkSrvUrl = null;

// data objects
var gmap = null;
var gmapMrkMgr = null;

/* key: bmId, value: a map of geometry markers. 
 * - geometry marker map
 *   - key: 'GMarker', 'GPolyline', 'GPolygon' (only GMarker is currently supported)
 *   - value: an array of Google Maps objects.
 */
var gmapObjectMap = {};
var TYPE_GMARKER = 'GMarker';

function bmId(id){
	return 'bm_'+id;
}

function bmDspId(id){
	return 'dsp_'+bmId(id);
}

function bmShwPlcmkId(id){
	return 'shpl_'+bmId(id);
}

function isPlcmkJsonLoaded(bmId){
	var obj = gmapObjectMap[bmId];
	if(MochiKit.Base.isUndefinedOrNull(obj)){
		return false;
	}
	return true;
}

function getBookmark(bmId){
	/*
	for(var i = 0; i < bookmarks.length; i++){
		if(bookmarks[i].id == bmId){		
			return bookmarks[i];
		}
	}
	*/
	return bookmarksMap[bmId];
}

function cacheBookmarks(bmarks){
	var cachedBmarks = new Array();
	for(var i = 0; i < bmarks.length; i++){
		var id = bmarks[i].id;
		if(MochiKit.Base.isUndefinedOrNull(bookmarksMap[id]) == true){
			bookmarksMap[id] = bmarks[i];
			bookmarks.push(bmarks[i]);
			MochiKit.Logging.log('adding bmark: ' + id + ' to cache');
		}else{
			MochiKit.Logging.log('skip adding bmark: ' + id + ' to cache');
		}
		cachedBmarks.push(bookmarksMap[id]);
	}
	return cachedBmarks;
}

function getMyMakersOfType(bmId, type){
	var obj = gmapObjectMap[bmId];
	if(MochiKit.Base.isUndefinedOrNull(obj) == false){
		return obj[type];
	}
	return null;
}

function createMyGMapObjectMap(bmId){
	if(MochiKit.Base.isUndefinedOrNull(gmapObjectMap[bmId])){
		gmapObjectMap[bmId] = {};
	}
	return gmapObjectMap[bmId];
}

function setEditBookmarkServiceUrl(srvurl){
	edtBookmarkSrvUrl = srvurl;
}

function setGetGeometryMarkerServiceUrl(srvurl){
	getGMarkerSrvUrl = srvurl;
	MochiKit.Logging.log('set getGMarkerSrvUrl = ' + getGMarkerSrvUrl);
}

function setPagingServiceUrl(srvurl){
	pagingSrvUrl = srvurl;
	MochiKit.Logging.log('set pagingSrvUrl = ' + pagingSrvUrl);
}

function setGetMarkerIconServiceUrl(srvurl){
	getMarkerIconSrvUrl = srvurl;
	MochiKit.Logging.log('set getMarkerIconSrvUrl = ' + getMarkerIconSrvUrl);
}

function setUserTaggedBookmarksUrl(srvurl){
	userTaggedBookmarksUrl = srvurl;
	MochiKit.Logging.log('set userTaggedBookmarksUrl = ' + userTaggedBookmarksUrl);
}

function loadBookmarkData(data,curPage,pageMax,bmTotal){
	if(MochiKit.Base.isUndefinedOrNull(data) == false){
		cacheBookmarks(data);
		curPageNumber = curPage;
		maxPageNumber = pageMax;
		bmarkTotalNum = bmTotal;		
		MochiKit.Logging.log('initialized bookmarks with ' + bookmarks.length + ' objects');
		MochiKit.Logging.log('set maxPageNumber = ' + maxPageNumber);
		MochiKit.Logging.log('set bmarkTotalNum = ' + bmarkTotalNum);		
	}	
}

function setSelectionBookmarks(bmarks){
	MochiKit.DOM.replaceChildNodes(bookmarkListTBODYId);
	for(var i = 0; i < bmarks.length ; i++){
		var id = bmarks[i].id;
		var bmTtlElm = MochiKit.DOM.A(
			{'href':'javascript:showDescription('+id+')',
			 'class':'bookmarkTitle system-link'},
		     bmarks[i].title);
		var bmPlcMkShwElm = MochiKit.DOM.INPUT(
		  	{'id':bmShwPlcmkId(id),
		  	 'class':'showPlacemark',
		  	 'value':id,
		  	 'type':'checkbox',
		  	 'onclick':'toggleShowPlacemark('+id+');'});	  	 
		var rowElm = MochiKit.DOM.TR(null,
  		  MochiKit.DOM.TD({'class':''},(i+1)+(curPageNumber-1)*250),
		  MochiKit.DOM.TD({'class':'bookmarkTitleCell'},bmTtlElm),
		  MochiKit.DOM.TD({'class':'showHidePlacemarkCell'},bmPlcMkShwElm)
		);				
		MochiKit.DOM.appendChildNodes(bookmarkListTBODYId,rowElm);
		if(bmarks[i].isShowPlacemarkChecked == true){		
			bmPlcMkShwElm.checked = true;
			MochiKit.Logging.log('bmarks['+i+'] is checked -- ' + bmPlcMkShwElm.checked);
		}else{
			MochiKit.Logging.log('bmarks['+i+'] is not checked');
		}	
	}
	setPagingControls();
}

function hideLoadingImg(){
	MochiKit.DOM.addElementClass(loadingDIVId,'invisible');
}

function showLoadingImg(){
	MochiKit.DOM.removeElementClass(loadingDIVId,'invisible');
}

function toggleShowPlacemark(bmId){
	var callback = function(){		
		gmapMrkMgr.refresh();
		hideLoadingImg();
	}
	showLoadingImg();
	doShowHidePlacemarks(bmId,callback);
}

function clearDescription(){
	MochiKit.Logging.log('cleraDescription called');
	MochiKit.DOM.replaceChildNodes(bmrkDspDIVId,null);
}

function doShowHidePlacemarks(bmId, onFinishedCallback){
	var myGObjectMap = gmapObjectMap[bmId];	
	var bm = getBookmark(bmId);
	var shwPlcmrkElm = MochiKit.DOM.getElement(bmShwPlcmkId(bmId));
	if(shwPlcmrkElm.checked == true){	
		/* set a BOOLEAN flag on the bookmark to help function 'appendBookmarks' to
		 * decide whether the bookmark's check box is clicked */
		bm.isShowPlacemarkChecked = true;
		if(MochiKit.Base.isUndefinedOrNull(myGObjectMap)){
			myGObjectMap = createMyGMapObjectMap(bmId);
			var qs = MochiKit.Base.queryString(['id'],[bmId]);
			var d = MochiKit.Async.loadJSONDoc(getGMarkerSrvUrl+'?'+qs);
			MochiKit.Logging.log('calling getGMarkerSrv: '+getGMarkerSrvUrl+'?'+qs);
			// data is a map of gnizr geometry markers
			var gotData = function(data){
				MochiKit.Logging.log('gotData for bmId: ' + bmId);			
				for(var gmType in data){
					if(gmType == 'pointMarker'){
						var markers = createGMapGMarkers(bm,data[gmType]);
						if(MochiKit.Base.isUndefinedOrNull(myGObjectMap[TYPE_GMARKER])){
							myGObjectMap[TYPE_GMARKER] = new Array();
						}
						myGObjectMap[TYPE_GMARKER] = 
							MochiKit.Base.concat(myGObjectMap[TYPE_GMARKER],markers);
						// let's GMarkerManager to handle the display of our markers
						gmapMrkMgr.addMarkers(myGObjectMap[TYPE_GMARKER],0);																
					}else{
						Logging.Logging.log('unsupported geometry marker type detected. ' + gmType);
					}
				}				
				if(MochiKit.Base.isUndefinedOrNull(bm.showMyDescriptionCallback) == false){
					var shwMyDspCallback = bm.showMyDescriptionCallback;
					shwMyDspCallback(bmId);
				}
				if(MochiKit.Base.isUndefinedOrNull(onFinishedCallback) == false){
					onFinishedCallback();
				}				
			};
			var dataFetchFailed = function(err){
				if(MochiKit.Base.isUndefinedOrNull(onFinishedCallback) == false){
					onFinishedCallback();
				}
				alert('Error: ' + err);
			}
			d.addCallbacks(gotData,dataFetchFailed);
		}else{
			for(var type in myGObjectMap){
				var markers = myGObjectMap[type];
				MochiKit.Logging.log('# of markers of type: ' + type + ' = ' + markers.length);
				gmapMrkMgr.addMarkers(markers,0);
			}
			//gmapMrkMgr.refresh();	
			if(MochiKit.Base.isUndefinedOrNull(onFinishedCallback) == false){
				onFinishedCallback();
			}
		}
	}else{
		/* set a BOOLEAN flag on the bookmark to help function appendBookmarks to
		 * decide whether the bookmark's check box is clicked */
		bm.isShowPlacemarkChecked = false;
		for(var type in myGObjectMap){
			var markers = myGObjectMap[type];
			for(var i = 0; i < markers.length; i++){
				gmapMrkMgr.removeMarker(markers[i]);			
			}
		}
		if(MochiKit.Base.isUndefinedOrNull(onFinishedCallback) == false){
			onFinishedCallback();
		}
		clearDescription();
	}
}

function createGMapGMarkers(bookmark, pointMarkers){
	var markers = new Array();
	for(var i = 0; i < pointMarkers.length; i++){
		var pm = pointMarkers[i];
		var pmPt = pm.point.split(',');		
		var gLatLng = new GLatLng(pmPt[1],pmPt[0]);
		MochiKit.Logging.log('from pmPt: ' + pm.point + ' to ' + gLatLng);		
		var markerD = new GMarker(gLatLng,{icon:G_DEFAULT_ICON, draggable: false});
		markerD.pm = pm;
		GEvent.addListener(markerD,'click',
		 	function(){
		 		var pm = this.pm;
		 		var content = createPlacemarkerNotesHtml(bookmark,pm);
		 		this.openInfoWindowHtml(content);
		 	}
		);
		markers.push(markerD);
	
	}
	return markers;
}

function createPlacemarkerNotesHtml(bm,pm){
	var contentElm = MochiKit.DOM.DIV({'class':'notesContainer'},
		MochiKit.DOM.P({'class':'notes'},pm.notes),
		MochiKit.DOM.P({'class':'notesFrom'},'from: ',
			MochiKit.DOM.A({'class':'system-link',
			                'href':'javascript:showDescription('+bm.id+')'},bm.title))
	);
	return contentElm;
}

function showDescriptionHtml(bmId){		
	var bmark = getBookmark(bmId);
	MochiKit.Logging.log('bm: ' + bmark.id + ' title: ' + bmark.title);
	var titleElm = MochiKit.DOM.H2({'class':'title'},
		MochiKit.DOM.A({'href':bmark.url,'target':'_blank'},bmark.title));
	var edtElm = '';
	if(MochiKit.Base.isUndefinedOrNull(edtBookmarkSrvUrl) == false){
		var redirectTo = window.location.href;
		var qs = MochiKit.Base.queryString(['url','redirectToPage'],[bmark.url,redirectTo]);
	    var edUrl = edtBookmarkSrvUrl+'?'+qs;
	    edtElm = MochiKit.DOM.P(null,
	         MochiKit.DOM.A({'href':edUrl,'class':'system-link'},
	                         'edit this bookmark'));
	}
    var notesElm = MochiKit.DOM.P({'class':'notes'},bmark.summary);
    var tags = bmark.tags;
    var tagsSubTitleElm = '';
    var tagsListElm = '';	  
    if(tags.length > 0){
    	MochiKit.Logging.log('tags: ' + tags);    
	    tagsSubTitleElm = MochiKit.DOM.createDOM('H5',null,'tags');	    
	    //MochiKit.Logging.log('h5 ' + toHTML(tagsSubTitleElm));
	    tagsListElm = MochiKit.DOM.UL({'class':'tags'});
	    for(var i = 0; i < tags.length; i++){
	    	var qs = MochiKit.Base.queryString(['tag'],[tags[i]]);
	    	var url = userTaggedBookmarksUrl + '&'+qs;
	    	MochiKit.DOM.appendChildNodes(tagsListElm,
	    	  MochiKit.DOM.LI(null,
	    	    MochiKit.DOM.A({'href':url,'target':'_blank'},tags[i])));
	    }
	    //MochiKit.Logging.log('ul ' + toHTML(tagsListElm));
	    MochiKit.DOM.appendChildNodes(tagsSubTitleElm,tagsListElm);
    }	
    var plcMrkSubTitleElm = MochiKit.DOM.createDOM('H5',null,'placemarks');
	var plcMrkListElm = MochiKit.DOM.UL({'class':'placemarks'});
    var myGObjectMap = gmapObjectMap[bmId];	
    for(var type in myGObjectMap){
    	var markers = myGObjectMap[type];
    	MochiKit.Logging.log('type ' + type + ' # of markers ' + markers.length);
    	for(var i = 0; i < markers.length; i++){
    		var gMarker = markers[i];
    		var iconQs = MochiKit.Base.queryString(['iconId'],[gMarker.pm.iconId]);    	
    		MochiKit.DOM.appendChildNodes(plcMrkListElm,
	    	  MochiKit.DOM.LI(null, 
	    	    MochiKit.DOM.IMG({'src':getMarkerIconSrvUrl+'?'+iconQs,
	    	                      'alt':'click to zoom to this placemark',
	    	                      'onclick':'zoomToPlacemark('+bmId+',"'+type+'",'+i+')'}),
	    	    gMarker.pm.notes));            	    	  
    	}
    }
    var dom = MochiKit.DOM.replaceChildNodes(bmrkDspDIVId,
      MochiKit.DOM.DIV({'class':'scrollableList'},
            titleElm,edtElm,notesElm,    	
	        tagsSubTitleElm,tagsListElm,
    	 	plcMrkSubTitleElm,plcMrkListElm));    
}

function zoomToPlacemark(bmId,type,markerIdx){
	MochiKit.Logging.log(bmId + ',' +type+','+markerIdx);
	var markers = getMyMakersOfType(bmId,type);
	var gMarker = markers[markerIdx]; 
		
	var gLatLng = gMarker.getLatLng();
	MochiKit.Logging.log('zoomToPlacemark: panTo ' + gLatLng);
	gmap.panTo(gLatLng);
		
	var bm = getBookmark(bmId);
	MochiKit.Logging.log('zoomToPlacemark: getBookmark of bmId='+bmId + ', bm='+bm);	
	var content = createPlacemarkerNotesHtml(bm,gMarker.pm);
	MochiKit.Logging.log('zoomToPlacemark: content created: ' + toHTML(content));
	MochiKit.Logging.log('gMarker isHidden():  ' + gMarker.isHidden());
	if(gMarker.isHidden() == false){
		gMarker.openInfoWindowHtml(content);
	}
	
}

function showDescription(bmId){
	if(isPlcmkJsonLoaded(bmId) == false){
		var bm = getBookmark(bmId);			
		bm.showMyDescriptionCallback = showDescriptionHtml;
		MochiKit.Logging.log('bmId: ' + bmId + ' has showMyDescriptionCallback set.');
	}else{
		MochiKit.Logging.log('calling showDescriptionHtml');
		showDescriptionHtml(bmId);		
	}		
	var shwPlcmrkElm = MochiKit.DOM.getElement(bmShwPlcmkId(bmId));
	if(shwPlcmrkElm.checked == false){
		shwPlcmrkElm.click();
	}
}

function initMap(){
	if (GBrowserIsCompatible()) {
        gmap = new GMap2(MochiKit.DOM.getElement(mapDIVId));        
        gmap.addControl(new GLargeMapControl());
        gmap.addControl(new GMapTypeControl());
        //gmap.addControl(new GOverviewMapControl());       
        
        var mt = gmap.getMapTypes();
        // Overwrite the getMinimumResolution() and getMaximumResolution() methods
        for (var i=0; i<mt.length; i++) {
        	mt[i].getMinimumResolution = function() {return 1;}
	        //mt[i].getMaximumResolution = function() {return 11;}
    	}         
        gmap.enableContinuousZoom();
		gmap.enableDoubleClickZoom();	
		gmap.enableScrollWheelZoom();
  	    gmap.setCenter(new GLatLng(0,0),1,G_HYBRID_MAP);  	   
  	   
  	    gmapMrkMgr = new MarkerManager(gmap,{maxZoom:18});  	  
    }	   
}

function setMenuOptionHref(){
	if(bmarkTotalNum > 0){
		MochiKit.DOM.setNodeAttribute(shwAllPlcMrkAId,'href','javascript:showAllPlacemarks()');
		MochiKit.DOM.setNodeAttribute(hdeAllPlcMrkAId,'href','javascript:hideAllPlacemarks()');
		MochiKit.DOM.removeElementClass(slctBmrkOptDIVId,'invisible');
	}
}

function showAllPlacemarks(){	
	showLoadingImg();
	var bmarkIds = new Array();
	var shwPlcMrkInputElms = 
	  	 MochiKit.DOM.getElementsByTagAndClassName('INPUT',
	      shwPlcMrkInputClass,bookmarkListTBODYId);
	MochiKit.Logging.log('inputElm length: ' + shwPlcMrkInputElms.length);
	for(var i = 0; i < shwPlcMrkInputElms.length; i++){
		var elm = shwPlcMrkInputElms[i];
		if(elm.checked == false){
			elm.checked = true;
			var bmId = parseBookmarkId(elm.id);
			log(i + ': elm.id: ' + elm.id + ', parsed bmid: ' + bmId);
			bmarkIds.push(bmId);
		}
	}      	      	      
	var totalCount = bmarkIds.length;
	if(totalCount > 0){		
		var countUntilFinished = function(){
			totalCount--;
			MochiKit.Logging.log('totalCount = ' + totalCount);		
			if(totalCount == 0){						
				gmapMrkMgr.refresh();
				hideLoadingImg();
			}	
		};
		for(var i = 0; i < bmarkIds.length; i++){
			var bmId = bmarkIds[i];
			doShowHidePlacemarks(bmId,countUntilFinished);
		}	   
	}else if(totalCount == 0){
		hideLoadingImg();
	}		
}

function hideAllPlacemarks(){	
	var bmarkIds = new Array();
	var shwPlcMrkInputElms = 
	  	 MochiKit.DOM.getElementsByTagAndClassName('INPUT',
	      shwPlcMrkInputClass,bookmarkListTBODYId);
	for(var i = 0; i < shwPlcMrkInputElms.length; i++){
		var elm = shwPlcMrkInputElms[i];
		if(elm.checked == true){
			elm.checked = false;
			var bmId = parseBookmarkId(elm.id);
			bmarkIds.push(bmId);
		}
	}      	      	      
	var totalCount = bmarkIds.length;
	if(totalCount > 0){
		showLoadingImg();	
		var countUntilFinished = function(){
			totalCount--;
			if(totalCount == 0){
				hideLoadingImg();
				gmapMrkMgr.refresh();
			}
		};
		for(var i = 0; i < bmarkIds.length; i++){
			var bmId = bmarkIds[i];
			doShowHidePlacemarks(bmId,countUntilFinished);
		}
	}	  	  
}

function parseBookmarkId(s){
	var re = /_(\d+)$/;
	var matched =  re.exec(s);
	MochiKit.Logging.log('s: ' + s + ', matched: ' + matched);
	return matched[1];
}

function fetchBookmarksOnPage(pgnum){	
	var qs = MochiKit.Base.queryString(['page'],[pgnum]);
	var fetchDataUrl = pagingSrvUrl + '&' + qs;
	MochiKit.Logging.log('call paging: ' + fetchDataUrl);
	var d = MochiKit.Async.loadJSONDoc(fetchDataUrl);
	var gotData = function(newBmarks){
		if(newBmarks.length > 0){
			curPageNumber = pgnum;
			var cachedBmarks = cacheBookmarks(newBmarks);		
			setSelectionBookmarks(cachedBmarks);
		}else{
			window.location.reload();
		}	
	}
	var dataFetchFailed = function (err){
		alert('Error: ' + err);
	}
	d.addCallbacks(gotData,dataFetchFailed);
}

function setPagingControls(){
	var pgShwElm = MochiKit.DOM.DIV({'id':'pageShowing'},
	   'page ' + curPageNumber + ' of ' + maxPageNumber);
    log('showing page: ' + curPageNumber + ', max page #: ' + maxPageNumber);	   
	var prvElm = '';
    if(curPageNumber > 1){	   
    	var n = curPageNumber - 1;
      	prvElm = MochiKit.DOM.A({'class':'system-link','href':'javascript:fetchBookmarksOnPage('+n+')'},'previous'); 	   
      	log('preElm set to ' + n);
    }
    var nxtElm = '';
    if(curPageNumber < maxPageNumber){
    	var n = curPageNumber + 1;
    	nxtElm = MochiKit.DOM.A({'class':'system-link','href':'javascript:fetchBookmarksOnPage('+n+')'},'next'); 
    	log('nxtElm set to ' + n);
    }
    var pgngElm = MochiKit.DOM.DIV({'id':'pagingControl'},prvElm,' | ',nxtElm);     
    MochiKit.DOM.replaceChildNodes(leftFtDIVId,pgShwElm,pgngElm);             	   
}

function initializePage(){
	//MochiKit.LoggingPane.createLoggingPane(true);	
	if(gmap == null){
		initMap();
	}
	setMenuOptionHref();
	setSelectionBookmarks(bookmarks);
}

MochiKit.Signal.connect(window,'onload',initializePage);