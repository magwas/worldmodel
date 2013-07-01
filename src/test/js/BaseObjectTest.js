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
		equal(ob.name,"(@theid)[thevalue]");
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

	 
	test("fragToObject no id", function() {
		// no id FIXME not checked. Should it be?
		frag3 = document.createElement("BaseObject");
		frag3.setAttribute("type","fragToObjectType3");
		frag3.setAttribute("source","fragToObjectSource3");
		var ob1 = BaseObject.fragToObject(frag3);
        equal(ob1.id,null);
        equal(ob1.type,"fragToObjectType3");
        equal(ob1.source,"fragToObjectSource3");
        equal(ob1.dest,null);
        equal(ob1.value,null);
	});	
	
	test("naming", function () {
	       var doc = (new DOMParser()).parseFromString(
	               '<object>'+
	               '<BaseObject id="named1" type="folder" source="another folder"/>'+
                   '<BaseObject id="named2" type="folder" source="another folder"/>'+
                   '<BaseObject id="unnamed1" type="folder"/>'+
                   '<BaseObject id="containsname" type="name" source="contains" value="contains"/>'+
                   '<BaseObject id="namedrel" type="contains" source="named1" dest="unnamed1"/>'+
                   '<BaseObject id="named1_name" type="name" source="named1" value="Name of The Game"/>'+
                   '<BaseObject id="named2_name" type="name" source="named2" value="Name of The Lame"/>'+
                   '<BaseObject id="namedrel2" type="contains" source="named1" dest="named2"/>'+
	               '</object>', 'text/xml');
	        var numobjs = Object.keys(BaseObject.objectManager.data).length;
	        BaseObject.objectManager.processResponse(doc);
	        checkalert();
	        equal(Object.keys(BaseObject.objectManager.data).length,numobjs+8);
	        equal(BaseObject.objectManager.getObjectForId("named1").name,"Name of The Game");
            equal(BaseObject.objectManager.getObjectForId("namedrel").name,"(Name of The Game contains (@unnamed1))");
            equal(BaseObject.objectManager.getObjectForId("namedrel2").name,"(Name of The Game contains Name of The Lame)");
            equal(BaseObject.objectManager.getObjectForId("c1").name,"((@hierarchyroot) contains (@ontology))");


	});
	
});
