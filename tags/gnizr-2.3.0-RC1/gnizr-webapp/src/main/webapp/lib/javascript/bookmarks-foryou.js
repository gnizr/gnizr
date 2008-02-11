var bmarkForYouId = "bmarkForYou";
var getForYouCountUrl = "";

function setForYouCountUrl(url){
	getForYouCountUrl = url;
}

function initializePage(){
	var forYouElm = MochiKit.DOM.getElement(bmarkForYouId);	
	if(forYouElm != null && getForYouCountUrl != ""){
		var d = MochiKit.Async.loadJSONDoc(getForYouCountUrl);
		var gotCount = function(totalCnt){
			if(totalCnt >= 0){			
				forYouElm.innerHTML += ' (' + formatTotalCnt(totalCnt) + ')';
			}
		}
		var fetchCountFailed = function(err){
			/*alert('failed to fetch # of bookmarks for you. ' + err);*/
		}
		d.addCallbacks(gotCount,fetchCountFailed);
	}
}

function formatTotalCnt(cnt){
	if(cnt <= 0){
		return 0;
	}else if(cnt > 1000){
		return '1000+';
	}
	return cnt;
}


MochiKit.Signal.connect(window,'onload',initializePage);