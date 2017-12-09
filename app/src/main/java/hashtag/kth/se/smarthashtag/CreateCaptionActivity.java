package hashtag.kth.se.smarthashtag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hashtag.kth.se.smarthashtag.api.GoogleVisionAPI;
import hashtag.kth.se.smarthashtag.utils.MediaUtils;

public class CreateCaptionActivity extends AppCompatActivity {
    private String selectedFilePath;
    private Map<String, Integer> hashtagItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_caption);

        // set thumbnail picture
        selectedFilePath = getIntent().getStringExtra("selectedFilePath");
        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        MediaUtils.populateImageToView(this, thumbnailImageView, selectedFilePath, 0, 0);

        // call Google vision API
        GoogleVisionAPI googleVisionAPI = new GoogleVisionAPI();
        googleVisionAPI.prepareVisionApi();
        googleVisionAPI.callRequestHashtags(this, selectedFilePath);

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
        int collectionSize = hashtagItems.size();
        String[] trendArr = new String[collectionSize];
        String[] hashtagArr = new String[collectionSize];

        // sort map
        Map<String, Integer> sortedMap = sortByComparator(hashtagItems);
        int i = 0;

        for (String hashtag : sortedMap.keySet()) {
            String trend = sortedMap.get(hashtag).toString();
            hashtagArr[i] = formatHashtag(hashtag);
            trendArr[i] = formatTrend(trend);
            i++;
        }

        return new HashtagListView(this, hashtagArr, trendArr);
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {

                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public void populateListView() {
        // get hashtag list and populate it
        ListView listView;
        listView = findViewById(R.id.hashtagList);
        HashtagListView hashtagListView = createHashtagList();
        listView.setAdapter(hashtagListView);
    }

    public void removeSpinner() {
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

    private String formatTrend(String trend) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('\'');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(Long.valueOf(trend));
    }

    public void addHashtagAndTrend(String hashtag, Integer trend) {
        hashtagItems.put(hashtag, trend);
    }

    public Map<String, Integer> getHashtagItems() {
        return hashtagItems;
    }
}
