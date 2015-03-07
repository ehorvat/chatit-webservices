package com.chatit.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.HasPendingRequestException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.exceptions.TokenExpiredException;
import com.chatit.model.User;

public class AuthenticationService {

	public String email = null;
	
	public String password = null;
	
	public String oldPassword = null;
	
	public String action = null;
	
	public String token = null;
	
	public int uid;
			
	public AuthenticationService(String email, String password, String action, String token) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException, NamingException, ServletException{
		this.email=email;
		this.password=password;
		this.action=action;
		this.token=token;
	}
	
	public AuthenticationService(String email, String temp) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException, NamingException, ServletException{
		this.email=email;
		this.token=temp;
	}
	
	public AuthenticationService(int uid, String token){
		this.uid=uid;
		this.token=token;
	}
	
	public AuthenticationService(String email, String oldPassword, String newPassword){
		this.email=email;
		this.oldPassword=oldPassword;
		this.password=newPassword;
	}

	public void authenticate() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException {		
		AuthenticationHelper.validate(email, password, action, token);
	}
	
	public void resetPassword() throws NoSuchAlgorithmException, SQLException, InvalidEmailException{
		AuthenticationHelper.resetPassword(email, token);
	}
	
	public void changePasswordRecovery() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException{
		AuthenticationHelper.changePassword(uid, password);
	}
	
	public void changePasswordInApp() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, InvalidEmailException, InvalidPasswordException, AccountNotActivatedException{
		AuthenticationHelper.changePasswordInApp(email, oldPassword, password);
	}
	
	public void addRequest() throws NoSuchAlgorithmException, SQLException, HasPendingRequestException, InvalidEmailException{
		AuthenticationHelper.addRequest(email, token);
	}
	
	public void verifyToken() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException, TokenExpiredException{
		AuthenticationHelper.verifyToken(uid, token);
	}
	
	
	
}
