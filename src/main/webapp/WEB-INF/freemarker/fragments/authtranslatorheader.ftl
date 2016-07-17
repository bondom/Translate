<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<div class="panel-heading">
		<a href = "<@spring.url "/translator/index"/>" class="btn btn-info" role="button">LANGUAGES.RU</a>
		<kbd>
			<@security.authentication property = "principal.username"/>
		</kbd>
		<a href = "<@spring.url "/translator/profile"/>" class="btn btn-info" role="button">My Profile</a>
		<a href = "javascript:formSubmit()" class="btn btn-info" role="button">Log out</a>
	</div>
	<form action = "<@spring.url "/translator/logout"/>" method = "post" id = "logoutForm">
			<input type = "hidden" 
					name = "${_csrf.parameterName}"
					value = "${_csrf.token}"/>
		</form>
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
</body>
</html>