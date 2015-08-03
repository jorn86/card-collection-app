var Datamodel = function Datamodel($http) {
    var self = this;

    this.getDecks = function() {
        return $http.get('content/list.json');
    };

    this.getDeck = function(id) {
        return $http.get('content/deck.json');
    }
};