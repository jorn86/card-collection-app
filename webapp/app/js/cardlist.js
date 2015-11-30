angular.module('card-app')

    .controller('CardListController', function ($scope) {
        $scope.mapSetCodeToImage = function (setcode) {
        };

        var types = ['Creature', 'Instant', 'Sorcery', 'Artifact', 'Enchantment', 'Planeswalker', 'Land', 'Other'];
        var typeGrouping = function (card) {
            if (card.type) {
                for (var i = 0; i < types.length; i++) {
                    if (card.type.indexOf(types[i]) >= 0) {
                        return types[i];
                    }
                }
            }
            return 'Other';
        };
        var typeOrder = function (card) {
            return _.indexOf(types, card.$key);
        };

        var colors = {
            'W': 'White',
            'U': 'Blue',
            'B': 'Black',
            'R': 'Red',
            'G': 'Green',
            'C': 'Colorless',
            'M': 'Multicolored'
        };
        var colorGrouping = function (card) {
            return colors[color(card.mana)];
        };
        var colorOrder = function (group) {
            return _.indexOf(_.keys(colors), color(group.$key));
        };
        var color = function (mana) {
            if (!mana) return 'C';

            var color = null;
            for (var symbol in colors) {
                if (mana.indexOf(symbol) >= 0) {
                    if (color) {
                        return 'M'
                    }
                    color = symbol;
                }
            }
            return color || 'C';
        };

        $scope.grid = {
            rows: [],

            groupingOptions: [{
                name: 'Type', group: typeGrouping, order: typeOrder
            }, {
                name: 'Color', group: colorGrouping, order: colorOrder
            }, {
                name: 'None', group: function () {
                    return 'All'
                }, order: function () {
                    return 0
                }
            }],
            groupBy: typeGrouping,
            orderBy: typeOrder,

            sortingOptions: [
                {name: 'Count', field: 'amount'},
                {name: 'Name', field: 'name'},
                {name: 'Type', field: 'type'},
                {name: 'Set', field: 'setcode'},
                {name: 'Converted Mana Cost', field: 'cmc'}
            ],
            sortDescending: false,

            allSelected: false
        };

        $scope.grid.currentGrouping = $scope.grid.groupingOptions[0];
        $scope.onGroupingChange = function () {
            $scope.grid.orderBy = $scope.grid.currentGrouping.order;
            $scope.grid.groupBy = $scope.grid.currentGrouping.group;
        };

        $scope.grid.currentSorting = $scope.grid.sortingOptions[4];

        $scope.updateAmount = function (data) {
            $scope.datamodel.updateAmount($scope.deck.id, data.rowId, data.amount);
        };

        $scope.updateGrid = function (cards) {
            $scope.grid.rows = cards;
        };

        $scope.onSelectAll = function () {
            for (var i = 0; i < $scope.grid.rows.length; i++) {
                $scope.grid.rows[i].selected = $scope.grid.allSelected;
            }
        };

        $scope.getSelected = function () {
            var selected = [];
            for (var i = 0; i < $scope.grid.rows.length; i++) {
                if ($scope.grid.rows[i].selected) {
                    selected.push($scope.grid.rows[i]);
                }
            }
            return selected;
        };

        $scope.cardSearchValue = '';
        var searchResult = null;
        $scope.searchCallback = function (query) {
            return $scope.datamodel.searchCardsByName(query.query).then(function (result) {
                return result.data;
            });
        };
        $scope.searchSelectCallback = function (result) {
            searchResult = result.item;
            return result.item.name;
        };
        $scope.submitAdd = function () {
            console.log("add", searchResult)
            if (!searchResult) {
                return;
            }

            $scope.datamodel.addCardToDeck($scope.deck.id, searchResult.id, 1).then(function (result) {
                console.log("Added card", result)
                return $scope.getDeck($scope.deck.id);
            }).then($scope.setDeck);

            searchResult = null;
        };

        var currentUserId;
        $scope.$on('user', function (event, user) {
            currentUserId = user ? user.id : null;
        });

        $scope.setDeck = function (deck) {
            $scope.deck = deck;
            $scope.editable = deck.userid === currentUserId;
            $scope.updateGrid(deck.cards);
        };

        $scope.totalAmount = function (group) {
            return _.map(group, function(value) { return value.amount; }).reduce(function(a,b) { return a+b;}, 0);
        };
    })

    .directive('linkcard', function() {
        return {
            link: function($scope, element) {
                $scope.$watch(function() {
                    return element.children().length;

                }, function() {
                    $scope.$evalAsync(function() {
                        inlinemtg.linkcards($(element));
                    });
                });
            }
        };
    });
