package vms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vms.dao.AttendanceDAO;
import vms.dao.ProjectDAO;
import vms.dao.UserDAO;
import vms.modal.Attendance;
import vms.modal.AttendanceStatus;
import vms.modal.Project;
import vms.modal.ProjectTemplate;
import vms.modal.Staff;
import vms.modal.User;
import vms.modal.UserRole;
import vms.modal.Volunteer;
import vms.utils.Alert;

public class VMSApp {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		UserDAO dao = new UserDAO();
		User user = dao.doLogin("email", "password");
		System.out.println(user.getId());
		System.out.println(user.getRoles());
		dao.createRole(user, UserRole.Staff);
		UserRole r = UserRole.Staff;
		System.out.println(r.toString());

		ProjectTemplate template = new ProjectTemplate();
		template.setActive(true);
		template.setName("Saturday's Bread");
		template.setDescription("Help to serve people in need a meal on Saturday");
		template.setAddress("Arlington Church, Test Ave");
		template.setId(1);
		
		Staff staff = new Staff();
		staff.setId(user.getId());
		template.setCreatedBy(staff);
		
		Project project = new Project();
		project.setTemplatedBy(template);
		Date start = new Date();
		start.setHours(11);
		start.setMinutes(0);
		start.setSeconds(0);
		project.setStartTime(start);
		
		Date end = new Date();
		end.setHours(14);
		end.setMinutes(0);
		end.setSeconds(0);
		project.setEndTime(end);
		project.setActive(true);
		
		ProjectDAO pdao = new ProjectDAO();
		pdao.create(project);

	}
	
	private static VMSApp instance = null;
	
	public static VMSApp getInstance(){
		if(instance == null) instance = new VMSApp();
		return instance;
	}
	
	public Alert alert = null;
	
	private User currentUser = null;
	
	public void setLoggedUser(User user){
		this.currentUser = user;
	}
	
	public User getCurrentUser(){
		return currentUser;
	}
	
	public List<Project> getProjects(){
		ProjectDAO dao = new ProjectDAO();
		if(this.currentUser != null && this.currentUser.getRoles().contains(UserRole.Staff)) {
			return dao.selectAll(false);
		} else {
			return dao.selectAll(true);
		}
	}
	
	public List<Project> getProjectsByName(String name){
		ProjectDAO dao = new ProjectDAO();
		if(this.currentUser != null && this.currentUser.isStaff()) {
			return dao.findByName(name, false);
		} else {
			return dao.findByName(name, true);
		}
	}
	
	public List<User> getUsers(){
		UserDAO dao = new UserDAO();
		if(this.currentUser != null && this.currentUser.isStaff()) {
			return dao.selectAll();
		} else {
			return new ArrayList<User>();
		}
	}
	
	public Project getProject(int id){
		ProjectDAO dao = new ProjectDAO();
		Project project = null;
		if(this.currentUser != null && (this.currentUser.isStaff() || this.currentUser.isVolunteerLeader())) {
			project = dao.findById(id, false);
			AttendanceDAO attDao = new AttendanceDAO();
			List<Attendance> attendanceList = attDao.getAttendanceForProject(project);
			project.setAttendaceList(attendanceList);
		} else {
			project = dao.findById(id, true);
		}
		return project;
	}
	
	public List<Project> getProjectsLedBy(User user){
		ProjectDAO dao = new ProjectDAO();
		return dao.getProjectsLedBy(VMSApp.getInstance().getCurrentUser());
	}
	
	public List<Project> getProjectsAttendedBy(User user){
		ProjectDAO dao = new ProjectDAO();
		return dao.getProjectsAttendedBy(VMSApp.getInstance().getCurrentUser());
	}
	
	public Attendance getAttendance(User user, Project project){
		AttendanceDAO dao = new AttendanceDAO();
		Attendance attendance = new Attendance();
		attendance.setAttendedBy(new Volunteer(user));
		attendance.setAttends(project);
		attendance = dao.getAttendance(attendance);
		return attendance;
	}

}
