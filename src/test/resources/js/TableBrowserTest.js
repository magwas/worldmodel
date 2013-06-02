require([
         "thing/TableBrowser",
         "thing/ObjectManager",
         ], function(TableBrowser,ObjectManager){
	


	test( "TableRowObject.init, edit, unedit (handleObject (addCell))", function() {
		checkalert();//should not get an alert yet
		var doc = (new DOMParser()).parseFromString('<object>'+
				'<BaseObject id="TRO1" type="TRO1Type" source="TRO1Source" dest="TRO1Dest" value="TRO1Value"></BaseObject>'+
				'<BaseObject id="TRO2" value="pr2Value">foo</BaseObject>'+
				'</object>', 'text/xml');
		var numobjs = ObjectManager.data.length;
		ObjectManager.processResponse(doc);
		var tb = new TableBrowser()
		.placeAt("testarea");
		equal(ObjectManager.data.length,numobjs+2);
		var trohtml = document.getElementById("TRO1");
		var vi=trohtml.getAttribute("widgetid")
		var str = ("<tr id=\"TRO1\" widgetid=\"WIDGETID\">" +
					"<td id=\"TRO1_id\">TRO1</td>" +
					"<td id=\"TRO1_name\">id=TRO1</td>" +
					"<td id=\"TRO1_value\"><a href=\"javascript:search('id=TRO1Value')\">TRO1Value</a><a href=\"javascript:search('value=TRO1')\">+</a></td>" +
					"<td id=\"TRO1_type\"><a href=\"javascript:search('id=TRO1Type')\">TRO1Type</a><a href=\"javascript:search('type=TRO1')\">+</a></td>" +
					"<td id=\"TRO1_source\"><a href=\"javascript:search('id=TRO1Source')\">TRO1Source</a><a href=\"javascript:search('source=TRO1')\">+</a></td>" +
					"<td id=\"TRO1_dest\"><a href=\"javascript:search('id=TRO1Dest')\">TRO1Dest</a><a href=\"javascript:search('dest=TRO1')\">+</a></td>" +
					"<td><input onclick=\"javascript:dojo.WIDGETID.edit('TRO1')\" value=\"Edit\" id=\"TRO1_but\" type=\"submit\"/></td>" +
				"</tr>").replace(/WIDGETID/g,vi);
		var doc = (new DOMParser()).parseFromString(str,'text/xml');
		equalxml(trohtml,doc.documentElement);

		dojo.thing_TableRowObject_0.edit();
		dojo.thing_TableRowObject_0.unedit();
		var trohtml2 = document.getElementById("TRO1");
		equalxml(trohtml2,doc.documentElement);

	});
	
/*
	test( "TableRowObject.edit, submit (addNodeAttr, handleObject(addCell)) ", function() {
		checkalert();//should not get an alert yet
		var numobjs = Object.keys(ObjectManager.objects).length;
		var frag = document.createElement("BaseObject");
		frag.setAttribute("id","TRO2");
		frag.setAttribute("type","TRO2Type");
		frag.setAttribute("source","TRO2Source");
		frag.setAttribute("dest","TRO2Dest");
		frag.setAttribute("value","TRO2Value");
		ObjectManager.fragToObject(frag,TableRowObject);

		var tro = ObjectManager.getObjectForId("TRO2");
		tro.edit();
		equal(Object.keys(ObjectManager.objects).length,numobjs+1);
		equal(tro.id,"TRO2");
		equal(tro.type,"TRO2Type");
		equal(tro.source,"TRO2Source");
		equal(tro.dest,"TRO2Dest");
		equal(tro.value,"TRO2Value");
		var trohtml = document.getElementById("TRO2");
		var doc = (new DOMParser()).parseFromString(
				"<tr id=\"TRO2\">" +
				"<td id=\"TRO2_id\"><input id=\"TRO2_id_t\" type=\"text\"/></td>" +
				"<td id=\"TRO2_value\"><input id=\"TRO2_value_t\" type=\"text\"/></td>" +
				"<td id=\"TRO2_type\"><input id=\"TRO2_type_t\" type=\"text\"/></td>" +
				"<td id=\"TRO2_source\"><input id=\"TRO2_source_t\" type=\"text\"/></td>" +
				"<td id=\"TRO2_dest\"><input id=\"TRO2_dest_t\" type=\"text\"/></td>" +
				"<td><input onclick=\"javascript:submit('TRO2')\" value=\"Submit\" id=\"TRO2_but\" type=\"submit\"/>" +
				"<input onclick=\"javascript:unedit('TRO2')\" value=\"Cancel\" type=\"submit\"/></td>" +
				"</tr>",
				 'text/xml');
		equalxml(trohtml,doc.documentElement);
		tro.submit();
		
	});
*/	
});
