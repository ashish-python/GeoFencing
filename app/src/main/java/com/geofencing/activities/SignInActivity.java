package com.geofencing.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geofencing.R;
import com.geofencing.constants.Endpoints;
import com.geofencing.listeners.BaseListener;
import com.geofencing.stores.TokenStore;
import com.geofencing.utils.NetworkPostRequest;

public class SignInActivity extends BaseAppCompatActivity implements BaseListener {
    private EditText usernameET;
    private EditText passwordET;
    private Button signInBtn;
    private TextView errorMessageTV;
    private EditText childPhoneET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        childPhoneET = findViewById(R.id.child_phone_et);
        signInBtn = findViewById(R.id.sign_in_btn);
        errorMessageTV = findViewById(R.id.error_message_tv);
    }

    private void setupListeners() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboard();
                signInBtn.setEnabled(false);
                signIn(usernameET.getText().toString(), passwordET.getText().toString(), childPhoneET.getText().toString());
            }
        });
    }

    private void signIn(String email, String password, String childPhone) {
        new NetworkPostRequest(this, Endpoints.SIGN_IN_URL, this::callback, Endpoints.SIGN_IN_TASK).execute(email, password, childPhone);
    }

    @Override
    public void callback(Context context, Integer status, String responseString) {
        if (responseString.equals("fail")) {
            signInBtn.setEnabled(true);
            errorMessageTV.setVisibility(View.VISIBLE);
            errorMessageTV.setText(R.string.login_error);

        } else {
            errorMessageTV.setText("");
            TokenStore.getInstance(getApplicationContext()).setUser(responseString);
            startActivity(SignInActivity.this, MainActivity.class, FINISH_CURRENT_ACTIVITY);
        }
    }

    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

