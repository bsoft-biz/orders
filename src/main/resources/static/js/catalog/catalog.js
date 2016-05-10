angular.module('catalog', ['data','me-lazyload'])
    .controller('catalog',['data','$scope', function(data,$scope) {
        $scope.items=data.getItems();
        $scope.groups=data.getGroups();
    }]);
