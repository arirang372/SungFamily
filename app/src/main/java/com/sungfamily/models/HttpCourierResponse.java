package com.sungfamily.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HttpCourierResponse<T>
{
    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("response_code")
    @Expose
    private int response_code;
    
	@Expose
	@SerializedName("response_message")
	private String response_message = "";
    
	
    @SerializedName("android_id")
    @Expose
    private String android_id;
	
    @Expose
    @SerializedName("result")
    public T result;
    
    
    public HttpCourierResponse()
    {

    }
    
    public void setResponseCode(int code)
    {
    	this.response_code = code;
    }

    
    public int getResponseCode()
    {
    	return this.response_code;
    }
    
	public String getResponseMessage()
	{
		return response_message;
	}

	public void setResponseMessage(String responseMessage)
	{
		this.response_message = responseMessage;
	}
	
	
	public String getAndroidId()
	{
		return this.android_id;
	}

	public void setAndroidId(String androidId)
	{
		this.android_id = androidId;
	}
	
	@Override
	public String toString()
	{
		Gson gson = new Gson();
		return gson.toJson(this, this.getClass());
	}

}