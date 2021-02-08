<%@page import="vms.modal.Volunteer"%>
<%@page import="vms.modal.AttendanceStatus"%>
<%@page import="vms.modal.Attendance"%>
<%@page import="vms.modal.UserRole"%>
<%@page import="vms.dao.UserDAO"%>
<%@page import="vms.modal.User"%>
<%@page import="vms.modal.VolunteerLeader"%>
<%@page import="java.util.List"%>
<%@page import="vms.modal.ProjectTemplate"%>
<%@page import="vms.modal.Project"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" 
    import="vms.VMSApp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>VMS App - Control Panel</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">

<style>
	.navbar-inverse .navbar-nav > li > a, .navbar-fixed-top a:link, .navbar-fixed-top a:visited {
		color: white;
	}
	.navbar-inverse .navbar-brand:focus, .navbar-inverse .navbar-brand:hover,
	.navbar-inverse .navbar-nav > li > a:hover, .navbar-inverse .navbar-nav > li > a:focus {
		font-weight: bold;
	}
	.navbar.navbar-default.navbar-inverse.navbar-fixed-top {
		background-color: #336699";
	}
</style>

</head>
<body>

<%
// Beginning of page properties setup
	VMSApp app = VMSApp.getInstance();
	User cur_user = app.getCurrentUser();
 	String curPage = "projects";
  	if (request.getAttribute("page") != null) curPage = request.getAttribute("page").toString();
  	else if (request.getParameter("page") != null) curPage = request.getParameter("page");
%>
<div class="container-fluid">
   <div class="row">
	<div class="col-md-12">
		<nav class="navbar navbar-default navbar-inverse navbar-fixed-top" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
					 <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
				</button> <a class="navbar-brand" href="#">VMSApp</a>
			</div>
			
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav">
			  <!--  <li class="active">
						<a href="#">Link</a>
					</li>
					<li>
						<a href="#">Link</a>
					</li>
					<li class="dropdown">
						 <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown<strong class="caret"></strong></a>
						<ul class="dropdown-menu">
							<li>
								<a href="#">Action</a>
							</li>
							<li>
								<a href="#">Another action</a>
							</li>
							<li>
								<a href="#">Something else here</a>
							</li>
							<li class="divider">
							</li>
							<li>
								<a href="#">Separated link</a>
							</li>
							<li class="divider">
							</li>
							<li>
								<a href="#">One more separated link</a>
							</li>
						</ul>
					</li> -->
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li>
						<a><%= ((cur_user == null) ? "User" : cur_user.getName() + " - " + cur_user.getEmail()) %></a>
					</li>
					<% if (cur_user != null && cur_user.getRoles().size() > 0) { %>
					<li class="dropdown" style="margin-right: 20px">
						 <a href="#" class="dropdown-toggle" data-toggle="dropdown">Roles<strong class="caret"></strong></a>
						<ul class="dropdown-menu">
							<% for(UserRole role : cur_user.getRoles()) { %>
							<li>
								<a><%= role.toString()%></a>
							</li>
							<% } %>
							<li class="divider">
							</li>
							<li>
								<a href="index.jsp" style="color: gray">Logout</a>
							</li>
						</ul>
					</li>
					<% } %>
				</ul>
			</div>
			
		</nav>
	</div>
  </div>
  <div class="row" style="margin-top:50px">
  	<div class="col-md-3">
		<h3 class="text-primary"> Control Panel </h3>
		
		<ul class="nav nav-pills nav-stacked">
			<li class="<%= ((curPage.equals("projects"))? "active" : "") %>">
				<a onclick="loadPage('projects')">Projects</a>
			</li>
			<% if (curPage.equals("project_details")) { %>
				<li class="active">
					<a onclick="loadPage('projects')">Project Details</a>
				</li>
			<% } %>
			<% if (cur_user.isVolunteer()) { %>
			<li class="<%= (curPage.equals("volunteer"))? "active" : "" %>">
				<a onclick="loadPage('volunteer')">My Volunteer Profile</a>
			</li>
			<% } if (cur_user.isVolunteerLeader()) { %>
			<li class="<%= (curPage.equals("leader"))? "active" : "" %>">
				<a onclick="loadPage('leader')">My Leader Profile</a>
			</li>
			<% } if (cur_user.isStaff()) { %>
			<li class="<%= (curPage.equals("users"))? "active" : "" %>">
				<a onclick="loadPage('users')">Users</a>
			</li>
			<% } %>
			
		</ul>
	</div>
  	<!--  DYNAMIC CENTER STARTS HERE -->
    <div class="col-md-9"> 	
