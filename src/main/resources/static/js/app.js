angular
		.module('OrdersApp', [ 'ngRoute', 'ngResource', 'me-lazyload', "xeditable", 'pascalprecht.translate'])
		.config(

				function($routeProvider, $httpProvider, $locationProvider, $translateProvider) {

					$translateProvider
						.useSanitizeValueStrategy('escape')
						.useStaticFilesLoader({
						prefix: 'locale/',
						suffix: '.json'
						})
						.registerAvailableLanguageKeys(['ru', 'en'], {
							'ru_*': 'ru',
							'en_*': 'en'
						})
						// .preferredLanguage('ru');
						// try to find out preferred language by yourself
						.determinePreferredLanguage()
						.fallbackLanguage('en');

					//$locationProvider.html5Mode(true);

					$routeProvider.when('/', {
						templateUrl : 'templates/home.html',
						controller : 'home',
						controllerAs : 'controller',
                        resolve: {userSettingsPrep: userSettingsPrep}
					}).when('/order', {
						templateUrl : 'templates/order.html',
						controller : 'order',
						controllerAs : 'controller',
                        resolve: {userSettingsPrep: userSettingsPrep}
					}).when('/order/:posId/:date/:groupId', {
						templateUrl : 'templates/order.html',
						controller : 'order',
						controllerAs : 'controller',
						resolve: {userSettingsPrep: userSettingsPrep}
					}).when('/catalog', {
						templateUrl : 'templates/catalog.html',
						controller : 'catalog',
						controllerAs : 'controller'
					}).when('/catalog/:itemId', {
						templateUrl : 'templates/catalog-detail.html',
						controller : 'catalog-detail',
						controllerAs : 'controller'
					}).when('/login', {
						templateUrl : 'templates/login.html',
						controller : 'navigation',
						controllerAs : 'controller'
					}).when('/passwdreset', {
						templateUrl : 'templates/passwdreset.html',
						controller : 'passwdreset',
						controllerAs : 'controller'
					}).when('/registration', {
						templateUrl : 'templates/registration.html',
						controller : 'registration',
						controllerAs : 'controller'
					}).otherwise('/');
					
					$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

				}).run(function(auth) {

			// Initialize auth module with the home page and login/logout path respectively
			auth.init('/', '/login', '/logout');

		})
	.run(function(editableOptions) {
		editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
	});

function userSettingsPrep(data){
    return data.getUserSettingsPromise();
}