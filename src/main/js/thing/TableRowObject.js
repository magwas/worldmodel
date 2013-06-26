/*globals dojo: false, define: false*/
define([ "dojo/_base/declare", "dijit/_WidgetBase",
        "dijit/_TemplatedMixin", "thing/ObjectManager",
        "dijit/layout/ContentPane" ], function (declare,
        WidgetBase, TemplatedMixin, ObjectManager) {
    "use strict";
    return declare("thing.TableRowObject", [ WidgetBase, TemplatedMixin ],
            {
            templateString : "<tr></tr>",
            constructor : function (attrs) {
                this.obj = attrs.obj;
            },
            /* private */
            addCell : function (attname) {
                var td, a, a2;
                td = document.createElement("td");
                td.setAttribute("id", this.obj.id + "_" + attname);
                this.domNode.appendChild(td);
                if (attname !== "id" && attname !== "name") {
                    if (this.obj[attname] !== null) {
                        a = document.createElement("a");
                        a.onclick = function () {
                            ObjectManager.search(this.id);
                        };
                        a.textContent = this.obj[attname];
                        td.appendChild(a);
                    }
                    a2 = document.createElement("a");
                    a2.onclick = function () {
                        ObjectManager.search(this.obj.id);
                    };
                    a2.textContent = "+";
                    td.appendChild(a2);
                } else {
                    td.textContent = this.obj[attname];
                }
            },
            postCreate : function () {
                var td, but;
                this.domNode.innerHTML = "";
                this.domNode.id = this.obj.id;
                this.addCell("id");
                this.addCell("name");
                this.addCell("value");
                this.addCell("type");
                this.addCell("source");
                this.addCell("dest");
                td = document.createElement("td");
                but = document.createElement("input");
                but.setAttribute("id", this.obj.id + "_but");
                but.type = "submit";
                but.value = "Edit";
                but.onclick = function () {
                    this.edit(this.obj.id);
                };
                td.appendChild(but);
                this.domNode.appendChild(td);
                dojo[this.id] = this;
            },
            /* private */
            addEditCell : function (attname) {
                var td, input;
                td = document.createElement("td");
                if (attname !== "name") {
                    input = document.createElement("input");
                    input.type = "text";
                    input.id = this.obj.id + "_" + attname + "_t";
                    if (this.obj[attname] === null) {
                        input.value = "";
                    } else {
                        input.value = this.obj[attname];
                    }
                    td.appendChild(input);
                    td.setAttribute("id", this.obj.id + "_" + attname);
                }
                this.domNode.appendChild(td);
            },
            edit : function () {
                this.domNode.innerHTML = "";
                this.addEditCell("id");
                this.addEditCell("name");
                this.addEditCell("value");
                this.addEditCell("type");
                this.addEditCell("source");
                this.addEditCell("dest");
                var td, but, but2;
                td = document.createElement("td");
                but = document.createElement("input");
                but.setAttribute("id", this.obj.id + "_but");
                but.type = "submit";
                but.value = "Submit";
                but.onclick = function () {
                    ObjectManager.submit(this);
                };
                but2 = document.createElement("input");
                but2.type = "submit";
                but2.value = "Cancel";
                but.onclick = function () {
                    this.unedit(this.obj.id);
                };
                td.appendChild(but);
                td.appendChild(but2);
                this.domNode.appendChild(td);
            },
            getDataFor : function (attname) {
                var e = document.getElementById(this.obj.id + "_" + attname
                        + "_t");
                return e.value;
            },
            unedit : function () {
                this.postCreate();
            },
            submit : function () {
                ObjectManager.submit(this);
            },
            submitted : function () {
                this.postCreate();
            }

        });
});
