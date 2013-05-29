<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>

<xsl:template match="/">
<html>
  		<head>
  			<script type="text/javascript" src="js/Class.js"></script>
  			<script type="text/javascript" src="js/Util.js"></script>
  			<script type="text/javascript" src="../../test/resources/js/TestUtil.js"></script>
  			<script type="text/javascript" src="js/BaseObject.js"></script>
  			<script type="text/javascript" src="js/TableRowObject.js"></script>
  			<script type="text/javascript" src="js/worldmodel.js"></script>
  			<link rel="stylesheet" href="worldmodel.css"></link>
  			<meta charset='utf-8'/> 
  			<title>World Model</title>
  		</head>
  		<body>
  			<div id="menu">menu comes here</div>
  			<div id="navigator">navigator comes here</div>
  			<div id="browser">
  				<div id="browser.table" class="tablebrowser">
		  			<table id="table">
	  					<tr>
	  						<th>id</th><th>value</th><th>type</th><th>src</th><th>dest</th><th></th>
	  					</tr>
	  					<xsl:apply-templates select=".//BaseObject"/>
	  				</table>
  				</div>
  			</div>
  			<div id="inspector">inspector view comes here</div>
 		</body>
  	</html>
</xsl:template>
<xsl:template match="BaseObject">
	<script>
		ObjectManager.create('<xsl:value-of select="@id"/>','<xsl:value-of select="@type"/>','<xsl:value-of select="@source"/>','<xsl:value-of select="@dest"/>','<xsl:value-of select="@value"/>');
	</script>
</xsl:template>
<xsl:template match="@*|*|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
