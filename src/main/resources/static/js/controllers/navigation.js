angular.module('OrdersApp').controller('navigation', navigation);

navigation.$inject=['$route', 'auth', 'data'];

function navigation($route, auth, data)
{

    var self = this;

    self.tab = function(route) {
        return $route.current && route === $route.current.controller;
    };

    self.authenticated = function() {
        return auth.authenticated;
    };

    self.logout = auth.clear;

    self.title = data.getTitle;

}