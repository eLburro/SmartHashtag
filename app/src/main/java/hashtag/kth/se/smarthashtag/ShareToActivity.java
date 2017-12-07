package hashtag.kth.se.smarthashtag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ShareToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to);

        // set thumbnail picture
        String selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        MediaUtils.populateImageToView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // set caption text
        String captionText = getIntent().getStringExtra("captionText");
        EditText captionField = findViewById(R.id.captionField);
        captionField.setText(captionText);

        // handle arrow back button
        ImageView arrowBackBtn = findViewById(R.id.btn_arrowBack);
        arrowBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateCaptionActivity.class);
                startActivity(intent);
            }
        });
    }
}
