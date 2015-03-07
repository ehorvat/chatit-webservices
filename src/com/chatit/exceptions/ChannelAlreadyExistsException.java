package com.chatit.exceptions;

public class ChannelAlreadyExistsException extends Exception{

	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
  public ChannelAlreadyExistsException() {}

  //Constructor that accepts a message
  public ChannelAlreadyExistsException(String message)
  {
     super(message);
  }
	
	
}
