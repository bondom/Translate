<#ftl encoding="UTF-8">
<html>
	<body>
		Hello, ${client.getFirstName()} ${client.getLastName()}.</br>
		<p><a href = "${webRootPath}/translators/${translator.getId()}">${translator.getFirstName()} ${translator.getLastName()}</a>	responsed on
		<a href = "${webRootPath}/ads/${ad.getId()}">${ad.getName()}</a>				
		<br>
		<a href="${webRootPath}/client/reject?radId=${radId}">Reject</a>						
		
		<a href="${webRootPath}/client/accept?radId=${radId}">Accept</a>
	</body>
</html>