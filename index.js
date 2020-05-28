var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);


server.listen(8080, function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	console.log("Player Connected!");
	/*when player connects server will automatically emit
	a unique socket id to that player, i've named it "socketID"*/
	socket.emit('socketID', { id: socket.id });
	/*the player will be connected on it's socket,
	we have just sent it an id using the socket
	we can send to all other players on different sockets by
	using broadcast as below*/
	socket.broadcast.emit('newPlayer', { id: socket.id });
	socket.on('disconnect', function(){
		console.log("Player Disconnected");
	});
	
});
