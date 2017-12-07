package hashtag.kth.se.smarthashtag;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HashtagListView extends ArrayAdapter<String> {
    private String[] hashtag;
    private String[] trend;
    private Integer[] imgId;
    private Activity context;

    public HashtagListView(Activity context, String[] hashtag, String[] trend, Integer[] imgId) {
        super(context, R.layout.hashtag_layout, hashtag);

        this.context = context;
        this.hashtag = hashtag;
        this.trend = trend;
        this.imgId = imgId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            view = layoutInflater.inflate(R.layout.hashtag_layout, null, true);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivwIcon.setImageResource(imgId[position]);
        viewHolder.tvwHashtag.setText(hashtag[position]);
        viewHolder.tvwTrend.setText(trend[position]);

        // click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView hashtagView = view.findViewById(R.id.textViewhashtag);
                String hashtagText = hashtagView.getText().toString();
                ImageView iconView = view.findViewById(R.id.imageViewhashtag);

                toggleHashtag(hashtagText, iconView);
            }
        });

        return view;
    }

    // TODO: Remove only the correct text!
    public void toggleHashtag(String hashtag, ImageView iconView) {
        EditText captionField = context.findViewById(R.id.captionField);
        String captionText = captionField.getText().toString();
        String hashtagText = "\n" + hashtag;

        if (captionText.contains(hashtag)) {
            // remove hashtag
            captionText = captionText.replace(hashtagText, "");
            captionField.setText(captionText);

            // change to plus icon
            iconView.setImageResource(R.drawable.plus);

        } else {
            // add hashtag
            captionField.getText().append(hashtagText);

            // change to minus icon
            iconView.setImageResource(R.drawable.minus);
        }
    }

    class ViewHolder {
        TextView tvwHashtag;
        TextView tvwTrend;
        ImageView ivwIcon;

        ViewHolder(View v) {
            tvwHashtag = v.findViewById(R.id.textViewhashtag);
            tvwTrend = v.findViewById(R.id.textViewtrend);
            ivwIcon = v.findViewById(R.id.imageViewhashtag);
        }
    }
}
