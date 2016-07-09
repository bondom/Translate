<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Login Form</title>
</head>
<body>
	<div class="container">
	  	<div class="panel panel-default">
			<div class="panel-heading">
				<a href = "<@spring.url "/registration"/>" class="btn btn-info" role="button">Sign In</a>
			</div>
			<div class="panel-body" style = "margin: 0px">
				<#if resultRegistration??>
					<div class="alert alert-success">${resultRegistration}</div>
				</#if>
				<h3>Login</h3>
					
					<form action = "<@spring.url "/j_spring_security_check"/>" method = "Post" role = "form">
						<div class="form-group col-xs-5" >
								<#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && 
								Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
								    <div class="alert alert-danger">
										<@spring.message "login.badcredentials"/>
									</div>
								</#if> 
								<#if (param.error)??>
									<p>Invalid username or password
									</p>
								</#if>
								<#if (param.logout)??>
									<p> You have been logget out
									</p>
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
							</div>
					</form>
				</div>
		</div>
	</div>
	
	

</body>
</html>