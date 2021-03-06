<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:attribute name="test" select="@value"/>
        <xsl:namespace name="test" select="@value"/>
        <xsl:processing-instruction name="test" select="@value"/>
        <xsl:comment select="@value"
                     use-when="system-property('xsl:is-schema-aware')='yes'"/>

        <xsl:message select="@value"/>

        <xsl:for-each-group select="test"
                            group-by="type"
                            group-adjacent="self::index"
                            group-starting-with="@start"
                            group-ending-with="@end"
                            collation="http://example.com">
            <xsl:number select="@count"/>
            <xsl:sequence select="(1, 2)"/>
        </xsl:for-each-group>

        <xsl:perform-sort select="dolor">
            <xsl:sort select="."/>
        </xsl:perform-sort>

        <xsl:analyze-string select="." regex="[a-z]"/>
    </xsl:template>

</xsl:transform>