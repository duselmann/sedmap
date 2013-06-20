OpenLayers.Layer.WMS.prototype.getFullRequestString = function(newParams,altUrl)
{
    try{
        var projectionCode=typeof this.options.projection == 'undefined' ? this.map.getProjection() : this.options.projection;
    }catch(err){
        var projectionCode=this.map.getProjection();
    }
    projectionCode = 'EPSG:4326'

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
    ]
    var bounds = new OpenLayers.Bounds(-173, 18, -60, 70);
	
    var options = {
    	controls: controls,
    	numZoomLevels: NUM_ZOOM_LEVELS,
//        maxExtent: bounds,
    	maxExtent: new OpenLayers.Bounds(-180,-90,180,90),
        // Got this number from Hollister, and he's not sure where it came from.
        // Without this line, the esri road and relief layers will not display
        // outside of the upper western hemisphere.
        maxResolution: 1.40625,
//      maxResolution: 1.40625/2,
//        maxResolution: 0.45,
//        maxResolution: auto,
        projection: "EPSG:4326", // or 4269
        //projection: "EPSG:900913", // or 4269
        units: 'degrees'
    };
    
    map = new OpenLayers.Map('map', options);

    // the arcgis base maps
    addArcGisLayer(map, "Topographic", "World_Topo_Map")
    addArcGisLayer(map, "World Image", "World_Imagery")
    // etc...
    addNlcdLayer(map, "NLCD 2006", "24")

    // sedmap project maps
    addLayer(map, "States", "sedmap:CONUS_states_multipart", false) // add a new visible layer
    addLayer(map, "Counties", "sedmap:countyp020", false)   // add a new invisible layer
    addLayer(map, "HUC8", "sedmap:huc_8_multipart_wgs", false)
    addLayer(map, "Instant Sites", "sedmap:Instant Sites", false)
    addLayer(map, "Daily Sites", "sedmap:Daily Sites", false)
    addLayer(map, "NID", "sedmap:NID", false)
	
    // zoom and move viewport to desired location
    //map.zoomToMaxExtent();
	var center = new OpenLayers.LonLat(-96,37)
	var proj   = new OpenLayers.Projection("EPSG:4326");
	center.transform(proj, map.getProjectionObject());
	map.setCenter(center,3);
				
   
}

function _addLayer(map, title,layer) {
   layers[title] = layer;
   map.addLayer(layer); // add the new layer to the map viewport 
}
/* 
it is best to make a method for repetitive tasks.  you will likely have more than one layer and the order they are added determines the order they are overlaid 
*/
function addLayer(map, title, layerId, initiallyVisible) {
	
   var layer = new OpenLayers.Layer.WMS(title, projectUrl+"wms",
       {
           LAYERS: layerId,
           STYLES: '',
           format: format,
           tiled: true, // it is best to tile
           transparent: true, // do not forget this is your want layers to overlap
           tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
       },{
	       opacity: .6,
           buffer: 0,
           displayOutsideMaxExtent: true,
           isBaseLayer: false,
           visibility: initiallyVisible,
//           yx : {'EPSG:4326' : false}
       }
   );
   _addLayer(map, title, layer)
}

// http://raster.nationalmap.gov/ArcGIS/services/TNM_LandCover/MapServer/WMSServer?LAYERS=24&STYLES=&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&BBOX=-7514238.6275,7514083.6275,-5009550.085,10018772.17&WIDTH=256&HEIGHT=256
//the NLCD topographical world map
function addNlcdLayer(map, title, layerId) {
   var layer = new OpenLayers.Layer.WMS(title, nlcdUrl,
       {
	       LAYERS: layerId,
	       transparent: true, // do not forget this is your want layers to overlap
	       isBaseLayer: false,
	       STYLES: '',
	//       SRS:'EPSG:4326',
	       format: format,
	//       tiled: true, // it is best to tile
	//       tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
	   },{
//	         sphericalMercator: true,
	       opacity: .5,
	       isBaseLayer: false,
//	         numZoomLevels: NUM_ZOOM_LEVELS,
	       buffer: 0,
	       displayOutsideMaxExtent: true,
	         wrapDateLine: false,
	       visibility: true,
//	       yx : {'EPSG:4326' : false}
	   }
   );
   _addLayer(map, title, layer)
}




//the arcgis topographical world map
function addArcGisLayer(map, title, layerId) {
 var layerUrl = arcgisUrl+layerId +"/MapServer/tile/${z}/${y}/${x}"
 var layer = new OpenLayers.Layer.XYZ(title, layerUrl, 
     {
//         sphericalMercator: true,
//         isBaseLayer: true, // openlayers will render this layer first
//         numZoomLevels: NUM_ZOOM_LEVELS,
	       buffer: 0,
	       displayOutsideMaxExtent: true,
         wrapDateLine: false,
	       visibility: true,
//	       yx : {'EPSG:4326' : false}
     }
 );
 _addLayer(map, title, layer)
}
