package sani.ango.assessment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ango on 07-Mar-17.
 */
public class ProfilePageActivity extends Activity{

    private String name; //username
    private String profile; //profile url

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        ImageView iconImageView = (ImageView) findViewById(R.id.view);
        TextView nameTextView = (TextView) findViewById(R.id.profileUsername);
        TextView profileTextView = (TextView) findViewById(R.id.profileURL);

        //retrieve data from Intent
        name = getIntent().getStringExtra("name");
        Bitmap icon = getIntent().getParcelableExtra("image");
        profile = getIntent().getStringExtra("profile");


        iconImageView.setImageBitmap(icon);
        nameTextView.setText(name);
        profileTextView.setText(profile);
    }

    public void goToProfilePage(View view){
        //get the link to the profile
        TextView url = (TextView)view;
        String urlString = url.getText().toString();

        //create an intent to choose a web browser
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));

        startActivity(Intent.createChooser(webIntent,
                getString(R.string.chooser_title)));
    }

    public void shareProfile(View view){
        //create intent to share user profile
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_content, name, profile));
        shareIntent.setType("text/plain");

        //display apps that can share text
        startActivity(Intent.createChooser(shareIntent,
                getString(R.string.chooser_title)));
    }
}
