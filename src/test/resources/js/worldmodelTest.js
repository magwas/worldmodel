test( "worldmodel edit, unedit, submit", function() {

	ObjectManager.create("worldmodel edit test")
	var numobjs = Object.keys(ObjectManager.objects).length;
	edit("worldmodel edit test");
	var trohtml = document.getElementById("worldmodel edit test");
	var doc = (new DOMParser()).parseFromString(
	"<tr id=\"worldmodel edit test\">" +
	"<td id=\"worldmodel edit test_id\"><input id=\"worldmodel edit test_id_t\" type=\"text\"/></td>" +
	"<td id=\"worldmodel edit test_value\"><input id=\"worldmodel edit test_value_t\" type=\"text\"/></td>" +
	"<td id=\"worldmodel edit test_type\"><input id=\"worldmodel edit test_type_t\" type=\"text\"/></td>" +
	"<td id=\"worldmodel edit test_source\"><input id=\"worldmodel edit test_source_t\" type=\"text\"/></td>" +
	"<td id=\"worldmodel edit test_dest\"><input id=\"worldmodel edit test_dest_t\" type=\"text\"/></td>" +
	"<td>" +
	"<input onclick=\"javascript:submit('worldmodel edit test')\" value=\"Submit\" id=\"worldmodel edit test_but\" type=\"submit\"/>" +
	"<input onclick=\"javascript:unedit('worldmodel edit test')\" value=\"Cancel\" type=\"submit\"/>" +
	"</td></tr>",
	 'text/xml');
	equal(Object.keys(ObjectManager.objects).length,numobjs);
	equalxml(trohtml,doc.documentElement);
	
	var desttag = document.getElementById("worldmodel edit test_dest_t");
	desttag.value="test1"
	var trohtml = document.getElementById("worldmodel edit test");
	equalxml(trohtml,doc.documentElement);	
	equal(document.getElementById("worldmodel edit test_id_t").value,"worldmodel edit test")
	equal(document.getElementById("worldmodel edit test_type_t").value,"")
	equal(document.getElementById("worldmodel edit test_source_t").value,"")
	equal(document.getElementById("worldmodel edit test_dest_t").value,"test1")


	unedit("worldmodel edit test");
	var doc2 = (new DOMParser()).parseFromString(
			"<tr id=\"worldmodel edit test\">" +
			"<td id=\"worldmodel edit test_id\">worldmodel edit test</td>" +
			"<td id=\"worldmodel edit test_value\">" +
			 "<a href=\"javascript:search('value=worldmodel edit test')\">+</a></td>" +
			"<td id=\"worldmodel edit test_type\">" +
			 "<a href=\"javascript:search('type=worldmodel edit test')\">+</a></td>" +
			"<td id=\"worldmodel edit test_source\">" +
			 "<a href=\"javascript:search('source=worldmodel edit test')\">+</a></td>" +
			"<td id=\"worldmodel edit test_dest\">" +
			 "<a href=\"javascript:search('dest=worldmodel edit test')\">+</a></td>" +
			"<td><input onclick=\"javascript:edit('worldmodel edit test')\" value=\"Edit\" id=\"worldmodel edit test_but\" type=\"submit\"/></td>" +
			"</tr>",
			 'text/xml');
	
	equal(Object.keys(ObjectManager.objects).length,numobjs);
	var trohtml = document.getElementById("worldmodel edit test");
	console.log(trohtml.innerHTML)
	equalxml(trohtml,doc2.documentElement);

	edit("worldmodel edit test");
	var desttag = document.getElementById("worldmodel edit test_dest_t");
	desttag.value="test2"

	submit("worldmodel edit test");
	equal(Object.keys(ObjectManager.objects).length,numobjs);
	var trohtml = document.getElementById("worldmodel edit test");
	equalxml(trohtml,doc2.documentElement);
	equal(ObjectManager.getObjectForId("worldmodel edit test").id,"worldmodel edit test");
	equal(ObjectManager.getObjectForId("worldmodel edit test").type,null);
	equal(ObjectManager.getObjectForId("worldmodel edit test").source,null);
	equal(ObjectManager.getObjectForId("worldmodel edit test").dest,"test2");
	equal(ObjectManager.getObjectForId("worldmodel edit test").value,null);


})

test( "worldmodel search", function() {
	var numobjs = Object.keys(ObjectManager.objects).length;
	search("id=worldmodel search test");
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
})