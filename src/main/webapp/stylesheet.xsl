<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>

<xsl:include href="baseobj_as_row.xsl"/>

<xsl:template match="/">
<html>
  		<head>
  			<script type="text/javascript" src="worldmodel.js"></script>
  			<meta charset='utf-8'/> 
  			<title>World Model</title>
  		</head>
  		<body>
  			<table id="table">
  				<tr>
  					<th>id</th><th>value</th><th>type</th><th>src</th><th>dest</th><th></th>
  				</tr>
  				<xsl:apply-templates select=".//BaseObject"/>
  			</table>
 		</body>
  	</html>
</xsl:template>

<xsl:template match="@*|*|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
