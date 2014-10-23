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

    .controller('LostPasswordController', ['$scope', 'LostPasswordService', function($scope, LostPasswordService) {

        $scope.lostPassword = function() {

            $scope.success = false;
            $scope.error = false;

            LostPasswordService.do($scope.lostUser,
                function (value, responseHeaders) {

                    $scope.success = true;

                },
                function (httpResponse) {

                    //TODO: different status code ?
                    //if (httpResponse.status === 400
                    //    && httpResponse.statusText === "Bad Request") {

                        $scope.error = true;

                    //}

                }
            );
        };


    }])

    .controller('ResetPasswordController', ['$scope', '$routeParams', 'ResetPasswordService', function($scope, $routeParams, ResetPasswordService) {

        console.log($routeParams);
        $scope.resetUser = {};
        $scope.resetUser.email = $routeParams.email;
        $scope.resetUser.resetPasswordKey = $routeParams.key;

        $scope.resetPassword = function() {

            $scope.success = false;
            $scope.error = false;

            if($scope.resetUser.password != $scope.confirmPassword) {

                $scope.errorPasswordNotMatching = true;

            } else {
                ResetPasswordService.reset($scope.resetUser,
                    function (value, responseHeaders) {

                        $scope.success = true;

                    },
                    function (httpResponse) {

                        //TODO: different status code ?
                        //if (httpResponse.status === 400
                        //    && httpResponse.statusText === "Bad Request") {

                            $scope.error = true;

                        //}

                    }
                );
            }

        }


    }])

    .controller('RegisterController', ['$scope', 'RegisterService', function($scope, RegisterService) {

        $scope.register = function () {

            $scope.success = false;
            $scope.error = false;
            $scope.errorUsernameExist = false;
            $scope.errorEmailExist = false;
            $scope.errorPasswordNotMatching = false;

            if($scope.registerUser.password != $scope.confirmPassword) {

                $scope.errorPasswordNotMatching = true;

            } else {

                RegisterService.save($scope.registerUser,
                    function (value, responseHeaders) {

                        $scope.success = true;

                    },
                    function (httpResponse) {

                        if (httpResponse.status === 400 && httpResponse.data.code == 1) {

                            $scope.errorUsernameExist = true;

                        } else if (httpResponse.status === 400 && httpResponse.data.code == 2) {

                            $scope.errorEmailExist = true;

                        } else if (httpResponse.status === 400 && httpResponse.data.code == 3) {

                            $scope.errorUsernameExist = true;
                            $scope.errorEmailExist = true;

                        } else {

                            $scope.error = true;

                        }
                    }
                );

            }

        };

    }])

    .controller('ActivateController', function ($scope, $routeParams, ActivateService) {
        console.log($routeParams);
        ActivateService.get({email: $routeParams.email, key: $routeParams.key},
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.success = 'OK';
            },
            function (httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    })

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

    .controller('ConfigurationPropertiesController', ['$scope', 'ConfigurationPropertiesService', function($scope, ConfigurationPropertiesService) {

        ConfigurationPropertiesService.get(function(data) {

            var extractProperties = function(label, obj, result) {

                var keys = Object.keys(obj);
                angular.forEach(keys, function(key) {
                    var name = label + "." + key;
                    var value = obj[key];
                    if(angular.isObject(value)) {
                        result.concat(extractProperties(name, value, result));
                    } else {
                        result.push({ 'name':name, 'value':value });
                    }

                });

            };

            var keys = Object.keys(data);

            angular.forEach(keys, function(key) {

                if(data[key].prefix != undefined) {

                    var properties = [];
                    extractProperties(data[key].prefix, data[key].properties, properties);
                    data[key].properties = properties;

                }

            });

            $scope.configprops = data;
        });

    }])

    .controller('AboutController', ['$scope', function($scope) {

    }]);