<%  	
	if (curPage.equals("projects")) { 
%>
  	
<div id="projects" class="page">
	<h3 class="text-primary"> Volunteer Projects </h3>
	<form class="form-inline" role="search">
		<div class="form-group" class="col-md-6">
			<input type="text" class="form-control" name="search">
		</div> 
		<button type="submit" class="btn btn-default">
			Search
		</button>
	</form>
	<table class="table table-hover table-striped table-condensed">
		<thead>
			<tr>
				<th> # </th> <th> Project </th> <th> Date </th> <th> Time </th> <th>Leader</th><th></th>
			</tr>
		</thead>
		<tbody>
  <%
  // Implementation of the Query Projects UC
  	String search = request.getParameter("search");
  	List<Project> projects;
    if(search == null || search.equals("")){
    	projects = app.getProjects();
    } else {
    	projects = app.getProjectsByName(search);
    }
	if(projects.size() == 0){
   %>
      <tr>
	 	<td colspan="5"> There is no project available <%= (search == null || search == "") ? " at this time" :  " for this search!" %> </td>
	 </tr>		
  <%	
	}
	for (Project project : projects) {
		ProjectTemplate template = project.getTemplatedBy();
		if (template == null) {
			template = new ProjectTemplate();
		}
		VolunteerLeader leader = project.getLedBy(); 
		if(!cur_user.isStaff() && !cur_user.isVolunteerLeader()) {
			if(leader == null) { continue; } // skip projects without leader
		}
	%>
		 <tr class="active">
		 	<td><%=project.getId()%></td>
			<td><%=template.getName()%></td>
			<td><%=project.getDate() + "(" + project.getDayOfWeek() + ")"%></td>
			<td><%=project.getStart() + " - " + project.getEnd()%></td>
	<%	
		if (leader == null || leader.getId() == 0) {  %>
			<td>-</td>
	<%	} else {  %>
			<td><%=leader.getName() %></td>
	<%	} %>
			<td>
			<button class="btn btn-default" type="button" onclick="showProject(<%=project.getId()%>)">
				<em class="glyphicon glyphicon-info-sign"></em> Details
			</button>
			</td>
		 </tr>
<% } %>
					
		</tbody>
	</table>
	<hr/>
<% if (cur_user.isStaff()) {  %>	
	<h3 class="text-primary">Create a new Project </h3>
	<div class="col-md-6"> 
		<form role="form" class="form-horizontal" method="POST" action="Project">
		<div class="form-group">
				<label for="name">
					Name:
				</label>
				<input type="text" class="form-control" id="name" name="name" required/>
			</div>
			<div class="form-group">
				<label for="description">
					Description:
				</label>
				<input type="text" class="form-control" id="description" name="description"  required/>
			</div>
			<div class="form-group">
				<label for="address">
					Address:
				</label>
				<input type="text" class="form-control" id="address" name="address" required/>
			</div>
			
			<h3>Project Ocurrences</h3>
			<table id="project_occurrence_table" class="table table-hover table-striped table-condensed">
				<thead>
					<tr>
						<th> # </th> <th>Date</th> <th>Start Time</th> <th>End Time</th>
					</tr>
				</thead>
				<tbody>								
				</tbody>
			</table>
			
			<div id="project_occurrences_inputs">
				<input type="hidden" id="quantity" name="quantity" value="0"/>
				<input type="hidden" name="action" value="create_project"/>
			</div>
			
			<div id="project_occurrence_new">
				<div class="form-group">
					<label for="project_date">
						Date:
					</label>
					<input type="date" class="form-control" id="project_date">
				</div>
				<div class="form-group">
					<label for="project_start_time">
						Start Time:
					</label>
					<input type="time" class="form-control" id="project_start_time">
				</div>
				<div class="form-group">
					<label for="project_end_time">
						End Time:
					</label>
					<input type="time" class="form-control" id="project_end_time">
				</div>
				<div class="form-group">
					<input type="button" value="Add Occurrence" onclick="addProjectOccurrence()"  class="btn btn-default"/>
				</div>
			</div>
			
			<button type="submit" class="btn btn-default">
				Submit Form
			</button>
		</form>
	</div>
<% }  %>	
</div>

</div>


<% } if (curPage.equals("project_details")) { 
	int id = Integer.parseInt(request.getParameter("pid"));
	Project project = app.getProject(id);
	ProjectTemplate template = project.getTemplatedBy();
	VolunteerLeader leader = project.getLedBy();
	String action = request.getParameter("action");
	System.out.println(project.getDateReverse());
	boolean canEdit = (action != null && action.equals("edit") && cur_user != null && cur_user.isStaff());
%>
<div id="project_details" class="page">
	<h3 class="text-primary"> Project Details </h3>
<% if(cur_user != null && cur_user.isStaff()) { %>

	<div class="btn-group btn-group-lg">		 
		<button class="btn btn-default <%= (canEdit)? "" : " active"%>" type="button" onclick="showProject(<%=project.getId()%>)">
			<em class="glyphicon glyphicon-info-sign"></em> View Mode
		</button> 
		<button class="btn btn-default <%= (canEdit)? " active" : ""%>" type="button" onclick="editProject(<%=project.getId()%>)">
			<em class="glyphicon glyphicon-edit"></em> Edit Mode
		</button>
	</div>
	<h3>Project Details</h3>
	
<% if(canEdit) { %>
				
	<div class="col-md-6"> 
		<form role="form" class="form-horizontal" method="POST" action="Project">
			<div class="form-group">
				<label for="name">
					Id:
				</label>
				<input type="text" class="form-control" id="project_id" name="project_id" value="<%=project.getId()%>" disabled="disabled"/>
			</div>
			<div class="form-group">
				<label for="name">
					Name:
				</label>
				<input type="text" class="form-control" id="name" name="name" value="<%=template.getName()%>" required/>
			</div>
			<div class="form-group">
				<label for="description">
					Description:
				</label>
				<input type="text" class="form-control" id="description" name="description" value="<%=template.getDescription()%>"  required/>
			</div>
			<div class="form-group">
				<label for="address">
					Address:
				</label>
				<input type="text" class="form-control" id="address" name="address" value="<%=template.getAddress()%>" required/>
			</div>			
			
			
				<div class="form-group">
					<label for="project_date">
						Date:
					</label>
					<input type="text" class="form-control" name="date" value="<%= project.getDate()%>">
				</div>
				<div class="form-group">
					<label for="project_start_time">
						Start Time:
					</label>
					<input type="time" class="form-control" name="start" value="<%= project.getStart()%>">
				</div>
				<div class="form-group">
					<label for="project_end_time">
						End Time:
					</label>
					<input type="time" class="form-control" name="end" value="<%= project.getEnd()%>">
				</div>
				<div class="checkbox">
				  <label>
				    <input type="checkbox" name="inactive" value="">
				    Make this project occurrence inactive
				  </label>
				</div>
				<div class="checkbox">
				  <label>
				    <input type="checkbox" name="complete_inactive" value="">
				    Make the complete project inactive (the set of occurrences)
				  </label>
				</div>
				<input type="hidden" name="action" value="update_project"/>
			
			<button type="submit" class="btn btn-default">
				Save
			</button>
		</form>
	</div>	
	
<% }
} // end of if user is staff

if (!canEdit){ %> 
	
	<h4><%= template.getName() %></h4>
	<p><%= template.getDescription() %></p>
	<p><em><%= template.getAddress() %></em></p>
	<p>Date: <%= project.getDate()%></p>
	<p>Start Time: <%= project.getStart()%></p>
	<p>End Time: <%= project.getEnd()%></p>
	<p>Volunteer Leader: <%= (leader != null && leader.getId()>0) ? leader.getName() : "TBD" %></p>	

<% if ((leader == null || leader.getId()==0) && cur_user.isVolunteerLeader()) { %>
		<form role="form" class="form-horizontal" method="POST" action="Attendance">
			<input type="hidden" name="action" value="leader_sign_up"/>
			<input type="hidden" name="volunteer_id" value="<%=cur_user.getId() %>"/>
			<input type="hidden" name="project_id" value="<%=project.getId() %>"/>
			<button type="submit" class="btn btn-primary">
				Volunteer Leader Sign up
			</button>
		</form>
		<hr/>
		<h4>Volunteers Attendance</h4>
		<table class="table table-hover table-striped table-condensed">
		<thead>
			<tr>
				<th> # </th> <th> Name </th> <th> Email </th> <th> Hours </th> <th>Status</th><th></th>
			</tr>
		</thead>
		<tbody>	
	<% for (Attendance attendance : project.getAttendaceList()) {
		Volunteer attendee = attendance.getAttendedBy();
		if(attendee == null || attendee.getId() == 0) {
			if(leader == null) { continue; } // skip attendance without info
		}
	%>
		 <tr class="active">
		 	<td><%=attendee.getName()%></td>
		 	<td><%=attendee.getEmail()%></td>
			<td><%=attendance.getHours()%></td>
			<td><%=attendance.getStatus()%></td>
			<td>
			<button class="btn btn-default" type="button" onclick="showAttendance(<%=attendee.getId()%>, <%=project.getId()%>)">
				<em class="glyphicon glyphicon-info-sign"></em> Details
			</button>
			</td>
		 </tr>
<% 		}  %>
					
		</tbody>
	</table>
		<hr/>
<% } else if (leader != null && leader.getId() == cur_user.getId()) { %> %>
	<form role="form" class="form-horizontal" method="POST" action="Attendance">
		<input type="hidden" name="action" value="leader_remove"/>
		<input type="hidden" name="volunteer_id" value="<%=cur_user.getId() %>"/>
		<input type="hidden" name="project_id" value="<%=project.getId() %>"/>
		<button type="submit" class="btn btn-primary">
			Remove me from leadership opportunity
		</button>
	</form>
	<hr/>
<% } if ((leader == null && leader.getId()>0) && cur_user.isVolunteer()) { 
		Attendance attendance = app.getAttendance(cur_user, project); 
		if (attendance != null && attendance.getStatus().equals(AttendanceStatus.Registered)) { %>
		<form role="form" class="form-horizontal" method="POST" action="Attendance">
			<input type="hidden" name="action" value="volunteer_remove"/>
			<input type="hidden" name="volunteer_id" value="<%=cur_user.getId() %>"/>
			<input type="hidden" name="project_id" value="<%=project.getId() %>"/>
			<button type="submit" class="btn btn-primary">
				Remove me from Volunteer Opportunity
			</button>
		</form>
			
<% 			}
		}
	} 
} // end of curpage equals project_details
///////////////////////////////////
%>

