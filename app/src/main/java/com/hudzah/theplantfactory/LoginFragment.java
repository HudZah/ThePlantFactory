package com.hudzah.theplantfactory;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class LoginFragment extends Fragment {

    EditText usernameEditText;
    EditText passwordEditText;
    TextView errorTextView;
    NavController navController;
    FrameLayout layout;
    LoadingDialog loadingDialog;
    Button button;
    private static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        usernameEditText = (EditText) view.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        errorTextView = (TextView) view.findViewById(R.id.errorTextView);
        layout = (FrameLayout) view.findViewById(R.id.layout);
        button = (Button) view.findViewById(R.id.button);
        loadingDialog = new LoadingDialog(getActivity());

        ParseUser.logOut();



        checkAlreadyLoggedIn();

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

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

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            loadingDialog.dismissDialog();
            errorTextView.setText("A username and password are required");
        }
        else{
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null && e == null){
                        Log.i(TAG, "done: Logged in");
                        // clear backstack of fragments
                        navController.navigate(R.id.action_loginFragment_to_mainFragment);
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

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void checkAlreadyLoggedIn(){
        if(ParseUser.getCurrentUser() != null){
            navController.navigate(R.id.action_loginFragment_to_mainFragment);
        }
    }
}
