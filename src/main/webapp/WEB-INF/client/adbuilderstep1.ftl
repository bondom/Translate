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
	<div class="container">
	<div class="panel panel-default">
		<#include "/fragments/authclientheader.ftl">
		<div class="panel-body" style = "margin: 0px">
				
				<h4>First you need to choose type of advertisement</h4>
				<form method="get" action="<@spring.url "/client/adbuilder"/>">
					 <input type = "hidden" name = "page" 
										 value = "2" />
					<@spring.formSingleSelect "cTTBean.translateType" translateTypes/>
					<button type = "submit" class="btn btn-info">
						Next
					</button>
				</table>
				</form>
		</div>
	</div>
	</div>
</body>
</html>