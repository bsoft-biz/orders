angular.module('OrdersApp').controller('verify',verify);

verify.$inject=['data','$http','$routeParams'];

function verify(data,$http,$routeParams) {
    data.setTitle("TITLE_VERIFY");
    var token=$routeParams.token;
    if (token!==undefined)
    {
        $http.get("users/verify?token="+token).then(function(response) {
            console.log("verification succeeded");
            $scope.result = "Ok";
        }, function(response) {
            console.log("verification failed");
            $scope.result = "Sorry";
        });
    }
}
