package com.example.hatemzam.testandroidstudioapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

public class UserFeed extends AppCompatActivity {

    LinearLayout linearLayout;
    GridLayout gridLayout;
    String activeUsername = "";
    Boolean follow = false;
    Button followButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        //linearLayout = (LinearLayout)findViewById(R.id.linearLayOut);
        gridLayout = (GridLayout)findViewById(R.id.gridLay1);
        //follow = true;
        gridLayout.setColumnCount(2);

        Intent intent = getIntent();
        activeUsername = intent.getStringExtra("username");
        Log.i("AppInfo", activeUsername);
        setTitle(activeUsername + "'s Feeds");

        followButton = (Button) findViewById(R.id.followBtn);

        if (ParseUser.getCurrentUser().getList("following") == null){
            List<String> emptyList = new ArrayList<String>();
            ParseUser.getCurrentUser().put("following", emptyList);
        }

        if (ParseUser.getCurrentUser().getList("following").contains(activeUsername)){
            follow = true;
            followButton.setText("Unfollow");
        }

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereEqualTo("username", activeUsername);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){

                            final ParseFile file = (ParseFile) object.get("image");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null){
                                        Bitmap bitmapImg = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ImageView imageView = new ImageView(getApplicationContext());
                                        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmapImg, 300, 300, false));
                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));
                                        gridLayout.addView(imageView, 300, 300);
                                    }
                                }
                            });

                        }

                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_feed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.backToUserList) {

            Intent i = new Intent(getApplicationContext(), UserList.class);
            startActivity(i);

            return true;
        }else if (id == R.id.chat){
            Intent i = new Intent(getApplicationContext(), ChatActivity.class);
            i.putExtra("chatUsername", activeUsername);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void followOrUnfollow(View view){
        if (follow == true){
            List<String> unFollowList = new ArrayList<String>();
            unFollowList.addAll(ParseUser.getCurrentUser().<String>getList("following"));
            unFollowList.remove(activeUsername);
            ParseUser.getCurrentUser().remove("following");
            ParseUser.getCurrentUser().addAll("following", unFollowList);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        followButton.setText("Follow");
                        follow = false;
                    }
                }
            });
        }else{
            ParseUser.getCurrentUser().add("following", activeUsername);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    followButton.setText("Unfollow");
                    follow = true;
                }
            });
        }
    }
}

