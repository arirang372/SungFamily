package com.sungfamily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.internal.LinkedTreeMap;
import com.sungfamily.models.HttpCourierResponse;
import com.sungfamily.models.User;
import com.sungfamily.rest.RemoteService;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {
    private EditText txtUserName;
    private EditText txtPassword;
    private Button btnSignIn;
    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpToolbar();
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.email_sign_in_button);
        txtUserName.setText("hanna@gmail.com");
        txtPassword.setText("m12345");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                login();
            }
        });
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Geico Mobile");
    }


    public void showProgress() {
        if (dialog == null)
            dialog = new ProgressDialog(this);
        dialog.setTitle("Loading ... Please wait ...");
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void login() {
        final User user = new User();
        user.setEmail(txtUserName.getText().toString());
        user.setUserPassword(txtPassword.getText().toString());
        RemoteService remoteService = RemoteService.Factory.getInstance(this);
        remoteService.logInUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<HttpCourierResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<HttpCourierResponse> response) {
                        hideProgress();
                        if (response.isSuccessful()) {
                            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) response.body().result;
                            user.setFullName((String) map.get("FullName"));
                            double id = (double) map.get("Id");
                            user.setId((int) id);
                            Intent intent = new Intent(LogInActivity.this, ChatActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        } else {
                            Utils.toast(LogInActivity.this, response.body().getResponseMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Utils.toast(LogInActivity.this, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
