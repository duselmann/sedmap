// slightly less to type
var Ogc = {}
Ogc.Logic=OpenLayers.Filter.Logical
Ogc.Comp =OpenLayers.Filter.Comparison
Ogc.Geo  =OpenLayers.Filter.Spatial

Ogc.encode = function(filter) {
	var xml = new OpenLayers.Format.XML();
    var filter_1_1 = new OpenLayers.Format.Filter({version: "1.1.0"});

	return xml.write( filter_1_1.write(filter) );
}



// apply for each layer and each filter
var applyFilter = function() {
	var hasErrors = $('.filterWarn:not(:empty)').length>0
	if (hasErrors) alert('Please address warnings.')

	var layerFilters = {}
	$.each(map.layers, function(i,layer){
		layerFilters[layer.title]=[]
	})

	// build each layers' OGC filter
	$.each(Filters.Instances, function(i,inst) {
		if ( inst.isMapOgc && isDefined(inst.filter) ) {
			// default to filter layers and use all layers for keyword 'all' 
			var applyTo = inst.layers
			if ( inst.layers.indexOf('all') !== -1 ) {
				applyTo = []
				$.each(map.layers, function(i,layer){
					applyTo.push( layer.title )
				})
			}
			
			$.each(applyTo, function(i,layerTitle){
				layerFilters[layerTitle].push(inst.filter)
			})
		}
	})

	// encode each layers filters
	$.each(layerFilters, function(layerTitle,filters) {
		if (layerFilters[layerTitle].length>0) {
			var filter = new Ogc.Logic({
				type   : Ogc.Logic.AND,
				filters: layerFilters[layerTitle]
			})
			layerFilters[layerTitle] = Ogc.encode(filter)
		} else {
			layerFilters[layerTitle] = ''
		}
	})
	
	// because year filter hads to be a different filter
	var yearFilter = makeYearFilter()

	// apply filters to layers
	$.each(map.layers, function(i,layer){
		// only apply the year filter to its assigned layers
		var localYearFilter = ''
		if ( theYearFilter.layers.indexOf(layer.title) >= 0 ) {
			localYearFilter = yearFilter
		}
		var ogcXml = layerFilters[layer.title]
		// apply filter
		layer.mergeNewParams({filter:ogcXml,viewparams:localYearFilter})
	})

}

// this is annoying but geoserver handles view params outside the OGC query 
// we will render the OGC for data download as per apply but the isMapOgc will be ignored
var makeYearFilter = function() {
	var filter = theYearFilter.filter
	if ( isUndefined(filter) ) {
		return ''
	}
	var yr1 = filter.filters[0].value
	var yr2 = filter.filters[1].value
	return 'yr1:'+yr1+';yr2:'+yr2
}










var Filters = Class.extend({
	init : function(params) {
		this.el      = params.el
		this.$el     = '#'+params.el
		this.$warn   = this.$el+'-warn'
		this.parent  = params.parent
		this.field   = params.field
		this.class   = defaultValue(params.class,'')
		this.label   = params.label
		this.trueVal = params.trueVal
		this.errClass= defaultValue(params.errorClass,'inputFilterWarn')
		this.layers  = defaultValue(params.layers,['all'])
		this.isMapOgc= defaultValue(params.isMapOgc,true)
		this.filter  = undefined
		this.isPrime = defaultValue(params.isPrime, true) // default not sub-filter
		
		// if there is no give clear action then used the default undefined action
		if (params.clearAction) {
			this.clear = params.clearAction
		}
		
		var dom = this.createDom() + '</div>' 
		// this is so composite filters get one error div
		if (this.isPrime) {
			dom += this.errorDom()
			Filters.Instances.push(this)
		}
		$(this.parent).append(dom)
		
		if (this.isPrime) {
			this.linkChange()
		}
	},

	// function that is called when the user wants to clear this filter
	clearFilter : function() {
		// default clearing is the undefined
		this.filter = undefined
	},

	// create common dom
	createDom : function() {
		var dom = '<div id="'+this.el+'" '
		if (this.class) dom += 'class="'+this.class+'"'
		return dom + '>'
	},
	
	errorDom : function() {
		return '<div id="'+this.el+'-warn" class="filterWarn"></div>'
	},
	
	onchange : function() {
		this.clearFilter()
		this.validate()
		// no text is a clean bill of health
		return $(this.$warn).text().length > 0
	},
	
	makeFilter : function() {
		return this.filter
	},
	
	validate : function(noReset) {
		if ( isUndefined(noReset) || noReset) {
			this.validateReset()
		}
		var msgs = this.subvalidate()
		// if there is a message, and sub-filters do not manage the messages
		if (this.isPrime && msgs.length>0) {
			var msgsText = msgs.join(', ')
			$(this.$el).addClass(this.errClass)
			$(this.$warn).addClass(this.errClass+"On")
			$(this.$warn).html(msgsText)
		}
		return msgs
	},
	
	validateReset : function() {
		$(this.$el).removeClass(this.errClass)
		$(this.$warn).removeClass(this.errClass+"On")
		$(this.$warn).html('')
	},
	
	subvalidate : function() {
		return [] // default validation
	},
	// pass through to jquery val()
	val : function(val) {
		if ( isDefined(val) ) {
			return $(this.$el+' input').val(val)
		}
		return $(this.$el+' input').val()
	}
})

