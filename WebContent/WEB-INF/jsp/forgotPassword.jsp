<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/bootstrap.min.css" type="text/css">
	<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/font-awesome.min.css" type="text/css">
<title>Chatit | Password Recovery</title>
<style>
#changeForm input.error {
	border: 1px solid #B80000;
}

#changeForm input.valid {
	border: 1px solid #00C957;
}
</style>
</head>
<body>

<div class="container">
		<div style="margin:0 auto; width:50%; margin-top:50px;">
			<form id="changeForm" class="well" action="http://chatitwebservices-env.elasticbeanstalk.com/services/changePassword" method="POST">
			
				<h1 style="color:#1C86EE; text-align:center;">Chatit</h1>
			
				<h3 style="text-align:center;">Password Recovery</h3>
				<hr>
				<div class="row">
					<small>*All fields required</small>
				</div>
				<div class="row errors">
					<small id="mismatch" style="visibility:hidden;">*Passwords do not match</small>
				</div>
			
					<input id="pass" type="password" name="new_password" class="form-control" placeholder="New Password">
					<br/>
					<input id="confPass" type="password" name="conf_password" class="form-control" placeholder="Confirm Password">			
		
			
			
				<hr>
				<button type="submit" class="btn btn-primary btn-lg">
					<b>Change Password</b>
				</button>
			</form>
		</div>
	</div>

</body>
  	<script
		src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script
		src="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
		<script type="text/javascript"
		src="assets/js/jquery.validate.min.js"></script>
	<script type="text/javascript"
		src="assets/js/additional-methods.js"></script>
	<script type="text/javascript" src="assets/js/main.js"></script>
</html>