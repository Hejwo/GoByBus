app.controller('mapController', function ($scope, $rootScope, $http, $interval, NgMap) {
    $rootScope.settings = {
        lineStroke: {color: '#FF0000', weight: 3}
    };
    $rootScope.lineInfo = {
        dynamicMarkers: [],
        path: [],
        info: {
            line: "---",
            brigade: "---",
            time: "--:--"
        },
        details : ""
    };

    NgMap.getMap().then(function (map) {
        var mcOptions = {
            maxZoom: 16,
            imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
        };
        $rootScope.map = map;
        $rootScope.markerClusterer = new MarkerClusterer(map, $rootScope.lineInfo.dynamicMarkers, mcOptions);
    });

    $rootScope.addPosition = function (info) {
        var latLng = new google.maps.LatLng(info.lat, info.lng);
        var marker = new google.maps.Marker({position: latLng});

        google.maps.event.addListener(marker, 'click', function () {
            $rootScope.lineInfo.info = info;
            $rootScope.map.showInfoWindow('pointInfo', marker);
        });

        $rootScope.lineInfo.dynamicMarkers.push(marker);
        $rootScope.lineInfo.path.push([info.lat, info.lng]);

        $rootScope.markerClusterer.clearMarkers();
        $rootScope.markerClusterer.addMarkers($rootScope.lineInfo.dynamicMarkers);
    };

    $rootScope.addPositionMarkers = function (positionMarkers) {
        var poses = positionMarkers.map(function (x) {
            return [x.info.lat, x.info.lng];
        });

        $rootScope.lineInfo.dynamicMarkers = positionMarkers;
        $rootScope.lineInfo.path = poses;
        $rootScope.markerClusterer.clearMarkers();
        $rootScope.markerClusterer.addMarkers($rootScope.lineInfo.dynamicMarkers);
    };

    $rootScope.createMarker = function (position) {
        var info = {
            line: position.line,
            brigade: position.brigade,
            time: position.time,
            lat: position.latitude,
            lng: position.longitude
        };
        var latLng = new google.maps.LatLng(info.lat, info.lng);
        var marker = new google.maps.Marker({position: latLng});
        marker.info = info;
        google.maps.event.addListener(marker, 'click', function () {
            $rootScope.lineInfo.info = marker.info;
            $rootScope.map.showInfoWindow('pointInfo', marker);
        });

        return marker;
    };

});