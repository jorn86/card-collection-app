angular.module('card-app')
    .directive('newButtons', function($rootScope, $state) {
        return {
            restrict: 'E',
            template: '<button ng-click="createTag()">Tag</button><button ng-click="createDeck()">Deck</button>',
            scope: {},
            link: function(scope) {
                scope.createDeck = function() {
                    $rootScope.datamodel.createDeck('New deck').then(function(result) {
                        $rootScope.$broadcast('reloadUserDecks');
                        $state.go('app.deck', {id: result.data.id});
                    });
                };
                scope.createTag = function() {
                    $rootScope.datamodel.createTag('New tag').then(function() {
                        $rootScope.$broadcast('reloadUserDecks');
                    });
                };
            }
        };
    });
