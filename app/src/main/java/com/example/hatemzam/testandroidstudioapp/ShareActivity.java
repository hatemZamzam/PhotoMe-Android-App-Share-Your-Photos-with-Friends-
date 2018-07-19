package com.example.hatemzam.testandroidstudioapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ShareActivity extends AppCompatActivity {

    ImageView photoImgView;
    public static final int req_code = 1;
    Bitmap bitMImg;
    EditText photoDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        photoDescription = (EditText) findViewById(R.id.photoDescribe);
        photoImgView = (ImageView) findViewById(R.id.photoToPost);
        photoImgView.setImageResource(R.drawable.addphoto);

        photoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, req_code);
            }
        });

        setTitle("Share Photo");
    }

    public void cancelShare(View view){
        Intent intent = new Intent(this, FriendsFeeds.class);
        startActivity(intent);
    }

    public void postPhoto(View view){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitMImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArr = stream.toByteArray();
        ParseFile file = new ParseFile("image.jpg", byteArr);
        ParseObject object = new ParseObject("Images");
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("description", photoDescription.getText().toString());
        object.put("image", file);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        object.setACL(acl);

        Log.i("AppInfo", "before saveInBG");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(getApplication().getBaseContext(), "Image Posted Successfully", Toast.LENGTH_LONG).show();
                    Log.i("AppInfo", "Image Posted Successfully");
                    Intent intent = new Intent(ShareActivity.this, FriendsFeeds.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplication().getBaseContext(), "Error! Please try again...", Toast.LENGTH_LONG).show();
                    Log.i("AppInfo", "Error! Please try again...");
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if((requestCode == req_code)){

                Log.i("AppInfo", "Right REQUEST CODE");

                if ((resultCode == RESULT_OK) && (data != null)){
                    Uri selImage = data.getData();
                    try {

                        bitMImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selImage);
                        photoImgView.setImageBitmap(bitMImg);

                    } catch (IOException e) {
                        Toast.makeText(getApplication().getBaseContext(), "Error! Please try again...", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        Log.i("AppInfo", "ERROR!!!");
                    }
                }

            }else {
                Log.i("AppInfo", "WRONG REQUEST CODE!!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("AppInfo", "ERROR in Try_Catch!!!");
        }

    }
}
