/**
 * Utility "class" for ajax/xslt related functionality
 * not meant to be instantiated
 */

var Util = function(){
	alert("Util not meant to be instantiated!");
};

Util.xsltproc = null;

Util.xmlPost = function(xml,callback) {
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			  callback(xmlhttp.responseXML);
		};
	};
	xmlhttp.open("POST","/worldmodel/worldmodel",true);
	xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	xmlhttp.send(xml);
}

Util.xmlGet = function(uriend, callback) {
	var xmlhttp;
	var uri;
	if (window.XMLHttpRequest) {
		// code for modern browsers
		xmlhttp=new XMLHttpRequest();
	} else {// code for dinosaurs
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	if(document.URL.startsWith("file://")) {
		uri = "https://tomcat.realm:8443/worldmodel/worldmodel?";
	} else {
		uri = "/worldmodel/worldmodel?"
	}
//	alert(uri+uriend);
	xmlhttp.onreadystatechange=function() {
//		alert("readystate="+xmlhttp.readyState+", status="+xmlhttp.status);
		if (xmlhttp.readyState==4 && xmlhttp.status==0) {
//			alert("preflight OK");
		    callback(xmlhttp.responseXML);
		}
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
//			alert("calling back");
		    callback(xmlhttp.responseXML);
		}
	}
	xmlhttp.open("GET",uri+uriend,true);
	xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	xmlhttp.send();		
};

/*
Util.xml2Fragment = function(response) {
	if (Util.xsltproc == null) {
		Util.xsltproc = new XSLTProcessor();;
		var xsltdoc = Util.xmlGet("baseobj_as_row.xsl");
		Util.xsltproc.importStylesheet(xsltdoc);
	}
	frag = Util.xsltproc.transformToFragment(response,document);
	return frag;
}
*/

Util.processExceptions = function(response) {
	var allobjs = response.getElementsByTagName("exception"); 
	for ( var i=0; i<allobjs.length;i++ ) {
		 alert(allobjs[i].textContent);
	}
	return (allobjs.length == 0)
}
