package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;

public class MyServer {

    private static boolean created =false;
    private static Socket socket;
    public static String id;
    public static Player player;
    public static Texture playerShip;
    public static Texture friendlyShip;
    /*the hashmap will store the a key which in our case is a string
     and a value which for us is a Starship class
    * string will store the id , starship will store a starship class*/
    public static HashMap<String, Player> friendlyPlayers;

    /**
     *     * the friendlyPlayers list will be iterated over during the draw method
     *     * if a new item is added to the collection when iterating over it, which could
     *     * happen depending ont he timing, it will crash. this list prevents that,
     *     * add the player to this list, then add it to the friendlyPlayers list when safe*/

    public static HashMap<String, Player> friendlyPlayersAddingList;
    public static SpriteBatch spriteBatch;
    public static BitmapFont bitmapFont;
    public static ShapeRenderer shapeRenderer;
    public static float timer;
    private final static float UPDATE_TIME = 1/60f;


    public MyServer(){

    }
    public static void create(){

        if(created==false){

            created=true;
            /*spriteBatch = new SpriteBatch();*/
            bitmapFont = new BitmapFont();
            shapeRenderer = new ShapeRenderer();
            friendlyPlayers = new HashMap<String, Player>();
            friendlyPlayersAddingList = new HashMap<String, Player>();
            playerShip = new Texture("badlogic.jpg");
            friendlyShip = new Texture("badlogic1.jpg");
            player = new Player(playerShip);

            startServer();
        }
    }



    /**
     * don't really need a draw method, i don't think,
     */
    public static void update(){
        handleInput(Gdx.graphics.getDeltaTime());
        updateServer(Gdx.graphics.getDeltaTime());
        /*
        spriteBatch.begin();
        drawPlayers();
        spriteBatch.end();
        */
    }
    public static void drawPlayers(SpriteBatch spriteBatch){
        if(player != null){
            player.draw(spriteBatch);
            bitmapFont.draw(spriteBatch,"[bottomleft] x: "+player.getX()+" y: "+player.getY(),10,20);
            bitmapFont.draw(spriteBatch," [topleft] x: "+player.getX()+" y: "+player.getY(),10,1280-20);
        }

        /* modifying the friendlyPlayers collection while iterating over it
        * will cause errors, the friendlyPlayersAddingList helps prevent this*/
        if(friendlyPlayersAddingList.size()>0){
            friendlyPlayers.putAll(friendlyPlayersAddingList);
        }
        friendlyPlayersAddingList.clear();

        /*for each entry in the hash map, get the value, which remember
         * is a starship class, and call the function draw to draw that
         * starship.  note that starship inherits draw method from texture*/
        for(HashMap.Entry<String, Player> entry : friendlyPlayers.entrySet()){
            entry.getValue().draw(spriteBatch);
        }
    }
    public static void startServer() {
        connectSocket();
        configSocketEvents();
    }
    public static void handleInput(float dt){
        if(player != null) {
            if (Gdx.input.isTouched()) {

                player.setPosition(Gdx.input.getX(), 1280-Gdx.input.getY());
            }
        }
    }
    /*send an update to the server*/
    public static void updateServer(float dt){
        timer+=dt;
        if(timer>= UPDATE_TIME && player!= null&& player.hasMoved()){
            JSONObject data = new JSONObject();
            try{
                data.put("x",player.getX());
                data.put("y",player.getY());
                /*the player emits data to the server here
                 * the server has a corresponding method to
                 * interpret this data*/
                socket.emit("playerMoved",data);

            } catch(JSONException e) {
                Gdx.app.log("SOCKET.IO","Error sending update data");
            }

        }

    }

