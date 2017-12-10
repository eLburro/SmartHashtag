package hashtag.kth.se.smarthashtag.api;

import android.os.AsyncTask;

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

import hashtag.kth.se.smarthashtag.CreateCaptionActivity;
import hashtag.kth.se.smarthashtag.api.InstagramAPI;

public class GoogleVisionAPI {
    private final String API_KEY = "AIzaSyCei2QI9MhOVHDuaVlbR-_SjCDE8nOgpiA";
    private Vision vision;

    public void prepareVisionApi() {
        // create vision object
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer(API_KEY));

        vision = visionBuilder.build();
    }

    public void callRequestHashtags(CreateCaptionActivity context, String filePath) {
        RequestHashtagsTask requestHashtagsTask = new RequestHashtagsTask(context);
        requestHashtagsTask.execute(filePath);
    }

    /**
     * Background task to request tags from the Google Vision API
     */
    private class RequestHashtagsTask extends AsyncTask<String, Integer, Long> {
        private CreateCaptionActivity activity;

        public RequestHashtagsTask(CreateCaptionActivity context) {
            this.activity = context;
        }

        @Override
        protected Long doInBackground(String... filePaths) {
            // convert photo to byte array
            byte[] photoData;

            try {
                File file = new File(filePaths[0]);
                InputStream inputStream = new FileInputStream(file);

                photoData = IOUtils.toByteArray(inputStream);
                inputStream.close();

                Image inputImage = new Image();
                inputImage.encodeContent(photoData);

                List<Feature> featureList = new ArrayList<>();
                // label detection
                Feature labelFeature = new Feature();
                labelFeature.setType("LABEL_DETECTION");
                featureList.add(labelFeature);

                /*
                // landmark detection
                Feature landmarkFeature = new Feature();
                landmarkFeature.setType("LANDMARK_DETECTION");
                featureList.add(landmarkFeature);

                // image properties detection
                Feature imgPropertiesFeature = new Feature();
                imgPropertiesFeature.setType("IMAGE_PROPERTIES");
                featureList.add(imgPropertiesFeature);

                // text detection
                Feature textFeature = new Feature();
                textFeature.setType("TEXT_DETECTION");
                featureList.add(textFeature);
                */

                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(featureList);

                BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));

                BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();
                List<AnnotateImageResponse> responses = batchResponse.getResponses();

                for (AnnotateImageResponse res : responses) {
                    for (EntityAnnotation annotation : res.getLabelAnnotations()) {
                        activity.addHashtagAndTrend(annotation.getDescription(), 0);
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

            InstagramAPI igApi = new InstagramAPI(activity);
            igApi.getHashtagTrend();
        }
    }
}
