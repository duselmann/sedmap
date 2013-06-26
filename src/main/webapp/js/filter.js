// this version of each does not conflict with the jquery version
// TODO well actually jquery needs a push method on this for some reason - will look into it or use underscore later
// jquery is in the way on this cool extension
// Object.prototype.each = function(callback) {
/*
var each = function(obj, callback) {
    for (var key in this) {
    	if (this.hasOwnProperty(key)) {
	        callback(this[key],key)
	    }
    }
}
Array.prototype.each = function(callback) {
    for (var i=0; i<this.length; i++) {
        callback(this[i],i) 
    }
}
*/
Array.prototype.eachReverse = function(callback) {
    for (var i=this.length-1; i>=0; i--) {
        callback(this[i],i) 
    }
}
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length)
  this.length = from < 0 ? this.length + from : from
  return this.push.apply(this, rest)
}

var Tag = new function() {
	this.OPEN  = false
	this.CLOSE = true

	this.make = function(tag,close) {
		if (close) {
			var index = tag.indexOf(" ")
			index = index>=0 ?index :tag.length 
			tag = tag.substring(0,index)
		}
	
		var clause = "<"+(close?'/':'')+tag+">"
		return clause
	}
	this.open = function(tag) {
		return this.make(tag, this.OPEN)
	}
	this.close = function(tag) {
		return this.make(tag, this.CLOSE)
	}
}

/*
<PropertyIsEqualTo>
<PropertyIsNotEqualTo>
<PropertyIsLessThan>
<PropertyIsLessThanOrEqualTo>
<PropertyIsGreaterThan>
<PropertyIsGreaterThanOrEqualTo>
*/
// And Or Not should be literal param clause keys



var Ogc = new function() {
	this.operators = {
		'=':'EqualTo',
		'!':'Not',
		'<':'LessThan',
		'>':'GreaterThan',
		'L':'Like wildCard="*" singleChar="." escape="!"',
	}
	this.opcodes = Object.keys(this.operators)

	this.filter = function(params) {
		var filter = this.clause("Filter", params)
		return filter
	}

	
	this.clause = function(tag, params) {
		tag = this.propertyTag(tag)
		var clause = Tag.make(tag)
		clause += this.inner(params)
		clause += Tag.make(tag, Tag.CLOSE)
		return clause
	}
	
	this.propertyTag = function(tag) {
		if (tag.length > 2) return tag
		var newTag = "PropertyIs"
		var or = ""
		for (var oc=0; oc<tag.length; oc++) {
			var opcode = tag[oc]
			if (this.opcodes.indexOf(opcode) < 0) {
				return tag
			}
			newTag += or + this.operators[opcode]
			or = opcode==="!" ?"" :"Or" // when joining 'not' does not use 'Or'
		}
		return newTag
	}
	
	this.inner = function(params) {
		var clause = ""
		var _this = this
			
		if (params.name && params.value) {
			clause += this.property(params.name, params.value)
		} else {
			$.each(params, function(p,child) {
				if ( isNaN(Number(p)) ) {
					clause += _this.clause(p, child)
				} else {
					clause += _this.inner(child)
				}
			})
		}
		return clause
	}


	this.property = function(name, value) {
		var property  = this.wrap("PropertyName",name)
			property += this.wrap("Literal",value)
		return property
	}
	
	
	this.wrap = function(tag, value) {
		var wrap = Tag.open(tag)+value+Tag.close(tag)
		return wrap
	}

}

var mp = function(op, name, value) {
	param = {}
	param[op] = {name:name,value:value}
	return param
}


var stateFilter    = {Or:[]}
var basinFilter    = undefined
var hucFilter      = undefined
var minYrsFilter   = undefined
var refOnlyFilter  = undefined
var drainageFilter = undefined

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
	var filter = {And:[]}
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

	$('input.refonly').click(onRefOnlyClick)
	$('#applyFilter').click(applyFilter)
	$('input.basin').blur(onBasinBlur)
	$('input.huc').blur(onHucBlur)
	$('input.drainage').blur(onDrainageBlur)
	$('input.yearRange').blur(onYearRangeBlur)
	$('input.minyrs').blur(onMinYrsBlur)

	$('#STATE').change(function(e){
		addStateFilter(e)
		cloneStateFilter()
	})

	$('#clearFilter').click( function(e) {
		$('#states').find('input.destroy').parent().remove()
		$('input.drainage').val('')
		$('input.yearRange').val('')
		$('input.basin').val('')
		$('input.huc').val('')
		$('input.minyrs').val('')
		$('input.refonly').attr("checked",false);
		
		stateFilter    = {Or:[]}
		basinFilter    = undefined
		hucFilter      = undefined
		drainageFilter = undefined
		yearRange      = '' // this is a non-ogc param that will need OGC for the webservice call
		minYrsFilter   = undefined
		refOnlyFilter  = undefined
		applyFilterToLayers({filter:'',viewparams:''},'all')
	})
})

