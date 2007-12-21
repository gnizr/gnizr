/**
 * This script file is used in the following template pages:
 *  - bookmarks/edit.ftl
 */

/* ID of the HTML text field for editing tags */
var bmarkUrlInputFieldId = "bookmarkUrl";
var tagsInputFieldId = "saveBookmark_tags";

/* ID of the node where the list of user tags will be written to*/
var saveSubmitClass = "saveSubmit";
var saveBookmarkFormId = "saveBookmark";
var submitSaveBookmarkId = "submitSaveBookmark";
var userTagsId = "userTags";
var tagCloudId = "tagCloud";
var selectUserTagDIVId = "selectUserTag";
var editPlcMrkDIVId = "editPlacemarks";
var tagGroupSelectionId = "tagGroupSelection";
var userSelectionId = "userSelection";
var loadYourTagsId = "loadYourTags";
var loadRecommendedTagsId = "loadRecommendedTags";
var clearTaglineId = "clearTagline";
var selectTagsNoneId = "selectTagsNone";
var selectTags5Id = "selectTags5";
var selectTags10Id = "selectTags10";
var openUrlWindowId = "openUrlWindow";
var pagePreviewControlId = "pagePreviewControl";
var edToolsId = "editTools";
var edToolsAddTagsId = "addTags";
var edToolsAddPlcMrkId = "addPlacemarks";
var addPlacemarkId = "createPlacemark";
var zoomToId="zoomTo";
var zoomToInputId="zoomToInput";
var mapId = "map";
var geomMarkerClass = "geomMarker";
var machineTagHelpersSPANId = "machineTagHelpers";
var geonamesMTHelperId = "geonamesMTHelper";
var forUserMTHelperId = "forUserMTHelper";
var folderMTHelperId = "folderMTHelper";
var subscribeMTHelperId = "subscribeMTHelper";
var suggestedTagsDIVId = "suggestedTags";

/* to be defined in the header of an HTML page */
var getRecommendedTagsUrl = null;
var getUserTagsUrl = null;
var getUserTagGroupUrl = null;

/* stores the ID of the input button from which the form is submitted */
var submitSrcButtonId = null;

var tagIdPrefixMap = {userTagsId:'ti_'};

var workingTags = {};
var workingUserTagGroups = {};

/* a map of tags that the current user has used in the past */
var tags = {};

/* 
 * a map of user-defined class tags 
 * data structure: 
 *   {'tag-group-name':['member tag 1','member tag 2',..., 'member tag N']}
 */
var userTagGroups = {};

/* tags used to label the current bookmark*/
var bookmarkTags = new Array();

var users = {};

/* a Google Map object */
var gmap = null;
var gmapGeocoder = null;
var gmapMrkMgr = null;

/* 
 * A map of PointMakers associated with this bookmark.
 * Keys are PointMarker Id and values are PointMarker objects in JSON.
 */
var pointMarkers = {};
var gmapMarkers = new Array();

var newPlacemarkCount = 1;

function getNewPlacemarkId(){
	return (newPlacemarkCount++ * -1);
}

/* initialize user tags */
function loadUserTags(t){
	tags = t;
}

/* initialize user tag groups */
function loadUserTagGroups(g){
	userTagGroups = g;
}

function loadUserList(u){
	users = u;
}

/* initialize 'folder:' machine tags of all the folders of this user */
function loadFolderTags(t){
	for(var ftag in t){
		var f_machine_tag = toFolderMachineTag(ftag);
		folderTags[f_machine_tag]=t[ftag];		
	}
}

function loadPointMarkers(p){
	for(var i = 0; i < p.length; i++){
		var id = p[i].id;
		pointMarkers['pm_'+id] = p[i];
	}
}

function taggle(tag){
	doReadTagline();
	doSwapTag(tag);
	doChangeTagColor(tag);
	doWriteTagline();
}

function doUncolorAllTags(){
	var tagLinks = MochiKit.DOM.getElementsByTagAndClassName('A','selected');
	for(var i = 0; i < tagLinks.length; i++){
		MochiKit.DOM.removeElementClass(tagLinks[i],'selected');
	}
}

function doChangeTagColor(tag){
	for(var tagListId in tagIdPrefixMap){	
		var tid = tagIdPrefixMap[tagListId] + tag.escapeHTML();		
		var tagHref = MochiKit.DOM.getElement(tid);		
		if(tagHref){
			if(MochiKit.DOM.hasElementClass(tagHref,'selected') == false){			
				MochiKit.DOM.addElementClass(tagHref,'selected');			
			}else{			
				MochiKit.DOM.removeElementClass(tagHref,'selected');
			}	
		}
	}
}

