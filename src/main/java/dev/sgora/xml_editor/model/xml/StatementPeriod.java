//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 02:14:28 PM CEST 
//


package dev.sgora.xml_editor.model.xml;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * <p>Java class for statementPeriod complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="statementPeriod">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *       &lt;attribute name="startBalance" use="required" type="{}twoPlaceDecimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statementPeriod", propOrder = {
		"from",
		"to"
})
public class StatementPeriod {

	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar from;
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar to;
	@XmlAttribute(name = "startBalance", required = true)
	protected BigDecimal startBalance;

	/**
	 * Gets the value of the from property.
	 *
	 * @return possible object is
	 * {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getFrom() {
		return from;
	}

	/**
	 * Sets the value of the from property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setFrom(XMLGregorianCalendar value) {
		this.from = value;
	}

	/**
	 * Gets the value of the to property.
	 *
	 * @return possible object is
	 * {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getTo() {
		return to;
	}

	/**
	 * Sets the value of the to property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setTo(XMLGregorianCalendar value) {
		this.to = value;
	}

	/**
	 * Gets the value of the startBalance property.
	 *
	 * @return possible object is
	 * {@link BigDecimal }
	 */
	public BigDecimal getStartBalance() {
		return startBalance;
	}

	/**
	 * Sets the value of the startBalance property.
	 *
	 * @param value allowed object is
	 *              {@link BigDecimal }
	 */
	public void setStartBalance(BigDecimal value) {
		this.startBalance = value;
	}

}