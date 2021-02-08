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
import vms.dao.ProjectDAO;
import vms.modal.Project;
import vms.modal.ProjectTemplate;
import vms.modal.Staff;
import vms.modal.User;
import vms.utils.Alert;

/**
 * Servlet implementation class UserController
 */
@WebServlet("/ProjectController")
public class ProjectController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProjectController() {
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
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		String address = request.getParameter("address");
		
		User curUser = VMSApp.getInstance().getCurrentUser();

		try {
			if (curUser == null) throw new Exception("User session expired! No login credentials found!");
			ProjectTemplate template = null;
			Project project = null;
			ProjectDAO dao = new ProjectDAO();
					
			if (request.getParameter("action")!= null && request.getParameter("action").equals("update_project")){
				int id = Integer.parseInt(request.getParameter("project_id"));
				project = dao.findById(id, false);
				template = project.getTemplatedBy();
				template.setName(name);
				template.setDescription(description);
				template.setAddress(address);
				String complete_inactive = request.getParameter("complete_inactive");
				if(complete_inactive != null) template.setActive(false);
				else template.setActive(true);
				
				List<String> columns = new ArrayList<String>();
				columns.add("name");
				columns.add("description");
				columns.add("address");
				columns.add("active");
				
				dao.update(template, columns);
				
				String date = request.getParameter("date");
				String start = request.getParameter("start");
				String end = request.getParameter("end");
				project.setStartTime(date, start);
				project.setEndTime(date, end);
				
				String inactive = request.getParameter("inactive");
				if(inactive != null) project.setActive(false);
				else project.setActive(true);
				
				columns = new ArrayList<String>();
				columns.add("startTime");
				columns.add("endTime");
				columns.add("active");
				dao.update(project, columns);
			} else {
				int qtt = 0;
				if (request.getParameter("quantity") != null)
					qtt = Integer.parseInt(request.getParameter("quantity"));
				
				template = new ProjectTemplate();
				template.setName(name);
				template.setDescription(description);
				template.setAddress(address);
				Staff staff = new Staff();
				staff.setId(curUser.getId());
				template.setCreatedBy(staff);
				template.setCreatedBy(staff);
				int id = dao.createTemplate(template);
				template.setId(id);
				// going over every date and times for each project
				for (int i = 1; i <= qtt; i++) {
					project = new Project();
					project.setTemplatedBy(template);
					
					String date = request.getParameter("date_"+i);
					String start = request.getParameter("start_"+i);
					String end = request.getParameter("end_"+i);
					if(date != null && start != null && end != null) {
						System.out.println(date + " " + start + " - " + end);					
						
						project.setStartTime(date, start);
						project.setEndTime(date, end);
	
						dao.create(project);		
					}
				}
			}
			HttpServletRequest req = (HttpServletRequest) request;
			if (VMSApp.getInstance().getCurrentUser() != null){
				RequestDispatcher rd = req.getRequestDispatcher("dashboard.jsp");
				request.setAttribute("alert", new Alert("success", "Project saved!"));
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
