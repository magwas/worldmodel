<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>

  <xsl:template match="BaseObject">
  					<tr id="{@id}">
  						<td id="{@id}_id">
  						 <xsl:value-of select="@id"/>
  						</td>
  						<td id="{@id}_value">
  						 <xsl:value-of select="."/>
  						</td>
  						<td id="{@id}_type">
  						<a href="javascript:query('{@type}')">
  						 <xsl:value-of select="@type"/>
  						 </a>
  						</td>
  						<td id="{@id}_source">
  						<a href="javascript:query('{@source}')">
  						 <xsl:value-of select="@source"/>
  						</a>
  						</td>
  						<td id="{@id}_dest">
  						<a href="javascript:query('{@dest}')">
  						 <xsl:value-of select="@dest"/>
  						 </a>
  						</td>
              <td><input id="{@id}_but" type="submit" onclick="javascript:edit('{@id}')" value="Edit"/></td>
  					</tr>
  </xsl:template>
</xsl:stylesheet>
