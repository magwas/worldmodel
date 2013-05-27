function setUp() {
	ObjectManager = new BaseObject();
};

setUp();

test( "create, getObjectForId, and getHandlerForType", function() {
	//create 
	ObjectManager.create("theid","thetype","thesrc","thedest","thevalue");
	
	// getObjectForId
	equal(ObjectManager.getObjectForId("theid").id,"theid");
	
	// getHandlerForType
	var handler = ObjectManager.getHandlerForType("thing");
	equal(handler,TableRowObject);
});

test("fragToObject full", function() {
	// fragToObject
	var frag = document.createElement("BaseObject");
	frag.setAttribute("id","fragToObjectId");
	frag.setAttribute("type","fragToObjectType");
	frag.setAttribute("source","fragToObjectSource");
	frag.setAttribute("dest","fragToObjectDest");
	frag.setAttribute("value","fragToObjectValue");
	ObjectManager.fragToObject(frag,TableRowObject);
	equal(ObjectManager.getObjectForId("fragToObjectId").id,"fragToObjectId");
	equal(ObjectManager.getObjectForId("fragToObjectId").type,"fragToObjectType");
	equal(ObjectManager.getObjectForId("fragToObjectId").source,"fragToObjectSource");
	equal(ObjectManager.getObjectForId("fragToObjectId").dest,"fragToObjectDest");
	equal(ObjectManager.getObjectForId("fragToObjectId").value,"fragToObjectValue");
	
	// same object not added twice
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.fragToObject(frag,TableRowObject);
	equal(Object.keys(ObjectManager.objects).length,numobjs);
});	

test("fragToObject partial", function() {
	// not all attributes exist
	var frag2 = document.createElement("BaseObject");
	frag2.setAttribute("id","fragToObjectId2");
	frag2.setAttribute("type","fragToObjectType2");
	frag2.setAttribute("source","fragToObjectSource2");
	ObjectManager.fragToObject(frag2,TableRowObject);
	equal(ObjectManager.getObjectForId("fragToObjectId2").id,"fragToObjectId2");
	equal(ObjectManager.getObjectForId("fragToObjectId2").type,"fragToObjectType2");
	equal(ObjectManager.getObjectForId("fragToObjectId2").source,"fragToObjectSource2");
	equal(ObjectManager.getObjectForId("fragToObjectId2").dest,null);
	equal(ObjectManager.getObjectForId("fragToObjectId2").value,null);
});	

/* 
test("fragToObject no id", function() {
	// no id FIXME not checked. Should it be?
	frag3 = document.createElement("BaseObject");
	frag3.setAttribute("type","fragToObjectType3");
	frag3.setAttribute("source","fragToObjectSource3");
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.fragToObject(frag3,TableRowObject);
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
	ObjectManager.fragToObject(frag3,TableRowObject);
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
});	
*/

test("fragToObjectAutomatic", function() {

	// fragtoObjectAutomatic
	var frag4 = document.createElement("BaseObject");
	frag4.setAttribute("id","fragToObjectId4");
	frag4.setAttribute("type","fragToObjectType4");
	frag4.setAttribute("dest","fragToObjectDest4");
	ObjectManager.fragToObjectAutomatic(frag4);
	equal(ObjectManager.getObjectForId("fragToObjectId4").id,"fragToObjectId4");
	equal(ObjectManager.getObjectForId("fragToObjectId4").type,"fragToObjectType4");
	equal(ObjectManager.getObjectForId("fragToObjectId4").source,null);
	equal(ObjectManager.getObjectForId("fragToObjectId4").dest,"fragToObjectDest4");
	equal(ObjectManager.getObjectForId("fragToObjectId4").value,null);
});	

test("processResponse basic", function() {	
	//processResponse: exceptions
	var frag = document.createElement("objects");
	var obj = document.createElement("BaseObject");
	obj.setAttribute("id","processResponseId1");
	obj.setAttribute("type","processResponseType1");
	obj.setAttribute("value","processResponseValue1");
	frag.appendChild(obj);
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(frag);
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
	equal(ObjectManager.getObjectForId("processResponseId1").id,"processResponseId1");
	equal(ObjectManager.getObjectForId("processResponseId1").type,"processResponseType1");
	equal(ObjectManager.getObjectForId("processResponseId1").source,null);
	equal(ObjectManager.getObjectForId("processResponseId1").dest,null);
	equal(ObjectManager.getObjectForId("processResponseId1").value,"processResponseValue1");
	
});

