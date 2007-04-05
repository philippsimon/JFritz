package de.moonflower.jfritz.network;

public class Login {

	private String user, password;
	
	private boolean allowAdd, allowUpdate, allowRemove;
	
	public Login(String user, String password, boolean a, boolean u, boolean r){
		this.user = user;
		this.password = password;
		allowAdd = a;
		allowUpdate = u;
		allowRemove = r;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getPassword(){
		return password;
	}
	
	public boolean allowAdd(){
		return allowAdd;
	}
	
	public boolean allowUpdate(){
		return allowUpdate;
	}
	
	public boolean allowRemove(){
		return allowRemove;
	}
	
}
