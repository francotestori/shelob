/**
 * Created by lucas on 10/09/15.
 */
$(function() {

    ajaxCall();

});

var ajaxCall = function() {
    var ajaxCallBack = {
        success : onSuccess,
        error : onError
    }

    jsRoutes.controllers.Application.ajaxCall().ajax(ajaxCallBack);
};

var  onSuccess = function(data) {
    alert(data);
}

var onError = function(error) {
    alert(error);
}