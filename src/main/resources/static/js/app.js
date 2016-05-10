angular
		.module('orderApp', [ 'ngRoute', 'auth', 'home', 'order', 'navigation', 'catalog', 'catalog-detail',
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
					}).when('/catalog', {
						templateUrl : 'js/catalog/catalog.html',
						controller : 'catalog',
						controllerAs : 'controller'
					}).when('/catalog/:itemId', {
						templateUrl : 'js/catalog-detail/catalog-detail.html',
						controller : 'catalog-detail',
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
