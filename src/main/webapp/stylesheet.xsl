<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>

  <xsl:template match="/">
<html>
  		<head>
  			<script type="text/javascript" src="worldmodel.js"></script>
  			<meta charset='utf-8'/> 
  			<title>World Model</title>
  		</head>
  		<body>
              <form id="form" action="javascript:edit()">
  			<table>
  				<tr>
  					<th>id</th><th>type</th><th>value</th><th>src</th><th>dest</th><th></th>
  				</tr>
  				<xsl:for-each select=".//BaseObject">
  					<tr>
  						<td id="id">
  						 <xsl:value-of select="@id"/>
  						</td>
  						<td id="value">
  						 <xsl:value-of select="."/>
  						</td>
  						<td id="type">
  						 <xsl:value-of select="@type"/>
  						</td>
  						<td id="source">
  						 <xsl:value-of select="@source"/>
  						</td>
  						<td id="dest">
  						 <xsl:value-of select="@dest"/>
  						</td>
              <td id="but"><input type="submit" value="Edit"/></td>
  					</tr>
  				</xsl:for-each>
  			</table>
              </form>
 		</body>
  	</html>
  </xsl:template>
  <xsl:template match="@*|*|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
