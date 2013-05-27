Util.xmlPost = function(xml) {
	var doc = (new DOMParser()).parseFromString(xml,'text/xml');
	return doc;
}

Util.xmlGet = function(uriend) {
	var doc = (new DOMParser()).parseFromString('<objects>\
			<BaseObject id=\"'+ uriend+ '\" type=\"'+uriend+'Type\"></BaseObject>\
			</objects>', 'text/xml')
	return doc;
};

	QUnit.config.noglobals = true;//FIXME: it does not work, check for noglobals by hand

alerttext = "";
alert = function (str) {
	alerttext += str;
};
clearalert = function() {
	alerttext = "";
};
checkalert = function(str) {
	if (str == null) {
		str ="";
	}
	equal(alerttext,str,"Alert check");
	clearalert();
};

function ifequal(a,b,comment) {
	if (a != b) {
		equal(a,b,comment)
	}
}

function equalxml(dom1,dom2) {
	ifequal(dom1.tagName.toLowerCase(),dom2.tagName.toLowerCase(),"tags "+dom1.tagName);
	var atts = dom1.attributes;
	for(var j=0; j<atts.length; j++) {
		var name = atts.item(j).name
		ifequal(dom2.getAttribute(name),atts.item(j).value, "attributes differ for "+name);
	}
	var atts2 = dom2.attributes;
	for(var j=0; j<atts2.length; j++) {
		var name = atts2.item(j).name
		ifequal(dom1.getAttribute(name),atts2.item(j).value, "attribute1 differ for "+name);
	}
	var nl = dom1.chilNodes;
	ifequal(dom1.childNodes.length,dom2.childNodes.length,"children length")
	for (var i=0; i< dom1.children.length; i++) {
		equalxml(dom1.childNodes[i],dom2.childNodes[i]);
	}
}
