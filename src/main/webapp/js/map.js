var map;
var imageLayer;
var topoLayer;
var baseLayer;
var pureCoverage = false;
// pink tile avoidance
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
// make OL compute scale according to WMS spec
OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;

var toggleBaseLayer = function() {
	if (baseLayer === imageLayer) {
		baseLayer = topoLayer
	} else {
		baseLayer = imageLayer
	}
	map.setBaseLayer(baseLayer)
}

function init(){
    // if this is just a coverage or a group of them, disable a few items,
    // and default to jpeg format
    format = 'image/png';
    if(pureCoverage) {
        document.getElementById('filterType').disabled = true;
        document.getElementById('filter').disabled = true;
        document.getElementById('antialiasSelector').disabled = true;
        document.getElementById('updateFilterButton').disabled = true;
        document.getElementById('resetFilterButton').disabled = true;
        document.getElementById('jpeg').selected = true;
        format = "image/jpeg";
    }

    var bounds = new OpenLayers.Bounds(
        -172.953934831794, 18.032464,
        -58.1941695891842, 70.4953759999999
    );
    var options = {
        controls: [],
        maxExtent: bounds,
        maxResolution: 0.4482803329789445,
        projection: "EPSG:900913", // or 4269
        units: 'degrees'
    };
    map = new OpenLayers.Map('map', options);

	topoLayer = new OpenLayers.Layer.XYZ("Topo",
        "http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/${z}/${y}/${x}",
        {
            sphericalMercator: true,
            isBaseLayer: true,
            numZoomLevels: 20,
            wrapDateLine: true
        }
    )
	map.addLayer(topoLayer);
	
	imageLayer = new OpenLayers.Layer.XYZ("World Imagery",
        "http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/${z}/${y}/${x}",
        {
            sphericalMercator: true,
            isBaseLayer: true,
            numZoomLevels: 20,
            wrapDateLine: true
        }
    )
	map.addLayer(imageLayer);

	
    var states = new OpenLayers.Layer.WMS(
            "States", "http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/wms",
            {
                LAYERS: 'sedmap:CONUS_states_multipart',
                STYLES: '',
                format: format,
                tiled: true,
	            transparent: true,
                tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
            },
            {
                buffer: 0,
                displayOutsideMaxExtent: true,
                isBaseLayer: false,
                yx : {'EPSG:900913' : false}
            } 
        );
    map.addLayer(states);
    var counties = new OpenLayers.Layer.WMS(
            "Counties", "http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/wms",
            {
                LAYERS: 'sedmap:countyp020',
                STYLES: '',
                format: format,
                tiled: true,
                transparent: true,
                tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
            },
            {
                buffer: 0,
                displayOutsideMaxExtent: true,
                isBaseLayer: false,
                yx : {'EPSG:900913' : false}
            } 
        );
        map.addLayer(counties);
        
        var huc8 = new OpenLayers.Layer.WMS(
                "HUC8", "http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/wms",
                {
                    LAYERS: 'sedmap:huc_8_multipart_wgs',
                    STYLES: '',
                    format: format,
                    tiled: true,
    	            transparent: true,
                    tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
                },
                {
                    buffer: 0,
                    displayOutsideMaxExtent: true,
                    isBaseLayer: false,
                    yx : {'EPSG:900913' : false}
                } 
            );
        map.addLayer(huc8);
	
		    
    var instant = new OpenLayers.Layer.WMS(
	        "Instant Sites", "http://cida-wiwsc-sedmapdev.er.usgs.gov:8080/geoserver/sedmap/wms",
	        {
	            LAYERS: 'sedmap:Instant Sites',
	            STYLES: '',
	            format: format,
	            tiled: true,
	            transparent: true,
	            tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
	        },
	        {
	            buffer: 0,
	            displayOutsideMaxExtent: true,
	            isBaseLayer: false,
	            yx : {'EPSG:4269' : true}
	        } 
	    );
	    map.addLayer( instant);
	    
		var dailies = new OpenLayers.Layer.WMS(
		        "Daily Sites", "http://cida-wiwsc-sedmapdev.er.usgs.gov:8080/geoserver/sedmap/wms",
		        {
		            LAYERS: 'sedmap:Daily Sites',
		            STYLES: '',
		            format: format,
		            tiled: true,
		            transparent: true,
		            tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
		        },
		        {
		            buffer: 0,
		            displayOutsideMaxExtent: true,
		            isBaseLayer: false,
		            yx : {'EPSG:4269' : true}
		        } 
		    );
		    map.addLayer( dailies);
	    
    var nid = new OpenLayers.Layer.WMS(
            "NID", "http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/wms",
            {
                LAYERS: 'sedmap:NID',
                STYLES: '',
                format: format,
                tiled: true,
	            transparent: true,
                tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
            },
            {
                buffer: 0,
                displayOutsideMaxExtent: true,
                isBaseLayer: false,
                yx : {'EPSG:900913' : false}
            } 
        );
    map.addLayer(nid);
    
    
			    
	map.zoomToMaxExtent();
	var center = new OpenLayers.LonLat(-100,40)
	var proj = new OpenLayers.Projection("EPSG:4326");
	center.transform(proj, map.getProjectionObject());
	map.setCenter(center,4);
				
    // build up all controls
    map.addControl(new OpenLayers.Control.PanZoomBar({
        position: new OpenLayers.Pixel(2, 15)
    }));
    map.addControl(new OpenLayers.Control.Navigation());
    map.addControl(new OpenLayers.Control.Scale($('scale')));
    map.addControl(new OpenLayers.Control.MousePosition({element: $('location')}));
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    
    // wire up the option button
    //var options = document.getElementById("options");
    //options.onclick = toggleControlPanel;
    
    // support GetFeatureInfo
    map.events.register('click', map, function (e) {
        document.getElementById('nodelist').innerHTML = "Loading... please wait...";
        var params = {
            REQUEST: "GetFeatureInfo",
            EXCEPTIONS: "application/vnd.ogc.se_xml",
            BBOX: map.getExtent().toBBOX(),
            SERVICE: "WMS",
            INFO_FORMAT: 'text/html',
            QUERY_LAYERS: map.layers[0].params.LAYERS,
            FEATURE_COUNT: 50,
            Layers: 'sedmap:SM_SITE_REF',
            WIDTH: map.size.w,
            HEIGHT: map.size.h,
            format: format,
            styles: map.layers[0].params.STYLES,
            srs: map.layers[0].params.SRS};
        
        // handle the wms 1.3 vs wms 1.1 madness
        if(map.layers[0].params.VERSION == "1.3.0") {
            params.version = "1.3.0";
            params.j = parseInt(e.xy.x);
            params.i = parseInt(e.xy.y);
        } else {
            params.version = "1.1.1";
            params.x = parseInt(e.xy.x);
            params.y = parseInt(e.xy.y);
        }
            
        // merge filters
        if(map.layers[0].params.CQL_FILTER != null) {
            params.cql_filter = map.layers[0].params.CQL_FILTER;
        } 
        if(map.layers[0].params.FILTER != null) {
            params.filter = map.layers[0].params.FILTER;
        }
        if(map.layers[0].params.FEATUREID) {
            params.featureid = map.layers[0].params.FEATUREID;
        }
        OpenLayers.loadURL("http://cida-wiwsc-sedmapdev.er.usgs.gov:8080/geoserver/sedmap/wms", params, this, setHTML, setHTML);
        OpenLayers.Event.stop(e);
    });
}

