<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>


	<form action="http://chatitwebservices-env.elasticbeanstalk.com/services/report" method="POST">
	
	<h1>Ch@It Webservice Tester</h1>

		<input id="pass" type="text" name="reporter" placeholder="Reporter">
					<br/>
		<input id="confPass" type="text" name="reportee" placeholder="Reportee">
		
		<input type="text" name="channel" placeholder="Channel">
		
		<input type="text" name="message" placeholder="Message">	
		
		<input type="submit" value="Submit">

	</form>


</body>
</html>