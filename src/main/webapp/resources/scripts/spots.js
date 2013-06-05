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
		var date = $(this).find("#timestamp").datepicker( "getDate" );
		date.setHours($(this).find("#hour").val(), $(this).find("#minute").val(), $(this).find("#second").val());

		$.ajax({
			"dataType" : 'json',
			"type" : (id == null)?"POST":"PUT",
			"contentType": "application/json",
			"url" : baseUrl + "/spots?access_token=" + token,
			"data" : JSON.stringify({
				"spotId" : id,
				"longitude" : $(this).find("#longitude").val(),
				"latitude" : $(this).find("#latitude").val(),
				"timestamp" : date.getTime()/1000,
				"status" : $(this).find("#status").val()
			})
		}).done(function(json){
			editWindow.dialog( "close" );
			table.fnReloadAjax();
		});
		return false;
	});
	$("#spot_edit form #spot_cancel").click(function(){
		editWindow.dialog('close');
	});
	
	$('#add_spot_btn').button().click(function(){
		self.editWindow();
	});
	
	$('#spot_delete').dialog({autoOpen:false});
	
	$( "#spot_edit #timestamp" ).datepicker();
	
	var table = $('#spots-table').dataTable(
		{
			"bProcessing" : true,
			"bJQueryUI": true,
			"aoColumns": [
			              { "mData": "latitude" },
			              { "mData": "longitude" },
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
							$.each(json,function(index){
								var d = new Date(this.timestamp*1000);
								var curr_date = d.getDate() < 10 ? "0"+d.getDate():d.getDate();
							    var curr_month = d.getMonth() + 1 < 10 ? "0"+(d.getMonth()+1):d.getMonth()+1;
							    var curr_year = d.getFullYear();
							    var h = d.getHours() < 10 ? "0"+d.getHours():d.getHours();
							    var m = d.getMinutes()< 10 ? "0"+d.getMinutes():d.getMinutes();
							    var s = d.getSeconds()< 10 ? "0"+d.getSeconds():d.getSeconds();
							    this.timestamp = curr_date + "-" + curr_month + "-" + curr_year + " "+h+":"+m+":"+s;
							});
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
			//$(editWindow).find("#timestamp").val("");
			$(editWindow).find("#status").val("");
			
			var d = new Date();
			
			$(editWindow).find("#timestamp").datepicker('setDate', d);
			(editWindow).find("#hour").val(d.getHours());
			(editWindow).find("#minute").val(d.getMinutes());
			(editWindow).find("#second").val(d.getSeconds());
			
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
				//$(editWindow).find("#timestamp").val(json.timestamp);
				$(editWindow).find("#status").val(json.status);
				
				var d = new Date(json.timestamp*1000);
				
				$(editWindow).find("#timestamp").datepicker('setDate', d);
				(editWindow).find("#hour").val(d.getHours());
				(editWindow).find("#minute").val(d.getMinutes());
				(editWindow).find("#second").val(d.getSeconds());
				
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