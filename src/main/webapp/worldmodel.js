

function loadXMLDoc(xml)
{
	  var xmlhttp;
	  var x,i;
	  xmlhttp=new XMLHttpRequest();
	  xmlhttp.open("POST","https://tomcat.realm:8443/worldmodel/worldmodel",false);
	  xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	  xmlhttp.send(xml);
	  xmlDoc=xmlhttp.responseXML;
	  var xmlText = new XMLSerializer().serializeToString(xmlDoc);
	  alert(xmlText);
	  return xmlDoc;
}

function loadXSLT(xslt)
{
	  var xmlhttp;
	  var x,i;
	  xmlhttp=new XMLHttpRequest();
	  xmlhttp.open("GET","https://tomcat.realm:8443/worldmodel/"+xslt,false);
	  xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	  xmlhttp.send();
	  xmlDoc=xmlhttp.responseXML;
	  var xmlText = new XMLSerializer().serializeToString(xmlDoc);
	  alert(xmlText);
	  return xmlDoc;
}

function addNodeAttr(node,objid,id) {
	 e=document.getElementById(objid+'_'+id+'_t').value
	 node.setAttribute(id,e);
}

function submit(id) {
	alert("submit "+id);
	doc = (new DOMParser()).parseFromString('<root id="root"/>', 'text/xml');
	 root = doc.documentElement;
	 bo = doc.createElement("BaseObject");
	 root.appendChild(bo);
	 addNodeAttr(bo,id,"id");
	 addNodeAttr(bo,id,"type");
	 addNodeAttr(bo,id,"source");
	 addNodeAttr(bo,id,"dest");
	 tn=doc.createTextNode("");
	 e=document.getElementById(id+'_value_t').value
	 tn.data=e;
	 bo.appendChild(tn);
	 var xmlText = new XMLSerializer().serializeToString(doc);
	 alert("query="+xmlText);
	 response = loadXMLDoc(xmlText);
	 insertRow(response);
}

function addCellFromId(obj,row,id) {
	td = document.createElement("td");
	td.textContent = obj.getAttribute(id);
	row.appendChild(td);	 
}

var xsltproc = null;

function query(id) {
	response=loadXSLT("worldmodel?id="+id);
	insertRow(response);
}
function insertRow(response) {
	table=document.getElementById("table");
	 allobjs = response.getElementsByTagName("exception"); 
	 for ( var i=0; i<allobjs.length;i++ ) {
		 alert(allobjs[i].textContent);
	 }

	if (xsltproc == null) {
		proc = new XSLTProcessor();;
		var xsltdoc = loadXSLT("baseobj_as_row.xsl");
	}
	if (allobjs.length == 0) {
		proc.importStylesheet(xsltdoc);
		frag = proc.transformToFragment(response,document);
		table.appendChild(frag);
	}

}

function toedit(objid,id) {
	fullid = objid+'_'+id;
	 e=document.getElementById(fullid);
	 v=e.textContent;
	 e.textContent=""
	 input=document.createElement("input");
	 input.type="text";
	 input.id=fullid+"_t";
	 input.value=v;
	 e.appendChild(input);
}

function edit(id) {
	 toedit(id,"id");
	 toedit(id,"type");
	 toedit(id,"value");
	 toedit(id,"source");
	 toedit(id,"dest");
	 but=document.getElementById(id+"_but");
	 but.type="submit";
	 but.value="Submit"
	 but.setAttribute("onclick","javascript:submit('"+id+"')");
}
