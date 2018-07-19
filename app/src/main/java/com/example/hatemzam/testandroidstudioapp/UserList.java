package com.example.hatemzam.testandroidstudioapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.parse.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ListView userslist;
    ArrayAdapter adapter;
    ArrayList<String> users;
    public static final int req_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userslist = (ListView) findViewById(R.id.userList);
        //userslist.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        users = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0) {
                        for (ParseUser user : objects) {
                            users.add(user.getUsername());
                        }
                        userslist.setAdapter(adapter);
                    }
                }
            }
        });

        userslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /*CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()){
                    Log.i("AppInfo", users.get(i) + "is checked");
                }else {
                    Log.i("AppInfo", users.get(i) + "is not checked");
                }*/

                Intent intent = new Intent(getApplicationContext(), UserFeed.class);
                intent.putExtra("username", users.get(i));
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.share) {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, req_code);

            return true;
        }else if (id == R.id.logout){

            ParseUser.logOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
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

                        Bitmap bitMImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selImage);
                        Log.i("AppInfo", "Image Recieved");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitMImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArr = stream.toByteArray();
                        ParseFile file = new ParseFile("image.jpg", byteArr);
                        ParseObject object = new ParseObject("Images");
                        object.put("username", ParseUser.getCurrentUser().getUsername());
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
                                }else {
                                    Toast.makeText(getApplication().getBaseContext(), "Error! Please try again...", Toast.LENGTH_LONG).show();
                                    Log.i("AppInfo", "Error! Please try again...");
                                    e.printStackTrace();
                                }
                            }
                        });

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


        //super.onActivityResult(requestCode, resultCode, data);
    }

    public void navToUsers(View view){
        Intent intent = new Intent(this, UserList.class);
        startActivity(intent);
    }

    public void toFeeds(View view){
        Intent intent = new Intent(this, FriendsFeeds.class);
        startActivity(intent);
    }
}
