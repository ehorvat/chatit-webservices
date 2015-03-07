package com.chatit.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.chatit.dao.DAOFactory;
import com.chatit.dao.DAOManager;
import com.chatit.dao.EmailDAO;
import com.chatit.exceptions.AccountNotActivatedException;
import com.chatit.exceptions.InvalidEmailException;
import com.chatit.exceptions.InvalidPasswordException;
import com.chatit.exceptions.NoToken;
import com.chatit.exceptions.TokenExpiredException;
import com.chatit.util.AuthenticationService;
import com.chatit.util.EmailUtility;
import com.chatit.util.Queries;

@Controller
public class WebController {

	@RequestMapping("/index")
	public String redirectToIndex(){
		
		
		
		return "index";
	}
	
	
	@RequestMapping(value = "/activate", method = RequestMethod.GET)
	public @ResponseBody ModelAndView checkToken(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		
		String token = request.getParameter("token");
		
		System.out.println("Token  :  " + token);

		ModelAndView model = new ModelAndView();
		
		
		
		try {
			
			Queries.dbc();
			
			EmailUtility.ActivateAccount(token);
			
		
			model.setViewName("/index");

			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (TokenExpiredException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return model;

	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public @ResponseBody ModelAndView resetPassword(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		
		String token = request.getParameter("token");
		
		String uid = request.getParameter("uid");
		
		session.setAttribute("uid", Integer.parseInt(uid));
		
		System.out.println("Token  :  " + token + " uid : " + uid);

		ModelAndView model = new ModelAndView();
		
		
		
		try {
			
			Queries.dbc();
		
			new AuthenticationService(Integer.parseInt(uid), token).verifyToken();
								
			model.setViewName("/forgotPassword");
			
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (TokenExpiredException e) {
			model.setViewName("/expiredRequest");
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
		
		return model;

	}
	
	@RequestMapping(value = "http://chatitwebservices-env.elasticbeanstalk.com/changePassword", method = RequestMethod.POST)
	public @ResponseBody ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("here");
		
		HttpSession session = request.getSession();
		
		int uid = (Integer) session.getAttribute("uid");
		
		System.out.println(uid);
		
		String password = request.getParameter("new_password");
		String confPassword = request.getParameter("conf_password");
		
		
		ModelAndView model = new ModelAndView();

		try {
		if(password.equals(confPassword)){
			
				Queries.dbc();
			
				System.out.println("New: " + password.trim() + " Conf: " + confPassword.trim());

				
				new AuthenticationService(uid,password).changePasswordRecovery();
				
				model.setViewName("success");
				
				
			
		}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InvalidEmailException e) {
			e.printStackTrace();
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (AccountNotActivatedException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}finally{
			Queries.connection.disconnect();
		}
				
		return model;

	}
	
	@RequestMapping(value = "/activateEmail", method = RequestMethod.GET)
	public @ResponseBody ModelAndView activateEmail(HttpServletRequest request, HttpServletResponse response) {
				
		String token = request.getParameter("token");
		
		String base_email = request.getParameter("base");
		
		String new_email = request.getParameter("new");
		
		System.out.println("Activation Token : " + token);
		
		ModelAndView model = new ModelAndView();
		
		try {
			DAOManager manager = DAOFactory.createDaoManager();
			
			EmailDAO email_dao = manager.getEmailDao();
			email_dao.verifyActivationToken(token);
			email_dao.addEmail(base_email, new_email);
			
			model.setViewName("emailSuccess");
			
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (TokenExpiredException e) {
			
			model.setViewName("emailFail");
			
			e.printStackTrace();
		} catch (NoToken e) {
			
			model.setViewName("emailFail");
			
			e.printStackTrace();
		}
	
		return model;
		
	}
	
	
}
