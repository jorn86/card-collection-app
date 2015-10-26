angular.module('card-app')
    .controller('UserController', function ($scope, $http, $rootScope) {
        $scope.$on('event:google-plus-signin-success', function (event, authResult) {
            $http.get('https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=' + authResult.access_token).then(function(result) {
                var user = result.data;
                $scope.userInfo = {
                    name: user.name,
                    email: user.email,
                    image: user.picture
                };

                console.log(authResult, user)
                $scope.datamodel.authenticateUser({
                    name: user.name,
                    email: user.email,
                    id: user.id,
                    type: 'google'
                });
                $rootScope.$broadcast('user', result.data);
            });
        });

        $scope.$on('event:google-plus-signin-failure', function (event, authResult) {
            console.log('sign in failed', event, authResult)
            $rootScope.$broadcast('user', null);
        });
    });