angular.module('OrdersApp').controller('verify',verify);

verify.$inject=['data','$http','$routeParams','$location','auth'];

function verify(data,$http,$routeParams,$location,auth) {
    data.setTitle("TITLE_VERIFY");
    var self=this;
    var token=$routeParams.token;
    if (token===undefined) {
        $location.path(auth.homePath);
    }
    else
    {
        $http.get("users/verify?token="+token).then(function(response) {
            console.log("verification succeeded");
            self.result = "Ok";
        }, function(response) {
            console.log("verification failed");
            self.result = "Sorry";
        });
    }
}
