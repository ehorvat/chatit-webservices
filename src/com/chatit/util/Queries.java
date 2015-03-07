package com.chatit.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

public class Queries {
	
	public static final String ACTION_LOGIN = "login";
	
	public static final String ACTION_REGISTER = "register";
	
	public static final String ACTION_CHANGE_PASSWORD = "change";
	
	public static DBConnect connection;
	
	public static Connection c;
		
	public static Connection dbc() throws NamingException, SQLException,ServletException {
		try {
			connection = new DBConnect("classroomhero", null);
			
			c = connection.connect();
		}

		catch (Exception e) {
			throw new ServletException(e);
		}
		return c;
	}
	
	/////////////////
	//
	// Login Queries
	//
	/////////////////
	public static final String LOOK_UP_USER = "Select * From \"User\" where base_email=?";
	
	public static final String LOOK_UP_USER_BY_ID = "Select * from \"User\" where uid=?";
	
	public static final String LOOK_UP_USER_REQUEST = "Select * From \"Password_Reset_Requests\" where \"uid\"=?";

		
	/////////////////////
	//
	// Register Queries
	//
	/////////////////////
	public static final String REGISTER_USER = "insert into \"User\" (\"base_email\", \"salt\", \"hash\", \"createdAt\", \"isActivated\", \"token\", \"expires\") values(?,?,?,?,?,?,?) RETURNING \"uid\";";
	
	public static final String REGISTER_EMAIL = "insert into \"Emails\" (registered_email, \"uid\") values (?,?)";
	
	/////////////////////
	//
	// Activation Queries
	//
	/////////////////////
	
	public static final String VERIFY_TOKEN = "select \"uid\", \"token\", \"expires\" from \"User\" where \"token\"=?";
	
	public static final String VERIFY_EMAIL_TOKEN = "select * from \"Add_Email_Request\" where \"token\"=?;";

	public static final String ACTIVATE_ACCOUNT = "update \"User\" set \"isActivated\"=TRUE where \"uid\"=?";
	
	//public static final String ACTIVATE_EMAIL = "update \"Emails\" set \"isActivated\"=TRUE where \"uid\"=?";
	
	public static final String NEW_EMAIL_REQUEST = "insert into \"Add_Email_Request\" (\"uid\",pending_email, token, expires) values(?,?,?,?);";
	
	public static final String DELETE_EMAIL_REQUEST = "delete from \"Add_Email_Request\" where token=?";
	
	////////////////////////////////////
	//
	// Password Recovery and Management
	//
	////////////////////////////////////
	
	public static final String SET_PASS_BY_ID = "update \"User\" set \"hash\" = ?, \"salt\" = ? where \"uid\" = ?;";
	
	public static final String SET_PASS_BY_EMAIL = "update \"User\" set \"hash\" = ?, \"salt\" = ? where \"base_email\" = ?;";
	
	public static final String SET_PASS_BY_UID = "update \"User\" set \"hash\" = ?, \"salt\" = ? where \"uid\" = ?;";
	
	public static final String CREATE_REQUEST = "INSERT into \"Password_Reset_Requests\" (rid, tstamp, uid) VALUES (?,?,?)";
	
	public static final String VERIFY_RESET_TOKEN = "Select * from \"Password_Reset_Requests\" where \"uid\"=?";
	
	public static final String DELETE_REQUEST = "delete from \"Password_Reset_Requests\" where \"uid\"=?";
	
	
	////////////////////////////////////
	//
	// Channel Queries
	//
	////////////////////////////////////
	
	public static final String INSERT_CHANNEL = "insert into \"Channels\" (channel_name, uid, email, eid) values (?,?,?,null)";
	
	public static final String INSERT_CHANNEL_WITH_EID = "insert into \"Channels\" (channel_name, uid, email, eid) values (?,?,?,?)";

	public static final String SELECT_CHANNEL = "select * from \"Channels\" where channel_name=? and \"email\"=?";
	
	public static final String SELECT_ALL_CHANNELS = "Select * from \"Channels\" where uid=? ";
	
	public static final String SELECT_CHANNELS_BY_EMAIL = "Select * from \"Channels\" where uid=? and email=? ";
	
	public static final String DELETE_CHANNEL = "delete from \"Channels\" where channel_name=? and uid=? and email=?";
	
	////////////////////////////////////
	//
	// Email Queries
	//
	////////////////////////////////////
	
	public static final String EMAIL_LOOK_UP = "select * from \"User\" where base_email=?";
	
	public static final String LOOK_IN_EMAILS = "select * from \"Emails\" where registered_email=?";
	
	public static final String CHECK_EMAIL = "select * from \"Emails\" where registered_email=? and uid=?";
	
	public static final String ADD_NEW_EMAIL = "insert into \"Emails\" (registered_email,\"uid\") values (?, ?)";
	
	public static final String GET_PENDING_EMAILS = "select pending_email from \"Add_Email_Request\" where uid=?";
	
	public static final String GET_REGISTERED_EMAILS = "select registered_email from \"Emails\" where uid=?";
	
	public static final String DELETE_EMAIL = "delete from \"Emails\" where registered_email=? and uid=?";
	
	public static final String DELETE_FROM_PENDING = "delete from \"Add_Email_Request\" where pending_email=? and uid=?";
	
}
