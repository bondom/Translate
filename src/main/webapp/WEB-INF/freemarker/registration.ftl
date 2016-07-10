<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registration Form</title>
</head>
<body>
	
	<div class="container">
		<div class="jumbotron">
	  		 <h1>University Site</h1>
		    <p>In this site we can find students from all groups in our university</p>
	  	</div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<a href="<@spring.url "/login"/>" class="btn btn-info" role="button">Log In</a>
			</div>
			<div class="panel-body" style = "margin: 0px">
			<h3>Registration</h3>
				<form action = "<@spring.url "/registrationConfirm"/>" method = "Post" role = "form">
					<div class="form-group col-xs-5" >
							<#if resultRegistration??>
								<div class="alert alert-danger">${resultRegistration}</div>
							</#if>
							<@spring.bind "user.login"/>
							<input type = "text" id= "login" name="${(spring.status.expression)!"login"}"
							value = "${spring.status.value!""}" class="form-control" placeholder = "Login" />
							<br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							<@spring.bind "user.password"/>
							<input type = "password" id = "password" name = "${(spring.status.expression)!"password"}" 
							class="form-control" placeholder = "Password"/>
							<br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							<@spring.bind "user.email"/>
							<input type = "text" id = "email" name = "${(spring.status.expression)!"email"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Email"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "user.name"/>
							<input type = "text" id = "name" name = "${(spring.status.expression)!"name"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "user.surname"/>
							<input type = "text" id = "surname" name = "${(spring.status.expression)!"surname"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Surname"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "user.phoneNumber"/>
							<input type = "text" id = "phoneNumber" name = "${(spring.status.expression)!"phoneNumber"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Phone number"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
														
							
							<button type = "submit" class="btn btn-info">
								Sign In
							</button>
						</div>
				</form>
			</div>
		</div>
	</div>
	
</body>
</html>