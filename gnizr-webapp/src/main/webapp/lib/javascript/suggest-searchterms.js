var topDIVId = 'searchPageTop';
var boxDIVId = 'searchTermSuggest';
var listDIVId = 'suggestedSearchTerms';
var controlDIVId = 'searchTermSuggestControl';

var searchUrl = '/';
var fetchSuggestionUrl = null;

SuggestSearchTerms = {
    relatedTags: null,     
    init: function(){
        //MochiKit.LoggingPane.createLoggingPane(true);
        if(MochiKit.Base.isUndefinedOrNull(fetchSuggestionUrl) == false){
            MochiKit.Logging.log('SuggestSearchTerms: fetch JSON: ' + fetchSuggestionUrl);
            var d = MochiKit.Async.loadJSONDoc(fetchSuggestionUrl);
            var gotData = function(data){
                if(MochiKit.Base.isUndefinedOrNull(data) == false){
                    var t = data['related'];
                    if(MochiKit.Base.isUndefinedOrNull(t) == false && t.length > 0){
                        SuggestSearchTerms.relatedTags = t;
                        MochiKit.Logging.log('SuggestSearchTerms: related tags: ' + SuggestSearchTerms.relatedTags);
                    }
                }
                if(SuggestSearchTerms.relatedTags != null){
                    SuggestSearchTerms.createSuggestionBox();
                }
            };
            var gotDataFailed = function(err){
                MochiKit.Logging.log('ERROR: SuggestSearchTerms: fetch JSON doc failed: ' + fetchSuggestionUrl,err);
            }; 
            d.addCallbacks(gotData,gotDataFailed);
        }
    },
    
    boxElm : null,
    cntrlLinkElm: MochiKit.DOM.A({'href':'javascript:showSearchSuggestion()','class':'system-link'},'show'),
    createSuggestionBox: function(){
        MochiKit.Logging.log('SuggestSearchTerms: createSuggestionBox: called');
        SuggestSearchTerms.showCntrlElm = SuggestSearchTerms.boxElm = MochiKit.DOM.DIV(
          {'id':boxDIVId},
          MochiKit.DOM.DIV({'id':controlDIVId},'search suggestions: ', 
                           SuggestSearchTerms.cntrlLinkElm));
        MochiKit.DOM.appendChildNodes(topDIVId,SuggestSearchTerms.boxElm);
    },
    
    listElm : null,
    showSuggestion: function(){
        if(SuggestSearchTerms.listElm == null){
            SuggestSearchTerms.listElm = MochiKit.DOM.UL(null);
            if(SuggestSearchTerms.relatedTags != null){
                MochiKit.DOM.appendChildNodes(SuggestSearchTerms.listElm,
                 MochiKit.DOM.LI(null,'Related terms',
                 SuggestSearchTerms.createListData(SuggestSearchTerms.relatedTags)));
            }
        }
        MochiKit.DOM.appendChildNodes(SuggestSearchTerms.boxElm,SuggestSearchTerms.listElm); 
        MochiKit.DOM.setNodeAttribute(SuggestSearchTerms.cntrlLinkElm,'href','javascript:hideSearchSuggestion()');
        MochiKit.DOM.replaceChildNodes(SuggestSearchTerms.cntrlLinkElm,'hide');        
    },
    hideSuggestion: function(){
        MochiKit.DOM.removeElement(SuggestSearchTerms.listElm);
        MochiKit.DOM.setNodeAttribute(SuggestSearchTerms.cntrlLinkElm,'href','javascript:showSearchSuggestion()');
        MochiKit.DOM.replaceChildNodes(SuggestSearchTerms.cntrlLinkElm,'show');      
    },
    createListData: function(tags){
        var listElm = MochiKit.DOM.UL(null);
        for(var i = 0; i < tags.length; i++){
            var tUrl = searchUrl + '?' + MochiKit.Base.queryString({'q':tags[i]});
            MochiKit.DOM.appendChildNodes(listElm,
            MochiKit.DOM.LI(null,
             MochiKit.DOM.A({'href':tUrl},tags[i])));
        }
        return listElm;
    }
}

function showSearchSuggestion(){
    SuggestSearchTerms.showSuggestion();
}

function hideSearchSuggestion(){
    SuggestSearchTerms.hideSuggestion();
}

MochiKit.Signal.connect(window,'onload',SuggestSearchTerms.init);