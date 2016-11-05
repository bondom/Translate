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
		<#include "/fragments/authtranslatorheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			Your balance is: ${balance}
			You can  <a href="<@spring.url "/translator/balance/withdrawp"/>">withdraw</a> 
			funds.
		</div>
	</div>
	</div>
</body>
</html>