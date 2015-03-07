package com.chatit.exceptions;

public class InvalidEmailException extends Exception{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
   public InvalidEmailException() {}

   //Constructor that accepts a message
   public InvalidEmailException(String message)
   {
      super(message);
   }
}
