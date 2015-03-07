package com.chatit.model;

public class User {

	public int uid;
	
	public String email;
	
	boolean isActivated;
	
	String token;
	
	public User(int uid, String email, boolean isActivated, String token){
		this.uid=uid;
		this.email=email;
		this.isActivated=isActivated;
		this.token=token;
	}
	
	public User(int uid, String email){
		this.uid=uid;
		this.email=email;
	}

	public int getUid() {
		return uid;
	}

	public String getEmail() {
		return email;
	}

	public boolean isActivated() {
		return isActivated;
	}
	
	public String getToken(){
		return token;
	}
	
}
