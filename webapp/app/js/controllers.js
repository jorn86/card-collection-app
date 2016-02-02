angular.module('card-app')

    .controller('IndexController', function ($rootScope, $scope, $http, $state, $stateParams) {
        $rootScope.datamodel = new Datamodel($http);

        var expandTo = function(node, id) {
            if (node.tagId === id
                    || _.any(node.decks, function(deck) { return deck.id === id})
                    || _.any(node.children, function(node) { return expandTo(node, id)})) {
                node.expand = true;
                return true;
            }
            return false;
        };

        $scope.reloadUserDecks = function(event, args) {
            $rootScope.datamodel.getDecks().then(function(result) {
                $scope.decksForUser = result.data;
                $scope.decksForUser.expand = true;

                if (args && (args.tag || args.deck)) {
                    expandTo(result.data, args.tag || args.deck);
                }
            });
        };

        $scope.$on('user', function(event, user) {
            $scope.user = user;
            $rootScope.currentUserId = user ? user.id : null;
            if (!$state.includes('app')) {
                $state.go('app');
            }

            $scope.reloadUserDecks(null, {deck: $stateParams.id});
        });
        $rootScope.$on('reloadUserDecks', $scope.reloadUserDecks);

        $rootScope.datamodel.getPreconstructedDecks().then(function(result) {
            $scope.preconstructedDecks = result.data;
            $scope.preconstructedDecks.expand = true;
        });
    })

    .controller('DeckController', function ($scope, $stateParams) {
        $scope.deckId = $stateParams.id;
        $scope.reload = function() {
            $scope.datamodel.getDeck($scope.deckId).then(function (result) {
                $scope.deck = result.data;
                $scope.editable = $scope.deck.userid === $scope.currentUserId;
            });
        };
        $scope.$on('user', $scope.reload);
        $scope.reload();
    })

    .controller('StatisticsController', function($scope) {
        $scope.datamodel.getSetStatistics().then(function (result) {
            $scope.data = result.data;
        });

        $scope.order = 'releasedate';
        $scope.reverse = true;

        $scope.setOrder = function (field) {
            if ($scope.order === field) {
                $scope.reverse = !$scope.reverse;
            }
            else {
                $scope.order = field;
                $scope.reverse = false;
            }
        };
    });
