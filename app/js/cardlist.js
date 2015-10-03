angular.module('card-app')

    .controller('CardListController', function ($scope, $timeout) {
        $scope.mapManaSymbolToImage = function(symbol) {
            var base = 'img/symbols/mana/';
            switch (symbol) {
                case 'W': return base + 'A - Colored Mana/A01 - Colored Mana - White.svg';
                case 'U': return base + 'A - Colored Mana/A02 - Colored Mana - Blue.svg';
                case 'B': return base + 'A - Colored Mana/A03 - Colored Mana - Black.svg';
                case 'R': return base + 'A - Colored Mana/A04 - Colored Mana - Red.svg';
                case 'G': return base + 'A - Colored Mana/A05 - Colored Mana - Green.svg';
                case '0': return base + 'B - Colorless Mana/B00 - Colorless Mana - Zero.svg';
                case '1': return base + 'B - Colorless Mana/B01 - Colorless Mana - One.svg';
                case '2': return base + 'B - Colorless Mana/B02 - Colorless Mana - Two.svg';
                case '3': return base + 'B - Colorless Mana/B03 - Colorless Mana - Three.svg';
                case '4': return base + 'B - Colorless Mana/B04 - Colorless Mana - Four.svg';
            }
        };

        $scope.mapSetCodeToImage = function(setcode) {
            var base = 'img/symbols/modern/';
            switch (setcode) {
                case '1E': return base + 'A - Core Sets/A01 - Pre 6th Fake Symbols/A0101 - Alpha - Common.svg';
                case 'M11': return base + 'A - Core Sets/A03 - Magic 20xx/A0305 - Magic 2011 - Common.svg';
                case 'FS': return base + 'B - Expert Level Expansion Sets/B13 - Time Spiral Block/B1310 - Future Sight - Rare.svg';
                case 'CFX': return base + 'B - Expert Level Expansion Sets/B15 - Shards of Alara Block/B1508 - Conflux - Mythic Rare.svg';
                case 'DKA': return base + 'B - Expert Level Expansion Sets/B18 - Innistrad Block/B1805 - Dark Ascension - Common.svg';
                case 'THS': return base + 'B - Expert Level Expansion Sets/B20 - Theros Block/B2003 - Theros - Rare.svg';
                case 'PC2': return base + 'C - Command Zone Sets/D04 - Planechase 2012/D0401 - Planechase 2012 - Common.svg';
                case 'IVG': return base + 'F - Duel Decks/F10 - Izzet vs. Golgari/F1002 - Izzet vs Golgari - Uncommon.svg';
            }
        };

        var cardTypeRenderer = function(params) {
            if (params.data.subtype) {
                return params.data.type + ' &mdash; ' + params.data.subtype;
            }
            return params.data.type;
        };

        var nameComparator = function(first, second, firstData, secondData) {
            return firstData.data.name.localeCompare(secondData.data.name);
        };

        var types = ['Creature', 'Instant', 'Sorcery', 'Artifact', 'Enchantment', 'Planeswalker', 'Land'];
        $scope.typeGrouping = function(card) {
            for (var i = 0; i < types.length; i++) {
                if (card.type.indexOf(types[i]) >= 0) {
                    return types[i];
                }
            }
            return 'Other';
        };

        $scope.updateAmount = function(data) {
            $scope.datamodel.updateAmount(data.name, data.amount);
        };

        $scope.grid = {
            rows: [],
            groupBy: $scope.typeGrouping,
            allSelected: false
        };

        $scope.updateGrid = function(cards) {
            if (cards) {
                $scope.grid.rows = cards;
            }
            inlinemtg.linkcards($('#card-grid'));
        };

        $scope.onSelectAll = function() {
            for (var i = 0; i < $scope.grid.rows.length; i++) {
                $scope.grid.rows[i].selected = $scope.grid.allSelected;
            }
        };

        $scope.getSelected = function() {
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
        $scope.searchCallback = function(query) {
            return $scope.datamodel.getInventory().then(function(result) {
                return result.data.cards;
            });
        };
        $scope.searchSelectCallback = function(result) {
            searchResult = result.item.name;
            return result.item.name;
        };
        $scope.submitAdd = function() {
            if (!searchResult) {
                return;
            }

            $scope.grid.rows.push({name: searchResult});
            $scope.updateGrid();
            document.getElementById('add-card').value = '';
            searchResult = null;
        };

        $timeout($scope.updateGrid, 500);
    });
