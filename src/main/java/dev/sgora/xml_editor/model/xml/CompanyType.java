//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 02:14:28 PM CEST 
//


package dev.sgora.xml_editor.model.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for companyType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="companyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="S.A."/>
 *     &lt;enumeration value="s. c."/>
 *     &lt;enumeration value="sp. j."/>
 *     &lt;enumeration value="sp. k."/>
 *     &lt;enumeration value="sp. p."/>
 *     &lt;enumeration value="sp. z o.o."/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "companyType")
@XmlEnum
public enum CompanyType {

	@XmlEnumValue("S.A.")
	S_A("S.A."),
	@XmlEnumValue("s. c.")
	S_C("s. c."),
	@XmlEnumValue("sp. j.")
	SP_J("sp. j."),
	@XmlEnumValue("sp. k.")
	SP_K("sp. k."),
	@XmlEnumValue("sp. p.")
	SP_P("sp. p."),
	@XmlEnumValue("sp. z o.o.")
	SP_Z_O_O("sp. z o.o.");
	private final String value;

	CompanyType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static CompanyType fromValue(String v) {
		for (CompanyType c : CompanyType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
