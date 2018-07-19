package com.example.hatemzam.testandroidstudioapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeUser = "";
    ArrayList<String> messages = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ListView msgsList;

    public void sendMessage(View view){

        final EditText chatEditTxt = (EditText)findViewById(R.id.chatTxt);

        ParseObject msg = new ParseObject("Message");
        msg.put("sender", ParseUser.getCurrentUser().getUsername());
        msg.put("recipient", activeUser);
        msg.put("message", chatEditTxt.getText().toString());
        msg.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    //Toast.makeText(ChatActivity.this, "Message has been sent", Toast.LENGTH_LONG).show();
                    messages.add("You: " + chatEditTxt.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    chatEditTxt.setText("");
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        activeUser = intent.getStringExtra("chatUsername");
        setTitle("Chat with " + activeUser);

        msgsList = (ListView)findViewById(R.id.msgsListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        msgsList.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        ArrayList<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        messages.clear();
                        for (ParseObject msg : objects){
                            String msgContent = msg.getString("message");
                            if (msg.getString("sender").equals(ParseUser.getCurrentUser().getUsername())){
                                msgContent = "You: " + msgContent;
                            }else{
                                msgContent = activeUser + ": " + msgContent;
                            }
                            messages.add(msgContent);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
}
