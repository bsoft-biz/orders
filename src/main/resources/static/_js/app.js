'use strict';
(function() {
// Declare js level module which depends on views, and components
  var ordersApp = angular.module('myApp', [
    'ngRoute',
    'myApp.view1',
      'myApp.view2',
    'myApp.version',
      'myApp.view3'
  ]);

  ordersApp.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/login', {
      templateUrl: 'js/login/login.html',
      controller: 'login',
      controllerAs: 'controller'
    })
        .otherwise({redirectTo: '/'});

    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.defaults.useXDomain = true;
  }]);

  ordersApp.controller('login',['$rootScope', '$http', '$location', function($rootScope, $http, $location){

    var self = this;

    self.logout = function() {
      $http.post('logout', {}).finally(function () {
        $rootScope.authenticated = false;
        $location.path("/");
      });
    };

    var authenticate = function(credentials, callback) {

      var headers = credentials ? {authorization : "Basic "
      //+ credentials.username + ":" + credentials.password
      + btoa(credentials.username + ":" + credentials.password)
      } : {};
      //console.log('login headers = '+'Basic ' + btoa(credentials.username + ":" + credentials.password));
      //$http.defaults.headers.common.Authorization = 'Basic ' + btoa(credentials.username + ":" + credentials.password);

      //credentials&&alert(credentials.username);
      $http.get('users/auth', {headers : headers}
          //{headers: {'Authorization': 'Basic YWxleDoxMjM0NTY='}}
      ).success(function(data) {
        if (data.name) {
          $rootScope.authenticated = true;
        } else {
          $rootScope.authenticated = false;
        }
        callback && callback();
      }).error(function() {
        $rootScope.authenticated = false;
        callback && callback();
      });

    };

    authenticate();
    self.credentials = {};
    self.login = function() {
      //console.log('login headers = '+'Basic ' + btoa(self.credentials.username + ":" + self.credentials.password));
      authenticate(self.credentials, function() {
        if ($rootScope.authenticated) {
          $location.path("/");
          self.error = false;
        } else {
          $location.path("/login");
          self.error = true;
        }
      });
    };
  }]);

  ordersApp.controller('tabController',function(){
    this.tab=1;
    this.isSet=function(checkTab){
      return this.tab === checkTab;
    }
    this.setTab=function(activeTab){
      this.tab = activeTab;
    }
  });
})();