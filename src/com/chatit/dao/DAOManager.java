package com.chatit.dao;

import java.sql.Connection;

public class DAOManager {
	
	  protected Connection connection = null;
	  
	  protected ChannelDAO channelDao  = null;
	  
	  protected UserDAO userDao = null;
	  
	  protected EmailDAO emailDao = null;
	  
	  protected ReportDAO reportDao = null;

	  public DAOManager(Connection connection){
	    this.connection = connection;
	  }

	  public ChannelDAO getChannelDao(){
	    if(this.channelDao == null){
	      this.channelDao = new ChannelDAO(this.connection);
	    }
	    return this.channelDao;
	  }
	  
	  public UserDAO getUserDao(){
		   if(this.userDao == null){
			      this.userDao = new UserDAO(this.connection);
			    }
			    return this.userDao;
	  }
	  
	  public EmailDAO getEmailDao(){
		  if(this.emailDao == null){
			  this.emailDao = new EmailDAO(this.connection);
		  }
		  return this.emailDao;
	  }
	  
	  public ReportDAO getReportDao(){
		  if(this.reportDao == null){
			  this.reportDao = new ReportDAO(this.connection);
		  }
		  return this.reportDao;
	  }
	  
}
