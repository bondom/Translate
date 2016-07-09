<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<#include "/fragments/authheader.ftl">
	<h5>Please upload a image</h5>
	<form method="post" action="<@spring.url "/personal/save"/>" enctype="multipart/form-data">
		<input type="file" name="file"/>
		<input type="submit"/>
	</form>


</body>
</html>