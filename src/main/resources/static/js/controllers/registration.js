angular.module('OrdersApp').controller('registration',registration);

registration.$inject=['data','$http'];

function registration(data,$http) {
    data.setTitle("TITLE_REGISTRATION");
    var self=this;
    self.register = function(){
        console.log(self.user);
        $http.post('users/register', self.user).then(function(response) {
            console.log("Register succeeded");
        }, function(response) {
            console.log("Register failed");
        });
    }
}