function doReadTagline(){
	// reads existing bookmark tags into bookmarkTags
	var tagsInput = getElement(tagsInputFieldId)
	// removes the leading and trailing white spaces
	var tagline = strip(tagsInput.value);	
	var ltags = tagline.split(/\s+/);
	bookmarkTags = new Array();			
	for(var i = 0; i < ltags.length; i++){
		if(getInCurrentBookmarkTagsPos(ltags[i]) == -1){
			bookmarkTags.push(ltags[i]);
		}
	}
}

function doWriteTagline(){	
	var tagsInput = MochiKit.DOM.getElement(tagsInputFieldId);	
	var tagline = toTagline(bookmarkTags);
	tagsInput.value = tagline;
}

function doSwapTag(tag){		
	var curPos = getInCurrentBookmarkTagsPos(tag);
	if(curPos == -1){
		bookmarkTags.push(tag);
	}else{
		bookmarkTags.splice(curPos,1);
	}	
}

function getInCurrentBookmarkTagsPos(taggledTag){
	for(var i = 0; i < bookmarkTags.length; i++){		
		if(taggledTag == bookmarkTags[i]){						
			return i;
		}
	}
	return -1;
}

function createTagSelectionBlock(tagData, tagListId, tagIdPrefix){		
	workingTags = tagData;	
	
	MochiKit.Visual.appear(userTagsId,{		
		beforeStart:function(){
			
	var tagListElm = MochiKit.DOM.getElement(tagListId);			
	tagListElm.innerHTML = '';
	for(var t in tagData){		
		var tagId = tagIdPrefix + t;
		var listElm = LI(null);
		var unescaped_t = t.unescapeHTML();
		var tagLink = A({'id':tagId,'href':'javascript:taggle(\''+unescaped_t+'\')'});
		var tagCount = tagData[t];		
		MochiKit.DOM.addElementClass(tagLink,getTagPopularityStyle(tagCount) + " a"+tagCount);
		tagLink.innerHTML = t;				
		MochiKit.DOM.appendChildNodes(listElm,tagLink);
		MochiKit.DOM.appendChildNodes(tagListElm,listElm,'\n');			
	}	
	}});
}

function initializePage(){		
	//MochiKit.LoggingPane.createLoggingPane(true);
	setMenuHref();
	createUserSelection(users);
	monitorUserSelectionChanges();
	createTagGroupSelection(userTagGroups);	
	monitorTagGroupSelectionChanges();
	createTagSelectionBlock(tags,userTagsId,tagIdPrefixMap.userTagsId);
	colorSelectedTags();
	monitorTaglineChanges();	
	setOpenUrlWindowOnClickEvent();
	setEditToolsOnClickEvent();
	monitorZoomInputSubmit();
}

/**
 * Sets the 'onclick' attribute of the 'open url window' button
 */
function setOpenUrlWindowOnClickEvent(){
	var openUrlWindowElm = MochiKit.DOM.getElement(openUrlWindowId);
	MochiKit.DOM.setNodeAttribute(openUrlWindowElm,'onclick','showPagePreview()');
}

function setEditToolsOnClickEvent() {
	var addTagsElm = MochiKit.DOM.getElement(edToolsAddTagsId);
	var addPlcMrksElm = MochiKit.DOM.getElement(edToolsAddPlcMrkId);
	MochiKit.DOM.setNodeAttribute(addTagsElm,'href','javascript:showAddTagsPane();');
	MochiKit.DOM.setNodeAttribute(addPlcMrksElm,'href','javascript:showAddPlacemarksPane()');
	
}

function showAddTagsPane(){
	MochiKit.DOM.addElementClass(editPlcMrkDIVId,'invisible');
	MochiKit.DOM.removeElementClass(selectUserTagDIVId,'invisible');
}

function showAddPlacemarksPane(){
	MochiKit.DOM.addElementClass(selectUserTagDIVId,'invisible');
	MochiKit.DOM.removeElementClass(editPlcMrkDIVId,'invisible');
	if(gmap == null){
		initMap();
	}
}

function initMap(){
	if (GBrowserIsCompatible()) {
        gmap = new GMap2(MochiKit.DOM.getElement(mapId));       
        gmap.addControl(new GLargeMapControl());
        gmap.addControl(new GMapTypeControl());
        gmap.enableContinuousZoom();
		gmap.enableDoubleClickZoom();	
  	    gmap.setCenter(new GLatLng(38.95,77.46),1,G_HYBRID_MAP);
  	    gmapMrkMgr = new MarkerManager(gmap,{trackMarkers:true, maxZoom:18});  	   
        for(var k in pointMarkers){
        	var pm = pointMarkers[k];
        	var v = pm.point.split(",");
        	var gLatLng = new GLatLng(v[1],v[0]);
        	pointMarkers[k].gmapMakerIdx = createGmapPointMarker(pm,gLatLng);
        }       	   
       fitGmapToData();
    }
}

