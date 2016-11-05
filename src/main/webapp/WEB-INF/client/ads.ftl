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
			<#if error??>
					<div class="alert alert-danger">${error}</div>
			</#if>
			<#if msg??>
					<div class="alert alert-success">${msg}</div>
			</#if>
			<#if success??>
					<div class="alert alert-success">${success}</div>
			</#if>
			<#if adsWithStatusMessages?? && adsWithStatusMessages?size gt 0>
				<#list adsWithStatusMessages as adWithStatusMessage>
					<#assign ad = adWithStatusMessage.ad>
					<#assign statusMessage = adWithStatusMessage.statusMessage>
					<div>
						<p>Name: ${ad.getName()}
						<p>Description: ${ad.getDescription()}
						<p>Init Language: ${ad.getInitLanguage()}
						<p>Result Language: ${ad.getResultLanguage()}
						<p>TranslateType: ${ad.getTranslateType()}
						<#if ad.translateType.name()=="ORAL">
							<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
							<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
						</#if>
						<#if ad.translateType.name()=="WRITTEN">
							<p>End Date: ${ad.getEndDate()} 
							<p>File:
							<a href="<@spring.url "/download/${ad.id}"/>" target="_blank" >
									${ad.document.fileName}
							</a>
						</#if>
						<p>Status: ${ad.getStatus()}<br>
						<#if ad.status.name()=="SHOWED">
							${statusMessage}<br>
							<form action = "<@spring.url "/client/ads/delete"/>" method = "Post" role = "form">
								<input name="adId" type="hidden" value="${ad.getId()}"/>
								<button type = "submit" class="btn btn-info">
									Delete
								</button>
								<input type="hidden"
										name="${_csrf.parameterName}"
										value="${_csrf.token}"/>
							</form>
							<p>Refreshed:${ad.getPublicationDateTime()}
							<form action = "<@spring.url "/client/ads/refresh"/>" method = "Post" role = "form">
								<input name="adId" type="hidden" value="${ad.getId()}"/>
								<button type = "submit" class="btn btn-info">
									Refresh Date
								</button>
								<input type="hidden"
										name="${_csrf.parameterName}"
										value="${_csrf.token}"/>
							</form>
							<a href = "<@spring.url "/client/ads/edit?adId=${ad.getId()}"/>">EDIT</a>
						</#if>

						<#if ad.translator??>
							<#assign translator=ad.translator>
						</#if>

						<#if ad.translateType="WRITTEN">
							<#if ad.status.name()="ACCEPTED" ||
								 ad.status.name()="NOTCHECKED" ||
								 ad.status.name()="REWORKING">
								<div class="alert alert-warning">
									${statusMessage}				
								</div>
								<br>Translator: 
								<a href ="<@spring.url "/translators/${translator.id}"/>">
									${translator.firstName}&nbsp&nbsp${translator.lastName}
								</a>
							</#if>
							<#if ad.status.name()="CHECKED">
								<br>Translator: 
								<a href ="<@spring.url "/translators/${translator.id}"/>">
									${translator.firstName}&nbsp&nbsp${translator.lastName}
								</a>
								<div class="alert alert-success">
								${statusMessage}
									<form action = "<@spring.url "/client/payandget"/>" method = "Post" role = "form">
										<input name="adId" type="hidden" value="${ad.getId()}"/>
										<button type = "submit" class="btn btn-info">
											Pay And Get
										</button>
										<input type="hidden"
												name="${_csrf.parameterName}"
												value="${_csrf.token}"/>
									</form>
								</div>
							</#if>
							<#if ad.status.name()="PAYED">
								<div class="alert alert-success">
									${statusMessage}<br>
									<a href="<@spring.url "/downloadr/${ad.id}"/>" target="_blank" >
										${ad.resultDocument.fileName}
									</a>
								</div>
							</#if>
							<#if ad.status.name()="FAILED">
								<div class="alert alert-danger">
									Sorry, man..
								</div>
							</#if>
						</#if>

						<#if ad.translateType="ORAL">
							<#if ad.status.name()="ACCEPTED">
								<div class="alert alert-success">
									<#assign translatorPhone = ad.translator.phoneNumber>
									Translator's phone: ${translatorPhone}<br>
									${statusMessage}<br>
								</div> 
								After ending ${ad.getFinishDateTime()} you will can 
								<form action = "<@spring.url "/client/pay"/>" method="Post">
									<input name="adId" type="hidden" value="${ad.getId()}"/>
									<button type = "submit" class="btn btn-info">
										Pay 
									</button>
									<input type="hidden"
										name="${_csrf.parameterName}"
										value="${_csrf.token}"/>
								</form>
							</#if>
							<#if ad.status.name()="PAYED">
								<div class="alert alert-success">
									${statusMessage}<br>
								</div>
							</#if>
						</#if>

						<#if ad.status.name()="PAYED">
							<form action = "<@spring.url "/client/ads/clear"/>" method = "Post" role = "form">
								<input name="adId" type="hidden" value="${ad.getId()}"/>
								<button type = "submit" class="btn btn-info">
									Delete
								</button>
								<input type="hidden"
										name="${_csrf.parameterName}"
										value="${_csrf.token}"/>
							</form>
							<#if translator??>
								You can estimate work of translator: go to his profile:
								<a href ="<@spring.url "/translators/${translator.id}"/>">
									${translator.firstName}&nbsp&nbsp${translator.lastName}
								</a> and write comment.
							</#if>
						</#if>
					</div>
					</br>
				</#list>
			<#else>
				You haven't advertisements yet.</br>
			</#if>
			<a href = "<@spring.url "/client/adbuilder"/>" class="btn btn-info" role="button">Create Ad</a>
		</div>
	</div>
	</div>
</body>
</html>