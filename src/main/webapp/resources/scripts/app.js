var MapView = function(element) {
	
	self = this;
	self.spots = [];
	self.locations = [];
	
	self.fakeSpots = true;

	var intSpot = null;
	var intLocation = null;
	
	self.token = token;

	var center = new google.maps.LatLng(48.860, 2.344);
	var styles = [ {
		elementType : "geometry",
		stylers : [ {
			lightness : 33
		}, {
			saturation : -90
		} ]
	} ];

	var mapOptions = {
		zoom : 12,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		center : center,
		styles : styles
	};
	
	self.map = new google.maps.Map(document.getElementById(element),
			mapOptions);
	
	

	function getSpots() {
		$.ajax({
			url : baseUrl+"/spots?latitude="+self.map.getCenter().lat()+"&longitude="+self.map.getCenter().lng()+"&radius=30000&access_token="+self.token+"&tracking=false",
			dataType:"json"
		}).done(function(data) {
			//remove old spots from list
			$.grep(self.spots, function(oldSpot, index){
				var exist = false;
				$.each(data, function(index2, newSpot){
					if(oldSpot.spotId == newSpot.spotId){
						exist = true;
						return;
					}
				});
				if(!exist){
					oldSpot.marker.setMap(null);
					return false;
				}
			});
			
			
			
			//add new spots to list
			$.each(data, function(index, newSpot){
				
				
				var exist = false;
				$.each(self.spots, function(index2, oldSpot){
					if(oldSpot.spotId == newSpot.spotId){
						exist = true;
						return;
					}
				});
				
				if(!exist){
					var mapa = null;
					if(self.fakeSpots || newSpot.flag != 'fake'){
						mapa = self.map;
					}
					var markerOption = {
							map : mapa,
							position : new google.maps.LatLng(newSpot.latitude, newSpot.longitude),
							flat : true,
							zIndex : 0
					};
					newSpot.marker = new google.maps.Marker(markerOption);
					
					self.spots.push(newSpot);
				}
				
			});
			
			
		});
	}
	
	function getLocations() {
		$.ajax({
			url : baseUrl+"/locations/active?access_token="+self.token,
			dataType:"json"
		}).done(function(data) {
			//remove old spots from list
			$.grep(self.locations, function(oldLocation, index){
				var exist = false;
				$.each(data, function(index2, newLocation){
					if(oldLocation.id == newLocation.id){
						exist = true;
						return;
					}
				});
				if(!exist){
					oldLocation.marker.setMap(null);
					return false;
				}
			});
			
			
			
			//add new location to list
			$.each(data, function(index, newLocation){
				var exist = false;
				$.each(self.locations, function(index2, oldLocation){
					if(oldLocation.id == newLocation.id){
						exist = true;
						return;
					}
				});
				
				if(!exist){
					var user = newLocation.user;
					var pinColor = "0000FF";
					if(newLocation.user.role == "beta"){
						pinColor = "800080";
					}
					var pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
					        new google.maps.Size(21, 34),
					        new google.maps.Point(0,0),
					        new google.maps.Point(10, 34));
					var markerOption = {
							map : self.map,
							position : new google.maps.LatLng(newLocation.latitude, newLocation.longitude),
							flat : true,
							icon : pinImage,
							title : user.firstName+" "+user.lastName,
							zIndex : 1
					};
					newLocation.marker = new google.maps.Marker(markerOption);
					
					self.locations.push(newLocation);
				}
			});
			
			
		});
	}
	
	

	
	this.spotStart = function (token){
		self.token = token;
		clearInterval(intSpot);
		getSpots();
		intSpot = setInterval(getSpots, 2000);
	};
	
	this.spotStop = function (){
		clearInterval(intSpot);
		$.each(self.spots, function(index, spot){
			spot.marker.setMap(null);
		});
		self.spots = [];
	};
	
	this.locationStart = function (token){
		self.token = token;
		clearInterval(intLocation);
		getLocations();
		intLocation = setInterval(getLocations, 2000);
	};
	
	this.locationStop = function (){
		clearInterval(intLocation);
		$.each(self.locations, function(index, location){
			location.marker.setMap(null);
		});
		self.locations = [];
	};
	
	this.showFake = function(show){
		$.grep(self.spots, function(spot, index){
			if(show || spot.flag != "fake"){
				spot.marker.setMap(self.map);	
				return true;
			}else{
				spot.marker.setMap(null);
				return false;
			}
		});
		self.fakeSpots = show;
		getSpots();
	};
	
	

};

