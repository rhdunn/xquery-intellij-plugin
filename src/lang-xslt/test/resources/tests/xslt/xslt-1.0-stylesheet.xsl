<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:key name="keys" match="one/two" use="@test"/>

    <xsl:template match="lorem/ipsum" name="test" mode="test">
        <xsl:number count="position()" from="chapter" format="1. "/>
        <xsl:for-each select="value">
            <xsl:apply-templates select="dolor" mode="test">
                <xsl:sort select="value"/>
            </xsl:apply-templates>
            <xsl:value-of select="position()"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>