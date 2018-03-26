app.service('linesService', function ($http) {
    this.queryNonEmptyDays = function () {
        return $http.get('lines/days').then(
            function (daysResponse) {
                return daysResponse.data;
            },
            function (errorResponse) {
                console.log("Days info not loaded !");
                return [];
            });
    };

    this.queryLinesForADay = function ($date) {
        return $http.get('lines/' + moment($date).format('YYYY-MM-DD') + '/lines-list')
            .then(
                function (linesResponse) {
                    return linesResponse.data;
                },
                function (errorResponse) {
                    console.log("error while querying lines !");
                    return [];
                }
            );
    };

    this.queryBrigadesForLine = function ($date, $line) {
        return $http.get('lines/' + moment($date).format('YYYY-MM-DD') + '/' + $line + '/brigades')
            .then(
                function (brigadesResponse) {
                    return brigadesResponse.data;
                },
                function (errorResponse) {
                    console.log("error while querying brigades !");
                    return [];
                }
            );
    };

    this.queryPositionsForLineAndBrigade = function ($date, $line, $brigade) {
        return $http.get('lines/' + moment($date).format('YYYY-MM-DD') + '/' + $line + '/' + $brigade)
            .then(
                function (positionsResponse) {
                    return positionsResponse.data;
                },
                function (errorResponse) {
                    console.log("error while querying positions !");
                    return [];
                }
            );
    }

});