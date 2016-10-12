angular.module('home', [])
    .controller('home',['$http','$scope', 'data','$translate', function($http,$scope,data,$translate) {
        data.setTitle("TITLE_HOME");
        userSettings = {};
        $scope.userPassword = {old: "", new1:"", new2:""};
        var self = this;
	$http.get('users/userSettings/').then(function(response) {
		self.userSettings = response.data;
	});
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
                {
                    $translate('MSG_ERR_PASMATCH').then(function (message) {
                        $scope.editableForm.$setError('new2', message);
                    }, function (translationId) {
                        $scope.editableForm.$setError('new2', translationId);
                    });
                    return "error";
                }
			};
			$scope.updateSettings = function(){
			return $http.post('users/userSettings/',self.userSettings)
		};
}]);
