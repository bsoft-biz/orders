angular.module('order', ['ngResource','data']).
controller('order',['$resource', '$scope', '$q', '$http', '$filter', 'data', function($resource, $scope, $q, $http,  $filter, data) {
    $scope.date = new Date();
    $scope.date.setDate($scope.date.getDate()+1);
    $scope.groups=data.getGroups();
    $scope.group = 43;//= groups[0].id;
    $scope.items=data.getItems();
    $scope.fullOrderItems = {};
    $scope.orderStatus = {};

    $scope.loadOrder = function() {
        var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
        $scope.url="orders/orderitems?date="+frmtDate+"&group_id="+$scope.group;
        var FullOrderItems = $resource($scope.url);
        $scope.fullOrderItems = FullOrderItems.query();
        $scope.orderStatus = $resource("orders/orderstatus?date="+frmtDate).get();
    };
    $scope.loadOrder();

    $scope.getItemName = function(idItem){
        var item = $filter('filter')($scope.items, {id: idItem});
        return (item.length) ? item[0].itemName : "Не известно";
    };

    $scope.confirmOrder = function(){
        var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
        $http.post("orders/confirmorder?date="+frmtDate).then(
            function successCallback(response) {
                $scope.orderStatus = response.data;
            }, function errorCallback(err) {
                // unknown error
                alert(err.data.message);
        });
    };

    $scope.getOrderItem = function(idItem){
        var orderItems = $filter('filter')($scope.fullOrderItems, {item: {id: idItem}});
        if (orderItems.length === 0){
            //console.log("create empty orderItem");
            orderItem = {item: {id: idItem}};
            $scope.fullOrderItems.push(orderItem);
        }
        else
            orderItem = orderItems[0];
        return orderItem;
    };

    $scope.showGroup = function() {
        var selected = $filter('filter')($scope.groups, {id: $scope.group});
        return ($scope.group && selected.length) ? selected[0].groupName : 'Не задан';
    };

    $scope.saveColumn = function(formName) {
        //console.log($scope.FullOrderItems);
        //here we can filter only not null rows or with id
        return $http.post($scope.url, $scope.fullOrderItems).then(
            function successCallback(response) {
                var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
                $scope.orderStatus = $resource("orders/orderstatus?date="+frmtDate).get();
            }, function errorCallback(response) {
                var err=response.data;
                errCount = err.length;
                if(Array.isArray(err) && errCount>0) {
                    // err like {id: "id", msg: "Server-side error for this production!"}
                    var formNumber ='';
                    if (formName === 'count2form')
                        formNumber = 2;
                    if (err[0].id===0){
                        $scope[formName].$editables[0].setError(err[0].msg);
                    }
                    for (var i=0; i < errCount; i++){
                        $scope[formName].$setError('id'+err[i].id+'count'+formNumber, err[i].msg);
                    }
                } else {
                    // unknown error
                    $scope[formName].$editables[0].setError('Ошибка сохранения заказа!');
                }
                return "error";
            });
    };
}]);
