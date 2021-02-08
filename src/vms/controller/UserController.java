package vms.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vms.VMSApp;
import vms.dao.UserDAO;
import vms.modal.User;
import vms.utils.Alert;

/**
 * Servlet implementation class UserController
 */
@WebServlet("/UserController")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserController() {
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
		String id = request.getParameter("id");
		String name = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		try {
			if (id == null || id.equals("")) {
				User user = new User();
				user.setEmail(email);
				user.setName(name);
				user.setPassword(password);
				UserDAO dao = new UserDAO();
				dao.create(user);
				HttpServletRequest req = (HttpServletRequest) request;
				if (VMSApp.getInstance().getCurrentUser() != null){
					RequestDispatcher rd = req.getRequestDispatcher("dashboard.jsp");
					request.setAttribute("alert", new Alert("success", "User saved!"));
					request.setAttribute("page", "users");
					rd.forward(request, response);
				} else {
					RequestDispatcher rd = req.getRequestDispatcher("index.jsp");
					request.setAttribute("error", "Session expired!");
					rd.forward(request, response);
				}
			} else {
				
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
