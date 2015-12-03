angular.module('card-app')
    .directive('decks', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'partials/decks.html',
            link: function($scope, element, attributes) {
                $scope.showInventory = !!(attributes.userid);

                var promise = attributes.userid ? $rootScope.datamodel.getDecks(attributes.userid) : $rootScope.datamodel.getPreconstructedDecks();
                promise.then(function(result) {
                    $scope.node = result.data;
                });
            }
        }
    });
