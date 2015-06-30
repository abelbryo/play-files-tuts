angular.module("MainApp", [])


.controller("MainController", function($scope, $http) {
    console.log("--- MainController Loadded ---");
    $scope.title = "Hello Angular";

    $scope.form = {
        data: {}
    };

    $scope.filesChanged = function(elem){
        $scope.files = elem.files;
        $scope.$apply();
    };

    $scope.submitForm = function(form) {

        var data = angular.copy($scope.form.data);

        var fd = new FormData();
        fd.append("username", data.username);
        angular.forEach($scope.files, function(file){
            fd.append('file', file );
        });

        $http.post('/save', fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).success(function(response) {
            console.log(response);
        }).error(function(err) {
            console.log(err);
        });

        $scope.form.data = '';
        form.$setPristine();
    };


})

.directive('fileInput', function($parse){
    return {
        link: function(scope, elem, attrs ){
           elem.bind("change", function(){
               $parse(attrs.fileInput).assign(scope, elem[0].files);
               scope.$apply();
            })
        }
    };
})




