package com.hudzah.theplantfactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    TextView errorTextView;
    RelativeLayout layout;
    LoadingDialog loadingDialog;
    Button button;
    String username;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        layout = (RelativeLayout) findViewById(R.id.layout);
        button = (Button) findViewById(R.id.button);
        loadingDialog = new LoadingDialog(this);

        checkAlreadyLoggedIn();

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN){

                    login(v);
                }
                return false;
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

    }



    private void login(View view){

        loadingDialog.startLoadingDialog();

        Log.i(TAG, "login: Clickedhu");

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            loadingDialog.dismissDialog();
            errorTextView.setText("A username and password are required");
        }
        else{
            username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null && e == null){
                        Log.i(TAG, "done: Logged in");
                        // clear backstack and move to MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    else{
                        errorTextView.setText(e.getMessage());
                    }
                }
            });

            loadingDialog.dismissDialog();
        }
    }

    public void hideKeyboard(View view){
        if(view.getId() == R.id.layout){

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if(getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void checkAlreadyLoggedIn(){
        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
