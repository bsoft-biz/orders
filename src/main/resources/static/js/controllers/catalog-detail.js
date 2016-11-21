angular.module('OrdersApp').controller('catalog-detail',catalogDetail);

catalogDetail.$inject=['data','$scope','$routeParams'];

function catalogDetail(data,$scope,$routeParams) {
    $scope.itemInfo = data.getItemInfo().get({itemId: $routeParams.itemId}, function(itemInfo) {
        //$scope.mainImageUrl = itemInfo.images[0];
        data.setTitle($scope.itemInfo.item.itemName);
    });
    data.setTitle("CATALOG_LABEL");
}