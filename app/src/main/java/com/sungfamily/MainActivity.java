package com.sungfamily;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.sungfamily.constants.Constant;
import com.sungfamily.models.HttpCourierResponse;
import com.sungfamily.models.Review;
import com.sungfamily.rest.RemoteService;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText txtComment = findViewById(R.id.txtComment);
        txtComment.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String input = s.toString();
                if(!TextUtils.isEmpty(input))
                {
                    Review review = Review.getInstance();
                    review.setComment(input);
                    review.setUserName("John Sung");
                    review.setRecipientFbToken(Constant.RECIPIENT_FB_TOKEN);
                }
            }
        });

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
           @Override
           public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Review.getInstance().setRating( (int) ratingBar.getRating());
           }
       });


        Button btnPostReview = findViewById(R.id.btnPostReview);
        btnPostReview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Review.getInstance().setReviewedAt(Utils.getFormattedDateTime(System.currentTimeMillis()));

                RemoteService remoteService =  RemoteService.Factory.getInstance(MainActivity.this);
                remoteService.writeReview(Review.getInstance())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Response<HttpCourierResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d)
                            {

                            }

                            @Override
                            public void onNext(Response<HttpCourierResponse> response)
                            {
                                if(response.isSuccessful())
                                {
                                    log("Review was posted successfully...");
                                    Review.getInstance().clear();
                                    //startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                                }
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    private void log(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
