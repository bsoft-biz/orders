'use strict';
angular.module('myApp.view3', ['ngRoute'])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'js/home/home.html',
            controller: 'home',
            controllerAs: 'controller'
        });
    }])

    .controller('home', ['$http', function($http) {
        var self = this;
        this.user = {};
        $http.get('users/al').success(function(data){
            self.user = data;
        });
    }]);


//(function() {
// Declare js level module which depends on views, and components
/*    var App = angular.module('myApp.home', [
        'ngRoute'
    ]);

    App.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/home', {
            templateUrl: 'js/home/home.html',
            controller: 'home',
            controllerAs: 'controller'
        });
    }]);

    App.controller('home',['$http',function($http){
        var self = this;
        this.user = {};
        $http.get('users/al').success(function(data){
            self.user = data;
        });
    }]);*/
//})();