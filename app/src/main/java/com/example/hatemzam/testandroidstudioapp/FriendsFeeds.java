package com.example.hatemzam.testandroidstudioapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.parse.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsFeeds extends AppCompatActivity {

    LinearLayout friendsLayout;
    //Date lastElementDate = new Date();
    ArrayList<Bitmap> imgsPosts;
    //ArrayList<Date> imgsDates;
    ArrayList<String> imgsUsernames;
    ArrayList<String> photosDescription;
    ArrayList<String> prevImgsID;
    ArrayList<String> imgsID;
    //int pages = 0;
    int[] imgsLikes;

    ImageView img1;
    ImageView img2;
    ImageView img3;

    TextView textView4;
    TextView textView5;
    TextView textView6;

    TextView tv;
    TextView tv2;
    TextView tv3;

    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;

    Button bkBtn;
    Button nxtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_feeds);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //friendsLayout = (LinearLayout) findViewById(R.id.friendsFeedsLinearLayout);
        imgsPosts = new ArrayList<Bitmap>();
        imgsUsernames = new ArrayList<String>();
        prevImgsID = new ArrayList<String>();
        photosDescription = new ArrayList<String>();

        imgsLikes = new int[3];

        setTitle("Friends Feeds");

        img1 = (ImageView)findViewById(R.id.firstImgV);
        img2 = (ImageView)findViewById(R.id.secondImgV);
        img3 = (ImageView)findViewById(R.id.thirdImgV);

        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);

        imgsID = new ArrayList<String>();

        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);

        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btn5 = (Button) findViewById(R.id.button5);
        btn6 = (Button) findViewById(R.id.button6);
        btn7 = (Button) findViewById(R.id.button7);
        btn8 = (Button) findViewById(R.id.button8);

        bkBtn = (Button) findViewById(R.id.button12);
        nxtBtn = (Button) findViewById(R.id.nextBtn);
        if (prevImgsID.isEmpty()){
            bkBtn.setEnabled(false);
        }else{
            bkBtn.setEnabled(true);
        }

        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
        btn7.setEnabled(false);
        btn8.setEnabled(false);

        if (ParseUser.getCurrentUser().getList("following") == null){
            List<String> emptyList = new ArrayList<String>();
            ParseUser.getCurrentUser().put("following", emptyList);
        }

        reloadImgs();

    }

    public void navToUsers(View view){
        Intent intent = new Intent(this, UserList.class);
        startActivity(intent);
    }

    public void toFeeds(View view){
        Intent intent = new Intent(this, FriendsFeeds.class);
        startActivity(intent);
    }

    public void toSharePage(View view){
        Intent intent = new Intent(this, ShareActivity.class);
        startActivity(intent);
    }

    public void reloadImgs(){

        textView4.setText("");
        textView5.setText("");
        textView6.setText("");

        tv.setText("");
        tv2.setText("");
        tv3.setText("");

        img1.setImageResource(R.mipmap.ic_launcher);
        img2.setImageResource(R.mipmap.ic_launcher);
        img3.setImageResource(R.mipmap.ic_launcher);

        //bkBtn.setEnabled(false);


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("following"));
        query.addDescendingOrder("createdAt");
        //query.whereLessThanOrEqualTo("createdAt", lastElementDate);
        query.setLimit(3);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        imgsPosts.clear();
                        //imgsDates.clear();
                        imgsUsernames.clear();
                        photosDescription.clear();
                        imgsID.clear();

                        Log.i("AppInfo", "query is right");

                        int i = 0;

                        for (final ParseObject object : objects){

                            if (object.getList("likedBy") == null){
                                List<String> emptyList = new ArrayList<String>();
                                object.put("likedBy", emptyList);
                            }

                            if (object.getList("likedBy").contains(ParseUser.getCurrentUser().getUsername())){
                                imgsLikes[i] = 1;
                            }else {
                                imgsLikes[i] = 0;
                            }
                            Log.i("onClick", String.valueOf(i));
                            i = i + 1;

                            //imgsDates.add((Date) object.get("createdAt"));
                            imgsID.add(object.getObjectId().toString());
                            imgsUsernames.add(object.getString("username"));
                            prevImgsID.add(object.getObjectId());
                            photosDescription.add(object.getString("description"));
                            //Log.i("AppInfo", "imgsDates worked");
                            final ParseFile file = (ParseFile) object.get("image");
                            Log.i("AppInfo", "get data in background ...");

                            try {
                                Bitmap bitmapImg = BitmapFactory.decodeStream(file.getDataStream());
                                imgsPosts.add(bitmapImg);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        i = 0;
                        Log.i("AppInfo", String.valueOf(imgsPosts.size()));
                        //lastElementDate = imgsDates.get(imgsPosts.size()-1);
                        Log.i("AppInfo", "!!!!!!!!!!!");

                        switch (imgsPosts.size()){
                            case 1:
                                img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                                textView4.setText(imgsUsernames.get(0));
                                tv.setText(photosDescription.get(0));
                                setLikeBtn(btn3, 0);

                                break;
                            case 2:
                                img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                                img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                                textView4.setText(imgsUsernames.get(0));
                                textView5.setText(imgsUsernames.get(1));
                                tv.setText(photosDescription.get(0));
                                tv2.setText(photosDescription.get(1));
                                setLikeBtn(btn3, 0);
                                setLikeBtn(btn5, 1);
                                break;
                            case 3:
                                img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                                img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                                img3.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(2), 300, 300, false));
                                textView4.setText(imgsUsernames.get(0));
                                textView5.setText(imgsUsernames.get(1));
                                textView6.setText(imgsUsernames.get(2));
                                tv.setText(photosDescription.get(0));
                                tv2.setText(photosDescription.get(1));
                                tv3.setText(photosDescription.get(2));
                                setLikeBtn(btn3, 0);
                                setLikeBtn(btn5, 1);
                                setLikeBtn(btn7, 2);
                                break;
                            default:
                                Log.i("AppInfo", "Error with imgsPost");
                        }
                        //pages++;
                        Log.i("imgsId", String.valueOf(prevImgsID.size()));
                        Log.i("imgsId", String.valueOf(prevImgsID.size()));
                        /*for (String imgId : prevImgsID){
                            Log.i("ImgsId",imgId);
                        }*/
                    }
                }
            }
        });
    }

    public void nextImages(View view){
        Log.i("AppInfo", "nextBtn Tapped...");
        Log.i("AppInfo", String.valueOf(imgsPosts.size()));
        Log.i("AppInfo", String.valueOf("prevImgsID size: " + prevImgsID.size()));

        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
        btn7.setEnabled(false);
        btn8.setEnabled(false);
        if (prevImgsID.isEmpty()){
            bkBtn.setEnabled(false);
        }else{
            bkBtn.setEnabled(true);
        }

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("following"));
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("objectId", prevImgsID);
        query.setLimit(3);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (objects.size() > 0){
                    imgsPosts.clear();
                    imgsUsernames.clear();
                    photosDescription.clear();
                    imgsID.clear();

                    textView4.setText("");
                    textView5.setText("");
                    textView6.setText("");

                    tv.setText("");
                    tv2.setText("");
                    tv3.setText("");

                    img1.setImageResource(R.mipmap.ic_launcher);
                    img2.setImageResource(R.mipmap.ic_launcher);
                    img3.setImageResource(R.mipmap.ic_launcher);

                    Log.i("AppInfo", "query is right");
                    int i = 0;
                    for (ParseObject object : objects){

                        if (object.getList("likedBy") == null){
                            List<String> emptyList = new ArrayList<String>();
                            object.put("likedBy", emptyList);
                        }

                        if (object.getList("likedBy").contains(ParseUser.getCurrentUser().getUsername())){
                            imgsLikes[i] = 1;
                        }else {
                            imgsLikes[i] = 0;
                        }
                        Log.i("onClick", String.valueOf(i));
                        i = i + 1;

                        //imgsDates.add((Date) object.get("createdAt"));
                        imgsID.add(object.getObjectId().toString());

                        imgsUsernames.add(object.getString("username"));
                        prevImgsID.add(object.getObjectId());
                        photosDescription.add(object.getString("description"));
                        //Log.i("AppInfo", "imgsDates worked");
                        final ParseFile file = (ParseFile) object.get("image");
                        Log.i("AppInfo", "get data in background ...");

                        try {
                            Bitmap bitmapImg = BitmapFactory.decodeStream(file.getDataStream());
                            imgsPosts.add(bitmapImg);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Log.i("AppInfo", String.valueOf(imgsPosts.size()));
                    //lastElementDate = imgsDates.get(imgsPosts.size()-1);
                    Log.i("AppInfo", "!!!!!!!!!!!");

                    switch (imgsPosts.size()){
                        case 1:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            tv.setText(photosDescription.get(0));
                            setLikeBtn(btn3, 0);
                            break;
                        case 2:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            textView5.setText(imgsUsernames.get(1));
                            tv.setText(photosDescription.get(0));
                            tv2.setText(photosDescription.get(1));
                            setLikeBtn(btn3, 0);
                            setLikeBtn(btn5, 1);
                            break;
                        case 3:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                            img3.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(2), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            textView5.setText(imgsUsernames.get(1));
                            textView6.setText(imgsUsernames.get(2));
                            tv.setText(photosDescription.get(0));
                            tv2.setText(photosDescription.get(1));
                            tv3.setText(photosDescription.get(2));
                            setLikeBtn(btn3, 0);
                            setLikeBtn(btn5, 1);
                            setLikeBtn(btn7, 2);
                            break;
                        default:
                            Log.i("AppInfo", "Error with imgsPost");
                    }
                }


            }
        });



        //reloadImgs();
    }

    public void goToPrevImgs(View view){

        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
        btn7.setEnabled(false);
        btn8.setEnabled(false);
        if (prevImgsID.isEmpty()){
            bkBtn.setEnabled(false);
        }else{
            bkBtn.setEnabled(true);
        }

        prevImgsID.remove(prevImgsID.size()-1);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("following"));
        query.whereContainedIn("objectId", prevImgsID);
        query.addAscendingOrder("createdAt");
        query.setLimit(3);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0){

                    imgsPosts.clear();
                    imgsUsernames.clear();
                    photosDescription.clear();
                    imgsID.clear();

                    // photos usernames
                    textView4.setText("");
                    textView5.setText("");
                    textView6.setText("");

                    // photos descriptions
                    tv.setText("");
                    tv2.setText("");
                    tv3.setText("");

                    // initialize imgs
                    img1.setImageResource(R.mipmap.ic_launcher);
                    img2.setImageResource(R.mipmap.ic_launcher);
                    img3.setImageResource(R.mipmap.ic_launcher);

                    int i = 0;
                    for (ParseObject object : objects){

                        if (object.getList("likedBy") == null){
                            List<String> emptyList = new ArrayList<String>();
                            object.put("likedBy", emptyList);
                        }

                        if (object.getList("likedBy").contains(ParseUser.getCurrentUser().getUsername())){
                            imgsLikes[i] = 1;
                        }else {
                            imgsLikes[i] = 0;
                        }
                        Log.i("onClick", String.valueOf(i));
                        i = i + 1;

                        //imgsDates.add((Date) object.get("createdAt"));
                        imgsID.add(object.getObjectId().toString());

                        imgsUsernames.add(object.getString("username"));
                        prevImgsID.remove(object.getObjectId());
                        photosDescription.add(object.getString("description"));
                        //Log.i("AppInfo", "imgsDates worked");
                        final ParseFile file = (ParseFile) object.get("image");
                        Log.i("AppInfo", "get data in background ...");

                        try {
                            Bitmap bitmapImg = BitmapFactory.decodeStream(file.getDataStream());
                            imgsPosts.add(bitmapImg);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                    }

                    switch (imgsPosts.size()){
                        case 1:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            tv.setText(photosDescription.get(0));
                            setLikeBtn(btn3, 0);
                            break;
                        case 2:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            textView5.setText(imgsUsernames.get(1));
                            tv.setText(photosDescription.get(0));
                            tv2.setText(photosDescription.get(1));
                            setLikeBtn(btn3, 0);
                            setLikeBtn(btn5, 1);
                            break;
                        case 3:
                            img1.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(0), 300, 300, false));
                            img2.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(1), 300, 300, false));
                            img3.setImageBitmap(Bitmap.createScaledBitmap(imgsPosts.get(2), 300, 300, false));
                            textView4.setText(imgsUsernames.get(0));
                            textView5.setText(imgsUsernames.get(1));
                            textView6.setText(imgsUsernames.get(2));
                            tv.setText(photosDescription.get(0));
                            tv2.setText(photosDescription.get(1));
                            tv3.setText(photosDescription.get(2));
                            setLikeBtn(btn3, 0);
                            setLikeBtn(btn5, 1);
                            setLikeBtn(btn7, 2);
                            break;
                        default:
                            Log.i("AppInfo", "Error with imgsPost");
                    }

                }
            }
        });
    }

    public void setLikeBtn(Button btnL, int ind){
        btnL.setEnabled(true);
        if (imgsLikes[ind] == 1){
            btnL.setText("Unlike");
            //btnL.setTextColor(Color.rgb(1,1,1));
            //btnL.setBackgroundColor(Color.rgb(0,0,0));
        }else {
            btnL.setText("Like");
            //btnL.setTextColor(Color.rgb(0,0,0));
            //btnL.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    public void likeImg1(View view){
        //ParseObject imgs = new ParseObject("Images");
        Log.i("onClick", "Tapped");
        if (imgsLikes[0] == 0){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(0));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                Log.i("onClick", String.valueOf(objects.size()));
                                object.add("likedBy", ParseUser.getCurrentUser().getUsername().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[0] = 1;
                                            btn3.setText("Unlike");
                                            //btn3.setTextColor(Color.rgb(1, 1, 1));
                                            //btn3.setBackgroundColor(Color.rgb(0, 0, 0));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });


        }else if (imgsLikes[0] == 1){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(0));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                List<String> unLikeList = new ArrayList<String>();
                                unLikeList.addAll(object.<String>getList("likedBy"));
                                unLikeList.remove(ParseUser.getCurrentUser().getUsername().toString());
                                object.remove("likedBy");
                                object.addAll("likedBy", unLikeList);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[0] = 0;
                                            btn3.setText("Like");
                                            //btn3.setTextColor(Color.rgb(0, 0, 0));
                                            //btn3.setBackgroundColor(Color.rgb(1, 1, 1));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    public void likeImg2(View view){
        Log.i("onClick", "Tapped");
        if (imgsLikes[1] == 0){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(1));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                Log.i("onClick", String.valueOf(objects.size()));
                                object.add("likedBy", ParseUser.getCurrentUser().getUsername().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[1] = 1;
                                            btn5.setText("Unlike");
                                            //btn3.setTextColor(Color.rgb(1, 1, 1));
                                            //btn3.setBackgroundColor(Color.rgb(0, 0, 0));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });


        }else if (imgsLikes[1] == 1){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(1));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                List<String> unLikeList = new ArrayList<String>();
                                unLikeList.addAll(object.<String>getList("likedBy"));
                                unLikeList.remove(ParseUser.getCurrentUser().getUsername().toString());
                                object.remove("likedBy");
                                object.addAll("likedBy", unLikeList);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[1] = 0;
                                            btn5.setText("Like");
                                            //btn3.setTextColor(Color.rgb(0, 0, 0));
                                            //btn3.setBackgroundColor(Color.rgb(1, 1, 1));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    public void likeImg3(View view){
        Log.i("onClick", "Tapped");
        if (imgsLikes[2] == 0){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(2));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                Log.i("onClick", String.valueOf(objects.size()));
                                object.add("likedBy", ParseUser.getCurrentUser().getUsername().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[2] = 1;
                                            btn7.setText("Unlike");
                                            //btn3.setTextColor(Color.rgb(1, 1, 1));
                                            //btn3.setBackgroundColor(Color.rgb(0, 0, 0));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });


        }else if (imgsLikes[2] == 1){

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
            query.whereEqualTo("objectId", imgsID.get(2));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() == 1){
                            for (ParseObject object : objects) {
                                List<String> unLikeList = new ArrayList<String>();
                                unLikeList.addAll(object.<String>getList("likedBy"));
                                unLikeList.remove(ParseUser.getCurrentUser().getUsername().toString());
                                object.remove("likedBy");
                                object.addAll("likedBy", unLikeList);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            imgsLikes[2] = 0;
                                            btn7.setText("Like");
                                            //btn3.setTextColor(Color.rgb(0, 0, 0));
                                            //btn3.setBackgroundColor(Color.rgb(1, 1, 1));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

}
