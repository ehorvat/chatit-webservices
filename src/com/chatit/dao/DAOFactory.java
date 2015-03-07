package com.chatit.dao;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import com.chatit.util.Queries;

public class DAOFactory {
	
	public static DAOManager createDaoManager() throws NamingException, SQLException, ServletException{
		return new DAOManager(Queries.dbc());
	}
	
}
