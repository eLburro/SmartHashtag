package hashtag.kth.se.smarthashtag.api;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hashtag.kth.se.smarthashtag.CreateCaptionActivity;

public class InstagramAPI {
    public static final String REDIRECT_URI = "http://der-esel.ch";
    public static final String CLIENT_ID = "254d133fb83641f1879c1a5e4053a2c0";
    public static final String AUTH_URL = "https://www.instagram.com/oauth/authorize/";
    public static String accessToken = null;

    private final String REQUEST_TAG_URL = "https://api.instagram.com/v1/tags/";
    private CreateCaptionActivity createCaptionActivity;

    public InstagramAPI(CreateCaptionActivity createCaptionActivity) {
        this.createCaptionActivity = createCaptionActivity;
    }

    public void getHashtagTrend() {
        // get all hashtags
        new RequestHashtagTrendTask().execute(createCaptionActivity);
    }

    public static String getRequestAccessTokenUrl() {
        return AUTH_URL + "?" +
                "client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=token" +
                "&scope=basic+public_content";
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();

            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    /**
     * Background task to request tags from the Google Vision API
     */
    private class RequestHashtagTrendTask extends AsyncTask<CreateCaptionActivity, Integer, Long> {
        private CreateCaptionActivity myActivity;

        @Override
        protected Long doInBackground(CreateCaptionActivity... activities) {
            myActivity = activities[0];

            try {
                for (String tag : myActivity.getHashtagItems().keySet()) {
                    String formattedTag = tag.replace(" ", "");
                    URL url = new URL(REQUEST_TAG_URL + formattedTag + "?access_token=" + accessToken);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    String trend = jsonObj.getJSONObject("data").getString("media_count");
                    System.out.println("tag" + formattedTag);
                    System.out.println("trend" + trend);
                    myActivity.addHashtagAndTrend(tag, Integer.valueOf(trend));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            myActivity.populateListView();
            myActivity.removeSpinner();
        }
    }
}
