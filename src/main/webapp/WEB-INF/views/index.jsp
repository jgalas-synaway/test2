<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<html>

<head>
<c:url var="jq" value="/resources/scripts/jquery-1.9.1.min.js" />
<c:url var="jq_cookie" value="/resources/scripts/jquery.cookie.js" />

<c:url var="ui" value="/resources/scripts/ui/jquery.ui.core.js" />
<c:url var="ui_widget" value="/resources/scripts/ui/jquery.ui.widget.js" />
<c:url var="ui_dialog" value="/resources/scripts/ui/jquery.ui.dialog.js" />
<c:url var="ui_position"
	value="/resources/scripts/ui/jquery.ui.position.js" />
<c:url var="ui_button" value="/resources/scripts/ui/jquery.ui.button.js" />
<c:url var="ui_accordion"
	value="/resources/scripts/ui/jquery.ui.accordion.js" />
<c:url var="ui_tabs" value="/resources/scripts/ui/jquery.ui.tabs.js" />
<c:url var="datepicker" value="/resources/scripts/ui/jquery.ui.datepicker.js" />
<c:url var="dataTable"
	value="/resources/scripts/dataTables/media/js/jquery.dataTables.js" />
<c:url var="dataTableUpdate"
	value="/resources/scripts/dataTables/plugins/fnReloadAjax.js" />

<c:url var="dataTablePagination"
	value="/resources/scripts/dataTables/plugins/pagination.js" />

<c:url var="app" value="/resources/scripts/app.js" />
<c:url var="users" value="/resources/scripts/users.js" />
<c:url var="spots" value="/resources/scripts/spots.js" />
<c:url var="reports" value="/resources/scripts/reports.js" />

<c:url var="style" value="/resources/styles/style.css" />
<c:url var="ui_style"
	value="/resources/scripts/themes/base/jquery.ui.all.css" />
<c:url var="tableStyle"
	value="/resources/scripts/dataTables/media/css/jquery.dataTables.css" />


<link rel="stylesheet" href="${style}" />
<link rel="stylesheet" href="${ui_style}" />
<link rel="stylesheet" href="${tableStyle}" />

<script type="text/javascript" src="${jq}"></script>
<script type="text/javascript" src="${jq_cookie}"></script>
<script type="text/javascript" src="${ui}"></script>
<script type="text/javascript" src="${ui_widget}"></script>
<script type="text/javascript" src="${ui_dialog}"></script>
<script type="text/javascript" src="${ui_position}"></script>
<script type="text/javascript" src="${ui_button}"></script>
<script type="text/javascript" src="${ui_accordion}"></script>
<script type="text/javascript" src="${ui_tabs}"></script>
<script type="text/javascript" src="${datepicker}"></script>
<script type="text/javascript" src="${dataTable}"></script>
<script type="text/javascript" src="${dataTableUpdate}"></script>
<script type="text/javascript" src="${dataTablePagination}"></script>

<script type="text/javascript" src="${app}"></script>
<script type="text/javascript" src="${users}"></script>
<script type="text/javascript" src="${spots}"></script>
<script type="text/javascript" src="${reports}"></script>
<script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>

