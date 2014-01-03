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


var getFilters = function(parentGroupEl, layerName) {
	var hasErrors = $(parentGroupEl + ' .filterWarn:not(:empty):not(#applyFilter-warn)').length>0
	if (hasErrors) {
		$('#applyFilter-warn').fadeIn(1000).delay(1000).fadeOut(1000)
		return
	}

	var filters = [];

	// build OGC filter
	$.each(Filters.Instances[parentGroupEl], function(i,inst) {
		// if the user entered a value for this filter
		if ( isDefined(inst.filter) ) {
			// also if it is of the layer of interest
			if ( inst.layers.indexOf('all') !== -1 || inst.layers.indexOf(layerName) !== -1 ) {
				filters.push(inst.filter)
			}
		}
	})

	// encode each layers filters
	if (filters.length>0) {
		var filter = new Ogc.Logic({
			type   : Ogc.Logic.AND,
			filters: filters
		})
		filters = Ogc.encode(filter)
	} else {
		filters = ''
	}
	
	return filters;
}




var checkForErrorsBeforeApplingFilters = function(parentGroupEl) {
    var hasErrors = $(parentGroupEl + ' .filterWarn:not(:empty):not(#applyFilter-warn)').length>0
    if (hasErrors) {
        $('#applyFilter-warn').fadeIn(1000).delay(1000).fadeOut(1000)
        return true
    }
    return false
}
var initLayerFilters = function(map) {
    var layerFilters = {}
	var layerOr={}
    $.each(map.layers, function(i,layer){
        layerFilters[layer.name]=[]
        layerOr[layer.name]=[]
        if ( isUndefined(Filters.Layers[layer.name]) ) {
            Filters.Layers[layer.name] = {filter:'',viewparams:''}
        } 
    })
    return [layerFilters,layerOr]
}
var buildEachLayerOGCandViewparamFilter = function(map, parentGroupEl, layerFilters) {

    var filters      = initLayerFilters(map)
    var layerFilters = filters[0]
    var layerOr      = filters[1]
    var filterGroup  = Filters.Instances[parentGroupEl]
    
    $.each(filterGroup, function(f, inst) {
        // isMapOgc is used to differentiate viewparams from OGC standard filters like year range 
        if ( inst.isMapOgc && (isDefined(inst.filter) || isDefined(inst.orDefaultFilter)) ) {
            $.each(inst.layers, function(i, layerTitle) {
                var orClauses = layerOr[layerTitle]
                if (inst.orWith) {
                    // if there is an orWith clause then handle it
                    var orClause = []
                    // first check if there is an orClause with the given orWith dependencies commenced
                    $.each(inst.orWith, function(w,orWith) {
						var fw = filterGroup.indexOf(orWith)
                        if (isDefined(orClauses[fw])) {
                            orClause = orClauses[fw]
                        }
                    })
                    // if there is no existing orClause then init one
                    if (orClause.length===0) {
                        orClauses[f] = orClause
                    }
                    // if the main filter has not been set then take the default filter for or-clause
                    var filter = isDefined(inst.filter) ?inst.filter :inst.orDefaultFilter
                    // finally add this clause to the orClause
                    orClause.push(filter)
                } else {
                    layerFilters[layerTitle].push(inst.filter)
                }
            })
        }
    })
    
    // if there are orClauses then add then to the parent filter
    $.each(layerOr, function(layerTitle, orClauses) {
		$.each(Object.keys(orClauses), function(i, inst) {
    	    var orClause = orClauses[inst][0]
	        // if there is only one then there is no need for or-wrapper
            if (orClauses[inst].length > 1) {
                var newOr = new Ogc.Logic({
                    type   : Ogc.Logic.OR,
                    filters: orClauses[inst]
                })
                orClause = newOr
            } else if (orClause.value === filterGroup[inst].orDefaultValue) {
                orClause = undef;
            }
            // apply filter if the orClause survived
            if (isDefined(orClause)) {
                layerFilters[layerTitle].push(orClause)
            }
 	    })
    })
	
	return layerFilters
}

var encodeEachLayerFilterOGC = function(layerFilters) {
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
}

// because year filter has to be a different, viewparams, filter for geoserver.
// this is annoying but geoserver handles viewparams outside the OGC query.
// we will have to apply year to the OGC for data download with the isMapOgc flag ignored.
var makeYearFilter = function() {
    var filter = theYearFilter.filter
    if ( isUndefined(filter) ) {
        return ''
    }
    var yr1 = filter.filters[0].value
    var yr2 = filter.filters[1].value
    return 'yr1:'+yr1+';yr2:'+yr2
}

