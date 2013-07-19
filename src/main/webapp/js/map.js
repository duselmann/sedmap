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

OpenLayers.ImgPath = "images/openlayers/"

// pink tile avoidance
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
// make OL compute scale according to WMS spec
OpenLayers.DOTS_PER_INCH = 90; // 25.4 / 0.28;

var NUM_ZOOM_LEVELS = 18

var map // this will be your main openlayers handle
var format     = 'image/png'; // your default wms return type. 
var projectUrl = '/sediment/map/'; // your project server. 
var arcgisUrl  = 'http://services.arcgisonline.com/ArcGIS/rest/services/'; // ArcGIS server. 
var nlcdUrl    = 'http://raster.nationalmap.gov/ArcGIS/services/TNM_LandCover/MapServer/WMSServer'; // NLCD server ?request=GetCapabilities&service=WMS&version=1.3.0
var flowUrl    = '/sediment/flow/'; // url to the flow proxy point


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
//        numZoomLevels: NUM_ZOOM_LEVELS,
//      maxExtent: bounds,
        maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
//        maxExtent: new OpenLayers.Bounds(-20037508,-20037508,20037508,20037508),
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
    var nlcd = addNlcdLayer(map, "NLCD 2006", "24")
    nlcd.events.register('visibilitychanged', nlcd, nlcdThumbToggle)

    // sedmap project maps
    addProjectLayer(map, "Discrete Sites", "sedmap:instant", true)
    addProjectLayer(map, "Daily Sites", "sedmap:daily", true)
    addProjectLayer(map, "States", "sedmap:statep010", false) // add a new visible layer *new added 7/23/13 mwarren
    addProjectLayer(map, "Counties", "sedmap:countyp020", false)   // add a new invisible layer
    addProjectLayer(map, "HUC8", "sedmap:huc_8_multipart_wgs", false)
    addFlowLinesLayer(map);
    addProjectLayer(map, "NID", "sedmap:NID", false)
    addProjectLayer(map, "Ecoregion Level 2", "sedmap:NA_CEC_Eco_Level2", false)
    addProjectLayer(map, "USGS Gage Basins for Daily Sites", "sedmap:Alldailybasins", false)
    addProjectLayer(map, "USGS Gage Basins for Discrete Sites", "sedmap:Alldiscretebasins", false)
    addProjectLayer(map, "USGS Gage Basins for All Sites", "sedmap:AllbasinswDVs1", false)
    
    // zoom and move viewport to desired location
    //map.zoomToMaxExtent();
    var center = new OpenLayers.LonLat(-96*111000,37*111000)
    var proj   = new OpenLayers.Projection("EPSG:3857");
    center.transform(proj, map.getProjectionObject());
    map.setCenter(center,4);
    
    map.events.register('click', map, getSiteInfo);

    
    $('#nlcdthumb').click(nlcdLegendToggle)
    $('#nlcdimg').appendTo('#map:first-child')
    $('#siteInfo').click(clearSiteInfo)
}

function getSiteInfo(e) {
    var layer = layers['Daily Sites']
    var params = {
            REQUEST: "GetFeatureInfo",
            EXCEPTIONS: "application/vnd.ogc.se_xml",
            BBOX: map.getExtent().toBBOX(),
            SERVICE: "WMS",
            INFO_FORMAT: 'text/html',
            QUERY_LAYERS: layer.params.LAYERS,
            FEATURE_COUNT: 50,
            Layers: 'sedmap:daily',
            WIDTH: map.size.w,
            HEIGHT: map.size.h,
            format: format,
            styles: layer.params.STYLES,
            srs: "EPSG:3857"
    };
    
    // handle the wms 1.3 vs wms 1.1 madness
    if(layer.params.VERSION == "1.3.0") {
        params.version = "1.3.0";
        params.j = parseInt(e.xy.x);
        params.i = parseInt(e.xy.y);
    } else {
        params.version = "1.1.1";
        params.x = parseInt(e.xy.x);
        params.y = parseInt(e.xy.y);
    }
    
    // merge filters
    if (layer.params.CQL_FILTER != null) {
        params.cql_filter = layer.params.CQL_FILTER;
    } 
    if (layer.params.FILTER != null) {
        params.filter = layer.params.FILTER;
    }
    if (layer.params.FEATUREID) {
        params.featureid = layer.params.FEATUREID;
    }
    OpenLayers.Request.GET({url:projectUrl+"wms", params:params, scope:this, success:onSiteInfoResponse, failure:onSiteInfoResponseFail});
    OpenLayers.Event.stop(e);
}

