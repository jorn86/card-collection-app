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
            }, 1000);
        });
    })

    .controller('DeckController', function ($scope, $stateParams) {
        $scope.deckId = $stateParams.id;

        $scope.reload = function() {
            $scope.datamodel.getDeck($scope.deckId).then(function (result) {
                $scope.deck = result.data;
                $scope.deck = result.data;
                $scope.editable = $scope.deck.userid === $scope.currentUserId;
            }, function (error) {
                console.log(error);
                $scope.deck = null;
            });
        };

        $scope.uploadStatus = 'Import';
        var fileUploadControl = document.getElementById('fileupload');
        $scope.uploadDeckboxImport = function() {
            if (fileUploadControl.files) {
                $scope.uploadStatus = 'Working...';
                $scope.datamodel.uploadDeckboxImport($scope.deckId, fileUploadControl.files[0]).then(function(result) {
                    $scope.warnings = result.data.length > 0 ? result.data : null;
                    $scope.uploadStatus = 'Succeeded' + ($scope.warnings ? ' with warnings' : '');
                    $scope.reload();
                });
            }
        };

        $scope.inventory = $scope.user && $scope.deckId === $scope.user.inventoryid;
        $scope.$on('reloadDeck', $scope.reload);
        $scope.$on('user', function(event, user) {
            $scope.inventory = $scope.deckId === user.inventoryid;
            $scope.reload();
        });

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
        $scope.query = $scope.originalQuery;

        $scope.datamodel.getSearchResults($scope.query);
        $scope.datamodel.getDeck('3376d0d9-490e-4c99-b269-7bf0e48887b7').then(function(result) {
            $scope.results = result.data.boards[1].cards;
            $scope.update();
        });

        $scope.update = function(page) {
            if (page) {
                $scope.page = page;
            }
            var end = $scope.page * 10;
            $scope.pageresults = $scope.results ? $scope.results.slice(end - 10, end - 1) : null;
        };

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
    })

    .controller('EditBoardController', function($scope, $rootScope) {
        var id = $scope.ngDialogData.id;
        $scope.fields = { name: '', targetBoard: null };
        $rootScope.datamodel.getBoard(id).then(function(result) {
            $scope.fields.name = result.data.name;
        });
        $rootScope.datamodel.getOtherBoards(id).then(function(result) {
            $scope.boards = result.data;
            if ($scope.boards.length > 0) {
                $scope.fields.targetBoard = $scope.boards[0].id;
            }
        });
        $scope.submitName = function() {
            $scope.datamodel.updateBoard(id, {name: $scope.fields.name}).then($scope.closeThisDialog);
        };
        $scope.submitDelete = function() {
            $scope.datamodel.deleteBoard(id).then($scope.closeThisDialog);
        };
        $scope.submitMerge = function() {
            $scope.datamodel.mergeBoard(id, $scope.fields.targetBoard).then($scope.closeThisDialog);
        };
    })

    .controller('FormatController', function($scope, $rootScope) {
        $scope.formats = {};
        _.forEach(['Commander', 'Legacy', 'Modern', 'Standard', 'Vintage'], function(f) {
            $rootScope.datamodel.getFormat(f).then(function(result) {
                $scope.formats[f] = result.data;
            });
        })
    });
