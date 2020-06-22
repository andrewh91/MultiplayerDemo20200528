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
            playerShip = new Texture("badlogic.jpg");
            friendlyShip = new Texture("badlogic.jpg");
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
    public static void connectSocket() {
        try {
            socket = IO.socket("http://192.168.1.6:8080");
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
                player = new Player(playerShip);
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
                    friendlyPlayers.put(playerId, new Player(friendlyShip));
                    Gdx.app.log("SocketIO", "total players in hashmap: " + friendlyPlayers.size());
                    /*call the gotAllPlayers method which will set the player indexes
                    * if we have the correct number of players*/
                    gotAllPlayers();
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
                    }
                } catch(JSONException e){

                }
            }
        });

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
     * called when a new player is added, this will test to see if we have the
     * specified number of players, if so it will give them either a 0,1 or 2 index
     */
    public static void gotAllPlayers(){
        if(friendlyPlayers.size() == OptionsStage.numberOfPlayers){
            byte i =0;
            for(HashMap.Entry<String, Player> entry : friendlyPlayers.entrySet()){
                entry.getValue().setIndex(i);
                i++;
            }
        }
    }
}
