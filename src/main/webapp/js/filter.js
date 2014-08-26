var benchmark
var minYears
var minSamples
var drainArea
var theYearFilter
var states
var huc
var gageBasin
var urbanLand
var agLand
var forestLand
var soilKfactor
var soilRfactor
var soilPerm
var ecoNum
var usgsStation
var stationName 

// see minSamples for field comments

function initFilters() {
	
    gageBasin = new Filters.Value({
        clazz:"usgsBasinNo",
        field:'BASIN_NO', 
        el:'BASIN_ID', 
        size:11,
        maxlength:15,
        parent:'#filterDiv', 
        group:'#Boundary',
        label:'Basin Boundary:',
        pattern: /^\d{8,15}$/,
        valueDecorator: function(value) {
            return "*"+value+"*"
        },
        patternMsg: "Expecting a full 8-15 digit basin ID.",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "USGS Basin Boundaries",
                "SiteInfo"
                ],
        helpText: 'Enter corresponding USGS Station ID'
    })
    
    minYears = new Filters.Value({
        clazz:"minyrs",
        field:'SAMPLE_YEARS', 
        el:'minyrs', 
        size:4,
        maxlength:2,
        parent:'#filterDiv', 
        group:'#Data-Characteristic',
        label:'Min Years of Daily Data:',
        max:90,
        compare: Ogc.Comp.GREATER_THAN_OR_EQUAL_TO ,
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive integer",
        orDefaultValue: '1',
        callback: function(el) {                     // the special validation handler
			return hiddenLayerWarning(el, layers[DAILY])
        },
        layers:[
                "Daily Sites",
                "SiteInfo"
                ]
    })

    
    
    benchmark = new Filters.Bool({
        field:'BENCHMARK_SITE', 
        trueVal:'1', 
        el:'refonly', 
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Hydrologic Benchmark Sites:',
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    

    usgsStation = new Filters.Value({
        clazz:"usgsStationId",
        field:'SITE_NO', 
        el:'USGS_STATION_ID', 
        size:11,
        maxlength:15,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'USGS Station ID:',
        pattern: /^\d+\*?$/,
        patternMsg: "Expecting a USGS Station number with possible wild card, '*'",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
               ]
    })

    

    minSamples = new Filters.Value({
        clazz:"minSamples",                          // the css class to attach for use custom UI
        field:'SAMPLE_COUNT',                        // the filter field to apply to - note: all layers must use the same filed at this time
        el:'minSamples',                             // the element name
        size:8,                                      // the input field UI size
        maxlength:12,                                // the input field max length
        parent:'#filterDiv',                         // the filter parent group
        group:'#Data-Characteristic',                // the display-only group EL and label
        label:'Min Discrete Samples:',               // the label for this filter parameter
        compare: Ogc.Comp.GREATER_THAN_OR_EQUAL_TO , // the compare to perform on this field - default is EQUALS
        pattern: /^\d+$/,                            // the pattern to validate entries against
        patternMsg: "Expecting a positive integer",  // the message to display when the pattern is violated
        orWith: [minYears],                          // the other filters used in or-conjunction
        orDefaultValue: '1',                         // the default value to use in or-conjunction
        callback: function(el) {                     // the special validation handler
            return hiddenLayerWarning(el, layers[DISCRETE])
        },
        layers:[                                     // the array of layers that this filter should be applied
                "Discrete Sites",
                "SiteInfo"
                ]
    })
    minYears.orWith = [minSamples]



    
    layers[DAILY].events.register('visibilitychanged', layers[DAILY], function() {
        hiddenSiteLayerFilterToggle(minYears, layers[DAILY]) 
        /*
        if (layers[DAILY].visibility) {
            minYears.setDefaultOrFilter('1')
        } else {
            minYears.setDefaultOrFilter('999999')
            $("#" +minYears.el+ " input").val('')
            minYears.filter = undef;
        }
        applyFilters(minYears.parent)
        */
    })
    layers[DISCRETE].events.register('visibilitychanged', layers[DISCRETE], function() {
        hiddenSiteLayerFilterToggle(minSamples, layers[DISCRETE]) 
        /*
        if (layers[DISCRETE].visibility) {
            minSamples.setDefaultOrFilter('1')
        } else {
            minSamples.setDefaultOrFilter('999999')
            $("#" +minSamples.el+ " input").val('')
            minSamples.filter = undef;
        }
        applyFilters(minYears.parent)
        */
    })


    
    stationName = new Filters.Value({
        clazz:"usgsStationName",
        field:'SNAME', 
        el:'USGS_STATION_NAME', 
        size:11,
        maxlength:50,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'USGS Station Name:',
        pattern: /^[\w\. ]+$/,
        valueDecorator: function(value) {
            value = defaultValue(value,"") // prevent undefined issues
            return "*"+value.toUpperCase()+"*"
        },
        patternMsg: "Expecting any part of a station name.",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    
    drainArea = new Filters.Range({
        clazz:"drainage",
        field:'NWISDA1', 
        el:'DRAINAGE_AREA_MI_SQ', 
        size:8,
        maxlength:11,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Drainage Area (mi<sup>2</sup>):',
        min:0,
        max:9999999,
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    soilKfactor = new Filters.Range({
        clazz:"soilkfactor",
        field:'KFACT', 
        el:'SOIL_K', 
        size:6,
        maxlength:6,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Soil K-Factor:',
        min:0.08,
        max:0.55,
        pattern: /^[01]?\.\d{0,3}$/,
        patternMsg: "Expecting number between 0.08 and 0.55",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    soilRfactor = new Filters.Range({
        clazz:"rfactor",
        field:'RFACT', 
        el:'rfact', 
        size:6,
        maxlength:6,
        parent:'#filterDiv',
        group:'#Site-Characteristic',
        label:'R-Factor:',
        min:2.85,
        max:670,
        pattern: /^\d{0,3}(\.\d{0,3})?$/,
        patternMsg: "Expecting number between 2.85 and 670",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    soilPerm = new Filters.Range({
        clazz:"soilPerm",
        field:'PERM', 
        el:'SOIL_PERM', 
        size:6,
        maxlength:6,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Soil Permeability:',
        min:0.23,
        max:14.2,
        pattern: /^\d{0,2}(\.\d{0,3})?$/,
        patternMsg: "Expecting number between 0.23 and 14.2",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    urbanLand = new Filters.Range({
        clazz:"urbanLand",
        field:'URBAN', 
        el:'urbanLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Urban Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    agLand = new Filters.Range({
        clazz:"agLand",
        field:'AGRIC', 
        el:'agLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Ag Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
    forestLand = new Filters.Range({
        clazz:"forestLand",
        field:'FOREST', 
        el:'forestLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristic',
        label:'Forest Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
	theYearFilter = new Filters.Range({
        clazz:"yearRange",
        field:'YEAR', 
        el:'year', 
        size:8,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Data-Characteristic',
        label:'Year Range:',
        min:1900,
        max:new Date().getFullYear(),
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        isMapOgc: false,
        layers:[
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
    
	ecoNum = new Filters.Value({
        clazz:"ecoNumFilter",
        field:'ECO_L2_COD', 
        el:'ecoNum', 
        size:8,
        maxlength:12,
        parent:'#filterDiv', 
        group:'#Boundary',
        label:'Ecoregion Level 2 Number:',
        pattern: /^(\d?(\d\.?))?\d?\*?$/,
        patternMsg: "Ecoregion numbers have the format 'Level 1 value.Level 2 value' with possible wild card, '*'",
        callback: function(el) {
        	try {
                var val = $(el + " input").val()
                var int = isDefined(val) ?val.replace(/\*/g,'') :val
                if (int && int.length > 0) {
                    if (int.split('\.').length < 3) {
                        int = parseFloat(val)
                        if (int < 2.2 || int > 15.4) {
                            if (int === 2) {
                                if (val.indexOf('*')>=0) return true
                            }
                            var elWarn = el + "-warn" 
                            var msgText = "Valid Ecoregions are between 2.2 and 15.4"
                            Filters.applyWarn(el, elWarn, '#filterDiv', 'inputFilterWarn', msgText)
                            return false
                        }
                    }
                }
            } catch(e) {
            	console.log(e) // this is here so that if a parseFloat fails the parser will continue
            }
            return true
        },
        layers:[
                "Ecoregion Level 2",
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
	
    huc = new Filters.Value({
        clazz:"hucFilter",
        field:'HUC_8', 
        el:'huc', 
        size:8,
        maxlength:8,
        parent:'#filterDiv', 
        group:'#Boundary',
        label:'HUC:',
        pattern: /^(\d\d)+\*?$/,
        patternMsg: "Expecting a 2, 4, 6, or 8 digit HUC number with possible wild card, '*'",
        callback: function(el) {
            var val = $(el + " input").val()
            if (val.length > 0  && val.length < 8  &&  val.indexOf('*') < 0) {
                $(el + " input").val( val+'*' )
            }
            return true
        },
        layers:[
                "HUC8",
                "Discrete Sites",
                "Daily Sites",
                "SiteInfo"
                ]
    })
	
    states = new Filters.Option({
        clazz:"stateFilter",
        field:'STATE', 
        el:'state', 
        parent:'#filterDiv', 
        group:'#Boundary',
        label:'States:',
        labelClass:"stateFilterLabel",
        baseTxt: "Select a State",
        layers:[	
                "Discrete Sites",
            	"Daily Sites",
            	"States",
            	"Counties",
            	"National Inventory of Dams",
                "USGS Basin Boundaries",
                "SiteInfo"
                ],
        options: {
            "AK":"Alaska","AL":"Alabama","AZ":"Arizona","AR":"Arkansas",
            "CA":"California","CO":"Colorado","CT":"Connecticut",
            "DE":"Delaware",
            "FL":"Florida",
            "GA":"Georgia","GU":"Guam",
            "HI":"Hawaii",
            "ID":"Idaho","IL":"Illinois","IN":"Indiana","IA":"Iowa",
            "KS":"Kansas","KY":"Kentucky",
            "LA":"Louisiana",
            "ME":"Maine","MD":"Maryland","MA":"Massachusetts",
            "MI":"Michigan","MN":"Minnesota","MS":"Mississippi",
            "MO":"Missouri","MT":"Montana",
            "NE":"Nebraska","NV":"Nevada","NH":"New Hampshire",
            "NJ":"New Jersey","NM":"New Mexico","NY":"New York",
            "NC":"North Carolina","ND":"North Dakota",
            "OH":"Ohio","OK":"Oklahoma","OR":"Oregon",
            "PA":"Pennsylvania","PR":"Puerto Rico",
            "RI":"Rhode Island",
            "SC":"South Carolina","SD":"South Dakota",
            "TN":"Tennessee","TX":"Texas",
            "UT":"Utah",
            "VT":"Vermont","VA":"Virginia","VI":"Virgin Islands",
            "WA":"Washington","WV":"West Virginia",
            "WI":"Wisconsin","WY":"Wyoming"
        }
    })


    $("div.downloadWindow .closeWindow").click(function(){
        $("#DL-cancel").click()
    })
    $("#DL-cancel").click(function(){
        $(".blackoverlay").fadeOut("slow")
    })
    $("#DL-open").click(function(){
        clearDelay('#DL-msg',0)
        $(".blackoverlay").fadeIn("slow")
    })
}



function closeDL(timeout) {
	if(typeof timeout === "undefined") {
		timeout = 5000;
	} else if(isNaN(timeout)) {
		timeout = 5000;
	}
	
    setTimeout(function(){
        $(".blackoverlay").fadeOut("slow")
    },timeout)
}



function downloadShow() {
    clearDelay('#DL-msg', 0)
    var filterDiv = '#filterDiv';
    var dailyFilters = getFilters(filterDiv, DAILY);
    var discreteFilters = getFilters(filterDiv, DISCRETE);
    var doDownload = function () {
    	$('#DL-msg').hide();
    	
        var isDaily = layers[DAILY].visibility
        var isDiscr = layers[DISCRETE].visibility
        
        $("#DL-daily").prop('checked', isDaily);
        $("#DL-discrete").prop('checked', isDiscr);
        //$('#DL-directdownload').prop('checked', false);
        $('#DL-downloadprogress').hide();
        $('#DL-download').show();
        
        //$("#DL-discreteFlow").prop('checked', isDiscr)

        $(".blackoverlay").fadeIn("slow")
    };
    if ((dailyFilters.length === 0) && (discreteFilters.length === 0)) {
        $('#noFiltersConfirmation').dialog({
            dialogClass: 'sedmap-modal',
            height: 250,
            modal: true,
            buttons: {
                "Proceed with large download": function(){
                    $(this).dialog("close");
                    doDownload();
                },
                "Cancel": function () {
                    $(this).dialog("close");
                }
            }
        });
    }
    else {
        doDownload();
    }
}


function hiddenLayerWarning(el, layer) {
    if ( ! layer.visibility ) {
        var val = $(el + " input").val()
        if (val.length > 0) {
            var elWarn = el + "-warn" 
            var msgText = "Please turn on related layer to apply filter."
            Filters.applyWarn(el, elWarn, '#filterDiv', 'inputFilterWarn', msgText)
            return false
        }
    }
    return true
}
function hiddenSiteLayerFilterToggle(filter, layer) {
    if (layer.visibility) {
        filter.setDefaultOrFilter('1')
        filter.onchange();
    } else {
        filter.setDefaultOrFilter('999999')
        $("#" +filter.el+ " input").val('')
        filter.clearFilter();
    }
    applyFilters(filter.parent)
}



$().ready(function(){
    initMap()
    initFilters()
})

