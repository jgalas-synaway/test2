var Reports = function(token) {

	var self = this;

	$("#from_date").datepicker({
		dateFormat : "yy-mm-dd"
	});
	$("#to_date").datepicker({
		dateFormat : "yy-mm-dd"
	});
	$("#map_from_date").datepicker({
		dateFormat : "yy-mm-dd"
	});
	$("#map_to_date").datepicker({
		dateFormat : "yy-mm-dd"
	});

	$("#users").multiselect({
		selectedText : "# of # users",
		noneSelectedText: "Select users"
	});
	$("#map_users").multiselect({
		selectedText : "# of # users",
		noneSelectedText: "Select users",
		close : function(){
			tileLayer.redraw();
		}
	});

	$('#status').selectmenu();
	$('#map_status').selectmenu();

	$.ajax({
		"dataType" : 'json',
		"type" : "GET",
		"url" : baseUrl + "/users?access_token=" + token
	}).done(function(json) {
		$.each(json, function(index,user){
			$("#users").append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
			$("#map_users").append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
		});
		$("#users").multiselect('refresh');
		$("#users").multiselect('checkAll');
		$("#map_users").multiselect('refresh');
		$("#map_users").multiselect('checkAll');
		tileLayer.redraw();
	});

	$("#generate-report").click(
			function() {
				var data = {};
				data.from = new Date($("#from_date").datepicker().val()+ "T00:00:00.000Z");
				data.to = new Date($("#to_date").datepicker().val()+ "T23:59:00.000Z");
				data.status = $('#status').val();
				data.users = $("#users").val();
				data.users = (data.users === null)? [] : data.users;
				$.ajax(
						{
							"dataType" : 'json',
							"type" : "POST",
							"url" : baseUrl + "/reports/activity?&access_token=" + token,
							"contentType": 'application/json',
							"data":JSON.stringify(data)
						}).done(function(json) {
					$('#active-users').text(json.activeUsers);
					$('#click-count').text(json.greenRedClickCount);
					$('#average-clicks').text(json.averageClicksPerUser);
					$('#activity-report').show();
				});
			});

	var map = L.map('map').setView([ 48.860, 2.344 ], 13);

	var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib = 'Map data © OpenStreetMap contributors';
	var osm = new L.TileLayer(osmUrl, {
		attribution : osmAttrib
	});
	map.addLayer(osm);

	var tileLayer = new L.TileLayer.Ajax(
			baseUrl
					+ '/reports/activity/map/{z}/{x}/{y}.json?access_token={accessToken}',
			{
				accessToken : token				
			}).addTo(map);

	$("#map_from_date").change(function() {
		tileLayer.redraw();
	});

	$("#map_to_date").change(function() {
		tileLayer.redraw();
	});
	
	$('#map_status').change(function() {
		tileLayer.redraw();
	});
	
};

// Load data tiles using the JQuery ajax function
L.TileLayer.Ajax = L.TileLayer.extend({
	_createTile : function() {
		var tile = L.DomUtil.create('div',
				'leaflet-tile leaflet-tile-loaded activity-tile');
		var tileSize = this.options.tileSize;
		tile.style.width = tileSize + 'px';
		tile.style.height = tileSize + 'px';
		tile.onselectstart = tile.onmousemove = L.Util.falseFn;
		return tile;
	},

	drawTile : function(tile, tilePoint) {
		// override with rendering code
	},

	onAdd : function(map) {
		L.TileLayer.prototype.onAdd.call(this, map);
		this.on('tileunload', this._unloadTile);
	},
	onRemove : function(map) {
		L.TileLayer.prototype.onRemove.call(this, map);
		this.off('tileunload', this._unloadTile);
	},
	_addTile : function(tilePoint, container) {
		var key = tilePoint.x + ':' + tilePoint.y;
		var tilePos = this._getTilePos(tilePoint);
		var tiledata = {
			key : key,
			datum : null,
			latlon : this._map.layerPointToLatLng(tilePos)
		};
		this._tiles[key] = tiledata;
		this._loadTile(tiledata, tilePoint);
	},
	_addTileData : function(tile) {
		tile.marker = new L.Marker(tile.latlon, {
			icon : new L.divIcon({
				className : 'leaflet-click-count',
				iconAnchor : new L.Point(0, -128),
				iconSize : new L.Point(256, 256),
				html : tile.datum.greenRedClickCount
			})
		})
		tile.marker.addTo(this._map);
	},
	// Load the requested tile via AJAX
	_loadTile : function(tile, tilePoint) {
		this._adjustTilePoint(tilePoint);
		var layer = this;
		var data = {};
		data.from = new Date($("#map_from_date").datepicker().val()+ "T00:00:00.000Z");
		data.to = new Date($("#map_to_date").datepicker().val()+ "T23:59:00.000Z");
		data.status = $('#map_status').val();
		data.users = $("#map_users").val();
		data.users = (data.users === null)? [] : data.users;
		$.ajax({
			"dataType" : 'json',
			"type" : "POST",
			"url" : this.getTileUrl(tilePoint),
			"contentType": 'application/json',
			"data":JSON.stringify(data)
		}).done(function(json) {
			tile.datum = json;
			layer._addTileData(tile);
		});
	},
	_unloadTile : function(evt) {
		var tile = evt.tile, req = tile._request;
		this._map.removeLayer(tile.marker);
		if (req) {
			tile._request = null;
			req.abort();
			this.fire('tilerequestabort', {
				tile : tile,
				request : req
			});
		}
	},
	_update : function() {
		// console.log('_update');
		if (this._map._panTransition && this._map._panTransition._inProgress) {
			return;
		}
		if (this._tilesToLoad < 0)
			this._tilesToLoad = 0;
		L.TileLayer.prototype._update.apply(this, arguments);
	}
});
