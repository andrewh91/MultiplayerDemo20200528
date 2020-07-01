package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;
import static com.gmail.andrewahughes.StageInterface.TRIDENTBUILDINGSTAGE;


public class DealStage extends Stage {

    boolean visible = false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    /**
     * no animation is playing, this is the default
     */
    final static byte ANIMATIONSTOPPED = 0;
    /**
     * animation has begun, display the cards in a grid
     */
    final byte ANIMATIONDISPLAYCARDS = 1;
    /**
     * animation moves cards to overlap each other in the centre
     */
    final byte ANIMATIONOVERLAPCARDS = 2;

    /**
     * how much time passes in seconds from the start of the animation until
     * ANIMATIONOVERLAPCARD begins
     */
    final static float ANIMATIONOVERLAPCARDSTIMER = 1;
    /**
     * animation pauses while all cards are overlapped to allow user to decide par
     */
    final byte ANIMATIONPAR = 3;
    /**
     * animation moves cards to their correct positions in their player's
     * card hand
     */
    final static byte ANIMATIONDEALCARDS = 4;
    /**
     * how much time passes in seconds from the start of the animation until
     * ANIMATIONDEALCARDS begins
     */
    final static float ANIMATIONDEALCARDSTIMER = 2;
    static byte  ANIMATIONSTAGE = ANIMATIONSTOPPED;
    /**
     * half way through the animation a method called set text will be called
     * this flag ensures it's only called once,
     */
    boolean ANIMATIONSETTEXTFLAG=true;
    boolean ANIMATIONSETPARFLAG=true;
    float animationTimer = 0;
    static int par=0,par0=0,par1=1,par2=2;

    /*when the cards are dealt, add up all the pip values of the cards
    * this will help with working out par */
    static Array<Integer> playerHandValue = new Array<Integer>();
    static int [] handDiff;
    static int[] handValue ;
    static int[] handTruePar ;

    static boolean dealReady=false;
    static boolean parReady=false;

    public static boolean amendPar=false;