<% if (curPage.equals("attendance")) {
	User u = new User();
	u.setId(Integer.parse(request.getParameter("vid")));
	Project project = app.getProject(request.getParameter("pid"));
	Attendance attendance = app.getAttendance(user, project); 
	if (attendance != null && attendance.getAttendedBy() != null) {%>		

		<form role="form" class="form-horizontal" method="POST" action="Attendance">
			<input type="hidden" name="action" value="attendance_verification"/>
			
			
			
			
			<button type="submit" class="btn btn-primary">
				Save
			</button>
		</form>

  <% } %>
<% } if (curPage.equals("users")) { 
	String action = request.getParameter("action");
	if (action != null){
		UserDAO dao = new UserDAO();
		User u = dao.getUser(Integer.parseInt(request.getParameter("user_id")));
		if (action.endsWith("Staff")){			
			dao.updateUserRole(u, UserRole.Staff, action.startsWith("Add"), null);
		} else if (action.endsWith("Leader")){
			dao.updateUserRole(u, UserRole.VolunteerLeader, action.startsWith("Add"), null);
		} 
		else if (action.endsWith("Volunteer")){
			dao.updateUserRole(u, UserRole.Volunteer, action.startsWith("Add"), null);
		}
	}


%>
<div id="users" class="page">
	<h3 class="text-primary"> Access Management </h3>
		<table class="table table-hover table-striped table-condensed">
			<thead>
				<tr>
					<th> # </th> <th> Name </th> <th> Email </th> <th> Staff </th><th>Leader</th><th>Volunter</th><th></th>
				</tr>
			</thead>
			<tbody>
  <%
	List<User> users = app.getUsers();
	if(users.size() == 0){
   %>
	      <tr>
		 	<td colspan="5"> There is no users available at this time! </td>
		 </tr>		
  <%	
	}
	for (User user : users) {
	%>
		 <tr class="active">
		 	<td><%=user.getId()%></td>
			<td><%=user.getName()%></td>
			<td><%=user.getEmail()%></td>
			<td>
			<form action="dashboard.jsp" method="get">
				<input type="hidden" id="user_id" name="user_id" value="<%= user.getId()%>"/>
				<input type="hidden" name="page" value="users"/>
				<% if (user.isStaff()) { %>
					<input type="hidden" name="action" value="RemoveStaff"/>
					<input type="submit" name="btn" value="Remove Staff"/>
				<% } else { %>
				<input type="hidden" name="action" value="AddStaff"/>
					<input type="submit" name="btn" value="Add Staff"/>
				<% } %>
			</form>
			</td>
			<td>
			<form action="dashboard.jsp" method="get">
				<input type="hidden" name="page" value="users"/>
				<input type="hidden" id="user_id" name="user_id" value="<%= user.getId()%>"/>
				<% if (user.isVolunteerLeader()) { %>
					<input type="hidden" name="action" value="RemoveLeader"/>
					<input type="submit" name="btn" value="Remove Leader"/>
				<% } else { %>
				<input type="hidden" name="action" value="AddLeader"/>
					<input type="submit" name="btn" value="Add Leader"/>
				<% } %>
			</form>
			</td>
			<td>
			<form action="dashboard.jsp" method="get">
				<input type="hidden" name="page" value="users"/>
				<input type="hidden" id="user_id" name="user_id" value="<%= user.getId()%>"/>
				<% if (user.isVolunteer()) { %>
					<input type="hidden" name="action" value="RemoveVolunteer"/>
					<input type="submit" name="btn" value="Remove Volunteer"/>
				<% } else { %>
				<input type="hidden" name="action" value="AddVolunteer"/>
					<input type="submit" name="btn" value="Add Volunteer"/>
				<% } %>
			</form>
			</td>
		 </tr>
<% } %>			
		</tbody>
	</table>
	<h3 class="text-primary">Create a new User </h3>
	<div class="col-md-6"> 
		<form role="form" class="form-horizontal" method="POST" action="User">
			<div class="form-group">
				<label for="username">
					Name:
				</label>
				<input type="text" class="form-control" id="username" name="username">
			</div>
			<div class="form-group">
				<label for="email">
					Email:
				</label>
				<input type="email" class="form-control" id="email" name="email">
			</div>
			<div class="form-group">
				<label for="password">
					Password:
				</label>
				<input type="password" class="form-control" id="password" name="password">
			</div> 
			<button type="submit" class="btn btn-default">
				Submit
			</button>
		</form>
	</div>
</div>
	

<% } if (curPage.equals("leader") || curPage.equals("volunteer")) { %>		

<div id="profile" class="page">
	<h3 class="text-primary"> My Volunteer<%= ((curPage.equals("leader")) ? " leader " : " ") %>Projects </h3>
	<table class="table table-hover table-striped table-condensed">
		<thead>
			<tr>
				<th> # </th> <th> Project </th> <th> Date </th> <th> Time </th> <th>Leader</th><th></th>
			</tr>
		</thead>
		<tbody>
		
<%
  	List<Project> projects;
	if(cur_user.isVolunteerLeader() || curPage.equals("leader")){
		projects = app.getProjectsLedBy(cur_user);
	} else {
		projects =  app.getProjectsAttendedBy(cur_user);
	}
	if(projects.size() == 0){
   %>
      <tr>
	 	<td colspan="5"> There is no project available in your profile. </td>
	 </tr>		
  <%	
	}
	for (Project project : projects) {
		ProjectTemplate template = project.getTemplatedBy();
		if (template == null) {
			template = new ProjectTemplate();
		}
		VolunteerLeader leader = project.getLedBy(); 
		if(!cur_user.isStaff() && !cur_user.isVolunteerLeader()) {
			if(leader == null) { continue; } // skip projects without leader
		}
	%>
		 <tr class="active">
		 	<td><%=project.getId()%></td>
			<td><%=template.getName()%></td>
			<td><%=project.getDate() + "(" + project.getDayOfWeek() + ")"%></td>
			<td><%=project.getStart() + " - " + project.getEnd()%></td>
	<%	
		if (leader == null) {  %>
			<td>-</td>
	<%	} else {  %>
			<td>leader.getName()</td>
	<%	} %>
			<td>
			<button class="btn btn-default" type="button" onclick="showProject(<%=project.getId()%>)">
				<em class="glyphicon glyphicon-info-sign"></em> Details
			</button>
			</td>

		 </tr>
<% }%>
					
		</tbody>
	</table>
</div>

<% } %>


 	</div>
 	
	
 	</div>
  </div>
