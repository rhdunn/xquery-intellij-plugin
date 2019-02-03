<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">

    <xsl:template match="lorem/ipsum" name="test">
        <xsl:accumulator name="lorem-ipsum" initial-value="2 cast as xs:long">
            <xsl:accumulator-rule match="test" phase="start" select="@value"/>
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
            <xsl:on-non-empty select="1"/>
        </xsl:sequence>

        <xsl:evaluate xpath="@query"
                      with-params="map{}"
                      context-item="@ctx"
                      namespace-context=".."/>

        <xsl:copy select="@*"/>

        <xsl:merge-source for-each-source="'test.xml'"
                          for-each-item="@value"
                          select="item">
            <xsl:merge-key select="@key"/>
        </xsl:merge-source>
    </xsl:template>

</xsl:stylesheet>