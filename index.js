var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
var cards = [];
var playerIndex=0;


server.listen(8080,"192.168.1.6", function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	/*when a player connects, we add their id and pos to this array*/
	players.push(new player(socket.id, 0, 0,playerIndex));
	playerIndex++;
	console.log("Player Connected!");
	/*when player connects server will automatically emit
	a unique socket id to that player, i've named it "socketID"*/
	socket.emit('socketID', { id: socket.id });
	/*send the players array to the new player so they
	know who else is playing*/
	socket.emit('getPlayers', players);
	/*the player will be connected on it's socket,
	we have just sent it an id using the socket
	we can send to all other players on different sockets by
	using broadcast as below*/
	socket.broadcast.emit('newPlayer', { id: socket.id });
	/*when a player emits a playerMoved event the server
	will interpret it here*/
	socket.on('playerMoved', function(data){
	    /*the data the player sent did not include it's playerid
	    just because we didn't need to -
	    because we already know the socket id which is identical
	    so add the id to the data object,
	    the data now includes x, y and id*/
	    data.id=socket.id;
	    /*send the data we received (x,y and id) to all other players
	     specifically all players apart from the one that sent it */
	    socket.broadcast.emit("playerMoved",data);

	    console.log("playerMoved: "+
	                "ID: "+data.id +
	                "x: "+data.x +
	                "y: "+data.y);
        /*we're going to go through our array of existing players
        and store the new position info for each relevant player*/
	    for (var i =0; i< players.length;i++){
	        if(players[i].id == data.id){
	            players[i].x=data.x;
	            players[i].y=data.y;
	        }
	    }
	});
	/*when player1 emits a dealDataReceived event the server
    	will interpret it here*/
    	socket.on('emitDealDataToServer', function(data){

    	    /*send the data we received (x,y and id) to all other players
    	     specifically all players apart from the one that sent it */
    	    socket.broadcast.emit("emitDealDataToPlayers",data);

    	    console.log("dealDataReceived: "+
    	                "v: "+data.v0 +
    	                " p: "+data.p0 );
            /*add all this data to the servers' cards array*/
            cards.push(new card(data.v0,data.p0));

    	});
	/*when player1 emits an emitPlayerIndexToServer event the server
    	will interpret it here*/
    	socket.on('emitPlayerIndexToServer', function(data){

    	    /*amend and send send playerindex to all players */
    	     data.id0 = players[0].id;
    	     data.id1 = players[1].id;
    	     if(data.size==3){
    	        data.id2 = players[2].id;
    	     }
    	     /*send the data to the originating player*/
            console.log("call emitPlayerIndexToPlayers");
            socket.emit("emitPlayerIndexToPlayers",data);
    	    /*then send the data to all other players*/
    	    socket.broadcast.emit("emitPlayerIndexToPlayers",data);

    	    console.log("playerIndexReceived: "+"index0: "+data.index0 + " id0" + data.id0);
    	    console.log("playerIndexReceived: "+"index1: "+data.index1 + " id1" + data.id1);
    	    if(data.size==3){
    	        console.log("playerIndexReceived: "+"index2: "+data.index2 + " id2" + data.id2);
    	    }
    	});

	socket.on('disconnect', function(){
		console.log("Player Disconnected");
		/*when the player disconnects tell all other players*/
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		/*when a player disconnects, remove that player from the player array*/
		for(var i = 0; i < players.length; i++){
        			if(players[i].id == socket.id){
        				players.splice(i, 1);
        				playerIndex--;
        			}
        		}
	});

});
/*this is a simple player class that holds id and pos and index*/
function player(id, x, y,index){
/*id will be the socket id, a long random number*/
	this.id = id;
	this.x = x;
	this.y = y;
	/*index will be 0 1 or 2 , based on if this is player 1 2 or 3 */
	this.index = index;
}
/*this is a simple card class that holds value and playerIndex*/
function card(v,p){
	this.v = v;
	this.p = p;
}
