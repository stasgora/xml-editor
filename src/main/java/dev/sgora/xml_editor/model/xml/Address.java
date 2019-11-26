//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 02:14:28 PM CEST 
//


package dev.sgora.xml_editor.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for address complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="street" type="{}words"/>
 *         &lt;element name="number">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[1-9][0-9]*((/[1-9][0-9]*\p{L}?)|(\p{L}?))?"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="postCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[0-9]{2}-[0-9]{3}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="city">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="(\p{Lu}\p{Ll}+)( \p{Lu}\p{Ll}+)*"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder = {
		"street",
		"number",
		"postCode",
		"city"
})
public class Address {

	@XmlElement(required = true)
	protected String street;
	@XmlElement(required = true)
	protected String number;
	@XmlElement(required = true)
	protected String postCode;
	@XmlElement(required = true)
	protected String city;

	/**
	 * Gets the value of the street property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Sets the value of the street property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setStreet(String value) {
		this.street = value;
	}

	/**
	 * Gets the value of the number property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the value of the number property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setNumber(String value) {
		this.number = value;
	}

	/**
	 * Gets the value of the postCode property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * Sets the value of the postCode property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPostCode(String value) {
		this.postCode = value;
	}

	/**
	 * Gets the value of the city property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the value of the city property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setCity(String value) {
		this.city = value;
	}

}