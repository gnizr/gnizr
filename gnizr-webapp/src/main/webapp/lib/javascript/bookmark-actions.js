var selectOptions = {'addFolder':'Add to folder:', 
                     'removeFolder':'Remove from folder:'};
var submitOptions = {'delete':'Delete'};                     

/**
 * Global variables that define ID names used in the HTML template page
 */
var selectActionId = 'selectAction';
var checkboxActionId = 'checkboxAction';
var submitActionId = 'submitAction';
var bookmarkActionFormId = 'bookmarkActionForm';
var bookmarkActionId = 'bookmark-action';

var selectBookmarkClass = 'selectBookmark';

/**
 * Global variables that are to be instantied by callbacks functions
 * defined the HEAD tag of the HTML page. 
 */
var enabledSelectOptions = {};
var enabledSubmitOptions = {};
var fldrList = {};

/**
 * A callback function that initializes the list of user folders.
 */
function setUserFoldersData(dataUrl){
	fldrList = dataUrl;
}

/**
 * A callback function that initializes various SELECT and SUBMIT
 * actions. 'action' is one of the keys in selectOptions and
 * submitOptions. 'actionHref' is a URL defined in the HTML
 * page that specifies the end-point URL of the given 'action'.
 */
function enableAction(action,actionHref){	
	if(selectOptions[action]){				
		enabledSelectOptions[action] = actionHref;		
	}else if(submitOptions[action]){
		enabledSubmitOptions[action] = actionHref;
	}
}

/**
 * A global variable to hold a list of folders that the
 * user may select to remove bookmarks from. This list
 * is dynamically populated by reading folders that 
 * a selected set of bookmarks are currently saved in. 
 */
var removalFldrsList = {};

/**
 * A function to be called when windows.onLoad is called.
 */
function initializePage(){
	//MochiKit.LoggingPane.createLoggingPane(true);		
	makeVisibleCheckbox();
	populateCheckboxAction();	
	populateSubmitOption();
	populateSelectOption();		
	monitorSelection();	
}

/**
 * Removes CSS class 'invisible' from bookmark checkboxes, and
 * connects a callback to the 'onclick' event of each checkbox.
 */
function makeVisibleCheckbox(){
	/**
	 * On event 'onclick', check if the selected bookmark 
	 * is saved in one or more folders. If true, read the folder
	 * names and add these names to 'removalFldrsList'.
	 */
    var handleBookmarkCheckboxClicked = function(e){
		var bmarkCheckBoxElm = e.src();
		var bmarkId = getBookmarkId(bmarkCheckBoxElm.id);	
		var folders = getBookmarkInFolders(bmarkId);
		if(bmarkCheckBoxElm.checked == true){		
			addRemovalFolderList(folders);
		}else{		
			subRemovalFolderList(folders);
		}
		populateRemovalFolderOption();
	}	
	var bmarkSelects = MochiKit.DOM.getElementsByTagAndClassName('INPUT',selectBookmarkClass);	
	for(var i = 0; i < bmarkSelects.length; i++){	
		/*
		if(MochiKit.DOM.hasElementClass(bmarkSelects[i],'invisible')){
			MochiKit.DOM.removeElementClass(bmarkSelects[i],'invisible');
		}
		*/
		MochiKit.Signal.connect(bmarkSelects[i],'onclick',handleBookmarkCheckboxClicked);							
	}	
}

/**
 * A utility function that parses bookmark ID from a checkbox id string.
 * Bookmark ID is the primary key of a bookmark record in the Database.
 * Checkbox id string is 'id' attribute of the checkbox in the HTML page.
 */
function getBookmarkId(checkBoxId){
	// string pattern: c_bmark_[id]
	return checkBoxId.substr(2);
}

