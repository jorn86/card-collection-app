angular.module('card-app')

    .controller('IndexController', function ($rootScope, $scope, $http, $state, $stateParams, $timeout) {
        $rootScope.datamodel = new Datamodel($http);

        var expandTo = function(node, tagId, deckId) {
            if (node.tagId === tagId
                    || _.any(node.decks, function(deck) { return deck.id === deckId})
                    || _.any(node.children, function(node) { return expandTo(node, tagId, deckId)})) {
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
                    expandTo($scope.decksForUser, args.tag, args.deck);
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
            // wait for $stateParams to realize there is one
            $timeout(function() {
                expandTo($scope.preconstructedDecks, null, $stateParams.id);
            });
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
    })

    .controller('SearchResultsController', function($scope, $stateParams, $state) {
        $scope.originalQuery = $stateParams.query;
        $scope.query = $stateParams.query;

        // $scope.datamodel.getSearchResults($stateParams.query);
        $scope.datamodel.getDeck('c61cb204-3bc6-49c9-9410-df0e850daab1').then(function(result) {
            $scope.results = result.data.boards[0].cards;
            $scope.pages = [];
            var pages = $scope.results.length / 10;
            for (var i = 0; i < pages; i++) {
                $scope.pages.push(i+1);
            }
            var end = $stateParams.page * 10;
            $scope.pageresults = $scope.results.slice(end - 10, end - 1);
        });

        $scope.search = function() {
            $state.go('app.searchresults', {query: $scope.query, page: 1});
        };
    })

    .controller('NewDeckController', function($scope, $rootScope, $state) {
        $scope.tags = [];
        $scope.input = {
            tagId: null,
            name: ''
        };
        var addTags = function(node) {
            $scope.tags.push({id: node.tagId, name: node.tagName});
            _.forEach(node.children, addTags);
        };
        $rootScope.datamodel.getDecks().then(function(result) {
            _.forEach(result.data.children, addTags);
        });

        $scope.createTag = function() {
            $rootScope.datamodel.createTag($scope.input.name, $scope.input.tagId).then(function(result) {
                $rootScope.$broadcast('reloadUserDecks', {tag: result.data.id});
            });
            $scope.closeThisDialog();
        };
        $scope.createDeck = function() {
            $rootScope.datamodel.createDeck($scope.input.name, $scope.input.tagId).then(function(result) {
                $rootScope.$broadcast('reloadUserDecks', {deck: result.data.id});
                $state.go('app.deck', {id: result.data.id});
            });
            $scope.closeThisDialog();
        };
    });
