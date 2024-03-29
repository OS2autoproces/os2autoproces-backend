//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.29 at 07:05:22 AM CET 
//


package dk.kleonline.emner;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.kleonline.emner package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EmneFilter_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "EmneFilter");
    private final static QName _EmneTitel_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "EmneTitel");
    private final static QName _HovedgruppeVejledning_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HovedgruppeVejledning");
    private final static QName _KLEAdministrativInfo_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "KLEAdministrativInfo");
    private final static QName _HandlingsfacetKategoriNr_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetKategoriNr");
    private final static QName _GruppeTitel_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "GruppeTitel");
    private final static QName _Emne_QNAME = new QName("http://www.kle-online.dk/rest/resources/emne/liste", "Emne");
    private final static QName _HandlingsfacetKategoriVejledning_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetKategoriVejledning");
    private final static QName _GruppeNr_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "GruppeNr");
    private final static QName _HandlingsfacetKategoriTitel_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetKategoriTitel");
    private final static QName _HandlingsfacetTitel_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetTitel");
    private final static QName _BevaringJaevnfoerArkivloven_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "BevaringJaevnfoerArkivloven");
    private final static QName _HovedgruppeNr_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HovedgruppeNr");
    private final static QName _SletningJaevnfoerPersondataloven_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "SletningJaevnfoerPersondataloven");
    private final static QName _HandlingsfacetNr_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetNr");
    private final static QName _HandlingsfacetVejledning_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HandlingsfacetVejledning");
    private final static QName _GruppeRetskildeReference_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "GruppeRetskildeReference");
    private final static QName _GruppeFilter_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "GruppeFilter");
    private final static QName _HovedgruppeFilter_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HovedgruppeFilter");
    private final static QName _EmneNr_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "EmneNr");
    private final static QName _HovedgruppeTitel_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "HovedgruppeTitel");
    private final static QName _UUID_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "UUID");
    private final static QName _GruppeVejledning_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "GruppeVejledning");
    private final static QName _EmneVejledning_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "EmneVejledning");
    private final static QName _EmneRetskildeReference_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "EmneRetskildeReference");
    private final static QName _VejledningTekstTypePUl_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "ul");
    private final static QName _VejledningTekstTypePB_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "b");
    private final static QName _VejledningTekstTypePOl_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "ol");
    private final static QName _VejledningTekstTypePI_QNAME = new QName("http://www.kle-online.dk/rest/resources/emneplan", "i");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.kleonline.emner
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VejledningTekstType }
     * 
     */
    public VejledningTekstType createVejledningTekstType() {
        return new VejledningTekstType();
    }

    /**
     * Create an instance of {@link VejledningTekstType.P }
     * 
     */
    public VejledningTekstType.P createVejledningTekstTypeP() {
        return new VejledningTekstType.P();
    }

    /**
     * Create an instance of {@link KLEEmner }
     * 
     */
    public KLEEmner createKLEEmner() {
        return new KLEEmner();
    }

    /**
     * Create an instance of {@link EmneType }
     * 
     */
    public EmneType createEmneType() {
        return new EmneType();
    }

    /**
     * Create an instance of {@link VejledningType }
     * 
     */
    public VejledningType createVejledningType() {
        return new VejledningType();
    }

    /**
     * Create an instance of {@link FilterType }
     * 
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link KLEAdministrativInfoType }
     * 
     */
    public KLEAdministrativInfoType createKLEAdministrativInfoType() {
        return new KLEAdministrativInfoType();
    }

    /**
     * Create an instance of {@link RetskildeReferenceType }
     * 
     */
    public RetskildeReferenceType createRetskildeReferenceType() {
        return new RetskildeReferenceType();
    }

    /**
     * Create an instance of {@link HistoriskType }
     * 
     */
    public HistoriskType createHistoriskType() {
        return new HistoriskType();
    }

    /**
     * Create an instance of {@link VejledningTekstType.P.Ul }
     * 
     */
    public VejledningTekstType.P.Ul createVejledningTekstTypePUl() {
        return new VejledningTekstType.P.Ul();
    }

    /**
     * Create an instance of {@link VejledningTekstType.P.Ol }
     * 
     */
    public VejledningTekstType.P.Ol createVejledningTekstTypePOl() {
        return new VejledningTekstType.P.Ol();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "EmneFilter")
    public JAXBElement<FilterType> createEmneFilter(FilterType value) {
        return new JAXBElement<FilterType>(_EmneFilter_QNAME, FilterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "EmneTitel")
    public JAXBElement<String> createEmneTitel(String value) {
        return new JAXBElement<String>(_EmneTitel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HovedgruppeVejledning")
    public JAXBElement<VejledningType> createHovedgruppeVejledning(VejledningType value) {
        return new JAXBElement<VejledningType>(_HovedgruppeVejledning_QNAME, VejledningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KLEAdministrativInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "KLEAdministrativInfo")
    public JAXBElement<KLEAdministrativInfoType> createKLEAdministrativInfo(KLEAdministrativInfoType value) {
        return new JAXBElement<KLEAdministrativInfoType>(_KLEAdministrativInfo_QNAME, KLEAdministrativInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetKategoriNr")
    public JAXBElement<String> createHandlingsfacetKategoriNr(String value) {
        return new JAXBElement<String>(_HandlingsfacetKategoriNr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "GruppeTitel")
    public JAXBElement<String> createGruppeTitel(String value) {
        return new JAXBElement<String>(_GruppeTitel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmneType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emne/liste", name = "Emne")
    public JAXBElement<EmneType> createEmne(EmneType value) {
        return new JAXBElement<EmneType>(_Emne_QNAME, EmneType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetKategoriVejledning")
    public JAXBElement<VejledningType> createHandlingsfacetKategoriVejledning(VejledningType value) {
        return new JAXBElement<VejledningType>(_HandlingsfacetKategoriVejledning_QNAME, VejledningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "GruppeNr")
    public JAXBElement<String> createGruppeNr(String value) {
        return new JAXBElement<String>(_GruppeNr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetKategoriTitel")
    public JAXBElement<String> createHandlingsfacetKategoriTitel(String value) {
        return new JAXBElement<String>(_HandlingsfacetKategoriTitel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetTitel")
    public JAXBElement<String> createHandlingsfacetTitel(String value) {
        return new JAXBElement<String>(_HandlingsfacetTitel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "BevaringJaevnfoerArkivloven")
    public JAXBElement<String> createBevaringJaevnfoerArkivloven(String value) {
        return new JAXBElement<String>(_BevaringJaevnfoerArkivloven_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HovedgruppeNr")
    public JAXBElement<String> createHovedgruppeNr(String value) {
        return new JAXBElement<String>(_HovedgruppeNr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "SletningJaevnfoerPersondataloven")
    public JAXBElement<Duration> createSletningJaevnfoerPersondataloven(Duration value) {
        return new JAXBElement<Duration>(_SletningJaevnfoerPersondataloven_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetNr")
    public JAXBElement<String> createHandlingsfacetNr(String value) {
        return new JAXBElement<String>(_HandlingsfacetNr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HandlingsfacetVejledning")
    public JAXBElement<VejledningType> createHandlingsfacetVejledning(VejledningType value) {
        return new JAXBElement<VejledningType>(_HandlingsfacetVejledning_QNAME, VejledningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetskildeReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "GruppeRetskildeReference")
    public JAXBElement<RetskildeReferenceType> createGruppeRetskildeReference(RetskildeReferenceType value) {
        return new JAXBElement<RetskildeReferenceType>(_GruppeRetskildeReference_QNAME, RetskildeReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "GruppeFilter")
    public JAXBElement<FilterType> createGruppeFilter(FilterType value) {
        return new JAXBElement<FilterType>(_GruppeFilter_QNAME, FilterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HovedgruppeFilter")
    public JAXBElement<FilterType> createHovedgruppeFilter(FilterType value) {
        return new JAXBElement<FilterType>(_HovedgruppeFilter_QNAME, FilterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "EmneNr")
    public JAXBElement<String> createEmneNr(String value) {
        return new JAXBElement<String>(_EmneNr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "HovedgruppeTitel")
    public JAXBElement<String> createHovedgruppeTitel(String value) {
        return new JAXBElement<String>(_HovedgruppeTitel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "UUID")
    public JAXBElement<String> createUUID(String value) {
        return new JAXBElement<String>(_UUID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "GruppeVejledning")
    public JAXBElement<VejledningType> createGruppeVejledning(VejledningType value) {
        return new JAXBElement<VejledningType>(_GruppeVejledning_QNAME, VejledningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "EmneVejledning")
    public JAXBElement<VejledningType> createEmneVejledning(VejledningType value) {
        return new JAXBElement<VejledningType>(_EmneVejledning_QNAME, VejledningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetskildeReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "EmneRetskildeReference")
    public JAXBElement<RetskildeReferenceType> createEmneRetskildeReference(RetskildeReferenceType value) {
        return new JAXBElement<RetskildeReferenceType>(_EmneRetskildeReference_QNAME, RetskildeReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningTekstType.P.Ul }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "ul", scope = VejledningTekstType.P.class)
    public JAXBElement<VejledningTekstType.P.Ul> createVejledningTekstTypePUl(VejledningTekstType.P.Ul value) {
        return new JAXBElement<VejledningTekstType.P.Ul>(_VejledningTekstTypePUl_QNAME, VejledningTekstType.P.Ul.class, VejledningTekstType.P.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "b", scope = VejledningTekstType.P.class)
    public JAXBElement<Object> createVejledningTekstTypePB(Object value) {
        return new JAXBElement<Object>(_VejledningTekstTypePB_QNAME, Object.class, VejledningTekstType.P.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VejledningTekstType.P.Ol }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "ol", scope = VejledningTekstType.P.class)
    public JAXBElement<VejledningTekstType.P.Ol> createVejledningTekstTypePOl(VejledningTekstType.P.Ol value) {
        return new JAXBElement<VejledningTekstType.P.Ol>(_VejledningTekstTypePOl_QNAME, VejledningTekstType.P.Ol.class, VejledningTekstType.P.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kle-online.dk/rest/resources/emneplan", name = "i", scope = VejledningTekstType.P.class)
    public JAXBElement<Object> createVejledningTekstTypePI(Object value) {
        return new JAXBElement<Object>(_VejledningTekstTypePI_QNAME, Object.class, VejledningTekstType.P.class, value);
    }

}
