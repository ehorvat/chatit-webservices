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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import org.json.JSONException;
import org.json.JSONObject;

import com.chatit.dao.DAOFactory;
import com.chatit.dao.DAOManager;
import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.util.EmailUtility;
import com.chatit.util.Queries;

@Path("/signup")
public class SignUpResource {

	private JSONObject mainObject;
	
	@Context
	Request request;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String authUser(InputStream incomingData) {
				
		String email = null;
				
		String password = null;
		
		StringBuilder builder = new StringBuilder();
		
		JSONObject response = new JSONObject();
	
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
			
			mainObject = new JSONObject(builder.toString());
			email = mainObject.getString("email").trim();
			password = mainObject.getString("password").trim();
						
			Queries.dbc();
			
			//Generate random token
			SecureRandom random = new SecureRandom();
			String token = new BigInteger(100, random).toString(32);
			
			DAOManager manager = DAOFactory.createDaoManager();
			
			manager.getUserDao().validate(email, password, Queries.ACTION_REGISTER, token);
			
			response.put("success", true);
			response.put("error", "none");
			
			String subject = "Welcome to Chatit!";
			String content = "<p>Thank you for signing up with chatit! Click <a href='http://chatitwebservices-env.elasticbeanstalk.com/activate?token=" + token + "'>here</a> to activate your account and join the conversation!</p>";
		
			//String content = "<p>Thank you for signing up with chatit! <a href='http://localhost:8080/ChatitWebServices/activate?token=" + token + "'>Click Here</a> to activate your account and join the conversation!</p>";

			
			EmailUtility.sendEmail(email, subject, content);
				
		
		
		} catch (NamingException | SQLException | ServletException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			e.printStackTrace();
			try {
				response.put("success", false);
				response.put("error", "Email already exists");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccountNotActivatedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}


		
		return response.toString();
	}
	
}
