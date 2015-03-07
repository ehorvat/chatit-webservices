package com.chatit.model;

public class Channel {
	
	String channel_name;
	
	String parent_email;
	
	int channel_creator;
	
	public Channel(String channel_name, int channel_creator, String parent_email){
		this.channel_name=channel_name;
		this.parent_email=parent_email;
		this.channel_creator=channel_creator;
	}
	
	public Channel(String channel_name){
		this.channel_name=channel_name;
	}

	public String getChannel_name() {
		return channel_name;
	}

	public String getParent_email() {
		return parent_email;
	}

	public int getChannel_creator() {
		return channel_creator;
	}

	public void setChannel_creator(int channel_creator) {
		this.channel_creator = channel_creator;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	public void setParent_email(String parent_email) {
		this.parent_email = parent_email;
	}
	
}
