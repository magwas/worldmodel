<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>

<xsl:template match="/">
<html>
<head>

	<title>The Thing</title>
<script type="text/javascript" src="js/Util.js"></script>
<link rel="stylesheet" href="js/dijit/themes/claro/document.css"/>
<link rel="stylesheet" href="js/dijit/themes/claro/claro.css"/>
<script type="text/javascript" src="js/dojo/dojo.js" data-dojo-config="parseOnLoad: false, async:true">
</script><link rel="stylesheet" href="worldmodel.css"/>
<script type="text/javascript">
require(["thing/BaseObject"],function(BaseObject) {
  //BEGIN objects
	<xsl:apply-templates select=".//BaseObject"/>
  //END objects
	});
	</script>
</head>
<body class="claro">

	<!-- basic preloader: -->
	<div id="loader"><div id="loaderInner" style="direction:ltr;white-space:nowrap;overflow:visible;">Loading ... </div></div>

  <!-- context menu -->
	<div data-dojo-type="dijit.Menu" id="submenu1" data-dojo-props='contextMenuForWindow:true, style:"display:none"' style="display: none;">
		<div data-dojo-type="dijit.MenuItem" data-dojo-props="disabled:true">Context menu</div>
		<div data-dojo-type="dijit.MenuSeparator"></div>
	</div>
	<!-- end contextMenu -->

  <!-- main menu -->
	<div id="main" data-dojo-type="dijit.layout.BorderContainer" data-dojo-props="liveSplitters:false, design:'sidebar', persist:true">
		<div id="header" data-dojo-type="dijit.MenuBar" data-dojo-props="region:'top'">
			<div data-dojo-type="dijit.PopupMenuBarItem" id="edit">
				<span>Edit</span>
				<div data-dojo-type="dijit.Menu" id="editMenu">
					<div data-dojo-type="dijit.MenuItem" id="undo" data-dojo-props="iconClass:'dijitIconUndo'">A menu item will go here</div>
				</div>
			</div>
		</div>
  <!-- end main menu -->

  <!-- navigators -->
		<div data-dojo-type="dijit.layout.AccordionContainer" data-dojo-props="region:'leading', splitter:true, minSize:20"
			style="width: 300px;" id="navigatorArea">
		</div>
  <!-- end navigators -->

  <!-- browsers -->
		<!-- top tabs (marked as "center" to take up the main part of the BorderContainer) -->
		<div data-dojo-type="dijit.layout.TabContainer" data-dojo-props="region:'center', tabStrip:true" id="browserArea">
		</div><!-- end of region="center" TabContainer -->
  <!-- browsers -->

		<!-- bottom right tabs -->
		<div data-dojo-type="dijit.layout.TabContainer" id="inspectorArea"
			data-dojo-props="
				tabPosition:'bottom', region:'bottom',
				splitter:true, tabStrip:true
			">
			Inspector widgets can come here.
		</div><!-- end Bottom TabContainer -->

	</div><!-- end of BorderContainer -->
	
<script type="text/javascript" src="js/main.js"></script>
</body>
</html>

</xsl:template>

<xsl:template match="BaseObject">
		BaseObject.objectManager.create('<xsl:value-of select="@id"/>','<xsl:value-of select="@type"/>','<xsl:value-of select="@source"/>','<xsl:value-of select="@dest"/>','<xsl:value-of select="@value"/>');
</xsl:template>
<xsl:template match="@*|*|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
