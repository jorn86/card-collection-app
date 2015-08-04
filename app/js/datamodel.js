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
};
