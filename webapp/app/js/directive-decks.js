angular.module('card-app')
    .directive('decks', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'partials/decks.html',
            scope: {userid: '=', preconstructed: '='},
            controller: function($scope) {
                $scope.$watch('userid', function(value) {
                    if (!$scope.preconstructed && !value) {
                        $scope.node = null;
                        return;
                    }

                    var promise = $scope.preconstructed
                        ? $rootScope.datamodel.getPreconstructedDecks()
                        : $rootScope.datamodel.getDecks($scope.userid);
                    promise.then(function(result) {
                        $scope.node = result.data;
                    });
                });
            }
        };
    });
