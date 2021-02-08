package vms.modal;

import java.io.Serializable;
import java.util.Date;

public class VolunteerLeader extends Volunteer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date vlStartDate;
	private Date vlEndDate;
	private boolean vlActive;

	public VolunteerLeader() {
		super();
	}

	public VolunteerLeader(User user) {
		super();
		this.setId(user.getId());
		this.setName(user.getName());
		this.setEmail(user.getEmail());	
	}

	public VolunteerLeader(User user, boolean active, Date startDate, Date endDate) {
		super();
		this.setId(user.getId());
		this.setName(user.getName());
		this.setEmail(user.getEmail());		
		this.vlActive = active;
		this.vlStartDate = startDate;
		this.vlEndDate = endDate;
	}

	/**
	 * @return the vlStartDate
	 */
	public Date getVlStartDate() {
		return vlStartDate;
	}

	/**
	 * @param vlStartDate the vlStartDate to set
	 */
	public void setVlStartDate(Date vlStartDate) {
		this.vlStartDate = vlStartDate;
	}

	/**
	 * @return the vlEndDate
	 */
	public Date getVlEndDate() {
		return vlEndDate;
	}

	/**
	 * @param vlEndDate the vlEndDate to set
	 */
	public void setVlEndDate(Date vlEndDate) {
		this.vlEndDate = vlEndDate;
	}

	/**
	 * @return the vlActive
	 */
	public boolean isVlActive() {
		return vlActive;
	}

	/**
	 * @param vlActive the vlActive to set
	 */
	public void setVlActive(boolean vlActive) {
		this.vlActive = vlActive;
	}
	
	
	@Override
	public boolean isActiveRole(UserRole role) {
		return role.equals(UserRole.VolunteerLeader) && this.getId() > 0 && vlStartDate != null;
	}
    
    
}
