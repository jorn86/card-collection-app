angular.module('card-app')

    .controller('CardListController', function ($scope) {
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
            return colors[color(card.cost)];
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

        var setGrouping = function(card) {
            return $scope.setNames[card.setcode];
        };
        var setOrder = function(group) {
            return _.indexOf(_.values($scope.setNames), group.$key);
        };

        $scope.grid = {
            rows: [],

            groupingOptions: [{
                name: 'Type', group: typeGrouping, order: typeOrder
            }, {
                name: 'Color', group: colorGrouping, order: colorOrder
            }, {
                name: 'Set', group: setGrouping, order: setOrder
            }, {
                name: 'None', group: function () {
                    return 'All';
                }, order: function () {
                    return 0;
                }
            }],
            groupBy: typeGrouping,
            orderBy: typeOrder,

            sortingOptions: [
                {name: 'Count', field: 'amount'},
                {name: 'Name', field: 'name'},
                {name: 'Type', field: 'fulltype'},
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
            $scope.datamodel.updateAmount($scope.deck.id, data.id, data.amount).then(function() {
                return $scope.datamodel.getDeck($scope.deck.id);

            }).then(function(result) {
                $scope.setDeck(result.data);
            });
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
            if (!searchResult) {
                return;
            }

            $scope.datamodel.addCardToDeck($scope.deck.id, searchResult.id, 1).then(function() {
                return $scope.datamodel.getDeck($scope.deck.id);

            }).then(function (result) {
                $scope.setDeck(result.data);
            });

            searchResult = null;
        };

        $scope.setDeck = function (deck) {
            $scope.deck = deck;
            $scope.editable = deck.userid === $scope.currentUserId;
            $scope.updateGrid(deck.cards);
        };

        $scope.requeryOnAuth = function(id) {
            $scope.$on('user', function() {
                $scope.datamodel.getDeck(id).then(function (result) {
                    $scope.setDeck(result.data);
                });
            });
        };

        $scope.totalAmount = function (group) {
            return _.map(group, function(value) { return value.amount; }).reduce(function(a,b) { return a+b; }, 0);
        };

        $scope.setNames = {};
        $scope.datamodel.getAllSets().then(function(sets) {
            for (var index in sets.data) {
                var s = sets.data[index];
                $scope.setNames[s.gatherercode] = s.name;
            }
        });
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
