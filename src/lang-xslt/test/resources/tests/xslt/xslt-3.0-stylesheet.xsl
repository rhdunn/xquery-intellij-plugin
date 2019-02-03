<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:accumulator name="lorem-ipsum">
            <xsl:accumulator-role match="test" phase="start"/>
        </xsl:accumulator>

        <xsl:iterate select="lorem">
            <xsl:break select="ipsum"/>
            <xsl:on-completion select="dolor"/>
        </xsl:iterate>

        <xsl:try select="lorem">
            <xsl:catch select="ipsum">
            </xsl:catch>
        </xsl:try>

        <xsl:sequence>
            <xsl:on-empty select="0"/>
        </xsl:sequence>
    </xsl:template>

</xsl:stylesheet>