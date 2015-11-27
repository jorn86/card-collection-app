angular.module('card-app')
    .directive('setimage', function() {
        var getSetClass = function(setcode) {
            switch(setcode) {
                case '1E':  return 'alpha';
                case '2E':  return 'beta';
                case '2U':  return 'unlimited';
                case '3E':  return 'revised';
                case '4E':  return 'fourth';
                case '5E':  return 'fifth';
                case '6E':  return 'sixth';
                case '7E':  return 'seventh';
                case '8ED': return 'eighth';
                case '9ED': return 'ninth';
                case '10E': return 'tenth';
                case 'M10': return 'm10';
                case 'M11': return 'm11';
                case 'M12': return 'm12';
                case 'M13': return 'm13';
                case 'M14': return 'm14';
                case 'M15': return 'm15';
                case 'ORI': return 'magic-origins';
                case 'AN':  return 'arabian-nights';
                case 'AQ':  return 'antiquities';
                case 'LE':  return 'legends';
                case 'DK':  return 'the-dark';
                case 'FE':  return 'fallen-empires';
                case 'HM':  return 'homelands';
                case 'IA':  return 'ice-age';
                case 'AL':  return 'alliances';
                case 'CSP': return 'coldsnap';
                case 'MI':  return 'mirage';
                case 'VI':  return 'visions';
                case 'WL':  return 'weatherlight';
                case 'TE':  return 'tempest';
                case 'ST':  return 'stronghold';
                case 'EX':  return 'exodus';
                case 'UZ':  return 'urzas-saga';
                case 'GU':  return 'urzas-legacy';
                case 'CG':  return 'urzas-destiny';
                case 'MM':  return 'mercadian-masques';
                case 'NE':  return 'nemesis';
                case 'PR':  return 'prophecy';
                case 'IN':  return 'invasion';
                case 'PS':  return 'planeshift';
                case 'AP':  return 'apocalypse';
                case 'OD':  return 'odyssey';
                case 'TOR': return 'torment';
                case 'JUD': return 'judgment';
                case 'ONS': return 'onslaught';
                case 'LGN': return 'legions';
                case 'SCG': return 'scourge';
                case 'MRD': return 'mirrodin';
                case 'DST': return 'darksteel';
                case '5DN': return 'fifth-dawn';
                case 'CHK': return 'champions-of-kamigawa';
                case 'BOK': return 'betrayers-of-kamigawa';
                case 'SOK': return 'saviors-of-kamigawa';
                case 'RAV': return 'ravnica';
                case 'GPT': return 'guildpact';
                case 'DIS': return 'dissension';
                case 'TSP': return 'time-spiral';
                case 'PLC': return 'planar-chaos';
                case 'FUT': return 'future-sight';
                case 'LRW': return 'lorwyn';
                case 'MOR': return 'morningtide';
                case 'SHM': return 'shadowmoor';
                case 'EVE': return 'eventide';
                case 'ALA': return 'shards-of-alara';
                case 'CON': return 'conflux';
                case 'ARB': return 'alara-reborn';
                case 'ZEN': return 'zendikar';
                case 'WWK': return 'worldwake';
                case 'ROE': return 'rise-of-the-eldrazi';
                case 'SOM': return 'scars-of-mirrodin';
                case 'MBS': return 'mirrodin-besieged';
                case 'NPH': return 'new-phyrexia';
                case 'ISD': return 'innistrad';
                case 'DKA': return 'dark-ascension';
                case 'AVR': return 'avacyn-restored';
                case 'RTR': return 'return-to-ravnica';
                case 'GTC': return 'gatecrash';
                case 'DGM': return 'dragons-maze';
                case 'THS': return 'theros';
                case 'BNG': return 'born-of-the-gods';
                case 'JOU': return 'journey-into-nyx';
                case 'KTK': return 'khans-of-tarkir';
                case 'FRF': return 'fate-reforged';
                case 'DTK': return 'dragons-of-tarkir';
                case 'BFZ': return 'battle-for-zendikar';
                case 'OGW': return 'oath-of-the-gatewatch';
                case 'EXP': return 'zendikar-expeditions';
                case 'HOP': return 'planechase';
                case 'PC2': return 'planechase-2012';
                case 'ARC': return 'archenemy';
                case 'CMD': return 'commander';
                case 'CM1': return 'commanders-arsenal';
                case 'C13': return 'commander-2013';
                case 'C14': return 'commander-2014';
                case 'C15': return 'commander-2015';
                case 'MMA': return 'modern-masters';
                case 'MM2': return 'modern-masters-2015';
                case 'PO':  return 'portal';
                case 'P2':  return 'portal-second-age';
                case 'PK':  return 'portal-three-kingdoms';
                case 'EVG': return 'elves-vs-goblins';
                case 'DD2': return 'jace-vs-chandra';
                case 'DDC': return 'divine-vs-demonic';
                case 'DDD': return 'garruk-vs-liliana';
                case 'DDE': return 'phyrexia-vs-the-coalition';
                case 'DDF': return 'elspeth-vs-tezzeret';
                case 'DDG': return 'knights-vs-dragons';
                case 'DDH': return 'ajani-vs-nicol-bolas';
                case 'DDI': return 'venser-vs-koth';
                case 'DDJ': return 'izzet-vs-golgari';
                case 'DDK': return 'sorin-vs-tibalt';
                case 'DDL': return 'heroes-vs-monsters';
                case 'DDM': return 'jace-vs-vraska';
                case 'DDN': return 'speed-vs-cunning';
                case 'DDO': return 'elspeth-vs-kiora';
                case 'DDP': return 'zendikar-vs-eldrazi';
                case 'DRB': return 'from-the-vault-dragons';
                case 'V09': return 'from-the-vault-exiled';
                case 'V10': return 'from-the-vault-relics';
                case 'V11': return 'from-the-vault-legends';
                case 'V12': return 'from-the-vault-realms';
                case 'V13': return 'from-the-vault-twenty';
                case 'V14': return 'from-the-vault-annihilation';
                case 'V15': return 'from-the-vault-angels';
                case 'H09': return 'premium-deck-series-slivers';
                case 'PD2': return 'premium-deck-series-fire-lightning';
                case 'PD3': return 'premium-deck-series-graveborn';
                case 'UG':  return 'unglued';
                case 'UNH': return 'unhinged';
                case 'CH':  return 'chronicles';
                case 'P3':  return 'starter-1999';
                case 'P4':  return 'starter-2000';
                case 'DKM': return 'deckmasters';
                case 'CST': return 'coldsnap'; // Coldsnap Theme Decks
                case 'TSB': return 'time-spiral'; // Timeshifted
                case 'MD1': return 'modern-event-deck-2014';
                case 'ATH':  return 'anthologies';
                case 'BD':  return 'beatdown';
                case 'BR':  return 'battle-royale';
                case 'DPA': return 'duels-of-the-planewalkers';
                case 'CNS': return 'conspiracy';
                case 'MED': return 'masters-edition-1';
                case 'ME2': return 'masters-edition-2';
                case 'ME3': return 'masters-edition-3';
                case 'ME4': return 'masters-edition-4';
                case 'VMA': return 'vintage-masters';
                case 'TPR': return 'tempest-remastered';
                case 'DD3_DVD': return 'divine-vs-demonic'; // Reprint
                case 'DD3_EVG': return 'elves-vs-goblins'; // Reprint
                case 'DD3_GVL': return 'garruk-vs-liliana'; // Reprint
                case 'DD3_JVC': return 'jace-vs-vraska'; // Reprint
                case 'FRF_UGIN': return 'fate-reforged';
            }
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
            template: '<i class="set mtg {{setClass}} {{rarityClass}}">',
            link: function($scope) {
                $scope.setClass = getSetClass($scope.set) || 'promo-2';
                $scope.rarityClass = getRarityClass($scope.rarity) || '';
            }
        };
    });
