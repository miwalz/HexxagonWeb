Board = {

    getGrid: (function () {
        var grid;
        if (!grid) {
            grid = new HexagonGrid("HexCanvas", 40);
        }
        return grid;
    })(),

    init: function () {
        WebSocket.getWebSocket.send(JSON.stringify("init"));
    },

    update: function (gameState) {
        if (gameState.fields && gameState.fields.length && gameState.players && gameState.players.length > 1) {
            var p1 = gameState.players[0];
            var p2 = gameState.players[1];
            var grid = Board.getGrid;
         	$("#nameP1").text(p1.name.substr(0,15));
         	$("#nameP2").text(p2.name.substr(0,15));
         	$("#scoreP1").text(p1.score);
            $("#scoreP2").text(p2.score);
            if (p1.active) {
                $("#nameP1").css("text-decoration", "underline");
                $("#nameP2").css("text-decoration", "none");
            } else {
                $("#nameP1").css("text-decoration", "none");
                $("#nameP2").css("text-decoration", "underline");
            }
            _.each(gameState.fields, function (f) {
                grid.drawHexAtColRow(f.x, f.y, f.f);
            });
            if (gameState.gameOver) {
                var msg;
                if (p1.score > p2.score) {
                    msg = p1.name + " wins!"
                } else if (p1.score < p2.score) {
                    msg = p2.name + " wins!"
                } else {
                    msg = "The game ended in a draw."
                }
                alert("Game Over! " + msg);
                Board.init();
            }
        }
    }

};

WebSocket = {

    getWebSocket: (function () {
        var ws;
        if (!ws) {
            ws = new WebSocket("wss://" + location.host + "/socket");
            ws.onopen = function (evt) {
                console.log("WebSocket connection established.");
                Board.init();
            };
            ws.onclose = function (evt) {
                console.log("WebSocket connection closed.");
            };
            ws.onmessage = function (evt) {
                Board.update(JSON.parse(evt.data));
            };
        }
        return ws;
    })()

};