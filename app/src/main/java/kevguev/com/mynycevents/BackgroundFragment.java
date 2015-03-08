package kevguev.com.mynycevents;

/**
 * Created by Kevin Guevara on 12/25/2014.    Christmas
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Retrieves background information and updates the UI with it.
 * Parent Activity is MainActivity
 */
public class BackgroundFragment extends Fragment {

    private ArrayAdapter<String> listAdapter;
    private String[] venueName;
    private String[] neighborhoodNames;
    private String[] webDescNames;
    private String[] eventUrls;
    private String[] dates;

    public BackgroundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateEvents();
    }

    public void updateEvents() {
        FetchEventTask task = new FetchEventTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String eventType = prefs.getString(getString(R.string.pref_event_key), getString(R.string.pref_event_jazz));
        //Jazz Dance Theater forChildren
        task.execute(eventType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_back, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_events);
        listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_event,
                R.id.list_item_event_textview, new ArrayList<String>());
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Toast.makeText(getActivity(), venueName[position],Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), neighborhoodNames[position],Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), webDescNames[position],Toast.LENGTH_SHORT).show();*/

                Intent myIntent = new Intent(getActivity(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("venueName", venueName[position]); //Optional parameters
                extras.putString("neighborhoodName", neighborhoodNames[position]);
                extras.putString("webDescription", webDescNames[position]);
                extras.putString("eventUrl" , eventUrls[position]);
                extras.putString("dates", dates[position]);
                myIntent.putExtras(extras);
                startActivity(myIntent);
            }
        });

        return rootView;
    }

    //fetch nyt api json data, then pass it to the parser
    public class FetchEventTask extends AsyncTask<String, Void, JsonData> {

        @Override
        protected JsonData doInBackground(String... params) {

            final String LOG_TAG = FetchEventTask.class.getSimpleName();
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String eventsJsonStr = null;

            try {

                final String BASE_URL = "http://api.nytimes.com/svc/events/v2/";
                final String API_INNER = "listings.json?&filters=category:"+params[0]+",borough:Manhattan&api-key=";
                final String API_KEY = "e391e5ad15f7cc4fd33f9213fc706a2d:5:70566826";

                String LINK = BASE_URL+API_INNER+API_KEY;


                /*final String BASE_URL = "http://api.nytimes.com/svc/events/v2/";
                final String API_KEY = "e391e5ad15f7cc4fd33f9213fc706a2d:5:70566826";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("listings.json")
                        .appendQueryParameter("filters", "category:" + params[0])
                        .appendQueryParameter("borough", "Manhattan")
                        .appendQueryParameter("api-key", API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());*/
                URL url = new URL(LINK);
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                eventsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DetailFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in to parsing it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DetailFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                Log.v(LOG_TAG,eventsJsonStr);
                return new JsonData(eventsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonData jsonData) {
            try {
                String[] eventTitles = jsonData.getArrayListTitles();
                venueName = jsonData.getVenueNames();
                neighborhoodNames = jsonData.getNeighborhoodNames();
                webDescNames = jsonData.getWebDescriptionNames();
                eventUrls = jsonData.getEventUrls();
                dates = jsonData.getDateDesc();
                if (eventTitles != null) {
                    listAdapter.clear();
                    for (String eventString : eventTitles) {
                        listAdapter.add(eventString);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}