define([
     "dojo/_base/declare",
     "dojo/parser",
     "dojo/ready",
     "dijit/_WidgetBase",
     "dijit/_TemplatedMixin",
     "thing/ObjectManager",
     "dijit/layout/ContentPane"
 ], function(declare, parser, ready, _WidgetBase, _TemplatedMixin, ObjectManager){
     return declare("thing.TableRowObject", [_WidgetBase,_TemplatedMixin], {
    	 templateString: "<tr></tr>",
    	 constructor: function(attrs) {
    		 this.obj = attrs.obj;
    	 },
    	 addCell : function(attname) {
 			var td = document.createElement("td");
 			td.setAttribute("id",this.obj.id+"_"+attname);
 			this.domNode.appendChild(td);
 			if ( attname != "id" && attname != "name") {
 				if(this.obj[attname] != null) {
 					var a = document.createElement("a");
 					a.setAttribute("href","javascript:dojo.ObjectManager.search('id="+this.obj[attname]+"')");
 					a.textContent = this.obj[attname];
 					td.appendChild(a);
 				}
 				var a2 = document.createElement("a");
 				a2.setAttribute("href","javascript:dojo.ObjectManager.search('"+attname+"="+this.obj.id+"')");
 				a2.textContent = "+";
 				td.appendChild(a2);
 			} else {
 				td.textContent = this.obj[attname];
 			}
 		},
 		postCreate: function() {
 			this.domNode.innerHTML="";
        	this.domNode.id = this.obj.id;
        	this.addCell("id");
        	this.addCell("name");
        	this.addCell("value");
        	this.addCell("type");
        	this.addCell("source");
        	this.addCell("dest");
     		var td = document.createElement("td");
    		var but = document.createElement("input");
    		but.setAttribute("id",this.obj.id+"_but");
    		but.type="submit";
    		but.value="Edit";
    		but.setAttribute("onclick","javascript:dojo."+this.id+".edit('"+this.obj.id+"')");
    		td.appendChild(but);
    		this.domNode.appendChild(td);
    		dojo[this.id] = this;
         },
         addEditCell : function(attname) {
  			var td = document.createElement("td");
 			if ( attname != "name") {
 	     		var input = document.createElement("input");
 	     		input.type = "text";
 	     		input.id = this.obj.id+"_"+attname + "_t";
 	     		if(this.obj[attname] == null) {
 	     			input.value = "";			
 	     		} else {
 	     			input.value = this.obj[attname];
 	     		}
 	     		td.appendChild(input);
 	 			td.setAttribute("id",this.obj.id+"_"+attname);
 			}
 			this.domNode.appendChild(td);
         },
         edit : function() {
  			this.domNode.innerHTML="";
        	this.addEditCell("id");
        	this.addEditCell("name");
        	this.addEditCell("value");
        	this.addEditCell("type");
        	this.addEditCell("source");
        	this.addEditCell("dest");
     		var td = document.createElement("td");
    		var but = document.createElement("input");
    		but.setAttribute("id",this.obj.id+"_but");
    		but.type = "submit";
    		but.value = "Submit"
    		but.setAttribute("onclick","javascript:dojo."+this.id+".submit('"+this.obj.id+"')");
    		var but2 = document.createElement("input");
    		but2.type = "submit";
    		but2.value = "Cancel";
    		but2.setAttribute("onclick","javascript:dojo."+this.id+".unedit('"+this.obj.id+"')");
    		td.appendChild(but);
    		td.appendChild(but2);
    		this.domNode.appendChild(td);
         },
         getDataFor : function(attname) {
        	 var e = document.getElementById(this.obj.id+"_"+attname + "_t");
        	 return  e.value;
         },
         unedit: function() {
     		this.postCreate();        	 
         },
         submit: function() {
        	 ObjectManager.submit(this);
         },
         submitted: function() {
      		this.postCreate();        	 
         }

     });
     ready(function(){//FIXME is it reached?
         parser.parse();
     });
});
