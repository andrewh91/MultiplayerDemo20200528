var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];


server.listen(8080, function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
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
	socket.on('disconnect', function(){
		console.log("Player Disconnected");
		/*when the player disconnects tell all over players*/
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		/*when a player disconnects, remove that player from the player array*/
		for(var i = 0; i < players.length; i++){
        			if(players[i].id == socket.id){
        				players.splice(i, 1);
        			}
        		}
	});
	/*when a player connects, we add their id and pos to this array*/
	players.push(new player(socket.id, 0, 0));
});
/*this is a simple player class that holds id and pos*/
function player(id, x, y){
	this.id = id;
	this.x = x;
	this.y = y;
}
