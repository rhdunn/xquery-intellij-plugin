<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:for-each-group select="test" group-starting-with="@value" collation="http://example.com"/>
    </xsl:template>

</xsl:transform>