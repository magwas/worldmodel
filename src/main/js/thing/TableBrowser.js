/*global define: false */
define(
    [ "dojo/_base/declare", "dojo/dom",
            "dijit/_WidgetBase", "dijit/_TemplatedMixin",
            "thing/ObjectManager", "thing/TableRowObject",
            "dijit/layout/ContentPane" ],
    function (declare, dom, WidgetBase, TemplatedMixin,
            ObjectManager, TableRowObject) {
        "use strict";
        return declare(
            "thing.TableBrowser",
            [ WidgetBase, TemplatedMixin ],
            {
                templateString : "<div data-dojo-type=\"dijit.layout.ContentPane\" data-dojo-props=\"title:'Table Browser'\">",
                constructor : function () {
                    this.query = ObjectManager.query();
                    this.query.observe(this.observer(this));
                },
                /* private */
                observer : function (self) {
                    /*, FIXME prevoiusIndex, newIndex */
                    return function (object) {
                        self.handleObject(object);
                    };
                },
                postCreate : function () {
                    var objs, i;
                    this.domNode.innerHTML = "<div><table width=\"100%\"><tr><td>id</td><td>name</td><td>value</td><td>type</td><td>source</td><td>dest</td></tr></table></div>";
                    this.table = this.domNode.children[0].children[0];
                    objs = ObjectManager.query();
                    objs.observe();
                    for (i = 0; i < objs.length; i += 1) {
                        this.handleObject(objs[i]);
                    }
                },
                /* private */
                handleObject : function (obj) {
                    var row = dom.byId(obj.id);
                    if (row) {
                        row.update(obj);
                    } else {
                        row = new TableRowObject({
                            obj : obj
                        }).placeAt(this.table);
                    }
                    return row;
                }
            }
        );

    }
);
