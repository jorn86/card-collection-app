angular.module('card-app')
    .directive('setimage', function() {
        var promoSets = ['CED', 'CEI', 'RQS', 'MGB', 'ITP', 'VAN', 'CPK'];
        var mapSet = function(setcode) {
            switch(setcode) {
                case 'BD': return 'BTD'; // Coldsnap Theme Decks
                case 'CST': return 'CSP'; // Coldsnap Theme Decks
                case 'TSB': return 'TSP'; // Timeshifted
                case 'DD3_EVG': return 'EVG'; // Reprint
                case 'DD3_JVC': return 'DD2'; // Reprint
                case 'DD3_DVD': return 'DDC'; // Reprint
                case 'DD3_GVL': return 'DDD'; // Reprint
                case 'FRF_UGIN': return 'FRF';
            }
            return setcode;
        };

        var getRarityClass = function(rarity) {
            switch (rarity) {
                case 'Uncommon': return 'uncommon';
                case 'Rare': return 'rare';
                case 'Mythic Rare': return 'mythic';
            }
        };

        return {
            scope: {'set': '=', rarity: '='},
            template: '&nbsp;<span class="set"><i class="mtg {{setClass}} {{rarityClass}}"></i></span>',
            link: function($scope) {
                if ($scope.set) {
                    if ($scope.set.slice(0, 1) === 'p' || promoSets.indexOf($scope.set) >= 0) {
                        $scope.setClass = 'promo-2';
                    } else {
                        $scope.setClass = 'e-' + mapSet($scope.set).toLowerCase();
                    }
                }
                else {

                }
                $scope.rarityClass = getRarityClass($scope.rarity) || '';
            }
        };
    });
