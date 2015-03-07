package com.chatit.model;

import java.util.ArrayList;

public class Email {
	

	String address;
		
	boolean isActivated;
	
	String emailStatus;
	
	ArrayList<Channel> channels;
	
	public Email(String address, boolean isActivated, String email_status){
		this.address=address;
		this.isActivated=isActivated;
		this.emailStatus=email_status;
	}
	

	public String getEmailStatus() {
		return emailStatus;
	}


	public void setEmailStatus(String emailStatus) {
		this.emailStatus = emailStatus;
	}


	public ArrayList<Channel> getChannels() {
		return channels;
	}


	public void setChannels(ArrayList<Channel> channels) {
		this.channels = channels;
	}


	public String getAddress() {
		return address;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	

}