    public DealStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer) {
        this.stageInterface = stageInterface;

        this.shapeRenderer =shapeRenderer;
        this.setViewport(viewport);
        this.spriteBatch = batch;


        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);
        createButtons();
    }

    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible) {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.0f, 1.0f, 0.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            MyServer.drawPlayers(spriteBatch);
            /*draw all actors of this stage*/
            drawTriButtons();
            drawAnimation();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*this is the dealAnimationRectangle*/
            shapeRenderer.line(35    ,35      ,685     ,35);
            shapeRenderer.line(685   ,35      ,685     ,1245);
            shapeRenderer.line(685   ,1245    ,35      ,1245);
            shapeRenderer.line(35    ,1245    ,35      ,35);
            /*draw all actors of this stage*/
            drawTriButtonsShape();
            drawAnimationShape();
            shapeRenderer.end();
            MyServer.update();
        }
    }

    void drawTriButtons() {
        for (int i = 0; i < triButtonArray.size; i++) {
            triButtonArray.get(i).draw(spriteBatch, 1.0f);
        }
    }

    void drawAnimation() {

        /*after a certain amount of time, begin the overlap animation */
        if (animationTimer>ANIMATIONOVERLAPCARDSTIMER&& animationTimer <= ANIMATIONDEALCARDSTIMER+ANIMATIONOVERLAPCARDSTIMER){
            ANIMATIONSTAGE=ANIMATIONOVERLAPCARDS;

            /*fade the CardButton's fadeFont so that it becomes more
            * transparent as it overlaps*/
            CardButton.fadeFont.setColor(1,1,1,(1-(animationTimer-ANIMATIONOVERLAPCARDSTIMER)/ANIMATIONDEALCARDSTIMER*4));
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).overlapAnimation((animationTimer-ANIMATIONOVERLAPCARDSTIMER)/ANIMATIONDEALCARDSTIMER);
            }
        }
        /*end the overlap animation and begin the deal animation*/
        if (animationTimer>ANIMATIONDEALCARDSTIMER+ANIMATIONOVERLAPCARDSTIMER){

            /*use this flag to set some things once only*/
            /*as soon as we enter this phase of the animation, we will set the ANIMATIONSTAGE to
            * ANIMATIONPAR, which will pause all the animation, and the timer, while the user
            * interacts with the par buttons, everything will resume when user clicks confirm*/
            if (ANIMATIONSETPARFLAG) {
                ANIMATIONSETPARFLAG = false;
                /*display the par buttons*/
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setVisible(true);
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setVisible(true);
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR).setVisible(true);
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setY(1280 / 2 -35 - (float)(CardButton.dealAnimationTridentEdgeLength*Math.sin(Math.PI/3)));
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setY(1280 / 2 -35 - (float)(CardButton.dealAnimationTridentEdgeLength*Math.sin(Math.PI/3)));
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR).setY(1280 / 2 -35 - (float)(CardButton.dealAnimationTridentEdgeLength*Math.sin(Math.PI/3)));

                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setX(720/2-stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALINCREASEPAR).edgeLength);
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setX(720/2-stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALDECREASEPAR).edgeLength/2);
                stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR). setX(720/2);


                ANIMATIONSTAGE=ANIMATIONPAR;
            }

            if(ANIMATIONSTAGE==ANIMATIONPAR){
                /*don't actually need to do anything here, but the par buttons will be visible.
                * they will be handled in the touch logic*/
            }
            /*this will be set when the user clicks confirm par and then receives the deal from the server*/
            else if (ANIMATIONSTAGE==ANIMATIONDEALCARDS) {
                /*the cardbuttons will have already been set a random number,
                 * this will actually display that number */
                if (ANIMATIONSETTEXTFLAG) {
                    ANIMATIONSETTEXTFLAG = false;
                    /*when the cards are moved to their player's card hand
                     * we need to reserve some space at the top for the trident hand
                     * this method determines how much space */
                    CardButton.setTridentHandHeight();
                    Gdx.app.log("DealStage","set position of all cards in card hand");
                    for (int i = 0; i < Deck.cardArray.size; i++) {
                        TridentBuildingStage.cardButtonArray.get(i).setText();
                        TridentBuildingStage.cardButtonArray.get(i).edgeLength = CardButton.dealAnimationTridentEdgeLength /2;
                        TridentBuildingStage.cardButtonArray.get(i).updateBounds();
                        TridentBuildingStage.cardButtonArray.get(i).setDealAnimationPosition();
                    }
                    /*the TRIDENTBUILDINGSTAGE has a trident hand big enough for 9 tridents
                     * but if we are using less than that many keep the others invisible*/
                    TridentBuildingStage.updatePlayerTridentHand();
                }
                /*fade the text back in as the cards move to their player's card hand*/
                CardButton.fadeFont.setColor(1, 1, 1, ((animationTimer - ANIMATIONDEALCARDSTIMER - ANIMATIONOVERLAPCARDSTIMER - 1) / ANIMATIONDEALCARDSTIMER * 4));
                /* actually don't fade the text back in until we've resolved par*/

                for (int i = 0; i < Deck.cardArray.size; i++) {
                    TridentBuildingStage.cardButtonArray.get(i).moveToPositionAnimation((animationTimer - ANIMATIONDEALCARDSTIMER - ANIMATIONOVERLAPCARDSTIMER) / ANIMATIONDEALCARDSTIMER);
                }
                /*when the animation is complete, we will automatically move on to the next stage */
                if((animationTimer - ANIMATIONDEALCARDSTIMER - ANIMATIONOVERLAPCARDSTIMER) / ANIMATIONDEALCARDSTIMER>1f){
                    stageInterface.goToStage(TRIDENTBUILDINGSTAGE);
                }
            }
        }
        /*if the begin deal button is clicked, display all the cards spread out
        * across the screen*/
        if (ANIMATIONSTAGE == ANIMATIONDISPLAYCARDS) {
            animationTimer+=Gdx.graphics.getDeltaTime();
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).draw(spriteBatch,1.0f);
            }
        }
        /*after a set amount of time the cards will all move to the centre*/
        else if (ANIMATIONSTAGE == ANIMATIONOVERLAPCARDS ){
            animationTimer+=Gdx.graphics.getDeltaTime();
            if(OptionsStage.openMode ) {
                for (int i = 0; i < Deck.cardArray.size; i++) {
                    TridentBuildingStage.cardButtonArray.get(i).draw(spriteBatch, 1.0f);
                }
            }
        }
        /*once all cards have overlapped begin the par consideration*/
        else if (ANIMATIONSTAGE == ANIMATIONPAR ){
            /*don't increment the timer while waiting for user input*/
            //animationTimer+=Gdx.graphics.getDeltaTime();
            if(OptionsStage.openMode ) {
                for (int i = 0; i < Deck.cardArray.size; i++) {
                    TridentBuildingStage.cardButtonArray.get(i).draw(spriteBatch, 1.0f);
                }
            }
        }
        /*once moved to the centre the cards will then move to their
        * position in their player's card hand after the player clicks confirm par*/
        else if (ANIMATIONSTAGE == ANIMATIONDEALCARDS ){
            animationTimer+=Gdx.graphics.getDeltaTime();

            TridentBuildingStage.drawTriButtons(spriteBatch);
            if(OptionsStage.openMode ) {
                for (int i = 0; i < Deck.cardArray.size; i++) {
                    TridentBuildingStage.cardButtonArray.get(i).draw(spriteBatch, 1.0f);
                }
            }
        }
    }
    /**
     * i've made a bit of a temporary animation here
     */
    void drawAnimationShape() {
        if (ANIMATIONSTAGE == ANIMATIONDISPLAYCARDS) {
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).drawShape(shapeRenderer);
            }
        }
        else if (ANIMATIONSTAGE == ANIMATIONOVERLAPCARDS) {
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).drawShape(shapeRenderer);
            }
        }
        else if (ANIMATIONSTAGE == ANIMATIONPAR) {
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).drawShape(shapeRenderer);
            }
        }
        else if (ANIMATIONSTAGE == ANIMATIONDEALCARDS) {
            /*this will draw the trident hand array from the TRIDENTBUILDINGSTAGE*/
            TridentBuildingStage.drawTriButtonsShape(shapeRenderer);
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).drawShape(shapeRenderer);
            }
        }

    }


    /**
     * draw the trident buttons shape - which should just be it's bounds
     */
    void drawTriButtonsShape() {

        for (int i = 0; i < triButtonArray.size; i++) {
            triButtonArray.get(i).drawShape(shapeRenderer);

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
        stageInterface.addTriButton(new TriButton(stageInterface, 0, 0, false, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALNEXTSTAGE), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALNEXTSTAGE).setText("Trident\nBuilding");
        //stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALNEXTSTAGE).setTridentToTextSize();

        stageInterface.addTriButton(new TriButton(stageInterface, 720 / 2, 1280 / 2, true, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALBEGINDEAL), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setText("Wait for\nplayers");
        ///stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setTridentToTextSize();
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).centre();

        /*the par buttons are created here, but i'll need to update the y position later, once we know how big the
        * TRIDENTBUILDINGSTAGE cardbuttons are*/
        stageInterface.addTriButton(new TriButton(stageInterface, 0, 0, true, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALINCREASEPAR), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setText("Increase\nPar");
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setVisible(false);
        /*here we are increasing the size of the increase par button, so it will fit the decrease par button text
        * we are not setting the text to anything, just making sure all 3 par buttons are the same size as the decrease par button*/
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALINCREASEPAR).setTridentToTextSize("Decrease\nPar");
        stageInterface.addTriButton(new TriButton(stageInterface, 0, 0, false, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALDECREASEPAR), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setText("Decrease\nPar");
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALDECREASEPAR).setTridentToTextSize("Decrease\nPar");
        stageInterface.addTriButton(new TriButton(stageInterface, 0 , 0, true, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALCONFIRMPAR), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR).setText("Confirm:"+par+"\nPar");
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALCONFIRMPAR).setTridentToTextSize("Decrease\nPar");
    }

    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     *
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryTriButtonTouch(float x, float y) {

        for (int i = 0; i < triButtonArray.size; i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if (triButtonArray.get(i).triangleHit(x, y)) {
                triButtonArray.get(i).touchLogic(x, y);
                break;
            }


        }
    }

    public void reset() {
        /*reset the animation stages and timer*/
        ANIMATIONSTAGE = ANIMATIONSTOPPED;
        animationTimer = 0;
        /*display the begin deal button*/
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setVisible(true);
        CardButton.fadeFont.setColor(1,1,1,1);
        ANIMATIONSETTEXTFLAG=true;
        ANIMATIONSETPARFLAG=true;

        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALCONFIRMPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALINCREASEPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALDECREASEPAR).setVisible(false);

        playerHandValue.clear();
        dealReady=false;
        parReady=false;

    }

    /** when the begindeal button is clicked;
     * consider the number of players, if the cards are visible to all players,
     * if there is a pre and post game card,
     * shuffle and deal an equal amount of cards to each player,
     * there might be cards left over
     */
    private void beginDeal() {
        /*only take action if all players have joined, or else it won't work properly*/
        if(dealReady) {

            /*hide the begindeal button, it will reappear on reset*/
            stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setVisible(false);

            /*start off the animation*/
            ANIMATIONSTAGE = ANIMATIONDISPLAYCARDS;
            animationTimer = 0;
        }
    }

    /**
     * this will be called from the server when all players have been sent their
     * index numbers
     */
    static void dealReady(){
        if (dealReady==false) {
            dealReady = true;
            /*when all players have joined the begin deal button should say 'begin deal'
            * instead of 'wait for players'*/
            stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setText("Begin\nDeal");

        }
    }

    /**
     * this will be called when all players have clicked confirm par
     */
    static void parReady(){
        if (parReady==false) {
            parReady = true;
        }
    }
    /**
    *assign a player index to all players, this will be called everytime a player goes to the dealStage
    *but it will only do something when the number of players equals the required number of players
     * it will assign the players a number, either 0 1 or 2
     *
     */
    private static void assignPlayerIndex() {
        /*this will be called when each player goes to the dealStage
        * call the gotAllPlayers method which will check if we have the correct number of players
        * if so then it sends a message using the server class, message is received in the index.js
        * which will send an array to all players, the array contains each player id and a unique
        * index number which will be 0, 1 or 2, this index number will be recorded on each player's
        * server class, once that is complete DealStage.dealReady() is called, which sets the
        * dealReady bool to true and calls deal() again*/
        if(dealReady==false) {
            MyServer.gotAllPlayers();
            Gdx.app.log("Deal Stage", "MyServer.player.index: " + MyServer.player.index);
        }

    }
    static void deal(){
        /* this should be called from the server after all players have confirmed par
         * the assignPlayerIndex should have already been called
         * gotAllPlayers will be called everytime a player goes to the deal stage,
         * but that method will only do something if all the players are there,
         * so when the last person goes to the deal stage, it will sort all players
         * out with an index number, as soon as that happens we should prompt player one to
         * send the deal, when it's confirmed that it is sent the other players should receive it
         * but this should only happen after resolving par,
         * so set a flag when all players have a player index, then when all player click begin deal
         * stop the deal halfway and show the par buttons,
         * when all players confirm par, force player one to send the deal */
        if(dealReady&&parReady) {
            /*if this player is player 1, deal the cards, otherwise receive data from player1's deal*/
            if (MyServer.player.index == 0) {
                for (byte p = 0; p < OptionsStage.numberOfPlayers; p++) {
                    for (int i = 0; i < OptionsStage.cardsEach; i++) {
                        TridentBuildingStage.cardButtonArray.get(OptionsStage.cardsEach * p + i).setPlayerIndex(p);
                    }
                }
                Gdx.app.log("Deal Stage", "complete deal");
                for(int i=0; i < TridentBuildingStage.cardButtonArray.size;i++){
                    Gdx.app.log("Deal Stage", "card: "+i + " value: "+ TridentBuildingStage.cardButtonArray.get(i).value+ " pip: "+ TridentBuildingStage.cardButtonArray.get(i).getPip()+ " player: " + TridentBuildingStage.cardButtonArray.get(i).playerIndex);
                }


                Gdx.app.log("Deal Stage", "begin par. par0: "+par0+" par1: "+par1+" par2: "+par2);
                amendPar=false;
                /*resolve par is a huge complex method which attempts to swap cards between players
                * and undealt cards in order to reduce the difference between the total value of a hand and
                * the truePar of each player to 0, if they can't all be reduced to 0 the attempt is made
                * to make them all the same distance away from zero as each other to make it fair.  */
                resolvePar();


                /*player 1 will send the results of the deal to all players, only the value and the
                 * player index of each of the 52 cards are sent*/
                MyServer.emitDeal();
                Gdx.app.log("Deal Stage", "Send deal");
                calculateValueOfHand();


            } else {
                /*if the current player is not player 1 the MyServer class will receive data
                 * from player1's deal, which will alter the TridentBuildingStage's cardbuttonArray*/
                Gdx.app.log("Deal Stage", "receive deal");
            }
        }
    }

    /**
     *
     * @param exclude player index to exclude, we normally will be calling this method to
     *                get a reference to a player other than the current player, so pass
     *                the current player's index in so this won't return that. can pass in
     *                -1 if you don't want to exclude any
     * @param exclude2 the variable we're trying to get is stored in the playerHandValue
     *                 it stores a variable for each player and an additional one for the
     *                 undealt cards, so we might want to exclude that too, OptionsStage.numberOfPLayers
     *                 will exclude the undealt hand
     * @return
     */
    private static int getPlayerWithHighestHandValue(int exclude,int exclude2,int exclude3){
        int highestPlayer =0;
        int highestValue =0;
        for (int p =0; p<playerHandValue.size;p++){
            if(p==exclude || p ==exclude2|| p ==exclude3) {

            }
            else if (playerHandValue.get(p)>highestValue){
                highestValue=playerHandValue.get(p);
                highestPlayer =p;
            }
        }

        return highestPlayer;
    }


    /**
     *
     * @param exclude player index to exclude, we normally will be calling this method to
     *                get a reference to a player other than the current player, so pass
     *                the current player's index in so this won't return that. can pass in
     *                -1 if you don't want to exclude any
     * @param exclude2 the variable we're trying to get is stored in the playerHandValue
     *                 it stores a variable for each player and an additional one for the
     *                 undealt cards, so we might want to exclude that too, OptionsStage.numberOfPLayers
     *                 will exclude the undealt hand
     * @return
     */
    private static int getPlayerWithHighestHandDiff(int exclude,int exclude2,int exclude3){
        int highestPlayer =0;
        int highestValue =0;
        for (int p =0; p<handDiff.length;p++){
            if(p==exclude || p ==exclude2|| p ==exclude3) {

            }
            else if (handDiff[p]>highestValue){
                highestValue=handDiff[p];
                highestPlayer =p;
            }
        }

        return highestPlayer;
    }
    /**
     *
     * @param exclude player index to exclude, we normally will be calling this method to
     *                get a reference to a player other than the current player, so pass
     *                the current player's index in so this won't return that. can pass in
     *      *                -1 if you don't want to exclude any
     *  @param exclude2 the variable we're trying to get is stored in the playerHandValue
     *        it stores a variable for each player and an additional one for the
     *        undealt cards, so we might want to exclude that too, OptionsStage.numberOfPLayers
     *        will exclude the undealt hand
     * @return
     */
    private static int getPlayerWithLowestHandValue(int exclude,int exclude2,int exclude3){
        int lowestPlayer =0;
        int lowestValue =365;
        for (int p =0; p<playerHandValue.size;p++){
            if(p==exclude || p== exclude2|| p ==exclude3){

            }
            else if (playerHandValue.get(p)<lowestValue){
                lowestValue=playerHandValue.get(p);
                lowestPlayer =p;
            }
        }

        return lowestPlayer;
    }

    /**
     *
     * @param exclude player index to exclude, we normally will be calling this method to
     *                get a reference to a player other than the current player, so pass
     *                the current player's index in so this won't return that. can pass in
     *      *                -1 if you don't want to exclude any
     *  @param exclude2 the variable we're trying to get is stored in the playerHandValue
     *        it stores a variable for each player and an additional one for the
     *        undealt cards, so we might want to exclude that too, OptionsStage.numberOfPLayers
     *        will exclude the undealt hand
     * @return
     */
    private static int getPlayerWithLowestHandDiff(int exclude,int exclude2,int exclude3){
        int lowestPlayer =0;
        int lowestValue =365;
        for (int p =0; p<handDiff.length;p++){
            if(p==exclude || p ==exclude2|| p ==exclude3) {

            }
            else if (handDiff[p]<lowestValue){
                lowestValue=handDiff[p];
                lowestPlayer =p;
            }
        }

        return lowestPlayer;
    }
    /**
     * this will help with the par method
     * each card will have been given a playerIndex value, either 1,2 or 3
     * that will tell you who owns the card, a value of 0 means it was not
     * dealt at all
     */
    private static void calculateValueOfHand() {
        playerHandValue.clear();

        /*for each player add a 0 value to the array, combined with the one
        * we will make below, the array should now have 1 more item
        * that the number of players*/
        for (byte p = 0; p < OptionsStage.numberOfPlayers; p++) {
            playerHandValue.add(0);
        }

        /*add one player to the player hand value array,
         * this will intercept any cards that were not dealt*/
        playerHandValue.add(0);
        int playerHandValueIndex=0;
        /*for all 52 cards*/
        for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
            /*the default player index is -1, any undealt cards will still have -1 playerIndex, so
            * change the variable used to access the playerHandValue array to = numberOfPlayers*/
            if((int) TridentBuildingStage.cardButtonArray.get(i).playerIndex>-1) {
                playerHandValueIndex = (int) TridentBuildingStage.cardButtonArray.get(i).playerIndex;
            }
            else{
                playerHandValueIndex=OptionsStage.numberOfPlayers;
            }
            /*basically, for each card, this will add the value of the card to the
            relevant item in the playerHandValue array*/
            playerHandValue.set(playerHandValueIndex,playerHandValue.get(playerHandValueIndex)+TridentBuildingStage.cardButtonArray.get(i).value%13+1);
        }
        for (int i = 0; i < playerHandValue.size; i++) {
            if(i==OptionsStage.numberOfPlayers){
                Gdx.app.log("dealStage", "value of undealt cards = " + playerHandValue.get(i));
            }
            else {
                Gdx.app.log("dealStage", "value of player" + i + "'s cards = " + playerHandValue.get(i));
            }
        }
        Gdx.app.log("dealStage", "value of all" + (playerHandValue.get(0)+playerHandValue.get(1)+playerHandValue.get(2)+(playerHandValue.size>3?playerHandValue.get(3):0)));

        if(OptionsStage.numberOfPlayers==2){
            handValue = new int[]{playerHandValue.get(0), playerHandValue.get(1)};
            handTruePar = new int[]{7 * OptionsStage.cardsEach + par0,7 * OptionsStage.cardsEach + par1};
            handDiff = new int[]{handValue[0]-handTruePar[0],handValue[1]-handTruePar[1]};

            Gdx.app.log("Deal Stage", "hand value "+handValue[0]+"hand truePar "+handTruePar[0]+"hand diff "+handDiff[0]);
            Gdx.app.log("Deal Stage", "hand value "+handValue[1]+"hand truePar "+handTruePar[1]+"hand diff "+handDiff[1]);
        }
        else if(OptionsStage.numberOfPlayers==3) {
            /*total value of all cards in the player's hand*/
            handValue = new int[]{playerHandValue.get(0), playerHandValue.get(1), playerHandValue.get(2)};
            /*to get the true par we need to add the average total value of a hand,
            364 is the total value of all cards
            There are 52 cards so on average a card is worth 7
            So to work out the true par it’s 7*cardsEach + par
            */
            handTruePar = new int[]{7 * OptionsStage.cardsEach + par0,7 * OptionsStage.cardsEach + par1,7 * OptionsStage.cardsEach + par2};
            /*the difference between the truePar and the actual value
             * if this is positive it means we need to lower the value of the hand
             * if it's negative it means we need to raise the value of the hand*/
            handDiff = new int[]{handValue[0]-handTruePar[0],handValue[1]-handTruePar[1],handValue[2]-handTruePar[2]};

            Gdx.app.log("Deal Stage", "hand value "+handValue[0]+"hand truePar "+handTruePar[0]+"hand diff "+handDiff[0]);
            Gdx.app.log("Deal Stage", "hand value "+handValue[1]+"hand truePar "+handTruePar[1]+"hand diff "+handDiff[1]);
            Gdx.app.log("Deal Stage", "hand value "+handValue[2]+"hand truePar "+handTruePar[2]+"hand diff "+handDiff[2]);
        }

    }

    /**
     * this will be called in the tributton class,
     *
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex) {

        switch (triButtonIndex) {
            case DEALNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.TRIDENTBUILDINGSTAGE);
                break;
            }
            case DEALBEGINDEAL:
                beginDeal();
                break;
            case DEALINCREASEPAR:
                increasePar();
                break;
            case DEALDECREASEPAR:
                decreasePar();
                break;
            case DEALCONFIRMPAR:
                confirmPar();
                break;
            default:
                Gdx.app.log("Example", "DEFAULT " + triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
    private void increasePar(){
        par++;
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALCONFIRMPAR).setText("Confirm:"+par+"\nPar");
    }
    private void decreasePar(){
        par--;
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALCONFIRMPAR).setText("Confirm:"+par+"\nPar");
    }

    /**called when user clicks the confirm par button
     * will emit this player's chosen par to the server
     * */
    private void confirmPar(){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALCONFIRMPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALINCREASEPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALDECREASEPAR).setVisible(false);
        MyServer.emitConfirmPar(par);
    }

    /**
     * this will be called by the server once the server has received all the pars and worked out each player's correct par
     * @param par0a
     * @param par1a
     * @param par2a
     */
    static void parConfirmedByServer(int par0a,int par1a, int par2a){
        Gdx.app.log("Deal Stage","player index "+ MyServer.player.index+" chosen par was "+par);
        parReady();
        if(MyServer.player.index ==0){
            par = par0a;
        }
        else if(MyServer.player.index ==1){
            par = par1a;
        }
        else if(MyServer.player.index ==2){
            par = par2a;
        }
        par0=par0a;
        par1=par1a;
        par2=par2a;

        Gdx.app.log("Deal Stage","Server says par is : "+par);
        Gdx.app.log("Deal Stage","par 0 : "+par0);
        Gdx.app.log("Deal Stage","par 1 : "+par1);
        Gdx.app.log("Deal Stage","par 2 : "+par2);
        deal();
    }

    /**
     * this will be called from the server once the deal has been received from the server
     */
    static void dealLoaded(){

        Gdx.app.log("Deal Stage", "deal received");
        for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
            Gdx.app.log("Deal Stage", "card: " + i + " value: " + TridentBuildingStage.cardButtonArray.get(i).value + " pip: " + TridentBuildingStage.cardButtonArray.get(i).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(i).playerIndex);
        }
        /*should only call this once deal data received*/
        ANIMATIONSTAGE=ANIMATIONDEALCARDS;
    }

    /**
     * this will be the first method called in the deal stage (after resetting the deal stage)
     * amend the cards in the tridentBuildingStage's cardbuttonarray
     * will set up the default positions for the deal animation
     * next method to do with the deal with be beginDeal which is only called on clicking the
     * beginDeal button
     */
    public void amendCardsForDealStageAnimation() {
        /*this will set up a rectangle in which the card buttons will be arranged for the
         * deal animation*/
        CardButton.setDealAnimationRectangle(35, 35, 720 - 70, 1280 - 70);
        CardButton.updateBounds();
        /*loop through the tridentBuildingStage's cardButtonArray setting all the values
         * so the cardButtons are spread out and oriented correctly */
        for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
            TridentBuildingStage.cardButtonArray.get(i).setDealAnimationValues(i);
        }

        /*this is where the cards are shuffled*/
        amendCardsForDeal();
        /*we will deal the cards to the players here,
        but the effect of the deal won't be visible yet*/
        assignPlayerIndex();
    }

    /**
     * once the cards have been dealt to the players and the animation plays out,
     * the cards should appear in each player's cardHand, ready to be manually added to the trident hand
     *
     */
    public void amendCardsForTridentStage(){
        /*for each player, get data from the player to determine
         * the location of the cardButtons */
        for (HashMap.Entry<String, Player> entry : MyServer.friendlyPlayers.entrySet()) {
            for (int i = 0; i < OptionsStage.cardsEach; i++) {
                /*first of all need to reamend the edgeLength*/
            }
        }
    }

    /**
     * assign the random value to the cardbutton, next step
     * after this method is to deal the cards to the players
     * use OptionsStage.cardsEach to figure out how many cards
     * to give each player, then before setting the value of each cardbutton
     * we can sort that player's cards.  this results in player 1's sorted cards
     * being in teh array first, followed by player 2's sorted cards etc
     */
    public static void amendCardsForDeal() {

        Deck.shuffle();
        Array<Byte> tempArray = new Array<>();
        for(int j=0;j<OptionsStage.numberOfPlayers;j++) {
            tempArray.clear();
            for (int i = 0; i < OptionsStage.cardsEach; i++) {
                tempArray.add(Deck.randomCardArray.get(j*OptionsStage.cardsEach+i));
            }
            tempArray.sort();
            for (int k =0; k < tempArray.size;k++){
                TridentBuildingStage.cardButtonArray.get(j*OptionsStage.cardsEach+k).setValue(tempArray.get(k));
            }
        }
        /*also add any undealt cards*/
        tempArray.clear();
        for(int i =OptionsStage.numberOfPlayers*OptionsStage.cardsEach; i<52;i++){
            tempArray.add(Deck.randomCardArray.get(i));
        }
        tempArray.sort();
        int j=0;
        for(int i =OptionsStage.numberOfPlayers*OptionsStage.cardsEach; i<52;i++){
            TridentBuildingStage.cardButtonArray.get(i).setValue(tempArray.get(j));
            j++;
        }
    }

    public static void resolvePar(){
        /* divide the totalHandDiff by the
         * numberOfPlayers and set new truePar for each player*/
        calculateValueOfHand();
        Gdx.app.log("Deal Stage a", "currentPar, par0 " + par0 + "par1 " + par1 +"par2 " + par2 );
        int totalDiff = handDiff[0]+handDiff[1]+(handDiff.length>2?handDiff[2]:0);
        Gdx.app.log("Deal Stage a", "totalDiff "+totalDiff );
        int amendTruePar = (int)Math.round((float)totalDiff/(float)OptionsStage.numberOfPlayers);
            par0=par0+amendTruePar;
            par1=par1+amendTruePar;
            par2=par2+amendTruePar;
        Gdx.app.log("Deal Stage a", "amended Par, par0 " + par0 + "par1 " + par1 +"par2 " + par2 );

        calculateValueOfHand();
        /*work out the total diff again*/
        totalDiff = handDiff[0]+handDiff[1]+(handDiff.length>2?handDiff[2]:0);
        Gdx.app.log("Deal Stage a", "new totalDiff "+totalDiff );
        /*the total diff should now be either -1, 0 or +1 i think
        if the total diff is not 0 that means we can't reduce all player's hand
        diff by swapping cards just between the players, however we can swap
        between the undealt cards and a player, and rather than doing that at the end
        we should do it now*/
        /**
         * this boolean is used only to break the nested for loops when we swap one player
         * card with one undealt card
         */
        /*if the total diff is not 0, we will swap a card between one player and the undealt
        * hand so that the total diff becomes 0*/
        if(totalDiff!=0) {
            boolean swappedWithUndealt = false;
            /*for every undealt card*/
            for (int i = 0; i < 52 - OptionsStage.numberOfPlayers * OptionsStage.cardsEach; i++) {
                /*find the value of the undealt card*/
                int undealtCardIndex = TridentBuildingStage.getNthLowestCard(OptionsStage.numberOfPlayers, i);
                /*for each player*/
                for (int j = 0; j < OptionsStage.numberOfPlayers; j++) {
                    /*for each card*/
                    for (int k = 0; k < OptionsStage.cardsEach; k++) {
                        /*see if the card is equal to the undealt card + the totalDiff, if so swap then cancel these for loops*/
                        int playerCardIndex = TridentBuildingStage.getNthLowestCard(j, k);
                        if (TridentBuildingStage.cardButtonArray.get(playerCardIndex).getPip() == TridentBuildingStage.cardButtonArray.get(undealtCardIndex + totalDiff).getPip()) {
                            Gdx.app.log("Deal Stage a", "the total diff was " + totalDiff + " so swapped player" + j + "'s card index " + playerCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(playerCardIndex).getPip() + " with undealt card index " + undealtCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(undealtCardIndex).getPip());
                            TridentBuildingStage.swapCards(playerCardIndex, undealtCardIndex);
                            calculateValueOfHand();
                            swappedWithUndealt = true;
                            break;
                        }
                    }
                    if (swappedWithUndealt) {
                        break;
                    }
                }

                if (swappedWithUndealt) {
                    break;
                }
                /*if none of the player's cards are suitable, which could only happen if there where several undealt cards*/
                /*then this will loop again and set the undealtCardIndex to the next lowest card in the undealt hand and try again*/
            }
        }
            int closestApproxValue=365;
            int closestApproxIndex=-1;
            int closestApproxIndex2=-1;
            int lowestCardOppHandIndex ;
            /*we will run the below for loop at most twice, on the second time we
             * want to exclude the player we just resolved the handDiff for */
        int playerWithResolvedHand=-1;
        int playerWithResolvedHand1=-1;

            /*this will repeat for (numberOfPlayers) times, it will loop round when we succeed in getting one player's
             * handDiff to 0*/
            for(int numberOfPlayersHandDiffResolvedFor=0;numberOfPlayersHandDiffResolvedFor<OptionsStage.numberOfPlayers;numberOfPlayersHandDiffResolvedFor++) {

                int targetCardIndex = -1;
                /*
                int highDiffPlayer= getPlayerWithHighestHandDiff(playerWithResolvedHand,playerWithResolvedHand1,OptionsStage.numberOfPlayers);
                int lowDiffPlayer = getPlayerWithLowestHandDiff(highDiffPlayer, playerWithResolvedHand, OptionsStage.numberOfPlayers);
                */
                int highDiffPlayer=numberOfPlayersHandDiffResolvedFor;
                int lowDiffPlayer = numberOfPlayersHandDiffResolvedFor+1;
                /*if we are on the last player, force the lowDiffPlayer to be the undealt hand*/
                if(numberOfPlayersHandDiffResolvedFor==OptionsStage.numberOfPlayers-1) {
                    Gdx.app.log("Deal Stage", "last player, will have to trade with undealt");
                    lowDiffPlayer = OptionsStage.numberOfPlayers;
                }
                Gdx.app.log("Deal stage","numberOfPlayersHandDiffResolvedFor "+numberOfPlayersHandDiffResolvedFor);
                /*if the player with the highest hand diff is positive and not 0, we need to lower it's value*/
                if (handDiff[highDiffPlayer] > 0) {
                    Gdx.app.log("Deal stage +","");

                        /*the following for loop will find the player with the highest hand diff call that player1, and
                        the player with the lowest hand diff call that player2.
                        it will compare the lowest card in player2 with the highest card in player1
                        if the difference between these cards is less than the player1 hand difference
                        then just swap them instantly - the for loop will continue infinitely if the
                        difference between the cards is always less than the player1 hand difference, but that
                        difference will become lower with each loop, so it should not happen infinitely.
                        if the difference between these cards is more than the player1 hand difference
                        then swapping them would make the the player1 handDiff negative, so instead
                        try and find a value in player1's card array that when swapped with player2's lowest card
                        will reduce the player1 handDiff to exactly 0 . if such a card can not be found, we find the
                        next lowest card in the player2 hand array and repeat. such a card should eventually be found
                        and we can break the for loop when the player1 handDiff becomes 0, but if no such card can be found
                        which i think would be unlikley or maybe impossible, just display a message to that effect
                        and shuffle the cards and start all over.
                        if we do get a player to handDiff =0, then for a 3 player game we can run through this one more time
                        so we have 2 players on handDiff=0, the 3rd player would need to swap with the undealt cards, and if
                        it can't resolve it's handDiff with those cards then we need another additional solution
                        * */

                        int passTotal = OptionsStage.cardsEach-1;
                        /*if we are on the last player adjust the amount of passes we can do */
                    if(numberOfPlayersHandDiffResolvedFor==OptionsStage.numberOfPlayers-1) {
                         passTotal = 52 - OptionsStage.numberOfPlayers*OptionsStage.cardsEach-1;
                    }

                    for (int passes = 0; passes < passTotal; ) {
                        /*find the index of the nth lowest card in the player with the lowest hand diff's hand
                         * where n is the number of times we've had to sweep through this statement ergo the
                         * number of times we have failed to find a suitable card*/
                        lowestCardOppHandIndex = TridentBuildingStage.getNthLowestCard(lowDiffPlayer, passes);

                        Gdx.app.log("Deal Stage +", "player " + lowDiffPlayer + "handDiff "+handDiff[lowDiffPlayer]+" that player's lowest card " + lowestCardOppHandIndex + " pip " + TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip());

                        /*if the handDiff1 is greater than the first chosen player's highest card minus the second chosen
                         * player's lowest card, then we will need to swap more than one card - meaning
                         * if we swap the first player's highest card with the other player's lowest card we
                         * will need to make at least one additional swap*/
                        int highestCard1Index = TridentBuildingStage.getNthLowestCard(highDiffPlayer, OptionsStage.cardsEach - 1);
                        Gdx.app.log("Deal Stage +", "player " + highDiffPlayer +" handDiff "+handDiff[highDiffPlayer]+ " that player's highest card index " + highestCard1Index + " pip " + TridentBuildingStage.cardButtonArray.get(highestCard1Index).getPip());
                        /*if the highest card in one hand is the same value as the lowest card in the other and we swap them we will get an infinite loop since
                        * the same swap will occur next time as well, so make sure the 2 cards have different values, specifically the highest card must be
                        * greater than the lowest card*/
                        if (handDiff[highDiffPlayer] > TridentBuildingStage.cardButtonArray.get(highestCard1Index).getPip() - TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip() && TridentBuildingStage.cardButtonArray.get(highestCard1Index).getPip() > TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip()) {
                            Gdx.app.log("Deal Stage +", "can't resolve hand diff in one swap as hand diff is too great ");
                            /*swap the cards*/
                            Gdx.app.log("Deal Stage +", "swap 2 cards. "+ highDiffPlayer + " card " + highestCard1Index + " pip " + TridentBuildingStage.cardButtonArray.get(highestCard1Index).getPip()+ " player" + lowDiffPlayer + " card " + lowestCardOppHandIndex+" pip " + TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip());
                            TridentBuildingStage.swapCards(highestCard1Index, lowestCardOppHandIndex);
                            calculateValueOfHand();

                            /*
                            for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
                                Gdx.app.log("Deal Stage", "card: " + i + " value: " + TridentBuildingStage.cardButtonArray.get(i).value + " pip: " + TridentBuildingStage.cardButtonArray.get(i).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(i).playerIndex);
                            }
                            */

                        } else {
                            Gdx.app.log("Deal Stage +", "try and resolve hand diff in one swap... attempt: "+passes);
                            /*if the handDiff1 is low enough to be reduced to 0 in one go,
                             * considering the lowest value in the opponent's hand that we already
                             * selected, and considering the handDiff, find what value of card we need to swap*/
                            int targetValue = TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip() + handDiff[highDiffPlayer];
                            Gdx.app.log("Deal stage +","targetPip "+targetValue);
                            /*default is -1, it will be set to a number between 0 and 51 if we
                             * find a suitable card, if not it will remain -1*/
                            targetCardIndex = -1;
                            /*this is just a for loop through the player with the highest hand diff's cards
                             * we will test each card's pip value to see if one is equal to the target value
                             * if we find one we can swap it and will have set this player's handDiff to 0
                             * if we can't find one we will need to find a different lowestCardOppHandIndex*/
                            for (int i = highDiffPlayer * OptionsStage.cardsEach; i < highDiffPlayer * OptionsStage.cardsEach + OptionsStage.cardsEach; i++) {
                                if (TridentBuildingStage.cardButtonArray.get(i).getPip() == targetValue) {
                                    targetCardIndex = i;
                                    if(playerWithResolvedHand==1) {
                                        playerWithResolvedHand = highDiffPlayer;
                                    }
                                    else{
                                        playerWithResolvedHand1 = highDiffPlayer;
                                    }
                                    Gdx.app.log("Deal Stage +", "swap 2 cards. player" + highDiffPlayer + " card " + targetCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(targetCardIndex).getPip()+ " player" + lowDiffPlayer + " card " + lowestCardOppHandIndex+" pip " + TridentBuildingStage.cardButtonArray.get(lowestCardOppHandIndex).getPip());
                                    TridentBuildingStage.swapCards(targetCardIndex, lowestCardOppHandIndex);

                                    /*
                                    for (int j = 0; j < TridentBuildingStage.cardButtonArray.size; j++) {
                                        Gdx.app.log("Deal Stage", "card: " + j + " value: " + TridentBuildingStage.cardButtonArray.get(j).value + " pip: " + TridentBuildingStage.cardButtonArray.get(j).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(j).playerIndex);
                                    }
                                    */

                                    calculateValueOfHand();
                                    break;
                                }
                                else{
                                    Gdx.app.log("Deal stage +","pip does not equal target, cardIndex "+i+", pip "+TridentBuildingStage.cardButtonArray.get(i).getPip());
                                }
                            }
                            /*if after the for loop the targetCardIndex is still -1, we know we didn't find a suitable card
                             * we should set a new lowestCardOppHandIndex, one that is next lowest after the previous one
                             * then we should run all this again*/
                            if (targetCardIndex == -1) {
                                Gdx.app.log("Deal stage +","didn't find a suitable card, so find the next lowest card for player"+lowDiffPlayer);
                                passes++;

                            }
                            /*if we did set targetCardIndex then we must have resolved the handDiff, so  break the for loop*/
                            else {
                                /*break the 'passes' for loop*/
                                break;
                            }

                        }
                    }
                    /*i'm not sure if this can happen, it should at least be unlikely,
                     * if we are unable to resolve the handDiff because we run out of
                     * cards in the opponent's hand then display a message and shuffle
                     * and start the deal all over again.
                     * note : for this to happen we would have to have swapped enough cards so
                     * that the handDiff is now low enough to be resolved in one more swap,
                     * but then we have gone through all the cards in one player's array,
                     * compared each of them to all the cards in the
                     * other player's array and not find any that could be swapped to make the
                     * handDiff 0 , also note that for this section of the resolve par the
                     * undealt hands has been excluded */
                    if (targetCardIndex == -1) {
                        /*theoretically with the new totalDiff and amendPar methods i have at teh
                        * top of resolvePar, this following log should never be reached*/
                        Gdx.app.log("WARNING Deal Stage +", "WARNING: unable to swap cards to resolve handDiff, will attempt to shuffle cards and deal again");
                        amendCardsForDeal();
                        deal();
                    }
                }
                /*if the highest hand diff is negative and not 0*/
                else if (handDiff[highDiffPlayer] < 0) {
                    Gdx.app.log("Deal stage -","");
                        /*logically if the player with the highest hand diff's hand diff is negative then
                        all player's hand diff must be negative, so would have to trade with the undealt hand

                        the following for loop will find the player with the highest hand diff call that player1
                        it will compare the highest card in the undealthand with the lowest card in player1
                        if the difference between these cards is less than the player1 hand difference
                        then just swap them instantly - the for loop will continue infinitely if the
                        difference between the cards is always less than the player1 hand difference, but that
                        difference will become lower with each loop, so it should not happen infinitely.
                        if the difference between these cards is more than the player1 hand difference
                        then swapping them would make the the player1 handDiff positive, so instead
                        try and find a value in player1's card array that when swapped with the undealt hands's highest card
                        will reduce the player1 handDiff to exactly 0 . if such a card can not be found, we find the
                        next highest card in the undealt hand array and repeat. such a card should eventually be found
                        and we can break the for loop when the player1 handDiff becomes 0, but if no such card can be found
                        which i think is much more likely now that we only have relatively few cards in to swap with, just the
                        undealt ones, so i should make a solution that could swap a pair of cards with on of the previous players
                        such that the previous player's total value does not change but so that the current player has more suitable
                        cards that are better able to be swapped with the undealt cards
                        * */
                    int highestCardInUndealtIndex;
                    int passTotal = OptionsStage.cardsEach-1;
                    /*if we are on the last player adjust the amount of passes we can do */
                    if(numberOfPlayersHandDiffResolvedFor==OptionsStage.numberOfPlayers-1) {
                        passTotal = 52 - OptionsStage.numberOfPlayers*OptionsStage.cardsEach-1;
                    }
                    for (int passes = 0; passes < passTotal; ) {
                        /*this will find the index position of the highest pip value card in the undealt hand
                         * on the next loop it will be the second highest etc*/
                        highestCardInUndealtIndex = TridentBuildingStage.getNthLowestCard(lowDiffPlayer, passTotal-passes);
                        Gdx.app.log("Deal Stage -", "player " + lowDiffPlayer + " handDiff "+handDiff[lowDiffPlayer]+ " that player's highest card " + highestCardInUndealtIndex + " pip " + TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip());

                        /*if the handDiff1 is smaller than the player's lowest card minus the undealt
                         * hand's highest card, then we will need to swap more than one card - meaning
                         * if we swap the  player's lowest card with the undealt hand's highest card we
                         * will need to make at least one additional swap*/
                        int lowestCardIndex = TridentBuildingStage.getNthLowestCard(highDiffPlayer, passes);
                        Gdx.app.log("Deal Stage -", "player " + highDiffPlayer + " handDiff "+handDiff[highDiffPlayer]+ " that player's lowest card index " + lowestCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(lowestCardIndex).getPip());
                        if (handDiff[highDiffPlayer] < TridentBuildingStage.cardButtonArray.get(lowestCardIndex).getPip() - TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip() && TridentBuildingStage.cardButtonArray.get(lowestCardIndex).getPip() < TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip()) {
                            Gdx.app.log("Deal Stage -", "can't resolve hand diff in one swap as hand diff is too small ");
                            /*swap the cards*/
                            Gdx.app.log("Deal Stage -", "swap 2 cards. "+ highDiffPlayer + " card " + lowestCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(lowestCardIndex).getPip()+ " player" + lowDiffPlayer + " card " + highestCardInUndealtIndex+" pip " + TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip());
                            TridentBuildingStage.swapCards(lowestCardIndex, highestCardInUndealtIndex);
                            calculateValueOfHand();
                            /*
                            for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
                                Gdx.app.log("Deal Stage -", "card: " + i + " value: " + TridentBuildingStage.cardButtonArray.get(i).value + " pip: " + TridentBuildingStage.cardButtonArray.get(i).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(i).playerIndex);
                            }
                            */
                        } else {
                            Gdx.app.log("Deal Stage -", "try and resolve hand diff in one swap... attempt: "+passes);
                            /*if the handDiff1 is close enough to 0 to be reduced to 0 in one go,
                             * considering the highest value in the opponent's hand that we already
                             * selected, and considering the handDiff, find what value of card we need to swap*/
                            int targetValue = TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip() + handDiff[highDiffPlayer];
                            Gdx.app.log("Deal stage -","targetPip "+targetValue);
                            /*default is -1, it will be set to a number between 0 and 51 if we
                             * find a suitable card, if not it will remain -1*/
                            targetCardIndex = -1;
                            int playerIndex = highDiffPlayer;
                            /*this is just a for loop through the player with the highest hand diff's cards
                             * we will test each card's pip value to see if one is equal to the target value
                             * if we find one we can swap it and will have set this player's handDiff to 0
                             * if we can't find one we will need to find a different highestCardInUndealtIndex*/
                            for (int i = playerIndex * OptionsStage.cardsEach; i < playerIndex * OptionsStage.cardsEach + OptionsStage.cardsEach; i++) {
                                if (TridentBuildingStage.cardButtonArray.get(i).getPip() == targetValue) {
                                    targetCardIndex = i;
                                    if (playerWithResolvedHand==-1) {
                                        playerWithResolvedHand = highDiffPlayer;
                                    }
                                    else{
                                        playerWithResolvedHand1 = highDiffPlayer;
                                    }
                                    Gdx.app.log("Deal Stage -", "swap 2 cards. player" + highDiffPlayer + " card " + targetCardIndex + " pip " + TridentBuildingStage.cardButtonArray.get(targetCardIndex).getPip()+ " player" + lowDiffPlayer + " card " + highestCardInUndealtIndex+" pip " + TridentBuildingStage.cardButtonArray.get(highestCardInUndealtIndex).getPip());
                                    TridentBuildingStage.swapCards(targetCardIndex, highestCardInUndealtIndex);
                                    for (int j = 0; j < TridentBuildingStage.cardButtonArray.size; j++) {
                                        Gdx.app.log("Deal Stage -", "card: " + j + " value: " + TridentBuildingStage.cardButtonArray.get(j).value + " pip: " + TridentBuildingStage.cardButtonArray.get(j).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(j).playerIndex);
                                    }
                                    calculateValueOfHand();
                                    break;
                                }
                                else{
                                    Gdx.app.log("Deal stage -","pip does not equal target, pip "+TridentBuildingStage.cardButtonArray.get(i).getPip());

                                    /*record how close we came to reaching handDiff =0, and the index of the card in the player and undealt
                                     * hand, so if we can't reach 0 exactly we can at least try and get closer*/
                                    if(Math.abs(TridentBuildingStage.cardButtonArray.get(i).getPip()-targetCardIndex)<closestApproxValue) {
                                        closestApproxValue = Math.abs(TridentBuildingStage.cardButtonArray.get(i).getPip() - targetCardIndex);
                                        closestApproxIndex = i;
                                        closestApproxIndex2 = highestCardInUndealtIndex;
                                    }
                                }
                            }
                            /*if after the for loop the targetCardIndex is still -1, we know we didn't find a suitable card
                             * we should set a new lowestCardOppHandIndex, one that is next lowest after the previous one
                             * then we should run all this again*/
                            if (targetCardIndex == -1) {
                                Gdx.app.log("Deal stage -","didn't find a suitable card, so find the next lowest card for player"+lowDiffPlayer);
                                passes++;

                            }
                            /*if we did set targetCardIndex then we must have resolved the handDiff, so  break the for loop*/
                            else {
                                /*break the 'passes' for loop*/
                                break;
                            }

                        }
                    }
                    /* if we are unable to resolve the handDiff because we run out of
                     * cards in the undealt hand then this will swap the 2 cards that will get us closest
                     * to handDiff=0
                     */
                    if (targetCardIndex == -1) {
                        /*theoretically with the new totalDiff and amendPar methods i have at teh
                         * top of resolvePar, this following log should never be reached*/
                        Gdx.app.log("WARNING Deal Stage -", "WARNING: unable to swap cards to resolve handDiff, will attempt to use the best cards available to get as close to handDiff= 0 as possible");
                        Gdx.app.log("Deal Stage c", "player "+highDiffPlayer+" unable to reach handDiff=0, handDiff = " + handDiff[highDiffPlayer] );

                        /*if the closest approx values have been set then use those to swap cards to get us closer to 0*/
                        if(closestApproxIndex>-1 && closestApproxIndex2>-1) {
                            TridentBuildingStage.swapCards(closestApproxIndex, closestApproxIndex2);
                            Gdx.app.log("Deal Stage c", "swap 2 cards- closest approx. player" + highDiffPlayer + " card " + closestApproxIndex + " pip " + TridentBuildingStage.cardButtonArray.get(closestApproxIndex).getPip() + " player" + lowDiffPlayer + " card " + closestApproxIndex2 + " pip " + TridentBuildingStage.cardButtonArray.get(closestApproxIndex2).getPip());
                            for (int j = 0; j < TridentBuildingStage.cardButtonArray.size; j++) {
                                Gdx.app.log("Deal Stage c", "card: " + j + " value: " + TridentBuildingStage.cardButtonArray.get(j).value + " pip: " + TridentBuildingStage.cardButtonArray.get(j).getPip() + " player: " + TridentBuildingStage.cardButtonArray.get(j).playerIndex);
                            }
                            calculateValueOfHand();
                        }
                        Gdx.app.log("Deal Stage c", "current Par, par0" + par0 + "par1" + par1 +"par2" + par2 );



                        /*only do this once per deal, run through the whole resolve par again,with
                        * the amended par*/
                        /*
                        if(amendPar==false) {
                            amendPar=true;
                            Gdx.app.log("Deal Stage ", "run resolve par again from the start with new amended par" );

                            resolvePar();
                        }
                        else{
                            Gdx.app.log("Deal stage c","would run resolvePar() again, but we've already ran through it twice");
                        }
                        */

                        /*first of all i need to know can the handDiff of the last player
                         * be so high that the cards in the undealt hand would not be able
                         * to resolve it even if those cards were as high as possible?
                         * well i suppose i don't need to know if it's possible i just need to make
                         * something happen to even the cards out in this situation */

                        /*in fact instead of swapping cards with other players just to try and
                         * swap them again with the undealt hand, another idea would be to
                         * just raise or lower all player's handDiff until they are an
                         * approximately equal amount away from 0, obviously we would still have
                         * to work within the constraint of having few undealt cards to swap
                         * so if 2 players had handDiff 0, and 1 player had handDiff -10
                         * divide that by 3 and round down, = 3, so swap cards equal to 3 until
                         * all players are on either -3 or -4.
                         * */

                                /*
                                Gdx.app.log("WARNING Deal Stage", "WARNING: unable to swap cards to resolve handDiff, will attempt to shuffle cards and deal again");
                                amendCardsForDeal();
                                deal();*/
                    }

                } else if (handDiff[highDiffPlayer] == 0) {
                    Gdx.app.log("Deal stage 0","player "+highDiffPlayer+" handDiff happily is already 0");
                }
                Gdx.app.log("Deal Stage", (numberOfPlayersHandDiffResolvedFor+1)+"/"+OptionsStage.numberOfPlayers+" players should have the HandDiff resolved now");

            }
            /*this is the end of the for loop
             * now we should have set handDiff to 0 for a number of players equal
             * to numberOfPlayers-1, but we still need to set the handDiff for the
             * last player to 0
             * it would be best to avoid trading cards with the 2 other players
             * since they are now set at 0, so try trading with the undealt hand
             * if we can't resolve it though we will have to trade some cards with
             * the other players. */



    }
}