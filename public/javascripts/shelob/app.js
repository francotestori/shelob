angular.module('dropZone', [])
    .directive('dropZone', function () {
        return {
            restrict: 'A',
            scope: {
                upload: '@',
                done: '&'
            },
            template: '<div id="upload-dropzone" class="upload-box" > </div>',
            replace: true,
            link: function (scope, element) {
                var optionsObj = {
                    maxFilesize: 1024
                };

                if (scope.done) {
                    optionsObj.success = function (file, response) {
                        scope.$apply(function () {
                            scope.done({file: file, response: response});
                        });
                    };
                }

                scope.$watch('upload', function (oldValue, newValue) {
                    optionsObj.url = newValue;
                    element.dropzone(optionsObj);
                });
            }
        };
    })
    .controller('UploadCtrl', function ($scope) {
        $scope.uploadURL = "/rest/upload/filename";

        $scope.uploadFinished = function (file, response) {
            console.log('We just finished uploading');
            console.log('file: ' + file);
            console.log('response: ' + response);

        };
    })
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider.
            when('/upload', {templateUrl: '/assets/partials/upload.html'}).
            otherwise({redirectTo: '/upload'});
    }]);
