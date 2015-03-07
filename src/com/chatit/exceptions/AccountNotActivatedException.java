package com.chatit.exceptions;

public class AccountNotActivatedException extends Exception {
	 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//Parameterless Constructor
	   public AccountNotActivatedException() {}

	   //Constructor that accepts a message
	   public AccountNotActivatedException(String message)
	   {
	      super(message);
	   }
}
