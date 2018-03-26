app.controller('lineController', function ($scope, $rootScope, linesService) {
    $scope.selections = {
        date: new Date(),
        days: {
            all: []
        },
        brigades: {
            all: [],
            selected: null
        },
        lines: {
            all: [],
            selected: null
        }
    };

    $scope.datePopup = {
        opened: false,
        disabled: false,
        open: function () {
            $scope.datePopup.opened = true;
        },
        options: {
            customClass: getDayClass,
            showWeeks: false
        }
    };

    $scope.spinner = {
        active: true
    };

    linesService.queryNonEmptyDays().then(function (response) {
        $scope.selections.days.all = response;
        $scope.datePopup.disabled = false;
        $scope.spinner.active = false;
        $scope.datePopup.options.customClass = getDayClass;
        $scope.updateLinesForDay();
    });

    $scope.updateLinesForDay = function () {
        $scope.selections.brigades.all = [];
        $scope.selections.brigades.selected = null;
        $scope.selections.lines.selected = null;

        linesService.queryLinesForADay($scope.selections.date)
            .then(function (response) {
                console.log(response);
                $scope.selections.lines.all = response;
            });
    };

    $scope.updateBrigadesForLine = function () {
        $scope.selections.brigades.selected = null;

        linesService.queryBrigadesForLine($scope.selections.date,
                                          $scope.selections.lines.selected)
            .then(function (response) {
                console.log(response);
                $scope.selections.brigades.all = response;
            });
        //$scope.update_map();
    };

    $scope.updateMap = function () {
        linesService.queryPositionsForLineAndBrigade($scope.selections.date,
                                                     $scope.selections.lines.selected,
                                                     $scope.selections.brigades.selected)
            .then(function (response) {
                console.log(response);
                var markers = response.map($rootScope.createMarker);
                $rootScope.addPositionMarkers(markers)
            });
    };

    function getDayClass(data) {
        var date = moment(data.date).format('YYYY-MM-DD'),
            mode = data.mode;
        if (mode === 'day') {
            if ($scope.selections.days.all.includes(date)) {
                return 'full';
            }
        }
        return '';
    }
});

