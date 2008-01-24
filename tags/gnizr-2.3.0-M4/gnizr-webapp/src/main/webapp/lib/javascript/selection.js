// Author: Mihai Bazon, 2006
// http://www.bazon.net/mishoo/
// This code is (c) Dynarch.com, 2006.
//
// Permission is hereby granted to use this code 
// under the terms of the GNU LGPL. (www.gnu.org/licenses/lgpl.html)

var is_gecko = /gecko/i.test(navigator.userAgent);
var is_ie    = /MSIE/.test(navigator.userAgent);

function setSelectionRange(input, start, end) {
	if (is_gecko) {
		input.setSelectionRange(start, end);
	} else {
		// assumed IE
		var range = input.createTextRange();
		range.collapse(true);
		range.moveStart("character", start);
		range.moveEnd("character", end - start);
		range.select();
	}
};

function getSelectionStart(input) {
	if (is_gecko)
		return input.selectionStart;
	var range = document.selection.createRange();
	var isCollapsed = range.compareEndPoints("StartToEnd", range) == 0;
	if (!isCollapsed)
		range.collapse(true);
	var b = range.getBookmark();
	return b.charCodeAt(2) - 2;
};

function getSelectionEnd(input) {
	if (is_gecko)
		return input.selectionEnd;
	var range = document.selection.createRange();
	var isCollapsed = range.compareEndPoints("StartToEnd", range) == 0;
	if (!isCollapsed)
		range.collapse(false);
	var b = range.getBookmark();
	return b.charCodeAt(2) - 2;
};