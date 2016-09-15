angular.module('passwdreset', [])
    .controller('passwdreset',['data','$routeParams','$location', 'auth', function(data,$routeParams,$location,auth) {
        data.setTitle("Сброс пароля");
        var self = this;
        token=$routeParams.token;
        console.log(token);
        if (token==undefined) {
            $location.path(auth.homePath);
        }
        self.reset = function () {
            if(self.password!=self.password2){
                self.errorResetPasswordMessage="Пароли не совпадают!";
                self.errorResetPassword=true;
                return;
            }
            else{
                self.errorResetPasswordMessage="";
                self.errorResetPassword=false;
            }
            if (token!=undefined){
                auth.passwdresetWithResetPasswordToken(token, self.password, null,
                    function errorCallback(response) {
                        self.errorResetPasswordMessage=response.data.message;
                        self.errorResetPassword=true;
                    })
                // console.log("id="+id);
                // console.log("token="+token);
            }
            //console.log(self.password);
        }
    }]);