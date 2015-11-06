angular.module('card-app')
    .directive('setimage', function() {
        var getPrefixes = function(setcode) {
            switch(setcode) {
                case '1E':  return ['A', '01', ['01', '02', '03'], 'Alpha'];
                case '2E':  return ['A', '01', ['04', '05', '06'], 'Beta'];
                case '2U':  return ['A', '01', ['07', '08', '09'], 'Unlimited'];
                case '3E':  return ['A', '01', ['10', '11', '12'], 'Revised'];
                case '4E':  return ['A', '01', ['13', '14', '15'], 'Fourth Edition'];
                case '5E':  return ['A', '01', ['16', '17', '18'], 'Fifth Edition'];
                case '6E':  return ['A', '02', ['01', '02', '03'], 'Sixth Edition Classic'];
                case '7E':  return ['A', '02', ['04', '05', '06'], 'Seventh Edition'];
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
                case 'AN':  return ['B', '01', ['01', '02', '03'], 'Arabian Nights'];
                case 'AQ':  return ['B', '01', ['04', '05', '06'], 'Antiquities'];
                case 'LE':  return ['B', '01', ['07', '08', '09'], 'Legends'];
                case 'DK':  return ['B', '01', ['10', '11', '12'], 'The Dark'];
                case 'FE':  return ['B', '01', ['13', '14', '15'], 'Fallen Empires'];
                case 'HM':  return ['B', '01', ['16', '17', '18'], 'Homelands'];
                case 'IA':  return ['B', '02', ['01', '02', '03'], 'Ice Age'];
                case 'AL':  return ['B', '02', ['04', '05', '06'], 'Alliances'];
                case 'CSP': return ['B', '02', ['07', '08', '09'], 'Coldsnap'];
                case 'MI':  return ['B', '03', ['01', '02', '03'], 'Mirage'];
                case 'VI':  return ['B', '03', ['04', '05', '06'], 'Visions'];
                case 'WL':  return ['B', '03', ['07', '08', '09'], 'Weatherlight'];
                case 'TE':  return ['B', '04', ['01', '02', '03'], 'Tempest'];
                case 'ST':  return ['B', '04', ['04', '05', '06'], 'Stronghold'];
                case 'EX':  return ['B', '04', ['07', '08', '09'], 'Exodus'];
                case 'UZ':  return ['B', '05', ['01', '02', '03'], "Urza's Saga"];
                case 'GU':  return ['B', '05', ['04', '05', '06'], "Urza's Legacy"];
                case 'CG':  return ['B', '05', ['07', '08', '09'], "Urza's Destiny"];
                case 'MM':  return ['B', '06', ['01', '02', '03'], 'Mercadian Masques'];
                case 'NE':  return ['B', '06', ['04', '05', '06'], 'Nemesis'];
                case 'PR':  return ['B', '06', ['07', '08', '09'], 'Prophecy'];
                case 'IN':  return ['B', '07', ['01', '02', '03'], 'Invasion'];
                case 'PS':  return ['B', '07', ['04', '05', '06'], 'Planeshift'];
                case 'AP':  return ['B', '07', ['07', '08', '09'], 'Apocalypse'];
                case 'OD':  return ['B', '08', ['01', '02', '03'], 'Odyssey'];
                case 'TOR': return ['B', '08', ['04', '05', '06'], 'Torment'];
                case 'JUD': return ['B', '08', ['07', '08', '09'], 'Judgement'];
                case 'ONS': return ['B', '09', ['01', '02', '03'], 'Onslaught'];
                case 'LGN': return ['B', '09', ['04', '05', '06'], 'Legions'];
                case 'SCG': return ['B', '09', ['07', '08', '09'], 'Scourge'];
                case 'MRD': return ['B', '10', ['01', '02', '03'], 'Mirrodin'];
                case 'DST': return ['B', '10', ['04', '05', '06'], 'Darksteel'];
                case '5DN': return ['B', '10', ['07', '08', '09'], 'Fifth Dawn'];
                case 'CHK': return ['B', '11', ['01', '02', '03'], 'Champions of Kamigawa'];
                case 'BOK': return ['B', '11', ['04', '05', '06'], 'Betrayers of Kamigawa'];
                case 'SOK': return ['B', '11', ['07', '08', '09'], 'Saviors of Kamigawa'];
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
                case 'HOP': return ['C', '01', ['01', '02', '03'], 'Planechase'];
                case 'ARC': return ['C', '02', ['01', '02', '03'], 'Archenemy'];
                case 'CMD': return ['C', '03', ['01', '02', '03', '04'], 'Commander'];
                case 'PC2': return ['C', '04', ['01', '02', '03', '04'], 'Planechase 2012'];
                case 'CM1': return ['C', '05', ['01', '02', '03', '04'], "Commander's Arsenal"];
                case 'C13': return ['C', '06', ['01', '02', '03', '04'], 'Commander 2013'];
                case 'C14': return ['C', '07', ['01', '02', '03', '04'], 'Commander 2014'];
                case 'C15': return ['C', '08', ['01', '02', '03', '04'], 'Commander 2015'];
                case 'MMA': return ['D', '08', ['01', '02', '03', '04'], 'Modern Masters'];
                case 'MM2': return ['D', '09', ['01', '02', '03', '04'], 'Modern Masters 2015'];
                case 'PO':  return ['E', '01', ['01', '02', '03'], 'Portal'];
                case 'P2':  return ['E', '02', ['01', '02', '03'], 'Portal Second Age'];
                case 'PK':  return ['E', '03', ['01', '02', '03'], 'Portal Three Kingdoms'];
                case 'DDA': return ['F', '01', ['01', '02', '03'], 'Elves vs Goblins'];
                case 'DDB': return ['F', '02', ['01', '02', '03', '04'], 'Jace vs Chandra'];
                case 'DDC': return ['F', '03', ['01', '02', '03', '04'], 'Divine vs Demonic'];
                case 'DDD': return ['F', '04', ['01', '02', '03', '04'], 'Garruk vs Liliana'];
                case 'DDE': return ['F', '05', ['01', '02', '03', '04'], 'Phyrexia vs The Coalition'];
                case 'DDF': return ['F', '06', ['01', '02', '03', '04'], 'Elspeth vs Tezzeret'];
                case 'DDG': return ['F', '07', ['01', '02', '03', '04'], 'Knights vs Dragons'];
                case 'DDH': return ['F', '08', ['01', '02', '03', '04'], 'Ajani vs Nicol Bolas'];
                case 'DDI': return ['F', '09', ['01', '02', '03', '04'], 'Venser vs Koth'];
                case 'DDJ': return ['F', '10', ['01', '02', '03', '04'], 'Izzet vs Golgari'];
                case 'DDK': return ['F', '11', ['01', '02', '03', '04'], 'Sorin vs Tibalt'];
                case 'DDL': return ['F', '12', ['01', '02', '03', '04'], 'Heroes vs Monsters'];
                case 'DDM': return ['F', '13', ['01', '02', '03', '04'], 'Jace vs Vraska'];
                case 'DRB': return ['G', '01', ['01', '02', '03', '04'], 'From the Vault Dragons'];
                case 'V09': return ['G', '02', ['01', '02', '03', '04'], 'From the Vault Exiled'];
                case 'V10': return ['G', '03', ['01', '02', '03', '04'], 'From the Vault Relics'];
                case 'V11': return ['G', '04', ['01', '02', '03', '04'], 'From the Vault Legends'];
                case 'V12': return ['G', '05', ['01', '02', '03', '04'], 'From the Vault Realms'];
                case 'V13': return ['G', '06', ['01', '02', '03', '04'], 'From the Vault Twenty'];
                case 'V14': return ['G', '07', ['01', '02', '03', '04'], 'From the Vault Annihilation'];
                case 'V15': return ['G', '08', ['01', '02', '03', '04'], 'From the Vault Angels'];
                case 'H09': return ['H', '01', ['01', '02', '03', '04'], 'Premium Deck Series Slivers'];
                case 'PD2': return ['H', '02', ['01', '02', '03', '04'], 'Premium Deck Series Fire & Lightning'];
                case 'PD3': return ['H', '03', ['01', '02', '03', '04'], 'Premium Deck Series Graveborn'];
                case 'UG':  return ['K', '01', ['01', '02', '03'], 'Unglued'];
                case 'UNH': return ['K', '02', ['01', '02', '03'], 'Unhinged'];
            }
        };

        var getFolder = function(group, block) {
            switch (group + block) {
                case 'A01': return 'A - Core Sets/A01 - Pre 6th Fake Symbols';
                case 'A02': return 'A - Core Sets/A02 - 6th to 10th editions';
                case 'A03': return 'A - Core Sets/A03 - Magic 20xx';
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
                case 'D08': return 'D - Reprint, Starter and Other Box Sets/D08 - Modern Masters';
                case 'D09': return 'D - Reprint, Starter and Other Box Sets/D09 - Modern Masters 2015';
                case 'E01': return 'E - Portal Sets/E01 - Portal';
                case 'E02': return 'E - Portal Sets/E02 - Portal Second Age';
                case 'E03': return 'E - Portal Sets/E03 - Portal Three Kingdoms';
                case 'F01': return 'F - Duel Decks/F01 - Elves vs. Goblins';
                case 'F02': return 'F - Duel Decks/F02 - Jace vs. Chandra';
                case 'F03': return 'F - Duel Decks/F03 - Divine vs. Demonic';
                case 'F04': return 'F - Duel Decks/F04 - Garruk vs. Liliana';
                case 'F05': return 'F - Duel Decks/F05 - Phyrexia vs. The Coalition';
                case 'F06': return 'F - Duel Decks/F06 - Elspeth vs. Tezzeret';
                case 'F07': return 'F - Duel Decks/F07 - Knights vs. Dragons';
                case 'F08': return 'F - Duel Decks/F08 - Ajani vs. Nicol Bolas';
                case 'F09': return 'F - Duel Decks/F09 - Venser vs. Koth';
                case 'F10': return 'F - Duel Decks/F10 - Izzet vs. Golgari';
                case 'F11': return 'F - Duel Decks/F11 - Sorin vs. Tibalt';
                case 'F12': return 'F - Duel Decks/F12 - Heroes vs. Monsters';
                case 'F13': return 'F - Duel Decks/F13 - Jace vs. Vraska';
                case 'G01': return 'G - From the Vault/G01 - Dragons';
                case 'G02': return 'G - From the Vault/G02 - Exiled';
                case 'G03': return 'G - From the Vault/G03 - Relics';
                case 'G04': return 'G - From the Vault/G04 - Legends';
                case 'G05': return 'G - From the Vault/G05 - Realms';
                case 'G06': return 'G - From the Vault/G06 - Twenty';
                case 'G07': return 'G - From the Vault/G07 - Annihilation';
                case 'G08': return 'G - From the Vault/G08 - Angels';
                case 'H01': return 'H - Premium Deck Series/H01 - Slivers';
                case 'H02': return 'H - Premium Deck Series/H02 - Fire and Lightning';
                case 'H03': return 'H - Premium Deck Series/H03 - Graveborn';
                case 'K01': return 'K - Un-Sets/K01 - Unglued';
                case 'K02': return 'K - Un-Sets/K02 - Unhinged';
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

            rarity = rarity || 'Common';
            var i = rarities.indexOf(rarity);
            var folder = getFolder(prefixes[0], prefixes[1]);
            if (prefixes[0] == 'C') prefixes[0] = 'D'; // naming error in image library :(

            var names = {
                folder: folder,
                file:  prefixes[0] + prefixes[1] + prefixes[2][0] + ' - ' + prefixes[3] + ' - Common.svg'
            };
            if (prefixes[2][i]) {
                names.commonfile = names.file;
                names.file = prefixes[0] + prefixes[1] + prefixes[2][i] + ' - ' + prefixes[3] + ' - ' + rarity + '.svg';
            }
            return names;
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
                var image = '<img class="set" src="img/symbols/set/' + names.folder + '/' + names.file + '" onerror="' + onerror + '">';
                var img = element.append(image);
            }
        };
    });
