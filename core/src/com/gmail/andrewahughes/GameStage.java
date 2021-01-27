package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONObject;

import static com.gmail.andrewahughes.CardButton.LEFT;
import static com.gmail.andrewahughes.CardButton.RIGHT;
import static com.gmail.andrewahughes.CardButton.VERTICAL;
import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;
import static com.gmail.andrewahughes.StageInterface.DECKBUILDINGSTAGE;

public class GameStage extends Stage {

    boolean visible =false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();

    static Array<CardButton> cardButtonArray = new Array<CardButton>();

    static int highlightPosGameboard=-1;
    static int highlightPosTriHand =-1;

    /**
     * this will be set to true when placing a trident on the gameboard,
     * the options to flip and rotate may then appear, as well as the confirm or cancel button
     */
    static boolean placeMode=false;



   

    /*this is just used in the 3 player game, it will let us know when the first opposition player
    * has emitted their triHand data*/
    static boolean firstOppositionPlayerTriHandLoaded = false;
    static boolean allTriHandsLoaded = false;


    /**
     * this will be set either in the MyServer when the other player's trihand is received
     * or set in the game stage when the tri hand is sent
     */
    static boolean player0TriHandReceived = false;
    /**this will be set either in the MyServer when the other player's trihand is received
     * or set in the game stage when the tri hand is sent
     */
    static boolean player1TriHandReceived = false;
    /**this will be set either in the MyServer when the other player's trihand is received
     * or set in the game stage when the tri hand is sent
     */
    static boolean player2TriHandReceived = false;

    public GameStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        this.stageInterface =stageInterface;