<script type="text/javascript">
	var baseUrl = "${pageContext.request.contextPath}";
	var mapView = null;
	var token = $.cookie('access_token') == undefined ? null : $
			.cookie('access_token');
	var user = null;
	var users = null;
	var spots = null;
	var reports = null;
	
	$.ajaxSetup({cache:false})

	$(function() {

		mapView = new MapView("map_canvas");

		if (token == undefined || token == null) {
			$("#loginFormBox").dialog(
					{
						title : "Authentication",
						closeOnEscape : false,
						open : function(event, ui) {
							//hide close button.
							$(this).parent().children().children(
									'.ui-dialog-titlebar-close').hide();
						}

					});
			$("#loginForm").submit(function() {
				$("#errorBox").empty();
				$.ajax({
					url : baseUrl + "/users/auth",
					dataType : "json",
					type : "POST",
					data : {
						login : $(this).find("#login").val(),
						password : $(this).find("#password").val()
					}
				}).done(function(data) {
					$("#loginFormBox").remove();
					token = data.token;
					$.cookie('access_token', token);
					mapView.spotStart(token);
					$("#main").css("visibility", "visible");
					me(token);
					users = new Users(token);
					spots = new Spots(token);
					reports = new Reports(token);
				}).fail(function(data) {
					var error = $.parseJSON(data.responseText).error;
					if (error.code == 504) {
						$("#errorBox").append("<p>Invalid login</p>");
					}
					if (error.code == 505) {
						$("#errorBox").append("<p>Invalid password</p>");
					}
				});
				return false;
			});

			$("#loginButton").button();
		} else {
			$("#loginFormBox").remove();
			mapView.spotStart(token);
			$("#main").css("visibility", "visible");
			me(token);
			users = new Users(token);
			spots = new Spots(token);
			reports = new Reports(token);
		}

		$("#accordion").accordion(
				{
					heightStyle : "fill",
					beforeActivate : function(event, ui) {
						var forElement = ui.newHeader.attr("href");
						if (forElement == undefined || forElement == null
								|| forElement == "") {
							forElement = ui.newHeader.data("for");
						}
						changeTab(forElement);
					},
					create : function(event, ui) {
						var forElement = ui.header.attr("href");
						if (forElement == undefined || forElement == null
								|| forElement == "") {
							forElement = ui.header.data("for");
						}
						changeTab(forElement);
					}

				});

		function changeTab(id) {
			element = $($("#content #" + id));
			var parent = element.parent();
			var childs = parent.children(".tab");
			childs.each(function(index) {
				$(this).css("z-index", index);
			});
			element.css("z-index", childs.size());
		}

		function me(token) {
			$.ajax({
				url : baseUrl + "/users/me?access_token=" + token,
				dataType : "json",
			}).done(
				function(data) {
					user = data;
					$("#userInfo").empty();
					$("#userInfo").append(
						'<p>'
						+ user.firstName
						+ ' '
						+ user.lastName
						+ ' ('+user.login+')'
						+ ' <a href="#" id="logout">logout</a></p>'
					);
					$("#userInfo #logout").click(function() {
						$.removeCookie('access_token');
						location.reload();
					});
					$("#accordion").accordion("refresh");
				});
		}

		$(window).resize(function() {
			$("#accordion").accordion("refresh");
		});

		$("#spot").change(function() {
			if ($(this).is(":checked")) {
				mapView.spotStart(token);
				$('#fakeDiv').css('display', 'block');
			} else {
				mapView.spotStop();
				$('#fakeDiv').css('display', 'none');
			}
		});

		$("#location").change(function() {
			if ($(this).is(":checked")) {
				mapView.locationStart(token);
			} else {
				mapView.locationStop();
			}
		});
		
		$("#fake").change(function() {
			if ($(this).is(":checked")) {
				mapView.showFake(true);
			} else {
				mapView.showFake(false);
			}
		});

		$("#accordion .link").click(function(event){
			var forElement = $(this).attr("href");
			if (forElement == undefined || forElement == null
					|| forElement == "") {
				forElement = $(this).data("for");
			}
			changeTab(forElement);
			event.preventDefault();
		});

	});
</script>

