//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.19 at 10:48:38 AM CDT 
//


package org.subsonic.restapi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InternetRadioStations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InternetRadioStations">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="internetRadioStation" type="{http://subsonic.org/restapi}InternetRadioStation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InternetRadioStations", propOrder = {
    "internetRadioStation"
})
public class InternetRadioStations {

    protected List<InternetRadioStation> internetRadioStation;

    /**
     * Gets the value of the internetRadioStation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the internetRadioStation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInternetRadioStation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InternetRadioStation }
     * 
     * 
     */
    public List<InternetRadioStation> getInternetRadioStation() {
        if (internetRadioStation == null) {
            internetRadioStation = new ArrayList<InternetRadioStation>();
        }
        return this.internetRadioStation;
    }

}
