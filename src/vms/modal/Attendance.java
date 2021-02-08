package vms.modal;

import java.io.Serializable;

public class Attendance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private double hours;
	private AttendanceStatus status;
	private Project attends;
	private Volunteer attendedBy;

	
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
	 * @return the hours
	 */
	public double getHours() {
		return hours;
	}

	/**
	 * @param hours the hours to set
	 */
	public void setHours(double hours) {
		this.hours = hours;
	}

	/**
	 * @return the status
	 */
	public AttendanceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}
	
	/**
	 * @return the attends
	 */
	public Project getAttends() {
		return attends;
	}

	/**
	 * @param attends the attends to set
	 */
	public void setAttends(Project attends) {
		this.attends = attends;
	}

	/**
	 * @return the attendedBy
	 */
	public Volunteer getAttendedBy() {
		return attendedBy;
	}

	/**
	 * @param attendedBy the attendedBy to set
	 */
	public void setAttendedBy(Volunteer attendedBy) {
		this.attendedBy = attendedBy;
	}

}
