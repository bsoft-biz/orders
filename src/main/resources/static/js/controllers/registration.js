angular.module('OrdersApp').controller('registration',registration);

registration.$inject=['data'];

function registration(data) {
    data.setTitle("TITLE_REGISTRATION");
}