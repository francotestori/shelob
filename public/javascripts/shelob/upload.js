/**
 * Created by franco on 1/10/2015.
 */


$('#shelob').click(function() {
    $('#errors').hide();
    $.ajax({
        type : 'POST',
        url : "@routes.Application.apply()",
        data : $('#fileValue').text(),
        dataType : "text",
        success : function(data) {
            //setError('Call succedded');
            //$('#test1').attr("src", data)
        },
        error : function(data) {
            setError('Make call failed');
        }
    });
    return false;
});