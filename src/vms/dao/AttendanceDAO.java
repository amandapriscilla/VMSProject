package vms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vms.modal.Attendance;
import vms.modal.AttendanceStatus;
import vms.modal.Project;
import vms.modal.Volunteer;

public class AttendanceDAO {
	
	
	private ConnectionManager manager = null;
	
	public AttendanceDAO() {
		super();
		manager = ConnectionManager.getInstance();
	}

	public void create(Attendance attendance) throws Exception{
		Project project = attendance.getAttends();
		if (project == null || project.getId() == 0) { 
			throw new Exception("No project specified for this attendance");
		}
		Volunteer volunteer = attendance.getAttendedBy();
		if (volunteer == null || volunteer.getId() == 0) { 
			throw new Exception("No volunteer specified for this attendance");
		}
		
		String sql = "insert into Attendance (attends, attendedBy, status) values (?, ?, ?)";
		
		PreparedStatement statement = null;
		try {
			Connection connection =  manager.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setInt(1, attendance.getAttends().getId());
			statement.setInt(2, attendance.getAttendedBy().getId());
			statement.setString(3, attendance.getStatus().toString());
			if (statement.execute()) {
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()){
					int id = rs.getInt(1);
					attendance.setId(id);
					System.out.println("Attendance registered "+ id + " successfully!");
				}
			}
			manager.close(connection);
		} catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void verifyAttendance(Attendance attendance) throws Exception {
		if (attendance.getId() == 0){
			Project project = attendance.getAttends();
			if (project == null || project.getId() == 0) { 
				throw new Exception("No project specified for this attendance");
			}
			Volunteer volunteer = attendance.getAttendedBy();
			if (volunteer == null || volunteer.getId() == 0) { 
				throw new Exception("No volunteer specified for this attendance");
			}
			attendance = getAttendance(attendance);
		}
		if(attendance.getId() == 0){
			throw new Exception("No attendance found for the given arguments!");
		}
		List<String> columns = new ArrayList<String>();
		columns.add("hours");
		columns.add("status");
		String sql = manager.getUpdateSQL("Attendance", columns);
		System.out.println(sql);
		
		Connection connection = null;
		try {
			connection =  manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			int index = 1;
			for (String column : columns) {
				if(column.equals("hours")){
					statement.setDouble(index, attendance.getHours());
					index++;
				} else if(column.equals("status")){
					statement.setString(index, attendance.getStatus().toString());
					index++;
				}
			}
			statement.setInt(index, attendance.getId());
			statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		} 
	}
	
	public Attendance getAttendance(Attendance attendance) {
		Project project = attendance.getAttends();
		
		String sql = "select a.id, u.id as uid, u.name, u.email, a.status, a.hours "
				+ " from Attendance a, Users u "
				+ " where a.attendedBy = u.id AND "
				+ " a.attends = ? and a.attendedBy = ?";
		Connection connection = null;	
		try {
			if (project == null || project.getId() == 0) { 
				throw new Exception("No project specified for this attendance");
			}
			Volunteer volunteer = attendance.getAttendedBy();
			if (volunteer == null || volunteer.getId() == 0) { 
				throw new Exception("No volunteer specified for this attendance");
			}
			
			connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, project.getId());
			statement.setInt(2, volunteer.getId());
			ResultSet result = statement.executeQuery();
			if (result.next()){
				volunteer.setId(result.getInt("uid"));
				volunteer.setName(result.getString("name"));
				volunteer.setEmail(result.getString("email"));
				attendance.setId(result.getInt("id"));
				attendance.setStatus(AttendanceStatus.valueOf(result.getString("status")));
				attendance.setHours(result.getDouble("hours"));
				System.out.println(volunteer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.close(connection);
		}
		return attendance;
		
	}

	public List<Attendance> getAttendanceForProject(Project project) {
		List<Attendance> list = new ArrayList<Attendance>();
		String sql = "select a.id, u.id as uid, u.name, u.email "
				+ " from Attendance a, Users u "
				+ " where a.attendedBy = u.id AND "
				+ " a.attends = ? ";
			
		try {
			Connection connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, project.getId());
			ResultSet result = statement.executeQuery();
			while (result.next()){
				Volunteer volunteer = new Volunteer();
				volunteer.setId(result.getInt("uid"));
				volunteer.setName(result.getString("name"));
				volunteer.setEmail(result.getString("email"));
				Attendance att = new Attendance();
				att.setId(result.getInt("id"));
				att.setAttendedBy(volunteer);
				att.setAttends(project);
				list.add(att);
				System.out.println(volunteer);
			}
			manager.close(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	

	
	

}
