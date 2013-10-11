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


</head>
<body>
	<a href="itms-services://?action=download-manifest&url=${baseURL}/resources/mobile/manifest.plist">Install App</a>
</body>
</html>
