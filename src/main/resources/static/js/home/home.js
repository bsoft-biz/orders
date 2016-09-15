angular.module('home', [])
    .controller('home',['$http','$scope', 'data', function($http,$scope,data) {
        data.setTitle("Заявка ТХК");
        userSettings = {};
        $scope.userPassword = {old: "", new1:"", new2:""};
        var self = this;
	$http.get('users/userSettings/').then(function(response) {
		self.userSettings = response.data;
	});
//'old='+$scope.userPassword.old+"&new="+$scope.userPassword.new2
			$scope.changePassword = function() {//$scope.userPassword
                console.log($scope.editableForm);
                //$http.post('/users/userPassword','old='+$scope.userPassword.old+"&new="+$scope.userPassword.new2)
                return $http({
                    method: 'POST',
                    url: 'users/userPassword',
                    data: 'old='+$scope.userPassword.old+"&new="+$scope.userPassword.new2,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).error(function(err) {
                    $scope.editableForm.$setError('new2', err.message);
                });
			};
			$scope.checkPassword = function(data) {
                console.log("checkPassword "+$scope.editableForm.new1.$viewValue+" "+data);
                if(data !== $scope.editableForm.new1.$viewValue)
                    return "Пароли должны совпадать";
			};
			$scope.updateSettings = function(){
			return $http.post('users/userSettings/',self.userSettings)
		};
}]);
