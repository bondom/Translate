<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />

<html>
<head>
	<link rel="stylesheet" type = "text/css" 
					href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Group</title>
</head>
<body>
	<div class="panel panel-default">
		<#include "/fragments/authheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			<form action = "<@spring.url "/personal/students"/>" method = "POST" role = "form" >
				<div class="form-group col-xs-5">
					<@spring.bind "student.studentGroup"/>
					<input type="text"  id="studentGroup" name ="${spring.status.expression}" 
					class="form-control" required="true" placeholder="Type name of group"/>                    
				</div>
				<button type="submit" class="btn btn-info">
					<span class="glyphicon glyphicon-search"></span> Search
				</button>
				<br></br>
				<#list spring.status.errorMessages as error>
					<div class="alert alert-warning">${error}</div>
				</#list>
			</form>
			
			<form action = "<@spring.url "/getStudents"/>" method = "POST" role = "form">
				<#if studentsList??>
					<#list studentsList>
						<h3>Group ${groupName}</h3>
						<table class="table table-striped">
				        <thead>
				        <tr>
					        <td>#</td>
					        <td>Name</td>
					        <td>Average mark</td>
					        <td>Group</td>                                 
				        </tr>
				        </thead>
				        <#items as student>	
						    <tr class = "info">
						    	<td>${student.studentId}</td>
						        <td>${student.studentName}</td>
						        <td>${student.studentAverageMark}</td>
						        <td>${student.studentGroup}</td>
						   	</tr>
				        </#items>
						<#else>
							<div class="alert alert-info">  
					        	This group is empty
					        </div>
				    </#list>
				</#if>
			</form>	
		</div>
	</div>
</body>
</html>