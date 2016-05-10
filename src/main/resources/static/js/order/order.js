angular.module('order', ['ngResource','data']).
	controller('order',['$resource', '$scope', '$q', '$http', '$filter', 'data', function($resource, $scope, $q, $http,  $filter, data) {
		$scope.date = new Date();
        $scope.date.setDate($scope.date.getDate()+1);

        $scope.groups=data.getGroups();
        //{};
        //var Groups = $resource('orders/item_groups');
        //$scope.groups = Groups.query();
        $scope.group = 43;//= groups[0].id;

        $scope.items=data.getItems();
        //{};
        //var Items = $resource('orders/items');
        //$scope.items = Items.query();

        $scope.fullOrderItems = {};

        $scope.loadOrder = function() {
            var frmtDate=$filter('date')($scope.date, 'dd.MM.yyyy');
            $scope.url="orders/fullorderitems?date="+frmtDate+"&group_id="+$scope.group;//client_pos_id=2&
            console.log($scope.url);
            //-----
            var FullOrderItems = $resource($scope.url);
            $scope.fullOrderItems = FullOrderItems.query();
        };
        $scope.loadOrder();

        $scope.getItemName = function(idItem){
            var item = $filter('filter')($scope.items, {id: idItem})
            return (item.length) ? item[0].itemName : "Не известно";
        }

        $scope.showGroup = function() {
            var selected = $filter('filter')($scope.groups, {id: $scope.group});
            return ($scope.group && selected.length) ? selected[0].groupName : 'Не задан';
        };

		$scope.saveColumn = function(column) {
            console.log("----------FullOrderItems--------");
			console.log($scope.FullOrderItems);
			var results = [];
			//тут сожно отфильтровать только ненулевые строки или с ид
			//angular.forEach($scope.order.orderItems, function(orderItem) {
			//	var action = {column: column, value: orderItem[column], itemId: orderItem.item.id};
			//	console.log(action);
			//	//results.push($http.post('orders/order?client_pos_id=2&date=2016-04-08', action));
			//})
			//return $q($http.post('orders/order?client_pos_id=2&date=2016-04-08', $scope.order));

			results.push($http.post($scope.url, $scope.fullOrderItems));
			return $q.all(results);
		};

		//$scope.order = {};
		//var Order = $resource('orders/order?client_pos_id=2&date=2016-04-08');
		//$scope.order = Order.get();
        //
		//$scope.saveColumn = function(column) {
		//	console.log($scope.order.orderItems);
		//	console.log("------------------");
		//	var results = [];
		//	angular.forEach($scope.order.orderItems, function(orderItem) {
		//		var action = {column: column, value: orderItem[column], itemId: orderItem.item.id};
		//		console.log(action);
		//		//results.push($http.post('orders/order?client_pos_id=2&date=2016-04-08', action));
		//	})
		//	//return $q($http.post('orders/order?client_pos_id=2&date=2016-04-08', $scope.order));
		//	results.push($http.post('orders/order?client_pos_id=2&date=2016-04-08', $scope.order));
		//	return $q.all(results);
		//};
}]);
