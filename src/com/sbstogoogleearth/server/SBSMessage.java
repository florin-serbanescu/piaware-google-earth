/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main model of the SBS message
 *
 * @author florin
 */
public class SBSMessage {

    //
    // Fields from the message
    //
    public String transmissionMessage;
    public String type;
    public String transmissionType;
    public String sessionID;
    public String aircraftID;
    public String hexIdent;
    public String dateMessageGenerated;
    public String timeMessageGenerated;
    public String dateMessageLogged;
    public String timeMessageLogged;
    public String callsign;
    public String altitude;
    public String groundSpeed;
    public String track;
    public String latitude;
    public String longitude;
    public String verticalRate;
    public String squawk;
    public String alert;
    public String emergency;
    public String spi;
    public String isOnGround;

    //
    // Calculated fields
    //
    public String heading = "0";

    /**
     * Return a message object for the given string
     *
     * @param message Message to parse
     * @return Object model or throws exception if not correct
     */
    public static SBSMessage parse(String message) throws UnknownFormatConversionException {
        //
        // Add this overhead to make it work when empty tokens
        //
        String overhead = message + ",end";
        String[] parts = overhead.split(",");

        //
        // Many times is 21 so we handle those as well
        //
        if (parts.length < 21) {
            throw new UnknownFormatConversionException("Invalid SBS message. Found tokens:" + Integer.toString(parts.length));
        }

        SBSMessage ret = new SBSMessage();

        ret.transmissionMessage = parts[0];
        ret.type = parts[1];
        ret.transmissionType = parts[2];
        ret.sessionID = parts[3];
        ret.aircraftID = parts[4];
        ret.hexIdent = parts[5];
        ret.dateMessageGenerated = parts[6];
        ret.timeMessageGenerated = parts[7];
        ret.dateMessageLogged = parts[8];
        ret.timeMessageLogged = parts[9];
        ret.callsign = parts[10];
        ret.altitude = parts[11];
        ret.groundSpeed = parts[12];
        ret.track = parts[13];
        ret.latitude = parts[14];
        ret.longitude = parts[15];
        ret.verticalRate = parts[16];
        ret.squawk = parts[17];
        ret.alert = parts[18];
        ret.emergency = parts[19];
        ret.spi = parts[20];
        if (parts.length > 21) {
            ret.isOnGround = parts[21];
        } else {
            ret.isOnGround = "";
        }

        //
        // Convert feet to meter on altitude
        //
        try {
            double meters = Double.parseDouble(ret.altitude) * 0.3048;
            ret.altitude = Double.toString(meters);

        } catch (Exception ex) {

        }
        return ret;

    }

    /**
     * Calculate Heading between 2 points
     *
     * @param m1 Starting point
     * @param m2 Ending point
     * @return Heading in degrees or zero if something went wrong. We use int
     * for return as we don't need exact value
     */
    public static int calculateHeading(SBSMessage m1, SBSMessage m2) {

        double lat1 = 0;
        double lon1 = 0;
        double lat2 = 0;
        double lon2 = 0;
        try {
            lat1 = Double.parseDouble(m1.latitude);
            lat2 = Double.parseDouble(m2.latitude);
            lon1 = Double.parseDouble(m1.longitude);
            lon2 = Double.parseDouble(m2.longitude);
        } catch (Exception ex) {
            return 0;
        }

        //
        // Used for computation: http://www.movable-type.co.uk/scripts/latlong.html
        //
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        double b = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

        return (int) b;
    }

    /**
     * Simple string representation for this object
     *
     * @return String representation
     */
    public String toString() {
        return this.transmissionMessage + ":" + this.type + ":" + this.aircraftID;
    }

    /**
     * Return true if this object is suitable for KML transform like it contains
     * position, etc. Usually MSG 3 have this
     *
     * @return True if has position
     */
    public boolean isKMLSuitable() {
        return this.transmissionMessage.equals("MSG") && this.type.equals("3");
    }

    public String toKML() throws UnknownFormatConversionException {
        /*
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://earth.google.com/kml/2.0">
<Document>
<Camera>
<longitude>25.98561</longitude>
<latitude>44.64043</latitude>
<tilt>70.0</tilt>
<roll>0.7</roll>
<heading>263.1</heading>
<altitude>3368.1</altitude>
</Camera>
<Placemark>
<description>Flight : RYR4SW  
Reg : .NO-REG
Hex :4CA7C4
Type :
Flt Level : F111
</description>
<name>RYR4SW  </name>
<visibility>1</visibility>
<Camera>
<longitude>25.98561</longitude>
<latitude>44.64043</latitude>
<tilt>70.0</tilt>
<roll>0.7</roll>
<heading>263.1</heading>
<altitude>3368.1</altitude>
</Camera>
</Placemark>
    <ScreenOverlay>
        <Icon>
            <href><![CDATA[http://chart.apis.google.com/chart?chst=d_text_outline&chld=FFCC33|16|h|FF0000|_|Mad+Scientist|Boo]]></href>
        </Icon>
        <overlayXY x="0.01" xunits="fraction" y="0.9" yunits="fraction"/>
        <screenXY x="0.01" xunits="fraction" y="0.9" yunits="fraction"/>
    </ScreenOverlay>
</Document>
        
</kml>
        
        
Mode C altitude. Height relative to 1013.2mb (Flight Level). Not height AMSL..
         */

        if (this.isEmptyParam(this.latitude) || this.isEmptyParam(this.longitude) || this.isEmptyParam(this.altitude) || this.isEmptyParam(this.aircraftID)) {
            throw new UnknownFormatConversionException("Empty fields, cannot convert");
        }

        String overlay = this.aircraftID + ":: Alt:" + this.altitude + " Heading:" + this.heading;
        try {
            overlay = URLEncoder.encode(overlay, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SBSMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

        String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<kml xmlns=\"http://earth.google.com/kml/2.0\">"
                + "<Document>"
                + "<Camera>"
                + "<longitude>" + this.longitude + "</longitude>"
                + "<latitude>" + this.latitude + "</latitude>"
                + "<tilt>75.0</tilt>"
                + "<roll>0.7</roll>"
                + "<heading>" + this.heading + "</heading>"
                + "<altitude>" + this.altitude + "</altitude>"
                + "</Camera>"
                + "<Placemark>"
                + "<description>Flight " + this.aircraftID
                + "</description>"
                + "<name>" + this.aircraftID + "</name>"
                + "<visibility>1</visibility>"
                + "<Camera>"
                + "<longitude>" + this.longitude + "</longitude>"
                + "<latitude>" + this.latitude + "</latitude>"
                + "<tilt>75.0</tilt>"
                + "<roll>0.7</roll>"
                + "<heading>" + this.heading + "</heading>"
                + "<altitude>" + this.altitude + "</altitude>"
                + "</Camera>"
                + "</Placemark>"
                + "    <ScreenOverlay>"
                + "    <Icon>"
                + "        <href><![CDATA[http://chart.apis.google.com/chart?chst=d_text_outline&chld=FFCC33|16|h|FF0000|_|" + overlay + "]]></href>"
                + "    </Icon>"
                + "    <overlayXY x=\"0.01\" xunits=\"fraction\" y=\"0.9\" yunits=\"fraction\"/>"
                + "    <screenXY x=\"0.01\" xunits=\"fraction\" y=\"0.9\" yunits=\"fraction\"/>"
                + "</ScreenOverlay>"
                + "</Document>"
                + "</kml>";

        return ret;
    }

    /**
     * Return true if this param is null or empty
     *
     * @return
     */
    private boolean isEmptyParam(String p) {
        return (p == null) || (p.length() == 0);
    }
}