// apply for each layer and each filter
var applyFilters = function(parentGroupEl) {
    if ( checkForErrorsBeforeApplingFilters(parentGroupEl) ) return
	var layerFilters = buildEachLayerOGCandViewparamFilter(map, parentGroupEl, layerFilters)
    encodeEachLayerFilterOGC(layerFilters)
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
		
			// do not apply a filter against an unchanged filter layer
			if (Filters.Layers[layer.name].filter     !== ogcXml
			 || Filters.Layers[layer.name].viewparams !== localYearFilter) {
				
				layer.mergeNewParams({filter:ogcXml,viewparams:localYearFilter})
				Filters.Layers[layer.name].filter     = ogcXml
			 	Filters.Layers[layer.name].viewparams = localYearFilter
				
			}
		}
	})
}



var clearFilters = function(parentGroup) {
	$.each(Filters.Instances[parentGroup], function(i,filter){
		filter.clear()
	})
	applyFilters(parentGroup)
}







var Filters = Class.extend({
	init : function(params) {
		this.el      = params.el
		this.$el     = '#'+params.el
		this.$warn   = this.$el+'-warn'
		this.parent  = params.parent
		this.group   = defaultValue(params.group, params.parent)
		this.field   = params.field
		this.classEl = defaultValue(params.clazz,'') + ' filterAll'
		this.label   = params.label
		this.lblClass= defaultValue(params.labelClass,'')
		this.errClass= defaultValue(params.errorClass,'inputFilterWarn')
		this.layers  = defaultValue(params.layers,['all'])
		this.isMapOgc= defaultValue(params.isMapOgc,true)
		this.filter  = undefined
		this.isPrime = defaultValue(params.isPrime, true) // default not sub-filter
		this.callback= params.callback
                this.helpText = params.helpText
        this.orWith  = params.orWith
        this.orDefaultValue = params.orDefaultValue
		
		// if there is no give clear action then used the default undefined action
		if (params.clearAction) {
			this.clear = params.clearAction
		}
		
		this.setDefaultOrFilter(this.orDefaultValue)
		
		if (this.isPrime) {
			this.checkAndInitParentGroup()
			Filters.Instances[this.parent].push(this)
		}
		var dom = this.createDom() + this.endDom()
		// this is so composite filters get one error div
		if (this.isPrime) {
			dom += this.errorDom()
		}
		$(this.group).append(dom) // attach the new dom to the parent or if specified then the group
		$(this.parent).trigger('childchange');
		
		if (this.isPrime) {
			this.linkEvents()
		}
	},

    setDefaultOrFilter : function(orDefaultValue) {
        if (isDefined(orDefaultValue)) {
            this.orDefaultValue = orDefaultValue
            var save = this.filter
            this.orDefaultFilter = this.makeFilter(orDefaultValue)
            this.filter = save // TODO makeFilter should not set filter any more
        }
    },

	
	checkAndInitParentGroup : function() {
		// if there is no group yet and needed then make it
		if ( this.group !== this.parent && $(this.group).length === 0 ) {
			// use the group el without the # for jquery
			var group = this.group.replace('#','')
			$(this.parent).append('<div id="'+group+'" class="filterGroup"><div class="filterGroupLabel">'+group+' Filters'+'</div></div>')
		}
		if ( isDefined(Filters.Instances[this.parent]) ) return
		
		Filters.Instances[this.parent] = []
		
		var _this = this
		
		$().ready(function(){
			$(_this.parent + " input" ).keypress(function(event){
                var keyCode = (event.keyCode ? event.keyCode : event.which);
                if (keyCode == '13' || keyCode === 13) { // on enter
                	var element = event.target || event.srcElement;
                    $(element).blur().focus()
					// This is to prevent IE from executing the click before the blur
                    setTimeout(function(){$('.applyFilter').click()},10)
				}
			})
			// I would have like this to be related to the filter parent
			// but now that it is a menu we have to rethink how - no time
            $('.clearFilter').click(function(){clearFilters(_this.parent)})
			$('.applyFilter').click(function(){applyFilters(_this.parent)})
			
			$('#DL-download').click(function(){
				// TODO re-factor to a callback of some fashion
				var isDaily = $('#DL-daily:checkbox:checked').length != 0
				var isDiscr = $('#DL-discrete:checkbox:checked').length != 0
				
				if (!isDaily && !isDiscr) {
                    $('#DL-msg').html("Please select a data set.")
				    return
				}
				
                var isData  = $('#DL-sitesOnly:checkbox:checked').length == 0
                var isFlow  = $('#DL-discreteFlow:checkbox:checked').length != 0
                var email   = $("#DL-email").val()
				var urlPart = []
				var p = 0
				urlPart[p++] = "/sediment/data?format="
				urlPart[p++] = $('#DL-format').val()
                urlPart[p++] = "&email="+ email
				urlPart[p++] = "&dataTypes=sites_" // always include site info
				urlPart[p++] = isData  ?"data_"     :""
				urlPart[p++] = isDaily ?"daily_"    :""
                urlPart[p++] = isDiscr ?"discrete_" :""
                urlPart[p++] = isFlow  ?"flow_" :""
				urlPart[p++] = isDaily ?"&dailyFilter="   +getFilters(_this.parent, DAILY)    :""
				urlPart[p++] = isDiscr ?"&discreteFilter="+getFilters(_this.parent, DISCRETE) :""
				var url = urlPart.join("")
				//console.log(url)
                $('#DL-msg').html("request sent")
				if (email.indexOf('@') === -1) {
					window.location.href = url
                    closeDL();
				} else {
				    $.get(url, function(data) {
				        $('#DL-msg').html(data)
				        clearDelay('#DL-msg')
				        closeDL();
 				    }).done(function(data){
				    	$('#DL-msg').html(data)
                        clearDelay('#DL-msg')
                        closeDL();
				    }).fail(function(data){
				    	$('#DL-msg').html(data)
				    });
				}
			})
			
			$(_this.parent).on('childchange',updateFilterScroll)
		})
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
		if (this.classEl) dom += 'class="'+this.classEl+' '+this.oddEvenClass()+'"'
		dom += '>' + this.createLabel()
		return dom
	},
	endDom : function() {
		return '</div>';
	},
        createHelpText: function(){
            var helpDom = '';
            if(isDefined(this.helpText)){
                helpDom = '<a class="helpText" title="'+this.helpText+'"><img class="helpIcon" src="images/help.png"/></a>';
            }
            return helpDom;
        },
	createLabel : function() {
		var label = ''
		if (isDefined(this.label) && this.label.length) {
			label = '<span class="label '+this.lblClass+'">' + this.label + this.createHelpText() + '</span>';
		}
		return label
	},
	oddEvenClass : function() {
		if (Filters.Instances.length % 2) { // 0 is false so is odd
			return Filters.OddClass
		} else {
			return Filters.EvenClass
		}
	},
	
	errorDom : function() {
		return '<div id="'+this.el+'-warn" class="filterWarn"></div>'
	},
	
	onchange : function() {
		this.clearFilter()
        var callbackOk = true
        if ( isDefined(this.callback) ) {
            callbackOk = this.callback(this.$el)
        }
        if (callbackOk) {
            this.validate()
        }
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
			Filters.applyWarn(this.$el, this.$warn, this.parent, this.errClass, msgsText)
		}
		return msgs
	},
	
	validateReset : function() {
		if ($(this.$warn).html() !== ''){
			$(this.parent).trigger('childchange');
		}
		
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


Filters.applyWarn = function(el, elWarn, parent, errClass, msg) {
    $(el).addClass(errClass)
    $(elWarn).addClass(errClass+"On")
    $(elWarn).html(msg)
    $(parent).trigger('childchange')
}

// storage of the filter obj instances
Filters.Instances = {}
// storage of the unique filter names
Filters.Layers = {}
// the openlayers map these filters apply
Filters.Map = undefined

Filters.OddClass  = 'filterOdd'
Filters.EvenClass = 'filterEven'

Filters.defaultDecor = function(value){return value}


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
		
		params.clazz = defaultValue(params.clazz,'') + ' filterBool'
		this._super(params)
	},
	
	onchange : function() {
		if (this._super()) return
		
		var val = $(this.$el +' :checkbox:checked').length > 0
		if (val) {
			this.makeFilter()
		}
	},
	
	linkEvents : function() {
		var _this = this
		$(this.$el +' input').click(function(){_this.onchange()})
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
		return this._super()+' <input type="checkbox" >'
	}
})



