package com.chatit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

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
import com.chatit.exceptions.AlreadyReportedException;
import com.chatit.exceptions.ChannelAlreadyExistsException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.model.Report;

///////////////////////////////////////////
//
// Channel Moderator Controller
//
//////////////////////////////////////////

public class ChannelModeratorResource {
	
		@POST
		@Path("report")
		@Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public String reportMessage(InputStream incomingData) throws ChannelAlreadyExistsException, InvalidEmailException {
		/*public Response reportMessage(@FormParam("reporter") String reporter,
									  @FormParam("reportee") String reportee,
									  @FormParam("channel") String channel,
									  @FormParam("message") String message) throws ChannelAlreadyExistsException, InvalidEmailException {*/
		   		   
			String reporter, reportee, message, channel, tstamp;
			
			int type;
										
			StringBuilder builder = new StringBuilder();
			
			JSONObject mainObject = null;
			
			JSONObject response = new JSONObject();
			
			try {
				
				BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
		    	String line = null;
				while((line = in.readLine()) != null){
					builder.append(line);
				}		
				
				//Parse JSONObject
				mainObject = new JSONObject(builder.toString());
				channel = mainObject.getString("channel").trim();
				reporter = mainObject.getString("reporter");
				reportee = mainObject.getString("reportee");
				message = mainObject.getString("message");
				tstamp = mainObject.getString("tstamp");
				type = mainObject.getInt("type");
				
				
				Report report = new Report(reporter, reportee, tstamp, message, channel, type);
				
				DAOManager manager = DAOFactory.createDaoManager();
				
				manager.getReportDao().report(report);
				
				
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
			} catch (AlreadyReportedException e) {
				setErrorMessage("User already reported", response);
				e.printStackTrace();
			}finally{
			}
				   
			return response.toString();

			
			/**** For Testing ****/
			
			/*Report report = new Report(reporter, reportee, new Date(System.currentTimeMillis()).toString(), message, channel);
			
			DAOManager manager;
			
			try {
				manager = DAOFactory.createDaoManager();
				
				manager.getReportDao().report(report);
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyReportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				Queries.connection.disconnect();
			}
			
			
	 
			return Response.status(200)
				.entity(" reporter : " + reporter + ", reportee : " + reportee)
				.build();*/
		}
		
		private void setErrorMessage(String message, JSONObject response){
			try{
				response.put("success", false);
				response.put("error", message);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	
	
}
