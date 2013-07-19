var Reports = function(token){

	var self = this;
	
	$("#from_date").datepicker({ dateFormat: "yy-mm-dd" });
	$("#to_date").datepicker({ dateFormat: "yy-mm-dd" });

	$("#generate-report").click(function () {
		$.ajax({
			"dataType" : 'json',
			"type" : "GET",
			"url" : baseUrl + "/reports/activity?from=" +
				$("#from_date").datepicker().val() + " 00:00" +
				"&to=" + $("#to_date").datepicker().val() + " 23:59" +
				"&access_token=" + token
		}).done(function(json) {
			$('#active-users').text(json.activeUsers);
			$('#activity-report').show();
		});
	});
	
};