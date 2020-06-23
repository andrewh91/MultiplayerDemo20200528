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


public class DealStage extends Stage {

    boolean visible = false;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    /**
     * no animation is playing, this is the default
     */
    final byte ANIMATIONSTOPPED = 0;
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
    final byte ANIMATIONDEALCARDS = 4;
    /**
     * how much time passes in seconds from the start of the animation until
     * ANIMATIONDEALCARDS begins
     */
    final static float ANIMATIONDEALCARDSTIMER = 2;
    byte ANIMATIONSTAGE = ANIMATIONSTOPPED;
    /**
     * half way through the animation a method called set text will be called
     * this flag ensures it's only called once,
     */
    boolean ANIMATIONSETTEXTFLAG=true;
    boolean ANIMATIONSETPARFLAG=true;
    float animationTimer = 0;
    byte par=0;

    /*when the cards are dealt, add up all the pip values of the cards
    * this will help with working out par */
    static Array<Integer> playerHandValue = new Array<Integer>();


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
            else if (ANIMATIONSTAGE==ANIMATIONDEALCARDS) {
                /*the cardbuttons will have already been set a random number,
                 * this will actually display that number */
                if (ANIMATIONSETTEXTFLAG) {
                    ANIMATIONSETTEXTFLAG = false;
                    /*when the cards are moved to their player's card hand
                     * we need to reserve some space at the top for the trident hand
                     * this method determines how much space */
                    CardButton.setTridentHandHeight();
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
        * position in their player's card hand*/
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
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setText("Begin\nDeal");
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
        Deck.shuffle();
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

    }

    /** when the begindeal button is clicked;
     * consider the number of players, if the cards are visible to all players,
     * if there is a pre and post game card,
     * shuffle and deal an equal amount of cards to each player,
     * there might be cards left over
     */
    private void beginDeal() {
        /*hide the begindeal button, it will reappear on reset*/
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setVisible(false);

        /*start off the animation*/
        ANIMATIONSTAGE = ANIMATIONDISPLAYCARDS;
        animationTimer=0;

    }

    /**
     * this is where we actually deal each card to a player, however actually,
     * the player index is added to the cardButton
     * the amendCardsForDeal method has already set the random cards to
     * TridentBuildingStage.cardButtonArray and it did it in such a way that
     * the contents of the array includes a section of one player's cards which are sorted
     * in order, then another section of another player's cards after that which are also sorted
     * as such we can just loop through and assign the player index to each section
     *
     */
    private void deal() {
        /*call the gotAllPlayers method which will set the player indexes
         * if we have the correct number of players, if we do not have the correct number of players it does nothing*/
        if (MyServer.gotAllPlayers()) {
            /*if this player is player 1, deal the cards, otherwise receive data from player1's deal*/
            if (MyServer.player.index == 0) {
                for (byte p = 0; p < OptionsStage.numberOfPlayers; p++) {
                    for (int i = 0; i < OptionsStage.cardsEach; i++) {
                        TridentBuildingStage.cardButtonArray.get(OptionsStage.cardsEach * p + i).setPlayerIndex(p);
                    }
                }
                /*player 1 will send the results of the deal to all players, only the value and the
                 * player index of each of the 52 cards are sent*/
                MyServer.emitDeal();
                Gdx.app.log("Deal Stage", "Send deal");

            } else {
                /*if the current player is not player 1 the MyServer class will receive data
                 * from player1's deal, which will alter the TridentBuildingStage's cardbuttonArray*/
                Gdx.app.log("Deal Stage", "receive deal");
            }
            Gdx.app.log("Deal Stage", "MyServer.player.index" + MyServer.player.index);
        }
    }

    /**
     * this will help with the par method
     * each card will have been given a playerIndex value, either 1,2 or 3
     * that will tell you who owns the card, a value of 0 means it was not
     * dealt at all
     */
    private void calculateValueOfHand() {
        playerHandValue.clear();
        /*add one player to the player hand value array,
        * this will intercept any cards that were not dealt*/
        playerHandValue.add(0);

        /*for each player add a 0 value to the array, combined with the one
        * we have just made above, the array should now have 1 more item
        * that the number of players*/
        for (byte p = 0; p < OptionsStage.numberOfPlayers; p++) {
            playerHandValue.add(0);
        }
        /*for all 52 cards*/
        for (int i = 0; i < TridentBuildingStage.cardButtonArray.size; i++) {
            /*basically, for each card, this will add the value of the card to the
            relevant item in the playerHandValue array*/
            playerHandValue.set((int) TridentBuildingStage.cardButtonArray.get(i).playerIndex,playerHandValue.get(TridentBuildingStage.cardButtonArray.get(i).playerIndex)+TridentBuildingStage.cardButtonArray.get(i).value%13+1);
        }
        for (int i = 0; i < playerHandValue.size; i++) {
            if(i==0){
                Gdx.app.log("playerHandValue", "value of undealt cards = " + playerHandValue.get(i));
            }
            else {
                Gdx.app.log("playerHandValue", "value of player" + i + "'s cards = " + playerHandValue.get(i));
            }
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
                calculateValueOfHand();
                break;
            case DEALDECREASEPAR:
                decreasePar();
                calculateValueOfHand();
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

    /**
     * There will be a circle with your par, to the left and right there is a minus and plus arrow respectively. You can lower or increase your par, lowering it would make it more difficult for you and raising it would make it harder for you. The opponent will be doing the same.
     * You should be able to see how the opponent is altering their par in real time
     * When you are finished you can click deal.
     * The par values that have been chosen will be analysed, and a fair compromise will be attempted.
     * I’ve worked out a way to do this, as close to the chosen values as possible while still maintaining the difference in those values.
     *
     * N = number of players either 2 or 3
     * P[N] player array hold a value for that player’s par
     * X = Round( (P[0]+ P[1] + P[2]) / N ) // X is the average par, rounded
     *
     * For each p in P
     * P2 = P - X  //this is the first approximate of the correct par, it might be 1 out
     *
     * X2 = (P2[0]+P2[1]+P2[2])  // X2 is the total of the first approximate par, it should be either 1 or 0
     *
     * If (X2=0)  //if the first approximate par total is 0 then set par to that value and end
     *  End and ignore the below
     * Else //do the rest of this several lines below
     *
     *
     * For each p in P
     * P3 = ABS(P)//this is the absolute value
     *
     * P3.sort // sort that array
     *
     *
     * If P3[0] == P3[1] // if the 2 highest numbers are the same
     * P2[2] = P2[2]  - X2  // subtract X2 from the lowest number in the first approx par
     * Else
     * P2[0] = P2[0]  - X2  // subtract X2 from the highest number in the first approx par
     *
     * This should work for 2 or 3 players and it should keep the total par to 0 which is important,
     */
    private void confirmPar(){
        ANIMATIONSTAGE=ANIMATIONDEALCARDS;
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALCONFIRMPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALINCREASEPAR).setVisible(false);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DEALDECREASEPAR).setVisible(false);

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
        deal();
        calculateValueOfHand();
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
    public void amendCardsForDeal() {
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
    }
}