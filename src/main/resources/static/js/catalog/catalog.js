angular.module('catalog', ['data','me-lazyload'])
    .controller('catalog',['data','$scope','$filter', function(data,$scope,$filter) {
        $scope.items=data.getItems();
        $scope.groups=data.getGroups();
        $scope.group = 42;//= groups[0].id;

        $scope.showGroup = function() {
            var selected = $filter('filter')($scope.groups, {id: $scope.group});
            return ($scope.group && selected.length) ? selected[0].groupName : 'Не задан';
        };
    }]);