/**
 * A utility function that returns the name of folders that
 * a given bookmark is currently saved in. The input to this
 * function is the bookmark ID (in DB). 
 * 
 * Pre-condition: folder names are enclosed in HTML 'A' tag that 
 * are defined with a special CSS class attribute. This attribute
 * has a string pattern: 'f_[bookmark_id]'. The 'bookmark_id' is
 * the ID of the bookmark in DB. 
 */
function getBookmarkInFolders(bmarkId){
	var bmark_fldr_cls = 'f_'+bmarkId;
	var bmarkInFldrs = MochiKit.DOM.getElementsByTagAndClassName('A',bmark_fldr_cls,bookmarkActionFormId);	
	var fds = new Array();
	for(var i = 0; i < bmarkInFldrs.length; i++){	
		fds.push(bmarkInFldrs[i].innerHTML);
	}
	return fds;
}

/**
 * A utility function that adds a list of folder names to the 
 * 'removalFldrsList'. Folder names will be only added
 * if they don't already exist in the list.
 */
function addRemovalFolderList(folders){
	for(var i = 0; i < folders.length; i++){
		var f = folders[i];
		if(!removalFldrsList[f]){
			removalFldrsList[f] = 1;			
		}else{
			removalFldrsList[f]++;
		}	
	}
}

/**
 * A utility function that removes a list of folder names
 * from 'removalFldrList'. 
 */
function subRemovalFolderList(folders){
	for(var i = 0; i < folders.length; i++){
		var f = folders[i];
		if(removalFldrsList[f]){
			removalFldrsList[f]--;
		}		
	}
}


/**
 * This function populates the "Removal from folders" option
 * in pull-down menu. It adds folder removal options to the list
 * by reading folder names from 'removalFldrList'
 */
function populateRemovalFolderOption(){
	var slctOptElm = MochiKit.DOM.getElement(selectActionId);
	var c2rm = [];
	for(var i = 0; i < slctOptElm.childNodes.length; i++){
		var v = slctOptElm.childNodes[i].value;
		if(v == 'removeFolder'){
			c2rm.push(slctOptElm.childNodes[i]);
		}
	}	
	for(var i = 0; i < c2rm.length; i++){
		var cidx = c2rm[i];
		slctOptElm.removeChild(cidx);
	}
	var optElm = MochiKit.DOM.OPTION({'value':'removeFolder','disabled':'true'},selectOptions['removeFolder']);			
	MochiKit.DOM.appendChildNodes(selectActionId,optElm);	
	
	for(var folder in removalFldrsList){
		if(removalFldrsList[folder] > 0){					
			optElm = MochiKit.DOM.OPTION({'value':'removeFolder','class':'select-option'},folder);			
			MochiKit.DOM.appendChildNodes(selectActionId,optElm);
		}
	}
}

/**
 * Initializes the pull-down menu options. It disables 
 * the selection on OPTIONs that appear only for presentation purpose
 * (e.g., 'addFolder' and 'removeFolder').
 */
function populateSelectOption(){	
	for(var a in enabledSelectOptions){		
		var actionName = a;
		var actionLabel = selectOptions[a];
		var options = {'value':a};	
		if(a == 'addFolder' || a == 'removeFolder'){
			options['disabled'] = 'true';
		}		
		
		var optElm = MochiKit.DOM.OPTION(options,actionLabel);	
 		var slcActionElm = MochiKit.DOM.getElement(selectActionId);
 		if(slcActionElm != null){
			MochiKit.DOM.appendChildNodes(slcActionElm,optElm);		
			if(a == 'addFolder' && fldrList){	
				for(var fldr in fldrList){		
					var fldrOptElm = MochiKit.DOM.OPTION({'value':a,'class':'select-option'},fldr);		
					MochiKit.DOM.appendChildNodes(selectActionId,fldrOptElm);			
				}	
			}
		}
				
	}
}

/**
 * Initializes submit OPTIONS. 
 */
