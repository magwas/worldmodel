require([
	"dojo/dom",
	"dojo/ready",
	"dojo/_base/fx",
	"dijit/registry",
	"dojo/date/locale",
	"dojo/parser",
	"dojo/store/Memory",
	"dijit/tree/ObjectStoreModel",
	"dijit/Tree",
  "dojo/store/Observable",
  "dijit/layout/ContentPane",
	"dijit/Menu",
	"dijit/MenuBar",
	"dijit/MenuSeparator",
	"dijit/PopupMenuBarItem",
	"dijit/MenuItem",
	"dijit/layout/BorderContainer",
	"dijit/layout/AccordionContainer",
	"dijit/layout/TabContainer",
], function(  dom, ready, fx, registry, locale, parser, Memory, ObjectStoreModel, Tree, Observable, ContentPane){

	ObjectManager = new Memory({
    data: [
           { id: 'thing', name: 'id=thing', type:'thing'},
       ],
	handlers: [],
    getChildren: function(object) {
       return this.query({parent: object.id});
       },
	search : function(query,offset) {//FIXME parameters changed!
		this.currentSearchQuery = query;
		this.currentSearchOffset = offset;
		if(offset != null ) {
			query += "&offset="+offset;
		}
		var response = Util.xmlGet(query);
		this.processResponse(response);
	},
	processResponse : function(response) {
		// processes a response from the server
		Util.processExceptions(response);
		var trs = response.querySelectorAll("BaseObject");
		for ( var i=0; i<trs.length;i++ ) {
			this.fragToObject(trs[i]);
		}
		var continues = response.querySelectorAll("continues").length;
		if(continues) {
			this.search(this.currentSearchQuery,this.currentSearchOffset+trs.length);
		}
	},

  });

  otherStore = new Observable(ObjectManager);
  otherStore.getChildren = otherStore.getSuccessor;
  // Create the model
  var typeTreeModel = new ObjectStoreModel({ store: ObjectManager, query: {id: 'thing'} });
  var tree = new Tree({ model: typeTreeModel });
  var browser1 = ContentPane({ 
      title: "Type Tree", 
      }).placeAt("navigatorArea")
  tree.placeAt(browser1);
  tree.startup();

	ready(function(){
		// Delay parsing until the dynamically injected theme <link>'s have had time to finish loading
		setTimeout(function(){
			parser.parse(dom.byId('container'));

			dom.byId('loaderInner').innerHTML += " done.";
			setTimeout(function hideLoader(){
				fx.fadeOut({ 
					node: 'loader', 
					duration:500,
					onEnd: function(n){
						n.style.display = "none";
					}
				}).play();
			}, 250);

		}, 320);
	});

});