var applyFilterToLayers = function(filter, applyTo) {
	// default all layers and use all layers for 'all' keyword
	if (applyTo === undefined || applyTo === 'all') {
		applyTo = Object.keys(layers)
	}
	$.each(applyTo, function(i,layerId) {
		layers[layerId].mergeNewParams(filter)
	})
}

var applyFilter = function() {
	var filter = {And:[]}
	if (stateFilter.Or.length) {
		filter.And.push( stateFilter )
		var ogcXml = Ogc.filter(filter)

		applyFilterToLayers({filter:ogcXml}, ['States','Counties','NID'])
	}
	if (basinFilter) {
		filter.And.push(basinFilter)
	}
	if (hucFilter) {
		filter.And.push(hucFilter)
	}
	if (drainageFilter) {
		filter.And.push(drainageFilter[0])
		filter.And.push(drainageFilter[1])
	}
	if (refOnlyFilter) {
		filter.And.push(refOnlyFilter)
	}
	if (filter.And.length) { // TODO need a layers based approach
		var ogcXml = Ogc.filter(filter)
		var layers = ['Instant Sites']
		if (!minYrsFilter) { // min yrs only applies to daily
			layers.push('Daily Sites')
		}
		applyFilterToLayers({filter:ogcXml,viewparams:yearRange}, layers)
	}
	
	if (minYrsFilter) {
		filter.And.push(minYrsFilter)
		var ogcXml = Ogc.filter(filter)
		var layers = ['Daily Sites']
		applyFilterToLayers({filter:ogcXml,viewparams:yearRange}, layers)
	}
}


var applyRange = function(field, values) {
	var rangeFilter = []
	rangeFilter.push( mp('>=',filed,values[0]) )
	rangeFilter.push( mp('<=',filed,values[1]) )
	return rangeFilter
}

var onDrainageBlur  = function() {
	if ( rangeValidate('Drainage', 'input.drainage', '#drainage-warn', 0) ) {
		drainageFilter = applyRange('DRAINAGE_AREA_MI_SQ',vals)
	}	
}
var onYearRangeBlur = function() {
	if ( rangeValidate('Year', 'input.yearRange', '#yearRange-warn', 1900, 'present') ) {
		var yr1 = $('#yr1').val()
		var yr2 = $('#yr2').val()
		yearRange = 'yr1:'+yr1+';yr2:'+yr2
	}
}

var rangeValidate = function(title,fields,warn,min,max) {
	var errorText = ""
	var vals = []
	$(fields).each(function(i,input) {
		var val = $(input).val()
		console.log(val)
		if (val === "") return
		if (! $.isNumeric(val) || val<min || ($.isNumeric(max) && val>max) ) {
			errorText = title+' must be at least '+min
			if (max !== undefined) {
				errorText += ', to '+max
			}
			errorText += '.'
			
			$(input).focus()
		}
		vals.push(val)
	})
	
	if (vals.length === 2) {
		if (vals[0]>vals[1]) {
			errorText = 'Initial value must be less than the second.'
		} else {
			return true
		}
	} 
	$(warn).text(errorText)
	return false
}

var onRefOnlyClick = function() {
	refOnlyFilter  = undefined
	if ( $('input.refonly').attr("checked") ) {
		refOnlyFilter = mp('=','REFERENCE_SITE',"1")
	}
}

var onMinYrsBlur = function() {
	var errorText = ""
	var val = $('input.minyrs').val()
	console.log(val)
	if (val === "") return
	if (! $.isNumeric(val) || val<0 ) {
		errorText = 'Min year must be a number between 1 and 60.'
		$('#drainage-warn').text(errorText)
		$('input.minyrs').focus()
		return
	}
	
	minYrsFilter = mp('>','SAMPLE_YEARS',val)
}

