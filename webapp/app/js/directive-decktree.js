angular.module('card-app')
    .directive('decktree', function(RecursionHelper) {
        return {
            restrict: 'E',
            templateUrl: 'partials/decktree.html',
            scope: {node: '=', readonly: '='},
            compile: RecursionHelper.compile,
            controller: function ($scope, $rootScope) {
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
                $scope.onDrop = function(data, event) {
                    $scope.updateDeckParent(data['json/deckid'].deck);
                };
                $scope.updateDeckParent = function(deckId) {
                    $rootScope.datamodel.updateDeckTags(deckId, $scope.node.tagId).then(function(result) {
                        $rootScope.$broadcast('reloadUserDecks', {deck: result.data.id});
                    });
                };
            }
        };
    });
