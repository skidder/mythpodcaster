<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:dc="http://purl.org/dc/elements/1.1/">
  <xsl:output method="html" />
  <xsl:variable name="tz" select="xs:dayTimeDuration(fn:timezone-from-dateTime(fn:current-dateTime()))" />
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
	    <tr><td>Date:</td><td><xsl:value-of select="fn:format-dateTime(fn:adjust-dateTime-to-timezone(dc:date, $tz), '[D] [MNn] [Y] [h]:[m01][PN,*-2] [ZN,*-3]', (), (), 'us')"/></td></tr>
	    <tr><td>Channel:</td><td><xsl:value-of select="dc:creator"/></td></tr>
	    <tr><td>Media:</td><td><a><xsl:attribute name="href"><xsl:value-of select="enclosure/@url"/></xsl:attribute>Link</a></td></tr>
	    <tr><td>Embedded:</td><td><video preload="none"><xsl:attribute name="src"><xsl:value-of select="enclosure/@url"/></xsl:attribute><xsl:attribute name="type"><xsl:value-of select="enclosure/@type"/></xsl:attribute><xsl:attribute name="controls"/></video></td></tr>
	  </table>
	  <p/>  
	  <p/>  
	</xsl:for-each>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