function fitGmapToData() {
	if(gmapMarkers.length > 0){
		var bounds = new GLatLngBounds();
		for(var i = 0; i < gmapMarkers.length; i++){
			var latlng = gmapMarkers[i].getLatLng();
			bounds.extend(latlng);
		}
		gmap.setZoom(7);
		gmap.setCenter(bounds.getCenter());
	}
}

function findPlaceAndZoom(){
	var zoomToInputElm = MochiKit.DOM.getElement(zoomToInputId);
	var placename = zoomToInputElm.value;
	if(MochiKit.Base.isUndefinedOrNull(placename) == false){
		if(gmapGeocoder == null){
			gmapGeocoder = new GClientGeocoder();	
		}
		gmapGeocoder.getLatLng(
			placename,
			function(point){
				if(MochiKit.Base.isUndefinedOrNull(point) == true){
					alert(placename + ' not found.');
				}else{
					gmap.setCenter(point,13);					
				}	
			});
	}
}


function createUserSelection(userStats){
	var userSlctElm = MochiKit.DOM.getElement(userSelectionId);	
	// save a copy of the header '-- tag group --'
	var slctHeaderElm = userSlctElm[0].cloneNode(true);
	// clears all current selection OPTION
	userSlctElm.innerHTML = '';
	MochiKit.DOM.appendChildNodes(userSlctElm,slctHeaderElm);
	for(var uname in userStats){
		if(uname.length > 0 && userStats[uname] > 0){
			var userOptElm = MochiKit.DOM.OPTION({'value':uname,'class':'select-option'},uname);
			MochiKit.DOM.appendChildNodes(userSlctElm,userOptElm);
		}
	}	
}

function setMenuHref(){
	MochiKit.DOM.setNodeAttribute(loadYourTagsId,'href','javascript:fetchTags(\'yourtags\')');	
	MochiKit.DOM.setNodeAttribute(loadRecommendedTagsId,'href','javascript:fetchTags(\'rcmdtags\')');
	MochiKit.DOM.setNodeAttribute(selectTags5Id,'href','javascript:selectTagCloud(5)');
	MochiKit.DOM.setNodeAttribute(selectTags10Id,'href','javascript:selectTagCloud(10)');
	MochiKit.DOM.setNodeAttribute(selectTagsNoneId,'href','javascript:unselectTagCloud()');
	//MochiKit.DOM.setNodeAttribute(clearTaglineId,'href','javascript:clearTagline()');
	MochiKit.DOM.setNodeAttribute(addPlacemarkId,'href','javascript:addNewPlacemark()');
	var submitElms = MochiKit.DOM.getElementsByTagAndClassName('INPUT',saveSubmitClass,saveBookmarkFormId);
	for(var i = 0; i < submitElms.length; i++){
		MochiKit.Logging.log('set onclick writeGeometryMarkers() on saveSubmit: ' + submitElms[i].value);
		MochiKit.DOM.setNodeAttribute(submitElms[i],'onclick',
		   'writeGeometryMarkers();setSubmitInputSource(\''+submitElms[i].id+'\');');		
	}	
	MochiKit.DOM.setNodeAttribute(zoomToId,'onclick','findPlaceAndZoom()');
	MochiKit.DOM.setNodeAttribute(geonamesMTHelperId,'href','javascript:addGeonamesMT()');
	MochiKit.DOM.setNodeAttribute(forUserMTHelperId,'href','javascript:addForUserMT()');
	MochiKit.DOM.setNodeAttribute(subscribeMTHelperId,'href','javascript:addSubscribeMT()');
	MochiKit.DOM.setNodeAttribute(folderMTHelperId,'href','javascript:addFolderMT()');
}

/* ========  BEGIN: Machine Tag Helper Functions ========== */

function addGeonamesMT(){
   var inputTag = prompt("Enter a place name", "");
   var regexp = /[\\&\?\/:]/;
   if(MochiKit.Base.isUndefinedOrNull(inputTag) == false){
      if(inputTag.length == 0){
          alert("Place name can't be an empty string!");
      }else if(regexp.test(inputTag) == true){
          alert("Place name must not contain these special characters: & ? / \\ :");
      }else{
          var safeInputTag = inputTag.trim()
          safeInputTag = safeInputTag.replace(/\s+/g,'_');
          addMachineTagToTagline('gn','geonames',safeInputTag);      
      }
   }
}

