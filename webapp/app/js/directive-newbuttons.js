angular.module('card-app')
    .directive('newButtons', function($rootScope, $state) {
        return {
            restrict: 'E',
            templateUrl: 'partials/newbuttons.html',
            scope: {},
            link: function(scope) {
                scope.createDeck = function() {
                    $rootScope.datamodel.createDeck('New deck').then(function(result) {
                        $rootScope.$broadcast('reloadUserDecks');
                        $state.go('app.deck', {id: result.data.id});
                    });
                }
            }
        };
    });