'use strict';
/* Directives */
angular.module('myApp.directives', [])

    .directive('isActiveNav', [ '$location', function ($location) {
        return {
            restrict: 'A',
            link: function (scope, element) {
                scope.location = $location;
                scope.$watch('location.path()', function (currentPath) {
                    if ('#' + currentPath === element[0].attributes['href'].value) {
                        element.parent().addClass('active');
                    } else {
                        element.parent().removeClass('active');
                    }
                });
            }
        };
    }])

    .directive('appVersion', ['version', function(version) {
    return function(scope, elm, attrs) {
        elm.text(version);
    };

}]);