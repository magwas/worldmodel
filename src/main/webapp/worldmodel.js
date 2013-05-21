

function addNodeAttr(node,id) {
	 e=document.getElementById(id+'_t').value
	 node.setAttribute(id,e);
}

function loadXMLDoc(xml)
{
	  var xmlhttp;
	  var x,i;
	  xmlhttp=new XMLHttpRequest();
	  xmlhttp.open("POST","https://tomcat.realm:8443/worldmodel/worldmodel",false);
	  xmlhttp.setRequestHeader("Content-type","text/xml;charset=UTF-8");
	  alert("e");
	  xmlhttp.send(xml);
	  alert("f");
	  xmlDoc=xmlhttp.responseXML;
	  alert("xmlDoc="+xmlDoc);
	  var xmlText = new XMLSerializer().serializeToString(xmlDoc);
	  alert(xmlText);
}

function submit() {
	 doc = (new DOMParser()).parseFromString('<root id="root"/>', 'text/xml');
	 root = doc.documentElement;
	 bo = doc.createElement("BaseObject");
	 root.appendChild(bo);
	 addNodeAttr(bo,"id");
	 addNodeAttr(bo,"type");
	 addNodeAttr(bo,"source");
	 addNodeAttr(bo,"dest");
	 tn=doc.createTextNode("");
	 e=document.getElementById('value_t').value
	 tn.data=e;
	 bo.appendChild(tn);
	 var xmlText = new XMLSerializer().serializeToString(doc);
	 loadXMLDoc(xmlText);
	// alert("done");
}

function toedit(id) {
	 e=document.getElementById(id);
	 v=e.textContent;
	 e.textContent=""
	 input=document.createElement("input");
	 input.type="text";
	 input.id=id+"_t";
	 input.value=v;
	 e.appendChild(input);
}

function edit() {
	 but=document.getElementById("but");
	 but.textContent="Submit"
	 but.onclick=submit;
	 toedit("id");
	 toedit("type");
	 toedit("value");
	 toedit("source");
	 toedit("dest");
}
