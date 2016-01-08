angular.module('card-app', ['ui.router', 'LiveSearch', 'a8m.group-by', 'a8m.to-array', 'directive.g+signin'])
    //.config(function ($locationProvider) {
    //    $locationProvider.html5Mode(true);
    //})
    .config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');

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
            })
            .state('app.versioncheck', {
                url: '/versioncheck',
                templateUrl: 'partials/versioncheck.html'
            })
            .state('app.sets', {
                url: '/sets',
                templateUrl: 'partials/setstatistics.html'
            });
    });
