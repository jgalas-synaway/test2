var Reports = function(token){

	var self = this;
	
	$("#from_date").datepicker({ dateFormat: "yy-mm-dd" });
	$("#to_date").datepicker({ dateFormat: "yy-mm-dd" });
	$("#map_from_date").datepicker({ dateFormat: "yy-mm-dd" });
	$("#map_to_date").datepicker({ dateFormat: "yy-mm-dd" });

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
			$('#click-count').text(json.greenRedClickCount);
			$('#average-clicks').text(json.averageClicksPerUser);
			$('#activity-report').show();
		});
	});

	var map = L.map('map').setView([48.860, 2.344], 13);

	var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib='Map data © OpenStreetMap contributors';
	var osm = new L.TileLayer(osmUrl, {attribution: osmAttrib});		
	map.addLayer(osm);

	new L.TileLayer.Ajax(baseUrl + '/reports/activity/map/{z}/{x}/{y}.json?access_token={accessToken}&from={fromDate}&to={toDate}',	{
			accessToken: token,
			fromDate: function () { return $("#map_from_date").datepicker().val() + " 00:00" },
			toDate: function () { return $("#map_to_date").datepicker().val() + " 23:59" }	
		}).addTo(map);
};

//Load data tiles using the JQuery ajax function
L.TileLayer.Ajax = L.TileLayer.extend({
	_createTile: function () {
        var tile = L.DomUtil.create('div', 'leaflet-tile leaflet-tile-loaded activity-tile');
        var tileSize = this.options.tileSize;
        tile.style.width = tileSize + 'px';
        tile.style.height = tileSize + 'px';
        tile.onselectstart = tile.onmousemove = L.Util.falseFn;
        return tile;
    },

    drawTile: function (tile, tilePoint) {
        // override with rendering code
    },
    
    onAdd: function (map) {
        L.TileLayer.prototype.onAdd.call(this, map);
        this.on('tileunload', this._unloadTile);
    },
    onRemove: function (map) {
        L.TileLayer.prototype.onRemove.call(this, map);
        this.off('tileunload', this._unloadTile);
    },
    _addTile: function(tilePoint, container) {
        var key = tilePoint.x + ':' + tilePoint.y;
        var tilePos = this._getTilePos(tilePoint);
        var tiledata = { key: key, datum: null, latlon: this._map.layerPointToLatLng(tilePos) };
        this._tiles[key] = tiledata;
        this._loadTile(tiledata, tilePoint);
    },
    _addTileData: function(tile) {
    	tile.marker = new L.Marker(tile.latlon, {
    		icon:	new L.NumberedDivIcon({number: JSON.parse(tile.datum).greenRedClickCount})
    	})
    	tile.marker.addTo(this._map);
    },
    // XMLHttpRequest handler; closure over the XHR object, the layer, and the tile
    _xhrHandler: function (req, layer, tile) {
        return function() {
            if (req.readyState != 4) {
                return;
            }
            var s = req.status;
            if ((s >= 200 && s < 300) || s == 304) {
                // check if request is about to be aborted, avoid rare error when aborted while parsing
                if (tile._request) {
                    tile._request = null;
                    layer.fire('tileresponse', {tile: tile, request: req});
                    tile.datum = req.responseText;
                    layer._addTileData(tile);
                }
            } else {
                layer.fire('tileerror', {tile: tile});
                layer._tileLoaded();
            }
        }
    },
    // Load the requested tile via AJAX
    _loadTile: function (tile, tilePoint) {
        this._adjustTilePoint(tilePoint);
        var layer = this;
        var req = new XMLHttpRequest();
        tile._request = req;
        req.onreadystatechange = this._xhrHandler(req, layer, tile);
        this.fire('tilerequest', {tile: tile, request: req});
        req.open('GET', this.getTileUrl(tilePoint), true);
        req.send();
    },
    _unloadTile: function(evt) {
        var tile = evt.tile,
            req = tile._request;
        this._map.removeLayer(tile.marker);
        if (req) {
            tile._request = null;
            req.abort();
            this.fire('tilerequestabort', {tile: tile, request: req});
        }
    },
    _update: function() {
        //console.log('_update');
        if (this._map._panTransition && this._map._panTransition._inProgress) { return; }
        if (this._tilesToLoad < 0) this._tilesToLoad = 0;
        L.TileLayer.prototype._update.apply(this, arguments);
    }
});

L.NumberedDivIcon = L.Icon.extend({
	options: {
    // EDIT THIS TO POINT TO THE FILE AT http://www.charliecroom.com/marker_hole.png (or your own marker)
    iconUrl: 'http://www.charliecroom.com/marker_hole.png',
    number: '',
    shadowUrl: null,
    iconSize: new L.Point(25, 41),
		iconAnchor: new L.Point(13, 41),
		popupAnchor: new L.Point(0, -33),
		/*
		iconAnchor: (Point)
		popupAnchor: (Point)
		*/
		className: 'leaflet-div-icon'
	},

	createIcon: function () {
		var div = document.createElement('div');
		//var img = this._createImg(this.options['iconUrl']);
		var numdiv = document.createElement('div');
		numdiv.setAttribute ( "class", "number" );
		numdiv.innerHTML = this.options['number'] || '';
		//div.appendChild ( img );
		div.appendChild ( numdiv );
		this._setIconStyles(div, 'icon');
		return div;
	},

	//you could change this to add a shadow like in the normal marker if you really wanted
	createShadow: function () {
		return null;
	}
});
