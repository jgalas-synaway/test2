var AppView = function() {
	
	var spots = [];
	var self = {};

	var center = new google.maps.LatLng(50.06063, 19.856278);
	var styles = [ {
		elementType : "geometry",
		stylers : [ {
			lightness : 33
		}, {
			saturation : -90
		} ]
	} ];

	var mapOptions = {
		zoom : 9,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		center : center,
		styles : styles
	};
	
	self.map = new google.maps.Map(document.getElementById('map_canvas'),
			mapOptions);
	
	

	function getSpots() {
		
		$.ajax({
			url : baseUrl+"/spots?latitude="+self.map.getCenter().lat()+"&longitude="+self.map.getCenter().lng()+"&radius=30000&access_token=cfe3b962334808e63435a18d294854b3&tracking=false",
			dataType:"json"
		}).done(function(data) {
			//remove old spots from list
			$.each(spots, function(index, oldSpot){
				var exist = false;
				$.each(data, function(index2, newSpot){
					if(oldSpot.spotId == newSpot.spotId){
						exist = true;
						return;
					}
				});
				if(!exist){
					oldSpot.marker.setMap(null);
					spots.splice(index, 1);
				}
			});
			
			
			
			//add new spots to list
			$.each(data, function(index, newSpot){
				var exist = false;
				$.each(spots, function(index2, oldSpot){
					if(oldSpot.spotId == newSpot.spotId){
						exist = true;
						return;
					}
				});
				
				if(!exist){

					console.log(newSpot.latitude, newSpot.longitude);
					
					var markerOption = {
							map : self.map,
							position : new google.maps.LatLng(newSpot.latitude, newSpot.longitude),
							flat : true
					};
					newSpot.marker = new google.maps.Marker(markerOption);
					
					spots.push(newSpot);
				}
			});
			
			
		});
	}

	setInterval(getSpots, 2000);

};

var App = null;
$(function() {
	App = new AppView();
});