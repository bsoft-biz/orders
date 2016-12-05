angular.module('OrdersApp').controller('home',home);

home.$inject=['$http', '$scope', 'data', '$translate', 'userSettingsPrep', '$filter'];

function home($http,$scope,data,$translate,userSettingsPrep,$filter) {
    data.setTitle("TITLE_HOME");
    $scope.userSettings = data.getUserSettings();
    $scope.poses = data.getPoses();
    $scope.pos = $scope.userSettings.clientPOS;
    if ($scope.posId===undefined)
        $scope.posId = $scope.pos.id;
    $scope.userPassword = {old: "", new1:"", new2:""};
    var self = this;

    function getSelectedPos(){
        return $filter('filter')($scope.poses, {id: $scope.posId})[0];
    };
    $scope.showPos = function() {
        var selectedPos = getSelectedPos();
        console.log(selectedPos);
        return (selectedPos===undefined) ? '???' : selectedPos.posName;
    };
    $scope.loadPos = function() {
        $scope.pos = getSelectedPos();
    };
    $scope.savePos = function(){
        return $http.post('users/pos/',$scope.pos);
    };
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
        // TODO update first and last name and default values - Pos, product group
        return $http.post('users/userSettings/',self.userSettings);
    };
}
