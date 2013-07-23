// should consider underscore.js

if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function (str){
    	if (str===undefined || str.length===0) return false
    	return this.indexOf(str) == 0
    }
}
if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function (str) {
    	if (str===undefined || str.length===0) return false
    	return this.indexOf(str) == this.length-str.length
    }
}

if (typeof Array.prototype.eachReverse !== 'function') {
	Array.prototype.eachReverse = function(callback) {
	    for (var i=this.length-1; i>=0; i--) {
	        callback(this[i],i) 
	    }
	}
}

if (typeof Array.prototype.remove !== 'function') {
	Array.prototype.remove = function(from, to) {
	    var rest = this.slice((to || from) + 1 || this.length)
	    this.length = from < 0 ? this.length + from : from
	    return this.push.apply(this, rest)
	}
}

if (typeof Array.prototype.each !== 'function') {
	Array.prototype.each = function(callback) {
		for (var i=0; i<this.length; i++) {
		    callback(this[i],i)
		}
	}
}

if (typeof Array.prototype.concat !== 'function') {
	Array.prototype.concat = function(newElements) {
		newElements = isArray(newElements) ? newElements : [newElements]
		return this.push.apply(this, newElements)
	}
}

function defaultValue(val, defaultval) {
	return isDefined(val) ?val : defaultval
}

// returns true if the number is an integer
function isInt(val) {
	if ($.isNumeric(val)) {
		var v = parseFloat(val)
		return v == Math.floor(v)
	}
	return false
}

// use $.isNumeric(val) if you do not want float specifically
function isFloat(val) {
	if ($.isNumeric(val)) {
		var v = parseFloat(val)
		return v != Math.floor(v)
	}
	return false
}

function isString(val) {
	return typeof val === 'string'
}
function isObject(val) {
	return typeof val === 'object'
}
function isArray(val) {
	return typeof val === 'array'
}
function isNumber(val) {
	return typeof val === 'number'
}
function isUndefined(val) {
	return val === undefined
}
function isDefined(val) {
	return ! isUndefined(val)
}

// if source is a string then target will be returned as string
// if source is a number then target will be returned as a number if possible
// motivation: need like types for proper compare - lexical or numeric
function matchType(source, target) {
	var match = target
	
	if ( isNumber(source) && isString(target) ) {
		match = parseFloat(target)
	} else if ( isString(source) && isNumber(target) ) {
		match = ''+target
	}
	return match
}
// this might knock out Array proto each
// this DOES conflict with OpenLayers OGC encoding.
// we end up with an each attr with the js code in it. YUCK!
//if (typeof Object.prototype.each !== 'function') {
//	Object.prototype.each = function(callback) {
//		for (var key in this) {
//			if (this.hasOwnProperty(key)) {
//				callback(key, this[key])
//			}
//		}
//	}
//}


function encodeHtml(value){
  //create a in-memory div, set it's inner text(which jQuery automatically encodes)
  //then grab the encoded contents back out.  The div never exists on the page.
  return $('<div/>').text(value).html();
}

function decodeHtml(value){
  return $('<div/>').html(value).text();
}


function getIdFromEvent(e) {
	return $(e.srcElement).attr('id')
}
function getOptionValues(el) {
    var val  = $(el).val()
    var txt  = $(el).find('option:selected').text()
    return {val:val,txt:txt}
}


	
/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
 
  // The base Class implementation (does nothing)
  this.Class = function(){};
 
  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;
   
    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;
   
    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;
           
            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];
           
            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);        
            this._super = tmp;
           
            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }
   
    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }
   
    // Populate our constructed prototype object
    Class.prototype = prototype;
   
    // Enforce the constructor to be what we expect
    Class.prototype.constructor = Class;
 
    // And make this class extendable
    Class.extend = arguments.callee;
   
    return Class;
  };
})();