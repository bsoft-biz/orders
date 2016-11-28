angular.module('OrdersApp').controller('login', login);

login.$inject=['auth', 'data', '$routeParams'];

function login(auth, data, $routeParams)
{

	data.setTitle("TITLE_HOME");

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
