require([
         "thing/TableBrowser",
         "thing/ObjectManager",
         ], function(TableBrowser,ObjectManager){
	


	test( "TableBrowser.constructor, postCreate, ObjectManager.submit, edit, unedit, TableRowObject.constructor, postCreate, edit, unedit, getdataFor, submit, submitted", function() {
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
		var vi=trohtml.getAttribute("widgetid");
		var str = ("<tr id=\"TRO1\" widgetid=\"WIDGETID\">" +
					"<td id=\"TRO1_id\">TRO1</td>" +
					"<td id=\"TRO1_name\">id=TRO1</td>" +
					"<td id=\"TRO1_value\"><a>TRO1Value</a><a>+</a></td>" +
					"<td id=\"TRO1_type\"><a>TRO1Type</a><a>+</a></td>" +
					"<td id=\"TRO1_source\"><a>TRO1Source</a><a>+</a></td>" +
					"<td id=\"TRO1_dest\"><a>TRO1Dest</a><a>+</a></td>" +
					"<td><input value=\"Edit\" id=\"TRO1_but\" type=\"submit\"/></td>" +
				"</tr>").replace(/WIDGETID/g,vi);
		
		var doc = (new DOMParser()).parseFromString(str,'text/xml');
		equalxml(trohtml,doc.documentElement);

		document.getElementById("TRO1_but").onclick();
        document.getElementById("TRO1_butunedit").onclick();
		var trohtml2 = document.getElementById("TRO1");
		var vi2=trohtml.getAttribute("widgetid");
		equalxml(trohtml2,doc.documentElement);

		checkalert();//should not get an alert yet

	    document.getElementById("TRO1_but").onclick();

		var trohtml3 = document.getElementById("TRO1");
		var doc2str = "<tr id=\"TRO1\" widgetid=\"WIDGETID\">" +
				"<td id=\"TRO1_id\">" +
				"<input id=\"TRO1_id_t\" type=\"text\"/></td>" +
				"<td></td>" +
				"<td id=\"TRO1_value\"><input id=\"TRO1_value_t\" type=\"text\"/></td>" +
				"<td id=\"TRO1_type\"><input id=\"TRO1_type_t\" type=\"text\"/></td>" +
				"<td id=\"TRO1_source\"><input id=\"TRO1_source_t\" type=\"text\"/></td>" +
				"<td id=\"TRO1_dest\"><input id=\"TRO1_dest_t\" type=\"text\"/></td>" +
				"<td><input value=\"Submit\" id=\"TRO1_but\" type=\"submit\"/>" +
				"<input value=\"Cancel\" id=\"TRO1_butunedit\" type=\"submit\"/></td>" +
				"</tr>";
		doc2str = doc2str.replace(/WIDGETID/g,vi2);
		var doc2 = (new DOMParser()).parseFromString(doc2str,'text/xml');
		equalxml(trohtml2,doc2.documentElement);
		var numrows = trohtml2.parentElement.querySelectorAll("tr").length;
		
		var idfield = document.getElementById("TRO1_id_t");		
		equal(idfield.value,"TRO1");
		var srcfield = document.getElementById("TRO1_source_t");		
		equal(srcfield.value,"TRO1Source");
		
		idfield.value = "TRO11";
		srcfield.value = "NewSrc";
	    document.getElementById("TRO1_but").onclick();
		
		equal(ObjectManager.data.length,numobjs+3);
		equal(trohtml2.parentElement.querySelectorAll("tr").length,numrows+1);
		var tro11 = ObjectManager.getObjectForId("TRO11");
		var trohtml11 = document.getElementById("TRO11");
		equal(trohtml11.id,"TRO11");//exists
		equal(tro11.id,"TRO11");
		equal(tro11.type,"TRO1Type");
		equal(tro11.value,"TRO1Value");
		equal(tro11.source,"NewSrc");
		equal(tro11.dest,"TRO1Dest");
		
	});
});
