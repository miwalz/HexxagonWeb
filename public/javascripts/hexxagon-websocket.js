WebSocket = {

    ws: (function () {

        var ws = new WebSocket("ws://" + location.host + "/socket");

        ws.onopen = function (evt) {
            console.log("ws opened");
        };

        ws.onclose = function (evt) {
            console.log("ws closed");
        };

        ws.onmessage = function (evt) {
            console.log("ws message: " + evt.data);
        };

        ws.onerror = function (evt) {
            console.log("ws error");
        };

        return ws;
    })()

};