define([
     "dojo/_base/declare",
     "dojo/parser",
     "dojo/ready",
     "dojo/dom",
     "dojo/dom-construct",
     "dijit/_WidgetBase",
     "dijit/_TemplatedMixin",
     "thing/ObjectManager",
     "thing/TableRowObject",
     "dijit/layout/ContentPane"
 ], function(declare, parser, ready, dom, domConstruct, _WidgetBase, _TemplatedMixin, ObjectManager, TableRowObject){
	return declare("thing.GraphBrowser", [_WidgetBase,_TemplatedMixin], {
   	 templateString: "<div data-dojo-type=\"dijit.layout.ContentPane\" data-dojo-props=\"title:'Graph Browser'\">",
	 constructor: function() {
		 ObjectManager.tb=this
		 this.query = ObjectManager.query()
		 this.query.observe(this.observer(this));
		 this.currobj = 10;
		 this.backlog = {type: [], source: [], dest: []};
	 },
	 /* private*/ observer: function(self) {
		 return function(object, prevoiusIndex, newIndex) {
			 self.handleObject(object);
		 }
	 },
     postCreate: function() {
    	 this.canvas = document.createElement("div");
    	 this.canvas.setAttribute("class", "graphBrowserCanvas");
    	 this.domNode.appendChild(this.canvas);
    	 this.svg = document.createElementNS('http://www.w3.org/2000/svg','svg');
    	 this.svg.setAttribute("class", "graphBrowserSvg");
    	 this.canvas.appendChild(this.svg);
    	 var objs = ObjectManager.query();
    	 objs.observe();
    	 for(var i=0 ; i<objs.length; i++) {
    		 this.handleObject(objs[i]);
    	 }
     },
     /* private */ drawLine: function(/*BaseObject*/obj1,/*BaseObject*/obj2, attname) {
  		var newLine = document.createElementNS('http://www.w3.org/2000/svg','line');//FIXME use domCreate for all document.create.*
  		newLine.setAttribute("class", "graphBrowserLine graphBrowserAtt"+attname);
 	    newLine.setAttribute('x1',obj1.coords[0]);
 	    newLine.setAttribute('y1',obj1.coords[1]);
 	    newLine.setAttribute('x2',obj2.coords[0]);
 	    newLine.setAttribute('y2',obj2.coords[1]);    	
 	    this.svg.appendChild(newLine);
     },
 	/* private */ handleObject : function(/*BaseObject*/obj) {
 		if (obj.coords == null) {
 			// spiralling out from 100;100
 			// x = sin(currobj/6)* currobj*10 + 100
 			// y = cos(currobj/6)* currobj*10 + 100
 			obj.coords = [
 			              Math.sin(this.currobj/6) * this.currobj * 10 + 300,
 			              Math.cos(this.currobj/6) * this.currobj * 10 + 300
 			              ];
 			this.currobj ++;
 		}
 		// FIXME: type-specific widgets come here
 		obj.rect = domConstruct.create("div",{class: "graphObject"},this.canvas);
 		obj.rect.style.top=obj.coords[1]+"px";
 		obj.rect.style.left=obj.coords[0]+"px";
 		obj.rect.innerHTML = obj.name;
 		this.backlog.type.push(obj);
 		this.backlog.source.push(obj);
 		this.backlog.dest.push(obj);
 		this.processBacklog("type");
 		this.processBacklog("source");
 		this.processBacklog("dest");
 	},
 	/* private */ processBacklog : function(attname) {
 		for(var i=0; i< this.backlog[attname].length; i++) {
 			var o = this.backlog[attname][i];
 			var oo = ObjectManager.getObjectForId(o[attname])
 			if(oo && oo.coords) {
 				this.drawLine(o,oo,attname);
 				this.backlog[attname].splice(i,1); //FIXME test it!
 			}
 		}
 	}

	});
});
