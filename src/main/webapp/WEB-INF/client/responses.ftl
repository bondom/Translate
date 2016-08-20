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
			<#if respondedAds?has_content>
				<#list respondedAds as respondedAd>
					<#assign translator = respondedAd.translator>
					<#assign ad = respondedAd.ad>
					<p><a href = "<@spring.url "/translators/${translator.getId()}"/>">${translator.getFirstName()} ${translator.getLastName()}</a>	responsed on
					<a href = "<@spring.url "/ads/${ad.getId()}"/>">${ad.getName()}</a>				
					<p>at ${respondedAd.getDateTimeOfResponse()}
					<#assign adstatus = respondedAd.status>
					<p>Status:${adstatus}
					<#if adstatus.name()="SENDED">
						<form action = "<@spring.url "/client/reject"/>" method = "Post" role = "form">
							<input name="id" type="hidden" value="${respondedAd.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Reject
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</form>
						<form action = "<@spring.url "/client/accept"/>" method = "Post" role = "form">
							<input name="id" type="hidden" value="${respondedAd.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Accept
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</form>
					</#if>
				</#list>
				</br>
				<#list 1..numberOfPages as page>
					<a href="<@spring.url "/client/responses?page=${page}"/>">${page}</a>&nbsp
				</#list>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>