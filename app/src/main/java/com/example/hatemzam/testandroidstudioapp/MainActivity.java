package com.example.hatemzam.testandroidstudioapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText username;
    EditText password;
    TextView changeMod;
    Boolean signUpModActive;
    Button signUpBtn;
    ImageView insta;
    RelativeLayout relLay;
    ConstraintLayout constraintLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this);

        signUpModActive = true;
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        changeMod = (TextView) findViewById(R.id.changeMode);
        signUpBtn = (Button) findViewById(R.id.button8);
        insta = (ImageView) findViewById(R.id.imageView9);
        relLay = (RelativeLayout) findViewById(R.id.relLayOut);
        constraintLay = (ConstraintLayout) findViewById(R.id.constraintLay);

        changeMod.setOnClickListener(this);
        username.setOnKeyListener(this);
        password.setOnKeyListener(this);
        insta.setOnClickListener(this);
        relLay.setOnClickListener(this);
        constraintLay.setOnClickListener(this);

        if(ParseUser.getCurrentUser() != null){
            showUserList();
        }

    }


    public void signUpOrSignIn(View view){
        Log.i("AppInfo", String.valueOf(username.getText()));
        Log.i("AppInfo", String.valueOf(password.getText()));

        if(signUpModActive == true) {
            ParseUser user = new ParseUser();
            user.setUsername(String.valueOf(username.getText()));
            user.setPassword(String.valueOf(password.getText()));
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("AppInfo", "Signup Successful");
                        showUserList();
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            ParseUser.logInInBackground(String.valueOf(username.getText()), String.valueOf(password.getText()), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null){
                        Log.i("logIn", "Successful");
                        showUserList();

                    }else{
                        Log.i("logIn", "Failed");
                        Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.changeMode){
            //Log.i("AppInfo", "change Sign Up Mode");
            if(signUpModActive == true){
                signUpModActive = false;
                signUpBtn.setText("Log In");
                changeMod.setText("Sign Up");
            }else{
                signUpModActive = true;
                signUpBtn.setText("Sign Up");
                changeMod.setText("Log In");
            }
        }else if(view.getId() == insta.getId() || view.getId() == relLay.getId() || view.getId() == constraintLay.getId()){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if(i == KeyEvent.KEYCODE_ENTER &&  keyEvent.getAction() == keyEvent.ACTION_DOWN){
            signUpOrSignIn(view);
        }

        return false;
    }

    public void showUserList(){
        Intent i = new Intent(getApplicationContext(), FriendsFeeds.class);
        startActivity(i);
    }

}
