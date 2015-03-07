package com.chatit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.chatit.dao.DAOFactory;
import com.chatit.dao.DAOManager;
import com.chatit.dao.EmailDAO;
import com.chatit.dao.UserDAO;
import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.HasPendingRequestException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.model.User;
import com.chatit.util.AuthenticationHelper;
import com.chatit.util.EmailUtility;
import com.chatit.util.Queries;

@Path("/settings")
public class SettingResource {
	
	
	JSONObject mainObject = null;
	
	JSONObject response = new JSONObject();

	
   @POST
   @Path("recover")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String resetPassword(InputStream incomingData) {
		
		try {
			
			StringBuilder builder = new StringBuilder();
			
			
			String email = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
						
			mainObject = new JSONObject(builder.toString());
			
			email = mainObject.getString("email");
			

			System.out.println(email);
			//Generate random password
			SecureRandom random = new SecureRandom();
			
			String token = new BigInteger(100, random).toString(32);
			
			
			
			DAOManager manager = DAOFactory.createDaoManager();
			
			UserDAO userDao = manager.getUserDao();
			
			userDao.addRequest(email, token);
			
			User user = userDao.getUser();
											
			response.put("success", true);
			response.put("error", "none");
			
			System.out.println("Token : " + token);
			
			String subject = "Chatit Support - Password Recovery";
			String content = "<p>Please click <a href='http://chatitwebservices-env.elasticbeanstalk.com/forgotPassword?token=" + token + "&uid=" + user.getUid() + "'>here</a> to reset your Chatit Password <br/></p>";
		
			//String content = "<p>Please click <a href='http://localhost:8080/ChatitWebServices/forgotPassword?token=" + token + "&uid=" + AuthenticationHelper.getUid() + "'>here</a> to reset your Chatit Password <br/> <a href=''></p>";

			
			EmailUtility.sendEmail(email, subject, content);		
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			sendErrors(e);		
			e.printStackTrace();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (HasPendingRequestException e) {
			sendErrors(e);
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
		
		
	}
   
   @POST
   @Path("change")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String changePassword(InputStream incomingData) {
	   
	   try {
			StringBuilder builder = new StringBuilder();
			
			String email = null;
			
			String newPassword, oldPassword = null;
			
			int uid;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
						
			mainObject = new JSONObject(builder.toString());
			
			System.out.println(mainObject.toString());
			
			email = mainObject.getString("email");
			
			newPassword = mainObject.getString("new");
			
			oldPassword = mainObject.getString("old");
						
			DAOManager manager = DAOFactory.createDaoManager();
			
			manager.getUserDao().changePasswordInApp(email, oldPassword, newPassword);
											
			response.put("success", true);
			response.put("error", "none");
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			sendErrors(e);
			e.printStackTrace();
		} catch (InvalidPasswordException e) {
			sendErrors(e);
			e.printStackTrace();
		} catch (AccountNotActivatedException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
	   
	   
   }
   
   @POST
   @Path("requestEmail")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String newEmail(InputStream incomingData) {
	   
	   try {
			StringBuilder builder = new StringBuilder();
						
			String new_email = null;
			
			String base_email = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
						
			mainObject = new JSONObject(builder.toString());
			
			System.out.println(mainObject.toString());
						
			new_email = mainObject.getString("new_email");
			
			base_email = mainObject.getString("base_email");
				
			DAOManager manager = DAOFactory.createDaoManager();
			
			//Generate random password
			SecureRandom random = new SecureRandom();
			
			String token = new BigInteger(100, random).toString(32);
			
			EmailDAO email_dao = manager.getEmailDao();
			
			email_dao.newEmailRequest(new_email, base_email, token);
			
			String subject = "Ch@It Email Activation";
			String content = "<p>Please click <a href='http://chatitwebservices-env.elasticbeanstalk.com/activateEmail?token=" + token + "&base=" + base_email + "&new=" + new_email + "'>here</a> to activate your email! <br/></p>";
		
			//String content = "<p>Please click <a href='http://localhost:8080/ChatitWebServices/activateEmail?token=" + token + "&base=" + base_email + "&new=" + new_email + "'>here</a> to reset your Chatit Password <br/> <a href=''></p>";

			
			EmailUtility.sendEmail(new_email, subject, content);	
											
			response.put("success", true);
			response.put("error", "none");
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			sendErrors(e);
			e.printStackTrace();
		}  catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
	   
	   
   }
   
   @POST
   @Path("tapToActivate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String tapToActivate(InputStream incomingData) {
	   
	   try {
			StringBuilder builder = new StringBuilder();
						
			String new_email = null;
			
			String base_email = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
						
			mainObject = new JSONObject(builder.toString());
			
			System.out.println(mainObject.toString());
						
			new_email = mainObject.getString("new_email");
			
			base_email = mainObject.getString("base_email");
				
			DAOManager manager = DAOFactory.createDaoManager();
			
			EmailDAO email_dao = manager.getEmailDao();
			
			email_dao.checkEmailActivation(base_email, new_email);
													
			response.put("success", true);
			response.put("error", "none");
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			sendErrors(e);
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
	   
	   
   }
   
   
   @POST
   @Path("deleteEmail")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String deleteEmail(InputStream incomingData) {
	   
	   try {
			StringBuilder builder = new StringBuilder();
						
			String delete_email = null;
			
			String base_email = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
						
			mainObject = new JSONObject(builder.toString());
			
			System.out.println(mainObject.toString());
						
			delete_email = mainObject.getString("delete_email");
			
			base_email = mainObject.getString("base_email");
				
			DAOManager manager = DAOFactory.createDaoManager();
			
			EmailDAO email_dao = manager.getEmailDao();
			
			email_dao.deleteEmail(base_email, delete_email);
													
			response.put("success", true);
			response.put("error", "none");
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
	   
	   
   }
   
   private void sendErrors(Exception e){
		try {
			response.put("success", false);
			response.put("error", e.getMessage());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
   }

}
