package kevguev.com.mynycevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        //actionbar set back key and set event name as title of new activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setTitle(extras.getString("eventTitle"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true; // this worked before without return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView venueText;
        private TextView neighborText;
        private TextView descriptionText;
        private TextView dateText;
        private Button mapButton;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Toast.makeText(getActivity(),"Click Venue",Toast.LENGTH_SHORT).show();
            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();

            final String venueName = extras.getString("venueName");
            String neighborhoodName = extras.getString("neighborhoodName");
            String webDesc = extras.getString("webDescription");
            String eventUrl = extras.getString("eventUrl");
            String date = extras.getString("dates");


            venueText = (TextView) rootView.findViewById(R.id.venueNameTv);
            venueText.setText(venueName);
            venueText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "geo:?q="+ venueName;
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                }
            });
            /*mapButton= (Button) rootView.findViewById(R.id.button);
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "geo:?q="+ venueName;
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                }
            });
            */
            neighborText=(TextView) rootView.findViewById(R.id.neighborhoodTv);
            neighborText.setText(neighborhoodName);

            descriptionText = (TextView) rootView.findViewById(R.id.descriptionTv);
            descriptionText.setText(webDesc);

            dateText = (TextView) rootView.findViewById(R.id.dateTv);
            dateText.setText(date);

            final TextView myClickableUrl = (TextView) rootView.findViewById(R.id.urlTv);
            myClickableUrl.setText(eventUrl);
            Linkify.addLinks(myClickableUrl,Linkify.WEB_URLS);
            return rootView;
        }
    }
}