    /**
     * this will be called in the deal stage after the cards have been dealt,
     * emit that data to the other players
     */
    public static void emitDeal(){
        JSONArray objects = new JSONArray();
        try{
            /*data needs to contain the value 'v' of each card and who owns 'p' that card*/
            for(int i =0; i <OptionsStage.numberOfPlayers*OptionsStage.cardsEach; i++) {

                JSONObject data = new JSONObject();
                data.put("v" , TridentBuildingStage.cardButtonArray.get(i).value);
                data.put("p" , TridentBuildingStage.cardButtonArray.get(i).playerIndex);
                objects.put(data);
            }
            Gdx.app.log("Server","JSONarray: "+objects);
            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitDealDataToServer",objects);

        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending deal data");
        }
    }/**
     * this will be called in the deal stage when the player clicks confirm par
     * once all players have done so we can start sending the deal
     */
    public static void emitConfirmPar(int par){
        JSONObject data = new JSONObject();
        try{
            data.put("size",OptionsStage.numberOfPlayers);
            data.put("index",player.index);
            data.put("par",par);

            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitConfirmParToServer",data);

        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending confrim par data");
        }
    }
    public static void connectSocket() {
        try {
            socket = IO.socket("http://192.168.1.11:8080");
            socket.connect();
            System.out.println("connected");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            /*when something connects to the server*/
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
            }
        }).on("socketID", new Emitter.Listener() {
            /*the event listener will listen for an event called "socketID"*/
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    id = data.getString("id");
                    Gdx.app.log("SocketIO", "my ID: " + id);
                } catch(JSONException e){
                    Gdx.app.log("SocketIO","Error getting ID" +e);
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + playerId);
                    /*put the new player's id and the starship class in our hashmap*/
                    /*i think this is the casuse of an "LWJGL Application" java.util.ConcurrentModificationException
                    * error i've been getting, if you add a new player at the wrong moment we might be iterating over
                    * this friendlyPlayers collection when we modify the collection to add the new player, instead
                    * add the player to a different collection that is never iterated over and only add that to the
                    * friendlyPlayers when we are not iterating over it
                    * 20200701 i've had this error again so this obviosuly did not solve it */
                    friendlyPlayersAddingList.put(playerId, new Player(friendlyShip));
                    Gdx.app.log("SocketIO", "total players in hashmap: " + friendlyPlayers.size());
                    for(HashMap.Entry<String, Player> entry : friendlyPlayers.entrySet()){

                        Gdx.app.log("SocketIO", "new player: hashmap ids : " + entry.getKey());
                    }


                }catch(JSONException e){
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    /*ge thte id of the disconnected player*/
                    id = data.getString("id");
                    /*remove the player from the hashmap*/
                    friendlyPlayers.remove(id);
                    Gdx.app.log("SocketIO", "disconnected PlayerID: "+id);

                }catch(JSONException e){
                    Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*if a player moves the player will call the updateServer
                 * method as defined above
                 * the server will be informed of that player's position
                 * and will emit a playerMoved event
                 *
                 * all players will listen for a playerMoved event from
                 * the server and will update the position of other
                 * players on screen with that data*/
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    Double x= data.getDouble("x");
                    Double y= data.getDouble("y");
                    if(friendlyPlayers.get(playerId) != null){
                        friendlyPlayers.get(playerId).setPosition(x.floatValue(),y.floatValue());
                    }
                }catch(JSONException e){
                }
            }
        }).on("emitDealDataToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*player 1 will have called emitDeal,
                that will have called emitDealDataToServer
                when the server receives that emitDealDataToServer
                it calls emitDealDataToPlayers
                each player listens for the emitDealDataToPlayers
                emitDealDataToPlayers is received here
                 */
                JSONArray objects = (JSONArray) args[0];
                try {
                    for(int i=0; i<OptionsStage.numberOfPlayers*OptionsStage.cardsEach;i++){
                        TridentBuildingStage.cardButtonArray.get(i).value= objects.getJSONObject(i).getInt("v");
                        TridentBuildingStage.cardButtonArray.get(i).playerIndex= objects.getJSONObject(i).getInt("p");
                    }
                    DealStage.dealLoaded();
Gdx.app.log("Server: ","Deal received: "+objects);

                }catch(JSONException e){
                }
            }
        }).on("emitParConfirmedToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               /*when player clicked confirm par in the dealStage, that calls
               * emitConfirmPar() on the server which emits
               * emitConfirmParToServer
               * the server receives that and tests if all players have emitted par, if so
               * the server emits
               * emitParConfirmedToPlayers
               * to all players, this will simply call DealStage.parConfirmedByServer();*/
                JSONObject data = (JSONObject) args[0];
                try {
                    DealStage.parConfirmedByServer(data.getInt("par0"),data.getInt("par1"),data.getInt("par2"));
                }catch(JSONException e){
                }
            }
        }).on("emitPlayerIndexToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*when the number of players equals the number of players needed
                players will emit the player indexes, they are always just 0,1,2
                server will send player id and index, so set this player's index
                to the index paired with the player's id
                 */
                Gdx.app.log("Server: ", " call emitPlayerIndexToPlayers");
                JSONObject data = (JSONObject) args[0];
                Gdx.app.log("Server: ", " data "+data);

                try {

                    if(id.equals(data.getString("id0"))) {
                        player.index = data.getInt("index0");
                    }
                    else if(id.equals(data.getString("id1"))) {
                        player.index = data.getInt("index1");
                    }
                    else if(id.equals(data.getString("id2"))) {
                        player.index = data.getInt("index2");
                    }

                    /*let the deal stage know that the data has been received */
                    DealStage.dealReady();

                    Gdx.app.log("Server: ", " deal ready, player.index " + player.index);

                }catch(JSONException e){
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            /*getPlayers is for when the new player arrives and needs to know the
             * info about all the players that were there before it*/
            @Override
            public void call(Object... args) {
				/*get the players array from the server, which will contain the
				class called player, which has id and pos*/
                JSONArray objects = (JSONArray) args[0];
                try {
                    for(int i = 0; i < objects.length(); i++){
						/*create a new starship for every object inside the players
						array we have been passed*/
                        Player coopPlayer = new Player(friendlyShip);
                        Vector2 position = new Vector2();
                        /*apparently you cannot get a float value from a JSONObject, so cast it to double
                         * but set position method below only takes floats*/
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.setPosition(position.x, position.y);
                        /*add the new starship to the hash map*/
                        friendlyPlayers.put(objects.getJSONObject(i).getString("id"), coopPlayer);
                        Gdx.app.log("SocketIO", "current Players: " + objects.getJSONObject(i).getString("id")+ " index "+objects.getJSONObject(i).getString("index"));
                        for(HashMap.Entry<String, Player> entry : friendlyPlayers.entrySet()){

                            Gdx.app.log("SocketIO", "get players: hashmap ids : " + entry.getKey());
                        }

                    }
                } catch(JSONException e){

                }
            }
        });
        MatchMakingStage.serverReady();
        Gdx.app.log("Server ","server ready");
    }

    /**
     * this will be called when MyGdxGame.dispose method is called
     */
    public static void dispose () {
        spriteBatch.dispose();
        playerShip.dispose();
        friendlyShip.dispose();
    }

    /**
     * called when when a player clicks deal stage button
     * , this will test to see if we have the
     * specified number of players, if so it will give them either a 0,1 or 2 index
     */
    public static void gotAllPlayers(){
        Gdx.app.log("SocketIO", " call gotAllPlayers hashmap size = "+friendlyPlayers.size()+" num  of players needed = "+ (OptionsStage.numberOfPlayers));

        /*if we have the right number of players*/
        if(friendlyPlayers.size() == OptionsStage.numberOfPlayers){
                /*the player emits data to the server here
                 * this will trigger the server sending data back*/
            JSONObject data = new JSONObject();
            try{
                /*doesn't matter what the ids are here they will be overwritten*/
                data.put("size",friendlyPlayers.size());
                data.put("index0",0);
                data.put("id0",0);
                data.put("index1",1);
                data.put("id1",1);
                if(friendlyPlayers.size() == 3) {

                    data.put("index2", 2);
                    data.put("id2", 2);
                }
                /*the player emits data to the server here
                 * the server has a corresponding method to
                 * interpret this data*/
                socket.emit("emitPlayerIndexToServer",data);
                Gdx.app.log("SocketIO", " call emitPlayerIndexToServer");


            } catch(JSONException e) {
                Gdx.app.log("SOCKET.IO","Error sending index data");
            }
        }
        else {
        }
    }

}
