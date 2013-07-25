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
			$('#click-count').text(json.greenRedClickCount);
			$('#average-clicks').text(json.averageClicksPerUser);
			$('#activity-report').show();
		});
	});

	var map = L.map('map').setView([48.860, 2.344], 13);

	new L.TileLayer.Ajax(baseUrl + '/reports/activity/map/{z}/{x}/{y}.json?access_token={accessToken}',
			{ accessToken: token }).addTo(map);
};

//Load data tiles using the JQuery ajax function
L.TileLayer.Ajax = L.TileLayer.extend({
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
        var tile = { key: key, datum: null };
        this._tiles[key] = tile;
        this._loadTile(tile, tilePoint);
    },
    _addTileData: function(tile) {
        // override in subclass
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