test("processResponse exception as root element", function() {	
	checkalert();//should not get an alert yet
	var doc = (new DOMParser()).parseFromString('<exception>test exception</exception>', 'text/xml');
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(doc);
	checkalert("test exception");
	equal(Object.keys(ObjectManager.objects).length,numobjs);

});

test("processResponse exception inside", function() {	
	checkalert();//should not get an alert yet
	var doc = (new DOMParser()).parseFromString('<object><exception>test exception2</exception></object>', 'text/xml');
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(doc);
	checkalert("test exception2");
	equal(Object.keys(ObjectManager.objects).length,numobjs);
});

test("processResponse more exceptions inside", function() {	
	checkalert();//should not get an alert yet
	var doc = (new DOMParser()).parseFromString('<object><exception>test exception3</exception><exception>test exception 4</exception></object>', 'text/xml');
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(doc);
	checkalert("test exception3test exception 4");
	equal(Object.keys(ObjectManager.objects).length,numobjs);
});

test("processResponse more objects", function() {	
	checkalert();//should not get an alert yet
	var doc = (new DOMParser()).parseFromString('<object><BaseObject id="pR1" type="pR1Type"></BaseObject><BaseObject id="pR2" value="pr2Value">foo</BaseObject></object>', 'text/xml');
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(doc);
	checkalert();
	equal(Object.keys(ObjectManager.objects).length,numobjs+2);
	equal(ObjectManager.getObjectForId("pR1").id,"pR1");
	equal(ObjectManager.getObjectForId("pR1").type,"pR1Type");
	equal(ObjectManager.getObjectForId("pR1").source,null);
	equal(ObjectManager.getObjectForId("pR1").dest,null);
	equal(ObjectManager.getObjectForId("pR1").value,null);
	equal(ObjectManager.getObjectForId("pR2").id,"pR2");
	equal(ObjectManager.getObjectForId("pR2").type,null);
	equal(ObjectManager.getObjectForId("pR2").source,null);
	equal(ObjectManager.getObjectForId("pR2").dest,null);
	equal(ObjectManager.getObjectForId("pR2").value,"pr2Value");
});

test("processResponse exceptions and objects", function() {	
	checkalert();//should not get an alert yet
	var doc = (new DOMParser()).parseFromString('<object><exception>test exception 5</exception><BaseObject id="pR3" type="pR1Type"></BaseObject><BaseObject id="pR4" value="pr2Value">foo</BaseObject></object>', 'text/xml');
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.processResponse(doc);
	checkalert("test exception 5");
	equal(Object.keys(ObjectManager.objects).length,numobjs+2);
	equal(ObjectManager.getObjectForId("pR3").id,"pR3");
	equal(ObjectManager.getObjectForId("pR3").type,"pR1Type");
	equal(ObjectManager.getObjectForId("pR3").source,null);
	equal(ObjectManager.getObjectForId("pR3").dest,null);
	equal(ObjectManager.getObjectForId("pR3").value,null);
	equal(ObjectManager.getObjectForId("pR4").id,"pR4");
	equal(ObjectManager.getObjectForId("pR4").type,null);
	equal(ObjectManager.getObjectForId("pR4").source,null);
	equal(ObjectManager.getObjectForId("pR4").dest,null);
	equal(ObjectManager.getObjectForId("pR4").value,"pr2Value");

});

test("search id=...", function() {	
	checkalert();//should not get an alert yet
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.search("id","querytest");
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
	equal(ObjectManager.getObjectForId("worldmodel?id=querytest").id,"worldmodel?id=querytest");
	equal(ObjectManager.getObjectForId("worldmodel?id=querytest").type,"worldmodel?id=querytestType");
	equal(ObjectManager.getObjectForId("worldmodel?id=querytest").source,null);
	equal(ObjectManager.getObjectForId("worldmodel?id=querytest").dest,null);
	equal(ObjectManager.getObjectForId("worldmodel?id=querytest").value,null);
});

test("search", function() {	
	checkalert();//should not get an alert yet
	var numobjs = Object.keys(ObjectManager.objects).length;
	ObjectManager.search("type","searchtest");
	equal(Object.keys(ObjectManager.objects).length,numobjs+1);
	equal(ObjectManager.getObjectForId("worldmodel?type=searchtest").id,"worldmodel?type=searchtest");
	equal(ObjectManager.getObjectForId("worldmodel?type=searchtest").type,"worldmodel?type=searchtestType");
	equal(ObjectManager.getObjectForId("worldmodel?type=searchtest").source,null);
	equal(ObjectManager.getObjectForId("worldmodel?type=searchtest").dest,null);
	equal(ObjectManager.getObjectForId("worldmodel?type=searchtest").value,null);
});
