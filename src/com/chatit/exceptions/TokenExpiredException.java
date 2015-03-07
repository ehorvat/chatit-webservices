package com.chatit.exceptions;

public class TokenExpiredException extends Exception{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
   public TokenExpiredException() {}

   //Constructor that accepts a message
   public TokenExpiredException(String message)
   {
      super(message);
   }
	
	
}
