angular.module('route', ['ui.router'])
    .config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/");

        $stateProvider
            .state('app', {
                templateUrl: 'partials/main.html'
            })
            .state('app.deck', {
                url: '/deck/:id',
                controller: 'DeckController',
                templateUrl: 'partials/deck.html'
            })
            .state('app.inventory', {
                url: '/inventory',
                controller: 'InventoryController',
                templateUrl: 'partials/deck.html'
            });
    });

angular.module('card-collection', [
    'route',
    'controllers'
]);
