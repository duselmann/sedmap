OpenLayers.Layer.WMS.prototype.getFullRequestString = function(newParams,altUrl)
{
    try{
        var projectionCode=typeof this.options.projection == 'undefined' ? this.map.getProjection() : this.options.projection;
    }catch(err){
        var projectionCode=this.map.getProjection();
    }
    projectionCode = 'EPSG:3857'

    this.params.SRS = projectionCode=="none" ?null :projectionCode;
 
    return OpenLayers.Layer.Grid.prototype.getFullRequestString.apply(this,arguments);
}

// pink tile avoidance
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
// make OL compute scale according to WMS spec
OpenLayers.DOTS_PER_INCH = 90; // 25.4 / 0.28;

var NUM_ZOOM_LEVELS = 18

var map // this will be your main openlayers handle
var format     = 'image/png'; // your default wms return type. 
var projectUrl = 'http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/'; // your project server. 
var arcgisUrl  = 'http://services.arcgisonline.com/ArcGIS/rest/services/'; // ArcGIS server. 
var nlcdUrl    = 'http://raster.nationalmap.gov/ArcGIS/services/TNM_LandCover/MapServer/WMSServer'; // NLCD server ?request=GetCapabilities&service=WMS&version=1.3.0
	
var layers = {}


function init(){
    // build up all controls
	var controls = [
        new OpenLayers.Control.PanZoomBar({ position: new OpenLayers.Pixel(2, 15) }),
        new OpenLayers.Control.Navigation(),
        new OpenLayers.Control.Scale($('scale')),
        new OpenLayers.Control.MousePosition({element: $('location')}),
        new OpenLayers.Control.LayerSwitcher(),
        new OpenLayers.Control.ScaleLine(),
    ]
    var bounds = new OpenLayers.Bounds(-173*111000, 18*111000, -60*111000, 70*111000);
	
    var options = {
    	controls: controls,
//    	numZoomLevels: NUM_ZOOM_LEVELS,
//      maxExtent: bounds,
        maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
//    	maxExtent: new OpenLayers.Bounds(-20037508,-20037508,20037508,20037508),
        maxResolution: 1.40625/2,
//        maxResolution: 0.45,
        projection: "EPSG:3857",
        units: 'm'
    };
    
    map = new OpenLayers.Map('map', options);

    // the arcgis base maps
    addArcGisLayer(map, "Topographic", "World_Topo_Map")
    addArcGisLayer(map, "World Image", "World_Imagery")
//    // etc...
    addNlcdLayer(map, "NLCD 2006", "24")

    // sedmap project maps
    addLayer(map, "States", "sedmap:CONUS_states_multipart", false) // add a new visible layer
    addLayer(map, "Counties", "sedmap:countyp020", false)   // add a new invisible layer
    addLayer(map, "HUC8", "sedmap:huc_8_multipart_wgs", false)
    addLayer(map, "Instant Sites", "sedmap:instant", true)
    addLayer(map, "Daily Sites", "sedmap:daily", true)
//    addLayer(map, "Instant Sites", "sedmap:SM_INST_STATION", true)
//    addLayer(map, "Daily Sites", "sedmap:SM_DAILY_STATION", true)
    addLayer(map, "NID", "sedmap:NID", false)
	
    // zoom and move viewport to desired location
    //map.zoomToMaxExtent();
	var center = new OpenLayers.LonLat(-96*111000,37*111000)
	var proj   = new OpenLayers.Projection("EPSG:3857");
	center.transform(proj, map.getProjectionObject());
	map.setCenter(center,4);
}



function _addLayer(map, title,layer) {
   layers[title] = layer;
   map.addLayer(layer); // add the new layer to the map viewport 
}
/* 
it is best to make a method for repetitive tasks.  you will likely have more than one layer and the order they are added determines the order they are overlaid 
*/
function addLayer(map, title, layerId, show) {
	
   var layer = new OpenLayers.Layer.WMS(title, projectUrl+"wms",
       {
	       LAYERS: layerId,    // the layer id
	       transparent: true,  // overlay layer
	       isBaseLayer: false, // overlay layer
	       STYLES: '',         // default style
	       format: format,     // png file
           tiled: true,        // it is best to tile
           tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
       },{
	       buffer: 0,
	       opacity: .5,        // alpha for overlay
	       isBaseLayer: false, // overlay layer
	       wrapDateLine: false,// repeat the world map
           visibility: show,   // initial visibility
	       displayOutsideMaxExtent: true, // display full map returned
//           yx : {'EPSG:3857' : false}
       }
   );
   _addLayer(map, title, layer)
}

//the NLCD topographical world map
function addNlcdLayer(map, title, layerId) {
   var layer = new OpenLayers.Layer.WMS(title, nlcdUrl,
       {
	       LAYERS: layerId,    // the layer id
	       transparent: true,  // overlay layer
	       isBaseLayer: false, // overlay layer
	       STYLES: '',         // default style
	       format: format,     // png file
	   },{
	       buffer: 0,
	       opacity: .5,        // alpha for overlay
	       isBaseLayer: false, // overlay layer
	       wrapDateLine: true,// repeat the world map
	       visibility: false,  // default hidden
	       displayOutsideMaxExtent: true, // display full map returned
//	       sphericalMercator: true,
//	       numZoomLevels: NUM_ZOOM_LEVELS,
//	       yx : {'EPSG:3857' : false}
	   }
   );
   _addLayer(map, title, layer)
}




//the arcgis topographical world map - these are returned as EPSG:3857 or unofficially 900913
function addArcGisLayer(map, title, layerId) {
 var layerUrl = arcgisUrl+layerId +"/MapServer/tile/${z}/${y}/${x}"
 var layer = new OpenLayers.Layer.XYZ(title, layerUrl, 
     {
	 	   buffer: 0,
	       isBaseLayer: true, // base layer
	       wrapDateLine: true,// repeat the world map
	       visibility: true,   // default visible
	       displayOutsideMaxExtent: true, // display full map returned
           sphericalMercator: true,
//         numZoomLevels: NUM_ZOOM_LEVELS,
//	       yx : {'EPSG:3857' : false}
     }
 );
 _addLayer(map, title, layer)
}