function onSiteInfoResponseFail(response) {
    clearSiteInfo()
    alert('Failed to request site information.')
}

function onSiteInfoResponse(response) {
    // this makes the fade out and in work out right
    // because we are deleting and adding new rows
    // it must be sequenced
    clearSiteInfo({newinfo:response})
}

function clearSiteInfo(e) {
    $('#siteInfo').fadeOut(300, function(){
        $('.singleSiteInfo:not(:first)').remove()

        // this makes the fade out and in work out right
        // because we are deleting and adding new rows
        // it must be sequenced
        if (e.newinfo !== undefined) {
            renderSiteInfo(e.newinfo)
        }
    })
}

//sets the HTML provided into the nodelist element
function renderSiteInfo(response) {
    var html  = response.responseText
    var start = html.indexOf('<table')
    var end   = html.indexOf('</table') +8
    var table = html.substring(start, end)
    
    var fields = {STATION_NAME:-1,
        USGS_STATION_ID:-1,
        DRAINAGE_AREA_MI_SQ:-1,
        SAMPLE_YEARS:-1}
    $.each(fields, function(key,val) {
        var col = findCol(table, key)
        // TODO log error/not found
        fields[key] = col;
    })
    
    // TODO this is daily only - need inst also
    
    var rows = $(table).find('tr').length
    if (rows>7) { // first row is an ignored header row
        // when max rows reached then fix height and scroll
        $('#siteInfo').css('height',6*81);
        $('#siteInfo').css('overflow-y','scroll');
    } else { // allow to get larger automatically
        $('#siteInfo').css('height','auto');
        $('#siteInfo').css('overflow-y','hidden');
    }
    
    $(table).find('tr').each(function(r,row){
        if (r===0) return // skip header row because :not(:first) did not work here
        var info = $('#singleSiteInfo').clone()
        info.attr('id','siteInfo-'+r)
        $.each(fields, function(key,c) {
            var value = $(row).find('td').eq(c).text()
            $(info).find('#'+key).text(value)
            $(info).find('#'+key).attr('id',key+'-'+r) // give the field a unique id
        })
        $('#siteInfo').append(info)
        $('#siteInfo-'+r).show()
        $('#siteInfo').fadeIn(300)
    })
}

function findCol(table, el) {
    var col = -1
    $(table).find('th').each(function(i,th){
        var val = $(th).text()
        if (val===el) {
            col = i
        }
    })
    return col
}

//function assignValues(fields, infoEl, row) {
//}


function nlcdLegendToggle() {
    if ($('#nlcdimg').css('display') == 'none') {
        $('#nlcdimg').fadeIn("slow")
    } else {
        $('#nlcdimg').fadeOut("slow")
    }
}
function nlcdThumbToggle() {
    if ($('#nlcdthumb').css('display') == 'none') {
        $('#nlcdthumb').fadeIn("slow")
    } else {
        $('#nlcdimg').fadeOut("slow")
        $('#nlcdthumb').fadeOut("slow")
    }
}






function _addLayer(map, title, layerId, type, url, options, params, noDefaults) {
    if (params===undefined) params={}

    var paramDefaults  = {
               LAYERS: layerId,    // the layer id
               transparent: true,  // overlay layer
               isBaseLayer: false, // overlay layer
               STYLES: '',         // default style
               format: format,     // png file
               tiled: true         // it is best to tile
           }
    var optionDefaults = {
               buffer: 0,
               opacity: .5,        // alpha for overlay
               isBaseLayer: false, // overlay layer
               wrapDateLine: false,// repeat the world map
               visibility: false,   // initial visibility
               displayOutsideMaxExtent: true, // display full map returned
//               yx : {'EPSG:3857' : false}
           }
    
    if (!noDefaults) {
	    mergeDefaults(params,paramDefaults)
	    mergeDefaults(options,optionDefaults)
    } else {
    	params.layers = layerId
    	params.format = format
    	params.tiled  = true
    	options.isBaseLayer= false
    }
    
    var layer
    
    if (type==="wms") {
        layer = new OpenLayers.Layer.WMS(title, url, params, options) 
    } else if (type==='xyz') {
        layer = new OpenLayers.Layer.XYZ(title, url, options) 
    } else {
        alert('Layer type ' +type+ ' not yet supported.')
    }
    
    layers[title] = layer;
    map.addLayer(layer); // add the new layer to the map viewport 
    return layer
}

