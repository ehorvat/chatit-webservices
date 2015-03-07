package com.chatit.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.HasPendingRequestException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.exceptions.TokenExpiredException;
import com.chatit.model.User;
import com.chatit.util.PasswordManager;
import com.chatit.util.Queries;

public class UserDAO {

	Connection c;

	PreparedStatement ps = null;

	ResultSet rs = null;

	int uid;

	User user;

	public UserDAO(Connection c) {
		this.c = c;
	}
	
	public User getUser(){
		return user;
	}

	public void validate(String email, String password, String action,
			String token) throws SQLException, InvalidEmailException,
			NoSuchAlgorithmException, InvalidPasswordException,
			InvalidKeySpecException, AccountNotActivatedException {

		ps = c.prepareStatement(Queries.LOOK_UP_USER);
		ps.setString(1, email);

		rs = ps.executeQuery();

		if (rs.next()) {
			// Email already exists
			if (action.equals(Queries.ACTION_LOGIN)) {

				// User is logging in, validate password as well
				PasswordManager pm = new PasswordManager();
				

				if (!rs.getBoolean(6)) {
					throw new AccountNotActivatedException("Account Not Activated");
				}

				if (!pm.validatePassword(password, rs.getString(4))) {
					throw new InvalidPasswordException("Invalid Password");
				}

				user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
			}

			if (action.equals(Queries.ACTION_REGISTER)) {
				// User is trying to register an already valid email address
				throw new InvalidEmailException("Email already exists");
			}

		} else {

			if (action.equals(Queries.ACTION_LOGIN)) {
				// User is logging in, and new records found with matching email
				
				PreparedStatement ps1 = c.prepareStatement(Queries.LOOK_IN_EMAILS);
				ps1.setString(1, email);
				
				ResultSet rs1 = ps1.executeQuery();
				
				if(rs1.next()){
					int uid = rs1.getInt(2);
					
					PreparedStatement ps2 = c.prepareStatement(Queries.LOOK_UP_USER_BY_ID);
					ps2.setInt(1, uid);

					ResultSet rs2 = ps2.executeQuery();
										
					if(rs2.next()){
						user = new User(rs2.getInt(1), rs2.getString(2), rs2.getBoolean(6), null);
					}
					
				}else{
					throw new InvalidEmailException("Email does not exist");
				}
			}

			if (action.equals(Queries.ACTION_REGISTER)) {
				// Email does not exist on registration, create user.
				registerUser(email, password, token);
			}

			if (action.equals(Queries.ACTION_CHANGE_PASSWORD)) {
				// No email found with given password
				throw new InvalidEmailException("Email not found");
			}

		}

		ps.close();
		rs.close();

	}

	public void registerUser(String email, String password, String token)
			throws NoSuchAlgorithmException, SQLException {

		PasswordManager pm = new PasswordManager();

		// Create time stamp 30mins ahead of current time
		java.sql.Timestamp expiration = new java.sql.Timestamp(
				new java.util.Date(System.currentTimeMillis() + 30 * 60 * 1000)
						.getTime());

		// Secure password
		String hashedPassword = pm.encodePassword(password);

		ps = c.prepareStatement(Queries.REGISTER_USER);
		ps.setString(1, email);
		ps.setString(2, pm.genSalt);
		ps.setString(3, hashedPassword);
		ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		ps.setBoolean(5, false);
		ps.setString(6, token);
		ps.setTimestamp(7, expiration);
		ps.execute();

	}

	public void resetPassword(String email, String temp)
			throws NoSuchAlgorithmException, SQLException,
			InvalidEmailException {

		PasswordManager pm = new PasswordManager();

		String hashedPassword = pm.encodePassword(temp);

		ps = c.prepareStatement(Queries.SET_PASS_BY_EMAIL);
		ps.setString(1, hashedPassword);
		ps.setString(2, pm.genSalt);
		ps.setString(3, email);

		int r = ps.executeUpdate();
		if (r <= 0) {
			throw new InvalidEmailException("Email does not exist");
		}

		ps.close();

	}