function addFolderMT(){
   var inputTag = prompt("Save this bookmark to folder", "");
   var regexp = /[\\&\?\/:;%^+_*'"]/;
   if(MochiKit.Base.isUndefinedOrNull(inputTag) == false){
      if(inputTag.length == 0){
          alert("Folder name can't be an empty string!");
      }else if(regexp.test(inputTag) == true){
          alert("Folder name must not contain these special characters: * ? & / \\ ; ' \" * % ^ + _");
      }else{
          var safeInputTag = inputTag.trim()
          safeInputTag = safeInputTag.replace(/\s+/g,'_');
          addMachineTagToTagline('gn','folder',safeInputTag);      
      }
   }
}

function addForUserMT(){
   var inputTag = prompt("Recommend this bookmark to user", "");
   var regexp = /[\W]/;
   if(MochiKit.Base.isUndefinedOrNull(inputTag) == false){
      if(inputTag.length == 0){
          alert("User name can't be an empty string!");
      }else if(regexp.test(inputTag) == true){
          alert("Invalid user name!");
      }else{
          var safeInputTag = inputTag.trim()
          safeInputTag = safeInputTag.replace(/\s+/g,'');
          addMachineTagToTagline('gn','for',safeInputTag);      
      }
   }
}

function addSubscribeMT(){
   var okay = confirm("If this is an RSS feed, subscribe to it?");
   if(okay == true){
      addMachineTagToTagline('gn','subscribe','this');      
   }
}

function addMachineTagToTagline(ns,pred,value){
  var tagsInputElm = MochiKit.DOM.getElement(tagsInputFieldId);
  var tagline = tagsInputElm.value;
  var mtFull = '';
  var mtShrt = '';
  if(ns != null){
    mtFull = ns + ':' + pred + '=' + value;
  }
  mtShrt = pred + ':' + value;  
  var exists = false;
  for(var i = 0; i < bookmarkTags.length; i++){
      if(bookmarkTags[i] == mtFull ||bookmarkTags[i] == mtShrt){
          exists = true;
          break;
      }      
  }
  if(exists == false){     
      var spc = '';
      if(tagline.length > 0){
          spc = ' ';
      }
      tagsInputElm.value = tagline + spc + mtShrt;
      colorSelectedTags();
  }
}
/* ========  END: Machine Tag Helper Functions ========== */

function setSubmitInputSource(srcId){
    submitSrcButtonId = srcId;
}

function writeGeometryMarkers(){
	var inputElm = MochiKit.DOM.getElementsByTagAndClassName('INPUT',geomMarkerClass,saveBookmarkFormId);
	for(var i = 0; i < inputElm.length; i++){
		MochiKit.DOM.removeElement(inputElm[i]);		
	} 
	for(var k in pointMarkers){
		var json = MochiKit.Base.serializeJSON(pointMarkers[k]);
		MochiKit.Logging.log(json);
		var inputPtMrkerElm = MochiKit.DOM.INPUT({'type':'hidden','name':'pointMarkers','value':json});
		MochiKit.DOM.appendChildNodes(saveBookmarkFormId,inputPtMrkerElm);	
	}
}

/* until we find a better place to put this control in the HTML page */
/*
function clearTagline(){
	var taglineElm = MochiKit.DOM.getElement(bmarkTagsInputFieldId);
	taglineElm.value = '';
	colorSelectedTags();
}
*/

function unselectTagCloud(){
	var tagline = new Array();
	doReadTagline();
	for(var i = 0 ; i < bookmarkTags.length; i++){
		var t = bookmarkTags[i];
		if(MochiKit.Base.isUndefinedOrNull(workingTags[t]) == true){
			tagline = MochiKit.Base.concat(tagline,[t]);
		}
	}
	bookmarkTags = tagline;
	doWriteTagline();
	colorSelectedTags();
}

function selectTagCloud(number){
	var tags2add = getSortedWorkingTagsByFreq().slice(0,number-1);
	doReadTagline();
	for(var i = 0; i < tags2add.length; i++){
		var idx = MochiKit.Base.findValue(bookmarkTags,tags2add[i],0);
		if(idx == -1){
			bookmarkTags.unshift(tags2add[i]);
		}
	}
	doWriteTagline();
	colorSelectedTags();
}

function getSortedWorkingTagsByFreq(){
	var keys = MochiKit.Base.keys(workingTags);
	var sortByFreq = function (t1, t2){
		var t1Freq = workingTags[t1];
		var t2Freq = workingTags[t2];
		return t2Freq - t1Freq;
	};
	return keys.sort(sortByFreq);
}


function fetchTags(category){
	if(category == 'yourtags'){		
		MochiKit.Visual.fade(userTagsId,{afterFinish:function(){			
		createUserSelection(users);
		createTagGroupSelection(userTagGroups);	
		createTagSelectionBlock(tags,userTagsId,tagIdPrefixMap.userTagsId);
		colorSelectedTags();
		}});
	}else if(category == 'rcmdtags'){
		MochiKit.Visual.fade(userTagsId,{afterFinish:function(){
		if(getRecommendedTagsUrl != null){
			doLoadRecommendedTags();
		}
		}});
	}else{
		alert('fetch tags from: ' + category);
	}
}

function doLoadRecommendedTags(){
	var url = MochiKit.DOM.getElement(bmarkUrlInputFieldId).value;
	if(url != null && url.length > 0){		
		var qs = MochiKit.Base.queryString(['url'],[url])		
		var d = MochiKit.Async.loadJSONDoc(getRecommendedTagsUrl+'?'+qs);
		var gotData = function (rcmdTags) {
			if(MochiKit.Base.keys(rcmdTags).length > 0){
				createTagGroupSelection({});	
	  			createTagSelectionBlock(rcmdTags,userTagsId,tagIdPrefixMap.userTagsId);
				colorSelectedTags();
			}else{
				alert('No recommended tags for this URL at this time.');
				MochiKit.Visual.appear(userTagsId);
			}			
		};
		var dataFetchFailed = function (err) {
		  alert('Error: ' + err);
		};
		d.addCallbacks(gotData, dataFetchFailed);			
	}else{
		alert('Please specify the URL of your bookmark!');
		MochiKit.Visual.appear(userTagsId);
	}	
}

function doLoadUserTagCloud(username){
	var qs = MochiKit.Base.queryString(['username'],[username]);
	var d = MochiKit.Async.loadJSONDoc(getUserTagsUrl+'?'+qs);
	var gotData = function(userTagData){		
		createTagSelectionBlock(userTagData,userTagsId,tagIdPrefixMap.userTagsId);
		colorSelectedTags();
	};
	var dataFetchFailed = function(err){
		alert('Error: ' + err);
	}
	d.addCallbacks(gotData,dataFetchFailed);
}

function doLoadUserTagGroup(username){
	var qs = MochiKit.Base.queryString(['username'],[username]);
	var d = MochiKit.Async.loadJSONDoc(getUserTagGroupUrl+'?'+qs);
	var gotData = function(userTagGroupData){		
		createTagGroupSelection(userTagGroupData);
	};
	var dataFetchFailed = function(err){
		alert('Error: ' + err);
	}
	d.addCallbacks(gotData,dataFetchFailed);
}

function createTagGroupSelection(tagGroups){
	workingUserTagGroups = tagGroups;
	
	var tagGrpSlctElm = MochiKit.DOM.getElement(tagGroupSelectionId);
	// save a copy of the header '-- tag group --'
	var slctHeaderElm = tagGrpSlctElm[0].cloneNode(true);
	// clears all current selection OPTION
	tagGrpSlctElm.innerHTML = '';
	MochiKit.DOM.appendChildNodes(tagGrpSlctElm,slctHeaderElm);
	for(var tGrp in tagGroups){
		var tGrpOptElm = MochiKit.DOM.OPTION({'value':tGrp,'class':'select-option'},tGrp);
		MochiKit.DOM.appendChildNodes(tagGrpSlctElm,tGrpOptElm);
	}	
}

function keepOnlyTagsInTagGroupSet(tagHash,tagGroupSet){
	var resultHash = {};
	for(var i = 0; i < tagGroupSet.length; i++){
		var tag = tagGroupSet[i];	
		if(MochiKit.Base.isUndefinedOrNull(tagHash[tag]) == false){
			resultHash[tag] = tagHash[tag];			
		}else{
			resultHash[tag] = 0;
		}
	}
	return resultHash;
}


function colorSelectedTags(){
	doUncolorAllTags();
	doReadTagline();
	for(var i = 0; i < bookmarkTags.length; i++){		
		doChangeTagColor(bookmarkTags[i]);
	}
}

function monitorZoomInputSubmit(){
	var changed = function(e){		
		if(e.key().code == 13){
			findPlaceAndZoom();
		}
	}
	MochiKit.Signal.connect(zoomToInputId,'onkeydown',changed);
}

function removeSpecialCharInTagline(){
    var tagline = getTaglineString();
    tagline = tagline.replace(/[\t\r\n\f]+/g,'');
    var tagInputElm = MochiKit.DOM.getElement(tagsInputFieldId);
    tagInputElm.value = tagline;
}

function monitorTaglineChanges(){
	var changed1 = function(e){
	    //removeSpecialCharInTagline();
		colorSelectedTags();
	};
	var changed2 = function(e){
	    if(e.key().code != 32 && e.key().code != 9 && 
	       e.key().code != 13){
	        var caretPos = getSelectionStart(e.src());
	        MochiKit.Logging.log("caret start: " + caretPos);
       	    if(suggestTagsToComplete(caretPos) == false){
       	        clearSuggestedTags();
       	    }
	    }else{
	        MochiKit.Logging.log('SPACE-like key pressed: ' + e.key().string);
	        clearSuggestedTags();
	    }
	}
	var changed3 = function(e){
	    var caretPos = getSelectionStart(e.src());
	    MochiKit.Logging.log("caret start: " + caretPos);
       	if(suggestTagsToComplete(caretPos) == false){
       	   clearSuggestedTags();
        }
	}
	MochiKit.Signal.connect(tagsInputFieldId,'onkeyup',changed1);
	if(BO.ie == false){
  	   MochiKit.Signal.connect(tagsInputFieldId,'onkeyup',changed2);
  	   MochiKit.Signal.connect(tagsInputFieldId,'onmouseup',changed3);
	}else{
  	   MochiKit.Logging.log('IE detected. Will not load auto-complete function');
	}	
}

function monitorTagGroupSelectionChanges(){
	var drawTagList = function(e){
		var tagGrpSlctElm = e.src();
		var i = tagGrpSlctElm.selectedIndex;
		var tagGrpName = tagGrpSlctElm.options[i].value;	
		if(workingUserTagGroups[tagGrpName] != null){
			var tags2draw = keepOnlyTagsInTagGroupSet(tags,workingUserTagGroups[tagGrpName]);
			createTagSelectionBlock(tags2draw,userTagsId,tagIdPrefixMap.userTagsId);
			colorSelectedTags();
		}			
	};
	MochiKit.Signal.connect(tagGroupSelectionId,'onchange',drawTagList);	
}

function monitorUserSelectionChanges(){
	var switchTagCloud = function(e){	
		MochiKit.Visual.fade(userTagsId,
		{afterFinish:function(){
			var userSlctElm = e.src();
			var i = userSlctElm.selectedIndex;
			var username = userSlctElm[i].value;
			if(users[username] != null && users[username] > 0){
				doLoadUserTagCloud(username);
				doLoadUserTagGroup(username);
			}	
		}
		});		
	}
	MochiKit.Signal.connect(userSelectionId,'onchange',switchTagCloud);
}

function resetTagCloud(){
	createTagGroupSelection(userTagGroups);
	createTagSelectionBlock(tags,userTagsId,tagIdPrefixMap.userTagsId);
}

function showPagePreview(){	
	var urlElm = MochiKit.DOM.getElement('bookmarkUrl');
	if(MochiKit.Base.isUndefinedOrNull(urlElm) == false){
		var url = urlElm.value;		
		if(MochiKit.Base.isUndefinedOrNull(url) == false && isValidUrl(url) == true){
			window.open(url,'','alwaysRaised=yes,height=600,width=800,status=no,toolbar=no,resizable=yes,menubar=no,scrollbars=yes,left=0');
		}else{
			alert('The URL that you\'ve entered is invalid!');
		}
	}
}

function addNewPlacemark(){
	var id = getNewPlacemarkId();
	var curCenterPt = gmap.getCenter();
	var cv = curCenterPt.lng() + ',' + curCenterPt.lat();
	var pm = {'id':id,'notes':'This is a placemark associated with this bookmark.','iconId':0,'point':cv,'gLatLng':curCenterPt};
	MochiKit.Logging.log('->'+MochiKit.Base.serializeJSON(pm));
	pointMarkers['pm_'+id] = pm;
	pointMarkers['pm_'+id].gmapMakerIdx = createGmapPointMarker(pm,curCenterPt);	
}

function createGmapPointMarker(p,gLatLng){
	var markerD = new GMarker(gLatLng, {icon:G_DEFAULT_ICON, draggable: true}); 
	markerD.pmId = p.id;	
	gmapMarkers.push(markerD);
	//gmap.addOverlay(markerD);
	gmapMrkMgr.addMarker(markerD,0);
	markerD.enableDragging();
	GEvent.addListener(markerD, 'drag', 
		function(){
			var cLatLng = this.getPoint();
			var cv = cLatLng.lng() + ',' + cLatLng.lat();
			var ptmrkId = this.pmId;
			pointMarkers['pm_'+ptmrkId].point = cv;			
			MochiKit.Logging.log('id: ' + ptmrkId + ' has new location: ' + cv);
		}
	);
	markerD.bindInfoWindowHtml(createViewNotesElm(p.id));
	markerD.openInfoWindowHtml(
	  MochiKit.DOM.DIV({'class':'editMarkerNotesPopup'},
	    MochiKit.DOM.P(null,'This is a new placemark. Drag it to a desired location.')
	  ));
	return gmapMarkers.length-1;
}

function createViewNotesElm(pmId){
	var notes = pointMarkers['pm_'+pmId].notes;
	var notesElm = MochiKit.DOM.P({'id':'mn_'+pmId},notes);
	var editNotesElm = MochiKit.DOM.A({'class':'system-link align-right','href':'javascript:editNotes('+pmId+')'},'edit notes');
	var delMarkerElm = MochiKit.DOM.A({'class':'system-link align-right','href':'javascript:delMarker('+pmId+')'},'remove from the map')
	var contentElm = MochiKit.DOM.DIV({'id':'ed_mn_'+pmId,'class':'editMarkerNotesPopup'},
	   notesElm, editNotesElm, MochiKit.DOM.BR(null),delMarkerElm);
	return contentElm;
}

function editNotes(pmId){
	var pm = pointMarkers['pm_'+pmId];
	var marker = gmapMarkers[pm.gmapMakerIdx];
	var editAreaElm = MochiKit.DOM.TEXTAREA({'id':'mn_ta_'+pmId,'class':'textInput'},pm.notes);
	var doneEditElm = MochiKit.DOM.A({'class':'system-link align-right','href':'javascript:doneEditNotes('+pmId+')'},'done');
	var contentElm = MochiKit.DOM.DIV({'id':'ed_mn_'+pmId,'class':'editMarkerNotesPopup'},editAreaElm,MochiKit.DOM.BR(null),doneEditElm);	
	marker.openInfoWindowHtml(contentElm);
}

function doneEditNotes(pmId){
	var pm = pointMarkers['pm_'+pmId];
	var marker = gmapMarkers[pm.gmapMakerIdx];
	var editAreaElm = MochiKit.DOM.getElement('mn_ta_'+pmId);
	var newNotes = editAreaElm.value;
	pm.notes = newNotes;
	var contentElm = createViewNotesElm(pmId);
	marker.openInfoWindowHtml(contentElm);
}

function delMarker(pmId){
	var answer = confirm('Remove this placemark from the map. Yes?');
	if(answer == true){
		var pm = pointMarkers['pm_'+pmId];
		var idx = pm.gmapMakerIdx;
		//gmap.removeOverlay(gmapMarkers[idx]);	
		gmapMrkMgr.removeMarker(gmapMarkers[idx]);
		delete pointMarkers['pm_'+pmId];
	}
}

function isValidUrl(s){
	var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
	return regexp.test(s);
}

function saveAndClose(flag){
  MochiKit.Logging.log('closeAndSave = ' + flag);
     if(Boolean(flag) == true){
         if(submitSrcButtonId == submitSaveBookmarkId){        
             window.close();
         }
     }
}


var focusedTag = '';
var focusedTagCaretPos = -1;
/* ========  BEGIN: Suggest Tags for Auto-Complete Functions ========== */
function getLastNonWord(s){
   var lastLnBrkPos = s.lastIndexOf('\n');
   var lastSpcPos = s.lastIndexOf(' ');
   if(lastLnBrkPos > lastSpcPos){
       return lastLnBrkPos;
   }
   return lastSpcPos;
}

function getFirstNonWord(s){
   var frstLnBrkPos = s.indexOf('\n');
   var frstSpcPos = s.indexOf(' ');
   if(frstLnBrkPos < frstSpcPos){
       return frstLnBrkPos;
   }
   return frstSpcPos;
}

function suggestTagsToComplete(curPos){      
   var taglns = getTaglineString();
   MochiKit.Logging.log('curPos: ' + curPos + ' char:' + taglns[curPos]);
   var lPart = taglns.substring(0,curPos);
   var rPart = taglns.substring(curPos,taglns.length);
   MochiKit.Logging.log('lPart: ->'+lPart + '<- rPart: ->'+rPart + '<-');  
   var lastSpcOnLeftIdx = getLastNonWord(lPart);
   var frstSpcOnRightIdx = getFirstNonWord(rPart);
   MochiKit.Logging.log('lastSpcOnLeftIdx: ' + lastSpcOnLeftIdx + ' frstSpcOnRightIdx: ' + frstSpcOnRightIdx);
   if(lastSpcOnLeftIdx < 0){
       focusedTag = lPart;
   }else{
       focusedTag = lPart.substring(lastSpcOnLeftIdx+1,curPos);
   }
   if(focusedTag.length > 0){
       if(frstSpcOnRightIdx < 0){
          focusedTag = focusedTag + rPart;  
       }else{
          focusedTag = focusedTag + rPart.substring(0,frstSpcOnRightIdx);
       }
   }
   MochiKit.Logging.log('focusedTag: ->' + focusedTag+'<-');
   if(focusedTag.length > 0){
      var matched = getPartiallyMatchedTags(focusedTag,7);
      //MochiKit.Logging.log('auto-compete candidates: ' + matched);
      var matchedTags = MochiKit.Base.map(function(t){
         return focusedTag + t;
      },matched);
      MochiKit.Logging.log('Suggestion: ' + matchedTags);
      if(matchedTags.length > 0){
           doWriteSuggestedTags(matchedTags);
           focusedTagCaretPos = curPos;
           return true;
      }else{
          focusedTagCaretPos = -1;
      }     
   }
   return false;
}

function getTaglineString() {
   var tagInputElm = MochiKit.DOM.getElement(tagsInputFieldId);
   var s = tagInputElm.value;   
   if(MochiKit.Base.isUndefinedOrNull(s) == false){
       return s;
   }else{
       return '';
   }
}

function getParsedTagline(){
   var tagline = getTaglineString();
   var ltags = new Array();
   if(tagline.length > 0){  
     ltags = tagline.split(/\s+/);    
     MochiKit.Logging.log("parsed tagline: " + ltags);
   }
   return ltags;
}

function doWriteSuggestedTags(tags2suggest){
    var suggestTagsElm = MochiKit.DOM.getElement(suggestedTagsDIVId);
    MochiKit.DOM.replaceChildNodes(suggestTagsElm,
      MochiKit.DOM.SPAN({'class':'tipTitle'},'Suggestion: ')
    );
    for(var i = 0; i < tags2suggest.length; i++){
        var sep = ', ';
        if(i == (tags2suggest.length-1)){
            sep = ' ';
        }
        var t_unescaped = tags2suggest[i].unescapeHTML();
        MochiKit.DOM.appendChildNodes(suggestTagsElm,
          MochiKit.DOM.A({'href':'javascript:autoComplete(\''+t_unescaped+'\')'},tags2suggest[i]),sep);
    }
}

function autoComplete(t2c){
    MochiKit.Logging.log('auto-complete focusedTag: ' + focusedTag + ' using ->'+t2c+'<-');
    var tagline = getTaglineString();
    var lPart = tagline.substring(0,focusedTagCaretPos);  
    var lastSpcIdxOnLeft = getLastNonWord(lPart);
    MochiKit.Logging.log('lastSpcIdxOnLeft: ' + lastSpcIdxOnLeft);
    lPart = lPart.substring(0,lastSpcIdxOnLeft+1);
    var rPart = tagline.substring(focusedTagCaretPos,tagline.length);     
    var frstSpcIdxOnRght = getFirstNonWord(rPart);
    MochiKit.Logging.log('frstSpcIdxOnRgh: ' + frstSpcIdxOnRght);
    if(frstSpcIdxOnRght >= 0){
       rPart = rPart.substring(frstSpcIdxOnRght,rPart.length);          
    }else{
       rPart = '';
    }
    MochiKit.Logging.log('lPart: ->' + lPart + '<-');
    MochiKit.Logging.log('rPart: ->' + rPart + '<-');
    var newTagline = lPart + t2c + rPart;
    MochiKit.Logging.log('insert auto-complete tag into tagline: ' + newTagline);
    var tagInputElm = MochiKit.DOM.getElement(tagsInputFieldId);
    tagInputElm.value = newTagline + ' ';
    colorSelectedTags();
    clearSuggestedTags();
}

function clearSuggestedTags(){
    MochiKit.DOM.replaceChildNodes(suggestedTagsDIVId,'');
}

function existsInCurrentTagline(t){
    for(var i = 0; i < bookmarkTags.length; i++){
        if(bookmarkTags[i] == t){
            return true;
        }
    }
    return false;
}

function getPartiallyMatchedTags(ps,maxCount){
    var matchedTags = new Array();   
    var cnt = 0;
    var candidateTags = MochiKit.Base.keys(tags);
    if(MochiKit.Base.isUndefinedOrNull(ps) == false){
        var encodedPS = ps.escapeHTML();
        MochiKit.Logging.log('encodedPS = ->' + encodedPS+'<-');
        var re = new RegExp("^"+encodedPS+"(.*)");
        MochiKit.Logging.log('re: ' + re + ' and candidateTags.length = ' + candidateTags.length);
        for(var i = 0; i < candidateTags.length && cnt < maxCount; i++){          
            var match = re.exec(candidateTags[i]); 
            if(MochiKit.Base.isNull(match) == false){
               if(existsInCurrentTagline(candidateTags[i].unescapeHTML()) == false){             
                  matchedTags.push(match[1].unescapeHTML());
                  cnt++;
               }
            }
        }
    }
    return matchedTags;
}
/* ========  END: Suggest Tags for Auto-Complete Functions ========== */

MochiKit.Signal.connect(window,'onload',initializePage);
