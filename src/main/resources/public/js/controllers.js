'use strict';
/* Controllers */
angular.module('myApp.controllers', [])

    .controller('ApplicationController', ['$scope', 'InfosService', function($scope, InfosService) {

        InfosService.get(function(data) {
            $scope.infos = data;
        });

    }])

    .controller('HomeController', ['$scope', function($scope) {

    }])

    .controller('AboutController', ['$scope', function($scope) {

    }]);