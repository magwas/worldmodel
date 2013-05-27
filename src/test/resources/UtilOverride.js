Util.xmlPost = function(xml) {
	var doc = (new DOMParser()).parseFromString(xml);
	return doc;
}

Util.xmlGet = function(uriend) {
	var doc = (new DOMParser()).parseFromString('<objects>\
			<BaseObject id=\"'+ uriend+ '\" type=\"'+uriend+'Type\"></BaseObject>\
			</objects>', 'text/xml')
	return doc;
};
