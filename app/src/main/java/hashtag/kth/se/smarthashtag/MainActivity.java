package hashtag.kth.se.smarthashtag;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1337;

    ArrayList<String> imgPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check permissions
        checkPermissionREAD_EXTERNAL_STORAGE(this);

        setContentView(R.layout.activity_main);

        // TODO performance
        final RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // add gridlayout to recyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        imgPaths = MediaUtils.getImagePathsFromPhone(this);
        final MyAdapter adapter = new MyAdapter(this, imgPaths);
        adapter.populateMainImage(this, imgPaths.get(0));
        adapter.setSelectedFilePath(imgPaths.get(0));
        recyclerView.setAdapter(adapter);

        // handle generate hashtag button
        Button generateBtn = findViewById(R.id.generateBtn);
        generateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String selectedFilePath = adapter.getSelectedFilePath();
                Intent intent = new Intent(getBaseContext(), CreateCaptionActivity.class);
                intent.putExtra("selectedFilePath", selectedFilePath);
                startActivity(intent);
            }
        });
    }

    /*private ArrayList<String> getImagePaths() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";

            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    orderBy);
            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                //Store the path of the image
                imgPaths.add(cursor.getString(dataColumnIndex));
            }
            cursor.close();

            return imgPaths;
        }

        return new ArrayList<>();
    }*/

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
            }
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
