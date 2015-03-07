package com.chatit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chatit.dao.ChannelDAO;
import com.chatit.dao.DAOFactory;
import com.chatit.dao.DAOManager;
import com.chatit.dao.EmailDAO;
import com.chatit.dao.UserDAO;
import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.model.Channel;
import com.chatit.model.Email;
import com.chatit.model.User;
import com.chatit.util.Constants;
import com.chatit.util.Queries;

@Path("/login")
public class LoginResource {
	
	private JSONObject mainObject;


	@POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String authUser(InputStream incomingData) {
				
		String base_email = null;
				
		String password = null;
		
		String secondary_email = null;
				
		StringBuilder builder = new StringBuilder();
		
		JSONObject response = new JSONObject();
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}					
			
			mainObject = new JSONObject(builder.toString());
			base_email = mainObject.getString("email").trim();
			password = mainObject.getString("pass").trim();
			
			//Initialize DAO Manager
			DAOManager manager = DAOFactory.createDaoManager();
			
			//Get user dao
			UserDAO userDao = manager.getUserDao();
			
			//Get email dao
			EmailDAO emailDao = manager.getEmailDao();
			
			//Get channel dao
			ChannelDAO channelDao = manager.getChannelDao();
			
			//Validate user
			userDao.validate(base_email, password, Queries.ACTION_LOGIN, null);
			
			User user = userDao.getUser();
			
			base_email = user.getEmail();
			
			ArrayList<Channel> base_channels = channelDao.getChannelsByEmail(user.getUid(), null, base_email);						
			
			ArrayList<Email> emails = emailDao.getUserEmails(base_email);
			
			emails = channelDao.addChannels(emails, user);
		
			ArrayList<Channel> channels = null;
			
			Channel channel = null;
			
			JSONArray registered_emails = new JSONArray();
			
			JSONArray pending_emails = new JSONArray();
			
			JSONArray associated_channels = null;
						
			JSONObject jEmail = null;
			
			JSONObject jChannel = null;
					
			Email email = null;
			
			associated_channels = new JSONArray();

			for(int i = 0; i<base_channels.size();i++){
				channel = new Channel(base_channels.get(i).getChannel_name());
				jChannel = new JSONObject();
				jChannel.put("channel_name", base_channels.get(i).getChannel_name().trim());
				associated_channels.put(jChannel);
			}
			
			System.out.println("associated " + associated_channels.toString());
			
			jEmail = new JSONObject();
			jEmail.put("address", base_email);
			jEmail.put("channels", associated_channels);
			
			registered_emails.put(jEmail);
			
			for(int i = 0; i<emails.size(); i++){
				
				jEmail = new JSONObject();
				
				email = emails.get(i);
				
				String [] splitted = email.getAddress().trim().split("\\@");
				
				
				System.out.println("Emails: " + email.getAddress() + " base: " + base_email);
				
				//Check the status of the email
				if(!email.getEmailStatus().equals(Constants.EMAIL_STATUS_PENDING)){
						if(!email.getAddress().trim().equals(base_email.trim())){
							
						
							//Not pending so add email and associated channels
					
							channels = 	channelDao.getChannelsByEmail(user.getUid(), splitted[1].trim(), null);

				
							associated_channels = new JSONArray();
							
							
							jEmail.put("address", email.getAddress().trim());
				
							for(int j = 0; j<channels.size(); j++){
					
								jChannel = new JSONObject();
								channel = channels.get(j);
					
								jChannel.put("channel_name", channel.getChannel_name());
								associated_channels.put(jChannel);
					
								jEmail.put("channels", associated_channels);
								
								System.out.println("jEmail " + jEmail.toString());

							}
							
							System.out.println("associated secondary " + associated_channels.toString());

							registered_emails.put(jEmail);

						}
				
					}else{
						
						//Email is still pending, add to pending_emails array
						
						jEmail.put("address", email.getAddress().trim());
						pending_emails.put(jEmail);
					}
				
				}
			
			System.out.println("reg emails " + registered_emails.toString());
		
												
			response.put("success", true);
			response.put("error", "none");
			response.put("registered_emails", registered_emails);
			response.put("pending_emails", pending_emails);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			try {
				response.put("success", false);
				response.put("error", "Email does not exist");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (InvalidPasswordException e) {
			try {
				response.put("success", false);
				response.put("error", "Wrong password");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccountNotActivatedException e) {
			
			try {
				response.put("success", false);
				response.put("error", "Account is inactive. Please check your email");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return response.toString();
	}
	
	
}