function mergeDefaults(obj, defaults) {
    $.each(defaults, function(def) {
        if (obj[def] === undefined) {
            obj[def] = defaults[def]
        }
    })
}

/* 
it is best to make a method for repetitive tasks.  you will likely have more than one layer and the order they are added determines the order they are overlaid 
*/
function addProjectLayer(map, title, layerId, show) {

    var type    = "wms"
    var url     = projectUrl+"wms"
    var params  = {
               tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
           }
    var options = {
               visibility: show,   // initial visibility
               gutter:35,          // the amount of overlap to render large features the cross tile boundaries
           }
    
   return _addLayer(map, title, layerId, type, url, options, params)
}

//the NLCD topographical world map
function addNlcdLayer(map, title, layerId) {
    var type    = "wms"
    // nlcd layer uses the default params and options
    return _addLayer(map, title, layerId, type, nlcdUrl, {}, {})
}

//the arcgis topographical world map - these are returned as EPSG:3857 or unofficially 900913
function addArcGisLayer(map, title, layerId) {
    var type = "xyz"
    var url  = arcgisUrl+layerId +"/MapServer/tile/${z}/${y}/${x}"
    var options = {
            opacity: 1,        // alpha for base layer
            isBaseLayer: true, // base layer
            wrapDateLine: true,// repeat the world map
            sphericalMercator: true
        }
    // the xyz layer only has options - no params
    return _addLayer(map, title, layerId, type, url, options)
}





var streamOrderClipValues = [
     7, // 0
     7,
     7,
     6,
     6,
     6, // 5
     5,
     5,
     5,
     4,
     4, // 10
     4,
     3,
     3,
     3,
     2, // 15
     2,
     2,
     1,
     1,
     1  // 20
 ];

var streamOrderClipValue = 0;
var flowlineAboveClipPixel;
var createFlowlineColor = function(r,g,b,a) {
    flowlineAboveClipPixel = (a & 0xff) << 24 | (b & 0xff) << 16 | (g & 0xff) << 8  | (r & 0xff);
};
createFlowlineColor(100,100,255,255);

var addFlowLinesLayer = function(map) {
    streamOrderClipValue = streamOrderClipValues[map.zoom]
    
    map.events.register('zoomend', map, 
        function() { streamOrderClipValue = streamOrderClipValues[map.zoom] },
    true);    
    
    // define per-pixel operation
    var flowlineClipOperation = OpenLayers.Raster.Operation.create(function(pixel) {
        if (pixel >> 24 === 0) { return 0; }
        var value = pixel & 0x00ffffff;
        if (value >= streamOrderClipValue && value < 0x00ffffff) {
            return flowlineAboveClipPixel;
        } else {
            return 0;
        }
    });
    
    var flowlineLayer = "NHDPlusFlowlines:PlusFlowlineVAA_NHDPlus-StreamOrder";
    var options = { visibility: true,opacity: 0, displayInLayerSwitcher: false, tileOptions: { crossOriginKeyword: 'anonymous' } }
    var flowlinesWMSData = _addLayer(map, "Flowline WMS (Data)", flowlineLayer, "wms",
            flowUrl + "wms", options, {styles: "FlowlineStreamOrder"})

    // source canvas (writes WMS tiles to canvas for reading)
    var flowlineComposite = OpenLayers.Raster.Composite.fromLayer(flowlinesWMSData, {int32: true});
    
    // filter source data through per-pixel operation 
    var flowlineClipOperationData = flowlineClipOperation(flowlineComposite);
    
    var flowlineRaster = new OpenLayers.Layer.Raster({ 
        name: "NHD Flowlines", data: flowlineClipOperationData, isBaseLayer: false
    });
    
    // define layer that writes data to a new canvas
    flowlineRaster.setData(flowlineClipOperationData);
    
    // add the special raster layer to the map viewport 
    layers["NHD Flowlines"] = flowlineRaster;
    map.addLayer(flowlineRaster);
}    
