var mediaIconMTHelperElmId = 'mediaIconMTHelper';

var predicateIcon = 'icon';

var iconMachineTags = {
	'star'    : 'star-icon-16.png',
	'heart'   : 'heart-icon-16.png',
	'!'       : 'exclaim-icon-16.png',
	'db'      : 'db-icon-16.png',
	'video'   : 'video-icon-16.png',
	'audio'   : 'audio-icon-16.png',
	'photo'   : 'photo-icon-16.png',
}

function initMediaIconPicker(){	
	var pickIconElm = MochiKit.DOM.getElement(mediaIconMTHelperElmId);
	MochiKit.Logging.log('pickIconElm: ' + pickIconElm);
	
	var showIconName = function(e){
		var iconName = this;
		var machineTag = predicateIcon + ':' + iconName;
		MochiKit.Logging.log('changing text: ' + machineTag);		
		var nameFocusdElm = MochiKit.DOM.getElement('media-icon-name-focused');
		MochiKit.Logging.log(nameFocusdElm);
		nameFocusdElm.innerHTML = machineTag;
	}
	
	var addTag = function(e){
		var iconName = this;
		var machineTag = predicateIcon + ':' + iconName;
		
		// defined in edit-bookmark.js
		addMachineTagToTagline('gn',predicateIcon,iconName);
	}
	
	var showMediaIconPicker = function(e){
		var dialogId = 'mediaPickerDialog';
		var iconSlctElm = MochiKit.DOM.UL({'class':'media-icon-list'},null);
		for(tag in iconMachineTags){
			var imgElm = MochiKit.DOM.IMG({'src':imgUrl+iconMachineTags[tag],'alt':tag});
			MochiKit.DOM.appendChildNodes(iconSlctElm,
				MochiKit.DOM.LI({'class':'media-icon'},imgElm));
			MochiKit.Signal.connect(imgElm,'onmouseover',tag,showIconName);	
			MochiKit.Signal.connect(imgElm,'onclick',tag,addTag);
		}			
		var contentDIVElm = MochiKit.DOM.DIV(null, 
	  		//MochiKit.DOM.P(null,'Add a media icon to describe this bookmark.'),
	  		iconSlctElm,
	  		MochiKit.DOM.SPAN({'id':'media-icon-name-focused'},'icon:heart')
		 );
		//openDialog(dialogId,contentDIVElm);
		showMachineTagHelper(contentDIVElm);
	};
	
	MochiKit.Signal.connect(pickIconElm,'onclick',showMediaIconPicker);			
}

MochiKit.Signal.connect(window,'onload',initMediaIconPicker);