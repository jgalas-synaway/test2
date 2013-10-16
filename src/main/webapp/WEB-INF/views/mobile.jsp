<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<html>

<head>
<title>1places</title>
<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.6.3/leaflet.css" />
<!--[if lte IE 8]>
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.6.3/leaflet.ie.css" />
<![endif]-->
<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL"
	value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />


<style type="text/css">
	a {
		font-size: 50px;
	}

	#install-button {
		border: 2px dashed #BEB16E;
		border-radius: 15px;
		width: 400px;
		text-align: center;
		padding: 10px;
		margin: auto;
	}

	#content {
		padding-top: 50px;
	}
</style>
</head>
<body>
	<div id="content">
		<div id="install-button">
			<a href="itms-services://?action=download-manifest&url=${manifestUrl}">Install application</a>
		</div>
	</div>
</body>
</html>

