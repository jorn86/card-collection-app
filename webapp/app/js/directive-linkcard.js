angular.module('card-app')

    .directive('linkcard', function() {
        return {
            restrict: 'A',
            link: function($scope, element) {
                $scope.$watch(function() {
                    return element.children().length;

                }, function() {
                    $scope.$evalAsync(function() {
                        inlinemtg.linkcards($(element));
                    });
                });
            }
        };
    });
