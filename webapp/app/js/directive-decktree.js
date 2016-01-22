angular.module('card-app')
    .directive('decktree', function(RecursionHelper) {
        return {
            restrict: 'E',
            templateUrl: 'partials/decktree.html',
            scope: {node: '='},
            compile: RecursionHelper.compile,
            controller: function ($scope) {
                $scope.$watch('node.expand', function(expanded) {
                    if (!$scope.node) return;
                    if (expanded) {
                        if ($scope.node.children.length === 1) {
                            $scope.node.children[0].expand = true;
                        }
                    }
                    else {
                        _.each($scope.node.children, function (child) { child.expand = false; });
                    }
                });
            }
        };
    });
