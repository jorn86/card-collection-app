angular.module('card-app')
    .controller('UserController', function ($scope, $http) {
        $scope.$on('event:google-plus-signin-success', function (event, authResult) {
            $http.get('https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=' + authResult.access_token).then(function(result) {
                $scope.logout = authResult.logout;
                $scope.userInfo = {
                    name: result.data.name,
                    email: result.data.email,
                    image: result.data.picture
                };

                $scope.datamodel.authenticateUser({
                    name: result.data.name,
                    email: result.data.email,
                    token: authResult.id_token,
                    type: 'google'
                });
            });
        });
        $scope.$on('event:google-plus-signin-failure', function (event, authResult) {
            console.log('sign in failed', event, authResult)
        });
    });