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

    this.getBoard = function(id) {
        return $http.get(base + 'deck/board/' + id);
    };

    this.getOtherBoards = function(boardId) {
        return $http.get(base + 'deck/otherboards/' + boardId);
    };

    this.updateBoard = function(id, board) {
        return $http.put(base + 'deck/board/' + id, board);
    };

    this.mergeBoard = function(sourceId, targetId) {
        return $http.post(base + 'deck/board/merge/' + sourceId + '/' + targetId);
    };

    this.deleteBoard = function(id) {
        return $http.delete(base + 'deck/board/' + id);
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

    this.createDeck = function(name, tagId) {
        return $http.post(base + 'deck', {name: name, tags: tagId ? [tagId] : []});
    };

    this.createTag = function(name, parentTagId) {
        return $http.post(base + 'deck/tag', {name: name, parentid: parentTagId});
    };

    this.authenticateUser = function(user) {
        return $http.post(base + 'user', user).then(function(response) {
            $http.defaults.headers.common['X-UserId'] = response.data.id;
            return response.data;
        });
    };

    this.uploadDeckboxImport = function(deckId, file) {
        return $http.post(base + 'deck/' + deckId + '/deckboximport', file, { headers: {'Content-Type': 'text/csv'}});
    };

    this.updateDeckTags = function(deckId, tagId) {
        return $http.put(base + 'deck/' + deckId, {tags: tagId ? [tagId] : []});
    };

    this.getSearchResults = function(query) {
        return $http.get(base + 'search?query=' + encodeURIComponent(query));
    }
};
