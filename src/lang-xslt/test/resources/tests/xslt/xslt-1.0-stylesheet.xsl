<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:number count="position()" format="1. "/>
    </xsl:template>

</xsl:stylesheet>