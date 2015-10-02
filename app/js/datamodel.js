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

    this.updateAmount = function(name, amount) {
        return $http.put('content/inventory.json', {name: name, amount: amount});
    };

    this.authenticateUser = function(user) {
        return $http.post('login.json', user);
    };
};
