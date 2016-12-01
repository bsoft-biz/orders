angular.module('OrdersApp').controller('registration',registration);

registration.$inject=['data'];

function registration(data) {
    data.setTitle("TITLE_REGISTRATION");
    var self=this;
    self.register = function(){
        console.log(self.user);
    }
}