angular.module('controllers', [])
    .controller('IndexController', function ($scope, $http) {
        $scope.datamodel = new Datamodel($http);
    })
    .controller('ListController', function($scope) {
        $scope.datamodel.getDecks().then(function(result) {
            $scope.decks = result.data;
        });
    })
    .controller('DeckController', function($scope, $stateParams) {
        $scope.datamodel.getDeck($stateParams.id).then(function(result) {
            $scope.deck = result.data;
        });
    });
