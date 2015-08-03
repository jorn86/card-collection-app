angular.module('route', [ 'ui.router' ])
    .config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/");

        $stateProvider.state('deck', {
            url: '/deck/:id',
            controller: 'DeckController',
            templateUrl: 'partials/deck.html'
        });
    });

angular.module('card-collection',[
    'route',
    'controllers'
]);
