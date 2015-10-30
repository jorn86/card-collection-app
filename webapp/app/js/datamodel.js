var Datamodel = function Datamodel($http) {
    this.getDecks = function() {
        return $http.get('content/list.json');
        //return $http.get('decks');
    };

    this.getDeck = function(id) {
        console.log('get deck', id);
        return $http.get('content/decks.json');
        // return $http.get('deck/' + id);
    };

    this.getInventory = function() {
        return $http.get('content/inventory.json');
        // return $http.get('inventory');
    };

    this.updateAmount = function(deckId, rowId, amount) {
        console.log('update amount', deckId, rowId, amount);
        return $http.get('content/inventory.json');
        //return $http.put('deck/' + deckId, {id: rowId, amount: amount});
    };

    this.authenticateUser = function(user) {
        console.log('authenticate user', user);
        return $http.get('content/login.json');
        //return $http.post('user/login', user);
    };
};
