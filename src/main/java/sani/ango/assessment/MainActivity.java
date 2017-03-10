package sani.ango.assessment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // List of Developer objects representing the users data
    private List<Developer> developerList = new ArrayList<>();

    // ArrayAdapter for binding Developer objects to a ListView
    private DeveloperArrayAdapter adapter;
    private ListView developerListView; // displays weather info

    private URL url;
    private Map<String, String> profileList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createURL();


        if (isNetworkAvailable()) { //check for internet availability
            setContentView(R.layout.activity_main);

            developerListView = (ListView)findViewById(R.id.list);

            // create ArrayAdapter to bind developerList to the developerListView
            adapter = new DeveloperArrayAdapter(this, developerList);

            developerListView.setAdapter(adapter);
            developerListView.setOnItemClickListener(new ItemClickListener());

            new RetrieveGitHubAPI().execute(url); //make internet connection
        }
        else
        {
            setContentView(R.layout.no_connection);
        }

    }

    private void createURL() {
        String urlString = getString(R.string.api_url) +
                "" +  "+location:" + getString(R.string.location);
        try
        {
           url = new URL(urlString);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public void refresh(){
        if (isNetworkAvailable()) {
            setContentView(R.layout.activity_main); //load listView

            developerListView = (ListView)findViewById(R.id.list);

            // create ArrayAdapter to bind developerList to the developerListView
            adapter = new DeveloperArrayAdapter(this, developerList);

            developerListView.setAdapter(adapter);
            developerListView.setOnItemClickListener(new ItemClickListener());

            new RetrieveGitHubAPI().execute(url); //remake internet connection
        }
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager con = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = con.getActiveNetworkInfo();
        //return false if no internet connection
        return activeNetInfo != null && activeNetInfo.isConnected();
    }

    // makes the REST web service call to get weather data and
    // saves the data to a local HTML file
    private class RetrieveGitHubAPI extends AsyncTask<URL, Void, JSONObject>{

        @Override
        public void onPreExecute(){
            findViewById(R.id.progressBar)
                    .setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK)
                {
                    StringBuilder builder = new StringBuilder();
                    try(BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    )){
                        String line;

                        while ((line = reader.readLine()) != null)
                        {
                            builder.append(line);
                        }
                    }

                    return new JSONObject(builder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                connection.disconnect();
            }
            return null;
        }

        // process JSON response and update ListView
        @Override
        public void onPostExecute(JSONObject developerList){
            convertJSONtoArray(developerList); // repopulate developerList
            findViewById(R.id.progressBar)
                    .setVisibility(View.GONE);
            adapter.notifyDataSetChanged(); // rebind to ListView
            developerListView.smoothScrollToPosition(0); // scroll to top
        }
    }

    // create Developer objects from JSONObject containing data
    private void convertJSONtoArray(JSONObject object) {
        developerList.clear(); // clear old Developer data

        try {
            // get the "item" JSONArray from the data
            JSONArray items = object.getJSONArray("items");
            for (int i = 0; i < items.length(); ++i) {
                // convert each element of item to a Developer object
                JSONObject developer = items.getJSONObject(i);

                String username = developer.getString("login"); //get username
                String icon = developer.getString("avatar_url"); //get icon

                // add new Developer object to developerList
                developerList.add(new Developer(username, icon));

                //stores developer profile Url
                profileList.put(username, developer.getString("html_url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tv = ((TextView)((LinearLayout) view).getChildAt(1));
            String username = tv.getText().toString();

            ImageView image = ((ImageView)((LinearLayout) view).getChildAt(0));

            if(image.getDrawable() == null) //if icon has not finish downloading
                //set default icon
                image.setImageResource(R.drawable.image_loading);

            Bitmap icon = ((BitmapDrawable) image.getDrawable()).getBitmap();
            String profileURL = profileList.get(username);

            Intent intent = new Intent(MainActivity.this, ProfilePageActivity.class);
            intent.putExtra("name", username);
            intent.putExtra("image", icon);
            intent.putExtra("profile", profileURL);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.refresh){
            refresh();
            return true;
        }
        return false;
    }
}
