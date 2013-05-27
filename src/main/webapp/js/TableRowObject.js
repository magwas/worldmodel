/**
 * Object handler utilizing table rows
 */

var TableRowObject = BaseObject.extend({

	//list of types handled by this one. "*" means all
	handledTypes : ["*"],

	init: function(id,type,source,dest,value) {
		// this init is virtual, as handleObject should be implemented by subclasses
		this.id = id;
		this.type = type;
		this.source = source;
		this.dest = dest;
		this.value = value;
		this.handleObject();
	},

	unedit: function() {
		this.handleObject();
	},
	
	handleObject : function() {
		// add or update row
		var row = document.getElementById(this.id);
		if(row) {
			while (row.firstChild) {
			    row.removeChild(row.firstChild);
			}
		} else {
			row = document.createElement("tr");
			row.setAttribute("id",this.id);
			var table = document.getElementById("table");
			table.appendChild(row);
		}
		this.addCell(row,"id");
		this.addCell(row,"value");
		this.addCell(row,"type");
		this.addCell(row,"source");
		this.addCell(row,"dest");
		var td = document.createElement("td");
		var but = document.createElement("input");
		but.setAttribute("id",this.id+"_but");
		but.type="submit";
		but.value="Edit";
		but.setAttribute("onclick","javascript:edit('"+this.id+"')");
		td.appendChild(but);
		row.appendChild(td);
	},
	
	addCell : function(row,attname) {
		var td = document.createElement("td");
		td.setAttribute("id",this.id+"_"+attname);
		row.appendChild(td);
		if ( attname != "id" ) {
			var a = document.createElement("a");
			a.setAttribute("href","javascript:query('"+this[attname]+"')");
			a.textContent = this[attname];
			td.appendChild(a);
			var a2 = document.createElement("a");
			a2.setAttribute("href","javascript:search('"+attname+"','"+this.id+"')");
			a2.textContent = "+";
			td.appendChild(a2);
		} else {
			td.textContent = this[attname];
		}
	},

	addNodeAttr : function(node,fieldname) {
		// gathers the field value and sets it as the fieldname attribute of node
		// value is treated specially FIXME: value should be an attribute as well
		var e = document.getElementById(this.id+'_'+fieldname+'_t').value
		node.setAttribute(fieldname,e);
	},

	toedit : function(attname) {
		// turns a display row to an entry row
		var fullid = this.id + '_' + attname;
		var e = document.getElementById(fullid);
		e.textContent = ""
		var input = document.createElement("input");
		input.type = "text";
		input.id = fullid + "_t";
		input.value = this[attname];
		e.appendChild(input);
	},

	edit : function() {
		this.toedit("id");
		this.toedit("type");
		this.toedit("value");
		this.toedit("source");
		this.toedit("dest");
		var but = document.getElementById(this.id + "_but");
		but.type = "submit";
		but.value = "Submit"
		but.setAttribute("onclick","javascript:submit('" + this.id + "')");
		var but2 = document.createElement("input");
		but2.type = "submit";
		but2.value = "Cancel";
		but2.setAttribute("onclick","javascript:unedit('" + this.id + "')");
		but.parentNode.appendChild(but2);
	},
})

