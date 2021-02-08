package vms.modal;

import java.io.Serializable;
import java.util.Date;

public class Volunteer extends User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date startDate;
	private Date endDate;
	private boolean active;
	
	
	public Volunteer() {
		super();
	}
	public Volunteer(User user) {
		super();
		this.setId(user.getId());
		this.setName(user.getName());
		this.setEmail(user.getEmail());
	}
	
	public Volunteer(User user, boolean active, Date startDate, Date endDate) {
		super();
		this.setId(user.getId());
		this.setName(user.getName());
		this.setEmail(user.getEmail());		
		this.active = active;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public boolean isActiveRole(UserRole role) {
		return role.equals(UserRole.Volunteer) && this.getId() > 0 && startDate != null;
	}

    
	
	
}
