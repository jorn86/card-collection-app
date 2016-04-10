angular.module('card-app')
    .directive('manacost', function() {
        var parse = function(mana) {
            var re = /([XYZ]*)([0-9]*)(({.*?})*)([WUBRG]*)/ig;
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
                    var image = getImage(mana[i]);
                    if (image) {
                        element.append('<img class="mana" src="' + image + '">');
                    }
                    else {
                        element.append('<i class="mtg mana-' + mana[i].toLowerCase() + '">');
                    }
                }
            }
        };

        var getImage = function(symbol) {
            var base = 'img/symbols/mana/';
            switch (symbol) {
                case '100': return base + 'X - Miscellaneous Symbols/X08 - Colorless - One Hundred.svg';
                case '1000000': return base + 'X - Miscellaneous Symbols/X09 - Colorless - One Million.svg';
                case 'HC': return base + 'X - Miscellaneous Symbols/X06 - Half Mana - Colorless.svg';
                case 'HW': return base + 'X - Miscellaneous Symbols/X01 - Half Mana - White.svg';
                case 'HU': return base + 'X - Miscellaneous Symbols/X02 - Half Mana - Blue.svg';
                case 'HB': return base + 'X - Miscellaneous Symbols/X03 - Half Mana - Black.svg';
                case 'HR': return base + 'X - Miscellaneous Symbols/X04 - Half Mana - Red.svg';
                case 'HG': return base + 'X - Miscellaneous Symbols/X05 - Half Mana - Green.svg';
            }
        };

        return {
            scope: {
                mana: '='
            },
            link: function($scope, element) {
                if (!$scope.mana) {
                    return;
                }

                var result = $scope.mana.split('/');
                append(element, parse(result[0]));
                for (var i = 1; i < result.length; i++) {
                    element.append(' / ');
                    append(element, parse(result[i]));
                }
            }
        };
    })
    .directive('mana', function() {
        return {
            restrict: 'E',
            template: '<i class="mtg" ng-class="style"></i>',
            scope: {s: '@'},
            link: function(scope) {
                scope.$watch('s', function(e, symbol) {
                    scope.style = 'mana-' + symbol;
                });
            }
        }
    });
