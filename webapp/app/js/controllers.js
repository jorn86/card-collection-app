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
        $scope.c = {type: 'any'};
        $scope.ci = {c: true};

        $scope.$watch('ci.c', function(value) {
            if (value) {
                $scope.ci.w = false;
                $scope.ci.u = false;
                $scope.ci.b = false;
                $scope.ci.r = false;
                $scope.ci.g = false;
            }
        });
        var reset = function(value) { if (value) $scope.ci.c = false; };
        $scope.$watch('ci.w', reset);
        $scope.$watch('ci.u', reset);
        $scope.$watch('ci.b', reset);
        $scope.$watch('ci.r', reset);
        $scope.$watch('ci.g', reset);

        $scope.cost = '';
        $scope.costOptions = [
            '0', '1', '2', '3', '4', '5', '6', '7', '8',
            'w', 'u', 'b', 'r', 'g', 'c',
            'wp', 'up', 'bp', 'rp', 'gp',
            'wu', 'wb', 'ub', 'ur', 'br', 'bg', 'rg', 'rw', 'gw', 'gu',
            '2w', '2u', '2b', '2r', '2g'
        ];

        $scope.rarityOptions = ['Common', 'Uncommon', 'Rare', 'Mythic Rare', 'Special', 'Basic Land'];

        $scope.format = {value: 'All'};
        $scope.formatOptions = ['All', 'Vintage', 'Legacy', 'Extended', 'Modern', 'Standard', 'Commander', 'MTGO'];

        $scope.addManaCost = function(symbol) {
            $scope.cost = $scope.cost + (symbol.length == 1 ? symbol.toUpperCase() : '{' + symbol.toUpperCase() + '}');
        };

        $scope.doSearch = function() {
            console.log('search', $scope)
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
