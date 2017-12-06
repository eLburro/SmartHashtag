package hashtag.kth.se.smarthashtag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

public class CreateCaptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_caption);

        // set thumbnail picture
        String selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        MediaUtils.populateImagetoView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // get hashtag list and populate it
        ListView listView;
        listView = findViewById(R.id.hashtagList);
        HashtagListView hashtagListView = getHashtagList();
        listView.setAdapter(hashtagListView);
    }

    // TODO: make it dynamic with the api call
    private HashtagListView getHashtagList() {
        String[] hashtag = {"#Sun", "#CloudySky", "#Chillout", "#Sunset", "#RedOrangeYellow"};
        String[] trend = {"200'000", "176'500", "120'750", "90'000", "3'200"};
        Integer[] imgid = {R.drawable.plussign, R.drawable.plussign, R.drawable.plussign, R.drawable.plussign, R.drawable.plussign};

        return new HashtagListView(this, hashtag, trend, imgid);
    }
}
