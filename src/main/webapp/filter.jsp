<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
    
    <style>
    	div.warn {
    		color:red;
    	}
    </style>
    
    <body ><div id="filter">
        Filter By:    	 
        <input type="button" id="clearFilter" value="Clear">
        <input type="button" id="applyFilter" value="Apply">
        <br>
        States: <div id="states">
        <div id="baseState" style="margin-left:50px">
    	<select id="STATE">
			<option value="">Select a State</option>
			<option value="AK">Alaska</option><option value="AL">Alabama</option><option value="AZ">Arizona</option><option value="AR">Arkansas</option>
			<option value="CA">California</option><option value="CO">Colorado</option><option value="CT">Connecticut</option>
			<option value="DE">Delaware</option>
			<option value="FL">Florida</option>
			<option value="GA">Georgia</option>
			<option value="HI">Hawaii</option>
			<option value="ID">Idaho</option><option value="IL">Illinois</option><option value="IN">Indiana</option><option value="IA">Iowa</option>
			<option value="KS">Kansas</option><option value="KY">Kentucky</option>
			<option value="LA">Louisiana</option>
			<option value="ME">Maine</option><option value="MD">Maryland</option><option value="MA">Massachusetts</option>
			<option value="MI">Michigan</option><option value="MN">Minnesota</option><option value="MS">Mississippi</option>
			<option value="MO">Missouri</option><option value="MT">Montana</option>
			<option value="NE">Nebraska</option><option value="NV">Nevada</option><option value="NH">New Hampshire</option>
			<option value="NJ">New Jersey</option><option value="NM">New Mexico</option><option value="NY">New York</option>
			<option value="NC">North Carolina</option><option value="ND">North Dakota</option>
			<option value="OH">Ohio</option><option value="OK">Oklahoma</option><option value="OR">Oregon</option>
			<option value="PA">Pennsylvania</option>
			<option value="RI">Rhode Island</option>
			<option value="SC">South Carolina</option><option value="SD">South Dakota</option>
			<option value="TN">Tennessee</option><option value="TX">Texas</option>
			<option value="UT">Utah</option>
			<option value="VT">Vermont</option><option value="VA">Virginia</option>
			<option value="WA">Washington</option><option value="WV">West Virginia</option>
			<option value="WI">Wisconsin</option><option value="WY">Wyoming</option>
    	</select>
    	</div>
    	</div>
    	<BR>
<!--    	FIPS County: <input id="FIPS_CNTY" type="text">
    	<BR> -->
    	HUC: <input id="HUC_12" class="huc" type="text">
    	<BR>
    	Min Years of Daily Data: <input id="minyrs" class="minyrs" type="text" size="4" maxlength="2">
    	<BR>
    	Drainage Area (mi<super>2</super>): between <br>
    		<input id="DRAINAGE_AREA_MI_SQ-low" class="drainage" type="text" size="8">
    		and 
    		<input id="DRAINAGE_AREA_MI_SQ-high" class="drainage" type="text" size="8">
    		<div id="drainage-warn" class="warn"></div>
    </div></body>
    
    <script type="text/javascript" src="js/filter.js"></script>
    
</html>

