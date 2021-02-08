package vms.utils;

import java.io.Serializable;

public class Alert implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String message = "";
	
	public Alert() {
		super();
		type = "success";
		message = "Operation performed with success!";
	}

	public Alert(String type, String message) {
		super();
		this.type = type;
		this.message = message;
	}
}
