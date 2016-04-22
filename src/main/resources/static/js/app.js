angular
		.module('hello', [ 'ngRoute', 'auth', 'home', 'order', 'navigation',
		"xeditable" ])
		.config(

				function($routeProvider, $httpProvider, $locationProvider) {

					//$locationProvider.html5Mode(true);

					$routeProvider.when('/', {
						templateUrl : 'js/home/home.html',
						controller : 'home',
						controllerAs : 'controller'
					}).when('/order', {
						templateUrl : 'js/order/order.html',
						controller : 'order',
						controllerAs : 'controller'
					}).when('/login', {
						templateUrl : 'js/navigation/login.html',
						controller : 'navigation',
						controllerAs : 'controller'
					}).otherwise('/');

					$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

				}).run(function(auth) {

			// Initialize auth module with the home page and login/logout path
			// respectively
			auth.init('/', '/login', '/logout');

		})
	.run(function(editableOptions) {
		editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
	});
