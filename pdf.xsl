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
				<fo:flow flow-name="xsl-region-body" font-size="9pt">
					<xsl:apply-templates select="accountStatement/bank"/>
					<fo:block margin-top="4em">
						<fo:inline-container inline-progression-dimension="50%">
							<xsl:apply-templates select="accountStatement/account"/>
						</fo:inline-container>
						<fo:inline-container inline-progression-dimension="40%" margin-left="5em">
							<xsl:apply-templates select="accountStatement/client"/>
						</fo:inline-container>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="accountStatement/bank">
		<fo:block>
			<xsl:value-of select="concat(name, ' ', name/@type)"/>
		</fo:block>
		<fo:block>
			<xsl:value-of select="concat(address/street, ' ', address/number, ', ', address/postCode, ' ', address/city)"/>
		</fo:block>
	</xsl:template>
	<xsl:template match="accountStatement/account">
		<xsl:call-template name="titleValue">
			<xsl:with-param name="title" select="'Nr rachunku'" />
			<xsl:with-param name="value" select="concat(substring(number, 1, 2), ' ', substring(number, 3, 4), ' ', substring(number, 7, 4), ' ', substring(number, 11, 4), ' ', substring(number, 15, 4), ' ', substring(number, 19, 4), ' ', substring(number, 23, 4))" />
		</xsl:call-template>
		<xsl:call-template name="titleValue">
			<xsl:with-param name="title" select="'IBAN'" />
			<xsl:with-param name="value" select="concat(substring(number, 1, 2), ' ', substring(number, 3, 4), ' ', substring(number, 7, 4), ' ', substring(number, 11, 4), ' ', substring(number, 15, 4), ' ', substring(number, 19, 4), ' ', substring(number, 23, 4))" />
		</xsl:call-template>
		<xsl:call-template name="titleValue">
			<xsl:with-param name="title" select="'Waluta rachunku'" />
			<xsl:with-param name="value" select="currency" />
		</xsl:call-template>
		<xsl:call-template name="titleValue">
			<xsl:with-param name="title" select="'Nazwa rachunku'" />
			<xsl:with-param name="value" select="@type" />
		</xsl:call-template>
		<xsl:if test="credit">
			<fo:block margin-top="1.5em"/>
			<xsl:call-template name="titleValue">
				<xsl:with-param name="title" select="'Limit kredytowy w rachunku'" />
				<xsl:with-param name="value" select="credit/limit" />
			</xsl:call-template>
			<xsl:call-template name="titleValue">
				<xsl:with-param name="title" select="'Oprocentowanie limitu na dzien generowania wyciagu'" />
				<xsl:with-param name="value" select="credit/interest" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template name="titleValue">
		<xsl:param name = "title" />
		<xsl:param name = "value" />
		<fo:block text-align-last="justify">
			<xsl:value-of select = "concat($title, ':')" />
			<fo:leader leader-pattern="space" />
			<fo:inline font-weight="bold">
				<xsl:value-of select="$value"/>
			</fo:inline>
		</fo:block>
	</xsl:template>
	<xsl:template match="accountStatement/client">
		<fo:block font-weight="bold">
			<xsl:value-of select="concat(name/firstName, ' ', name/lastName)"/>
		</fo:block>
		<fo:block font-weight="bold">
			<xsl:value-of select="concat(address/street, ' ', address/number)"/>
		</fo:block>
		<fo:block font-weight="bold">
			<xsl:value-of select="concat(address/postCode, ' ', address/city)"/>
		</fo:block>
	</xsl:template>
</xsl:stylesheet>