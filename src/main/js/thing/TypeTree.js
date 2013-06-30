/*global define: false, console: false */
define(
    [ "dojo/_base/declare", "dijit/tree/ObjectStoreModel",
        "dijit/Tree", "thing/BaseObject",
        "dijit/layout/ContentPane"],
    function (declare, ObjectStoreModel,
              Tree, BaseObject,
              ContentPane, WidgetBase) {
        "use strict";
        return declare(
            "thing.TypeTree",
            [],
            {
                browser1 : null,
                constructor : function () {
                    var typeTreeModel, tree;
                    // Create the model
                    typeTreeModel = new ObjectStoreModel({
                        store : BaseObject.objectManager,
                        query : {
                            id : 'thing'
                        }
                    });
                    tree = new Tree({
                        model : typeTreeModel
                    });
                    this.browser1 = new ContentPane({
                        title : "Type Tree"
                    });
                    tree.placeAt(this.browser1);
                    tree.startup();

                },
                placeAt: function (where) {
                    this.browser1.placeAt(where);
                }
            }
        );

    }
);
