package hashtag.kth.se.smarthashtag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCaptionActivity extends AppCompatActivity {
    private final String API_KEY = "AIzaSyCei2QI9MhOVHDuaVlbR-_SjCDE8nOgpiA";

    private String selectedFilePath;
    private Vision vision;
    private ArrayList<String> hashtags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_caption);

        // set thumbnail picture
        selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        MediaUtils.populateImageToView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // call Google vision API
        prepareVisionApi();
        new RequestHashtagsTask().execute(selectedFilePath);

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

    private HashtagListView createHashtagList() {
        int arrSize = hashtags.size();
        String[] trendArr = new String[arrSize];
        Integer[] imgIdArr = new Integer[arrSize];
        String[] hashtagArr = new String[arrSize];

        for (int i = 0; i < arrSize; i++) {
            hashtagArr[i] = formatHashtag(hashtags.get(i));
            trendArr[i] = "200'000";
            imgIdArr[i] = R.drawable.plus;
        }

        return new HashtagListView(this, hashtagArr, trendArr, imgIdArr);
    }

    private void prepareVisionApi() {
        // create vision object
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer(API_KEY));

        vision = visionBuilder.build();
    }

    private void populateListView() {
        // get hashtag list and populate it
        ListView listView;
        listView = findViewById(R.id.hashtagList);
        HashtagListView hashtagListView = createHashtagList();
        listView.setAdapter(hashtagListView);
    }

    private void removeSpinner() {
        ProgressBar spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    private String formatHashtag(String tag) {
        StringBuilder newTag = new StringBuilder("#");
        String words[] = tag.split(" ");

        for (String word : words) {
            String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
            newTag.append(capitalizedWord);
        }

        return newTag.toString();
    }

    /**
     * Background task to request tags from the Google Vision API
     */
    private class RequestHashtagsTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... filePaths) {
            // convert photo to byte array
            //InputStream inputStream = getResources().openRawResource(R.drawable.test_img);

            byte[] photoData;

            try {
                File file = new File(filePaths[0]);
                InputStream inputStream = new FileInputStream(file);

                photoData = IOUtils.toByteArray(inputStream);
                inputStream.close();

                Image inputImage = new Image();
                inputImage.encodeContent(photoData);

                Feature desiredFeature = new Feature();
                desiredFeature.setType("LABEL_DETECTION");

                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));

                BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));

                BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();
                List<AnnotateImageResponse> responses = batchResponse.getResponses();

                for (AnnotateImageResponse res : responses) {
                    for (EntityAnnotation annotation : res.getLabelAnnotations()) {
                        hashtags.add(annotation.getDescription());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            populateListView();

            removeSpinner();
        }
    }
}
