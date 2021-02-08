package vms.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private Date startTime;
	private Date endTime;
	private boolean active;
	private ProjectTemplate templatedBy;
	private VolunteerLeader ledBy;
	
	private List<Attendance> attendaceList = new ArrayList<Attendance>();

	public Project() {
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
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	/**
	 * @return the templatedBy
	 */
	public ProjectTemplate getTemplatedBy() {
		return templatedBy;
	}

	/**
	 * @param templatedBy the templatedBy to set
	 */
	public void setTemplatedBy(ProjectTemplate templatedBy) {
		this.templatedBy = templatedBy;
	}
	
	/**
	 * @return the ledBy
	 */
	public VolunteerLeader getLedBy() {
		return ledBy;
	}

	/**
	 * @param ledBy the ledBy to set
	 */
	public void setLedBy(VolunteerLeader ledBy) {
		this.ledBy = ledBy;
	}
	
	/**
	 * @return the attendaceList
	 */
	public List<Attendance> getAttendaceList() {
		return attendaceList;
	}

	/**
	 * @param attendaceList the attendaceList to set
	 */
	public void setAttendaceList(List<Attendance> attendaceList) {
		this.attendaceList = attendaceList;
	}
	
	// Operations to format date
	
	@SuppressWarnings("deprecation")
	public String getDate(){
		int mm = startTime.getMonth() + 1;
		int dd = startTime.getDate();
		int yyyy = startTime.getYear() + 1900;
		return  mm + "/" + dd  + "/" + yyyy;
	}
	
	@SuppressWarnings("deprecation")
	public String getDateReverse(){
		int mm = startTime.getMonth() + 1;
		int dd = startTime.getDate();
		int yyyy = startTime.getYear() + 1900;
		return yyyy + "-" + mm  + "-" + dd;
	}
	
	@SuppressWarnings("deprecation")
	public String getStart(){
		String min = startTime.getMinutes() + "";
		if(min.length() == 1) min = "0"+min;
		return  startTime.getHours() + ":" + min;
	}
	@SuppressWarnings("deprecation")
	public String getEnd(){
		String min = endTime.getMinutes() + "";
		if(min.length() == 1) min = "0"+min;
		return  endTime.getHours() + ":" + min;
	}
	
	@SuppressWarnings("deprecation")
	public String getDayOfWeek(){
		switch (startTime.getDay()) {
			case 0: return "Sunday";
			case 1: return "Monday";
			case 2: return "Tuesday";
			case 3: return "Wednesday";
			case 4: return "Thursday";
			case 5: return "Friday";
			default: return "Saturday";
		}
	}
	
	public void setStartTime(String date, String time){	
		this.startTime = parseString(date, time);
	}
	
	public void setEndTime(String date, String time){
		this.endTime = parseString(date, time);
	}
	
	@SuppressWarnings("deprecation")
	private Date parseString(String date_string, String time){
		int yyyy, mm, dd;
	
		String[] split = date_string.split("-");
		if(split.length > 1) {
			yyyy = Integer.parseInt(split[0]);
			mm = Integer.parseInt(split[1]);
			dd = Integer.parseInt(split[2]);
		} else { 
			split = date_string.split("/");
			mm = Integer.parseInt(split[0]);
			dd = Integer.parseInt(split[1]);
			yyyy = Integer.parseInt(split[2]);		
		}
		int hh = Integer.parseInt(time.split(":")[0]);
		int min = Integer.parseInt(time.split(":")[1]);
		
		Date date = new Date();
		date.setYear(yyyy - 1900);
		date.setMonth(mm-1);
		date.setDate(dd);
		date.setHours(hh);
		date.setMinutes(min);
		return date;
	}
	
	/**
	 * @return the string that represents the object
	 */
	public String toString(){
		return getId() + " - " + getTemplatedBy().getName() + " - " + getDate() + " - Led by:" + getLedBy();
	}
	
}