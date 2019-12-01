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
							<xsl:apply-templates select="accountStatement/period"/>
						</fo:inline-container>
						<fo:inline-container inline-progression-dimension="40%" margin-left="5em">
							<xsl:apply-templates select="accountStatement/client"/>
						</fo:inline-container>
					</fo:block>
					<xsl:apply-templates select="accountStatement/transactionHistory"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="transactionHistory">
		<fo:table margin-top="1.5em">
			<fo:table-body>
				<fo:table-row background-color="#cccccc" border-top="solid 0.4mm black" border-bottom="solid 0.4mm black">
					<xsl:call-template name="cellText">
						<xsl:with-param name="text" select="'Data wykonania'" />
					</xsl:call-template>
					<xsl:call-template name="cellText">
						<xsl:with-param name="text" select="'Data ksiegowania'" />
					</xsl:call-template>
					<xsl:call-template name="cellText">
						<xsl:with-param name="text" select="'Opis transakcji'" />
					</xsl:call-template>
					<xsl:call-template name="cellText">
						<xsl:with-param name="text" select="'Kwota transakcji'" />
					</xsl:call-template>
					<xsl:call-template name="cellText">
						<xsl:with-param name="text" select="'Saldo po transakcji'" />
					</xsl:call-template>
				</fo:table-row>
				<xsl:for-each select="transaction">
					<fo:table-row border-bottom="solid 0.2mm gray">
						<xsl:apply-templates select="." />
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<xsl:template match="transaction">
		<xsl:call-template name="cellText">
			<xsl:with-param name="text" select="ordered" />
		</xsl:call-template>
		<xsl:call-template name="cellText">
			<xsl:with-param name="text" select="processed" />
		</xsl:call-template>
		<xsl:call-template name="cellText">
			<xsl:with-param name="text" select="description" />
		</xsl:call-template>
		<xsl:call-template name="cellText">
			<xsl:with-param name="text" select="amount" />
		</xsl:call-template>
		<xsl:call-template name="cellText">
			<xsl:with-param name="text" select="'n/a'" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="cellText">
		<xsl:param name="text" />
		<fo:table-cell padding=".4em">
			<fo:block>
				<xsl:value-of select="$text" />
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	<xsl:template match="period">
		<fo:block font-size="16pt" margin-top="1.5em">
			<xsl:text>Historia transakcji</xsl:text>
		</fo:block>
		<fo:block>
			<xsl:value-of select="concat('za okres ', from, ' do ', to)" />
		</fo:block>
		<fo:block margin-top="1.5em">
			<xsl:call-template name="titleValue">
				<xsl:with-param name="title" select="'Saldo poczatkowe'" />
				<xsl:with-param name="value" select="@startBalance" />
			</xsl:call-template>
			<xsl:call-template name="titleValue">
				<xsl:with-param name="title" select="'Saldo koncowe'" />
				<xsl:with-param name="value" select="'n/a'" />
			</xsl:call-template>
		</fo:block>
	</xsl:template>
	<xsl:template match="bank">
		<fo:block>
			<xsl:value-of select="concat(name, ' ', name/@type)"/>
		</fo:block>
		<fo:block>
			<xsl:value-of select="concat(address/street, ' ', address/number, ', ', address/postCode, ' ', address/city)"/>
		</fo:block>
	</xsl:template>
	<xsl:template match="account">
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
	<xsl:template match="client">
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