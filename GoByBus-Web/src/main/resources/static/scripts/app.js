var app = angular.module('goByBusApp', ['ngMaterial', 'ngSanitize', 'ngAnimate', 'ui.bootstrap', 'ngMap']);

app.module('ui.bootstrap.datepicker')
    .config(function ($provide) {
        $provide.decorator('datepickerDirective', function ($delegate) {
            var directive = $delegate[0];
            var link = directive.link;

            directive.compile = function () {
                return function (scope, element, attrs, ctrls) {
                    link.apply(this, arguments);

                    var datepickerCtrl = ctrls[0];
                    var ngModelCtrl = ctrls[1];

                    if (ngModelCtrl) {
                        scope.$on('refreshDatepickers', function refreshView() {
                            datepickerCtrl.refreshView();
                        });
                    }
                }
            };
            return $delegate;
        });
    });