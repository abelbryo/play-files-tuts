angular.module("MainApp", ['ngFileUpload'])


.controller("MainController", function($scope, $http) {

})

.controller("MySecondCtrl", function($scope, Upload){

    $scope.list = [];


    $scope.upload = function(files){
        if(files && files.length){
            angular.forEach(files, function(file){
                Upload.upload({
                    url: '/save',
                    fields: {'username': $scope.username},
                    file: file
                }).progress(function(evt){
                    var progressPct = parseInt(100 * evt.loaded / evt.total);
                    $scope.width = progressPct;
                    $scope.progressFileName = evt.config.file.name;
                    console.log('progress ' + progressPct + "% " + evt.config.file.name);
                }).success(function(data, status, headers, config){
                    console.log("file " + config.file.name + " uploaded. reponse: " , data);
                    $scope.list.push(config.file.name);
                });
            });
        }
    };

})

.directive('fileInput', function($parse){
    return {
        link: function(scope, elem, attrs ){
           elem.bind("change", function(){
               $parse(attrs.fileInput).assign(scope, elem[0].files);
               scope.$apply();
            });
        }
    };
});