// sets the HTML provided into the nodelist element
function setHTML(response){
    document.getElementById('nodelist').innerHTML = response.responseText;
};

// shows/hide the control panel
function toggleControlPanel(event){
    var toolbar = document.getElementById("toolbar");
    if (toolbar.style.display == "none") {
        toolbar.style.display = "block";
    }
    else {
        toolbar.style.display = "none";
    }
    event.stopPropagation();
    map.updateSize()
}

// Tiling mode, can be 'tiled' or 'untiled'
function setTileMode(tilingMode){
    if (tilingMode == 'tiled') {
        untiled.setVisibility(false);
        tiled.setVisibility(true);
        map.setBaseLayer(tiled);
    }
    else {
        untiled.setVisibility(true);
        tiled.setVisibility(false);
        map.setBaseLayer(untiled);
    }
}

// Transition effect, can be null or 'resize'
function setTransitionMode(transitionEffect){
    if (transitionEffect === 'resize') {
        tiled.transitionEffect = transitionEffect;
        untiled.transitionEffect = transitionEffect;
    }
    else {
        tiled.transitionEffect = null;
        untiled.transitionEffect = null;
    }
}

// changes the current tile format
function setImageFormat(mime){
    // we may be switching format on setup
    if(tiled == null)
      return;
      
    tiled.mergeNewParams({
        format: mime
    });
    untiled.mergeNewParams({
        format: mime
    });
    /*
    var paletteSelector = document.getElementById('paletteSelector')
    if (mime == 'image/jpeg') {
        paletteSelector.selectedIndex = 0;
        setPalette('');
        paletteSelector.disabled = true;
    }
    else {
        paletteSelector.disabled = false;
    }
    */
}

