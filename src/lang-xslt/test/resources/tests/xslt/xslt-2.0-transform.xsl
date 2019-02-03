<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:attribute name="test" select="@value"/>
        <xsl:namespace name="test" select="@value"/>
        <xsl:processing-instruction name="test" select="@value"/>
        <xsl:for-each-group select="test"
                            group-starting-with="@start"
                            group-ending-with="@end"
                            collation="http://example.com"/>
    </xsl:template>

</xsl:transform>