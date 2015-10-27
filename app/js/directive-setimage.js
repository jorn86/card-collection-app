angular.module('card-app')
    .directive('setimage', function() {
        var getPrefixes = function(setcode) {
            switch(setcode) {
                case '1E': return ['A', '01', ['01', '02', '03'], 'Alpha'];
                case '2E': return ['A', '01', ['04', '05', '06'], 'Beta'];
                case '2U': return ['A', '01', ['07', '08', '09'], 'Unlimited'];
                case '3E': return ['A', '01', ['10', '11', '12'], 'Revised'];
                case '4E': return ['A', '01', ['13', '14', '15'], 'Fourth Edition'];
                case '5E': return ['A', '01', ['16', '17', '18'], 'Fifth Edition'];
                case '6E': return ['A', '02', ['01', '02', '03'], 'Sixth Edition Classic'];
                case '7E': return ['A', '02', ['04', '05', '06'], 'Seventh Edition'];
                case '8ED': return ['A', '02', ['07', '08', '09'], 'Eighth Edition'];
                case '9ED': return ['A', '02', ['10', '11', '12'], 'Ninth Edition'];
                case '10E': return ['A', '02', ['13', '14', '15'], 'Tenth Edition'];
                case 'M10': return ['A', '03', ['01', '02', '03', '04'], 'Magic 2010'];
                case 'M11': return ['A', '03', ['05', '06', '07', '08'], 'Magic 2011'];
                case 'M12': return ['A', '03', ['09', '10', '11', '12'], 'Magic 2012'];
                case 'M13': return ['A', '03', ['13', '14', '15', '16'], 'Magic 2013'];
                case 'M14': return ['A', '03', ['17', '18', '19', '20'], 'Magic 2014'];
                case 'M15': return ['A', '03', ['21', '22', '23', '24'], 'Magic 2015'];
                case 'ORI': return ['A', '03', ['25', '26', '27', '28'], 'Magic Origins'];
                case 'RAV': return ['B', '12', ['01', '02', '03'], 'Ravnica'];
                case 'GPT': return ['B', '12', ['04', '05', '06'], 'Guildpact'];
                case 'DIS': return ['B', '12', ['07', '08', '09'], 'Dissension'];
                case 'TSP': return ['B', '13', ['01', '02', '03'], 'Time Spiral']; // 04: Timeshifted
                case 'PLC': return ['B', '13', ['05', '06', '07'], 'Planar Chaos'];
                case 'FUT': return ['B', '13', ['08', '09', '10'], 'Future Sight'];
                case 'LRW': return ['B', '14', ['01', '02', '03'], 'Lorwyn'];
                case 'MOR': return ['B', '14', ['04', '05', '06'], 'Morningtide'];
                case 'SHM': return ['B', '14', ['07', '08', '09'], 'Shadowmoor'];
                case 'EVE': return ['B', '14', ['10', '11', '12'], 'Eventide'];
                case 'ALA': return ['B', '15', ['01', '02', '03', '04'], 'Shards of Alara'];
                case 'CFX': return ['B', '15', ['05', '06', '07', '08'], 'Conflux'];
                case 'ARB': return ['B', '15', ['09', '10', '11', '12'], 'Alara Reborn'];
                case 'ZEN': return ['B', '16', ['01', '02', '03', '04'], 'Zendikar'];
                case 'WWK': return ['B', '16', ['05', '06', '07', '08'], 'Worldwake'];
                case 'ROE': return ['B', '16', ['09', '10', '11', '12'], 'Rise of the Eldrazi'];
                case 'SOM': return ['B', '17', ['01', '02', '03', '04'], 'Scars of Mirrodin'];
                case 'MBS': return ['B', '17', ['05', '06', '07', '08'], 'Mirrodin Besieged'];
                case 'NPH': return ['B', '17', ['09', '10', '11', '12'], 'New Phyrexia'];
                case 'ISD': return ['B', '18', ['01', '02', '03', '04'], 'Innistrad'];
                case 'DKA': return ['B', '18', ['05', '06', '07', '08'], 'Dark Ascension'];
                case 'AVR': return ['B', '18', ['09', '10', '11', '12'], 'Avacyn Restored'];
                case 'RTR': return ['B', '19', ['01', '02', '03', '04'], 'Return to Ravnica'];
                case 'GTC': return ['B', '19', ['05', '06', '07', '08'], 'Gatecrash'];
                case 'DGM': return ['B', '19', ['09', '10', '11', '12'], "Dragon's Maze"];
                case 'THS': return ['B', '20', ['01', '02', '03', '04'], 'Theros'];
                case 'BNG': return ['B', '20', ['05', '06', '07', '08'], 'Born of the Gods'];
                case 'JOU': return ['B', '20', ['09', '10', '11', '12'], 'Journey Into Nyx'];
                case 'KTK': return ['B', '21', ['01', '02', '03', '04'], 'Khans of Tarkir'];
                case 'FRF': return ['B', '21', ['05', '06', '07', '08'], 'Fate Reforged'];
                case 'DTK': return ['B', '21', ['09', '10', '11', '12'], 'Dragons of Tarkir'];
                case 'BFZ': return ['B', '22', ['01', '02', '03', '04'], 'Battle for Zendikar'];
                case 'OGW': return ['B', '22', ['05', '06', '07', '08'], 'Oath of the Gatewatch'];
                case 'EXP': return ['B', '22', ['09', '10', '11', '12'], 'Zendikar Expeditions'];
            }
        };

        var getFolder = function(group, block) {
            switch (group + block) {
                case 'A01': return 'A - Core Sets/A01 - Pre 6th Fake Symbols';
                case 'A02': return 'A - Core Sets/A02 - 6th to 10th editions';
                case 'A03': return 'A - Core Sets/A03 - Magic 20xx';
                case 'B00': return 'B - Expert Level Expansion Sets/B00 - Original Black-Grey Pre Exodus Set Symbols';
                case 'B01': return 'B - Expert Level Expansion Sets/B01 - Early Non-Block Sets';
                case 'B02': return 'B - Expert Level Expansion Sets/B02 - Ice Age Block';
                case 'B03': return 'B - Expert Level Expansion Sets/B03 - Mirage Block';
                case 'B04': return 'B - Expert Level Expansion Sets/B04 - Tempest Block';
                case 'B05': return 'B - Expert Level Expansion Sets/B05 - Urza Block';
                case 'B06': return 'B - Expert Level Expansion Sets/B06 - Masques Block';
                case 'B07': return 'B - Expert Level Expansion Sets/B07 - Invasion Block';
                case 'B08': return 'B - Expert Level Expansion Sets/B08 - Odyssey Block';
                case 'B09': return 'B - Expert Level Expansion Sets/B09 - Onslaught Block';
                case 'B10': return 'B - Expert Level Expansion Sets/B10 - Mirrodin Block';
                case 'B11': return 'B - Expert Level Expansion Sets/B11 - Kamigawa Block';
                case 'B12': return 'B - Expert Level Expansion Sets/B12 - Ravnica Block';
                case 'B13': return 'B - Expert Level Expansion Sets/B13 - Time Spiral Block';
                case 'B14': return 'B - Expert Level Expansion Sets/B14 - Lorwyn-Shadowmoor Block';
                case 'B15': return 'B - Expert Level Expansion Sets/B15 - Shards of Alara Block';
                case 'B16': return 'B - Expert Level Expansion Sets/B16 - Zendikar Block';
                case 'B17': return 'B - Expert Level Expansion Sets/B17 - Scars of Mirrodin Block';
                case 'B18': return 'B - Expert Level Expansion Sets/B18 - Innistrad Block';
                case 'B19': return 'B - Expert Level Expansion Sets/B19 - Return to Ravnica Block';
                case 'B20': return 'B - Expert Level Expansion Sets/B20 - Theros Block';
                case 'B21': return 'B - Expert Level Expansion Sets/B21 - Khans of Tarkir Block';
                case 'B22': return 'B - Expert Level Expansion Sets/B22 - Battle for Zendikar Block';
                case 'C00': return 'C - Command Zone Sets/D00 - Vanguard';
                case 'C01': return 'C - Command Zone Sets/D01 - Planechase';
                case 'C02': return 'C - Command Zone Sets/D02 - Archenemy';
                case 'C03': return 'C - Command Zone Sets/D03 - Commander';
                case 'C04': return 'C - Command Zone Sets/D04 - Planechase 2012';
                case 'C05': return "C - Command Zone Sets/D05 - Commander's Arsenal";
                case 'C06': return 'C - Command Zone Sets/D06 - Commander 2013';
                case 'C07': return 'C - Command Zone Sets/D07 - Commander 2014';
            }
        };

        var rarities = ['Common', 'Uncommon', 'Rare', 'Mythic Rare'];
        var getNames = function(setcode, rarity) {
            if (rarity === 'Timeshifted') {
                return {
                    folder: 'B - Expert Level Expansion Sets/B13 - Time Spiral Block',
                    file: 'B1304 - Time Spiral - Timeshifted.svg',
                    commonfile: 'B1301 - Time Spiral - Common.svg'
                };
            }

            var prefixes = getPrefixes(setcode);
            if (!prefixes) return null;

            return {
                folder: getFolder(prefixes[0], prefixes[1]),
                file:       prefixes[0] + prefixes[1] + prefixes[2][rarities.indexOf(rarity)]
                + ' - ' + prefixes[3] + ' - ' + rarity + '.svg',
                commonfile: prefixes[0] + prefixes[1] + prefixes[2][0]
                + ' - ' + prefixes[3] + ' - Common.svg'
            };
        };

        return {
            scope: {'set': '=', rarity: '='},
            link: function($scope, element) {
                var names = getNames($scope.set, $scope.rarity);
                if (!names) {
                    element.append("&nbsp;");
                    return;
                }
                var onerror = "$(this).attr('src', 'img/symbols/set/" + names.folder + "/" + names.commonfile + "').attr('onerror', '');";
                var img = element.append('<img class="set" src="img/symbols/set/' + names.folder + '/' + names.file + '" onerror="' + onerror + '">');
            }
        };
    });
