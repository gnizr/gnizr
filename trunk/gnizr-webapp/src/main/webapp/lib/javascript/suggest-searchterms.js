var topDIVId = 'searchPageTop';
var boxDIVId = 'searchTermSuggest';
var listDIVId = 'suggestedSearchTerms';
var boxCntTblId = 'searchTermSuggestContent';
var controlDIVId = 'searchTermSuggestControl';

var searchUrl = '/';
var fetchSuggestionUrl = null;

SuggestSearchTerms = {
    relatedTags: null,  
    narrowerTags: null,
    broaderTags: null,   
    init: function(){
        //MochiKit.LoggingPane.createLoggingPane(true);
        if(MochiKit.Base.isUndefinedOrNull(fetchSuggestionUrl) == false){
            MochiKit.Logging.log('SuggestSearchTerms: fetch JSON: ' + fetchSuggestionUrl);
            var d = MochiKit.Async.loadJSONDoc(fetchSuggestionUrl);
            var gotData = function(data){
                if(MochiKit.Base.isUndefinedOrNull(data) == false){
                    var t = data['related'];
                    if(t != null && t.length > 0){
                        SuggestSearchTerms.relatedTags = t;
                        MochiKit.Logging.log('SuggestSearchTerms: related tags: ' + SuggestSearchTerms.relatedTags);
                    }
                    t = data['narrower'];
                    if(t != null && t.length > 0){
                        SuggestSearchTerms.narrowerTags = t;
                        MochiKit.Logging.log('SuggestSearchTerms: narrower tags: ' + SuggestSearchTerms.narrowerTags);
                    }
                    t = data['broader'];
                    if(t != null && t.length > 0){
                        SuggestSearchTerms.broaderTags = t;
                        MochiKit.Logging.log('SuggestSearchTerms: broader tags: ' + SuggestSearchTerms.broaderTags);
                    }
                }
                if(SuggestSearchTerms.relatedTags != null ||
                   SuggestSearchTerms.narrowerTags != null ||
                   SuggestSearchTerms.broaderTags != null){
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
        SuggestSearchTerms.boxElm = MochiKit.DOM.DIV(
          {'id':boxDIVId},
          MochiKit.DOM.DIV({'id':controlDIVId},'search suggestions: ', 
                           SuggestSearchTerms.cntrlLinkElm));
        MochiKit.Style.setStyle(SuggestSearchTerms.boxElm,{'display':'none'});                
        MochiKit.DOM.appendChildNodes(topDIVId,SuggestSearchTerms.boxElm);
        MochiKit.Visual.appear(SuggestSearchTerms.boxElm);
    },  
    contentElm : null,
    showSuggestion: function(){
        if(SuggestSearchTerms.contentElm == null){
            var rowElm = MochiKit.DOM.TR(null);
            SuggestSearchTerms.contentElm = MochiKit.DOM.TABLE({'id':'searchTermSuggestContent'},rowElm);
            if(SuggestSearchTerms.relatedTags != null){
                MochiKit.DOM.appendChildNodes(rowElm,MochiKit.DOM.TD(null,'Related terms',
                SuggestSearchTerms.createListData(SuggestSearchTerms.relatedTags)));
            }
            if(SuggestSearchTerms.narrowerTags != null){
                MochiKit.DOM.appendChildNodes(rowElm,MochiKit.DOM.TD(null,'Narrower terms',
                SuggestSearchTerms.createListData(SuggestSearchTerms.narrowerTags)));
            }
            if(SuggestSearchTerms.broaderTags != null){
                MochiKit.DOM.appendChildNodes(rowElm,MochiKit.DOM.TD(null,'Broader terms',
                SuggestSearchTerms.createListData(SuggestSearchTerms.broaderTags)));
            }
        }
        MochiKit.DOM.appendChildNodes(SuggestSearchTerms.boxElm,SuggestSearchTerms.contentElm); 
        MochiKit.DOM.setNodeAttribute(SuggestSearchTerms.cntrlLinkElm,'href','javascript:hideSearchSuggestion()');
        MochiKit.DOM.replaceChildNodes(SuggestSearchTerms.cntrlLinkElm,'hide');        
    },
    hideSuggestion: function(){
        MochiKit.DOM.removeElement(SuggestSearchTerms.contentElm);
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