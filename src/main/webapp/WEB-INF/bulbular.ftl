<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/> 
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/customcss/style.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Entry</title>
</head>
<body>
	
	<div class="container">
		<form action = "<@spring.url "/j_spring_security_check"/>" method = "Post" role = "form">
			<div class="form-group col-md-4"></div>
			<div class="form-group col-md-4 vcenter">
				<h3>Admin Form<h3>
				<#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && 
					Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
						<div class="alert alert-danger">
							<@spring.message "login.badcredentials"/>
						</div>
				</#if> 
				<input type = "text" id= "userLogin" name = "username"
				class="form-control" placeholder = "Login" />
				<br/>
				
				<input type = "password" id = "userPassword" name = "password" 
				class="form-control" placeholder = "Password"/>
				<br/>

				<button type = "submit" class="btn btn-info">
					Log In
				</button>
				<input type="hidden"
					name="${_csrf.parameterName}"
					value="${_csrf.token}"/>
			</form>
			</div>
		</form>
	</div>

</body>
</html>