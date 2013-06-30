require(["thing/BaseObject"], function(BaseObject){

	test( "constructor, fragToObject", function() {
		//create 
		var ob = new BaseObject({
			id: "theid",
			type: "thetype",
			source: "thesrc",
			dest: "thedest",
			value: "thevalue"
		});
		equal(ob.id,"theid");
		equal(ob.name,"theid");
		equal(ob.type,"thetype");
		equal(ob.source,"thesrc");
		equal(ob.dest,"thedest");
		equal(ob.value,"thevalue");

		// fragToObject
		var frag = document.createElement("BaseObject");
		frag.setAttribute("id","fragToObjectId");
		frag.setAttribute("type","fragToObjectType");
		frag.setAttribute("source","fragToObjectSource");
		frag.setAttribute("dest","fragToObjectDest");
		frag.setAttribute("value","fragToObjectValue");

		var ob2 = ob.fragToObject(frag);
		equal(ob2.id,"fragToObjectId");
		equal(ob2.type,"fragToObjectType");
		equal(ob2.source,"fragToObjectSource");
		equal(ob2.dest,"fragToObjectDest");
		equal(ob2.value,"fragToObjectValue");
		
		// not all attributes exist
		var frag2 = document.createElement("BaseObject");
		frag2.setAttribute("id","fragToObjectId2");
		frag2.setAttribute("type","fragToObjectType2");
		frag2.setAttribute("source","fragToObjectSource2");

		var ob3 = ob.fragToObject(frag2);

		equal(ob3.id,"fragToObjectId2");
		equal(ob3.type,"fragToObjectType2");
		equal(ob3.source,"fragToObjectSource2");
		equal(ob3.dest,null);
		equal(ob3.value,null);
	});	

	/* 
	test("fragToObject no id", function() {
		// no id FIXME not checked. Should it be?
		frag3 = document.createElement("BaseObject");
		frag3.setAttribute("type","fragToObjectType3");
		frag3.setAttribute("source","fragToObjectSource3");
		var numobjs = Object.keys(BaseObject.objectManager.objects).length;
		BaseObject.objectManager.fragToObject(frag3,TableRowObject);
		equal(Object.keys(BaseObject.objectManager.objects).length,numobjs+1);
		BaseObject.objectManager.fragToObject(frag3,TableRowObject);
		equal(Object.keys(BaseObject.objectManager.objects).length,numobjs+1);
	});	
	*/
	
});
