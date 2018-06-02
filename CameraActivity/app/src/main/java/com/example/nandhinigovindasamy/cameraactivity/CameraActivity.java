package com.example.nandhinigovindasamy.cameraactivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class CameraActivity extends AppCompatActivity {
    Context context;
    private String[] title = {
            "View Profile",
            "Take Photo",
            "Upload Photo",

    };
    private static final int TAKE_PICTURE=1;
    private static final int GET_FROM_GALLERY=2;
    private static  final  int PIC_CROP=3;
    private  Uri selectedImage;
    ImageView textView;
    CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context=this;
       textView=(ImageView) findViewById(R.id.helloText);
       cropImageView=(CropImageView) findViewById(R.id.cropImageView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder.setItems(title, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0: {
                                        Toast.makeText(context, "View Profile", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    case 1: {


                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(intent,TAKE_PICTURE);
                                            break;



                                    }
                                    case 2:{
                                        Toast.makeText(context, "Upload Profile", Toast.LENGTH_LONG).show();
                                        //Intent intent=;
                                       // intent.setType("image/*");
                                       // intent.setAction(Intent.ACTION_GET_CONTENT);
                                        //startActivityForResult(Intent.createChooser(intent,"Select Picture"),REQUEST_CODE);
                                        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),GET_FROM_GALLERY);

                                    }

                                }
                            }
                        });
                //
                builder.show();

            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(context,"Sorry!You Have No Permission to Access Camera",Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (resultCode == RESULT_OK) {

            if (requestCode == TAKE_PICTURE) {
                //Uri bmp=intent.getData();
                //performCrop(bmp);
                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                selectedImage = getImageUri(CameraActivity.this, photo);
                performCrop();
               // textView.setImageBitmap(photo);

            }
            if (requestCode == GET_FROM_GALLERY) {
                 selectedImage = intent.getData();
                performCrop();

                // start picker to get image for cropping and then use the image in cropping activity
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);*/

// start cropping activity for pre-acquired image saved on the device

                //cropImageView.setImageUriAsync(selectedImage);






            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    /*Bitmap cropped = cropImageView.getCroppedImage();
                    textView.setImageBitmap(cropped);*/
                    Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    textView.setImageBitmap(bmp);

                } catch (Exception e) {

                }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }
     private void performCrop(){
         CropImage.activity(selectedImage)
                 .start(this);
     }
     private Uri getImageUri(Context context,Bitmap bmp){
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
         String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "Title", null);
         return Uri.parse(path);
     }
}
