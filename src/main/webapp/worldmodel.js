
var xsltproc = null;
var idlist = [];


function xmlPost(xml)
{
	  var xmlhttp;
	  var x,i;
	  xmlhttp=new XMLHttpRequest();
	  xmlhttp.open("POST","https://tomcat.realm:8443/worldmodel/worldmodel",false);
	  xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	  xmlhttp.send(xml);
	  xmlDoc=xmlhttp.responseXML;
	  return xmlDoc;
}

function xmlGet(xslt)
{
	  var xmlhttp;
	  var x,i;
	  xmlhttp=new XMLHttpRequest();
	  xmlhttp.open("GET","https://tomcat.realm:8443/worldmodel/"+xslt,false);
	  xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	  xmlhttp.send();
	  xmlDoc=xmlhttp.responseXML;
	  return xmlDoc;
}

function addNodeAttr(node,objid,id) {
	 e=document.getElementById(objid+'_'+id+'_t').value
	 node.setAttribute(id,e);
}

function submit(id) {
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
	 response = xmlPost(xmlText);
	 newid=document.getElementById(id+'_id_t').value
	 mytr = document.getElementById(id);
	 alert("newid="+newid+" id="+id);
	 if (newid == id ) {
		 if(processExceptions(response)) {
			 mytr.parentNode.replaceChild(xml2Fragment(response),mytr);
		 } else {
			 myself = xmlGet("worldmodel?id="+id);
			 mytr.parentNode.replaceChild(xml2Fragment(myself),mytr);			 
		 }
	 } else {
		 insertRow(response);
		 myself = xmlGet("worldmodel?id="+id);
		 mytr.parentNode.replaceChild(xml2Fragment(myself),mytr);
	 }

}

function addCellFromId(obj,row,id) {
	td = document.createElement("td");
	td.textContent = obj.getAttribute(id);
	row.appendChild(td);	 
}


function query(id) {
	response=xmlGet("worldmodel?id="+id);
	insertRow(response);
}

function xml2Fragment(response) {
	if (xsltproc == null) {
		xsltproc = new XSLTProcessor();;
		var xsltdoc = xmlGet("baseobj_as_row.xsl");
		xsltproc.importStylesheet(xsltdoc);
	}
	frag = xsltproc.transformToFragment(response,document);
	return frag;
}

function processExceptions(response) {
	 allobjs = response.getElementsByTagName("exception"); 
	 for ( var i=0; i<allobjs.length;i++ ) {
		 alert(allobjs[i].textContent);
	 }
	 return (allobjs.length == 0)
}
function insertRow(response) {
	table=document.getElementById("table");

	if (processExceptions(response)) {
		frag = xml2Fragment(response);
		trs = frag.querySelectorAll("tr");
		 for ( var i=0; i<trs.length;i++ ) {
			 newid=trs[i].id;
			 alert(newid);
			 if(idlist.indexOf(newid) == -1) {
				 idlist.push(trs[i].id);
				 table.appendChild(trs[i]);
			 }
		 }
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
