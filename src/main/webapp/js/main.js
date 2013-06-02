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
  "thing/ObjectManager",
  "thing/TableBrowser",
	"dijit/Menu",
	"dijit/MenuBar",
	"dijit/MenuSeparator",
	"dijit/PopupMenuBarItem",
	"dijit/MenuItem",
	"dijit/layout/BorderContainer",
	"dijit/layout/AccordionContainer",
	"dijit/layout/TabContainer",
], function(  dom, ready, fx, registry, locale, parser, Memory, ObjectStoreModel, Tree, Observable, ContentPane, ObjectManager, TableBrowser){


  ObjectManager.search("type=thing")
  obMan = ObjectManager;
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

      var tb = new TableBrowser().placeAt("browserArea")
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
