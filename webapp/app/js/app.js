angular.module('card-app', ['ui.router', 'LiveSearch', 'a8m.group-by', 'a8m.to-array', 'directive.g+signin', 'ngDialog', 'draganddrop'])
    //.config(function ($locationProvider) {
    //    $locationProvider.html5Mode(true);
    //})
    .config(function ($stateProvider, $urlRouterProvider, $urlMatcherFactoryProvider) {
        $urlRouterProvider.otherwise('/');

        $urlMatcherFactoryProvider.type("nonURIEncoded", {
            encode: function(item) { return item || ''; },
            decode: function(item) { return item || ''; },
            is: function() { return true; }
        });

        $stateProvider
            .state('app', {
                url: '/',
                templateUrl: 'partials/main.html'
            })
            .state('app.deck', {
                url: 'deck/:id',
                controller: 'DeckController',
                templateUrl: 'partials/deck.html'
            })
            .state('app.search', {
                url: 'search',
                controller: 'SearchController',
                templateUrl: 'partials/search.html'
            })
            .state('app.searchresults', {
                url: 'searchresults/:query/{page:int}',
                controller: 'SearchResultsController',
                templateUrl: 'partials/searchresults.html'
            })
            .state('app.sets', {
                url: 'sets',
                templateUrl: 'partials/setstatistics.html'
            });
    });
