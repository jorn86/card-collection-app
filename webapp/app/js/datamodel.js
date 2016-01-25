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

    this.updateAmount = function(boardId, rowId, amount) {
        return $http.put(base + 'deck/card', {id: rowId, boardid: boardId, amount: amount});
    };

    this.addCardToDeck = function(boardId, cardId, amount) {
        return $http.post(base + 'deck/card', {boardid: boardId, cardid: cardId, amount: amount});
    };

    this.createDeck = function(name) {
        return $http.post(base + 'deck', {name: name});
    };
    this.createTag = function(name) {
        return $http.post(base + 'deck/tag', {name: name});
    };

    this.authenticateUser = function(user) {
        return $http.post(base + 'user', user).then(function(response) {
            $http.defaults.headers.common['X-UserId'] = response.data.id;
            return response.data;
        });
    };
};
