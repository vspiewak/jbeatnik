'use strict';
/* Services */
// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('myApp.services', [])

    .value('version', '0.1')

    .factory('InfosService', ['$resource',
        function ($resource) {
            return $resource('manage/info', {}, {
                'get': { method: 'GET' }
            });
        }
    ])

    .factory('ConfigurationPropertiesService', ['$resource',
        function ($resource) {
            return $resource('manage/configprops', {}, {
                'get': { method: 'GET' }
            });
        }
    ])

    .factory('LostPasswordService', ['$resource',
        function ($resource) {
            return $resource('api/lostpassword', {}, {
                'do': { method: 'POST' }
            });
        }
    ])

    .factory('ResetPasswordService', ['$resource',
        function ($resource) {
            return $resource('api/resetpassword', {}, {
                'reset': { method: 'POST' }
            });
        }
    ])

    .factory('RegisterService', ['$resource',
        function ($resource) {
            return $resource('api/register', {}, {
            });
        }
    ])

    .factory('ActivateService', ['$resource',
        function ($resource) {
            return $resource('api/activate', {}, {
                'get': { method: 'GET' }
            });
        }
    ])

    .factory('Profile', ['$resource',
        function ($resource) {
            return $resource('api/profile', {}, {
                'get': { method: 'GET' }
            });
        }
    ])

    .factory('Session', ['localStorageService',

        function(localStorageService) {

            var KEY = 'session';
            var service = {};
            var session = null;

            service.get = function() {

                if (session == null) {
                    session = localStorageService.get(KEY);
                }

                return session;
            };

            service.create = function(username, email, roles) {

                session = {};
                session.username = username;
                session.email = email;
                session.roles = roles;

                localStorageService.set(KEY, session);

            };

            service.destroy = function() {
                session = null;
                localStorageService.remove(KEY);
            };

            return service;
        }

    ])

    .factory('AccessToken', ['localStorageService',
        function(localStorageService) {

            var TOKEN = 'token';
            var service = {};
            var token = null;

            service.get = function() {

                if (token == null) {
                    token = localStorageService.get(TOKEN);
                }

                if(token != null) {
                    return token.access_token;
                }

                return null;
            };

            service.set = function(oauthResponse) {
                token = {};

                token.access_token = oauthResponse.access_token;
                setExpiresAt(oauthResponse);
                localStorageService.set(TOKEN, token);
                return token
            };

            service.destroy = function() {
                token = null;
                localStorageService.remove(TOKEN);
            };

            service.expired = function() {
                return (token && token.expires_at && token.expires_at < new Date().getTime())
            };

            var setExpiresAt = function(oauthResponse) {
                if (token) {
                    var now = new Date();
                    var minutes = parseInt(oauthResponse.expires_in) / 60;
                    token.expires_at = new Date(now.getTime() + minutes*60000).getTime()
                }
            };

            return service;
        }
    ])

    .factory('AuthenticationService', ['$rootScope', '$http', 'authService', 'USER_ROLES', 'Profile', 'Session', 'AccessToken', 'Base64Service',
        function ($rootScope, $http, authService, USER_ROLES, Profile, Session, AccessToken, Base64Service) {

            var allowedByAllUsers = function(authorizedRoles) {

                if (!angular.isArray(authorizedRoles)) {
                    authorizedRoles = [authorizedRoles];
                }

                return authorizedRoles.indexOf(USER_ROLES.all) !== -1;

            };

            return {

                login: function (param) {
                    var data = "username=" + param.username + "&password=" + param.password + "&grant_type=password";
                    return $http.post('oauth/token', data, {
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded",
                            "Accept": "application/json",
                            "Authorization": "Basic " + Base64Service.encode("jbeatnikapp" + ':' + "myOAuthSecret")
                        },
                        ignoreAuthModule: 'ignoreAuthModule'
                    }).success(function (data) {

                        AccessToken.set(data);
                        $http.defaults.headers.common['Authorization'] = 'Bearer ' + AccessToken.get();

                        var updateConfig = function(config) {
                            config.headers['Authorization'] = 'Bearer ' + AccessToken.get();
                            return config;
                        };

                        Profile.get(function(data) {

                            var roles = $.map(data.authorities, function(o){ return o.name });
                            Session.create(data.username, data.email, roles);

                            authService.loginConfirmed(data, updateConfig);

                        });

                    }).error(function (data, status, headers, config) {
                        Session.destroy();
                        AccessToken.destroy();
                    });
                },

                isAuthenticated: function () {
                    return !!AccessToken.get() && !AccessToken.expired();
                },

                isAuthorized: function (authorizedRoles) {

                    var isAuthorized = false;

                    if (!angular.isArray(authorizedRoles)) {
                       authorizedRoles = [authorizedRoles];
                    }

                    angular.forEach(authorizedRoles, function(authorizedRole) {

                        if(authorizedRole == USER_ROLES.all) {
                            isAuthorized = true;
                            return; // no need to go further
                        }

                        if(!!Session.get() && Session.get().roles.indexOf(authorizedRole) !== -1) {
                            isAuthorized = true;
                            return; // no need to go further
                        }

                    });

                    return isAuthorized;
                },

                valid: function(authorizedRoles) {

                    // needed in case of refresh
                    if(AccessToken.get() !== null) {
                        $http.defaults.headers.common['Authorization'] = 'Bearer ' + AccessToken.get();
                    }

                    if(allowedByAllUsers(authorizedRoles)) {
                        return;
                    }

                    if(!this.isAuthorized(authorizedRoles)) {

                        //event.preventDefault();

                        if(this.isAuthenticated()) {
                            $rootScope.$broadcast("event:auth-notAuthorized");
                        } else {
                            $rootScope.$broadcast("event:auth-loginRequired");
                        }
                    }

                },

                destroy: function() {
                    Session.destroy();
                    AccessToken.destroy();
                },

                logout: function() {
                    $http.get('oauth/logout');
                    delete $http.defaults.headers.common['Authorization'];
                    Session.destroy();
                    AccessToken.destroy();
                    authService.loginCancelled();
                }

            };
        }
    ])

    .service('Base64Service', function () {

        var keyStr = "ABCDEFGHIJKLMNOP" +
            "QRSTUVWXYZabcdef" +
            "ghijklmnopqrstuv" +
            "wxyz0123456789+/" +
            "=";

        this.encode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }

            return output;
        };

        this.decode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            while (i < input.length) {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }
        };

    });