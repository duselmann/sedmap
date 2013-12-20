/*global Util*/
(function () {
    $(document).ready(function () {
        $.each($('#filterDiv input, #filterDiv select'), function (index, inputElt) {
            Util.highlightApplyFilterButtonOnInputChange(inputElt);
        });
    });
}());
