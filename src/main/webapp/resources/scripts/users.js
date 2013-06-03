var Users = function(token){

	$('#users-table').dataTable(
		{
			"bProcessing" : true,
			"bJQueryUI": true,
			"aoColumns": [
			              { "mData": "firstName" },
			              { "mData": "lastName" },
			              { "mData": "login" },
			              { "mData": "role" },
			              { "mData": "email" },
			              { "mData": "id" }
			          ],
			"sAjaxSource" : baseUrl + "/users?access_token=" + token,
			"fnServerData" : function(sSource, aoData, fnCallback) {
				$.ajax({
					"dataType" : 'json',
					"type" : "GET",
					"url" : sSource,
					"data" : aoData
				}).done(
						function(json) {
							fnCallback({
								aaData : json
							});
						});
			},
			"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
				  var id = aData['id']; 

				  var imgTag = '<img src="'+baseUrl+'/resources/images/edit.png"/><img src="'+baseUrl+'/resources/images/delete.png"/>';
				  $('td:eq(5)', nRow).html(imgTag); // where 4 is the zero-origin visible column in the HTML

				  return nRow;
				}
		});
};