var Datamodel = function Datamodel($http) {
    this.getDecks = function() {
        return $http.get('content/list.json');
    };

    this.getDeck = function(id) {
        return $http.get('content/decks.json').then(function(result) {
            result.data = result.data[id];
            return result;
        });
    };

    this.getInventory = function() {
        return $http.get('content/inventory.json');
    };

    this.getPrice = function(name, setCode) {
        return $http.get('https://api.deckbrew.com/mtg/cards?name=' + encodeURIComponent(name)).then(function(result) {
            for (var i = 0; i < result.data[0].editions.length; i++) {
                if (result.data[0].editions[i].set_id === setCode) {
                    return result.data[0].editions[i].price;
                }
            }
            return null;
        });
    };
};
