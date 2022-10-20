//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.24 at 01:14:23 AM PDT 
//


package org.apache.xml.security.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for TransformAlgorithmType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransformAlgorithmType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="URI" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="JAVACLASS" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="INOUT" type="{http://www.xmlsecurity.org/NS/configuration}inOutAttrType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformAlgorithmType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {
    "value"
})
public class TransformAlgorithmType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "URI", required = true)
    protected String uri;
    @XmlAttribute(name = "JAVACLASS", required = true)
    protected String javaclass;
    @XmlAttribute(name = "INOUT")
    protected InOutAttrType inout;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the javaclass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJAVACLASS() {
        return javaclass;
    }

    /**
     * Sets the value of the javaclass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJAVACLASS(String value) {
        this.javaclass = value;
    }

    /**
     * Gets the value of the inout property.
     * 
     * @return
     *     possible object is
     *     {@link InOutAttrType }
     *     
     */
    public InOutAttrType getINOUT() {
        return inout;
    }

    /**
     * Sets the value of the inout property.
     * 
     * @param value
     *     allowed object is
     *     {@link InOutAttrType }
     *     
     */
    public void setINOUT(InOutAttrType value) {
        this.inout = value;
    }

}
