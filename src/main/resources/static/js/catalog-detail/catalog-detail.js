angular.module('catalog-detail', ['data'])
    .controller('catalog-detail',['data','$scope','$routeParams', function(data,$scope,$routeParams) {
        $scope.itemInfo = data.getItemInfo().get({itemId: $routeParams.itemId}, function(itemInfo) {
            //$scope.mainImageUrl = itemInfo.images[0];
            data.setTitle($scope.itemInfo.item.itemName);
        });
        data.setTitle("Каталог продукции");
    }]);