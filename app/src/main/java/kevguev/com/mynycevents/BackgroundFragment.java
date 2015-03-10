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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String[] eventTitles;
    private int numResults;
    private TextView textView;

    public BackgroundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //to do add refresh icon from desktop and test and .xml

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_refresh) {
            updateEvents();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateEvents();
    }

    public void updateEvents() {
        //Jazz Dance Theater forChildren event types
        FetchEventTask task = new FetchEventTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String eventType = prefs.getString(getString(R.string.pref_event_key), getString(R.string.pref_event_jazz));
        String borough = prefs.getString(getString(R.string.pref_borough_key), getString(R.string.pref_borough_manhattan));
        //make textview here because its when events are updates and not on onCreateView
        textView.setText( eventType + " events at " + borough);
        task.execute(eventType,borough);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_back, container, false);
        textView = (TextView) rootView.findViewById(R.id.eventTypeTv);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_view_events);
        listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_event,
                R.id.list_item_event_textview, new ArrayList<String>());
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(getActivity(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("eventTitle",eventTitles[position]);
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
                final String API_INNER = "listings.json?&filters=category:"+params[0]+",borough:"+params[1]+"&api-key=";
                //final String API_INNER = "listings.json?&filters=category:Art,borough:Manhattan&api-key=";
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
                eventTitles = jsonData.getArrayListTitles();
                venueName = jsonData.getVenueNames();
                neighborhoodNames = jsonData.getNeighborhoodNames();
                webDescNames = jsonData.getWebDescriptionNames();
                eventUrls = jsonData.getEventUrls();
                dates = jsonData.getDateDesc();
                numResults = jsonData.getNumResults();

                if(numResults == 0){
                    Toast.makeText(getActivity(), "no events :(", Toast.LENGTH_SHORT).show();
                }

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