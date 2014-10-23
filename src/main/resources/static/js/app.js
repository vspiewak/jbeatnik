'use strict';
// Declare app level module which depends on filters, and services
angular.module('myApp', [
    'ngResource',
    'ngRoute',
    'http-auth-interceptor',
    'LocalStorageModule',
    'myApp.filters',
    'myApp.services',
    'myApp.directives',
    'myApp.controllers',
    'myApp.constants'
])
    .config(['$routeProvider', 'USER_ROLES', function($routeProvider, USER_ROLES) {
        $routeProvider

            .when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/register', {
                templateUrl: 'views/register.html',
                controller: 'RegisterController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/activate', {
                templateUrl: 'views/activate.html',
                controller: 'ActivateController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/lostpassword', {
                templateUrl: 'views/lostpassword.html',
                controller: 'LostPasswordController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/resetpassword', {
                templateUrl: 'views/resetpassword.html',
                controller: 'ResetPasswordController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/profile', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileController',
                access: {
                    authorizedRoles: [USER_ROLES.user]
                }
            })

            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .when('/configprops', {
                templateUrl: 'views/configprops.html',
                controller: 'ConfigurationPropertiesController',
                access: {
                    authorizedRoles: [USER_ROLES.admin]
                }
            })

            .when('/error', {
                templateUrl: 'views/error.html',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })

            .otherwise({
                redirectTo: '/home',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            });
        }
    ])

    .run(['$rootScope', '$location', '$http', 'AuthenticationService',
        function($rootScope, $location, $http, AuthenticationService) {
            $rootScope.$on('$routeChangeStart', function (event, next) {
                console.log('$routeChangeStart: ', $location.path());
                AuthenticationService.valid(next.access.authorizedRoles);
            });

            // Call when the the client is confirmed
            $rootScope.$on('event:auth-loginConfirmed', function(data) {
                console.log('event:auth-loginConfirmed');
                if ($location.path() === "/home" || $location.path() === "/login") {
                    $location.path('/profile').replace();
                }
            });

            // Call when the 401 response is returned by the server
            $rootScope.$on('event:auth-loginRequired', function(rejection) {
                console.log('event:auth-loginRequired');
                AuthenticationService.destroy();
                $location.path('/login').replace();
            });

            // Call when the 403 response is returned by the server
            $rootScope.$on('event:auth-notAuthorized', function(rejection) {
                console.log('event:auth-notAuthorized');
                $location.path('/error').replace();
            });

            // Call when the user logs out
            $rootScope.$on('event:auth-loginCancelled', function() {
                console.log('event:auth-loginCancelled');
                $location.path('/home').replace();
            });
        }
    ]);
