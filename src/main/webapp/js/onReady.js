/*global Util*/
(function () {
    $(document).ready(function () {
        $.each($('#filterDiv input, #filterDiv select'), function (index, inputElt) {
            Util.highlightApplyFilterButtonOnInputChange(inputElt);
        });
        if('true' !== $.cookie('hideSplash')){
            $('#splashContent').dialog({
                modal: true,
                width: 500,
                dialogClass: 'sedmap-modal',
                buttons: {
                    "OK" : function(){
                        $(this).dialog('close');
                    },
                    "Do not display this message in the future" : function(){
                        $.cookie('hideSplash', 'true');
                        $(this).dialog('close');
                    }
                }
            });
        }
    });
}());
