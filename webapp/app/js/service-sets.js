angular.module('card-app')
    .service('sets', function($rootScope) {
        this.sets = $rootScope.datamodel.getSetStatistics();
    });
