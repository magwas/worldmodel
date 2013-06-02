define([
     "dojo/_base/declare",
     "dojo/parser",
     "dojo/ready",
     "dojo/dom",
     "dijit/_WidgetBase",
     "dijit/_TemplatedMixin",
     "thing/ObjectManager",
     "thing/TableRowObject",
     "dijit/layout/ContentPane"
 ], function(declare, parser, ready, dom, _WidgetBase, _TemplatedMixin, ObjectManager, TableRowObject){
     return declare("thing.TableBrowser", [_WidgetBase,_TemplatedMixin], {
    	 templateString: "<div data-dojo-type=\"dijit.layout.ContentPane\" data-dojo-props='title:\"Table Browser\"'>",
    	 constructor: function() {
    		 this.query = ObjectManager.query()
    		 this.query.observe(this.observer(this));
    	 },
    	 observer: function(self) {
    		 return function(object, prevoiusIndex, newIndex) {
    			 self.handleObject(object);
    		 }
    	 },
         postCreate: function() {
        	 this.domNode.innerHTML = "<div><table width=\"100%\"><tr><td>id</td><td>name</td><td>value</td><td>type</td><td>source</td><td>dest</td></tr></table></div>";
        	 this.table = this.domNode.children[0].children[0]
        	 var objs = ObjectManager.query();
        	 objs.observe();
        	 for(var i=0 ; i<objs.length; i++) {
        		 this.handleObject(objs[i]);
        	 }
         },
     	handleObject : function(/*BaseObject*/obj) {
     		var row = dom.byId(obj.id);
     		if(row) {
     			row.update(obj)
     		} else {
     			row = new TableRowObject({obj:obj}).placeAt(this.table)
     		}
     		return row;
     	},
     });

/*
      ready(function(){//FIXME is it reached?
         parser.parse();
     });
*/
 });
