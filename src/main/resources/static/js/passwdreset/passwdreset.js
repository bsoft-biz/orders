angular.module('passwdreset', []).controller('passwdreset',passwdreset);

passwdreset.$inject=['data', '$routeParams', '$location', 'auth', '$translate'];

function passwdreset(data,$routeParams,$location,auth,$translate) {
    data.setTitle("HOME_LABEL_PASSWORD");
    var self = this;
    token=$routeParams.token;
    console.log(token);
    if (token===undefined) {
        $location.path(auth.homePath);
    }
    self.reset = function () {
        if(self.password!=self.password2){
            $translate('MSG_ERR_PASMATCH').then(function (message) {
                self.errorResetPasswordMessage = message;
            }, function (translationId) {
                self.errorResetPasswordMessage = translationId;
            });
            //self.errorResetPasswordMessage=data.transalte("MSG_ERR_PASMATCH");
            self.errorResetPassword=true;
            return;
        }
        else{
            self.errorResetPasswordMessage="";
            self.errorResetPassword=false;
        }
        if (token!==undefined){
            auth.passwdresetWithResetPasswordToken(token, self.password, null,
                function errorCallback(response) {
                    self.errorResetPasswordMessage=response.data.message;
                    self.errorResetPassword=true;
                });
            // console.log("id="+id);
            // console.log("token="+token);
        }
        //console.log(self.password);
    };
}