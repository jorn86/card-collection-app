angular.module('card-app')
    .directive('newButtons', function($rootScope) {
        return {
            restrict: 'E',
            template: '<button ng-click="createTag()">+<img src="../img/tag.svg" height="16" alt="New tag"></button>' +
                '&nbsp;<button ng-dialog="partials/dialog/newDeck.html">+<img src="../img/deck.svg" height="16" alt="New deck"></button>',
            scope: {},
            link: function(scope) {
                scope.createTag = function() {
                    $rootScope.datamodel.createTag('New tag').then(function() {
                        $rootScope.$broadcast('reloadUserDecks');
                    });
                };
            }
        };
    })

    .controller('NewDeckController', function($scope, $rootScope, $state) {
        $scope.tags = [];
        $scope.input = {
            tagId: null,
            deckName: 'New deck'
        };
        var addTags = function(node) {
            $scope.tags.push({id: node.tagId, name: node.tagName});
            _.forEach(node.children, addTags);
        };
        $rootScope.datamodel.getDecks().then(function(result) {
            _.forEach(result.data.children, addTags);
            $scope.input.tagId = null;
        });

        $scope.createDeck = function() {
            var tag = /*$scope.input.tagId === 'nullId' ? null :*/ $scope.input.tagId;
            $rootScope.datamodel.createDeck($scope.input.deckName, tag).then(function(result) {
                $rootScope.$broadcast('reloadUserDecks');
                $state.go('app.deck', {id: result.data.id});
            });
            $scope.closeThisDialog();
        };
    });
