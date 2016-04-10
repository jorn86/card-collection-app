angular.module('card-app')
    .directive('amount', function() {
        return {
            restrict: 'E',
            scope: {'value': '='},
            template: '<select ng-model="value.amounttype"><option ng-repeat="o in options" value="{{o}}">{{o}}</option></select>' +
                '<input type="number" ng-model="value.amount">',
            link: function(scope) {
                scope.options = ['>', '>=', '=', '<', '<='];
                scope.value.amounttype = scope.options[0];
            }
        };
    });
