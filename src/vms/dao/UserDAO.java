package vms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vms.modal.Staff;
import vms.modal.User;
import vms.modal.UserRole;
import vms.modal.Volunteer;
import vms.modal.VolunteerLeader;

public class UserDAO {
	
	
	private ConnectionManager manager = null;
	
	public UserDAO() {
		super();
		manager = ConnectionManager.getInstance();
	}

	public void create(User user){
		String sql = "insert into Users (name, email, password) values (?, ?, ?)";
		
		
		Connection connection =  manager.getConnection();
		try {
			connection =  manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPassword());
			statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			manager.close(connection);
		}
	}
	
	public List<User> selectAll(){
		List<User> list = new ArrayList<User>();
		String sql = "SELECT u.id, u.name, u.email, s.id AS sid, s.active AS sActive, "
				+ " v.id AS vid, v.active AS vActive, vl.id AS vlid, vl.active AS vlActive " 
				+ " FROM Users u LEFT JOIN Staff s ON u.id = s.id"
				+ " LEFT JOIN Volunteer v ON u.id = v.id "
				+ " LEFT JOIN VolunteerLeader vl ON v.id = vl.id ";
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			while (result.next()){
				User user = new User();
				user.setId(result.getInt("id"));
				user.setName(result.getString("name"));
				user.setEmail(result.getString("email"));
				if (result.getInt("sid")>0 && result.getBoolean("sActive")){
					user.getRoles().add(UserRole.Staff);
				}
				if (result.getInt("vid")>0 && result.getBoolean("vActive")){
					user.getRoles().add(UserRole.Volunteer);
				}
				if (result.getInt("vlid")>0 && result.getBoolean("vlActive")){
					user.getRoles().add(UserRole.VolunteerLeader);
				}
				list.add(user);
				System.out.println(user);
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public User doLogin(String email, String password) {
		User user = null;
		String sql = "SELECT u.id, u.name, u.email, s.id AS sid, s.active AS sActive, "
				+ " v.id AS vid, v.active AS vActive, vl.id AS vlid, vl.active AS vlActive " 
				+ " FROM Users u LEFT JOIN Staff s ON u.id = s.id"
				+ " LEFT JOIN Volunteer v ON u.id = v.id "
				+ " LEFT JOIN VolunteerLeader vl ON v.id = vl.id "
				+ " where u.email = ? and u.password = ?";
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, email);
			statement.setString(2, password);
			ResultSet result = statement.executeQuery();
			if(result.next()){
				user = new User();
				user.setId(result.getInt("id"));
				user.setName(result.getString("name"));
				user.setEmail(result.getString("email"));
				if (result.getInt("sid")>0 && result.getBoolean("sActive")){
					user.getRoles().add(UserRole.Staff);
				}
				if (result.getInt("vid")>0 && result.getBoolean("vActive")){
					user.getRoles().add(UserRole.Volunteer);
				}
				if (result.getInt("vlid")>0 && result.getBoolean("vlActive")){
					user.getRoles().add(UserRole.VolunteerLeader);
				}
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return user;
	}

	public void createRole(User user, UserRole role) {
		String sql = "";
		System.out.println(role.toString());
		// if already has the role, only activate it
		User userInRole = getRole(user, role);
		
		if (userInRole != null) {
			if (!userInRole.isActiveRole(role)) {
				this.updateUserRole(userInRole, role, true, userInRole);
			}
			return;
		}
		
		sql = "insert into " + role.toString() + 
				"(id, startDate, active) values (?, CURDATE(), 1)";
		
		PreparedStatement statement = null;
		try {
			Connection connection = manager.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			statement.execute();
			manager.close(connection);
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	


	
	
	public void updateUserRole(User user, UserRole role, boolean active, User userInRole) {		
		// check if there exists the role and if the role has the same active as given
		if (active && userInRole == null){
			if (!existsRole(user, role)){
				this.createRole(user, role);		
				return;
			}
		} else if (userInRole.isActiveRole(role) == active) return;
				
		List<String> columns = new ArrayList<String>();
		columns.add("active");
		String sql = manager.getUpdateSQL(role.toString(), columns);
		System.out.println(sql);
		
		PreparedStatement statement = null;
		try {
			Connection connection =  manager.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setBoolean(1, active);
			statement.setInt(2, user.getId());
			statement.execute();
			manager.close(connection);
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public boolean existsRole(User user, UserRole role){
		String sql = "select active from " + role.toString() + " where id = ?";
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();
			if (result.next()){
				return true;
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean existsRoleActive(User user, UserRole roleTable){
		String sql = "select active from "+  roleTable.toString() +" where u.id = ?";
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			if (result.next()){
				return result.getBoolean("active");
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public User getUser(int id) {
		User user = null;
		String sql = "SELECT u.id, u.name, u.email, s.id AS sid, s.active AS sActive, "
				+ " v.id AS vid, v.active AS vActive, vl.id AS vlid, vl.active AS vlActive " 
				+ " FROM Users u LEFT JOIN Staff s ON u.id = s.id"
				+ " LEFT JOIN Volunteer v ON u.id = v.id "
				+ " LEFT JOIN VolunteerLeader vl ON v.id = vl.id "
				+ " where u.id = ?";
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if(result.next()){
				user = new User();
				user.setId(result.getInt("id"));
				user.setName(result.getString("name"));
				user.setEmail(result.getString("email"));
				if (result.getInt("sid")>0 && result.getBoolean("sActive")){
					user.getRoles().add(UserRole.Staff);
				}
				if (result.getInt("vid")>0 && result.getBoolean("vActive")){
					user.getRoles().add(UserRole.Volunteer);
				}
				if (result.getInt("vlid")>0 && result.getBoolean("vlActive")){
					user.getRoles().add(UserRole.VolunteerLeader);
				}
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return user;
	}
	
	public User getRole(User user, UserRole role){
		String sql = "select id, active, startDate, endDate from " + role.toString() + " where id = ?";
		User userRole = null;
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();
			if (result.next()){
				boolean active = result.getBoolean("active");
				Date startDate = new Date(result.getTimestamp("startDate").getTime());
				
				Date endDate = null;
				if (result.getTimestamp("endDate") != null)
					endDate =  new Date(result.getTimestamp("endDate").getTime());
				
				switch (role) {
				case Staff: 
					userRole = new Staff(user, active, startDate, endDate);
					break;
				case VolunteerLeader:
					userRole = new VolunteerLeader(user, active, startDate, endDate);
				case Volunteer:
					userRole = new Volunteer(user, active, startDate, endDate);
					break;
				}
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userRole;
	}
	
	
}
