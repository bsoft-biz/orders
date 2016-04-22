'use strict';

angular.module('myApp.view3', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {
    templateUrl: 'js/view3/view3.html',
    controller: 'View3Ctrl',
    controllerAs: 'controller'
  });
}])

.controller('View3Ctrl', ['$http',function($http) {
      var self = this;
      this.user = {};
      $http.get('users/al').success(function(data){
        self.user = data;
      });
}]);