// sets the chosen style
function setStyle(style){
    // we may be switching style on setup
    if(tiled == null)
      return;
      
    tiled.mergeNewParams({
        styles: style
    });
    untiled.mergeNewParams({
        styles: style
    });
}

// sets the chosen WMS version
function setWMSVersion(wmsVersion){
    // we may be switching style on setup
    if(wmsVersion == null)
      return;
      
    if(wmsVersion == "1.3.0") {
       origin = map.maxExtent.bottom + ',' + map.maxExtent.left;
    } else {
       origin = map.maxExtent.left + ',' + map.maxExtent.bottom;
    }
      
    tiled.mergeNewParams({
        version: wmsVersion,
        tilesOrigin : origin
    });
    untiled.mergeNewParams({
        version: wmsVersion
    });
}

function setAntialiasMode(mode){
    tiled.mergeNewParams({
        format_options: 'antialias:' + mode
    });
    untiled.mergeNewParams({
        format_options: 'antialias:' + mode
    });
}

function setPalette(mode){
    if (mode == '') {
        tiled.mergeNewParams({
            palette: null
        });
        untiled.mergeNewParams({
            palette: null
        });
    }
    else {
        tiled.mergeNewParams({
            palette: mode
        });
        untiled.mergeNewParams({
            palette: mode
        });
    }
}

function setWidth(size){
    var mapDiv = document.getElementById('map');
    var wrapper = document.getElementById('wrapper');
    
    if (size == "auto") {
        // reset back to the default value
        mapDiv.style.width = null;
        wrapper.style.width = null;
    }
    else {
        mapDiv.style.width = size + "px";
        wrapper.style.width = size + "px";
    }
    // notify OL that we changed the size of the map div
    map.updateSize();
}

function setHeight(size){
    var mapDiv = document.getElementById('map');
    
    if (size == "auto") {
        // reset back to the default value
        mapDiv.style.height = null;
    }
    else {
        mapDiv.style.height = size + "px";
    }
    // notify OL that we changed the size of the map div
    map.updateSize();
}

function updateFilter(){
    if(pureCoverage)
      return;

    var filterType = document.getElementById('filterType').value;
    var filter = document.getElementById('filter').value;
    
    // by default, reset all filters
    var filterParams = {
        filter: null,
        cql_filter: null,
        featureId: null
    };
    if (OpenLayers.String.trim(filter) != "") {
        if (filterType == "cql") 
            filterParams["cql_filter"] = filter;
        if (filterType == "ogc") 
            filterParams["filter"] = filter;
        if (filterType == "fid") 
            filterParams["featureId"] = filter;
    }
    // merge the new filter definitions
    mergeNewParams(filterParams);
}

function resetFilter() {
    if(pureCoverage)
      return;

    document.getElementById('filter').value = "";
    updateFilter();
}

function mergeNewParams(params){
    tiled.mergeNewParams(params);
    untiled.mergeNewParams(params);
}