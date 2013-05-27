test( "TableRowObject.init test", function() {
	checkalert();//should not get an alert yet
	var numobjs = Object.keys(ObjectManager.objects).length;
	var tro = new TableRowObject("TRO1","TRO1Type","TRO1Source","TRO1Dest","TRO1Value")
	// registration is the task of object manager
	equal(Object.keys(ObjectManager.objects).length,numobjs);
	equal(tro.id,"TRO1");
	equal(tro.type,"TRO1Type");
	equal(tro.source,"TRO1Source");
	equal(tro.dest,"TRO1Dest");
	equal(tro.value,"TRO1Value");
	var trohtml = document.getElementById("TRO1");
	var doc = (new DOMParser()).parseFromString(
			"<tr id=\"TRO1\">" +
				"<td id=\"TRO1_id\">TRO1</td>" +
				"<td id=\"TRO1_value\"><a href=\"javascript:query('TRO1Value')\">TRO1Value</a><a href=\"javascript:search('value','TRO1')\">+</a></td>" +
				"<td id=\"TRO1_type\"><a href=\"javascript:query('TRO1Type')\">TRO1Type</a><a href=\"javascript:search('type','TRO1')\">+</a></td>" +
				"<td id=\"TRO1_source\"><a href=\"javascript:query('TRO1Source')\">TRO1Source</a><a href=\"javascript:search('source','TRO1')\">+</a></td>" +
				"<td id=\"TRO1_dest\"><a href=\"javascript:query('TRO1Dest')\">TRO1Dest</a><a href=\"javascript:search('dest','TRO1')\">+</a></td>" +
				"<td><input onclick=\"javascript:edit('TRO1')\" value=\"Edit\" id=\"TRO1_but\" type=\"submit\"/></td>" +
			"</tr>",
			 'text/xml');
	equalxml(trohtml,doc.documentElement);
});

