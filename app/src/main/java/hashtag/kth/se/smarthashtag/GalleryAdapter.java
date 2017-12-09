package hashtag.kth.se.smarthashtag;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import hashtag.kth.se.smarthashtag.utils.MediaUtils;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<String> galleryList;
    private Activity activity;
    private String selectedFilePath;

    public GalleryAdapter(Activity activity, ArrayList<String> galleryList) {
        this.galleryList = galleryList;
        this.activity = activity;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GalleryAdapter.ViewHolder viewHolder, int i) {
        MediaUtils.populateImageToView(activity, viewHolder.img, galleryList.get(i), 220, 220);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = viewHolder.getLayoutPosition();
                ImageView mainImageView = activity.findViewById(R.id.main_img);
                MediaUtils.populateImageToView(activity, mainImageView, galleryList.get(itemPosition), 0, 0);
                setSelectedFilePath(galleryList.get(itemPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }
    }

    public void populateMainImage(Activity mainActivity, String filePath) {
        ImageView mainImageView = mainActivity.findViewById(R.id.main_img);
        MediaUtils.populateImageToView(mainActivity, mainImageView, filePath, 0, 0);
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }
}