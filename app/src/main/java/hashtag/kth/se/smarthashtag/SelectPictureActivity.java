package hashtag.kth.se.smarthashtag;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import hashtag.kth.se.smarthashtag.utils.MediaUtils;

public class SelectPictureActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1337;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private ArrayList<String> imgPaths = new ArrayList<>();
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check permissions
        checkPermissionREAD_EXTERNAL_STORAGE(this);

        setContentView(R.layout.activity_select_picture);

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
        galleryAdapter = new GalleryAdapter(this, imgPaths);
        galleryAdapter.populateMainImage(this, imgPaths.get(0));
        galleryAdapter.setSelectedFilePath(imgPaths.get(0));
        recyclerView.setAdapter(galleryAdapter);

        // handle generate hashtag button
        Button generateBtn = findViewById(R.id.generateBtn);
        generateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String selectedFilePath = galleryAdapter.getSelectedFilePath();
                Intent intent = new Intent(getBaseContext(), CreateCaptionActivity.class);
                intent.putExtra("selectedFilePath", selectedFilePath);
                startActivity(intent);
            }
        });

        // handle camera button
        ImageView cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mainImageView = findViewById(R.id.main_img);
            mainImageView.setImageBitmap(imageBitmap);
        }
    }

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
