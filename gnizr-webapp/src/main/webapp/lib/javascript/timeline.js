var tl;

function initializePage() {
	
	var eventSource = new Timeline.DefaultEventSource();
	var bandInfos = [
    Timeline.createBandInfo({
    	eventSource: 	eventSource,
        width:          "80%",
        timeZone:       -5, 
        intervalUnit:   Timeline.DateTime.DAY, 
        intervalPixels: 100
    }),
    Timeline.createBandInfo({
     	showEventText:  false,
        trackHeight:    0.5,
        trackGap:       0.2,
    	eventSource:    eventSource,
        width:          "20%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 150
    })
  ];

  bandInfos[1].syncWith = 0;
  bandInfos[1].highlight = true;
  bandInfos[1].eventPainter.setLayout(bandInfos[0].eventPainter.getLayout());

  var height = getWindowHeight() * 0.75;
  document.getElementById("timeline").style.height=height+"px";

  var sourceUrl = document.getElementById("timelineSourceUrl").href;  
  tl = Timeline.create(document.getElementById("timeline"), bandInfos);  
  tl.loadXML(sourceUrl, function(xml, url) { eventSource.loadXML(xml, url); });
  setupFilterHighlightControls(document.getElementById("controls"), tl, [0,1], Timeline.getDefaultTheme());  
}

var resizeTimerID = null;
function onResize() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;            
            var height = getWindowHeight() * 0.75;
			document.getElementById("timeline").style.height=height+"px";						
            tl.layout();
        }, 500);
    }
}

function getWindowHeight() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }else{
    myHeight = 600;
  }
  return myHeight;
}


window.onload=initializePage;
window.onresize=onResize;