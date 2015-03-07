package com.chatit.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.jfree.date.DateUtilities;

import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.HasPendingRequestException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.exceptions.TokenExpiredException;
import com.chatit.model.User;

public class AuthenticationHelper {
	
	public static AuthenticationHelper lh = new AuthenticationHelper();
	
	private static PreparedStatement ps = null;
		
	private static ResultSet rs = null;
	
	public static User user = null;

	public static int uid;
			
	private AuthenticationHelper(){}
	
	public static void validate(String email, String password, String action, String token) throws SQLException, InvalidEmailException, NoSuchAlgorithmException, InvalidPasswordException, InvalidKeySpecException, AccountNotActivatedException{
		
		ps = Queries.c.prepareStatement(Queries.LOOK_UP_USER);
		ps.setString(1, email);
		
		rs = ps.executeQuery();
		
		if(rs.next()){
			//Email already exists
			System.out.println("Email Exists");
			if(action.equals(Queries.ACTION_LOGIN)){
				
				//User is logging in, validate password as well
				PasswordManager pm = new PasswordManager();
				
				if(!pm.validatePassword(password, rs.getString(4))){
					throw new InvalidPasswordException("Invalid Password");
				}
				
				if(!rs.getBoolean(6)){
					throw new AccountNotActivatedException("Account Not Activated");
				}
				
				user = new User(rs.getInt(1), rs.getString(2), rs.getBoolean(6), null);
			}
			
			if(action.equals(Queries.ACTION_REGISTER)){
				//User is trying to register an already valid email address
				throw new InvalidEmailException("Email already exists");
			}
			
		}else{
			
			if(action.equals(Queries.ACTION_LOGIN)){
				//User is logging in, and new records found with matching email
				throw new InvalidEmailException("Email does not exist");
				
			}
			
			if(action.equals(Queries.ACTION_REGISTER)){
				//Email does not exist on registration, create user.
				registerUser(email, password, token);
			}
			
			if(action.equals(Queries.ACTION_CHANGE_PASSWORD)){
				//No email found with given password
				throw new InvalidEmailException("Email not found");
			}
			
			
		}
		
		ps.close();
		rs.close();
		
		
	}
	
	public static void registerUser(String email, String password, String token) throws NoSuchAlgorithmException, SQLException{
		
		PasswordManager pm = new PasswordManager();

		
		//Create time stamp 30mins ahead of current time
		java.sql.Timestamp expiration = new java.sql.Timestamp(
				new java.util.Date(System.currentTimeMillis()+30*60*1000).getTime());
		
		// Secure password
		String hashedPassword = pm.encodePassword(password);
		
		ps = Queries.c.prepareStatement(Queries.REGISTER_USER);
		ps.setString(1, email);
		ps.setString(2, pm.genSalt);
		ps.setString(3, hashedPassword);
		ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		ps.setBoolean(5, false);
		ps.setString(6, token);
		ps.setTimestamp(7, expiration);
		ps.execute();
		
		ps.close();
		
	}
	
	public static void resetPassword(String email, String temp) throws NoSuchAlgorithmException, SQLException, InvalidEmailException{
		
		PasswordManager pm = new PasswordManager();
		
		String hashedPassword = pm.encodePassword(temp);
		
		ps = Queries.c.prepareStatement(Queries.SET_PASS_BY_EMAIL);
		ps.setString(1, hashedPassword);
		ps.setString(2, pm.genSalt);
		ps.setString(3, email);
		
		int r = ps.executeUpdate();
		if(r <= 0){
			throw new InvalidEmailException("Email does not exist");
		}
		
		ps.close();
		
		
	}

	static void changePassword(int uid, String newPassword) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException {
		
		PasswordManager pm = new PasswordManager();
		
		System.out.println("NEW PASS " +  newPassword);
				
		String hashedPassword = pm.encodePassword(newPassword);
			
			PreparedStatement ps = Queries.c.prepareStatement(Queries.SET_PASS_BY_ID);
			ps.setString(1, hashedPassword);
			ps.setString(2, pm.genSalt);
			ps.setInt(3, uid);
			
			ps.execute();
			
			ps = Queries.c.prepareStatement(Queries.DELETE_REQUEST);
			ps.setInt(1, uid);
			
			ps.execute();
			
			ps.close();
			
		
		
	}

	public static void addRequest(String email, String token) throws SQLException, NoSuchAlgorithmException, HasPendingRequestException, InvalidEmailException {
		
		PasswordManager pm = new PasswordManager();
		
		String hashedPassword = pm.encodePassword(token);
		
		if(userExists(email)){
			uid = rs.getInt(1);
			if(!hasRequest(uid)){
				ps = Queries.c.prepareStatement(Queries.CREATE_REQUEST);
				ps.setString(1, hashedPassword);
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setInt(3, uid);
				ps.execute();
			}else{
				throw new HasPendingRequestException("This email has a pending request");
			}

			
		
			
		}else{
			throw new InvalidEmailException("User doesn't exist");
		}
		
		
	}
	
	public static void verifyToken(int uid, String token) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, TokenExpiredException{
		
		
		ps = Queries.c.prepareStatement(Queries.VERIFY_RESET_TOKEN);
		ps.setInt(1,uid);
		 
		rs = ps.executeQuery();
		
		if(rs.next()){
			
			System.out.println("inside rs");
			
			long hour = 3600 * 1000; // 3600 seconds times 1000 milliseconds
			Timestamp incrementedDate = new Timestamp(rs.getTimestamp(1).getTime() + hour);
			

			
			Timestamp now = new Timestamp(System.currentTimeMillis());
						
			PasswordManager pm = new PasswordManager();
			
			System.out.println(now + " " + incrementedDate);
			
			if(!pm.validatePassword(token, rs.getString(3)) && !now.before(incrementedDate)){
				System.out.println("didnt expire");

				throw new TokenExpiredException("Request Expired");
			}
			
			System.out.println("didnt expire");
		}else{
			throw new TokenExpiredException("Request Expired");
		}
		System.out.println("after rs");

		ps.close();
		
		
	}
	
	private static boolean userExists(String email) throws SQLException{
		
		ps = Queries.c.prepareStatement(Queries.LOOK_UP_USER);
		ps.setString(1, email);
		
		rs = ps.executeQuery();
		if(rs.next()){
			System.out.println("Exists");
			return true;
		}else{
			System.out.println("Doesn't Exist");
			return false;
		}
		
	}
	
	private static boolean hasRequest(int uid) throws SQLException{
		
		ps = Queries.c.prepareStatement(Queries.LOOK_UP_USER_REQUEST);
		ps.setInt(1, uid);
		
		rs = ps.executeQuery();
		if(rs.next()){
			System.out.println("Exists");
			return true;
		}else{
			System.out.println("Doesn't Exist");
			return false;
		}
		
	}
	
	public static int getUid(){
		return uid;
	}

	public static void changePasswordInApp(String email, String oldPassword, String newPassword) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException {
		
		validate(email, oldPassword, Queries.ACTION_LOGIN, null);
			
		PasswordManager pm = new PasswordManager();
						
		String hashedPassword = pm.encodePassword(newPassword);
				
		ps = Queries.c.prepareStatement(Queries.SET_PASS_BY_EMAIL);
		ps.setString(1, hashedPassword);
		ps.setString(2, pm.genSalt);
		ps.setString(3, email);
		ps.execute();
				
		
		
		
		
	}
	
	
	
	
	
}
