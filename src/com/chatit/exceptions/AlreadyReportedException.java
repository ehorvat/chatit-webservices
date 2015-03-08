package com.chatit.exceptions;


public class AlreadyReportedException extends Exception {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
  public AlreadyReportedException() {}

  //Constructor that accepts a message
  public AlreadyReportedException(String message)
  {
     super(message);
  }
}
