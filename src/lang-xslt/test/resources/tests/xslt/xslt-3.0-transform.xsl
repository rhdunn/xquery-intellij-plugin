<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:accumulator name="lorem-ipsum">
            <xsl:accumulator-role match="test" phase="start"/>
        </xsl:accumulator>

        <xsl:iterate select="lorem">
        </xsl:iterate>
    </xsl:template>

</xsl:transform>