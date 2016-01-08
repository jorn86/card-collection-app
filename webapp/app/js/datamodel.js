var Datamodel = function Datamodel($http) {
    var base = "api/";

    this.getDecks = function() {
        return $http.get(base + 'deck/list');
    };

    this.getPreconstructedDecks = function() {
        return $http.get(base + 'deck/preconstructedlist');
    };

    this.getDeck = function(id) {
        return $http.get(base + 'deck/' + id);
    };

    this.getInventory = function() {
        $http.get(base + 'deck/inventory');
        return $http.get('content/inventory.json');
    };

    this.searchCardsByName = function(name) {
        return $http.get(base + 'database/search/?name=' + name);
    };

    this.getSetStatistics = function() {
        return $http.get(base + 'database/setstatistics');
    };

    this.updateAmount = function(deckId, rowId, amount) {
        return $http.put(base + 'deck/card', {id: rowId, deckid: deckId, amount: amount});
    };

    this.addCardToDeck = function(deckId, cardId, amount) {
        return $http.post(base + 'deck/card', {deckid: deckId, cardid: cardId, amount: amount});
    };

    this.authenticateUser = function(user) {
        return $http.post(base + 'user', user).then(function(response) {
            $http.defaults.headers.common['X-UserId'] = response.data.id;
            return response.data;
        });
    };
};
