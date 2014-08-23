'use strict';
/* Controllers */
angular.module('myApp.controllers', [])

    .controller('ApplicationController', ['$scope', 'USER_ROLES', 'AuthenticationService', 'Session', 'InfosService',
        function($scope, USER_ROLES, AuthenticationService, Session, InfosService) {

        $scope.roles = USER_ROLES;
        $scope.session = Session.get;
        $scope.isAuthorized = AuthenticationService.isAuthorized;
        $scope.isAuthenticated = AuthenticationService.isAuthenticated;
        $scope.logout = AuthenticationService.logout;

        InfosService.get(function(data) {
            $scope.infos = data;
        });

    }])

    .controller('HomeController', ['$scope', function($scope) {

    }])

    .controller('RegisterController', ['$scope', 'RegisterService', function($scope, RegisterService) {

        $scope.register = function () {

            console.log('register...');
            console.log($scope.registerUser);

            $scope.success = false;
            $scope.error = false;
            $scope.errorUsernameExist = false;
            $scope.errorPasswordNotMatching = false;

            if($scope.registerUser.password != $scope.confirmPassword) {

                $scope.errorPasswordNotMatching = true;

            } else {

                RegisterService.save($scope.registerUser,
                    function (value, responseHeaders) {

                        $scope.success = true;

                    },
                    function (httpResponse) {
                        console.log('error', httpResponse);
                        if (httpResponse.status === 304
                            && httpResponse.statusText === "Not Modified") {

                            $scope.errorUsernameExist = true;

                        } else {

                            $scope.error = true;

                        }
                    }
                );

            }

        };

    }])

    .controller('LoginController', ['$scope', 'AuthenticationService', 'Session', function($scope, AuthenticationService, Session) {

        $scope.login = function () {

            $scope.authenticationError = false;

            AuthenticationService.login({
                username: $scope.username,
                password: $scope.password
            }).error(function(data) {

                $scope.authenticationError = true;

            });
        };

    }])

    .controller('ProfileController', ['$scope', function($scope) {

    }])

    .controller('AboutController', ['$scope', function($scope) {

    }]);