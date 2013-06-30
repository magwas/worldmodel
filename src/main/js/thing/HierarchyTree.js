/*global define: false */
define(
    [ "dojo/_base/declare", "dijit/tree/ObjectStoreModel",
        "dijit/Tree", "thing/ObjectManager",
        "dijit/layout/ContentPane" ],
    function (declare, ObjectStoreModel,
              Tree, ObjectManager,
              ContentPane) {
        "use strict";
        return declare(
            "thing.HierarchyTree",
            [ ],
            {
                browser1: null,
                constructor : function () {
                    var obMan, hierarchyTreeModel, tree, browser1;
                    obMan = ObjectManager;
                    hierarchyTreeModel = new ObjectStoreModel({
                        store : ObjectManager,
                        containmentrelations: {contains: true},
                        query : {
                            id : 'hierarchyroot'
                        },
                        traceToContainment: function (trace, val) {
                            var i;
                            for (i = 0; i < trace.lenght; i += 1) {
                                hierarchyTreeModel.containmentrelations[trace[i].id] = val;
                            }
                        },
                        isContainment: function (obj, trace) {
                            var c, p;
                            c = hierarchyTreeModel.containmentrelations[obj.id];
                            /*jslint eqeq: true */
                            if (null != c) {
                                if (null == trace) {
                                    return c;
                                }
                                hierarchyTreeModel.traceToContainment(trace, c);
                                return c;
                            }
                            if (null == trace) {
                                trace = [];
                            }
                            if (-1 !== trace.indexOf(obj)) {
                                //loop
                                hierarchyTreeModel.traceToContainment(trace, false);
                                return false;
                            }
                            p = ObjectManager.get(obj.type);
                            if (p == null) {
                                hierarchyTreeModel.traceToContainment(trace, false);
                                return false;
                            }
                            trace.push(obj);
                            return hierarchyTreeModel.isContainment(p, trace);
                        },
                        getChildren: function (parentItem, onComplete, onError) {
                            var currentKids, q, i, l, listener, objectsfor, of, j;
                            currentKids = [];
                            q = ObjectManager.query({
                                source : parentItem.id
                            });
                            q.observe(listener);
                            objectsfor = function (obj) {
                                var r = [];
                                r.push(obj);
                                if (hierarchyTreeModel.isContainment(obj)) {
                                    r.push(ObjectManager.get(obj.dest));
                                }
                                return r;
                            };
                            listener = function (object, removedFrom, insertedInto) {
                                if (insertedInto > -1) {
                                    currentKids.push(objectsfor(object));
                                }
                                if (removedFrom > -1) {
                                    l = objectsfor(object);
                                    for (i = 0; i < l.length; i += 1) {
                                        currentKids.pop(l[i]);
                                    }
                                }
                                onComplete(currentKids);
                            };
                            for (i = 0; i < q.length; i += 1) {
                                of = objectsfor(q[i]);
                                for (j = 0; j < of.length; j += 1) {
                                    currentKids.push(of[j]);
                                }
                            }
                            window.currentKids = currentKids;
                            onComplete(currentKids);
                        }
                    });
                    this.tree = new Tree({
                        model : hierarchyTreeModel
                    });
                    this.browser1 = new ContentPane({
                        title : "Hierarchy Tree"
                    });
                    this.tree.placeAt(this.browser1);
                },
                placeAt: function (where) {
                    this.tree.startup();
                    this.browser1.placeAt(where);
                }
            }
        );
    }
);
