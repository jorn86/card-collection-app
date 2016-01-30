angular.module('card-app')
    .controller('SearchController', function($scope) {
        $scope.search = {
            name: '',
            text: '',
            types: '',
            p: null, // power
            t: null, // toughness
            c: {
                type: 'any'
            },
            ci: {
                c: true
            },
            cost: '',
            format: { value: 'All' },
            rarity: null
        };
        $scope.costOptions = [
            '0', '1', '2', '3', '4', '5', '6', '7', '8',
            'w', 'u', 'b', 'r', 'g', 'c',
            'wp', 'up', 'bp', 'rp', 'gp',
            'wu', 'wb', 'ub', 'ur', 'br', 'bg', 'rg', 'rw', 'gw', 'gu',
            '2w', '2u', '2b', '2r', '2g'
        ];
        $scope.rarityOptions = ['Common', 'Uncommon', 'Rare', 'Mythic Rare', 'Special', 'Basic Land'];
        $scope.formatOptions = ['All', 'Vintage', 'Legacy', 'Extended', 'Modern', 'Standard', 'Commander', 'MTGO'];

        $scope.$watch('search.ci.c', function(value) {
            if (value) {
                $scope.search.ci.w = false;
                $scope.search.ci.u = false;
                $scope.search.ci.b = false;
                $scope.search.ci.r = false;
                $scope.search.ci.g = false;
            }
        });
        var reset = function(value) { if (value) $scope.search.ci.c = false; };
        $scope.$watch('search.ci.w', reset);
        $scope.$watch('search.ci.u', reset);
        $scope.$watch('search.ci.b', reset);
        $scope.$watch('search.ci.r', reset);
        $scope.$watch('search.ci.g', reset);

        $scope.addManaCost = function(symbol) {
            $scope.search.cost = $scope.search.cost + (symbol.length == 1 ? symbol.toUpperCase() : '{' + symbol.toUpperCase() + '}');
        };

        $scope.doSearch = function() {
            console.log('search', $scope.search)
        };
    });
