//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.29 at 02:41:31 PM CEST 
//


package dk.formonline.formoperationer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HovedOmraadeIndgangType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HovedOmraadeIndgangType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oio:form:1.0.0}NavnTekst"/>
 *         &lt;element ref="{urn:oio:form:1.0.0}FormReference"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HovedOmraadeIndgangType", propOrder = {
    "navnTekst",
    "formReference"
})
public class HovedOmraadeIndgangType {

    @XmlElement(name = "NavnTekst", required = true)
    protected String navnTekst;
    @XmlElement(name = "FormReference", required = true)
    protected String formReference;

    /**
     * Gets the value of the navnTekst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavnTekst() {
        return navnTekst;
    }

    /**
     * Sets the value of the navnTekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavnTekst(String value) {
        this.navnTekst = value;
    }

    /**
     * Gets the value of the formReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormReference() {
        return formReference;
    }

    /**
     * Sets the value of the formReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormReference(String value) {
        this.formReference = value;
    }

}
