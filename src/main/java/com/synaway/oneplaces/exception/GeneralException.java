package com.synaway.oneplaces.exception;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class GeneralException extends Exception{

	private static final long serialVersionUID = 1L;

	public static final int OBJECT_NOT_FOUND = 100;
    public static final int USER_NOT_FOUND = 101;
    
    public static final int GENERAL_FILE_EXCEPTION = 200;
    public static final int FILE_NOT_FOUND = 201;
    
    public static final int GENERAL_ACCESS_TOKEN_EXCEPTION = 300;

	public static final int GENERAL_EXCEPTION = 500;

	public static final int GENERAL_ACCESS_TOKEN_REQUIRED = 301;
	public static final int GENERAL_ACCESS_TOKEN_INVALID = 302;
	public static final int GENERAL_ACCESS_TOKEN_EXPIRED = 303;
	
	public static final int GENERAL_INVALID_LOGIN = 504;
	public static final int GENERAL_INVALID_PASSWORD = 505;
	public static final int GENERAL_USER_EXIST = 506;
	
	@JsonProperty
	private int code = 0;
	
	@JsonProperty(value="message")
	private  String developerMessage = "unknown error occured";

	public GeneralException() {
		super();
	}

	public GeneralException(String message, Throwable cause) {
		super(message, cause);
		this.developerMessage = message;
	}

	public GeneralException(String message) {
		super(message);
		this.developerMessage = message;
	}

	public GeneralException(Throwable cause) {
		super(cause);
	}

	public GeneralException(String message, int code) {
		super(message);
		this.developerMessage = message;
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}


	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	

}
