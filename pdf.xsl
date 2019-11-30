<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="strona"
				                       page-height="29.7cm" page-width="21cm"
				                       margin-top="1cm" margin-bottom="1cm"
				                       margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="1cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="strona">
				<fo:flow flow-name="xsl-region-body">
					<fo:block font-size="12pt">
						<xsl:value-of select="accountStatement/bank/name"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>