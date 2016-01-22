angular.module('card-app')

    .controller('IndexController', function ($rootScope, $scope, $http, $state) {
        $rootScope.datamodel = new Datamodel($http);

        $scope.reloadUserDecks = function() {
            $rootScope.datamodel.getDecks().then(function(result) {
                $scope.decksForUser = result.data;
                $scope.decksForUser.expand = true;
            });
        };

        $scope.$on('user', function(event, user) {
            $scope.user = user;
            $rootScope.currentUserId = user ? user.id : null;
            if (!$state.includes('app')) {
                $state.go('app');
            }

            $scope.reloadUserDecks();
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

    .controller('SearchController', function($scope) {
        $scope.name = '';
        $scope.text = '';
        $scope.types = '';
        $scope.pow = null;
        $scope.tou = null;
        $scope.c = {};
        $scope.ci = {};

        $scope.doSearch = function() {
            console.log('search', $scope.name, $scope.text, $scope.types, $scope.pow, $scope.tou, $scope.c, $scope.ci)
        };
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