// storage of the filter obj instances
Filters.Instances = []
// storage of the unique filter names
Filters.Layers = {}
// the openlayers map these filters apply
Filters.Map = undefined

// general validation
Filters.Validate = {
	max : function(value, max) {
		if ( isUndefined(max) || max.length===0) return []
		var val = matchType(max, value)
		if (val > max) {
			return [' ' + value + ' exceeds the max: ' + max]
		}
		return []
	},

	min : function(value, min) {
		if ( isUndefined(min) || min.length===0) return []
		var val = matchType(min, value)
		if (val < min) return [' ' + value + ' is less than the min: ' + min]
		return []
	},
	
	pattern : function(value, pattern, msg) {
		if ( isUndefined(pattern) || pattern.length===0) return []
		if ( ! new RegExp(pattern).test(value) ) {
			return [' ' + msg]
		}
		return []
	},
	
	rangefull : function(a, b, min, max) {
		var msgs  = []
		var vals = []
			
		$.each([a,b], function(i,val){
			if (val === "") return
			if ( $.isNumeric(val) ) {
				var num = parseInt(val)
				msgs = msgs.concat( Filters.Validate.min(num,min) )
				msgs = msgs.concat( Filters.Validate.max(num,max) )
				vals.push(num)
			}
		})
		
		if (vals.length == 2) {
			if (vals[0]>vals[1]) {
				msgs = msgs.concat('Initial value must be less than the second.')
			}
		}
		return msgs
	},
	
	// need the min or max value for type checking to ensure lexical or numeric compare consistency
	// this does not check the min and max that is done in min and max functions
	// this checks that a <= b if both values exist
	range : function(a, b, minOrMax) {

		// first get proper type and only given values. the empty string means the user has not entered anything
		var vals = []
		$.each([a,b], function(i,val) {
			if (val !== "") {
				vals.push( matchType(minOrMax, val) )
			}
		})
		
		// now that the values are in the proper type
		// if the user has not entered both values then things are fine
		var msg = []
		if (vals.length == 2) {
			if ( vals[0] >= vals[1] ) {
				msg = ['Initial value must be less than the second.']
			}
		}
		return msg
	}	
}

//simple boolean checkbox filter
Filters.Bool   = Filters.extend({
	onchange : function() {
		if (this._super()) return
		
		var val = $(this.$el +' input').attr("checked")
		if (val) {
			this.makeFilter()
		}
	},
	
	makeFilter : function() {
		this.filter = new Ogc.Comp({
			type:Ogc.Comp.EQUAL_TO,
			property:this.field,
			value:this.trueVal
		})
		return this.filter
	},
	
	linkChange : function() {
		$(this.$el +' input').click(this.onchange)
	},
	
	createDom : function() {
		return this._super() + this.label+': <input type="checkbox" >'
	}
})

