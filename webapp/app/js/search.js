angular.module('card-app')
    .controller('SearchController', function($scope, $state) {
        $scope.search = {
            name: '',
            text: '',
            types: '',
            p: {},
            t: {},
            loyalty: {},
            c: {
                type: 'any',
                amount:{}
            },
            ci: {
                amount:{}
            },
            cost: '',
            cmc: {},
            format: 'All',
            rarity: null,
            ft: '',
            special: {},
            exclude: {
                un: true
            }
        };
        $scope.costOptions = [
            '0', '1', '2', '3', '4', '5', '6', '7', '8', 'x',
            'c', 'w', 'u', 'b', 'r', 'g',
            'wp', 'up', 'bp', 'rp', 'gp',
            'wu', 'wb', 'ub', 'ur', 'br', 'bg', 'rg', 'rw', 'gw', 'gu',
            '2w', '2u', '2b', '2r', '2g'
        ];
        $scope.rarityOptions = ['Common', 'Uncommon', 'Rare', 'Mythic Rare', 'Special', 'Basic Land'];
        $scope.formatOptions = ['All', 'Vintage', 'Legacy', 'Modern', 'Standard', 'Commander'];

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

        var parseAmount = function(amount) {
            return (amount.amounttype || '=') + amount.amount;
        };
        var color = function(color) {
            if (color.c) return 'c';
            var c = '';
            if (color.w) c += 'w';
            if (color.u) c += 'u';
            if (color.b) c += 'b';
            if (color.r) c += 'r';
            if (color.g) c += 'g';
            return c;
        };
        $scope.createQuery = function() {
            var parts = [];
            if ($scope.search.name) {
                parts.push('"' + $scope.search.name + '"');
            }
            if ($scope.search.text) {
                parts.push('o:"' + $scope.search.text + '"');
            }
            if ($scope.search.types) {
                parts.push(_.map($scope.search.types.split(/\s+/), function(type) { return 't:"' + type + '"'}));
            }
            if ($scope.search.cmc.amount) {
                parts.push('cmc' + parseAmount($scope.search.cmc));
            }
            if ($scope.search.ci.amount.amount) {
                parts.push('ca' + parseAmount($scope.search.ci.amount));
            }
            if (color($scope.search.c)) {
                if ($scope.search.c.type === 'any') {
                    var query = _.map(color($scope.search.c).split(''), function(c) { return 'c>=' + c }).join(' or ');
                    parts.push('(' + query + ')');
                }
                else if ($scope.search.c.type === 'all') {
                    parts.push('c>=' + color($scope.search.c));
                }
                else if ($scope.search.c.type === 'only') {
                    parts.push('c<=' + color($scope.search.c));
                }
                else if ($scope.search.c.type === 'exact') {
                    parts.push('c=' + color($scope.search.c));
                }
            }
            if ($scope.search.c.amount.amount) {
                parts.push('ca' + parseAmount($scope.search.c.amount));
            }
            if (color($scope.search.ci)) {
                parts.push('ci=' + color($scope.search.ci));
            }
            if ($scope.search.p.amount) {
                parts.push('pow' + parseAmount($scope.search.p));
            }
            if ($scope.search.t.amount) {
                parts.push('tou' + parseAmount($scope.search.t));
            }
            if ($scope.search.loyalty.amount) {
                parts.push('loyalty' + parseAmount($scope.search.loyalty));
            }
            if ($scope.search.rarity) {
                parts.push('r:"' + $scope.search.rarity + '"');
            }
            if ($scope.search.format && $scope.search.format !== 'All') {
                parts.push('f:' + $scope.search.format);
            }
            if (!$scope.search.special.conspiracy) {
                parts.push('not:(t:conspiracy)')
            }
            if (!$scope.search.special.plane) {
                parts.push('not:(l:plane)')
            }
            if (!$scope.search.special.phenomenon) {
                parts.push('not:(l:phenomenon)')
            }
            if (!$scope.search.special.scheme) {
                parts.push('not:(l:scheme)')
            }
            if (!$scope.search.special.token) {
                parts.push('not:(l:token)')
            }
            if ($scope.search.exclude.un) {
                parts.push('not:(e:unh or e:ug)')
            }

            return parts.join(' ');
        };

        $scope.doSearch = function() {
            var query = $scope.createQuery();
            //window.open('http://magiccards.info/query?q=' + query, '_blank');
            $state.go('app.searchresults', {query: query, page: 1});
        };
    });
