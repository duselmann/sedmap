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

function setupFilters() {
	
	benchmark = new Filters.Bool({
	    field:'BENCHMARK_SITE', 
	    trueVal:'1', 
	    el:'refonly', 
	    parent:'#filterDiv', 
        group:'#Site-Characteristics',
	    label:'Hydrologic Benchmark Sites:',
	    layers:[
	        	"Discrete Sites",
	        	"Daily Sites",
	        	]
	})
	
    minYears = new Filters.Value({
        class:"minyrs",
        field:'SAMPLE_YEARS', 
        el:'minyrs', 
        size:4,
        maxlength:2,
        parent:'#filterDiv', 
        group:'#Data-Characteristics',
        label:'Min Years of Daily Data:',
        max:90,
        compare: Ogc.Comp.GREATER_THAN_OR_EQUAL_TO ,
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:["Daily Sites"]
    })

    minSamples = new Filters.Value({
        class:"minSamples",
        field:'SAMPLE_COUNT', 
        el:'minSamples', 
        size:8,
        maxlength:12,
        parent:'#filterDiv', 
        group:'#Data-Characteristics',
        label:'Min Discrete Samples:',
        compare: Ogc.Comp.GREATER_THAN_OR_EQUAL_TO ,
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:["Discrete Sites"]
    })
	
    gageBasin = new Filters.Value({
        class:"gagebasin",
        field:'SITE_BASIN_REF', 
        el:'GAGE_BASIN_ID', 
        size:11,
        maxlength:15,
        parent:'#filterDiv', 
        group:'#Boundaries',
        label:'Gage Basin ID:',
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
    usgsStation = new Filters.Value({
        class:"usgsStationId",
        field:'SITE_NO', 
        el:'USGS_STATION_ID', 
        size:11,
        maxlength:15,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'USGS Station ID:',
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:[    "Discrete Sites",
                    "Daily Sites",
                ]
    })
    
    
    drainArea = new Filters.Range({
        class:"drainage",
        field:'NWISDA1', 
        el:'DRAINAGE_AREA_MI_SQ', 
        size:8,
        maxlength:11,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Drainage Area (mi<sup>2</sup>):',
        min:0,
        max:999999999,
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
    soilKfactor = new Filters.Range({
        class:"soilkfactor",
        field:'KFACT', 
        el:'SOIL_K', 
        size:6,
        maxlength:6,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Soil K-Factor:',
        min:0,
        max:1,
        pattern: /^[01]?\.\d{0,3}$/,
        patternMsg: "Expecting number between 0 and 1",
        layers:[    "Discrete Sites",
                    "Daily Sites",
                ]
    })
    
    soilRfactor = new Filters.Range({
        class:"soilRfactor",
        field:'RFACT', 
        el:'SOIL_R', 
        size:6,
        maxlength:6,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Soil R-Factor:',
        min:0,
        max:1,
        pattern: /^[01]?\.\d{0,3}$/,
        patternMsg: "Expecting number between 0 and 1",
        layers:[    "Discrete Sites",
                    "Daily Sites",
                ]
    })
    
    soilPerm = new Filters.Range({
        class:"soilPerm",
        field:'PERM', 
        el:'SOIL_PERM', 
        size:6,
        maxlength:6,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Soil R-Factor:',
        min:0,
        max:1,
        pattern: /^[01]?\.\d{0,3}$/,
        patternMsg: "Expecting number between 0 and 1",
        layers:[    "Discrete Sites",
                    "Daily Sites",
                ]
    })
    
    urbanLand = new Filters.Range({
        class:"urbanLand",
        field:'URBAN', 
        el:'urbanLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Urban Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
    agLand = new Filters.Range({
        class:"agLand",
        field:'AGRIC', 
        el:'agLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Ag Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
    forestLand = new Filters.Range({
        class:"forestLand",
        field:'FOREST', 
        el:'forestLand', 
        size:4,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Site-Characteristics',
        label:'Forest Land Use (%):',
        min:0,
        max:100,
        pattern: /^\d+$/,
        patternMsg: "Expecting number between 0 and 100",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
	theYearFilter = new Filters.Range({
        class:"yearRange",
        field:'YEAR', 
        el:'year', 
        size:8,
        maxlength:4,
        parent:'#filterDiv', 
        group:'#Data-Characteristics',
        label:'Year Range:',
        min:1900,
        max:new Date().getFullYear(),
        pattern: /^\d+$/,
        patternMsg: "Expecting a positive number",
        isMapOgc: false,
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
    
	ecoNum = new Filters.Value({
        class:"ecoNumFilter",
        field:'ECO_L3_CODE', 
        el:'ecoNum', 
        size:8,
        maxlength:12,
        parent:'#filterDiv', 
        group:'#Boundaries',
        label:'Ecoregion Number:',
        pattern: /^(\d?(\d\.))+\d?\*?$/,
        patternMsg: "Eco Region number have the format ##.##.## with possible wild card, '*'",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                ]
    })
	
    huc = new Filters.Value({
        class:"hucFilter",
        field:'HUC_8', 
        el:'huc', 
        size:8,
        maxlength:12,
        parent:'#filterDiv', 
        group:'#Boundaries',
        label:'HUC:',
        pattern: /^\d+\*?$/,
        patternMsg: "Expecting a HUC number with possible wild card, '*'",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                	"HUC8",
                ]
    })
	
    states = new Filters.Option({
        class:"stateFilter",
        field:'STATE', 
        el:'state', 
        parent:'#filterDiv', 
        group:'#Boundaries',
        label:'States:',
        labelClass:"stateFilterLabel",
        baseTxt: "Select a State",
        layers:[	"Discrete Sites",
                	"Daily Sites",
                	"States",
                	"Counties",
                	"NID",
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
            "VT":"Vermont","VA":"Virginia",
            "WA":"Washington","WV":"West Virginia",
            "WI":"Wisconsin","WY":"Wyoming",
        }
    })
}

$().ready(setupFilters)
