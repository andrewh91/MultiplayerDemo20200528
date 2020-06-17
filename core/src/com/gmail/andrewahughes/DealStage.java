package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

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
     * animation moves cards to their correct positions in their player's
     * card hand
     */
    final byte ANIMATIONDEALCARDS = 3;
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
    float animationTimer = 0;



    public DealStage(StageInterface stageInterface) {
        this.stageInterface = stageInterface;
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
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
            /*draw all actors of this stage*/
            drawTriButtons();
            drawAnimation();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
            /*the cardbuttons will have already been set a random number,
            * this will actually display that number */
            if(ANIMATIONSETTEXTFLAG) {
                ANIMATIONSETTEXTFLAG=false;
                /*when the cards are moved to their player's card hand
                * we need to reserve some space at the top for the trident hand
                * this method determines how much space */
                CardButton.setTridentHandHeight();
                for (int i = 0; i < Deck.cardArray.size; i++) {
                    TridentBuildingStage.cardButtonArray.get(i).setText();
                    TridentBuildingStage.cardButtonArray.get(i).setDealAnimationPosition();
                }
            }
            /*fade the text back in as the cards move to their player's card hand*/
            CardButton.fadeFont.setColor(1,1,1,((animationTimer-ANIMATIONDEALCARDSTIMER+ANIMATIONOVERLAPCARDSTIMER)/ANIMATIONDEALCARDSTIMER*4));
            /* actually don't fade the text back in until we've resolved par*/

            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).moveToPositionAnimation((animationTimer-ANIMATIONDEALCARDSTIMER-ANIMATIONOVERLAPCARDSTIMER)/ANIMATIONDEALCARDSTIMER);
            }
            ANIMATIONSTAGE=ANIMATIONDEALCARDS;
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
        /*once moved to the centre the cards will then move to their
        * position in their player's card hand*/
        else if (ANIMATIONSTAGE == ANIMATIONDEALCARDS ){
            animationTimer+=Gdx.graphics.getDeltaTime();
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
        if (ANIMATIONSTAGE == ANIMATIONOVERLAPCARDS) {
            for (int i = 0; i < Deck.cardArray.size; i++) {
                TridentBuildingStage.cardButtonArray.get(i).drawShape(shapeRenderer);
            }
        }
        if (ANIMATIONSTAGE == ANIMATIONDEALCARDS) {
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
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALNEXTSTAGE).setTridentToTextSize();

        stageInterface.addTriButton(new TriButton(stageInterface, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, true, StageInterface.DEALSTAGE, ButtonEnum.Tri.DEALBEGINDEAL), triButtonArray, this);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setText("Begin Deal");
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).setTridentToTextSize();
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.DEALBEGINDEAL).centre();

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

        for (byte p = 0; p < OptionsStage.numberOfPlayers; p++) {
            for (int i = 0; i < OptionsStage.cardsEach; i++) {
                TridentBuildingStage.cardButtonArray.get(OptionsStage.cardsEach*p+i).setPlayerIndex(p);
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
            default:
                Gdx.app.log("Example", "DEFAULT " + triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
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
        CardButton.setDealAnimationRectangle(50, 50, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100);
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