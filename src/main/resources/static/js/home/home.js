angular.module('home', [])
		.controller('home',['$http','$scope', function($http,$scope) {
	userSettings = {};
	var self = this;
	$http.get('/users/userSettings/').then(function(response) {
		self.userSettings = response.data;
	});

		$scope.updateSettings = function(){
			return $http.post('/users/userSettings/',self.userSettings)
		};
}]);
