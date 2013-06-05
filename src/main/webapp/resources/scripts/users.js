var Users = function(token){

	var self = this;
	
	var editWindow =  $("#user_edit").dialog({
		modal:true,
		autoOpen : false,
		width: 500,
		title: "Edit User"
	});
	
	$("#user_edit form").submit(function(){
		var id = $(this).find("#user_id").val();
		if(id == ""){
			id = null;
		}
		var password = $(this).find("#new_password").val()
		if(password == ""){
			password = null;
		}
		$.ajax({
			"dataType" : 'json',
			"type" : (id == null)?"POST":"PUT",
			"contentType": "application/json",
			"url" : baseUrl + "/users?access_token=" + token,
			"data" : JSON.stringify({
				"id" : id,
				"firstName" : $(this).find("#firstName").val(),
				"lastName" : $(this).find("#lastName").val(),
				"login" : $(this).find("#login").val(),
				"email" : $(this).find("#email").val(),
				"role" : $(this).find("#role").val(),
				"password" : password
			})
		}).done(function(json){
			editWindow.dialog( "close" );
			table.fnReloadAjax();
		});
		return false;
	});
	
	$('#add_user_btn').button().click(function(){
		self.editWindow();
	});
	
	$('#change_password').change(function(){
		var parent = $('#new_password').parent();
		console.log(parent);
		if($(this).is(':checked')){
			parent.css('display', 'block' );
		}else{
			parent.css('display', 'none' );
		}
		
	});
	
	$("#spot_edit form #user_cancel").click(function(){
		editWindow.dialog('close');
	});
	
	$('#user_delete').dialog({autoOpen:false});
	
	$('#refresch_user_btn').button().click(function(){
		table.fnReloadAjax();
	});
	
	var table = $('#users-table').dataTable(
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

				  var imgTag = '<img class="edit_btn" src="'+baseUrl+'/resources/images/edit.png"/><img class="delete_btn" src="'+baseUrl+'/resources/images/delete.png"/>';
				  $('td:eq(5)', nRow).html(imgTag); 
				  
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
		$('#add_user_btn').attr('checked', false);
		$(editWindow).find("#new_password").val("");
		if(id == undefined){
			$('#new_password').parent().css('display', 'block');
			$('#change_password').parent().css('display', 'none');
			$(editWindow).find("#user_id").val("");
			$(editWindow).find("#firstName").val("");
			$(editWindow).find("#lastName").val("");
			$(editWindow).find("#login").val("");
			$(editWindow).find("#email").val("");
			$(editWindow).find("#role").val("");
			editWindow.dialog( "open" );
		}else{
			$.ajax({
				"dataType" : 'json',
				"type" : "GET",
				"url" : baseUrl + "/users/"+id+"?access_token=" + token
			}).done(function(json) {	
				$('#new_password').parent().css('display', 'none');
				$('#change_password').parent().css('display', 'block');
				$(editWindow).find("#user_id").val(json.id);
				$(editWindow).find("#firstName").val(json.firstName);
				$(editWindow).find("#lastName").val(json.lastName);
				$(editWindow).find("#login").val(json.login);
				$(editWindow).find("#email").val(json.email);
				$(editWindow).find("#role").val(json.role);
				
				editWindow.dialog( "open" );
			});
		}	
	}
	
	this.deleteWindow = function(id){
		$('#user_delete').dialog( 'option',{
			modal:true,
			buttons:[
				         {
				        	 text:"OK",
				        	 click: function(){
				        		 $.ajax({
				        				"type" : "DELETE",
				        				"url" : baseUrl + "/users/"+id+"?access_token=" + token
				        			}).done(function(json){
				        				 $('#user_delete').dialog( "close" );
				        				table.fnReloadAjax();
				        			});
				        	 }
				         },
				         {
				        	 text:"Cancel",
				        	 click: function(){
				        		 $('#user_delete').dialog('close')
				        	 }
				         }
			         ]
		});
		$('#user_delete').dialog('open');
	}
};