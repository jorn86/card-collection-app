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
                    element.append('<img class="mana" src="' + getImage(mana[i]) + '">');
                }
            }
        };

        var getImage = function(symbol) {
            var base = 'img/symbols/mana/';
            switch (symbol) {
                case 'X': return base + 'B - Colorless Mana/B21 - Colorless Mana - X.svg';
                case 'Y': return base + 'B - Colorless Mana/B22 - Colorless Mana - Y.svg';
                case 'Z': return base + 'B - Colorless Mana/B23 - Colorless Mana - Z.svg';
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
                case 'HC': return base + 'X - Miscellaneous Symbols/X06 - Half Mana - Colorless.svg';
                case 'W': return base + 'A - Colored Mana/A01 - Colored Mana - White.svg';
                case 'U': return base + 'A - Colored Mana/A02 - Colored Mana - Blue.svg';
                case 'B': return base + 'A - Colored Mana/A03 - Colored Mana - Black.svg';
                case 'R': return base + 'A - Colored Mana/A04 - Colored Mana - Red.svg';
                case 'G': return base + 'A - Colored Mana/A05 - Colored Mana - Green.svg';
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
                case 'PW': return base + 'D - Phyrexian Mana/D01 - Phyrexian Mana - White.svg';
                case 'PU': return base + 'D - Phyrexian Mana/D02 - Phyrexian Mana - Blue.svg';
                case 'PB': return base + 'D - Phyrexian Mana/D03 - Phyrexian Mana - Black.svg';
                case 'PR': return base + 'D - Phyrexian Mana/D04 - Phyrexian Mana - Red.svg';
                case 'PG': return base + 'D - Phyrexian Mana/D05 - Phyrexian Mana - Green.svg';
                case 'HW': return base + 'X - Miscellaneous Symbols/X01 - Half Mana - White.svg';
                case 'HU': return base + 'X - Miscellaneous Symbols/X02 - Half Mana - Blue.svg';
                case 'HB': return base + 'X - Miscellaneous Symbols/X03 - Half Mana - Black.svg';
                case 'HR': return base + 'X - Miscellaneous Symbols/X04 - Half Mana - Red.svg';
                case 'HG': return base + 'X - Miscellaneous Symbols/X05 - Half Mana - Green.svg';
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
                    element.append('<img class="mana" src="img/symbols/mana/X - Miscellaneous Symbols/X17 - Miscellaneous - Forward Slash.svg">');
                    append(element, parse($scope.mana.substring(0, index)));
                }
                else {
                    append(element, parse($scope.mana));
                }
            }
        };
    });