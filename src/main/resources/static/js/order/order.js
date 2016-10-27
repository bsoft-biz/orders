angular.module('order', ['ngResource','data']).controller('order',order);

order.$inject = ['$resource', '$scope', '$q', '$http', '$filter', '$routeParams', '$location', 'data', 'userSettingsPrep'];

function order($resource, $scope, $q, $http,  $filter, $routeParams, $location, data, userSettingsPrep) {
    $scope.fullOrderItems = {};
    $scope.orderStatus = {};

    console.log(userSettingsPrep);
    //$scope.userSettings={clientPOS:{}};
    $scope.userSettings=data.getUserSettings();
    //$scope.userSettings=userSettingsPrep.data;
    $scope.groups=data.getGroups();
    $scope.poses=data.getPoses();
    console.log($scope.userSettings);
    console.log($scope.userSettings.clientPOS.id);
    $scope.pos = $routeParams.posId==undefined?$scope.userSettings.clientPOS.id:Number($routeParams.posId);
    $scope.group = $routeParams.groupId==undefined?42:Number($routeParams.groupId);//= groups[0].id;
    if ($routeParams.date == undefined){
        $scope.date = new Date();
        $scope.date.setDate($scope.date.getDate()+1);
    }
    else
    {
        var dateString = $routeParams.date.match(/^(\d{2})\.(\d{2})\.(\d{4})$/);
        $scope.date = new Date( dateString[3], dateString[2]-1, dateString[1] );
    }
    $scope.items=data.getItems();
    $scope.itemsInfo=data.getItemsInfo();

    $scope.getItemName = function(idItem){
        var item = $filter('filter')($scope.items, {id: idItem});
        return (item.length) ? item[0].itemName : "Не известно";
    };

    $scope.confirmOrder = function(){
        var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
        $http.post("orders/confirmorder?date="+frmtDate+"&group_id="+$scope.group).then(
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

    $scope.getItemInfo = function(idItem){
        var itemsInfo = $filter('filter')($scope.itemsInfo, {itemId: idItem});
        return itemsInfo[0];
    };

    $scope.showGroup = function() {
        var selected = $filter('filter')($scope.groups, {id: $scope.group});
        return ($scope.group && selected.length) ? selected[0].groupName : '???';
    };

    $scope.showPos = function() {
        var selected = $filter('filter')($scope.poses, {id: $scope.pos});
        return ($scope.pos && selected.length) ? selected[0].posName : '???';
    };

    $scope.saveColumn = function(formName) {
        //console.log($scope.FullOrderItems);
        //here we can filter only not null rows or with id
        return $http.post($scope.url, $scope.fullOrderItems).then(
            function successCallback(response) {
                var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
                $scope.orderStatus = $resource("orders/orderstatus?date="+frmtDate+"&group_id="+$scope.group).get();
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
                    $translate("MSG_ERR_ORDER_SAVE", {message:err.message}).then(function (message) {
                        $scope[formName].$editables[0].setError(message);
                    }, function (translationId) {
                        $scope[formName].$editables[0].setError(translationId+err.message);
                    });
                }
                return "error";
            });
    };

    $scope.cancelColumn = function(formName) {
        if($scope[formName].$submitted){
            $scope.loadOrder();
            //we need to cancel borh forms because of full order reloading
            $scope.countform.$cancel();
            $scope.count2form.$cancel();
        }
        else
            $scope[formName].$cancel();
    }


    $scope.loadOrder = function() {
        var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
        $location.path('/order/'+$scope.pos+'/'+frmtDate+'/'+$scope.group);
        if (Array.isArray($scope.groups) && $scope.groups.length > 0 && Array.isArray($scope.poses) && $scope.poses.length > 0)
            data.setTitle("TITLE_ORDERS",{pos:$scope.showPos(), date:frmtDate, group:$scope.showGroup()});
        else
            data.setTitle("TITLE_ORDERS_SHORT",{date:frmtDate});
        $scope.url="orders/orderitems?date="+frmtDate+"&group_id="+$scope.group;
        var FullOrderItems = $resource($scope.url);
        $scope.fullOrderItems = FullOrderItems.query();
        $scope.orderStatus = $resource("orders/orderstatus?date="+frmtDate+"&group_id="+$scope.group).get();
    };

    $scope.loadOrder();
}