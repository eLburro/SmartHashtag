package hashtag.kth.se.smarthashtag.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MediaUtils {

    public static ArrayList<String> getImagePathsFromPhone(Activity context) {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        ArrayList<String> imgPaths = new ArrayList<>();

        if (isSDPresent) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    orderBy);

            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                // store the path of the image
                imgPaths.add(cursor.getString(dataColumnIndex));
            }
            cursor.close();
        }

        return imgPaths;
    }

    public static void populateImageToView(Activity context, ImageView view, String imgPath, int width, int height) {
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (width == 0 && height == 0) {
            Glide.with(context).load(imgPath).into(view);
        } else {
            Glide.with(context).load(imgPath).override(width, height).into(view);
        }
    }
}
