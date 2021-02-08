package vms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vms.VMSApp;
import vms.dao.AttendanceDAO;
import vms.dao.ProjectDAO;
import vms.modal.Attendance;
import vms.modal.AttendanceStatus;
import vms.modal.Project;
import vms.modal.User;
import vms.modal.Volunteer;
import vms.modal.VolunteerLeader;
import vms.utils.Alert;

/**
 * Servlet implementation class UserController
 */
@WebServlet("/AttendanceController")
public class AttendanceController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AttendanceController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// pegando os parâmetros do request
		String action = request.getParameter("action");
		
		
		try {
			int volunteer_id = Integer.parseInt(request.getParameter("volunteer_id"));
			int project_id = Integer.parseInt(request.getParameter("project_id"));
			User cur_user = VMSApp.getInstance().getCurrentUser();
			Project project = new Project();
			project.setId(project_id);
			if (action.endsWith("leader_sign_up")) {
				if(cur_user.getId() == volunteer_id && cur_user.isVolunteerLeader()) {
					project.setLedBy(new VolunteerLeader(cur_user));
					ProjectDAO pdao = new ProjectDAO();
					List<String> columns = new ArrayList<String>();
					columns.add("ledBy");
					pdao.update(project, columns);
				} else throw new Exception("The current user is not allowed to perform this operation!");
			} else {
				AttendanceDAO dao = new AttendanceDAO();
				
				Attendance attendance = new Attendance();
				attendance.setAttends(project);
				
				Volunteer v = new Volunteer();
				v.setId(volunteer_id);
				attendance.setAttendedBy(v);
				
				if (action.equals("volunteer_sign_up")){
					if(cur_user.getId() == volunteer_id)
						dao.create(attendance);
				} else if (action.equals("volunteer_remove")){
					attendance.setStatus(AttendanceStatus.Removed);
					
					
				} else if (action.equals("attendance_verification")){
					String hours = request.getParameter("attendance_hours");
					String status = request.getParameter("attendance_status");
					
					attendance.setHours(Double.parseDouble(hours));
					attendance.setStatus(AttendanceStatus.valueOf(status));
					dao.verifyAttendance(attendance);
				}  else throw new Exception("The current user is not allowed to perform this operation!");
			}

			HttpServletRequest req = (HttpServletRequest) request;
			if (VMSApp.getInstance().getCurrentUser() != null){
				RequestDispatcher rd = req.getRequestDispatcher("dashboard.jsp");
				request.setAttribute("alert", new Alert("success", "You are signed up for this project!"));
				request.setAttribute("page", "projects");
				rd.forward(request, response);
			} else {
				RequestDispatcher rd = req.getRequestDispatcher("index.jsp");
				request.setAttribute("error", "Session expired!");
				rd.forward(request, response);
			}
		} catch (Exception e) {
			HttpServletRequest req = (HttpServletRequest) request;
			RequestDispatcher rd = req.getRequestDispatcher("dashboard.jsp");
			request.setAttribute("alert", new Alert("error", e.getMessage()));
			System.out.println(e.getMessage());
			rd.forward(request, response);
		}
		
	}

}
