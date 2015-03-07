package com.chatit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.NoToken;
import com.chatit.exceptions.TokenExpiredException;
import com.chatit.model.Channel;
import com.chatit.model.Email;
import com.chatit.model.User;
import com.chatit.util.Constants;
import com.chatit.util.Queries;

public class EmailDAO {

	
	Connection c;
	
	PreparedStatement ps = null;
	
	ResultSet rs = null;
			
	
	public EmailDAO(Connection c){
		this.c = c;
	}
	
	public void addEmail(String base_email, String new_email) throws SQLException{
		
		ps = c.prepareStatement(Queries.EMAIL_LOOK_UP);
		ps.setString(1, base_email);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			int uid = rs.getInt(1);
			
			ps = c.prepareStatement(Queries.ADD_NEW_EMAIL);
			ps.setString(1, new_email.trim());
			ps.setInt(2, uid);
			
			ps.execute();
		}
		
		
	}
	
	public void newEmailRequest(String new_email, String base_email, String token) throws SQLException, InvalidEmailException{
				
		ps = c.prepareStatement(Queries.EMAIL_LOOK_UP);
		ps.setString(1, base_email);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			
			if(rs.getString(2).trim().equals(new_email.trim())){
				throw new InvalidEmailException("Email already exists as active base channel");
			}
			
			Timestamp incrementedTstamp = new Timestamp(System.currentTimeMillis()+60*60*1000);
							
			ps = c.prepareStatement(Queries.NEW_EMAIL_REQUEST);
			ps.setInt(1, rs.getInt(1));
			ps.setString(2, new_email);
			ps.setString(3, token);
			ps.setTimestamp(4, incrementedTstamp);
			ps.execute();
			
		}
		
		ps.close();
		rs.close();
			
	}
	
	public ArrayList<Email> getUserEmails(String base_channel) throws SQLException{
		
		User user = getUserByEmail(base_channel);
		
		ArrayList<Email> emails = new ArrayList<Email>();
		
		PreparedStatement pending = c.prepareStatement(Queries.GET_PENDING_EMAILS);
		PreparedStatement registered = c.prepareStatement(Queries.GET_REGISTERED_EMAILS);
		
		pending.setInt(1, user.getUid());
		registered.setInt(1, user.getUid());
		
		rs = pending.executeQuery();
		
		Email email = null;

		while(rs.next()){
			String pending_email = rs.getString(1);
			
			email = new Email(pending_email, false, Constants.EMAIL_STATUS_PENDING);
			
			emails.add(email);
			
		}
		
		pending.close();
		
		rs = registered.executeQuery();
		
		while(rs.next()){
			String pending_email = rs.getString(1);
			
			email = new Email(pending_email, false, Constants.EMAIL_STATUS_REGISTERED);
			
			emails.add(email);
			
		}
		 
		return emails;
		
	}

	public void verifyActivationToken(String token) throws SQLException, TokenExpiredException, NoToken{
		
		ps = c.prepareStatement(Queries.VERIFY_EMAIL_TOKEN);
		ps.setString(1, token);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			
			rs.getString(2);
			
			Timestamp incrementedTstamp = new Timestamp(System.currentTimeMillis()+60*60*1000);
			
			Timestamp now = new Timestamp(System.currentTimeMillis());

			if(!now.before(incrementedTstamp)){
				
				ps = c.prepareStatement(Queries.DELETE_EMAIL_REQUEST);
				ps.setString(1, token);
				ps.execute();
				
				throw new TokenExpiredException("Request Expired");
			}
	
			
		}else{
			throw new NoToken("No Token");
		}
		
		ps = c.prepareStatement(Queries.DELETE_EMAIL_REQUEST);
		ps.setString(1, token);
		ps.execute();
		
		
	}
	
	public User getUserByEmail(String base_email) throws SQLException{
		
		User user = null;
		
		ps = c.prepareStatement(Queries.EMAIL_LOOK_UP);
		ps.setString(1, base_email);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			user = new User(rs.getInt(1),rs.getString(2));
		}
		return user;
	}
	
	public void checkEmailActivation(String base_email, String new_email) throws SQLException, InvalidEmailException{
		
		User user = getUserByEmail(base_email);
		
		ps = c.prepareStatement(Queries.CHECK_EMAIL);
		ps.setString(1, new_email.trim());
		ps.setInt(2, user.getUid());
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			;
		}else{
			throw new InvalidEmailException("Email not activated");
		}
		
	}
	
	public void deleteEmail(String base_email, String email_to_delete) throws SQLException{
		
		User user = getUserByEmail(base_email);
		
		ps = c.prepareStatement(Queries.LOOK_IN_EMAILS);
		ps.setString(1, email_to_delete);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			ps = c.prepareStatement(Queries.DELETE_EMAIL);
			
		}else{
			ps = c.prepareStatement(Queries.DELETE_FROM_PENDING);
		}
		

		ps.setString(1, email_to_delete);
		ps.setInt(2, user.getUid());
		
		ps.execute();
		
		
	}
}
