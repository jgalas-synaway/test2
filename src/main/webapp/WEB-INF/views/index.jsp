<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
	<c:url var="jq" value="/scripts/jquery-1.9.1.min.js" />
	<script type="text/javascript" src="${jq}"></script>
<body>

MENU:
<div>
	<ul>
		<li><a href="<c:url value="/frontend/clear-data" />">Clear data</a></li>
		<li><a href="<c:url value="/frontend/other" />">Other</a></li>
	</ul>
</div>

</body>
</html>
