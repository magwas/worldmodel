/*global require:false*/
require([ "dojo/dom", "dojo/ready", "dojo/_base/fx", "dojo/parser",
        "dijit/tree/ObjectStoreModel", "dijit/Tree",
        "dijit/layout/ContentPane", "thing/ObjectManager",
        "thing/TableBrowser", "thing/TypeTree", "thing/HierarchyTree",
        
        "dijit/Menu", "dijit/MenuBar",
        "dijit/MenuSeparator", "dijit/PopupMenuBarItem", "dijit/MenuItem",
        "dijit/layout/BorderContainer", "dijit/layout/AccordionContainer",
        "dijit/layout/TabContainer" ],
        function (dom, ready, fx, parser, ObjectStoreModel, Tree,
        ContentPane, ObjectManager, TableBrowser, TypeTree, HierarchyTree) {
    "use strict";
    (new HierarchyTree()).placeAt("navigatorArea");
    (new TypeTree()).placeAt("navigatorArea");
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
