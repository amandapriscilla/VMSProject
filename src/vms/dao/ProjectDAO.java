package vms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vms.modal.Project;
import vms.modal.ProjectTemplate;
import vms.modal.User;
import vms.modal.VolunteerLeader;

public class ProjectDAO {
	
	
	private ConnectionManager manager = null;
	
	public ProjectDAO() {
		super();
		manager = ConnectionManager.getInstance();
	}

	public int create(Project project){
		ProjectTemplate template = project.getTemplatedBy();
		int id = 0;
		if (template == null ) return 0;
		else if(template.getId() == 0){
			id = this.createTemplate(template);			
			template.setId(id);
		}
		id = 0;
		String sql = "insert into Project (startTime, endTime, templatedBy, active) values (?, ?, ?, 1)";
		
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection =  manager.getConnection();
			statement = connection.prepareStatement(sql);
			Timestamp tmstp1 = new java.sql.Timestamp(project.getStartTime().getTime());
			Timestamp tmstp2 = new java.sql.Timestamp(project.getEndTime().getTime());
			statement.setTimestamp(1, tmstp1);
			statement.setTimestamp(2, tmstp2);
			statement.setInt(3, template.getId());
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1); 
				System.out.println("Project "+id+ " inserted");
			}
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		} 
			
		return id;
	}
	
	public int createTemplate(ProjectTemplate template) {
		String sql = "insert into ProjectTemplate (name, description, address, createdBy, creationDate, active) "
				+ " values (?, ?, ?, ?, CURDATE(), 1)";
		int id = 0;
		Connection connection = null;
		try {
			connection =  manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, template.getName());
			statement.setString(2, template.getDescription());
			statement.setString(3, template.getAddress());
			statement.setInt(4, template.getCreatedBy().getId());
			
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1); 
				System.out.println("Project Template "+id+ " inserted");
			}
		
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		} 
		
		return id;
	}

	public void update(Project project, List<String> columns){
		if (project.getId() == 0 || columns.size() == 0) return;
		
		String sql = manager.getUpdateSQL("Project", columns);
		System.out.println(sql);
		
		Connection connection = null;
		try {
			connection =  manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			int index = 1;
			for (String column : columns) {
				if(column.equals("startTime")){
					statement.setTimestamp(index, new java.sql.Timestamp(project.getStartTime().getTime()));
					index++;
				} else if(column.equals("endTime")){
					statement.setTimestamp(index, new java.sql.Timestamp(project.getEndTime().getTime()));
					index++;
				} else if(column.equals("templatedBy")){
					statement.setInt(index, project.getTemplatedBy().getId());
					index++;
				} else if(column.equals("ledBy")){
					statement.setInt(index, project.getLedBy().getId());
					index++;
				} else if(column.equals("active")){
					statement.setBoolean(index, project.isActive());
					index++;
				}
			}
			statement.setInt(index, project.getId());
			statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		} 
	}
	
	public void update(ProjectTemplate template, List<String> columns){
		if (template.getId() == 0 || columns.size() == 0) return;
		
		String sql = manager.getUpdateSQL("ProjectTemplate", columns);
		System.out.println(sql);
		
		Connection connection = null;
		try {
			connection =  manager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			int index = 1;
			for (String column : columns) {
				if(column.equals("name")){
					statement.setString(index, template.getName());
					index++;
				} else if(column.equals("address")){
					statement.setString(index, template.getAddress());
					index++;
				} else if(column.equals("description")){
					statement.setString(index, template.getDescription());
					index++;
				} else if(column.equals("active")){
					statement.setBoolean(index, template.isActive());
					index++;
				}
			}
			statement.setInt(index, template.getId());
			statement.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		} 
	}	

	public Project findById(int id, boolean active) {
		String sql = "select p.id, p.startTime, p.endTime, p.active, p.ledBy, vl.name as VLName, vl.email AS VLEmail, "
				+ " t.id AS template, t.name, t.description, t.address, t.active AS tActive "
				+ " from Project p inner join ProjectTemplate t ON p.templatedBy = t.id "
				+ " left join Users vl ON p.ledBy = vl.id ";
		
		if(active){
			sql+= " WHERE p.active=1 ";
		}
		sql+= " and p.id=? ";
		Project project = new Project();
		Connection connection = null;
		try {
			connection = manager.getConnection();
			PreparedStatement statement  = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()){
				project.setId(result.getInt("id"));
				project.setStartTime(new Date(result.getTimestamp("startTime").getTime()));
				project.setEndTime(new Date(result.getTimestamp("endTime").getTime()));
				project.setActive(result.getBoolean("active"));
				ProjectTemplate template = new ProjectTemplate();
				template.setName(result.getString("name"));
				template.setDescription(result.getString("description"));
				template.setAddress(result.getString("address"));
				template.setActive(result.getBoolean("tActive"));
				project.setTemplatedBy(template);
				VolunteerLeader vl = new VolunteerLeader();
				vl.setId(result.getInt("ledBy"));
				vl.setName(result.getString("VLName"));
				vl.setEmail(result.getString("VLEmail"));
				project.setLedBy(vl);
				System.out.println(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		}
		return project;
	}
	
	public List<Project> selectAll(boolean active){
		List<Project> list = new ArrayList<Project>();
		String sql = "select p.id, p.startTime, p.endTime, p.active, p.ledBy, vl.name as VLName, vl.email AS VLEmail, "
				+ " t.id AS template, t.name, t.description, t.address, t.active AS tActive "
				+ " from Project p inner join ProjectTemplate t ON p.templatedBy = t.id "
				+ " left join Users vl ON p.ledBy = vl.id ";
		
		if(active){
			sql+= " WHERE p.active=1 ";
		}
		sql += " ORDER BY p.startTime DESC";
		Connection connection = null;
		try {
			connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			while (result.next()){
				Project project = new Project();
				project.setId(result.getInt("id"));
				project.setStartTime(new Date(result.getTimestamp("startTime").getTime()));
				project.setEndTime(new Date(result.getTimestamp("endTime").getTime()));
				project.setActive(result.getBoolean("active"));
				ProjectTemplate template = new ProjectTemplate();
				template.setName(result.getString("name"));
				template.setDescription(result.getString("description"));
				template.setAddress(result.getString("address"));
				template.setActive(result.getBoolean("tActive"));
				project.setTemplatedBy(template);
				VolunteerLeader vl = new VolunteerLeader();
				vl.setId(result.getInt("ledBy"));
				vl.setName(result.getString("VLName"));
				vl.setEmail(result.getString("VLEmail"));
				project.setLedBy(vl);
				list.add(project);
				System.out.println(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			manager.close(connection);
		}
		return list;
	}


	public List<Project> getProjectsLedBy(User user) {
		List<Project> list = new ArrayList<Project>();
		String sql = "select p.id, p.startTime, p.endTime, p.active, "
				+ " t.id AS template, t.name, t.description, t.address, t.active AS tActive"
				+ " from Project p, ProjectTemplate t"
				+ " where p.templatedBy = t.id "
				+ " AND p.ledBy = ? ";
		sql += " ORDER BY p.startTime DESC";
		
		Connection connection = null;
		try {
			connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();
			while (result.next()){
				Project project = new Project();
				project.setId(result.getInt("id"));
				project.setStartTime(new Date(result.getTimestamp("startTime").getTime()));
				project.setEndTime(new Date(result.getTimestamp("endTime").getTime()));
				project.setActive(result.getBoolean("active"));
				ProjectTemplate template = new ProjectTemplate();
				template.setName(result.getString("name"));
				template.setDescription(result.getString("description"));
				template.setAddress(result.getString("address"));
				template.setActive(result.getBoolean("tActive"));
				project.setTemplatedBy(template);
				project.setLedBy(new VolunteerLeader(user));
				list.add(project);
				System.out.println(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			manager.close(connection);
		}
		return list;
	}
	
	public List<Project> getProjectsAttendedBy(User user) {
		List<Project> list = new ArrayList<Project>();
		String sql = "select p.id, p.startTime, p.endTime, p.active, p.ledBy, vl.name as VLName, vl.email AS VLEmail, "
				+ " t.id AS template, t.name, t.description, t.address, t.active AS tActive"
				+ " from Project p inner join ProjectTemplate t ON p.templatedBy = t.id "
				+ " left join Users vl ON p.ledBy = vl.id "
				+ " where a.attends = p.id AND a.attendedBy = ? ";
		sql += " ORDER BY p.startTime DESC";
				
		Connection connection = null;
		try {
			connection = manager.getConnection();
			PreparedStatement statement;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();
			while (result.next()){
				Project project = new Project();
				project.setId(result.getInt("id"));
				project.setStartTime(new Date(result.getTimestamp("startTime").getTime()));
				project.setEndTime(new Date(result.getTimestamp("endTime").getTime()));
				project.setActive(result.getBoolean("active"));
				ProjectTemplate template = new ProjectTemplate();
				template.setName(result.getString("name"));
				template.setDescription(result.getString("description"));
				template.setAddress(result.getString("address"));
				template.setActive(result.getBoolean("tActive"));
				project.setTemplatedBy(template);
				list.add(project);
				VolunteerLeader vl = new VolunteerLeader();
				vl.setId(result.getInt("ledBy"));
				vl.setName(result.getString("VLName"));
				vl.setEmail(result.getString("VLEmail"));
				project.setLedBy(vl);
				System.out.println(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			manager.close(connection);
		}
		return list;
	}
	
	public List<Project> findByName(String name, boolean active){
		List<Project> list = new ArrayList<Project>();
		String sql = "select p.id, p.startTime, p.endTime, p.active, p.ledBy, vl.name as VLName, vl.email AS VLEmail, "
				+ " t.id AS template, t.name, t.description, t.address, t.active AS tActive"
				+ " from Project p inner join ProjectTemplate t ON p.templatedBy = t.id "
				+ " left join Users vl ON p.ledBy = vl.id "
				+ " where p.templatedBy = t.id and t.name like ?";
		sql += " ORDER BY p.startTime DESC";
		
		if(active){
			sql+= " and p.active=1";
		}
		Connection connection = null;
		try {
			connection = manager.getConnection();
			PreparedStatement statement  = connection.prepareStatement(sql);
			statement.setString(1, "%"+name+"%");
			ResultSet result = statement.executeQuery();
			while (result.next()){
				Project project = new Project();
				project.setId(result.getInt("id"));
				project.setStartTime(new Date(result.getTimestamp("startTime").getTime()));
				project.setEndTime(new Date(result.getTimestamp("endTime").getTime()));
				project.setActive(result.getBoolean("active"));
				ProjectTemplate template = new ProjectTemplate();
				template.setName(result.getString("name"));
				template.setDescription(result.getString("description"));
				template.setAddress(result.getString("address"));
				template.setActive(result.getBoolean("tActive"));
				project.setTemplatedBy(template);
				VolunteerLeader vl = new VolunteerLeader();
				vl.setId(result.getInt("ledBy"));
				vl.setName(result.getString("VLName"));
				vl.setEmail(result.getString("VLEmail"));
				project.setLedBy(vl);
				list.add(project);
				System.out.println(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				manager.close(connection);
		}
		return list;
	}

}
