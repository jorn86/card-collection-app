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

    .controller('StatisticsController', function($scope, sets) {
        sets.sets.then(function (result) {
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
        $scope.model = {query: $scope.originalQuery};

        $scope.results = {};
        $scope.message = null;
        $scope.datamodel.getSearchResults($scope.originalQuery).then(function(result) {
            $scope.results.cards = result.data;
            $scope.update();
        }, function(err) {
            if (err.status === 400 || err.status === 503) {
                $scope.message = err.data;
            }
        });

        $scope.update = function(page) {
            if (page) {
                $scope.page = page;
            }
            var end = $scope.page * 10;
            $scope.pageresults = $scope.results.cards ? $scope.results.cards.slice(end - 10, end - 1) : null;
        };

        $scope.search = function() {
            $state.go('app.searchresults', {query: $scope.model.query});
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

    .directive('banlist', function($rootScope) {
        return {
            restrict: 'E',
            scope: {name: '@'},
            templateUrl: 'partials/banlist.html',
            link: function(scope) {
                scope.loading = true;
                $rootScope.datamodel.getFormat(scope.name).then(function(result) {
                    scope.loading = false;
                    scope.format = result.data;
                });
            }
        };
    })

    .controller('DeckHelperBasicTypeLandController', function($scope, $rootScope) {
        $scope.title = 'Basic land types';
        $scope.results = {};
        $rootScope.datamodel.getSearchResults('t:land (t:plains or t:island or t:swamp or t:mountain or t:forest)').then(function (results) {
            $scope.results.cards = results.data;
        });
    })
    .controller('DeckHelperAnyColorLandController', function($scope, $rootScope) {
        $scope.title = 'Any color land';
        $scope.results = {};
        $rootScope.datamodel.getSearchResults('t:land o:add o:mana (o:"any color" or o:"any one color")').then(function(results) {
            $scope.results.cards = results.data;
        });
    })
    .controller('DeckHelperManLandController', function($scope, $rootScope) {
        $scope.title = 'Man land';
        $scope.results = {};
        $rootScope.datamodel.getSearchResults('t:land o:"~ becomes a" o:creature').then(function(results) {
            $scope.results.cards = results.data;
        });
    })
    .controller('DeckHelperColorlessFixController', function($scope, $rootScope) {
        $scope.title = 'Colorless mana fixers';
        $scope.results = {};
        $rootScope.datamodel.getSearchResults('c=c o:add o:mana not:(t:land) (not:(ci=c) or o:"any color" or o:"any one color" or o:"choose a color") not:(t:plane) not:(t:conspiracy)').then(function(results) {
            $scope.results.cards = results.data;
        });
    })
    .controller('DeckHelperNonbasicSearchController', function($scope, $rootScope) {
        $scope.title = 'Nonbasic land search';
        $scope.results = {};
        $rootScope.datamodel.getSearchResults('o:you o:search (o:nonbasic or not:(o:basic)) not:(o:"nonland card") (o:"land card" or o:"plains card" or o:"island card" or o:"swamp card" or o:"mountain card" or o:"forest card") (o:battlefield or o:hand) not:("Strata Scythe")').then(function(results) {
            $scope.results.cards = results.data;
        });
    })
    .controller('DeckHelperSweepersController', function($scope, $rootScope) {
        $scope.types = ['Artifact', 'Creature', 'Enchantment', 'Instant', 'Planeswalker', 'Sorcery'];
        $scope.results = {};
        $scope.type = 'Sorcery';
        $scope.$watch('type', function(type) {
            $rootScope.datamodel.getSearchResults('t:' + type + ' (o:"destroy all" or o:"destroy each" or o:"exile all" or o:"exile each" or o:"deals X damage to each" or o:"deals X damage to all") not:(o:"exile all cards") not:(t:plane or t:phenomenon or t:scheme)').then(function(results) {
                $scope.results.cards = results.data;
            });
        })
    })
    .controller('DeckHelperUtilityLandController', function($scope, $rootScope) {
        $scope.colors = { w: false, u: false, b: false, r: false, g: false };
        $scope.results = {};

        var update = _.throttle(function() {
            var identity = '';
            for (var c in $scope.colors) {
                if ($scope.colors.hasOwnProperty(c) && $scope.colors[c]) {
                    identity += c;
                }
            }
            if (identity === '') {
                identity = 'c'
            }

            $rootScope.datamodel.getSearchResults('ci<=' + identity + ' t:land not:(o:"add {W}" or o:"add {U}" or o:"add {B}" or o:"add {R}" or o:"add {G}" or o:"mana of any color" or o:"mana of any one color" or o:search or t:plains or t:island or t:swamp or t:mountain or t:forest or t:urza)').then(function(results) {
                $scope.results.cards = results.data;
            });
        }, 500);

        $scope.$watch('colors.w', update);
        $scope.$watch('colors.u', update);
        $scope.$watch('colors.b', update);
        $scope.$watch('colors.r', update);
        $scope.$watch('colors.g', update);
    })
    .controller('DeckHelperCommanderController', function($scope, $rootScope) {
        $scope.colors = { w: true, u: true, b: true, r: true, g: true };

        $scope.query = '';

        var canbe = 'o:"~ can be your commander"';
        var update = function() {
            var cols = '';
            for (var c in $scope.colors) {
                if ($scope.colors.hasOwnProperty(c) && $scope.colors[c]) {
                    cols += c;
                }
            }
            $scope.results = {};
            var colorQuery = '';
            if (cols === '') {
                colorQuery = 'c=c'
            }
            else if (cols === 'wubrg') {
                colorQuery = 'ci=wubrg';
            }
            else {
                colorQuery = 'c<=' + cols + ' ci>=' + cols;
            }
            $scope.query = colorQuery + ' f:commander (t:legendary or ' + canbe + ') (t:creature or ' + canbe + ')';
            $rootScope.datamodel.getSearchResults($scope.query).then(function(results) {
                $scope.results.cards = results.data;
            }, function(err) {
                console.log(err);
            });
        };

        $scope.$watch('colors.w', update);
        $scope.$watch('colors.u', update);
        $scope.$watch('colors.b', update);
        $scope.$watch('colors.r', update);
        $scope.$watch('colors.g', update);
    });
