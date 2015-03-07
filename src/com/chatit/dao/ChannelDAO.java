package com.chatit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.chatit.exceptions.ChannelAlreadyExistsException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.model.Channel;
import com.chatit.model.Email;
import com.chatit.model.User;
import com.chatit.util.Constants;
import com.chatit.util.Queries;

public class ChannelDAO {

	Connection c = null;

	PreparedStatement ps = null;

	ResultSet rs = null;

	public ChannelDAO(Connection c) {
		this.c = c;
	}

	public Channel getChannel(String channel_name, String email) throws SQLException {

		Channel channel = null;

		ps = c.prepareStatement(Queries.SELECT_CHANNEL);
		ps.setString(1, channel_name);
		ps.setString(2, email);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			channel = new Channel(rs.getString(1), rs.getInt(2), rs.getString(3));
		}

		return channel;
	}

	public void addChannel(String channel_name, String email)
			throws SQLException, ChannelAlreadyExistsException, InvalidEmailException {
				
		String[] splitted = email.split("\\@");
		
		
		if(getChannel(channel_name, splitted[1]) == null){
			ps = c.prepareStatement(Queries.LOOK_UP_USER);
			ps.setString(1, email);

			rs = ps.executeQuery();

			if (rs.next()) {
				
				ps = c.prepareStatement(Queries.INSERT_CHANNEL);
				ps.setString(1, channel_name);
				ps.setInt(2, rs.getInt(1));
				ps.setString(3, splitted[1]);
				ps.execute();
				ps.close();
			}else{
				ps = c.prepareStatement(Queries.LOOK_IN_EMAILS);
				ps.setString(1, email.trim());
				
				rs = ps.executeQuery();
				
				if(rs.next()){
					ps = c.prepareStatement(Queries.INSERT_CHANNEL_WITH_EID);
					ps.setString(1, channel_name);
					ps.setInt(2, rs.getInt(2));
					ps.setString(3, splitted[1]);
					ps.setInt(4, rs.getInt(3));
					ps.execute();
					ps.close();
				}else{
					throw new InvalidEmailException("No email found");
				}
			}
		}else{
			throw new ChannelAlreadyExistsException("Channel Already Exists");
		}

	

	}

	public ArrayList<Channel> getChannelsByEmail(int uid, String secondary_email, String base_email)
			throws SQLException {
		
		
		ps = c.prepareStatement(Queries.LOOK_UP_USER_BY_ID);
		ps.setInt(1, uid);

		rs = ps.executeQuery();

		ArrayList<Channel> channels = new ArrayList<Channel>();


		if (rs.next()) {

			if(base_email ==  null){
				
				System.out.println("secondary " +secondary_email);

				ps = c.prepareStatement(Queries.SELECT_CHANNELS_BY_EMAIL);
				ps.setInt(1, rs.getInt(1));
				ps.setString(2, secondary_email);
				rs = ps.executeQuery();

				while (rs.next()) {
					Channel channel = new Channel(rs.getString(1));
					channels.add(channel);
				}
			}else{
				String [] splitted = base_email.split("\\@");


				ps = c.prepareStatement(Queries.SELECT_CHANNELS_BY_EMAIL);
				ps.setInt(1, rs.getInt(1));
				ps.setString(2, splitted[1].trim());
				rs = ps.executeQuery();
				
				while (rs.next()) {
					Channel channel = new Channel(rs.getString(1));
					channels.add(channel);
				}
				
			}

			
		}
		return channels;
	}
	
	public ArrayList<Email> addChannels(ArrayList<Email> emails, User user) throws SQLException {
		
		for(Email email : emails){
			
			if(!email.getEmailStatus().equals(Constants.EMAIL_STATUS_PENDING)){

				ArrayList<Channel> channels = new ArrayList<Channel>();
				
				ps = c.prepareStatement(Queries.SELECT_CHANNELS_BY_EMAIL);
				ps.setInt(1,user.getUid());
				ps.setString(2, email.getAddress());
				
				rs = ps.executeQuery();
				
				while(rs.next()){
					Channel channel = new Channel(rs.getString(1));
					channels.add(channel);
				}
				
				email.setChannels(channels);	
			}
			
		}
		
		return emails;
	}
	
	public void removeChannel(String channel_name, int uid, String email) throws SQLException{
				
		ps = c.prepareStatement(Queries.DELETE_CHANNEL);
		ps.setString(1, channel_name);
		ps.setInt(2, uid);
		ps.setString(3, email.trim());
		ps.execute();
	}

}