</head>
<body>

	<div id="loginFormBox">
		<form id="loginForm" action="">

			<label for="login">Login:</label> <input id="login" name="login"
				type="text" /> <label for="passwors">Password:</label> <input
				id="password" name="password" type="password" /> <input
				id="loginButton" type="submit" value="Login" />
			<div id="errorBox"></div>
		</form>

		<script>
			
		</script>
	</div>
	<div id="main" style="visibility: hidden;">
		<div id="menu">
			<div id="userInfo"></div>
			<div id="accordion">
				<h3 id="acc-1" data-for="tab-1">Map</h3>
				<div>
					<input id="spot" name="spot" type="checkbox" checked="checked" />
					<label for="spot">spots</label>
					<div id="fakeDiv">
						<input id="fake" name="fake" type="checkbox" checked="checked"/>
						<label for="fake">show fake spots</label>
					</div>
					<input id="location" name="location" type="checkbox" />
					<label for="location">users locations</label>
				</div>
				<h3 id="acc-2" data-for="tab-2">Administration</h3>
				<div>
					<p class="link" data-for="tab-2">
						Users
					</p>
					<p  class="link" data-for="tab-3">
						Spots
					</p>
				</div>
				<h3 id="acc-3" data-for="tab-3">Reports</h3>
				<div>
					<p class="link" data-for="tab-4">
						activity report
					</p>
				</div>
			</div>

		</div>
		<div id="content">


			<div id="tab-1" class="tab">
				<div id="map_canvas"></div>
			</div>
			<div id="tab-2" class="tab">
				<div class="tabWrapper">
					<h3>Users</h3>
					<table cellpadding="0" cellspacing="0" border="0" class="display"
						id="users-table">
						<thead>
							<tr>
								<th width="20%">First Name</th>
								<th width="20%">Last Name</th>
								<th width="20%">Login</th>
								<th width="15%">Role</th>
								<th width="15%">email</th>
								<th width="10%">edit</th>
							</tr>
						</thead>
						<tbody>
	
						</tbody>
					</table>
					<div id="add_user_btn" class="under_table_btn">
					Add user
					</div>
					<div id="refresch_user_btn" class="under_table_btn">
					Refresh
					</div>
					<div id="user_edit">
						<form>
							<input name="user_id" id="user_id" type="hidden"/>
							<div class="form_row">
								<label for="firstName">First Name:</label> 
								<input id="firstName" name="firstName" type="text" />
							</div>
							<div class="form_row">
								<label for="lastName">Last Name:</label> 
								<input id="lastName" name="lastName" type="text" />
							</div>
							<div class="form_row">
								<label for="login">Login:</label> 
								<input id="login" name="login" type="text" />
							</div>
							<div class="form_row">
								<label for="email">Email:</label> 
								<input id="email" name="email" type="text" />
							</div>
							<div class="form_row">
								<label for="role">Role:</label> 
								<select id="role" name="role">
									<option value="user">user</option>
									<option value="beta">beta</option>
									<option value="admin">admin</option>
								</select>
							</div>
							<div class="form_row"> 
								<label for="change_password">Change password:</label> 
								<input id="change_password" name="change_password" type="checkbox" />
							</div>
							
							<div class="form_row">
								<label for="new_password">Password:</label> 
								<input id="new_password" name="new_password" type="password" />
							</div>
							<input value="save" type="submit"/>
							<input id="user_cancel" value="cancel" type="button"/>
						</form>
					</div>
					<div id="user_delete">
						Are you sure you want to delete this item?
					</div>
				</div>
			</div>
			<div id="tab-3" class="tab">
				<div class="tabWrapper">
					<h3>Spots</h3>
					<table cellpadding="0" cellspacing="0" border="0" class="display"
						id="spots-table">
						<thead>
							<tr>
								<th width="20%">Latitude</th>
								<th width="20%">Longitude</th>
								<th width="20%">Timestamp</th>
								<th width="20%">Status</th>
								<th width="20%">edit</th>
							</tr>
						</thead>
						<tbody>
	
						</tbody>
					</table>
					<div id="add_spot_btn"  class="under_table_btn">
					Add spot
					</div>
					<div id="refresch_spot_btn" class="under_table_btn">
					Refresh
					</div>
					<div id="spot_edit">
						<form>
							<input name="spot_id" id="spot_id" type="hidden"/>
							<div class="form_row">
								<label for="latitude">Latitude:</label> 
								<input id="latitude" name="latitude" type="text" />
							</div>
							<div class="form_row">
								<label for="longitude">Longitude:</label> 
								<input id="longitude" name="longitude" type="text" />
							</div>
							<div class="form_row">
								<label for="timestamp">Timestamp:</label> 
								<input style="width:105px" id="timestamp" name="timestamp" type="text" />
								<input style="width:23px;margin-left:0;" id="hour" name="hour"/>:<input style="width:23px;margin-left:0;" id="minute" name="minute"/>:<input style="width:23px;margin-left:0;" id="second" name="second"/>
							</div>
							<div class="form_row">
								<label for="status">Status:</label> 
								<select id="status" name="status">
									<option value="free">free</option>
									<option value="occupied">occupied</option>
								</select>
							</div>
							<input value="save" type="submit"/>
							<input id="spot_cancel" value="cancel" type="button"/>
						</form>
					</div>
					<div id="spot_delete">
						Are you sure you want to delete this item?
					</div>
				</div>
			</div>
			<div id="tab-4" class="tab">
				<div style="width: 80%">
					<div style="text-align:center">
					<span style="display:inline-block; text-align:center">
						From: <span id="from_date"></span>
					</span>
					<span style="display:inline-block; text-align:center">
						To: <span id="to_date"></span>
					</span>
					<br /><br />
					<span>
					<button id="generate-report">Generate report</button>
					</span>
					</div>
					<br />
					<div id="activity-report" style="text-align:center; display:none">
					Active users: <span id="active-users"></span>
					</div>
				</div>
			</div>
		</div>

	</div>



</body>
</html>
