package com.kersuzan.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by kersuzan on 21/02/16.
 */
public class ParseApplication {

    private String xmlData; // XML file we are processing
    private ArrayList<Application> applications;

    public ParseApplication(String xmlData) {
        this.xmlData = xmlData;
        this.applications = new ArrayList<Application>();
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public boolean process() {
        boolean status = true;
        Application currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(this.xmlData));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d("ParseApplications", "Starting tag for " + tagName);
                        if (tagName.equalsIgnoreCase("entry")) {
                            Log.d("ParseApplication", "Create application item");
                            inEntry = true;
                            currentRecord = new Application();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = parser.getText(); // data itself, used in End_Tag
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d("ParseApplication", "Ending tag for " + tagName);

                        if (inEntry) {
                            if (tagName.equalsIgnoreCase("entry")) {
                                // Save the record and set entry to false
                                Log.d("ParseApplication", "Store Application With Name " + currentRecord.getName());
                                this.applications.add(currentRecord);
                                inEntry = false;
                            } else if (tagName.equalsIgnoreCase("name")) {
                                currentRecord.setName(textValue); // TextValue already updated
                            } else if (tagName.equalsIgnoreCase("artist")) {
                                currentRecord.setArtist(textValue);
                            } else  if (tagName.equalsIgnoreCase("releaseDate")) {
                                currentRecord.setReleaseDate(textValue);
                            }
                        }

                        break;

                    default:
                        //TODO
                        break;
                }

                eventType = parser.next();
            }

        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
