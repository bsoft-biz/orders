angular.module('auth', []).factory(
		'auth',

		function($rootScope, $http, $location) {

			enter = function() {
				if ($location.path() != auth.loginPath) {
					auth.path = $location.path();
					if (!auth.authenticated) {
						$location.path(auth.loginPath);
					}
				}					
			};

			var auth = {

				authenticated : false,

				loginPath : '/login',
				logoutPath : '/logout',
				homePath : '/',
				path : $location.path(),

				authenticate : function(credentials, callback) {

					var headers = credentials && credentials.username ? {
						authorization : "Basic "
								+ btoa(credentials.username + ":"
										+ credentials.password)
					} : {};

					$http.get('users/user', {
						headers : headers
					}).then(function(response) {
						if (response.data.name) {
							auth.authenticated = true;
						} else {
							auth.authenticated = false;
						}
						callback && callback(auth.authenticated);
						$location.path(auth.path==auth.loginPath ? auth.homePath : auth.path);
					}, function() {
						auth.authenticated = false;
						callback && callback(false);
					});

				},

				clear : function() {
					//clear url parameters
					$location.url(auth.loginPath);
					//$location.search('token', null)
					//$location.path(auth.loginPath);
					auth.authenticated = false;
					$http.post(auth.logoutPath, {}).then(function() {
						console.log("Logout succeeded");
					}, function() {
						console.log("Logout failed");
					});
				},

				init : function(homePath, loginPath, logoutPath) {

					auth.homePath = homePath;
					auth.loginPath = loginPath;
					auth.logoutPath = logoutPath;

					auth.authenticate({}, function(authenticated) {
						if (authenticated) {
							$location.path(auth.path);
						}
					});

					// Guard route changes and switch to login page if unauthenticated
					$rootScope.$on('$routeChangeStart', function() {
						enter();
					});

				},

				resetPassword : function(email,successCallback,errorCallback) {
					$http.post('users/resetPassword?email='+email, {}).then(function(response) {
						console.log("Reset password succeeded");
						successCallback && successCallback(response);
					}, function(response) {
						console.log("Reset password failed");
						errorCallback && errorCallback(response);
					});
				},

				authWithResetPasswordToken : function(id,token,successCallback,errorCallback) {
					$http.get('users/changePassword?id='+id+'&token='+token, {}).then(function(response) {
						console.log("authWithResetPasswordToken succeeded");
						auth.authenticate();
						//auth.authenticated = true;
						//$location.path(auth.homePath);
						//successCallback && successCallback(response);
					}, function(response) {
						console.log("authWithResetPasswordToken failed");
						errorCallback && errorCallback(response);
					});
				},

				passwdresetWithResetPasswordToken : function(token,password,successCallback,errorCallback) {
					$http.post('users/savePassword?token='+token+'&password='+password, {}).then(function(response) {
						console.log("passwdresetWithResetPasswordToken succeeded");
						auth.clear();
					}, function(response) {
						console.log("passwdresetWithResetPasswordToken failed");
						errorCallback && errorCallback(response);
					});
				}
			};

			return auth;

		});
