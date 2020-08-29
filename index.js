var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
var playerIndex=0;
var confirmPar0 = false;
var confirmPar1 = false;
var confirmPar2 = false;
var par0 = 0;
var par1 = 0;
var par2 = 0;
var par =0;


server.listen(8080,"192.168.1.2", function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	/*when a player connects, we add their id and pos to this array*/
	players.push(new player(socket.id, 0, 0,playerIndex));
	playerIndex++;
	console.log("Player Connected! total players= "+players.length);
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
	    /*send the data we received (x,y and id) to all  players
	      */
	    socket.broadcast.emit("playerMoved",data);
	    socket.emit("playerMoved",data);

/*commenting this out since it creates a lot of logs
	    console.log("playerMoved: "+
	                "ID: "+data.id +
	                "x: "+data.x +
	                "y: "+data.y);
	                */
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

    	    /*send the data we received (x,y and id) to all  players */
    	    socket.broadcast.emit("emitDealDataToPlayers",data);
    	    socket.emit("emitDealDataToPlayers",data);

    	    console.log("dealDataReceived: "+
    	                "v: "+data.v +
    	                " p: "+data.p );
    	    console.log("total data: "+data);

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

    	    /*reset these variables, */
            confirmPar0 = false;
            confirmPar1 = false;
            confirmPar2 = false;
            par0 = 0;
            par1 = 0;
            par2 = 0;
            par =0;

    	});

	/*when player1 emits an emitConfirmParToServer event the server
    	will interpret it here*/
    	socket.on('emitConfirmParToServer', function(data){
            /*the player will have sent their player index, to indicate that they have confirmed par*/
            if(data.index==0){
            confirmPar0=true;
            par0=data.par;

            }
            else if (data.index==1){
            confirmPar1=true;
            par1=data.par;

            }
            else if(data.index==2){
            confirmPar2=true;
            par2=data.par;
            }



            /*emit the data back to all players, the data will contain par0, par1 and par2*/
            /*if 2 player game*/
            if(data.size==2){
                if(confirmPar0&&confirmPar1){
                    calculatePar(data.size);
                    data.par0=par0;
                    data.par1=par1;
                    data.par2=par2;

                    socket.emit("emitParConfirmedToPlayers",data);
                    socket.broadcast.emit("emitParConfirmedToPlayers",data);
                }
            }
            /*if 3 player game*/
            else if(data.size==3){
                 if(confirmPar0&&confirmPar1&&confirmPar2){
                    calculatePar(data.size);
                    data.par0=par0;
                    data.par1=par1;
                    data.par2=par2;
                    socket.emit("emitParConfirmedToPlayers",data);
                    socket.broadcast.emit("emitParConfirmedToPlayers",data);
                 }
            }
            console.log("call emitParConfirmedToPlayers");
    	});
    	function calculatePar(size) {
    	/*method to work out what the final par should be */
                    /*
                           * The par values that have been chosen will be analysed, and a fair compromise will be attempted.
                           * I’ve worked out a way to do this, as close to the chosen values as possible while still maintaining the difference in those values.
                           *
                           * N = number of players either 2 or 3*/
                           var n = size;
                           /*
                           * P[N] player array hold a value for that player’s par
                           * X = Round( (P[0]+ P[1] + P[2]) / N ) // X is the average par, rounded
                           */
                           var x = Math.round((par0+par1+par2)/n);/*we can safely add par2 even if it's not a 3 player game, cos it will be 0 by default*/
                           /*
                           *
                           * For each p in P
                           * P2 = P - X  //this is the first approximate of the correct par, it might be 1 out
                           */
                           var par0approx = par0-x;
                           var par1approx = par1-x;
                           if(n==3){
                           var par2approx = par2-x;
                           }
                           else{
                           var par2approx = 0;
                           }
                           /*
                           *
                           * X2 = (P2[0]+P2[1]+P2[2])  // X2 is the total of the first approximate par, it should be either 1 or 0
                           */
                           var x2 = par0approx+par1approx+par2approx;
                           /*
                           *
                           * If (X2=0)  //if the first approximate par total is 0 then set par to that value and end
                           *  End and ignore the below
                           */
                           if(x2==0){
                           par0=par0approx;
                           par1=par1approx;
                           par2=par2approx;
                           }
                           /*
                           * Else //do the rest of this several lines below
                           * For each p in P
                           * P3 = ABS(P)//this is the absolute value
                           */
                           else{
                               var parvalues = [];
                               parvalues.push(Math.abs(par0));
                               parvalues.push(Math.abs(par1));
                               parvalues.push(Math.abs(par2));

                               /*
                               * P3.sort // sort that array
                               */
                               parvalues.sort();
                               /*
                               * If P3[0] == P3[1] // if the 2 highest numbers are the same
                               * P2[2] = P2[2]  - X2  // subtract X2 from the lowest number in the first approx par
                               * Else
                               * P2[0] = P2[0]  - X2  // subtract X2 from the highest number in the first approx par
                               *
                               * This should work for 2 or 3 players and it should keep the total par to 0 which is important,
                               */
                               if(parvalues[0]==parvalues[1]){
                                    par2approx = par2approx-x2;
                               }
                               else{
                                    par0approx = par0approx -x2;
                               }

                               par0=par0approx;
                               par1=par1approx;
                               par2=par2approx;
                           }
                           console.log("par0 : "+par0);
                           console.log("par1 : "+par1);
                           console.log("par2 : "+par2);
    	}

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
