package teicrete.gkentzoglanis.dailyactivities.stations;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

// Helper class for parsing the downloaded xml files.
public class StationsXmlPullParser {

	static final String KEY_STATION = "gasstation";
	static final String KEY_STATION2 = "supermarket";
	static final String KEY_COMPANY = "company";
	static final String KEY_STREET_ADDRESS = "streetaddress";
	static final String KEY_STREET_NUMBER = "streetnumber";
	static final String KEY_CITY = "city";
	static final String KEY_POSTCODE = "postcode";
	static final String KEY_STATE = "state";
	static final String KEY_COUNTRY = "country";
	static final String KEY_OPENING = "opening";
	static final String KEY_CLOSING = "closing";
	static final String KEY_IMAGE_URL = "image";
	static final String KEY_LATITUDE = "latitude";
	static final String KEY_LONGITUDE = "longitude";

	public static List<Stations> getStationsFromFile(Context ctx, String lol) {

		// List of Stations that we will return
		List<Stations> stations;
		stations = new ArrayList<Stations>();

		// temp holder for current Stations while parsing
		Stations curStation = null;
		// temp holder for current text value while parsing
		String curText = "";

		try {
			// Get our factory and PullParser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();

			// Open up InputStream and Reader of our file.
			FileInputStream fis = ctx.openFileInput(lol);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			// point the parser to our file.
			xpp.setInput(reader);

			// get initial eventType
			int eventType = xpp.getEventType();

			// Loop through pull events until we reach END_DOCUMENT
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Get the current tag
				String tagname = xpp.getName();

				// React to different event types appropriately
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ((tagname.equalsIgnoreCase(KEY_STATION))
							|| (tagname.equalsIgnoreCase(KEY_STATION2))) {
						// If we are starting a new <station> block we need
						// a new Stations object to represent it
						curStation = new Stations();
					}
					break;

				case XmlPullParser.TEXT:
					// grab the current text so we can use it in END_TAG event
					curText = xpp.getText();
					break;

				case XmlPullParser.END_TAG:
					if ((tagname.equalsIgnoreCase(KEY_STATION))
							|| (tagname.equalsIgnoreCase(KEY_STATION2))) {
						// if </station> then we are done with current Station
						// add it to the list.
						stations.add(curStation);
					} else if (tagname.equalsIgnoreCase(KEY_COMPANY)) {
						// if </company> use setCompany() on curStation
						curStation.setCompany(curText);
					} else if (tagname.equalsIgnoreCase(KEY_STREET_ADDRESS)) {
						// if </streetaddress> use setStrtAddress() on
						// curStation
						curStation.setStrtAddress(curText);
					} else if (tagname.equalsIgnoreCase(KEY_STREET_NUMBER)) {
						// if </streetnumber> use setStrtNumber() on curStation
						curStation.setStrtNumber(curText);
					} else if (tagname.equalsIgnoreCase(KEY_CITY)) {
						// if </city> use setCity() on curStation
						curStation.setCity(curText);
					} else if (tagname.equalsIgnoreCase(KEY_POSTCODE)) {
						// if </postcode> use setPostcode() on curStation
						curStation.setPostcode(curText);
					} else if (tagname.equalsIgnoreCase(KEY_STATE)) {
						// if </state> use setState() on curStation
						curStation.setState(curText);
					} else if (tagname.equalsIgnoreCase(KEY_COUNTRY)) {
						// if </country> use setCountry() on curStation
						curStation.setCountry(curText);
					} else if (tagname.equalsIgnoreCase(KEY_OPENING)) {
						// if </image> use setOpening() on curStation
						curStation.setOpening(curText);
					} else if (tagname.equalsIgnoreCase(KEY_CLOSING)) {
						// if </image> use setClosing() on curStation
						curStation.setClosing(curText);
					} else if (tagname.equalsIgnoreCase(KEY_IMAGE_URL)) {
						// if </image> use setImgUrl() on curStation
						curStation.setImgUrl(curText);
					} else if (tagname.equalsIgnoreCase(KEY_LATITUDE)) {
						// if </latitude> use setLatitude() on curStation
						curStation.setLatitude(curText);
					} else if (tagname.equalsIgnoreCase(KEY_LONGITUDE)) {
						// if </lonitude> use setLongitude() on curStation
						curStation.setLongitude(curText);
					}
					break;

				default:
					break;
				}
				// move on to next iteration
				eventType = xpp.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return the populated list.
		return stations;
	}

}