package hashtag.kth.se.smarthashtag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ShareToActivity extends AppCompatActivity {

    public static final String MESSAGE_CAPTION = "Caption is copied to clipboard!";

    String type = "image/*";
    String selectedFilePath;
    String captionText;

    EditText captionTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to);

        // set thumbnail picture
        selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.main_img);
        MediaUtils.populateImageToView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // set caption text
        captionText = getIntent().getStringExtra("captionText");
        captionTextField = findViewById(R.id.captionField);
        captionTextField.setText(captionText);

        // handle caption text
        captionTextField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Select all caption text
                captionTextField.selectAll();

                // Copy caption text clipboard
                copyCaptionText(captionText);

                // Display message after copying thecaption
                Toast.makeText(ShareToActivity.this, MESSAGE_CAPTION,
                        Toast.LENGTH_LONG).show();
            }
        });

        // handle share to button
        Button shareToBtn = findViewById(R.id.shareToBtn);
        shareToBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                shareInstagramIntent(type, selectedFilePath, captionText);
            }
        });

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

    /**
     * Copy Caption text to clipboard
     *
     * @param captionText
     */
    private void copyCaptionText(String captionText) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("SmartHashtagCaption", captionText);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Share photo on Instagram
     *
     * @param type
     * @param filePath
     * @param captionText
     */
    private void shareInstagramIntent(String type, String filePath, String captionText) {

        Intent share = new Intent(Intent.ACTION_SEND);

        // MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(filePath);
        //Uri uri = Uri.fromFile(media);

        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Add the Caption to the Intent.
        // It is not working due to restriction imposed by Instagram
        //share.putExtra(Intent.EXTRA_TEXT, captionText);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
