/**
 * Created by franco on 1/10/2015.
 */


$('#shelob').click(function() {
    $('#errors').hide();
    $.post($('#fileValue').val())
    //$.post({
    //    url: "",
    //    contentType: "application/json",
    //    data: JSON.stringify({filename: })
    //});

    return false;
});