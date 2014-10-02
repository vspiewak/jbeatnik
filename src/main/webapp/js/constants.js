'use strict';
/* Constants */
angular.module('myApp.constants', [])

    .constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        user: 'ROLE_USER'
    });