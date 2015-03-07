package com.chatit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import com.chatit.dao.DAOFactory;
import com.chatit.dao.DAOManager;
import com.chatit.exceptions.ChannelAlreadyExistsException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.model.Channel;
import com.chatit.model.User;
import com.chatit.util.Queries;

////////////////////////////////////////
//
// Class for handling channel requests
//
////////////////////////////////////////

@Path("/channel")
public class ChannelResource {

	private JSONObject mainObject;


   @POST
   @Path("add")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String addChannel(InputStream incomingData) {
	   
		String channel_name = null;
	   
		String email;
						
		StringBuilder builder = new StringBuilder();
		
		JSONObject response = new JSONObject();
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
			
			mainObject = new JSONObject(builder.toString());
			channel_name = mainObject.getString("channel_name").trim();
			email = mainObject.getString("email");		
			
			
			DAOManager manager = DAOFactory.createDaoManager();
			
			manager.getChannelDao().addChannel(channel_name, email);
			
			
			response.put("success", true);
			response.put("error", "none");
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		}  catch (NamingException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (ChannelAlreadyExistsException e) {
			try {
				response.put("success", false);
				response.put("error", "Channel Already Exists");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			try {
				response.put("success", false);
				response.put("error", "Channel Already Exists");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
	   
		return response.toString();
	}
   
   @POST
   @Path("remove")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public String removeChannel(InputStream incomingData) {
	   
		String channel_name = null;
	   
		String email;
						
		StringBuilder builder = new StringBuilder();
		
		JSONObject response = new JSONObject();
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}		
			
			mainObject = new JSONObject(builder.toString());
			channel_name = mainObject.getString("channel_name").trim();
			email = mainObject.getString("email");			
			
			DAOManager manager = DAOFactory.createDaoManager();
			
			User user = manager.getUserDao().findUser(email);
			
			String splitted [] = email.split("\\@");
			
			manager.getChannelDao().removeChannel(channel_name.trim(), user.getUid(), splitted[1]);
			
			
			response.put("success", true);
			response.put("error", "none");
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		}  catch (NamingException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
	   
		return response.toString();
	}
   
   /*@POST
   @Path("getAll")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
    public String getChannels(InputStream incomingData) {
	   
		String email;
		
		StringBuilder builder = new StringBuilder();
		
		JSONObject response = new JSONObject();
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
	    	String line = null;
			while((line = in.readLine()) != null){
				builder.append(line);
			}					
			
			mainObject = new JSONObject(builder.toString());
			email = mainObject.getString("email");			
			
			DAOManager manager = DAOFactory.createDaoManager();
			
			ArrayList<Channel> channels = manager.getChannelDao().getChannelsByUser(email);
			
			JSONArray arr = new JSONArray();
			
			JSONObject jChannel = null;
			
			Channel channel = null;
			
			for(int i = 0; i<channels.size(); i++){
				jChannel = new JSONObject();
				channel = channels.get(i);
				jChannel.put("channel_name", channel.getChannel_name());
				arr.put(jChannel);
			}
			
					
			response.put("success", true);
			response.put("error", "none");
			response.put("channels", arr);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		}  catch (NamingException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
	   
		return response.toString();
	}	*/
	
}
