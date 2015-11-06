angular.module('card-app')

    .controller('IndexController', function ($scope, $http, $state) {
        $scope.datamodel = new Datamodel($http);
        $scope.$on('user', function(event, user) {
            $scope.user = user;
            if (!$state.includes('app')) {
                $state.go('app');
            }
        });
    })

    .controller('DeckListController', function ($scope) {
        $scope.$on('user', function(event, user) {
            if (user) {
                $scope.datamodel.getDecks().then(function (result) {
                    $scope.decks = result.data.decks;
                    $scope.tags = result.data.tags
                });
            }
        });

        $scope.selectedTag = null;
        $scope.tagFilter = function (deck) {
            return !$scope.selectedTag || (deck.tags.indexOf($scope.selectedTag) >= 0);
        };
    })

    .controller('DeckController', function ($scope, $stateParams) {
        $scope.datamodel.getDeck($stateParams.id).then(function (result) {
            $scope.setDeck(result.data);
        });
    })

    .controller('InventoryController', function ($scope) {
        $scope.datamodel.getInventory().then(function (result) {
            $scope.setDeck(result.data);
        });
    })

    .controller('VersionCheckController', function($scope) {
        $scope.datamodel.getAllSets().then(function(result) {
            $scope.sets = result.data;
        });
    });