// simple single value filter
Filters.Value  = Filters.extend({
	init : function(params) {
		// use size if given, use class if exists, default to 8 chars
		this.size       = defaultValue(params.size, params.class ? '' : 8)
		this.maxlength  = defaultValue(params.maxlength,256)
		this.minValue   = params.min
		this.maxValue   = params.max
		this.compare    = defaultValue(params.compare, Ogc.Comp.EQUAL_TO)
		this.pattern    = params.pattern
		this.patternMsg = params.patternMsg
		
		this._super(params)
	},
	
	onchange : function() {
		if (this._super()) return
		var val = $(this.$el +' input').val()
		
		if ( isDefined(val) && val.length ) {
			this.makeFilter(val)
		}
	},
	
	linkChange : function() {
		var _this = this
		$(this.$el +' input').blur(function(){_this.onchange()})
	},
	
	makeFilter : function(val) {
		var params = {
				property:this.field,
				value:val
			}
		if ( val.endsWith('*') ) {
			params.type=Ogc.Comp.LIKE
		} else {
			params.type=this.compare
		}
		this.filter = new Ogc.Comp(params)
		return this.filter
	},
	
	createDom : function() {
		var dom = this._super() + this.label+' <input type="text" '
		dom += 'size="'+this.size+'" '
		dom += 'maxlength="'+this.maxlength+'" '
		dom += '>'
		return dom
	},
	
	subvalidate : function() {
		var val = $(this.$el +' input').val()
		if (isUndefined(val) || val.length==0) return []
		var msgs = Filters.Validate.pattern(val, this.pattern, this.patternMsg)
		msgs = msgs.concat( Filters.Validate.max(val, this.maxValue) )
		msgs = msgs.concat( Filters.Validate.min(val, this.minValue) )
		return msgs
	}
})


Filters.Range  = Filters.extend({
	init : function(params) {
		this._super(params)
		
		var baseclass      = defaultValue(params.class, '')
		
		var minParams      = $.extend({}, params)
		minParams.el      += "-lo"
		minParams.label   += " between"
		minParams.compare  = Ogc.Comp.GREATER_THAN_OR_EQUAL_TO
		minParams.isPrime  = false
		minParams.parent   = this.$el
		minParams.class    = baseclass + " loRange"
		this.lo            = new Filters.Value(minParams)
		this.lo.$warn      = this.$warn
		
		var maxParams      = $.extend({}, params)
		maxParams.el      += "-hi"
		maxParams.label    = " and"
		maxParams.compare  = Ogc.Comp.LESS_THAN_OR_EQUAL_TO
		maxParams.isPrime  = false
		maxParams.parent   = this.$el
		maxParams.class    = baseclass + " hiRange"
		this.hi            = new Filters.Value(maxParams)
		this.hi.$warn      = this.$warn
		
		this.linkChange() // this already reand in super so have to do it again to catch the children
	},
	
	onchange : function(e) {
		if (this._super()) return
		this.lo.onchange()
		this.hi.onchange()

		if ( this.lo.filter && this.hi.filter ) {
			this.makeFilter()
		}
	},
	
	linkChange : function() {
		var _this = this
		$(this.$el +' input').blur(function(){_this.onchange()})
	},
	
	makeFilter : function() {
		if (this.lo.filter && this.hi.filter) 
		
		this.filter = new Ogc.Logic({
			type   : Ogc.Logic.AND,
			filters: [ this.lo.filter, this.hi.filter ]
		})
		return this.filter
	},

	createDom : function() {
		return this._super()
	},
	
	subvalidate : function() {
		var val  = $(this.$el +' input').val()
		var msgs = this.lo.validate(false) 
		msgs = msgs.concat( this.hi.validate(false) )
		
		if (msgs.length===0) {
			var rmsg = Filters.Validate.range(this.lo.val(), this.hi.val(), this.lo.minValue)
			msgs = msgs.concat(rmsg)
		}
		return msgs
	}
})


Filters.Option = Filters.extend({})



// TODO OptionFilter
	// TODO createOptionDom    as per cloneStateFilter
	// TODO getOptionValue     as per getStateValue
	// TODO removeOptionFilter as per removeStateFilter
	// TODO restOptionFilter   as per resetStateFilter
	// TODO addOptionFilter    as per addStateFilter
	// TODO onOptionFocus      as per onStateFocus
	// TODO onOptionChange     as per onStateChange


var stateFilter    = new Ogc.Logic({
	type:Ogc.Logic.OR
})
var basinFilter    = undefined
var hucFilter      = undefined
var minYrsFilter   = undefined
var refOnlyFilter  = undefined
var drainageFilter = undefined
var yearFilter     = ''        // THIS IS NOT OGC SO THE EMPTY STRING SETS IT ASSIDE


var getStateValues = function(el) {
    var st     = $(el).val()
    var state  = $(el).find('option:selected').text()
    return [st,state]
}

