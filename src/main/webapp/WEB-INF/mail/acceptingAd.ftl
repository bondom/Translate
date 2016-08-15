<#ftl encoding="UTF-8">
<html>
	<body>
		Hello, ${translator.getFirstName()} ${translator.getLastName()}.</br>
		<p>Client accepts your response on 
		<a href="${webRootPath}/ads/${ad.getId()}">${ad.getName()}</a>.
		<p>You can begin translate the text.
		<p>Remember, that end date is ${ad.getEndDate()}			
		<br>
	</body>
</html>