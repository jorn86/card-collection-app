angular.module('card-app')
    .directive('amount', function() {
        return {
            restrict: 'E',
            scope: {'type': '=', 'value': '='},
            template: '<input type="number" ng-model="value" style="float: right; height: 14px; width: 40px;">' +
                '<select ng-model="type" style="float: right; height: 20px;"><option ng-repeat="o in options" value="{{o}}">{{o}}</option></select>',
            link: function(scope) {
                scope.options = ['>', '>=', '=', '<', '<='];
                scope.type = scope.options[0];
            }
        };
    });
