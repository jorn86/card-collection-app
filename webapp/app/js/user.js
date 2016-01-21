angular.module('card-app')
    .controller('UserController', function ($scope, $http, $rootScope, $state) {
        $scope.$on('event:google-plus-signin-success', function (event, authResult) {
            $http.get('https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=' + authResult.access_token).then(function(result) {
                var user = result.data;
                $scope.userInfo = {
                    name: user.name,
                    email: user.email,
                    image: user.picture
                };

                $scope.datamodel.authenticateUser({
                    name: user.name,
                    email: user.email,
                    authenticationOptions: [{ id: user.id, type: 'google' }]

                }).then(function(user) {
                    $rootScope.$broadcast('user', user);
                });
            });
        });

        $scope.$on('event:google-plus-signin-failure', function (event, authResult) {
            console.log('sign in failed', event, authResult)
            $rootScope.$broadcast('user', null);
        });

        $scope.goToSearch = function() {
            $state.go('app.search');
        };
    });