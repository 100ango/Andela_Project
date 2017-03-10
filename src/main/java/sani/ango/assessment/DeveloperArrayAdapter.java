package sani.ango.assessment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ango on 07-Mar-17.
 */
public class DeveloperArrayAdapter extends ArrayAdapter<Developer>{
    // class for reusing views as list items scroll off and onto the screen
    private static class ViewHolder{
        TextView name;
        ImageView profileIcon;
    }

    // stores already downloaded Bitmaps for reuse
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    // constructor to initialize superclass inherited members
    public DeveloperArrayAdapter(Context context, List<Developer> developers) {
        super(context, -1, developers);
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View view, ViewGroup parent){
        // get developer object for this specified ListView position
        Developer developer = getItem(position);

        ViewHolder viewHolder; // object that reference's list item's views

        // check for reusable ViewHolder from a ListView item that scrolled
        // offscreen; otherwise, create a new ViewHolder
        if (view == null)// no reusable ViewHolder, so create one
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.name = (TextView)
                    view.findViewById(R.id.userTextView);
            viewHolder.profileIcon = (ImageView)
                    view.findViewById(R.id.imgIcon);

            view.setTag(viewHolder);
        }// reuse existing ViewHolder stored as the list item's tag
        else viewHolder = (ViewHolder) view.getTag();

        // if developer icon is already downloaded, use it;
        // otherwise, download icon in a separate thread
        if (bitmaps.containsKey(developer.getImgURL())) {
            viewHolder.profileIcon.setImageBitmap(
                    bitmaps.get(developer.getImgURL()));

        }// download and display weather condition image
        else new DownloadImageTask(viewHolder.profileIcon)
                    .execute(developer.getImgURL());

        viewHolder.name.setText(developer.getUsername());

        return view; // return completed list item to display
    }

    // AsyncTask to load developer icons in a separate thread
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        private ImageView imageView; // displays the thumbnail

        // store ImageView on which to set the downloaded Bitmap
        public DownloadImageTask(ImageView view){
            this.imageView = view;
        }

        // load image; params[0] is the String URL representing the image
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]); // create URL for image

                // open an HttpURLConnection, get its InputStream
                // and download the image
                connection = (HttpURLConnection) url.openConnection();

                try(InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap); // cache for later use

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect(); // close the HttpURLConnection
            }


            return bitmap;
        }

        // set developer image in list item
        @Override
        public void onPostExecute(Bitmap bitmap){
            imageView.setImageBitmap(bitmap);
        }
    }
}
