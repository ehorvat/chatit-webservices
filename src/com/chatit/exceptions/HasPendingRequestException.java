package com.chatit.exceptions;

public class HasPendingRequestException extends Exception{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
   public HasPendingRequestException() {}

   //Constructor that accepts a message
   public HasPendingRequestException(String message)
   {
      super(message);
   }
}
