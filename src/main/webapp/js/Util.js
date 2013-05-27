/**
 * Utility "class" for ajax/xslt related functionality
 * not meant to be instantiated
 */

var Util = function(){
	alert("Util not meant to be instantiated!");
};

Util.xsltproc = null;

Util.xmlPost = function(xml) {
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.open("POST","/worldmodel/worldmodel",false);
	xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	xmlhttp.send(xml);
	xmlDoc=xmlhttp.responseXML;
	return xmlDoc;

}
Util.xmlGet = function(uriend) {
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	alert(uriend);
	xmlhttp.open("GET","/worldmodel/"+uriend,false);
	xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	xmlhttp.send();
	xmlDoc=xmlhttp.responseXML;
	alert(xmlDoc);
	return xmlDoc;
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
