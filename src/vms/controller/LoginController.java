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

/**
 * Servlet implementation class UserController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
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
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		try {
			UserDAO dao = new UserDAO();
			User user = dao.doLogin(email, password);
			if (user != null){
				VMSApp app = VMSApp.getInstance();
				app.setLoggedUser(user);
				response.sendRedirect("dashboard.jsp");
			} else throw new Exception("User authentication invalid! Please try again!");
		} catch (Exception e) {
			HttpServletRequest req = (HttpServletRequest) request;
			RequestDispatcher rd = req.getRequestDispatcher("index.jsp");
			request.setAttribute("error", e.getMessage());
			request.setAttribute("page", "projects");
			System.out.println(e.getMessage());
			rd.forward(request, response);
		}
		
	}

}
