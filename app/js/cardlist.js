angular.module('card-app')

    .controller('CardListController', function ($scope) {
        var cardNameRenderer = function (params) {
            return '<span class=inlinemtg ' +
                (params.data.gathererid ? 'data-multiverseid="' + params.data.gathererid + '" ' : '') +
                (params.data.setcode ? 'data-set="' + params.data.setcode + '" ' : '') +
                (params.data.split ? 'data-options="rotate90" ' : '') +
                '>' + params.data.name + '</span>';
        };

        var manaRenderer = function(params) {
            var mana = params.value;
            if (!mana) {
                return '';
            }

            if (params.data && params.data.split) {
                var split = mana.indexOf('/');
                return manaRenderer({value: mana.slice(0, split)}) + ' / ' + manaRenderer({value: mana.slice(split + 1, mana.length)});
            }

            var symbols = [];
            for (var i = 0; i < mana.length; i++) {
                var image = mapManaSymbolToImage(mana[i]);
                symbols.push('<img src="' + image + '" class="mana" title="' + mana[i] + '">');
            }
            return symbols.join('');
        };

        var mapManaSymbolToImage = function(symbol) {
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

        var setRenderer = function(params) {
            var image = mapSetCodeToImage(params.value);
            return image ? '<img src="' + image + '" class="set">' : '';
        };

        var mapSetCodeToImage = function(setcode) {
            var base = 'img/symbols/modern/';
            switch (setcode) {
                case '1E': return base + 'A - Core Sets/A01 - Pre 6th Fake Symbols/A0101 - Alpha - Common.svg';
                case 'M11': return base + 'A - Core Sets/A03 - Magic 20xx/A0305 - Magic 2011 - Common.svg';
                case 'FS': return base + 'B - Expert Level Expansion Sets/B13 - Time Spiral Block/B1310 - Future Sight - Rare.svg';
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

        $scope.updateAmount = function(data) {
            $scope.datamodel.updateAmount(data.name, data.amount);
        };

        var amountRenderer = function() {
            return '<input type="number" min="0" ng-model="data.amount" size="3" class="gridinput" ng-change="updateAmount(data)">';
        };

        $scope.gridOptions = {
            angularCompileRows: true,
            columnDefs: [{
                headerName: 'Count',
                cellRenderer: amountRenderer,
                width: 40
            },{
                headerName: 'Name',
                cellRenderer: cardNameRenderer,
                comparator: nameComparator,
                minWidth: 200,
                maxWidth: 600
            }, {
                headerName: 'Type',
                cellRenderer: cardTypeRenderer,
                minWidth: 200,
                maxWidth: 600
            }, {
                headerName: 'Set',
                field: 'setcode',
                cellRenderer: setRenderer,
                width: 50
            }, {
                headerName: 'Cost',
                field: 'mana',
                cellRenderer: manaRenderer,
                width: 100
            }],
            enableSorting: true,
            rowData: []
        };

        $scope.updateGrid = function(cards) {
            if (cards) {
                $scope.gridOptions.rowData = cards;
            }
            $scope.gridOptions.api.onNewRows();
            inlinemtg.linkcards($('#card-grid'));
        };

        $scope.cardSearchValue = '';
        var searchResult = null;
        $scope.searchCallback = function(query) {
            console.log('searching for ' + query);
            return $scope.datamodel.getInventory().then(function(result) {
                return result.data.cards;
            });
        };
        $scope.searchSelectCallback = function(result) {
            searchResult = result.item.name;
            console.log('select ' + searchResult);
            return result.item.name;
        };
        $scope.submitAdd = function() {
            console.log('add ' + searchResult);
            if (!searchResult) {
                return;
            }

            $scope.gridOptions.rowData.push({name: searchResult});
            $scope.updateGrid();
            document.getElementById('add-card').value = '';
            searchResult = null;
        };
    });
