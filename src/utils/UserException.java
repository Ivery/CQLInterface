package utils;

public class UserException extends Exception {

	String exception;
	
	public UserException(){
		super();
		exception = "Unknown";
	}
	
	public UserException(String exception){
		super(exception);
		this.exception = exception;
	}
	
	public String getException(){
		return this.exception;
	}
	
}
