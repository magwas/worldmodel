/*global require:false*/
require([ "dojo/dom", "dojo/ready", "dojo/_base/fx", "dojo/parser",
        "dijit/tree/ObjectStoreModel", "dijit/Tree",
        "dijit/layout/ContentPane", "thing/ObjectManager",
        "thing/TableBrowser", "dijit/Menu", "dijit/MenuBar",
        "dijit/MenuSeparator", "dijit/PopupMenuBarItem", "dijit/MenuItem",
        "dijit/layout/BorderContainer", "dijit/layout/AccordionContainer",
        "dijit/layout/TabContainer" ], function (dom, ready, fx, parser, ObjectStoreModel, Tree,
        ContentPane, ObjectManager, TableBrowser) {
    "use strict";
    var obMan, typeTreeModel, tree, browser1;
    ObjectManager.search("type=thing");
    obMan = ObjectManager;
    // Create the model
    typeTreeModel = new ObjectStoreModel({
        store : ObjectManager,
        query : {
            id : 'thing'
        }
    });
    tree = new Tree({
        model : typeTreeModel
    });
    browser1 = new ContentPane({
        title : "Type Tree"
    }).placeAt("navigatorArea");
    tree.placeAt(browser1);
    tree.startup();

    ready(function () {
        // Delay parsing until the dynamically injected theme <link>'s have had
        // time to finish loading
        setTimeout(function () {
            parser.parse(dom.byId('container'));
            (new TableBrowser()).placeAt("browserArea");
            dom.byId('loaderInner').innerHTML += " done.";
            setTimeout(function () {
                fx.fadeOut({
                    node : 'loader',
                    duration : 500,
                    onEnd : function (n) {
                        n.style.display = "none";
                    }
                }).play();
            }, 250);

        }, 320);
    });

});