function populateSubmitOption(){	
	for(var a in enabledSubmitOptions){	
		var actionNameId = 'action_' + a;
		var actionElm = MochiKit.DOM.getElement(actionNameId);			
		if(actionElm != null){
			if(MochiKit.DOM.hasElementClass(actionElm,'invisible')){
				MochiKit.DOM.removeElementClass(actionElm,'invisible');
			}
			if(a == 'delete'){
				var askConfirm = function(e){
					var isConfirmed = confirm('Are you sure you want to delete all selected bookmarks?');
					if(isConfirmed == true){
						setButtonPressed(e);
					}
				};
				MochiKit.Signal.connect(actionElm,'onclick',askConfirm);
			}else{			
				MochiKit.Signal.connect(actionElm,'onclick',setButtonPressed);
			}
		}
	}
}

/**
 * A callback for handle 'onclick' event fired on a SUBMIT button.
 */
function setButtonPressed(e){
	var submitElm = e.src();	
	var key = MochiKit.DOM.getNodeAttribute(submitElm,'name');
	var bmarkActionFormElm = MochiKit.DOM.getElement(bookmarkActionFormId);
	bmarkActionFormElm.action = enabledSubmitOptions[key];	
	bmarkActionFormElm.submit();
}

/**
 * Initializes the 'href' attribute for 'select all' and 'select none' links
 * in the HTML page.
 */
function populateCheckboxAction(){	
	var slctAllElm = MochiKit.DOM.getElement(checkboxActionId+'_selectAll');
	var unslctAllElm = MochiKit.DOM.getElement(checkboxActionId+'_unselectAll');
	MochiKit.DOM.setNodeAttribute(slctAllElm,'href','javascript:selectAllBookmarks()');
	MochiKit.DOM.setNodeAttribute(unslctAllElm,'href','javascript:unselectAllBookmarks()');
}

/**
 * Connects the callback function to handle 'onchange' event that
 * occur in the pull-down menu. When an OPTION is selected, 
 * the FORM is submitted to the defined 'action' end-point.
 */
function monitorSelection(){
	var executeAction = function (e){	
		setFolderOrTagInput(e.src());		
		var actionFormElm = MochiKit.DOM.getElement(bookmarkActionFormId);		
		actionFormElm.submit();	
	}
	var slcActionElm = MochiKit.DOM.getElement(selectActionId);
	if(slcActionElm != null){
		MochiKit.Signal.connect(slcActionElm,'onchange',executeAction);	
	}
}

/**
 * Injects a 'HIDDEN' HTML tag to specify the folder name that
 * the user wishes bookmarks to be saved into or removed from.
 */
function setFolderOrTagInput(slctElm){	
	var idx = slctElm.selectedIndex;
	var optName = slctElm.options[idx].value;
	var optText = slctElm.options[idx].text;
	if(optName == 'addFolder' || optName == 'removeFolder'){
		if(fldrList[optText] != undefined){		
			var fldrInputElm = MochiKit.DOM.INPUT({'type':'hidden','name':'folder','value':optText});	
			MochiKit.DOM.appendChildNodes(bookmarkActionFormId,fldrInputElm);
		}
	}
}

/**
 * Checks all bookmarks
 */
function selectAllBookmarks() {	
	var bmarkSelects = MochiKit.DOM.getElementsByTagAndClassName('INPUT',selectBookmarkClass);
	for(var i = 0; i < bmarkSelects.length; i++){
		if(bmarkSelects[i].checked == false){
			bmarkSelects[i].click();
		}
	}
}

/**
 * Unchecks all bookmarks
 */
function unselectAllBookmarks(){
	var bmarkSelects = MochiKit.DOM.getElementsByTagAndClassName('INPUT',selectBookmarkClass);
	for(var i = 0; i < bmarkSelects.length; i++){
		if(bmarkSelects[i].checked == true){
			bmarkSelects[i].click();
		}
	}
}

MochiKit.Signal.connect(window,'onload',initializePage);

