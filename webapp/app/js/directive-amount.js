angular.module('card-app')
    .directive('amount', function() {
        return {
            restrict: 'E',
            scope: {'value': '='},
            template: '<select ng-model="value.amounttype" style="height: 20px;"><option ng-repeat="o in options" value="{{o}}">{{o}}</option></select>' +
                '<input type="number" ng-model="value.amount" style="height: 14px; width: 40px;">',
            link: function(scope) {
                scope.options = ['>', '>=', '=', '<', '<='];
                scope.value.amounttype = scope.options[0];
            }
        };
    });
