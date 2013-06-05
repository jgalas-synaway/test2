var Spots = function(token){

	var self = this;
	
	var editWindow =  $("#spot_edit").dialog({
		modal:true,
		autoOpen : false,
		width: 500,
		title: "Edit Spot"
	});
	
	$("#spot_edit form").submit(function(){
		var id = $(this).find("#spot_id").val();
		if(id == ""){
			id = null;
		}
		$.ajax({
			"dataType" : 'json',
			"type" : (id == null)?"POST":"PUT",
			"contentType": "application/json",
			"url" : baseUrl + "/spots?access_token=" + token,
			"data" : JSON.stringify({
				"spotId" : id,
				"longitude" : $(this).find("#longitude").val(),
				"latitude" : $(this).find("#latitude").val(),
				"timestamp" : $(this).find("#timestamp").val(),
				"status" : $(this).find("#status").val()
			})
		}).done(function(json){
			editWindow.dialog( "close" );
			table.fnReloadAjax();
		});
		return false;
	});
	
	$('#add_spot_btn').button().click(function(){
		self.editWindow();
	});
	
	$('#spot_delete').dialog({autoOpen:false});
	
	
	var table = $('#spots-table').dataTable(
		{
			"bProcessing" : true,
			"bJQueryUI": true,
			"aoColumns": [
			              { "mData": "longitude" },
			              { "mData": "latitude" },
			              { "mData": "timestamp" },
			              { "mData": "status" },
			              { "mData": "spotId" }
			          ],
			"sAjaxSource" : baseUrl + "/spots?access_token=" + token,
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
				  var id = aData['spotId']; 

				  var imgTag = '<img class="edit_btn" src="'+baseUrl+'/resources/images/edit.png"/><img class="delete_btn" src="'+baseUrl+'/resources/images/delete.png"/>';
				  $('td:eq(4)', nRow).html(imgTag); 
				  
				  $(nRow).find(".edit_btn").click(function(){
					  self.editWindow(id);
				  });
				  
				  $(nRow).find(".delete_btn").click(function(){
					  self.deleteWindow(id);
				  });
				  
				  return nRow;
				}
		});
	
	this.editWindow = function(id){
		if(id == undefined){
			$(editWindow).find("#spot_id").val("");
			$(editWindow).find("#latitude").val("");
			$(editWindow).find("#longitude").val("");
			$(editWindow).find("#timestamp").val("");
			$(editWindow).find("#status").val("");
			editWindow.dialog( "open" );
		}else{
			$.ajax({
				"dataType" : 'json',
				"type" : "GET",
				"url" : baseUrl + "/spots/"+id+"?access_token=" + token
			}).done(function(json) {	
				$('#new_password').parent().css('display', 'none');
				$('#change_password').parent().css('display', 'block');
				$(editWindow).find("#spot_id").val(json.spotId);
				$(editWindow).find("#latitude").val(json.latitude);
				$(editWindow).find("#longitude").val(json.longitude);
				$(editWindow).find("#timestamp").val(json.timestamp);
				$(editWindow).find("#status").val(json.status);
				
				editWindow.dialog( "open" );
			});
		}	
	}
	
	this.deleteWindow = function(id){
		$('#spot_delete').dialog('option',{
			modal:true,
			buttons:[
				         {
				        	 text:"OK",
				        	 click: function(){
				        		 $.ajax({
				        				"type" : "DELETE",
				        				"url" : baseUrl + "/spots/"+id+"?access_token=" + token
				        			}).done(function(json){
				        				 $('#spot_delete').dialog( "close" );
				        				table.fnReloadAjax();
				        			});
				        	 }
				         },
				         {
				        	 text:"Cancel",
				        	 click: function(){
				        		 $('#spot_delete').dialog('close')
				        	 }
				         }
			         ]
		});
		$('#spot_delete').dialog('open');
	}
	
};