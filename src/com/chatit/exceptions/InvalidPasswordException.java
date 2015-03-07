package com.chatit.exceptions;

public class InvalidPasswordException extends Exception{

	 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//Parameterless Constructor
	   public InvalidPasswordException() {}

	   //Constructor that accepts a message
	   public InvalidPasswordException(String message)
	   {
	      super(message);
	   }
	
}
