package hashtag.kth.se.smarthashtag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class CreateCaptionActivity extends AppCompatActivity {

    private String selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_caption);

        // set thumbnail picture
        selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        MediaUtils.populateImageToView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // get hashtag list and populate it
        ListView listView;
        listView = findViewById(R.id.hashtagList);
        HashtagListView hashtagListView = getHashtagList();
        listView.setAdapter(hashtagListView);

        // handle share to button
        ImageView shareToBtn = findViewById(R.id.btn_shareTo);
        shareToBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShareToActivity.class);
                intent.putExtra("selectedFilePath", selectedFilePath);
                EditText captionField = findViewById(R.id.captionField);
                intent.putExtra("captionText", captionField.getText().toString());
                startActivity(intent);
            }
        });

        // handle arrow back button
        ImageView arrowBackBtn = findViewById(R.id.btn_arrowBack);
        arrowBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SelectPictureActivity.class);
                startActivity(intent);
            }
        });
    }

    // TODO: make it dynamic with the api call
    private HashtagListView getHashtagList() {
        String[] hashtag = {"#Sun", "#CloudySky", "#Chillout", "#Sunset", "#RedOrangeYellow"};
        String[] trend = {"200'000", "176'500", "120'750", "90'000", "3'200"};
        Integer[] imgid = {R.drawable.plussign, R.drawable.plussign, R.drawable.plussign, R.drawable.plussign, R.drawable.plussign};

        return new HashtagListView(this, hashtag, trend, imgid);
    }
}