        this.shapeRenderer = shapeRenderer;
        this.setViewport(viewport);
        this.spriteBatch = batch;
        for (int i = 0; i < OptionsStage.gameBoardSize; i++) {
            cardButtonArray.add(        new CardButton(stageInterface,0,0,true,CardButton.VERTICAL,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25),
                    new CardButton(stageInterface,0,0,true, LEFT,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25),
                    new CardButton(stageInterface,0,0,true,CardButton.RIGHT,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25));

        }

        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);
        createButtons();
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.5f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawTriButtonsShapeFilled();
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape();
            drawHighlightShape();
            shapeRenderer.end();
            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons();

            spriteBatch.end();
        }
    }
    void drawTriButtons() {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).draw(spriteBatch,1.0f);
        }
    }


    /**
     * draw the trident buttons shape - which should just be it's bounds
     */
    void drawTriButtonsShape() {
        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShape(shapeRenderer);
        }
    }
    void drawTriButtonsShapeFilled() {
        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShapeFilled(shapeRenderer);
        }
    }
    void drawHighlightShape() {
        if (highlightPosGameboard>-1) {
            shapeRenderer.setColor(Color.RED);
            triButtonArray.get(highlightPosGameboard).drawShape(shapeRenderer);
            /*use the adjacent variables of the highlighted trident to figure out which trident's
             * need to be highlighted, we will only highlight one card of the adjacent tridents,
             * the trident adjacent to the vertical position will have it's vertical card highlighted, vert - vert
             * but generally the trident adjacent to the left position will have it's right card highlighted so left - right
             * and vice versa for the right one - right - left
             * UNLESS the adjacent trident is the same orientation as the current one, in which case  we do left -left, or right - right
             * this will only happen to tridents on the edge, where we've been creative in which one is adjacent */
            boolean sameOrientationLeft = triButtonArray.get(highlightPosGameboard).orientation==triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).orientation;
            boolean sameOrientationRight = triButtonArray.get(highlightPosGameboard).orientation==triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).orientation;
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexVertical).cardButtonArray.get(0).drawHighlightShape(shapeRenderer);
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).cardButtonArray.get(sameOrientationLeft?2:1).drawHighlightShape(shapeRenderer);
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).cardButtonArray.get(sameOrientationRight?1:2).drawHighlightShape(shapeRenderer);
            shapeRenderer.setColor(Color.WHITE);
        }
        if (highlightPosTriHand>-1) {
            shapeRenderer.setColor(Color.RED);
            triButtonArray.get(highlightPosTriHand).drawShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(0).drawHighlightShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(1).drawHighlightShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(2).drawHighlightShape(shapeRenderer);
            shapeRenderer.setColor(Color.WHITE);
        }
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void createButtons() {
        /*when creating new buttons we pass in the enum for that button so the button can store it
         * so it can reference itself later. the add button method also needs this stage
         * to add the actor to the stage and our array of buttons so we can add it to that too
         * when adding to the array the method actually inserts it in the array at the enum.value index
         * this means if we add the buttons out of order it will cause an error, which is good because
         * then i can make sure the buttons are in the correct order*/
        createGameBoard();
        createTridentHand();
        createPlaceModeButtons();
        createTridentHandPlayer1();
        createTridentHandPlayer2();

        createTridentHandCards();

        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMENEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setText("Game\nOver");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setTridentToTextSize();
    }
    public void createGameBoard(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD0),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD3),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD4),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD5),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD6),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD7),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD8),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD9),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD10),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD11),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD12),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD13),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD14),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD15),triButtonArray,this);

    }

    /**
     * there will be a max of 8 tridents in the trident array, plus 2 extras for pre and post game card
     */
    public void createTridentHand(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND0),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND3),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND4),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND5),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND6),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND7),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPREGAMECARD),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPOSTGAMECARD),triButtonArray,this);

    }
    /*we need to create a tridentHand for the other players too*/
    public void createTridentHandPlayer1(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND0P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND1P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND2P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND3P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND4P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND5P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND6P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND7P1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPREGAMECARDP1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPOSTGAMECARDP1),triButtonArray,this);

    }
    /*we need to create a tridentHand for the other players too*/
    public void createTridentHandPlayer2(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND0P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND1P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND2P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND3P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND4P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND5P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND6P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND7P2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPREGAMECARDP2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPOSTGAMECARDP2),triButtonArray,this);

    }
    /*just want to create cardbuttons so that they are not null, these values below will be changed */
    public void createTridentHandCards()
    {
        for (int i = 0; i < OptionsStage.nonPrePostGameTridentsEach; i++) {
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));

            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.add(new CardButton(stageInterface, 0, 0, true, (byte) 0, DECKBUILDINGSTAGE, ButtonEnum.Card.TRIHANDCARD0));
        }
    }

    public void createPlaceModeButtons(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEPLACEROTATE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMEPLACEROTATE).setText("Rotate");
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,true,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEPLACEFLIP),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMEPLACEFLIP).setText("Flip");
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEPLACECONFIRM),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMEPLACECONFIRM).setText("Confirm");

    }
    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryTriButtonTouch(float x, float y){

        for(int i=0;i<triButtonArray.size;i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if(triButtonArray.get(i).triangleHit(x,y)){
                triButtonArray.get(i).touchLogic(x,y);
                break;
            }


        }
    }
    /**
     * called when moving to the game stage, which should only happen when we have successfully set up
    * trident hand */
    public static void setUp()
    {

        for (int i = 0;i<OptionsStage.nonPrePostGameTridentsEach;i++){
            /*add 3 cards from the trident building stage's cardButtonArrayTridentHand to this tributton
            * note that the first TriButton in this tributtonarray is the button to go to the next stage,
            * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
            if (OptionsStage.tridentBuildingMode==OptionsStage.PREMADETRIDENTS)
            {
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0.value).updatePos(DeckBuildingStage.triButtonArray.get(i).getX(), DeckBuildingStage.triButtonArray.get(i).getY(), DeckBuildingStage.triButtonArray.get(i).orientation);
            }
            else
            {
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0.value).updatePos(TridentBuildingStage.getCardAtHighlightPos(i * 3).getX(), TridentBuildingStage.getCardAtHighlightPos(i * 3).getY(), TridentBuildingStage.getCardAtHighlightPos(i * 3).orientation);
            }
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).cardsVisible=true;
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).ownership=0;
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).confirmOwnership();

        }
        /*if using pre and post game cards save them to a trident each too */
        if (OptionsStage.preAndPostGameCard){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).setPreGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3));
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).setPostGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3+1));
        }

        setUpGameBoard();




    }
    /**check if all players have sent their trihand, if so call displayTriHand. the triHandLoaded method
     * will be called in the tirdentbuildingstage when the player confirms their trident hand, and also
     * in the MyServer when a triHand is recieved from another player */
    public static void triHandLoaded()
    {
        Gdx.app.log("GameStage","trihandloaded() - player "+MyServer.player.index+" received hand of player "+(player0TriHandReceived?0:"")+(player1TriHandReceived?1:"")+(player2TriHandReceived?2:"")+"");
        Gdx.app.log("GameStage","player0TriHandReceived "+player0TriHandReceived);
        Gdx.app.log("GameStage","player1TriHandReceived "+player1TriHandReceived);
        Gdx.app.log("GameStage","player2TriHandReceived "+player2TriHandReceived);



        if (OptionsStage.numberOfPlayers==3)
        {
            if (player0TriHandReceived&&player1TriHandReceived&&player2TriHandReceived) {
                displayTrihand();
            }
        }
        else if (OptionsStage.numberOfPlayers==2) {
            if (player0TriHandReceived && player1TriHandReceived) {
                displayTrihand();

            }
        }
    }



    public static void displayTrihand(){

        String tempString = new String();
        String tempString1 = new String();
        Gdx.app.log("GameStage", "AllTriHandsLoaded");
        Gdx.app.log("GameStage", "player1cardvalues"+MyServer.player1CardValues);
        Gdx.app.log("GameStage", "tridentbuildingstage card buttonarray "+TridentBuildingStage.cardButtonArray);
        Gdx.app.log("GameStage", "Deckbuildingstage tributtonArray ");
        for (int i =0;i< DeckBuildingStage.triButtonArray.size;i++)
        {
            Gdx.app.log("GameStage", DeckBuildingStage.triButtonArray.get(i).cardButtonArray+"");

        }

        if (OptionsStage.tridentBuildingMode==OptionsStage.BUILDYOUROWNTRIDENTS) {
            for (int i = 0; i < OptionsStage.nonPrePostGameTridentsEach; i++) {
                /*add 3 cards from the MyServer.player1CardValues array to this tributton
                 * note that the first TriButton in this tributtonarray is the button to go to the next stage,
                 * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).setUpCardButtons(TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3)), TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3 + 1)), TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3 + 2)));
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).oppositionWildCardSuit = MyServer.player1WildCard;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).oppositionWildCardSuit = MyServer.player1WildCard;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).oppositionWildCardSuit = MyServer.player1WildCard;

                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).position = 0;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).position = 1;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).position = 2;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).updatePos(TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).getX(), TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).getY() - CardButton.edgeLength, TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).orientation);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardsVisible = true;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).ownership = 1;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).confirmOwnership();
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).getPip());
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).getPip());
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).getPip());
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3));
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3 + 1));
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3 + 2));

            }
            Gdx.app.log("GAMESTAGE", "opposite player card values ");
            Gdx.app.log("pips", "" + tempString1);
            Gdx.app.log("GAMESTAGE", "opposite player tributton array ");
            Gdx.app.log("pips", "" + tempString);

            /*if using pre and post game cards save them to a trident each too */
            /*if (OptionsStage.preAndPostGameCard){
                triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P1.value).setPreGameCard(TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3)));
                triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P1.value+1).setPostGameCard(TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3+1)));
            }*/


            if (OptionsStage.numberOfPlayers == 3) {

                for (int i = 0; i < OptionsStage.nonPrePostGameTridentsEach; i++) {
                    /*add 3 cards from the MyServer.player1CardValues array to this tributton
                     * note that the first TriButton in this tributtonarray is the button to go to the next stage,
                     * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).setUpCardButtons(TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(i * 3)), TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(i * 3 + 1)), TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(i * 3 + 2)));
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).position = 0;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).position = 1;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).position = 2;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).updatePos(TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).getX(), TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).getY() - CardButton.edgeLength * 2, TridentBuildingStage.cardButtonArrayTridentHand.get(i * 3).orientation);
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardsVisible = true;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).ownership = 2;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).confirmOwnership();

                }
                /*if using pre and post game cards save them to a trident each too */
                /*if (OptionsStage.preAndPostGameCard){
                    triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P2.value).setPreGameCard(TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3)));
                    triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P2.value+1).setPostGameCard(TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3+1)));
                }

                 */


            }
        }
        else if (OptionsStage.tridentBuildingMode==OptionsStage.PREMADETRIDENTS)
        {


            for (int i = 0; i < OptionsStage.nonPrePostGameTridentsEach; i++) {

                /*add 3 cards from the MyServer.player1CardValues array to this tributton
                 * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).value = MyServer.player1CardValues.get(i * 3 + 0);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).value = MyServer.player1CardValues.get(i * 3 + 1);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).value = MyServer.player1CardValues.get(i * 3 + 2);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).setUpCardButtons(triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0), triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1), triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2));

                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).orientation = triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).orientation;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).orientation = triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).orientation;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).orientation = triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0P1.value).orientation;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).value = MyServer.player1CardValues.get(i * 3 + 1);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).value = MyServer.player1CardValues.get(i * 3 + 2);

                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).setColourFromSuit();
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).position = 0;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).position = 1;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).position = 2;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).updatePos(DeckBuildingStage.triButtonArray.get(i).getX(), DeckBuildingStage.triButtonArray.get(i).getY() - CardButton.edgeLength, DeckBuildingStage.triButtonArray.get(i).orientation);
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardsVisible = true;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).ownership = 1;
                triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).confirmOwnership();
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(0).getPip());
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(1).getPip());
                tempString = tempString + (" " + triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P1.value).cardButtonArray.get(2).getPip());
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3));
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3 + 1));
                tempString1 = tempString1 + (" " + MyServer.player1CardValues.get(i * 3 + 2));

                /*so now we've loaded in the opposition's tridents also load in the current players'*/
                triButtonArray.get(i + 16).setUpCardButtons(DeckBuildingStage.getCardWithValue(DeckBuildingStage.triButtonArray.get(i ).cardButtonArray.get(0).value), DeckBuildingStage.getCardWithValue(DeckBuildingStage.triButtonArray.get(i ).cardButtonArray.get(1).value),DeckBuildingStage.getCardWithValue(DeckBuildingStage.triButtonArray.get(i ).cardButtonArray.get(2).value));
                triButtonArray.get(i + 16).cardButtonArray.get(0).oppositionWildCardSuit = MyServer.player1WildCard;
                triButtonArray.get(i + 16).cardButtonArray.get(1).oppositionWildCardSuit = MyServer.player1WildCard;
                triButtonArray.get(i + 16).cardButtonArray.get(2).oppositionWildCardSuit = MyServer.player1WildCard;

                triButtonArray.get(i + 16).cardButtonArray.get(0).setColourFromSuit();
                triButtonArray.get(i + 16).cardButtonArray.get(1).setColourFromSuit();
                triButtonArray.get(i + 16).cardButtonArray.get(2).setColourFromSuit();
                triButtonArray.get(i + 16).cardButtonArray.get(0).position = 0;
                triButtonArray.get(i + 16).cardButtonArray.get(1).position = 1;
                triButtonArray.get(i + 16).cardButtonArray.get(2).position = 2;
                triButtonArray.get(i + 16).updatePos(DeckBuildingStage.triButtonArray.get(i).getX(), DeckBuildingStage.triButtonArray.get(i).getY() , DeckBuildingStage.triButtonArray.get(i).orientation);
                triButtonArray.get(i + 16).cardsVisible = true;
                triButtonArray.get(i + 16).ownership = 1;
                triButtonArray.get(i + 16).confirmOwnership();

            }
            Gdx.app.log("GAMESTAGE", "opposite player card values ");
            Gdx.app.log("pips", "" + tempString1);
            Gdx.app.log("GAMESTAGE", "opposite player tributton array ");
            Gdx.app.log("pips", "" + tempString);

            /*if using pre and post game cards save them to a trident each too */
            /*if (OptionsStage.preAndPostGameCard){
                triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P1.value).setPreGameCard(TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3)));
                triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P1.value+1).setPostGameCard(TridentBuildingStage.getCardWithValue(MyServer.player1CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3+1)));
            }*/


            if (OptionsStage.numberOfPlayers == 3) {

                for (int i = 0; i < OptionsStage.nonPrePostGameTridentsEach; i++) {
                    /*add 3 cards from the MyServer.player1CardValues array to this tributton
                     * note that the first TriButton in this tributtonarray is the button to go to the next stage,
                     * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).setUpCardButtons(DeckBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3)), DeckBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3 + 1)), DeckBuildingStage.getCardWithValue(MyServer.player1CardValues.get(i * 3 + 2)));
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).oppositionWildCardSuit = MyServer.player2WildCard;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).setColourFromSuit();
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(0).position = 0;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(1).position = 1;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardButtonArray.get(2).position = 2;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).updatePos(DeckBuildingStage.triButtonArray.get(i).getX(), DeckBuildingStage.triButtonArray.get(i).getY() - CardButton.edgeLength * 2, DeckBuildingStage.triButtonArray.get(i).orientation);
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).cardsVisible = true;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).ownership = 2;
                    triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0P2.value).confirmOwnership();

                }
                /*if using pre and post game cards save them to a trident each too */
                /*if (OptionsStage.preAndPostGameCard){
                    triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P2.value).setPreGameCard(TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3)));
                    triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0P2.value+1).setPostGameCard(TridentBuildingStage.getCardWithValue(MyServer.player2CardValues.get(OptionsStage.nonPrePostGameTridentsEach*3+1)));
                }

                 */


            }
        }
    }


    /**
     * this will be called in the myServer class, after a placement has been sent, and recieved, 
     * the data will be processed here
     * @param playerIndex this will be either 1 or 2 so that will correspond to either MyServer.player1CardValues or MyServer.player2CardValues 
     * @param triHandIndex
     * @param gameboardIndex
     * @param rotation
     * @param flipped
     */
    public static void placementLoaded(int playerIndex,int triHandIndex,int gameboardIndex,int rotation, boolean flipped){
        /*instead of playerIndex being 1 or 2, make it take on the index value of the beginning
         of the corresponding opposition player's tributton index in tributtonArray*/
        if(playerIndex==1){
            playerIndex=ButtonEnum.Tri.GAMETRIHAND0P1.value;
        }
        else{
            playerIndex=ButtonEnum.Tri.GAMETRIHAND0P2.value;
        }
        /*place the trident in the gameboard, the trihandindex will start at 16, because the 16
        gameboard buttons come before it, so subtract 16, then add the player index worked out
        above which will put us at that player's trihand buttons in the array */
        triButtonArray.get(gameboardIndex).place(gameboardIndex,triButtonArray.get(playerIndex+triHandIndex- ButtonEnum.Tri.GAMETRIHAND0.value));
        /*now apply any rotation and flip necessary*/
        /*rotation will be either 0, 1 or 2, and that's how many times we need to rotate*/
        for(int i=0;i<rotation;i++){
            rotate(gameboardIndex);
        }
        if (flipped){
            flip(gameboardIndex);
        }
        /*if the recieving player had highlighted a space, then the sending player put their trident in that
        * space, we need to unhighlight it so that the recieving player can't place there trident on top
        * this problem shouldn't happen anyway since it's turn based, but i haven't implemented any turn based stuff yet */
        if (highlightPosGameboard==gameboardIndex)
        {
            highlightPosGameboard=-1;
        }
        /*set the placed bool to true so that you can't place something on top of it*/
        triButtonArray.get(gameboardIndex).placed=true;

        /*the ownerhsip will have been set in the place method, but this will update the previousOwnership too*/
        triButtonArray.get(gameboardIndex).confirmOwnership();
        /*the ownership of the adjacents might have changed also, so confirm those too */
        triButtonArray.get(triButtonArray.get(gameboardIndex).adjacentIndexVertical).confirmOwnership();
        triButtonArray.get(triButtonArray.get(gameboardIndex).adjacentIndexLeft).confirmOwnership();
        triButtonArray.get(triButtonArray.get(gameboardIndex).adjacentIndexRight).confirmOwnership();
    }
    /**
     * 16 triButtons will have been created, need to set their position and orientation here
     * there is a static variable of CardButton that should have already been set, this variable
     * dealAnimationRectangleHeight gives us the size of the space we have to work with, screen size minus
     * a bit of a margin on the edge of the screen. the game board might be set up differently depending
     * on some of the OptionsStage variables
     */
    public static void setUpGameBoard(){
        Array<Integer> rows = new Array<>();
        rows.clear();
        int total = 0;
        /*this will fill  the row array with a number of items equal to the number of rows we need
        * and each item in the array will hold the number of tridents in that row*/
        for (int i = 0; total < OptionsStage.gameBoardSize; i=i+2) {
            rows.add(i+1);
            total = total + i+1;
        }
        //float edgeLength=(720-CardButton.dealAnimationRowMargin)/(float)(Math.ceil(rows.get(rows.size-1)/2f));
        float edgeLength = CardButton.edgeLength;
        float originX=720/2f  -(edgeLength*(float)(Math.ceil(rows.get(rows.size-1)/2f)))/2;
        float originY=1280/2f - CardButton.dealAnimationRectangleHeight/2 ;
        float incrementX=edgeLength/2;
        float incrementY=(float)(edgeLength*Math.sin(Math.PI/3));
        int index=0;
        /*for each row...*/
        for (int i = 0; i < rows.size; i++) {
            getGB(index).ownership=-1;
            getGB(index).confirmOwnership();
            /*for each trident on the row
            rows.get(i) will equal 1, 3 ,5 , 7 etc - that is the number of tridents on the row*/
            for (int j = 0; j < rows.get(i); j++) {
                getGB(index).setX(originX+ incrementX * (rows.get(rows.size-1)-j - (rows.size-i)) );
                getGB(index).setY(originY+ incrementY * (rows.size-i) );
                //getGB(index).edgeLength=edgeLength;
                getGB(index).updateBounds();
                /*even numbered tridents of any row will be point up*/
                getGB(index).orientation= j%2==0;
                getGB(index).setUpCardButtons(cardButtonArray.get(index*3),cardButtonArray.get(index*3+1),cardButtonArray.get(index*3+2));
                getGB(index).cardButtonArray.get(0).orientation=j%2==0;
                getGB(index).cardButtonArray.get(1).orientation=j%2==0;
                getGB(index).cardButtonArray.get(2).orientation=j%2==0;
                getGB(index).cardButtonArray.get(0).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(0).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(0).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(0).updateBounds();
                getGB(index).cardButtonArray.get(1).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(1).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(1).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(1).updateBounds();
                getGB(index).cardButtonArray.get(2).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(2).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(2).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(2).updateBounds();

                /*if this triButton is not on the last row AND not pointup set the vert */
                if (j%2==0&&index>=OptionsStage.gameBoardSize-rows.get(rows.size-1)) {
                    /*special case*/
                    if(OptionsStage.gameBoardSize==16) {
                        if (index==9){
                        getGB(index).adjacentIndexVertical=11;
                        }
                        else if (index==11){
                            getGB(index).adjacentIndexVertical=9;
                        }
                        else if (index==13){
                            getGB(index).adjacentIndexVertical=15;
                        }
                        else if (index==15){
                            getGB(index).adjacentIndexVertical=13;
                        }
                    }
                }
                else{
                    getGB(index).adjacentIndexVertical = j % 2 == 0 ? rows.get(i) + 1 + index :  index-(rows.get(i) - 1);
                }
                /*if not on the left edge, set the left adjacent*/
                if(j>0) {
                    getGB(index).adjacentIndexRight = index - 1;
                }
                else if(OptionsStage.gameBoardSize==16){
                    /*special case*/
                    if (index==0){
                        getGB(index).adjacentIndexRight = 1;
                    }
                    else if (index==1){
                        getGB(index).adjacentIndexRight = 0;
                    }
                    else if (index==4){
                        getGB(index).adjacentIndexRight = 9;
                    }
                    else if (index==9){
                        getGB(index).adjacentIndexRight = 4;
                    }
                }
                /*if not on the right edge set the right adjacent*/
                if(j<rows.get(i)-1) {
                    getGB(index).adjacentIndexLeft = index + 1;
                }
                else if(OptionsStage.gameBoardSize==16){
                    /*special case*/
                    if (index==0){
                        getGB(index).adjacentIndexLeft = 3;
                    }
                    else if (index==3){
                        getGB(index).adjacentIndexLeft = 0;
                    }
                    else if (index==8){
                        getGB(index).adjacentIndexLeft = 15;
                    }
                    else if (index==15){
                        getGB(index).adjacentIndexLeft = 8;
                    }
                }

                index++;
            }
        }

        /*
        * after setting up the game board we can set up the position of the  place mode buttons*/
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEROTATE.value).setX(720f/2-edgeLength);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEROTATE.value).setY(getGB(0).getY()+edgeLength);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEROTATE.value).setVisible(false);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEFLIP.value).setX(720f/2-edgeLength/2);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEFLIP.value).setY(getGB(0).getY()+edgeLength);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEFLIP.value).setVisible(false);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACECONFIRM.value).setX(720f/2);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACECONFIRM.value).setY(getGB(0).getY()+edgeLength);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACECONFIRM.value).setVisible(false);
    }

    /**
     * helper method to get game board tributtons with less code, starting with index 0 and ending with index 15
     * @return
     */
    private static TriButton getGB(int gameBoardIndex){
        gameBoardIndex = gameBoardIndex + ButtonEnum.Tri.GAMEBOARD0.value;
        return triButtonArray.get(gameBoardIndex);
    }
    public void reset(){

        if (OptionsStage.preAndPostGameCard && triButtonArray.size>OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).resetPreGameCard();
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).resetPostGameCard();
        }
        setPlaceMode(false);

        MyServer.player1CardValues.clear();
        MyServer.player2CardValues.clear();

        triButtonArray.clear();
        createButtons();
        firstOppositionPlayerTriHandLoaded =false;
        allTriHandsLoaded=false;
        player0TriHandReceived = false;
        player1TriHandReceived = false;
        player2TriHandReceived = false;

    }

    public void setPlaceMode(boolean bool){
        placeMode=bool;
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEROTATE.value).setVisible(bool);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACEFLIP.value).setVisible(bool);
        triButtonArray.get(ButtonEnum.Tri.GAMEPLACECONFIRM.value).setVisible(bool);
        if (bool ==false) {
            highlightPosGameboard = -1;
            highlightPosTriHand = -1;
        }
    }
    public void selectGameBoardTrident(int index){
        /*if the one you've tapped is greyed out because something has already been placed there then do nothing*/
        if (highlightPosGameboard != -1&&triButtonArray.get(highlightPosGameboard).placed==false) {
            /*we might have already tried to place a trident so clear that now */
            if (highlightPosTriHand != -1) {
                triButtonArray.get(highlightPosGameboard).cancelPlace();
            }
        }
            /*should always cancel battel before changing the highlightPosGameboard*/
            cancelBattle();
            /*highlight the tapped gameboard trident */
            highlightPosGameboard = index;
        if(triButtonArray.get(highlightPosGameboard).placed==false) {
            /*if a trident in the triarray has been selected ... */
            if (highlightPosTriHand != -1) {
                setPlaceMode(true);
                /*set the gameboard position trident to reflect the chosen trident*/
                triButtonArray.get(highlightPosGameboard).place(highlightPosGameboard,triButtonArray.get(highlightPosTriHand));
            }
        }
        else{
            highlightPosGameboard=-1;
        }
    }
    public void selectTriArrayTrident(int index){
        /*if the one you've tapped is greyed out because it has already been placed then do nothing*/
        if (highlightPosTriHand != -1 && triButtonArray.get(highlightPosTriHand).placed==false) {
            /*we might have already tried to place a trident so clear that now */
            if (highlightPosGameboard != -1) {
                triButtonArray.get(highlightPosGameboard).cancelPlace();
            }
        }
            /*highlight the tapped gameboard trident */
            highlightPosTriHand = index;
        /*if the one you've tapped is greyed out because it has already been placed then do nothing*/
        if(triButtonArray.get(highlightPosTriHand).placed==false) {
            /*if a trident in the gameboard has been selected ... */
            if (highlightPosGameboard != -1) {
                setPlaceMode(true);
                /*set the gameboard position trident to reflect the chosen trident*/
                triButtonArray.get(highlightPosGameboard).place(highlightPosGameboard, triButtonArray.get(highlightPosTriHand));
            }
        }
        else
        {
            highlightPosTriHand=-1;
        }
    }
    
    public static void rotate(int index){
        if(OptionsStage.allowFlipAndRotate) {
            byte tempPosition = triButtonArray.get(index).cardButtonArray.get(0).position;
            triButtonArray.get(index).cardButtonArray.get(0).position = triButtonArray.get(index).cardButtonArray.get(1).position;
            ;
            triButtonArray.get(index).cardButtonArray.get(1).position = tempPosition;

            tempPosition = triButtonArray.get(index).cardButtonArray.get(1).position;
            triButtonArray.get(index).cardButtonArray.get(1).position = triButtonArray.get(index).cardButtonArray.get(2).position;
            ;
            triButtonArray.get(index).cardButtonArray.get(2).position = tempPosition;

            triButtonArray.get(index).cardButtonArray.swap(1, 2);
            triButtonArray.get(index).cardButtonArray.swap(0, 1);

            /**
             * we need to keep track of how the original cards have been rotated for this trident, we
             * need to do this so we can tell the other player the correct position of each card
             */
            if (triButtonArray.get(index).flipped == false) {
                /*if the trident has not been flipped rotation will increment the rotation number through 0, 1 and 2,
                 * increment over 2 will bring it back to 0 using modulus %*/
                triButtonArray.get(index).rotation = (byte) ((triButtonArray.get(index).rotation + 1) % 3);
            } else {
                    /*if the trident is flipped then rotation will decrement instead, i'm not sure how modulus works with minus number in java so
                    i've just added 2 instead of subtracting 1 to ensure it's positive*/
                triButtonArray.get(index).rotation = (byte) ((triButtonArray.get(index).rotation - 1 + 3) % 3);
            }
            Gdx.app.log("GAMESTAGE", "rotation " + triButtonArray.get(index).rotation);
            evaluateBattle(index);
        }
    }
    public static void flip(int index){
        if(OptionsStage.allowFlipAndRotate) {
            byte tempPosition = triButtonArray.get(index).cardButtonArray.get(1).position;
            triButtonArray.get(index).cardButtonArray.get(1).position = triButtonArray.get(index).cardButtonArray.get(2).position;
            ;
            triButtonArray.get(index).cardButtonArray.get(2).position = tempPosition;
            triButtonArray.get(index).cardButtonArray.swap(1, 2);

            /*toggle the flipped boolean*/
            triButtonArray.get(index).flipped = !triButtonArray.get(index).flipped;
            Gdx.app.log("GAMESTAGE", "flip " + triButtonArray.get(index).flipped);
            evaluateBattle(index);
        }
    }
    /** this mehtod should be called after place, rotate or flip, basically when a new trident is added to the gameBoard
     * we need to call this to see if it wins or loses any battles.
     *
     */
    public static void evaluateBattle(int gameBoardIndex)
    {
        Gdx.app.log("BATTLE","gameBoardPos "+gameBoardIndex);
        /*variables should try and make this less confusing */
        int adjacentVertical= triButtonArray.get(gameBoardIndex).adjacentIndexVertical;
        int adjacentLeft= triButtonArray.get(gameBoardIndex).adjacentIndexLeft;
        int adjacentRight= triButtonArray.get(gameBoardIndex).adjacentIndexRight;
        /*save what the current adjacent's ownership is, so we can change back to that if this battle is cancelled*/


        Gdx.app.log("BATTLE","AdjacentGBPos V "+adjacentVertical);
        Gdx.app.log("BATTLE","AdjacentGBPos L "+adjacentLeft);
        Gdx.app.log("BATTLE","AdjacentGBPos R "+adjacentRight);
        /*noramlly in a battle we compare the vertical edge to the vertical of the adjacent card,
        the left with the right
        and the riight with the left,
        but there are special edge cases, if the card is on the right edge, the right card should be compared to the right card of the
        adjacent, instead of the left
        if the card is on the left edge, the left card should be compared to the left  card of the adjacent, instead of the right
        */
        boolean edgeCaseRight=false;
        boolean edgeCaseLeft=false;
        if (gameBoardIndex==0||gameBoardIndex==1||gameBoardIndex==4||gameBoardIndex==9)
        {
            edgeCaseRight=true;
        }
        if (gameBoardIndex==0||gameBoardIndex==3||gameBoardIndex==8||gameBoardIndex==15)
        {
            edgeCaseLeft=true;
        }
        /*for the newly added trident, test if the vertical card value is greater than the
        * vertical card value  of the trident adjacent to the vertical card - if there is one */
        if(triButtonArray.get(adjacentVertical).placed) {
            Gdx.app.log("BATTLE","V value "+triButtonArray.get(gameBoardIndex).cardButtonArray.get(VERTICAL).getPip()+ " adjacent v Value "+triButtonArray.get(adjacentVertical).cardButtonArray.get(VERTICAL).getPip());
            if(triButtonArray.get(gameBoardIndex).cardButtonArray.get(VERTICAL).getPip() > triButtonArray.get(adjacentVertical).cardButtonArray.get(VERTICAL).getPip())
            {
                triButtonArray.get(adjacentVertical).ownership=triButtonArray.get(gameBoardIndex).ownership;
                Gdx.app.log("BATTLE","battle won");

            }
            else
            {
                triButtonArray.get(adjacentVertical).ownership=triButtonArray.get(adjacentVertical).previousOwnership;
                Gdx.app.log("BATTLE","battle lost");
            }
        }
        if(triButtonArray.get(adjacentLeft).placed) {
            Gdx.app.log("BATTLE","L value "+triButtonArray.get(gameBoardIndex).cardButtonArray.get(LEFT).getPip()+ " adjacent R Value "+triButtonArray.get(adjacentLeft).cardButtonArray.get(RIGHT).getPip());
            if(triButtonArray.get(gameBoardIndex).cardButtonArray.get(LEFT).getPip() > triButtonArray.get(adjacentLeft).cardButtonArray.get(edgeCaseLeft?LEFT:RIGHT).getPip())
            {
                triButtonArray.get(adjacentLeft).ownership=triButtonArray.get(gameBoardIndex).ownership;
                Gdx.app.log("BATTLE","battle won");
            }
            else
            {
                triButtonArray.get(adjacentLeft).ownership= triButtonArray.get(adjacentLeft).previousOwnership;
                Gdx.app.log("BATTLE","battle lost");
            }
        }
        if(triButtonArray.get(adjacentRight).placed) {
            Gdx.app.log("BATTLE","R value "+triButtonArray.get(gameBoardIndex).cardButtonArray.get(RIGHT).getPip()+ " adjacent L Value "+triButtonArray.get(adjacentRight).cardButtonArray.get(LEFT).getPip());
            if(triButtonArray.get(gameBoardIndex).cardButtonArray.get(RIGHT).getPip() > triButtonArray.get(adjacentRight).cardButtonArray.get(edgeCaseRight?RIGHT:LEFT).getPip())
            {
                triButtonArray.get(adjacentRight).ownership=triButtonArray.get(gameBoardIndex).ownership;
                Gdx.app.log("BATTLE","battle won");
            }
            else
            {
                triButtonArray.get(adjacentRight).ownership=triButtonArray.get(adjacentRight).previousOwnership;
                Gdx.app.log("BATTLE","battle lost");
            }
        }
    }

    /**
     * if you begin a preliminary battle, but then cancel it we need to reset the ownership or else it will look like
     * the battle actualy took place, this should be called basically everytime before highlightPosGameboard changes
     */
    public static void cancelBattle() {
        if (highlightPosGameboard > -1)
        {
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexVertical).ownership = triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexVertical).previousOwnership;
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).ownership = triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).previousOwnership;
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).ownership = triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).previousOwnership;
        }
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){

        switch(triButtonIndex){
            case GAMENEXTSTAGE: {
                reset();
                stageInterface.goToStage(StageInterface.GAMEOVERSTAGE);
                break;
            }
            case GAMEBOARD0: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD1: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD2: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD3: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD4: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD5: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD6: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD7: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD8: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD9: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD10: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD11: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD12: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD13: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD14: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMEBOARD15: {
                selectGameBoardTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND0: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND1: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND2: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND3: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND4: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND5: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND6: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIHAND7: {
                selectTriArrayTrident(triButtonIndex.value);
                break;
            }
            case GAMETRIPREGAMECARD:{break;}
            case GAMETRIPOSTGAMECARD:{break;}
            case GAMETRIHAND0P1:{break;}
            case GAMETRIHAND1P1:{break;}
            case GAMETRIHAND2P1:{break;}
            case GAMETRIHAND3P1:{break;}
            case GAMETRIHAND4P1:{break;}
            case GAMETRIHAND5P1:{break;}
            case GAMETRIHAND6P1:{break;}
            case GAMETRIHAND7P1:{break;}
            case GAMETRIPREGAMECARDP1:{break;}
            case GAMETRIPOSTGAMECARDP1:{break;}
            case GAMETRIHAND0P2:{break;}
            case GAMETRIHAND1P2:{break;}
            case GAMETRIHAND2P2:{break;}
            case GAMETRIHAND3P2:{break;}
            case GAMETRIHAND4P2:{break;}
            case GAMETRIHAND5P2:{break;}
            case GAMETRIHAND6P2:{break;}
            case GAMETRIHAND7P2:{break;}
            case GAMETRIPREGAMECARDP2:{break;}
            case GAMETRIPOSTGAMECARDP2:{break;}
            case GAMEPLACEROTATE: {

                rotate(highlightPosGameboard);
                break;
            }
            case GAMEPLACEFLIP: {
                flip(highlightPosGameboard);
                break;
            }
            case GAMEPLACECONFIRM: {
                triButtonArray.get(highlightPosGameboard).placed=true;
                triButtonArray.get(highlightPosGameboard).confirmOwnership();
                triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexVertical).confirmOwnership();
                triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).confirmOwnership();
                triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).confirmOwnership();

                triButtonArray.get(highlightPosTriHand).placed=true;
                triButtonArray.get(highlightPosTriHand).greyed=true;
                MyServer.emitPlacement(highlightPosTriHand, highlightPosGameboard,triButtonArray.get(highlightPosGameboard).rotation,triButtonArray.get(highlightPosGameboard).flipped);
                setPlaceMode(false);
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
}