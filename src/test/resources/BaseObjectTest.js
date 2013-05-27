
function setUp() {
	ObjectManager = new BaseObject();

}

setUp();

test( "hello test", function() {
	//create and getObjectForId
	obj = ObjectManager.create("theid","thetype","thesrc","thedest","thevalue");
	equal("theid",ObjectManager.getObjectForId("theid").id);
	handler = ObjectManager.getHandlerForType("thing");
	equal(TableRowObject,handler);
});
