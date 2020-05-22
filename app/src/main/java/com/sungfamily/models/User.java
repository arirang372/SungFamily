package com.sungfamily.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
	@SerializedName("Id")
	@Expose
	private Integer id;
	@SerializedName("FullName")
	@Expose
	private String fullName;
	@SerializedName("Email")
	@Expose
	private String email;
	@SerializedName("UserPassword")
	@Expose
	private String userPassword;
	@SerializedName("FirebaseToken")
	@Expose
	private String firebaseToken;
	@SerializedName("IsLogin")
	@Expose
	private Integer isLogin;

	public Integer getId() {
	return id;
	}

	public void setId(Integer id) {
	this.id = id;
	}

	public String getFullName() {
	return fullName;
	}

	public void setFullName(String fullName) {
	this.fullName = fullName;
	}

	public String getEmail() {
	return email;
	}

	public void setEmail(String email) {
	this.email = email;
	}

	public String getUserPassword() {
	return userPassword;
	}

	public void setUserPassword(String userPassword) {
	this.userPassword = userPassword;
	}

	public String getFirebaseToken() {
	return firebaseToken;
	}

	public void setFirebaseToken(String firebaseToken) {
	this.firebaseToken = firebaseToken;
	}

	public Integer getIsLogin() {
	return isLogin;
	}

	public void setIsLogin(Integer isLogin) {
	this.isLogin = isLogin;
	}
}
