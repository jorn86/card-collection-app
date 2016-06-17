angular.module('card-app', ['ui.router', 'LiveSearch', 'a8m.group-by', 'a8m.to-array', 'directive.g+signin', 'ngDialog',
        'draganddrop', 'bw.paging'])
    //.config(function ($locationProvider) {
    //    $locationProvider.html5Mode(true);
    //})
    .config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');

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

            .state('app.helper', {
                template: "<ui-view></ui-view>"
            })
            .state('app.helper.basictypeland', {
                url: 'helper/basictypeland',
                controller: 'DeckHelperBasicTypeLandController',
                templateUrl: 'partials/helper/mana.html'
            })
            .state('app.helper.manland', {
                url: 'helper/manland',
                controller: 'DeckHelperManLandController',
                templateUrl: 'partials/helper/mana.html'
            })
            .state('app.helper.anycolorland', {
                url: 'helper/basictypeland',
                controller: 'DeckHelperAnyColorLandController',
                templateUrl: 'partials/helper/mana.html'
            })
            .state('app.helper.colorlessfix', {
                url: 'helper/colorlessfix',
                controller: 'DeckHelperColorlessFixController',
                templateUrl: 'partials/helper/mana.html'
            })
            .state('app.helper.nonbasicsearch', {
                url: 'helper/nonbasicsearch',
                controller: 'DeckHelperNonbasicSearchController',
                templateUrl: 'partials/helper/mana.html'
            })
            .state('app.helper.utilityland', {
                url: 'helper/utilityland',
                controller: 'DeckHelperUtilityLandController',
                templateUrl: 'partials/helper/utilityland.html'
            })
            .state('app.helper.sweepers', {
                url: 'helper/sweepers',
                templateUrl: 'partials/helper/sweepers.html'
            })
            .state('app.helper.commander', {
                url: 'helper/commander',
                templateUrl: 'partials/helper/commander.html'
            })
            .state('app.search', {
                url: 'search',
                controller: 'SearchController',
                templateUrl: 'partials/search.html'
            })
            .state('app.searchresults', {
                url: 'searchresults/:query',
                controller: 'SearchResultsController',
                templateUrl: 'partials/searchresults.html'
            })
            .state('app.sets', {
                url: 'sets',
                templateUrl: 'partials/setstatistics.html'
            })
            .state('app.formats', {
                url: 'formats',
                templateUrl: 'partials/formats.html'
            });
    });
