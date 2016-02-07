angular.module('card-app')
    .directive('cardlist', function() {
        return {
            restrict: 'E',
            templateUrl: 'partials/cardlist.html',
            scope: {list: '=', editable: '='},
            controller: function($scope, $rootScope) {
                var types = ['Creature', 'Instant', 'Sorcery', 'Artifact', 'Enchantment', 'Planeswalker', 'Land', 'Plane', 'Scheme', 'Other'];
                var typeGrouping = function (card) {
                    if (card.type) {
                        if (card.type.indexOf('Land') >= 0) {
                            return 'Land';
                        }
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
                    rows: $scope.list.cards,

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
                    var promise = $rootScope.datamodel.updateAmount($scope.list.id, data.id, data.amount);
                    if (data.amount == 0) {
                        promise.then(function(updatedBoard) {
                            $scope.list.cards = updatedBoard.data;
                            $scope.grid.rows = updatedBoard.data;
                        });
                    }
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
                    return $rootScope.datamodel.searchCardsByName(query.query).then(function (result) {
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

                    $rootScope.datamodel.addCardToDeck($scope.list.id, searchResult.id, 1).then(function(updatedBoard) {
                        $scope.list.cards = updatedBoard.data;
                        $scope.grid.rows = updatedBoard.data;
                    });

                    searchResult = null;
                };

                $scope.totalAmount = function (group) {
                    return _.map(group, function(value) { return value.amount; }).reduce(function(a,b) { return a+b; }, 0);
                };

                $scope.setNames = {};
                $rootScope.datamodel.getSetStatistics().then(function(sets) {
                    for (var index in sets.data) {
                        var s = sets.data[index];
                        $scope.setNames[s.gatherercode] = s.name;
                    }
                });
            }
        }
    });
