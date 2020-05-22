package com.sungfamily.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review
{
    @SerializedName("recipient_fb_token")
    @Expose
    private String recipientFbToken;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("reviewed_at")
    @Expose
    private String reviewedAt;

    private static Review instance;
    public static Review getInstance()
    {
        synchronized (Review.class)
        {
            if(instance == null)
                instance = new Review();

            return instance;
        }
    }

    public void clear()
    {
        this.userName = "";
        this.rating = 0;
        this.comment = "";
    }

    public String getRecipientFbToken() {
        return recipientFbToken;
    }

    public void setRecipientFbToken(String recipientFbToken) {
        this.recipientFbToken = recipientFbToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(String reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

}
