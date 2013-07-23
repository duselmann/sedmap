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
var applyFilters = function() {
	var hasErrors = $('.filterWarn:not(:empty):not(#applyFilter-warn)').length>0
	if (hasErrors) {
		$('#applyFilter-warn').fadeIn(1000).delay(1000).fadeOut(1000)
		return
	}

	var layerFilters = {}
	$.each(map.layers, function(i,layer){
		layerFilters[layer.name]=[]
	})

	// build each layers' OGC filter
	$.each(Filters.Instances, function(i,inst) {
		if ( inst.isMapOgc && isDefined(inst.filter) ) {
			// default to filter layers and use all layers for keyword 'all' 
			var applyTo = inst.layers
			if ( inst.layers.indexOf('all') !== -1 ) {
				applyTo = []
				$.each(map.layers, function(i,layer){
					applyTo.push( layer.name )
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
		if ( theYearFilter.layers.indexOf(layer.name) >= 0 ) {
			localYearFilter = yearFilter
		}
		var ogcXml = layerFilters[layer.name]
		// apply filter
		if ( isDefined(layer.mergeNewParams) ) {
			layer.mergeNewParams({filter:ogcXml,viewparams:localYearFilter})
		}
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


var clearFilters = function() {
	$.each(Filters.Instances, function(i,filter){
		filter.clear()
	})
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
			this.linkEvents()
		}
	},

	// function that is called when the user wants to clear this filter
	clearFilter : function() {
		// default clearing is the undefined
		this.filter = undefined
	},
	clear : function() {
		this.clearFilter()
		this.validateReset()
		$(this.$el+' input').val('')
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
	
	linkEvents : function() {
		var _this = this
		$(this.$el +' input').blur(function(){_this.onchange()})
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
	init: function(params) {
		this.trueVal = params.trueVal
		
		this._super(params)
	},
	
	onchange : function() {
		if (this._super()) return
		
		var val = $(this.$el +' input').attr("checked")
		if (val) {
			this.makeFilter()
		}
	},

	clear: function() {
		$(this.$el+' input').attr("checked",false)
		this._super()
	},
	
	makeFilter : function() {
		this.filter = new Ogc.Comp({
			type:Ogc.Comp.EQUAL_TO,
			property:this.field,
			value:this.trueVal
		})
		return this.filter
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
		this._super(params) // had to call this first so the parent conatiner exists
		
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
		
		this.linkEvents() // this already ran in super so have to do it again to catch the children
	},
	
	onchange : function(e) {
		if (this._super()) return
		this.lo.onchange()
		this.hi.onchange()

		if ( this.lo.filter && this.hi.filter ) {
			this.makeFilter()
		}
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


Filters.Option = Filters.extend({
	
	init: function(params) {
		this.options   = params.options
		this.baseTxt   = params.baseTxt
		this.optDiv    = params.el+'-inner'
		this.$optDiv   = '#'+this.optDiv
		this.globalRef = 'optFilter'+Math.floor(Math.random()*100000)
		
		window[this.globalRef] = this
		
		this._super(params)
	},

	setOldVal: function(srcEl,dstEl) {
	    var opt = getOptionValues(srcEl)
		$(dstEl).data('oldVal',opt)
	},
	
	onchange: function(e){
		this.addFilter(e)
		var optDom = this.createOptionDom()
		// add the new state selection to the dom
		$(this.$optDiv).append(optDom)
		this.linkEvents()
		
		var num = this.filter.filters.length
		this.setOldVal(this.$el+0, this.$el+num)
		$(this.$el+num).val([$(this.$el+'0').val()])

		// clear the original for then next state
		$(this.$el+'0').val([''])
	},

	optionchange: function(e) {
		// first remove the old filter
		var el    = e.originalEvent.srcElement
		var oldVal = $(el).data('oldVal')
		this.removeFilter(oldVal)
		// then add the new filter
		this.addFilter(e)
		// update the old value for the next pissible remove/change
		this.setOldVal(el, el)
	},
	addFilter: function(e) {
		var el  = e.originalEvent.srcElement
	    var opt = getOptionValues(el)

		if ( isUndefined(this.filter)) {
			this.filter = new Ogc.Logic({type:Ogc.Logic.OR})
		}
		
		this.filter.filters.push(new Ogc.Comp({
			type:Ogc.Comp.EQUAL_TO,
			property:this.field,
			value:opt.val}))
		this.filter.filters.push(new Ogc.Comp({
			type:Ogc.Comp.EQUAL_TO,
			property:this.field,
			value:opt.txt}))
	},
	
	onRemoveFilter:function(self) {
		var el  = $(self).find('select')
		var opt = getOptionValues(el)
		this.removeFilter(opt)
	    $(self).remove()
	},
	
	removeFilter: function(opt) {
		var indexes = []
	    $.each(this.filter.filters, function(index,filter){
	        var val = filter.value
	        if (val === opt.val || val === opt.txt) {
	        	indexes.push(index)
	        }
	    })
	    // must be reversed because deleted early indexes will effect subsequent
	    var _this=this
	    indexes.eachReverse(function(i){ 
	    	_this.filter.filters.remove(i)
	    })
	    if ( ! this.filter.filters.length ) {
	    	this.filter = undefined // TODO cannot call clearFilter at this time
	    }
	},
	
	clearFilter : function() {
		$(this.$optDiv).html('')
		this._super()
	},
	
	createDom: function() {
		var dom = this._super() + this.label+'<div> <div id="'+this.optDiv+'"></div>'
		dom += this.createOptionDom() + '</div>'
		return dom
	},
	
	createOptionDom: function(isBase) {
		var num = isDefined(this.filter) ?this.filter.filters.length :0
		var isBase = num===0
		var dom = '<div><select id="'+this.el+num+'">'
		if (isBase) {
			dom += '<option value="">'+this.baseTxt+'</option>'
		}
		$.each(this.options, function(val,txt){
			dom += '<option value="'+val+'">'+txt+'</option>'
		})
		if (!isBase) {
			dom += '<input type="button" value="-" class="destroy" onclick="'+this.globalRef+'.onRemoveFilter($(this).parent())"><br>'
		}
		dom += '</select></div>'
		return dom
	},
	
	linkEvents: function() {
		var num = isDefined(this.filter) ?this.filter.filters.length :0
		var _this = this

		if (num===0) {
			$(this.$el+num).change(function(e){_this.onchange(e)})
		} else {
			$(this.$el+num).change(function(e){_this.optionchange(e)})
		}
	},
})

$().ready(function(){
	$('body').on('keypress',function(e){
		if (e.keyCode === 13) {
		    // on enter
			applyFilters()
		}
	})
	$('#applyFilter').click(applyFilters)
	$('#clearFilter').click(clearFilters)
})
