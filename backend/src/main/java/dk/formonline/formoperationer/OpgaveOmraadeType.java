//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.29 at 02:41:31 PM CEST 
//


package dk.formonline.formoperationer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OpgaveOmraadeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OpgaveOmraadeType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oio:form:1.0.0}OpgaveOmraadeBaseType">
 *       &lt;choice>
 *         &lt;element ref="{urn:oio:form:1.0.0}Opgave" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oio:form:1.0.0}OpgaveIndgang" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpgaveOmraadeType", propOrder = {
    "opgave",
    "opgaveIndgang"
})
public class OpgaveOmraadeType
    extends OpgaveOmraadeBaseType
{

    @XmlElement(name = "Opgave")
    protected List<OpgaveType> opgave;
    @XmlElement(name = "OpgaveIndgang")
    protected List<OpgaveIndgangType> opgaveIndgang;

    /**
     * Gets the value of the opgave property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the opgave property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOpgave().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OpgaveType }
     * 
     * 
     */
    public List<OpgaveType> getOpgave() {
        if (opgave == null) {
            opgave = new ArrayList<OpgaveType>();
        }
        return this.opgave;
    }

    /**
     * Gets the value of the opgaveIndgang property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the opgaveIndgang property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOpgaveIndgang().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OpgaveIndgangType }
     * 
     * 
     */
    public List<OpgaveIndgangType> getOpgaveIndgang() {
        if (opgaveIndgang == null) {
            opgaveIndgang = new ArrayList<OpgaveIndgangType>();
        }
        return this.opgaveIndgang;
    }

}
