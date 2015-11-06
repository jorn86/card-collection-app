var Datamodel = function Datamodel($http) {
    var base = "api/";

    this.getDecks = function(userId) {
        //return $http.get('content/list.json');
        return $http.get(base + 'deck/list');
    };

    this.getDeck = function(id) {
        $http.get(base + 'deck/' + id);
        return $http.get('content/decks.json');
    };

    this.getInventory = function() {
        $http.get(base + 'deck/inventory');
        return $http.get('content/inventory.json');
    };

    this.getAllSets = function() {
        return $http.get(base + 'database/sets');
    };

    this.updateAmount = function(deckId, rowId, amount) {
        return $http.put(base + 'deck/' + deckId + '/row/' + rowId, {amount: amount});
    };

    this.authenticateUser = function(user) {
        //return $http.get('content/login.json');
        return $http.post(base + 'user', user).then(function(response) {
            console.log('got user, set id', response.data.id);
            $http.defaults.headers.common['X-UserId'] = response.data.id;
            return response.data;
        });
    };
};
