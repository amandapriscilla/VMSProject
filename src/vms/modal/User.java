package vms.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String email;
    private String password;
    
    /* Temporary list of roles */
    private List<UserRole> roles = new ArrayList<UserRole>();
    

    public User() {
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the string that represents the object
	 */
	public String toString(){
		return getId() + " - " + getName() + " - " + getEmail();
	}
	

	/**
	 * @return the roles
	 */
	public List<UserRole> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User user = (User) obj;
			return user.getId() == this.getId();
		}

		return false;
	}
	
	public boolean isStaff(){
		return roles.contains(UserRole.Staff);
	}
	public boolean isVolunteer(){
		return roles.contains(UserRole.Volunteer);
	}	
	public boolean isVolunteerLeader(){
		return roles.contains(UserRole.VolunteerLeader);
	}

	public boolean isActiveRole(UserRole role) {
		return false;
	}

}
