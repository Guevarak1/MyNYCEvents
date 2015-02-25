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

    public JsonData(String jsonString) throws JSONException{

        this.jsonString = jsonString;
        jsonObject = new JSONObject(jsonString);
        jsonArray = jsonObject.getJSONArray("results");

        }

    public String[] getArrayListTitles() throws JSONException {

        String[] titles = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject eventName = jsonArray.getJSONObject(i);
            titles[i] =  eventName.getString(EVENT_NAME) + "\n" ;
        }
        return titles;
    }

    public String[] getVenueNames() throws JSONException{
        String[] venNames = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject venueName = jsonArray.getJSONObject(i);
            venNames[i] = venueName.getString(VENUE_NAME);
        }
        return venNames;
    }

    public String[] getNeighborhoodNames() throws JSONException{
        String[] neighNames = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject venueName = jsonArray.getJSONObject(i);
            neighNames[i] = venueName.getString(NEIGHBORHOOD);
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
