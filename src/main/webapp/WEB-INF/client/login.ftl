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
			<#include "/fragments/initclientheader.ftl">
			<div class="panel-body" style = "margin: 0px">
				<#if msg??>
					<div class="alert alert-success">${msg}</div>
				</#if>
				<#if RequestParameters.error?? && Session.SPRING_SECURITY_LAST_EXCEPTION??>
				     <div class="alert alert-danger">
						${Session.SPRING_SECURITY_LAST_EXCEPTION.message} 
					 </div>
				</#if>
				<h3>Login</h3>
					
					<form action = "<@spring.url "/j_spring_security_check"/>" method = "Post" role = "form">
						<div class="form-group col-xs-5" >
								<#if RequestParameters.logout??>
									<div class="alert alert-success">
										You have benn logged out successfully
									</div>
								</#if>
								<#--<#if logout??>
									<div class="alert alert-success">
										${logout}
									</div>
								</#if>-->
								<input type = "text" id= "userLogin" name = "username"
								class="form-control" placeholder = "Login" />
								<br/>
								<input type = "password" id = "userPassword" name = "password" 
								class="form-control" placeholder = "Password"/>
								<br/>

								<button type = "submit" class="btn btn-info">
									Log In
								</button>
								<#if loginUpdate??>
								<#else>
								Remember Me: <input type="checkbox" name="remember-me" />
								</#if>
								<a href = "<@spring.url "/client/registration"/>" role="button">Sign In</a>
								<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
								</form>
							</div>
					</form>
				</div>
		</div>
	</div>
	
	

</body>
</html>