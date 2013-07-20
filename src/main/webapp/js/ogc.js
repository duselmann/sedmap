/*
<PropertyIsEqualTo>
<PropertyIsNotEqualTo>
<PropertyIsLessThan>
<PropertyIsLessThanOrEqualTo>
<PropertyIsGreaterThan>
<PropertyIsGreaterThanOrEqualTo>
*/
// And Or Not should be literal param clause keys


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
			
		if (params.name === undefined && params.value === undefined) {
			// handle non-property entries
			$.each(params, function(p,child) {
				if ( $.isNumeric(p) ) {
					clause += _this.inner(child)
				} else {
					clause += _this.clause(p, child)
				}
			})
		} else {
			// handle property entries
			clause += this.property(params.name, params.value)
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

	this.makeProperty = function(op, name, value) {
		param = {}
		param[op] = {name:name,value:value}
		return param
	}
	// abbrev for makeProperty
	this.mp = function(op, name, value) {
		return this.makeProperty(op, name, value)
	}
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

