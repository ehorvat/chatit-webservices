package com.chatit.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.chatit.exceptions.TokenExpiredException;

public class EmailUtility {
	public static void sendEmail(String toAddress,
			String subject, String content) throws AddressException,
			MessagingException {

		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("chatitservices@gmail.com", "halfnelson");
			}
		};

		Session session = Session.getInstance(properties, auth);

		// creates a new e-mail message
		MimeMessage msg = new MimeMessage(session);
		System.out.println("recipient : " + toAddress);
		msg.setFrom(new InternetAddress("chatitservices@gmail.com"));
		
		new InternetAddress(toAddress).validate();

		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(content, "text/html" );

		// sends the e-mail
		Transport.send(msg);

	}
	
	public static void ActivateAccount(String token) throws SQLException, TokenExpiredException{
		PreparedStatement ps;
		ResultSet rs;
		
		int uid = 0;
		
		ps = Queries.c.prepareStatement(Queries.VERIFY_TOKEN);
		ps.setString(1, token);
		rs = ps.executeQuery();
		
		if(rs.next()){
			
			uid = rs.getInt(1);
			ps.close();
			rs.close();
			
		}
		
		ps = Queries.c.prepareStatement(Queries.ACTIVATE_ACCOUNT);
		ps.setInt(1, uid);
		ps.execute();
		
		
		
	}
}
