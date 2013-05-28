<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>

<head>
	<c:url var="jq" value="/resources/scripts/jquery-1.9.1.min.js" />
	<c:url var="app" value="/resources/scripts/app.js" />
	<c:url var="style" value="/resources/styles/style.css" />
	<c:url var="baseUrl" value="/"/>
	
	<link rel="stylesheet" href="${style}"/>
	
	<script type="text/javascript" src="${jq}"></script>
	<script type="text/javascript" src="${app}"></script>
	<script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
	
	<script type="text/javascript">
		var baseUrl = "${baseUrl}";
	</script>
	
</head>
<body>


<div id="main">
	<div id="menu">
	
	</div>
	<div id="map_canvas">map div</div>
</div>



</body>
</html>
