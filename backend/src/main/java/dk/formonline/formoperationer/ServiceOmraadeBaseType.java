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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceOmraadeBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceOmraadeBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oio:form:1.0.0}ObjektType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oio:form:1.0.0}ServiceOmraadeNummerIdentifikator"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceOmraadeBaseType", propOrder = {
    "serviceOmraadeNummerIdentifikator"
})
@XmlSeeAlso({
    ServiceOmraadeType.class
})
public class ServiceOmraadeBaseType
    extends ObjektType
{

    @XmlElement(name = "ServiceOmraadeNummerIdentifikator", required = true)
    protected String serviceOmraadeNummerIdentifikator;

    /**
     * Gets the value of the serviceOmraadeNummerIdentifikator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceOmraadeNummerIdentifikator() {
        return serviceOmraadeNummerIdentifikator;
    }

    /**
     * Sets the value of the serviceOmraadeNummerIdentifikator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceOmraadeNummerIdentifikator(String value) {
        this.serviceOmraadeNummerIdentifikator = value;
    }

}