var onHucBlur = function(e) {
	var el  = e.srcElement
	var val = $(el).val()
	hucFilter = mp('L','HUC_12',val)
}


var onBasinBlur = function(e) {
	var el  = e.srcElement
	var val = $(el).val()
	basinFilter = mp('L','BASIN',val)
}



var Test = new function() {
	this.equal = function(actual, expect, msg) {
		if (actual !== expect) {
			msg = msg ?msg : "actual value was not equal to expected: '" + expect
			alert(msg + "' but was '" + actual + "'")
			return false
		}
		return true
	}
}

var tag    = "foo"
var expect = "<foo>"
var actual = Tag.open(tag)
Test.equal(actual,expect)
var expect = "</foo>"
var actual = Tag.close(tag)
Test.equal(actual,expect)
var tag    = 'foo attr="val"'
var expect = '<foo attr="val">'
var actual = Tag.open(tag)
Test.equal(actual,expect)
var expect = "</foo>"
var actual = Tag.close(tag)
Test.equal(actual,expect)


// test building property comparisons
var tag    = "foo"
var expect = "foo"
var actual = Ogc.propertyTag(tag)
Test.equal(actual,expect)
var tag    = "="
var expect = "PropertyIsEqualTo"
var actual = Ogc.propertyTag(tag)
Test.equal(actual,expect)
var tag    = "!="
var expect = "PropertyIsNotEqualTo"
var actual = Ogc.propertyTag(tag)
Test.equal(actual,expect)
var tag    = ">="
var expect = "PropertyIsGreaterThanOrEqualTo"
var actual = Ogc.propertyTag(tag)
Test.equal(actual,expect)

// simple clause test
var params = {name:'foo',value:'bar'}
var expect = "<clause><PropertyName>foo</PropertyName><Literal>bar</Literal></clause>"
var actual = Ogc.clause('clause',params)
Test.equal(actual,expect)

// logical multiple operator clause test
var param1 = {name:'foo1',value:'bar1'}
var param2 = {name:'foo2',value:'bar2'}
var params = {Or:[{'=':param1},{'!=':param2}]}
var expect = "<clause><Or><PropertyIsEqualTo><PropertyName>foo1</PropertyName><Literal>bar1</Literal></PropertyIsEqualTo><PropertyIsNotEqualTo><PropertyName>foo2</PropertyName><Literal>bar2</Literal></PropertyIsNotEqualTo></Or></clause>"
var actual = Ogc.clause('clause',params)
Test.equal(actual,expect)

// complex logical operator filter test - note the test params do not have to make logical sense to test xml rendering
var param1 = {name:'foo1',value:'bar1'}
var param2 = {name:'foo2',value:'bar2'}
var params = [{Or:[{'>=':param1},{'!=':param2}]},{And:[{'=':param1},{'=':param2}]},{And:[{'=':param1},{'=':param2}]}]
var expect = "<Filter><Or><PropertyIsGreaterThanOrEqualTo><PropertyName>foo1</PropertyName><Literal>bar1</Literal></PropertyIsGreaterThanOrEqualTo><PropertyIsNotEqualTo><PropertyName>foo2</PropertyName><Literal>bar2</Literal></PropertyIsNotEqualTo></Or><And><PropertyIsEqualTo><PropertyName>foo1</PropertyName><Literal>bar1</Literal></PropertyIsEqualTo><PropertyIsEqualTo><PropertyName>foo2</PropertyName><Literal>bar2</Literal></PropertyIsEqualTo></And><And><PropertyIsEqualTo><PropertyName>foo1</PropertyName><Literal>bar1</Literal></PropertyIsEqualTo><PropertyIsEqualTo><PropertyName>foo2</PropertyName><Literal>bar2</Literal></PropertyIsEqualTo></And></Filter>"
var actual = Ogc.filter(params)
Test.equal(actual,expect)

// actual logical filter test
var param1 = {name:'STATE',value:'WI'}
var param2 = {name:'STATE',value:'Wisconsin'}
var params = {Or:[{'=':param1},{'=':param2}]}
var expect = "<Filter><Or><PropertyIsEqualTo><PropertyName>STATE</PropertyName><Literal>WI</Literal></PropertyIsEqualTo><PropertyIsEqualTo><PropertyName>STATE</PropertyName><Literal>Wisconsin</Literal></PropertyIsEqualTo></Or></Filter>"
var actual = Ogc.filter(params)
Test.equal(actual,expect)























