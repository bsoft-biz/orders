angular.module('OrdersApp').controller('navigation', navigation);

navigation.$inject=['$route', 'auth', 'data', '$routeParams'];

function navigation($route, auth, data, $routeParams)
{

	var self = this;

	id=$routeParams.id;
	token=$routeParams.token;
	if (!(id===undefined||token===undefined)){
		auth.authWithResetPasswordToken(id, token, null,
			function errorCallback(response) {
				self.okResetPassword=false;
				self.errorResetPasswordMessage=response.data.message;
				self.errorResetPassword=true;
			});
		// console.log("id="+id);
		// console.log("token="+token);
	}

	self.credentials = {};

	self.email = "";

	self.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};

	self.authenticated = function() {
		return auth.authenticated;
	};

	self.login = function() {
		auth.authenticate(self.credentials, function(authenticated) {
			if (authenticated) {
				console.log("Login succeeded");
				self.error = false;
			} else {
				console.log("Login failed");
				self.error = true;
			}
		});
	};

	self.logout = auth.clear;

	self.title = data.getTitle;

	data.setTitle("TITLE_HOME");

	self.resetPassword = function() {
		//auth.resetPassword(self.email);
		auth.resetPassword(self.email,
			function successCallback(response) {
				self.errorResetPassword=false;
				self.okResetPassword=true;
			}, function errorCallback(response) {
				self.okResetPassword=false;
				self.errorResetPasswordMessage=response.data.message;
				self.errorResetPassword=true;
			});
	};
}