</div>
</body>

    <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/scripts.js"></script>
<script>
	function showProject(id){
		window.location.href = '//' + location.host + location.pathname + "?page=project_details&pid="+id;
	}
	function editProject(id){
		window.location.href = '//' + location.host + location.pathname + "?page=project_details&action=edit&pid="+id;
	}
	function showAttendance(volunteer_id, project_id){
		window.location.href = '//' + location.host + location.pathname + "?page=attendance&vid="+volunteer_id+"&pdid="+project_id;
	}
	function loadPage(page){
		window.location.href = '//' + location.host + location.pathname + "?page="+page;
	}
	
	function addProjectOccurrence(){
		var date = $("#project_occurrence_new #project_date").val();
		var start =  $("#project_occurrence_new #project_start_time").val();
		var end = $("#project_occurrence_new #project_end_time").val();
		var qtd =  $("#project_occurrence_table tbody tr").length + 1;

		var content = "<tr><td>"+ qtd + "</td><td>"+ date+ "</td><td>"+start + "</td><td>"+end + "</td></tr>";
		$("#project_occurrence_table tbody").append(content);
		console.log(date + " " + start + " " + end);
		$("#project_occurrences_inputs #quantity").val(qtd);
		$("#project_occurrences_inputs").append("<input type='hidden' name='date_"+ qtd + "' value='" +date+"'/>");
		$("#project_occurrences_inputs").append("<input type='hidden' name='start_"+ qtd + "' value='" +start+"'/>");
		$("#project_occurrences_inputs").append("<input type='hidden' name='end_"+ qtd + "' value='" +end+"'/>");
	}
</script>
</html>