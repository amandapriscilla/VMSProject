<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>VMS App</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid">
<div class="row">
  <div class="col-md-12">
  	<div class="page-header">
		<h1>VMS - Volunteer Management System</h1>
	</div>
	
	<br>
	<form role="form" class="form-horizontal" name="form1" method="POST" action="Login" id="form1">
		<div class="form-group" class="col-sm-8">		 
			<label for="email" class="col-sm-2 control-label">
				Email:
			</label>
			<div class="col-sm-6">
				<input type="email" class="form-control" id="email" name="email">
			</div>
		</div>
		<div class="form-group" class="col-sm-8">
			<label for="password" class="col-sm-2 control-label">
				Password
			</label>
			<div class="col-sm-6">
				<input type="password" class="form-control" id="password" name="password">
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-8">
				<input type="submit" value="Login" class="btn btn-default"/>

			</div>
		</div>
		<% if (request.getAttribute("error")!=null) {  %>
		
			<div class="alert alert-dismissable alert-danger">
				 
				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">
					×
				</button>
				<h4>
					Alert!
				</h4> <strong>Warning!</strong> <%=request.getAttribute("error") %>.
			</div>
		<% } %>
 	</form>
  </div>
</div>
</div>

</body>
</html>
