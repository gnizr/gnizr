package com.gnizr.core.user;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.db.dao.User;

/**
 * Manages the recovery of user passwords. 
 * 
 * @author Harry Chen
 * @since 2.4.0
 *
 */
public class PasswordManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9220221707141999631L;

	
	private UserManager userManager;
	
	private ConcurrentHashMap<String,ResetPasswordTicket> openTickets;
	
	private static final SecureRandom random = new SecureRandom();

	public void init(){
		openTickets = new ConcurrentHashMap<String, ResetPasswordTicket>();
		random.setSeed(System.currentTimeMillis());
	}
	
	/**
	 * Returns the <code>UserManager</code> object used to reset
	 * user password.
	 * 
	 * @return an instantiated manager.
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * Sets the <code>UserManager</code> object used to reset
	 * user password.
	 * 
	 * @param userManager an instantiated manager.
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	
	public String createResetToken(User user){
		if(user == null || user.getUsername() == null){
			throw new NullPointerException("User is null. user=" + user);
		}
		String token = UUID.randomUUID().toString();
		Date createdOn = GregorianCalendar.getInstance().getTime();
		ResetPasswordTicket ticket = new ResetPasswordTicket(token,user.getUsername(),createdOn);
		if(openTickets.replace(user.getUsername(), ticket) == null){
			openTickets.put(user.getUsername(),ticket);
		}
		return token;
	}
	
	
	public boolean isValidResetToken(String token, User user){
		if(user == null || user.getUsername() == null){
			throw new NullPointerException("User is null. user=" + user);
		}
		ResetPasswordTicket ticket = openTickets.get(user.getUsername());
		if(ticket != null && token != null && token.length() > 0){
			if(ticket.getUsername().equals(user.getUsername())){
				UUID tokenUUID = UUID.fromString(token);
				UUID checkUUID = UUID.fromString(ticket.getToken());
				return checkUUID.equals(tokenUUID);
			}
		}
		return false;
	}
	
	public boolean resetPassword(String token, User user) throws NoSuchUserException{
		if(isValidResetToken(token, user) == true){
			openTickets.remove(user.getUsername());
			return userManager.changePassword(user);
		}
		return false;
	}
	
	public List<ResetPasswordTicket> listOpenTickets(){
		return new ArrayList<ResetPasswordTicket>(openTickets.values());
	}
	
	/**
	 * Ticket for tracking password reset request.
	 * @author Harry Chen
	 *
	 */
	public class ResetPasswordTicket {		
		private String token;
		private String username;	
		private Date createdOn;
			
		public ResetPasswordTicket(String token, String username, Date createdOn){
			this.username = username;
			this.token = token;
			if(createdOn != null){
				this.createdOn = (Date)createdOn.clone();
			}else{
				this.createdOn = null;
			}
		}
		public String getUsername() {
			return username;
		}
		public String getToken() {
			return token;
		}
		public Date getCreatedOn() {
			return createdOn;
		}		
	}
	
	
	
}
