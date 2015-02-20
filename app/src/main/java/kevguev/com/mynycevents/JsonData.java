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
    JSONObject jsonObject;
    private JSONArray jsonArray;

    public JsonData(String jsonString) throws JSONException{

        this.jsonString = jsonString;
        jsonObject = new JSONObject(jsonString);
        jsonArray = jsonObject.getJSONArray("results");

        }

    public String[] getArrayListTitles() throws JSONException {

        String[] res = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject result = jsonArray.getJSONObject(i);
            res[i] =  result.getString(EVENT_NAME)+ "\n" ;
        }
        return res;
    }
}
