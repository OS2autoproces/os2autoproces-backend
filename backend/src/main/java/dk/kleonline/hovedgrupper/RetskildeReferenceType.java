//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.29 at 07:05:21 AM CET 
//


package dk.kleonline.hovedgrupper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RetskildeReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RetskildeReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RetskildeTitel" type="{http://www.kle-online.dk/rest/resources/emneplan}TitelType"/>
 *         &lt;element name="ParagrafEllerKapitel" type="{http://www.kle-online.dk/rest/resources/emneplan}ParagrafEllerKapitel" minOccurs="0"/>
 *         &lt;element name="RetsinfoAccessionsNr" type="{http://www.kle-online.dk/rest/resources/emneplan}RetsinfoAccessionsNrType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetskildeReferenceType", propOrder = {
    "retskildeTitel",
    "paragrafEllerKapitel",
    "retsinfoAccessionsNr"
})
public class RetskildeReferenceType {

    @XmlElement(name = "RetskildeTitel", required = true)
    protected String retskildeTitel;
    @XmlElement(name = "ParagrafEllerKapitel")
    protected String paragrafEllerKapitel;
    @XmlElement(name = "RetsinfoAccessionsNr", required = true)
    protected String retsinfoAccessionsNr;

    /**
     * Gets the value of the retskildeTitel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetskildeTitel() {
        return retskildeTitel;
    }

    /**
     * Sets the value of the retskildeTitel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetskildeTitel(String value) {
        this.retskildeTitel = value;
    }

    /**
     * Gets the value of the paragrafEllerKapitel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParagrafEllerKapitel() {
        return paragrafEllerKapitel;
    }

    /**
     * Sets the value of the paragrafEllerKapitel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParagrafEllerKapitel(String value) {
        this.paragrafEllerKapitel = value;
    }

    /**
     * Gets the value of the retsinfoAccessionsNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetsinfoAccessionsNr() {
        return retsinfoAccessionsNr;
    }

    /**
     * Sets the value of the retsinfoAccessionsNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetsinfoAccessionsNr(String value) {
        this.retsinfoAccessionsNr = value;
    }

}
