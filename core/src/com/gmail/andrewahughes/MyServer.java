package com.gmail.andrewahughes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    /**
     *a list of the opposition player's card values, from this you can figure out the suit and what tridents they made
     */
    static Array<Integer> player1CardValues = new Array<>();
    static Array<Integer> player2CardValues = new Array<>();
    /**
     * need to store the values of the other players' hands, this data will be recieved just after going to the game stage
     */
    static int player1WildCard = -1;
    static int player2WildCard = -1;

    static int player0Handicap=0;
    static int player1Handicap=0;
    static int player2Handicap=0;


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
    }

    /**
     * this will be called in the deckBuildingStage, everytime the building limit is adjusted

     */
    public static void emitBuildLimit(int buildLimit){
        Gdx.app.log("Server","emitBuildLimit");

        /*this array will have one object that gives the player index then the buildlimit*/
        JSONArray objects = new JSONArray();
        try{

            JSONObject data = new JSONObject();
            /*need to send the player index - not really needed for 2 player game, but need for 3 player*/
            data.put("p" , player.index);
            /*send the buildlimit so the other player can see what you set it to */
            data.put("b" , buildLimit);

            objects.put(data);

            Gdx.app.log("Server","player index "+player.index + " buildlimit "+ buildLimit);


            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitBuildLimitDataToServer",objects);
        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending buildLimit");
        }
    }
    /**
     * this will be called in the deckBuildingStage, everytime the handicap is adjusted

     */
    public static void emitHandicap(int handicap){
        Gdx.app.log("Server","emitHandicap");

        /*this array will have one object that gives the player index then the buildlimit*/
        JSONArray objects = new JSONArray();
        try{

            JSONObject data = new JSONObject();
            /*need to send the player index - not really needed for 2 player game, but need for 3 player*/
            data.put("p" , player.index);
            /*send the buildlimit so the other player can see what you set it to */
            data.put("h" , handicap);

            objects.put(data);

            Gdx.app.log("Server","player index "+player.index + " buildlimit "+ handicap);


            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitHandicapDataToServer",objects);
        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending handicap");
        }
    }


    /**
     * this will be called in the TridentBuildingStage, just before we go to the GameStage,
     * the player will have made and confirmed the triHand they want to use, so now that whole hand
     * will be transmitted to the other players.
     */
    public static void emitTriHand(){
        Gdx.app.log("Server","emitTriHand");

        /*this array will have one object that gives the player index then the wild card,
         * then it will have one object per card which tells us the value of each card*/
        JSONArray objects = new JSONArray();
        try{

            JSONObject data = new JSONObject();
            /*need to send the player index - not really needed for 2 player game, but need for 3 player*/
            data.put("p" , player.index);
            /*send the wild card suit, so the other player knows which suit you decided is the wild card suit*/
            data.put("w" , TridentBuildingStage.wildCardSuit);

            objects.put(data);
            String tempString = new String();
            /*send the value of each card the player put in the tri hand in order*/
            for(int i =0; i <TridentBuildingStage.cardButtonArrayTridentHand.size; i++) {

                JSONObject dataCard = new JSONObject();
                dataCard.put("v" , TridentBuildingStage.getCardAtHighlightPos(i).value);
                tempString=tempString+(" "+TridentBuildingStage.getCardAtHighlightPos(i).value);
                objects.put(dataCard);
            }
            Gdx.app.log("Server","player index "+player.index + " wildcardsuit "+ TridentBuildingStage.wildCardSuit);
            Gdx.app.log("Server","cards ");
            Gdx.app.log("pips",""+tempString);


            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitTriHandDataToServer",objects);
        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending triHand");
        }
    }

    /**
     * this will be called in the DeckBuildingStage, just before we go to the GameStage,
     * the player will have made and confirmed the triHand they want to use, so now that whole hand
     * will be transmitted to the other players.
     */
    public static void emitDeckTriHand(){
        Gdx.app.log("Server","emitDeckTriHand");

        /*this array will have one object that gives the player index ,
         * then it will have one object per card which tells us the value of each card*/
        JSONArray objects = new JSONArray();
        try{

            JSONObject data = new JSONObject();
            /*need to send the player index - not really needed for 2 player game, but need for 3 player*/
            data.put("p" , player.index);

            objects.put(data);
            String tempString = new String();
            /*send the value of each card the player put in the tri hand in order*/
            for(int i =0; i <OptionsStage.tridentsEach; i++) {

                JSONObject dataCard = new JSONObject();
                dataCard.put("v" , DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(0).value);
                objects.put(dataCard);
                JSONObject dataCard1 = new JSONObject();
                tempString=tempString+(" "+DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(0).value);
                dataCard1.put("v" , DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(1).value);
                objects.put(dataCard1);
                JSONObject dataCard2 = new JSONObject();
                tempString=tempString+(" "+DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(1).value);
                dataCard2.put("v" , DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(2).value);
                objects.put(dataCard2);
                tempString=tempString+(" "+DeckBuildingStage.triButtonArray.get(i).cardButtonArray.get(2).value);

            }
            Gdx.app.log("Server","player "+player.index+" cards ");
            Gdx.app.log("pips",""+tempString);


            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitDeckTriHandDataToServer",objects);
        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending deckTriHand");
        }
    }

    /**
     * this will be called in the gameStage. when a player places a trident then clicks confirm
     * i can emit just a small amount of data and it can be interpreted by the other player.
     * we need to know, player index, trihandindex, gameboard index, rotation, flip
     */
    public static void emitPlacement(int triHandIndex,int gameBoardIndex,int rotation,boolean flipped){
        Gdx.app.log("Server","emitPlacement");

        try{

            JSONObject data = new JSONObject();
            /*need to send the player index - not really needed for 2 player game, but need for 3 player*/
            data.put("p" , player.index);
            /*send the trihandindex, each player has already stored each other's tri hand, so we just need to tell
            * the other players which trident in the trihand we placed*/
            data.put("t" , triHandIndex);
            /*gameboardindex will tell us where the new trident has been placed*/
            data.put("g" , gameBoardIndex);
            /*rotation will tell us in what way the player has rotated the trident before confirming*/
            data.put("r" , rotation);
            /*flip will tell us in what way the player has flipped the trident before confirming*/
            data.put("f" , flipped);

            Gdx.app.log("Server","emit placement data  "+data);

            /*the player emits data to the server here
             * the server has a corresponding method to
             * interpret this data*/
            socket.emit("emitPlacementDataToServer",data);
        } catch(JSONException e) {
            Gdx.app.log("SOCKET.IO","Error sending placement");
        }
    }
    /**
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
            socket = IO.socket("http://192.168.1.10:8080");
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
        }).on("emitTriHandDataToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Gdx.app.log("Server","emitTriHandDataToPlayers");
                /*player 1 will have called emitTiHand,
                that will have called emitTriHandDataToServer
                when the server receives that emitTriHandDataToServer
                it calls emitTriHandDataToPlayers
                each player listens for the emitTriHandDataToPlayers
                emitTriHandDataToPlayers is received here
                 */
                JSONArray objects = (JSONArray) args[0];
                try {
                    /*player index will have been set for each player, it will be 0,1 or 2,
                     * so this player will be either 0, 1 or 2, the if statements below will figure out
                     * which other player is emitting this data and we'll save that data in
                     * a corresponding array*/
                    int index = objects.getJSONObject(0).getInt("p");
                    Gdx.app.log("Server","index - received "+index);

                    if(index==0)
                    {
                        GameStage.player0TriHandReceived=true;
                    }
                    else if(index==1)
                    {
                        GameStage.player1TriHandReceived=true;
                    }
                    else if(index==2)
                    {
                        GameStage.player2TriHandReceived=true;
                    }
                    if(index==(player.index+1)%OptionsStage.numberOfPlayers)
                    {

                        player1WildCard = objects.getJSONObject(0).getInt("w");
                        Gdx.app.log("Server","wild card - received "+player1WildCard);

                        String tempString = new String();
                        for(int i=1; i<OptionsStage.cardsEach+1;i++){
                            player1CardValues.add(objects.getJSONObject(i).getInt("v"));
                            tempString=tempString+(" "+ player1CardValues.get(i-1));
                        }
                        Gdx.app.log("Server","cards - received ");
                        Gdx.app.log("pips",""+tempString);

                    }
                    else if(index==(player.index+2)%OptionsStage.numberOfPlayers)
                    {

                        player2WildCard = objects.getJSONObject(0).getInt("w");
                        for(int i=1; i<OptionsStage.cardsEach+1;i++){
                            player2CardValues.add(objects.getJSONObject(i).getInt("v"));

                        }
                    }

                    Gdx.app.log("Server","index "+index+" wildcard "+player1WildCard);

                    GameStage.triHandLoaded();
                    Gdx.app.log("Server: ","TriHand received: "+objects);

                }catch(JSONException e){
                }
            }
        }).on("emitDeckTriHandDataToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Gdx.app.log("Server","emitDeckTriHandDataToPlayers");
                /*player 1 will have called emitDeckTiHand,
                that will have called emitDeckTriHandDataToServer
                when the server receives that emitDeckTriHandDataToServer
                it calls emitDeckTriHandDataToPlayers
                each player listens for the emitDeckTriHandDataToPlayers
                emitDeckTriHandDataToPlayers is received here
                 */
                JSONArray objects = (JSONArray) args[0];
                try {
                    /*player index will have been set for each player, it will be 0,1 or 2,
                     * so this player will be either 0, 1 or 2, the if statements below will figure out
                     * which other player is emitting this data and we'll save that data in
                     * a corresponding array*/
                    int index = objects.getJSONObject(0).getInt("p");
                    Gdx.app.log("Server","index - received "+index);

                    if(index==0)
                    {
                        GameStage.player0TriHandReceived=true;
                    }
                    else if(index==1)
                    {
                        GameStage.player1TriHandReceived=true;
                    }
                    else if(index==2)
                    {
                        GameStage.player2TriHandReceived=true;
                    }
                    if(index==(player.index+1)%OptionsStage.numberOfPlayers)
                    {
                        String tempString = new String();
                        for(int i=1; i<OptionsStage.cardsEach+1;i++){
                            player1CardValues.add(objects.getJSONObject(i).getInt("v"));
                            tempString=tempString+(" "+ player1CardValues.get(i-1));
                        }
                        Gdx.app.log("Server","cards - received ");
                        Gdx.app.log("pips",""+tempString);

                    }
                    else if(index==(player.index+2)%OptionsStage.numberOfPlayers)
                    {
                        for(int i=1; i<OptionsStage.cardsEach+1;i++){
                            player2CardValues.add(objects.getJSONObject(i).getInt("v"));

                        }
                    }

                    GameStage.triHandLoaded();
                    Gdx.app.log("Server: ","DeckTriHand received: "+objects);

                }catch(JSONException e){
                }
            }
        }).on("emitBuildLimitDataToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Gdx.app.log("Server","emitBuildLimitDataToPlayers");
                /*player 1 will have called emitBuildLimit,
                that will have called emitBuildLimitDataToServer
                when the server receives that emitBuildLimitDataToServer
                it calls emitBuildLimitDataToPlayers
                each player listens for the emitBuildLimitDataToPlayers
                emitBuildLimitDataToPlayers is received here
                 */
                JSONArray objects = (JSONArray) args[0];
                try {
                    /*player index will have been set for each player, it will be 0,1 or 2,
                     * so this player will be either 0, 1 or 2, the if statements below will figure out
                     * which other player is emitting this data and we'll save that data in
                     * a corresponding array*/
                    int index = objects.getJSONObject(0).getInt("p");
                    Gdx.app.log("Server","index - received "+index);

                    DeckBuildingStage.proposedValueLimitDiff = objects.getJSONObject(0).getInt("b");
                    Gdx.app.log("Server","build limit - received "+ DeckBuildingStage.proposedValueLimitDiff);


                    Gdx.app.log("Server: ","build limit received: "+objects);

                }catch(JSONException e){
                }
            }
        }).on("emitHandicapDataToPlayers", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                Gdx.app.log("Server","emitHandicapDataToPlayers");
                /*player 1 will have called emitHandicap,
                that will have called emitHandicapDataToServer
                when the server receives that emitHandicapDataToServer
                it calls emitHandicapDataToPlayers
                each player listens for the emitHandicapDataToPlayers
                emitHandicapDataToPlayers is received here
                 */
                JSONArray objects = (JSONArray) args[0];
                try {
                    /*player index will have been set for each player, it will be 0,1 or 2,
                     * so this player will be either 0, 1 or 2, the if statements below will figure out
                     * which other player is emitting this data and we'll save that data in
                     * a corresponding array*/
                    int index = objects.getJSONObject(0).getInt("p");
                    Gdx.app.log("Server","index - received "+index);


                    if(index==0)
                    {
                        player0Handicap=objects.getJSONObject(0).getInt("h");
                    }
                    else if(index==1)
                    {
                        player1Handicap=objects.getJSONObject(0).getInt("h");
                    }
                    else if(index==2)
                    {
                        player2Handicap=objects.getJSONObject(0).getInt("h");
                    }

                    DeckBuildingStage.updateHandicap();
                    Gdx.app.log("Server","handicap - received "+ objects.getJSONObject(0).getInt("h"));


                    Gdx.app.log("Server: ","handicap received: "+objects);

                }catch(JSONException e){
                }
            }
        }).on("emitPlacementDataToPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Gdx.app.log("Server","emitPlacementDataToPlayers");
                /*player 1 will have called emitplacement,
                that will have called emitPlacementDataToServer
                when the server receives that emitPlacementDataToServer
                it calls emitPlacementDataToPlayers
                each player listens for the emitPlacementDataToPlayers
                emitPlacementDataToPlayers is received here
                 */
                JSONObject data = (JSONObject) args[0];
                try {
                    /*player index will have been set for each player, it will be 0,1 or 2,
                     * so this player will be either 0, 1 or 2, the if statements below will figure out
                     * which other player is emitting this data and we'll save that data in
                     * a corresponding array*/

                    int index = data.getInt("p");
                    /*the index of each player will be a unique number, 0, 1 or 2
                    * the index here should never be= player.index because the player would not
                    * have emitted to itself. so it will either be 1 higher than the player's
                    * index, or 2 higher so long as you modulus the numberofplayers, which
                    * will be either the first opposition player or the second respectively*/

                    if(index==(player.index+1)%OptionsStage.numberOfPlayers)
                    {
                        index=1;
                    }
                    else if(index==(player.index+2)%OptionsStage.numberOfPlayers)
                    {
                        index=2;
                    }
                    int triHandIndex = data.getInt("t");
                    int gameboardIndex = data.getInt("g");
                    int rotation = data.getInt("r");
                    boolean flipped = data.getBoolean("f");
                    Gdx.app.log("Server","index - received "+index);

                    GameStage.placementLoaded(index,triHandIndex,gameboardIndex,rotation,flipped);
                    Gdx.app.log("Server: ","TriHand received: "+data);

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
