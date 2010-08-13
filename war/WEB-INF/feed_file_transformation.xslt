<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" />
  <xsl:template match="/">
    <html>
      <head>
	<title><xsl:value-of select="/rss/channel/title"/></title>
      </head>
      <body>
	<h2><xsl:value-of select="/rss/channel/title"/></h2>
	<h4>Listing of MythTV recordings of this program</h4>
	
	<xsl:for-each select="/rss/channel/item">
	  <table border="1">
	    <tr><td><b><h3>Title:</h3></b></td><td><b><xsl:value-of select="title"/></b></td></tr>
	    <tr><td>Description:</td><td><xsl:value-of select="description"/></td></tr>
	    <tr><td>Date:</td><td><xsl:value-of select="pubDate"/></td></tr>
	    <tr><td>Channel:</td><td><xsl:value-of select="creator"/></td></tr>
	    <tr><td>Media:</td><td><a><xsl:attribute name="href"><xsl:value-of select="enclosure/@url"/></xsl:attribute>Link</a></td></tr>
	    <tr><td>Embedded:</td><td><video preload="none" controls="true"><xsl:attribute name="src"><xsl:value-of select="enclosure/@url"/></xsl:attribute><xsl:attribute name="type"><xsl:value-of select="enclosure/@type"/></xsl:attribute></video></td></tr>
	  </table>
	  <p/>  
	  <p/>  
	</xsl:for-each>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