// simple single value filter
Filters.Value  = Filters.extend({
	init : function(params) {
		// use size if given, use class if exists, default to 8 chars
		this.size       = defaultValue(params.size, params.clazz ? '' : 8)
		this.maxlength  = defaultValue(params.maxlength,256)
		this.minValue   = params.min
		this.maxValue   = params.max
		this.compare    = defaultValue(params.compare, Ogc.Comp.EQUAL_TO)
		this.pattern    = params.pattern
		this.patternMsg = params.patternMsg
		this.valueDecor = defaultValue(params.valueDecorator, Filters.defaultDecor)
		
		params.clazz = defaultValue(params.clazz,'') + ' filterValue'
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
	    val = this.valueDecor(val)
		var params = {
				property:this.field,
				value:val
			}
		if (  val.indexOf('*') === -1 ) {
            params.type=this.compare
		} else {
            params.type=Ogc.Comp.LIKE
		}
		this.filter = new Ogc.Comp(params)
		return this.filter
	},
	
	createDom : function() {
		var dom = this._super()+' <input type="text" '
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
		var label = params.label
		params.label=""
		params.clazz = defaultValue(params.clazz,'') + ' filterRange'
		this._super(params) // had to call this first so the parent conatiner exists
		
		var baseclass      = defaultValue(params.clazz, '')
		
		var minParams      = $.extend({}, params)
		minParams.el      += "-lo"
		minParams.label    = label + " between"
		minParams.compare  = Ogc.Comp.GREATER_THAN_OR_EQUAL_TO
		minParams.isPrime  = false
		minParams.parent   = this.$el
		minParams.group    = undefined
		minParams.clazz    = baseclass + " loRange"
		this.lo            = new Filters.Value(minParams)
		this.lo.$warn      = this.$warn
		
		var maxParams      = $.extend({}, params)
		maxParams.el      += "-hi"
		maxParams.label    = " and"
		maxParams.compare  = Ogc.Comp.LESS_THAN_OR_EQUAL_TO
		maxParams.isPrime  = false
		maxParams.parent   = this.$el
		maxParams.group    = undefined
		maxParams.clazz    = baseclass + " hiRange"
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
		
		params.clazz = defaultValue(params.clazz,'') + ' filterOption'
		this._super(params)
	},

	setOldVal: function(srcEl,dstEl) {
	    var opt = getOptionValues(srcEl)
		$(dstEl).data('oldVal',opt)
	},
	
	onchange: function(e){
		this.addFilter(e)
                var optDom = this.createOptionDom();
                var selectDom = $(optDom).find('select');
                Util.highlightApplyFilterButtonOnInputChange(selectDom);
		// add the new state selection to the dom
		$(this.$optDiv).prepend(optDom)
		$(this.parent).trigger('childchange');
		this.linkEvents()
		
		var num = this.filter.filters.length
		this.setOldVal(this.$el+0, this.$el+num)
		$(this.$el+num).val([$(this.$el+'0').val()])

		// clear the original for then next state
		$(this.$el+'0').val([''])
	},

	optionchange: function(e) {
		// first remove the old filter
		var el    = e.originalEvent.srcElement || e.originalEvent.target
		var oldVal = $(el).data('oldVal')
		this.removeFilter(oldVal)
		// then add the new filter
		this.addFilter(e)
                Util.highlightApplyFilterButton();
		// update the old value for the next pissible remove/change
		this.setOldVal(el, el)
	},
	addFilter: function(e) {
		var el  = e.target
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
	
	// TODO pull sytle out into css class
	createDom: function() {
		var dom = '<div id="'+this.el+'" '
		if (this.classEl) dom += 'class="'+this.classEl+' '+this.oddEvenClass()+'" style="padding-bottom:15px" ' 
		dom += '><div id="a" style="display:inline-block">' + this.createLabel()
		dom += '<div  id="b" style="display:inline-block;position:absolute;top:1px;left:175px;">'
		dom += this.createOptionDom() + '</div></div><div id="'+this.optDiv+'" style="left: 175px;position: relative;top: 10px;">'
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
	}
})


// called when 'childchange' triggered 
function updateFilterScroll(e) {
	var childHeight = 0
	$(e.target).children().each(function(i,child) {
		childHeight += $(child).height()
	})

    var styleClass = getStyle('div.filter')
	if ( childHeight > styleClass.height) {
		$(e.target).addClass('filterScroll')
	} else {
		$(e.target).removeClass('filterScroll')
	}
}

