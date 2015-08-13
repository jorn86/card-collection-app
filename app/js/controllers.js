angular.module('card-app')

    .controller('IndexController', function ($scope, $http, $state) {
        $scope.datamodel = new Datamodel($http);
        $state.go('app');
    })

    .controller('DeckListController', function ($scope) {
        $scope.datamodel.getDecks().then(function (result) {
            $scope.decks = result.data.decks;
            $scope.tags = result.data.tags
        });

        $scope.selectedTag = null;
        $scope.tagFilter = function (deck) {
            return !$scope.selectedTag || (deck.tags.indexOf($scope.selectedTag) >= 0);
        };
    })

    .controller('DeckController', function ($scope, $stateParams) {
        $scope.datamodel.getDeck($stateParams.id).then(function (result) {
            $scope.deck = result.data;
            $scope.updateGrid(result.data.cards);
        });

    })
    .controller('InventoryController', function ($scope) {
        $scope.datamodel.getInventory().then(function (result) {
            $scope.deck = { name: 'Inventory' };
            $scope.updateGrid(result.data.cards);
        })
    });