	public void changePassword(int uid, String newPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			SQLException, InvalidEmailException, InvalidPasswordException,
			AccountNotActivatedException {

		PasswordManager pm = new PasswordManager();

		String hashedPassword = pm.encodePassword(newPassword);

		PreparedStatement ps = c.prepareStatement(Queries.SET_PASS_BY_ID);
		ps.setString(1, hashedPassword);
		ps.setString(2, pm.genSalt);
		ps.setInt(3, uid);

		ps.execute();

		ps = c.prepareStatement(Queries.DELETE_REQUEST);
		ps.setInt(1, uid);

		ps.execute();

		ps.close();

	}

	public void addRequest(String email, String token) throws SQLException,
			NoSuchAlgorithmException, HasPendingRequestException,
			InvalidEmailException {

		PasswordManager pm = new PasswordManager();

		String hashedPassword = pm.encodePassword(token);

		if (userExists(email)) {
			uid = rs.getInt(1);
			if (!hasRequest(uid)) {
				ps = c.prepareStatement(Queries.CREATE_REQUEST);
				ps.setString(1, hashedPassword);
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setInt(3, uid);
				ps.execute();
			} else {
				throw new HasPendingRequestException(
						"This email has a pending request");
			}

		} /*else {
			
			
			ps = c.prepareStatement(Queries.LOOK_IN_EMAILS);
			ps.setString(1, email);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				if (!hasRequest(rs.getInt(1))) {
					ps = c.prepareStatement(Queries.CREATE_REQUEST);
					ps.setString(1, hashedPassword);
					ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
					ps.setInt(3, uid);
					ps.execute();
				} else {
					throw new HasPendingRequestException(
							"This email has a pending request");
				}
			}
			
			throw new InvalidEmailException("User doesn't exist");
		}*/

	}

	public void verifyToken(int uid, String token) throws SQLException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			TokenExpiredException {

		ps = c.prepareStatement(Queries.VERIFY_RESET_TOKEN);
		ps.setInt(1, uid);

		rs = ps.executeQuery();

		if (rs.next()) {

			System.out.println("inside rs");

			long hour = 3600 * 1000; // 3600 seconds times 1000 milliseconds
			Date incrementedDate = new Date(rs.getTimestamp(1).getTime() + hour);

			Date now = new Date(System.currentTimeMillis());

			PasswordManager pm = new PasswordManager();

			if (!pm.validatePassword(token, rs.getString(3))
					&& !now.before(incrementedDate)) {
				System.out.println("didnt expire");

				throw new TokenExpiredException("Request Expired");
			}

			System.out.println("didnt expire");
		} else {
			throw new TokenExpiredException("Request Expired");
		}
		System.out.println("after rs");

		ps.close();

	}

	private boolean userExists(String email) throws SQLException {

		boolean success = false;
		
		ps = c.prepareStatement(Queries.LOOK_UP_USER);
		ps.setString(1, email);

		rs = ps.executeQuery();
		if (rs.next()) {
			System.out.println("Exists");
			user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
			success = true;
		} else {
			
			ps = c.prepareStatement(Queries.LOOK_IN_EMAILS);
			ps.setString(1, email);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				int uid = rs.getInt(2);
				
				ps = c.prepareStatement(Queries.LOOK_UP_USER_BY_ID);
				ps.setInt(1, uid);

				rs = ps.executeQuery();
				
				if(rs.next()){
					user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
					success = true;
				}
			}else{			
				System.out.println("Doesn't Exist");
				success = false;
			}
		
		}
		return success;

	}

	private boolean hasRequest(int uid) throws SQLException {

		ps = c.prepareStatement(Queries.LOOK_UP_USER_REQUEST);
		ps.setInt(1, uid);

		rs = ps.executeQuery();
		if (rs.next()) {
			return true;
		} else {
			return false;
		}

	}

	public int getUid() {
		return uid;
	}

	public void changePasswordInApp(String email, String oldPassword,
			String newPassword) throws NoSuchAlgorithmException,
			InvalidKeySpecException, SQLException, InvalidEmailException,
			InvalidPasswordException, AccountNotActivatedException {

		validate(email, oldPassword, Queries.ACTION_LOGIN, null);

		PasswordManager pm = new PasswordManager();

		String hashedPassword = pm.encodePassword(newPassword);

		ps = c.prepareStatement(Queries.SET_PASS_BY_UID);
		ps.setString(1, hashedPassword);
		ps.setString(2, pm.genSalt);
		ps.setInt(3, user.getUid());
		ps.execute();

	}
	
	public User findUser(String email) throws SQLException, InvalidEmailException{
		
		User user = null;
		
		ps = c.prepareStatement(Queries.LOOK_UP_USER);
		ps.setString(1, email);
		rs = ps.executeQuery();
				
	    if(rs.next()){
			user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
	    }else{
	    	ps = c.prepareStatement(Queries.LOOK_IN_EMAILS);
			ps.setString(1, email);
			rs = ps.executeQuery();
			
			if(rs.next()){
				ps = c.prepareStatement(Queries.LOOK_UP_USER_BY_ID);
				ps.setInt(1, rs.getInt(2));
				rs = ps.executeQuery();
				
				if(rs.next()){
					user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
				}
			}else{
				throw new InvalidEmailException("Invalid Email");
			}
	    }
				
		return user;
		
	}
	
	/*private boolean isRegistered(String email) throws SQLException{
		
		boolean registered = false;
		
		ps = c.prepareStatement(Queries.CHECK_EMAIL);
		ps.setString(1, email);
		ps.setInt(2, uid);
		ps.execute();
	
		
		return registered;
	}*/

}
