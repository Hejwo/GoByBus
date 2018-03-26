app.controller('pointController', function ($scope, $rootScope) {
    $scope.refreshMap = function () {
        if (typeof $scope.textAreaInput == 'undefined') {
            return;
        }
        var lines = $scope.textAreaInput.split('\n');
        if (lines.length) {
            $rootScope.lineInfo.path = [];
            $rootScope.lineInfo.dynamicMarkers = [];
        }
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            if (IsJsonString(line)) {
                var obj = JSON.parse(line);
                var point = {
                    latitude: obj.lat,
                    longitude: obj.lng,
                    details: obj.details
                };

                var marker = toMarker(point);
                if (!$scope.hideLine) {
                    $rootScope.lineInfo.path.push([marker.lat, marker.lng]);
                }
                if (!$scope.hidePoints) {
                    $rootScope.lineInfo.dynamicMarkers.push(marker);
                }
            }
        }
        $rootScope.markerClusterer.clearMarkers();
        $rootScope.markerClusterer.addMarkers($rootScope.lineInfo.dynamicMarkers);
    };

    function toMarker(point) {
        var latLng = new google.maps.LatLng(point.latitude, point.longitude);
        var marker = new google.maps.Marker({position: latLng});

        marker.lat = point.latitude;
        marker.lng = point.longitude;
        marker.details = point.details;

        google.maps.event.addListener(marker, 'click', function () {
            var displayContent = JSON.stringify(marker.details, null, "\t");
            $rootScope.lineInfo.details = displayContent;
            $rootScope.map.showInfoWindow('customPointInfo', marker);
        });

        return marker;
    }

    function IsJsonString(str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    }

});