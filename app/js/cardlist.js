angular.module('card-app')

    .controller('CardListController', function ($scope) {
        $scope.mapSetCodeToImage = function(setcode) {
            var base = 'img/symbols/modern/';
            switch (setcode) {
                case '1E': return base + 'A - Core Sets/A01 - Pre 6th Fake Symbols/A0101 - Alpha - Common.svg';
                case 'M11': return base + 'A - Core Sets/A03 - Magic 20xx/A0305 - Magic 2011 - Common.svg';
                case 'ORI': return base + 'A - Core Sets/A03 - Magic 20xx/A0325 - Magic Origins - Common.svg';
                case 'FUI': return base + 'B - Expert Level Expansion Sets/B13 - Time Spiral Block/B1310 - Future Sight - Rare.svg';
                case '5DN': return base + 'B - Expert Level Expansion Sets/B10 - Mirrodin Block/B1009 - Fifth Dawn - Rare.svg';
                case 'SHM': return base + 'B - Expert Level Expansion Sets/B14 - Lorwyn-Shadowmoor Block/B1409 - Shadowmoor - Rare.svg';
                case 'CFX': return base + 'B - Expert Level Expansion Sets/B15 - Shards of Alara Block/B1508 - Conflux - Mythic Rare.svg';
                case 'DKA': return base + 'B - Expert Level Expansion Sets/B18 - Innistrad Block/B1805 - Dark Ascension - Common.svg';
                case 'RTR': return base + 'B - Expert Level Expansion Sets/B19 - Return to Ravnica Block/B1902 - Return to Ravnica - Uncommon.svg';
                case 'THS': return base + 'B - Expert Level Expansion Sets/B20 - Theros Block/B2003 - Theros - Rare.svg';
                case 'PC2': return base + 'C - Command Zone Sets/D04 - Planechase 2012/D0401 - Planechase 2012 - Common.svg';
                case 'C14': return base + 'C - Command Zone Sets/D07 - Commander 2014/D0605 - Commander 2014 - Common.svg';
                case 'DDJ': return base + 'F - Duel Decks/F10 - Izzet vs. Golgari/F1002 - Izzet vs Golgari - Uncommon.svg';
                case 'UNH': return base + 'K - Un-Sets/K02 - Unhinged/K0203 - Unhinged - Rare.svg';
                case 'DRB': return base + 'G - From the Vault/G01 - Dragons/G0103 - From the Vault Dragons - Rare.svg';
            }
            console.log('No symbol available for set', setcode);
        };

        var types = ['Creature', 'Instant', 'Sorcery', 'Artifact', 'Enchantment', 'Planeswalker', 'Land', 'Other'];
        var typeGrouping = function(card) {
            for (var i = 0; i < types.length; i++) {
                if (card.type.indexOf(types[i]) >= 0) {
                    return types[i];
                }
            }
            return 'Other';
        };
        var typeOrder = function(card) {
            return _.indexOf(types, card.$key);
        };

        var colors = {'W': 'White', 'U': 'Blue', 'B': 'Black', 'R': 'Red', 'G': 'Green', 'C': 'Colorless', 'M': 'Multicolored'};
        var colorGrouping = function(card) {
            return colors[color(card.mana)];
        };
        var colorOrder = function(group) {
            return _.indexOf(_.keys(colors), color(group.$key));
        };
        var color = function(mana) {
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
            }],
            groupBy: typeGrouping,
            orderBy: typeOrder,

            sortingOptions: [
                { name: 'Count', field: 'amount' },
                { name: 'Name', field: 'name' },
                { name: 'Type', field: 'type' },
                { name: 'Set', field: 'setcode' },
                { name: 'CMC', field: 'cmc' },
                { name: 'Cost', field: 'mana' }
            ],
            sortDescending: false,

            allSelected: false
        };

        $scope.grid.currentGrouping = $scope.grid.groupingOptions[0];
        $scope.onGroupingChange = function() {
            $scope.grid.orderBy = $scope.grid.currentGrouping.order;
            $scope.grid.groupBy = $scope.grid.currentGrouping.group;
        };

        $scope.grid.currentSorting = $scope.grid.sortingOptions[0];

        $scope.updateAmount = function(data) {
            $scope.datamodel.updateAmount($scope.deck.id, data.rowId, data.amount);
        };

        $scope.updateGrid = function(cards) {
            $scope.grid.rows = cards;
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

            $scope.grid.rows.push({name: searchResult, type: 'Unknown', amount: 1});
            document.getElementById('add-card').value = '';
            searchResult = null;
        };

        $scope.setDeck = function(deck) {
            $scope.deck = deck;
            $scope.updateGrid(deck.cards);
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
    })

    .directive('manacost', function() {
        var parse = function(mana) {
            var re = /([XYZ]*)([0-9]*)(({.*?})*)([WUBRG]*)/ig; // ({.*})*
            var parsed = re.exec(mana);
            if (!parsed) {
                console.log('Error parsing mana cost', mana);
                return mana;
            }

            var result = [];
            if (parsed[1]) {
                result = result.concat(parsed[1].split(''));
            }
            if (parsed[2]) {
                result.push(parsed[2]);
            }
            if (parsed[3]) {
                var start = 0;
                do {
                    var end = parsed[3].indexOf('}', start);
                    result.push(parsed[3].slice(start + 1, end));
                    start = parsed[3].indexOf('{', end);
                }
                while (start > 0);
            }
            if (parsed[5]) {
                result = result.concat(parsed[5].split(''));
            }
            return result;
        };

        var append = function(element, mana) {
            for (var i = 0; i < mana.length; i++) {
                if (mana[i]) {
                    element.append('<img class="mana" src="' + getImage(mana[i]) + '">');
                }
            }
        };

        var getImage = function(symbol) {
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
                case '5': return base + 'B - Colorless Mana/B05 - Colorless Mana - Five.svg';
                case '6': return base + 'B - Colorless Mana/B06 - Colorless Mana - Six.svg';
                case '7': return base + 'B - Colorless Mana/B07 - Colorless Mana - Seven.svg';
                case '8': return base + 'B - Colorless Mana/B08 - Colorless Mana - Eight.svg';
                case '9': return base + 'B - Colorless Mana/B09 - Colorless Mana - Nine.svg';
                case '10': return base + 'B - Colorless Mana/B10 - Colorless Mana - Ten.svg';
                case '11': return base + 'B - Colorless Mana/B11 - Colorless Mana - Eleven.svg';
                case '12': return base + 'B - Colorless Mana/B12 - Colorless Mana - Twelve.svg';
                case '13': return base + 'B - Colorless Mana/B13 - Colorless Mana - Thirteen.svg';
                case '14': return base + 'B - Colorless Mana/B14 - Colorless Mana - Fourteen.svg';
                case '15': return base + 'B - Colorless Mana/B15 - Colorless Mana - Fifteen.svg';
                case '16': return base + 'B - Colorless Mana/B16 - Colorless Mana - Sixteen.svg';
                case '17': return base + 'B - Colorless Mana/B17 - Colorless Mana - Seventeen.svg';
                case '18': return base + 'B - Colorless Mana/B18 - Colorless Mana - Eighteen.svg';
                case '19': return base + 'B - Colorless Mana/B19 - Colorless Mana - Nineteen.svg';
                case '20': return base + 'B - Colorless Mana/B20 - Colorless Mana - Twenty.svg';
                case '100': return base + 'X - Miscellaneous Symbols/X08 - Colorless - One Hundred.svg';
                case '1000000': return base + 'X - Miscellaneous Symbols/X09 - Colorless - One Million.svg';
                case 'X': return base + 'B - Colorless Mana/B21 - Colorless Mana - X.svg';
                case 'Y': return base + 'B - Colorless Mana/B22 - Colorless Mana - Y.svg';
                case 'Z': return base + 'B - Colorless Mana/B23 - Colorless Mana - Z.svg';
                case 'WU': return base + 'C - Hybrid Mana/C01 - Hybrid Mana - White or Blue.svg';
                case 'WB': return base + 'C - Hybrid Mana/C02 - Hybrid Mana - White or Black.svg';
                case 'UB': return base + 'C - Hybrid Mana/C03 - Hybrid Mana - Blue or Black.svg';
                case 'UR': return base + 'C - Hybrid Mana/C04 - Hybrid Mana - Blue or Red.svg';
                case 'BR': return base + 'C - Hybrid Mana/C05 - Hybrid Mana - Black or Red.svg';
                case 'BG': return base + 'C - Hybrid Mana/C06 - Hybrid Mana - Black or Green.svg';
                case 'RG': return base + 'C - Hybrid Mana/C07 - Hybrid Mana - Red or Green.svg';
                case 'RW': return base + 'C - Hybrid Mana/C08 - Hybrid Mana - Red or White.svg';
                case 'GW': return base + 'C - Hybrid Mana/C09 - Hybrid Mana - Green or White.svg';
                case 'GU': return base + 'C - Hybrid Mana/C10 - Hybrid Mana - Green or Blue.svg';
                case '2W': return base + 'C - Hybrid Mana/C11 - Hybrid Mana - 2 Colorless or White.svg';
                case '2U': return base + 'C - Hybrid Mana/C12 - Hybrid Mana - 2 Colorless or Blue.svg';
                case '2B': return base + 'C - Hybrid Mana/C13 - Hybrid Mana - 2 Colorless or Black.svg';
                case '2R': return base + 'C - Hybrid Mana/C14 - Hybrid Mana - 2 Colorless or Red.svg';
                case '2G': return base + 'C - Hybrid Mana/C15 - Hybrid Mana - 2 Colorless or Green.svg';
            }
            console.log('Unknown mana symbol:', symbol);
        };

        return {
            scope: {
                mana: '='
            },
            link: function($scope, element) {
                if (!$scope.mana) {
                    return;
                }

                var index = $scope.mana.indexOf('/');
                if (index >= 0) {
                    append(element, parse($scope.mana.substring(index + 1)));
                    element.append('<img class="mana" src="img/symbols/mana/X - Miscellaneous Symbols/X17 - Miscellaneous - Forward Slash.svg">')
                    append(element, parse($scope.mana.substring(0, index)));
                }
                else {
                    append(element, parse($scope.mana));
                }
            }
        };
    });
