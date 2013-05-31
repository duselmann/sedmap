// pink tile avoidance
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
// make OL compute scale according to WMS spec
OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;

var map // this will be your main openlayers handle
var format     = 'image/png'; // your default wms return type. 
var projectUrl = 'http://cida-wiwsc-sedmapdev:8080/geoserver/sedmap/'; // your project server. 
var arcgisUrl  = 'http://services.arcgisonline.com/ArcGIS/rest/services/'; // ArcGIS server. 

var layers = {}


function init(){

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

    // the arcgis base maps
    addArcGisLayer(map, "Topographic", "World_Topo_Map")
    addArcGisLayer(map, "World Image", "World_Imagery")
    // etc...

    // sedmap project maps
    addLayer(map, "States", "sedmap:CONUS_states_multipart", true) // add a new visible layer
    addLayer(map, "Counties", "sedmap:countyp020", false)   // add a new invisible layer
    addLayer(map, "HUC8", "sedmap:huc_8_multipart_wgs", false)
    addLayer(map, "Instant Sites", "sedmap:Instant Sites", true)   
    addLayer(map, "Daily Sites", "sedmap:Daily Sites", true)   
    addLayer(map, "NID", "sedmap:NID", false)
	
    // zoom and move viewport to desired location
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
       },
       {
           buffer: 0,
           displayOutsideMaxExtent: true,
           isBaseLayer: false,
           visibility: initiallyVisible,
           yx : {'EPSG:900913' : false}
       }
   );
   layers[title] = layer;
   map.addLayer(layer); // add the new layer to the map viewport 
}

   // the arcgis topographical world map
function addArcGisLayer(map, title, layerId) {
   var layerUrl = arcgisUrl+layerId +"/MapServer/tile/${z}/${y}/${x}"
   var layer = new OpenLayers.Layer.XYZ(title, layerUrl, 
       {
           sphericalMercator: true,
           isBaseLayer: true, // openlayers will render this layer first
           numZoomLevels: 20,
           wrapDateLine: true
       }
   );
   layers[title] = layer;
   map.addLayer(layer); // add the new layer to the map viewport 
}

