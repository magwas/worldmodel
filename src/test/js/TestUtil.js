
// testcases can set the test data for "server result" here.
Util.xmlAnswers = null;
Util.xmlAnsNo = 0;

Util.xmlPost = function(xml) {//FIXME: what about a local edit mode using this, a "save to file", and a "submit from file"
	if(Util.xmlnAswers == null) {
		var doc = (new DOMParser()).parseFromString(xml,'text/xml');
		return doc;
	} else {
		doc = Util.xmlAnswers[Util.xmlAnsNo];
		Util.xmlAnsNo ++;
		return doc;
	}
};

Util.xmlGet = function(uriend) {
	if(Util.xmlAnswers == null) {
		var doc = (new DOMParser()).parseFromString(
		        '<objects> <BaseObject id=\"'+ uriend+ '\" type=\"'+uriend+'Type\"></BaseObject>'+ 
		        '</objects>', 'text/xml');
		return doc;
	} else {
		doc = Util.xmlAnswers[Util.xmlAnsNo];		Util.xmlAnsNo ++;
		return doc;
	}
};

//QUnit.config.noglobals = true;//FIXME: it does not work, check for noglobals by hand


 
alerttext = "";
Util.showException = function (str) {
    "use strict";
    alerttext += str;
};

clearalert = function() {
    "use strict";
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
		equal(a,b,comment);
	}
}

function equalxml_p(dom1,dom2) {
    oldalert(new XMLSerializer().serializeToString(dom1)+ "\n------------------\n" + new XMLSerializer().serializeToString(dom2));
    equalxml(dom1,dom2);
}

function equalxml(dom1,dom2) {
	ifequal(dom1.tagName.toLowerCase(),dom2.tagName.toLowerCase(),"tags "+dom1.tagName);
	var atts = dom1.attributes;
	for(var j=0; j<atts.length; j++) {
		var name = atts.item(j).name;
		ifequal(dom2.getAttribute(name),atts.item(j).value, "attributes differ for "+name);
	}
	var atts2 = dom2.attributes;
	for(var j=0; j<atts2.length; j++) {
		var name = atts2.item(j).name;
		ifequal(dom1.getAttribute(name),atts2.item(j).value, "attribute1 differ for "+name);
	}
	var nl = dom1.chilNodes;
	ifequal(dom1.childNodes.length,dom2.childNodes.length,"children length");
	for (var i=0; i< dom1.children.length; i++) {
		equalxml(dom1.childNodes[i],dom2.childNodes[i]);
	}
}
