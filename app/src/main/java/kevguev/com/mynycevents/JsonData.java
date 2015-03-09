package kevguev.com.mynycevents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin Guevara on 12/25/2014.
 */
public class JsonData {

    private static final String VENUE_NAME = "venue_name";
    private static final String EVENT_URL = "event_detail_url";
    private static final String DATE_TIME = "date_time_description";
    private static final String STREET_ADDRESS = "street_address";
    private static final String WEB_DESC = "web_description";
    private static final String EVENT_NAME = "event_name";
    private static final String BOROUGH = "borough";
    private static final String NEIGHBORHOOD = "neighborhood";
    private static final String CATEGORY= "category";
    private static final String PRICE= "price";

    private String jsonString;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int numResults;

    public JsonData(String jsonString) throws JSONException{

        this.jsonString = jsonString;
        jsonObject = new JSONObject(jsonString);
        jsonArray = jsonObject.getJSONArray("results");
        numResults = jsonObject.getInt("num_results");

        }

    public int getNumResults(){
        return numResults;
    }

    public String[] getArrayListTitles() throws JSONException {

        String[] events = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject eventName = jsonArray.getJSONObject(i);
            events[i] =  eventName.getString(EVENT_NAME) + "\n" ;
        }
        return events;
    }

    public String[] getDateDesc() throws JSONException {

        String[] events = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject eventName = jsonArray.getJSONObject(i);
            if(eventName.has(DATE_TIME)){
                events[i] = eventName.getString(DATE_TIME);
            }
        }
        return events;
    }

    public String[] getEventUrls() throws JSONException {

        String[] urls = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject urlName = jsonArray.getJSONObject(i);
            urls[i] =  urlName.getString(EVENT_URL);
        }
        return urls;
    }

    public String[] getVenueNames() throws JSONException{
        String[] venNames = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {

            JSONObject venueName = jsonArray.getJSONObject(i);

            //start off from here
            if(venueName.has(VENUE_NAME)) {
                venNames[i] = venueName.getString(VENUE_NAME);
            }
        }
        return venNames;
    }

    public String[] getNeighborhoodNames() throws JSONException{
        String[] neighNames = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject neighObj = jsonArray.getJSONObject(i);
            if (neighObj.has(NEIGHBORHOOD)) {
                neighNames[i] = neighObj.getString(NEIGHBORHOOD);
            }
        }
        return neighNames;
    }

    public String[] getWebDescriptionNames() throws JSONException{
        String[] webNames = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject venueName = jsonArray.getJSONObject(i);
            webNames[i] = venueName.getString(WEB_DESC).replace("<p>","").replace("</p>","")
                    .replace("&#8220;","\"").replace("&#8221;","\"").replace("&#8217;","'")
                    .replace("&#8212;","-").replace("&#161;","i").replace("&#160;"," ")
                    .replace("&#243;","o");
        }
        return webNames;
    }
}
