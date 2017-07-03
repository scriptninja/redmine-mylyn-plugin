package net.sf.redmine_mylyn.api.exception;

public class RedmineApiHttpStatusException extends RedmineApiRemoteException {
	
	private static final long serialVersionUID = 1L;

	private int statusCode;
	
	public RedmineApiHttpStatusException(int statusCode, String message, Object... params) {
		super(message, params);
		
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

}