var removeStateFilter = function(state) {
	var indexes = []
    $.each(stateFilter.Or, function(index,filter){
        var key = Object.keys(filter)[0]
        var val = filter[key].value 
        if (val === state[0] || val === state[1]) {
        	indexes.push(index)
        }
    })
    // must be reversed because deleted early indexes will effect subsequent
    indexes.eachReverse(function(i){ 
    	stateFilter.Or.remove(i)
    })
}

var destroyStateFilter = function(self) {
	var state = getStateValues( $(self).parent().find('select') )
    removeStateFilter(state)
    $(self).parent().remove()
}

var addStateFilter = function(e) {
	var el     = e.srcElement
    var state  = getStateValues(el)

	$(el).data('oldState',state)
    
    // TODO generalize with the element id or attr
	stateFilter.Or.push( mp('=','STATE',state[0]) )
	stateFilter.Or.push( mp('=','STATE',state[1]) )
}

var getIdFromEvent = function(e) {
	return $(e.srcElement).attr('id')
}

var cloneStateFilter = function() {
	var next = $('#baseState').clone()
	next.attr('id','anotherState')
	$(next).find('select').attr('id','aState')
	next.append('<input type="button" value="-" class="destroy" onclick="destroyStateFilter(this)">')

    var state = getStateValues('#STATE')
	$(next).find('select').data('oldState', state)
	$(next).find('select').val([state[0]])
	$(next).change(onStateChange)
	// add the new state selection to the dom
	$('#states').prepend(next)
	// clear the original for then next state
	$('#STATE').val([''])
}

//store old value
var onStateFocus = function(e) {
	var el  = e.srcElement
    var val = $(el).val()
    var txt = $(el+' option:selected').text()
}
var onStateChange = function(e) {
	// first remove the old filter
	var el    = e.srcElement
	var state = $(el).data('oldState')
	removeStateFilter(state)
	// then add the new filter
	addStateFilter(e)
}

$().ready(function(){

	// TODO for each filer set click and blur
	
	$('input.refonly').click(onRefOnlyClick)
	$('#applyFilter').click(applyFilter)
	$('input.basin').blur(onBasinBlur)
	$('input.huc').blur(onHucBlur)
	$('input.drainage').blur(onDrainageBlur)
	$('input.yearRange').blur(onYearRangeBlur)
	$('input.minyrs').blur(onMinYrsBlur)

	// TODO for option filter set change event to addOptionFilter/createOptionFilter
	$('#STATE').change(function(e){
		addStateFilter(e)
		cloneStateFilter()
	})

	
	$('#clearFilter').click( function(e) {
		
		// TODO for each filter clear values
		$('#states').find('input.destroy').parent().remove()
		$('input.drainage').val('')
		$('input.yearRange').val('')
		$('input.basin').val('')
		$('input.huc').val('')
		$('input.minyrs').val('')
		$('input.refonly').attr("checked",false);
		$('.warn').html('')
		
		// TODO for each filter reset
		stateFilter    = {Or:[]}
		basinFilter    = undefined
		hucFilter      = undefined
		drainageFilter = undefined
		yearFilter     = '' // this is a non-ogc param that will need OGC for the webservice call
		minYrsFilter   = undefined
		refOnlyFilter  = undefined
		
		// reset all layers to have no filter
		applyFilterToLayers({filter:'',viewparams:''},'all')
	})
})


var applyFilterOld = function() {
	var filter = {And:[]}
	if (stateFilter.Or.length) {
		filter.And.push( stateFilter )
		var ogcXml = Ogc.filter(filter)

		applyFilterToLayers({filter:ogcXml}, ['States','Counties','NID'])
	}
}


//
//var rangeValidate = function(title,fields,warn,min,max) {
//	var errorText = ""
//	$(warn).text(errorText) // reset warning msg
//	var vals = []
//	$(fields).each(function(i,input) {
//		var val = $(input).val()
//		console.log(val)
//		if (val === "") return
//		var num = parseInt(val)
//		if (! $.isNumeric(val) || num<min || ($.isNumeric(max) && num>max) ) {
//			errorText = title+' must be at least '+min
//			if ( isDefined(max) ) {
//				errorText += ', to '+max
//			}
//			errorText += '.'
//			
//			$(input).focus()
//		}
//		vals.push(num)
//	})
//	
//	if (vals.length === 2) {
//		if (vals[0]>vals[1]) {
//			errorText = 'Initial value must be less than the second.'
//		} else {
//			return vals
//		}
//	} 
//	$(warn).text(errorText)
//	return false
//}




