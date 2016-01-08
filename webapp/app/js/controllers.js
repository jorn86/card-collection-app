angular.module('card-app')

    .controller('IndexController', function ($rootScope, $scope, $http, $state) {
        $rootScope.datamodel = new Datamodel($http);
        $scope.$on('user', function(event, user) {
            $scope.user = user;
            $scope.currentUserId = user ? user.id : null;
            if (!$state.includes('app')) {
                $state.go('app');
            }
        });
    })

    .controller('DeckController', function ($scope, $stateParams) {
        $scope.datamodel.getDeck($stateParams.id).then(function (result) {
            $scope.deck = result.data;
        }, function(error) {
            if (error.status === 401) {
                //$scope.requeryOnAuth($stateParams.id);
            }
        });
    })

    .controller('InventoryController', function ($scope) {
        $scope.datamodel.getInventory().then(function (result) {
            $scope.inventory = result.data.boards[0];
        }, function(error) {
            if (error.status === 401) {
                //$scope.requeryOnAuth($stateParams.id);
            }
        });
    })

    .controller('StatisticsController', function($scope) {
        $scope.datamodel.getSetStatistics().then(function (result) {
            $scope.data = result.data;
        });

        $scope.order = 'releasedate';
        $scope.reverse = false;

        $scope.setOrder = function (field) {
            if ($scope.order === field) {
                $scope.reverse = !$scope.reverse;
            }
            else {
                $scope.order = field;
                $scope.reverse = false;
            }
        };
    });
