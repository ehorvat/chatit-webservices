package com.chatit.model;

import java.sql.Timestamp;

/**
 * @author Eric Horvat
 * 
 */
public class Report {
	
	int rid;

	String reporter;
	
	String reportee;

	String tstamp;

	String message;

	int downvotes;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	boolean removed;
	
	String channel;
	
	int type;

	public Report(String reporter, String reportee, String tstamp, String message, String channel, int type) {
		this.reporter = reporter;
		this.reportee = reportee;
		this.tstamp = tstamp;
		this.channel = channel;
		this.type = type;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public String getReporter() {
		return reporter;
	}

	public String getReportee() {
		return reportee;
	}

	public String getTstamp() {
		return tstamp;
	}

	public String getMessage() {
		return message;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public void setReportee(String reportee) {
		this.reportee = reportee;
	}

	public void setTstamp(String tstamp) {
		this.tstamp = tstamp;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	

}
