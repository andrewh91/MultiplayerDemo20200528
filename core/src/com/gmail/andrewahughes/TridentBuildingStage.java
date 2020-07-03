package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;


import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;

public class TridentBuildingStage extends Stage {

    boolean visible =false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    static Array<CardButton> cardButtonArray = new Array<CardButton>();
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    public TridentBuildingStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        this.stageInterface =stageInterface;

        this.shapeRenderer = shapeRenderer;

        this.setViewport(viewport);
        this.spriteBatch = batch;


        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);

        createButtons();

    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons(spriteBatch);
            drawCardButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape(shapeRenderer);
            drawCardButtonsShape();
            shapeRenderer.end();
        }
    }
    static void  drawTriButtons(SpriteBatch spriteBatch) {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).draw(spriteBatch,1.0f);

        }
    }
    /**
     * draw the trident buttons shape - which should just be it's bounds
     */
    static void drawTriButtonsShape(ShapeRenderer shapeRenderer) {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShape(shapeRenderer);

        }
    }
    void drawCardButtonsShape() {

        for(int i=0;i<cardButtonArray.size;i++) {
            cardButtonArray.get(i).drawShape(shapeRenderer);

        }
    }
    void drawCardButtons() {

        for(int i=0;i<cardButtonArray.size;i++) {
            cardButtonArray.get(i).draw(spriteBatch,1.0f);

        }
    }
    public void setVisible(boolean visible) {
        /*i call the drawTriButtons method of this stage in DEALSTAGE, which will draw all
        * triButtons from this stage, but i don't want to draw the TRIDENTBUILDINGNEXTSTAGE
        * button, so i've set it to invisible, but here i will set it to visible again. it
        * will be set to invisible witht eh rest of the stage whenever we move to a stage
        * that is not this*/
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setVisible(visible);
        this.visible = visible;
    }
    public void createButtons(){
        /*when creating new buttons we pass in the enum for that button so the button can store it
         * so it can reference itself later. the add button method also needs this stage
         * to add the actor to the stage and our array of buttons so we can add it to that too
         * when adding to the array the method actually inserts it in the array at the enum.value index
         * this means if we add the buttons out of order it will cause an error, which is good because
         * then i can make sure the buttons are in the correct order*/
        /*trident buttons*/
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setText("Game");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setTridentToTextSize();
        /*this is the player's trident hand*/
        createPlayerTridentHand();


        /*the auto build button, this will expand into more buttons*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setText("Auto\nBuild");


        /*the first selset of 7 tributtons, will determine the pip value of the 3 cards selected*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).setText("Med\nSpear");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).setText("Low\nSpear");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED).setText("Med\nBident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW).setText("Low\nBident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*2)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI).setText("Hi\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setText("Med\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setText("Low\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setVisible(false);


        /*the second set of 6 tributtons, will determine the suit of the 3 cards selected*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setText("Suit\nNature");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT).setText("Suit\nLight");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON).setText("Suit\nDemon");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).setText("Suit\nDark");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).setText("Suit\nAny");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).setVisible(false);


        /*3rd set of buttons rotate, flip and confirm */
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE).setText("Rotate");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).setText("Flip");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setText("Confirm");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setVisible(false);

        resetAutoBuild();

        /*card buttons, these are created in setup cards instead*/
        /*
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.LEFT,       StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.RIGHT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING1),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.VERTICAL,   StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING2),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.LEFT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING3),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.RIGHT,     StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING4),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.VERTICAL,  StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING5),cardButtonArray,this);

         */
}
/*the maximum number of tridents will be 8 in the players' trident hand,
* plus a space for the pre and post game cards*/
void createPlayerTridentHand(){
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY1),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY2),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY3),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY4),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY5),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY6),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY7),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY8),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY9),triButtonArray,this);
    /*set all buttons to invisible*/
    resetPlayerTridentHand();
}
/*the player's trident array will be created initially, but we might want to set
* the position and visibility. this will be called after the create buttons method to make them all invisible,
* this is so that the DEALSTAGE can call the draw method of the TRIDENTBUILDINGSTAGE while only drawing the
* buttons we want, */
static void updatePlayerTridentHand(){
    float w = CardButton.dealAnimationRectangleWidth/5;
    float h = (float) (w * Math.sin(Math.PI/3));
    for(int i = ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY1.value ; i < OptionsStage.tridentsEach+1;i++){
        triButtonArray.get(i).setVisible(true);
        if(i==0){

        }
        else {
            triButtonArray.get(i).edgeLength=w;
            triButtonArray.get(i).updateBounds();
            triButtonArray.get(i).drawMirror=true;
            triButtonArray.get(i).setX(CardButton.dealAnimationRectangleDealX + w/2 * (i-1));
            triButtonArray.get(i).setY(CardButton.dealAnimationRectangleHeight + CardButton.dealAnimationRectangleDealY-h*2);
            triButtonArray.get(i).orientation=i%2==1?true:false;

        }
    }
}
    static void resetPlayerTridentHand() {
        for(int i = 0 ; i < triButtonArray.size;i++) {
            triButtonArray.get(i).setVisible(false);
            triButtonArray.get(i).drawMirror=false;
        }
    }
    static void resetAutoBuild(){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(true);
        setAutoBuildPipVisibility(false);
        setAutoBuildSuitVisibility(false);
        setAutoBuildRotFlipConfirmVisibility(false);
        for (int i =0; i< triButtonArray.size;i++){
            triButtonArray.get(i).updateBounds();
        }
    }
    static void setAutoBuildPipVisibility(boolean visible){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setVisible(visible);
    }
    static void setAutoBuildSuitVisibility(boolean visible){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).   setVisible(visible);
    }

    static void setAutoBuildRotFlipConfirmVisibility(boolean visible) {
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE). setVisible(visible);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).   setVisible(visible);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setVisible(visible);
    }
    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryTriButtonTouch(float x, float y){
        /*could also do for all cardButtonArray but cardButtons shouldn't be placed close to TriButtons anyway*/
        /*for each triButton in this stage*/
        for(int i=0;i<triButtonArray.size;i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if(triButtonArray.get(i).triangleHit(x,y)){
                triButtonArray.get(i).touchLogic(x,y);
                break;
            }


        }
    }

    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a cardButton's bounding box but not in it's triangle
     * this method will test all actors in the array if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryCardButtonTouch(float x, float y){
        /*for each cardButton in this stage*/
        boolean touchHandled=false;
        for(int i=0;i<cardButtonArray.size;i++) {
            /*if the touch location is in this cardButton's triangle then break the for loop and do the touch logic*/
            if(cardButtonArray.get(i).triangleHit(x,y)){
                cardButtonArray.get(i).touchLogic(x,y);
                touchHandled=true;
                break;
            }
        }
        /*if the touch wasn't in any of the cards in the card array, check the tributtons in the triarray*/
        if(touchHandled==false){
            queryTriButtonTouch(x,y);
        }
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){
Gdx.app.log("TRIDENTBUILDINGSTAGE"," tri button array clicked, button "+triButtonIndex);
        switch(triButtonIndex){

            case TRIDENTBUILDINGNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.GAMESTAGE);
                break;
            }
            case TRIDENTBUILDINGAUTOBUILD: {
                /*the autobuild buttons will expand the auto build trident buttons*/
                stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(false);
                setAutoBuildPipVisibility(true);
                break;
            }
            case TRIDENTBUILDINGSPEARMED: {
                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);

                break;
            }
            case TRIDENTBUILDINGSPEARLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGBIDENTMED: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGBIDENTLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGTRIDENTHI: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGTRIDENTMED: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGTRIDENTLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                break;
            }
            case TRIDENTBUILDINGSUIT0NATURE: {
                setAutoBuildRotFlipConfirmVisibility(true);
                setAutoBuildSuitVisibility(false);
                break;
            }
            case TRIDENTBUILDINGSUIT1LIGHT: {
                setAutoBuildRotFlipConfirmVisibility(true);
                setAutoBuildSuitVisibility(false);
                break;
            }
            case TRIDENTBUILDINGSUIT2DEMON: {
                setAutoBuildRotFlipConfirmVisibility(true);
                setAutoBuildSuitVisibility(false);
                break;
            }
            case TRIDENTBUILDINGSUIT3DARK: {
                setAutoBuildRotFlipConfirmVisibility(true);
                setAutoBuildSuitVisibility(false);
                break;
            }
            case TRIDENTBUILDINGSUIT4ANY: {
                setAutoBuildRotFlipConfirmVisibility(true);
                setAutoBuildSuitVisibility(false);
                break;
            }
            case TRIDENTBUILDINGAUTOBUILDROTATE: {
                break;
            }
            case TRIDENTBUILDINGAUTOBUILDFLIP: {

                break;
            }
            case TRIDENTBUILDINGAUTOBUILDCONFIRM: {
                stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(true);
                setAutoBuildRotFlipConfirmVisibility(false);

                break;
            }
                default:
                Gdx.app.log("TRIDENTBUILDINGSTAGE", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
    /**
     * this will be called in the tributton class,
     * @param cardButtonIndex this will be the index of the cardbutton that was clicked, the index is set on creation of the
     *                       cardButton and will be the same as it's index in the cardButtonArray for this stage
     */
    public void touchLogicCard(ButtonEnum.Card cardButtonIndex){


    }
    public void reset(){
        cardButtonArray.clear();
        setUpCards();
        /*make all the trident buttons invisible*/
        resetPlayerTridentHand();
        resetAutoBuild();


    }
    /*set up all the cards, they should be given an enum each,
    some of the initial values don't matter much since they will be set
    when allocated to one of the players */
    public void setUpCards() {
        /*set up all cards*/
        for (ButtonEnum.Card cardEnum : ButtonEnum.Card.values()) {
            System.out.println(cardEnum);
            stageInterface.addCardButton(new CardButton(stageInterface, 0, 0, true, (byte) 0, stageInterface.TRIDENTBUILDINGSTAGE, cardEnum), cardButtonArray, this);
        }
        Gdx.app.log("TridentBuildingStage","cardButtonArray size :"+cardButtonArray.size);
    }
    public void amendCardsForTridentBuildingStage() {


    }

    /**
     *
     *  this method is designed for use in the del stage, the deal stage will need methods to get teh lowest, second lowest etc
     *  card deal to a specified player, these methods are just for clarity and convenience
     *  the cards are dealt to the player in order lowest to highest, but i'm after the highest pip
     *  for example king of suit 1 is index 12, ace of suit 2 is index 13, king is higher pip but lower index
     *
     * @param player which player's card are we getting, 0, 1 or 2 (2 for 3 player game only)
     * @param nthLowestCard 0 will get the lowest card, OptionsStage.cardsEach-1 will get the highest
     * @return returns the index position of the card in teh cardButtonArray
     */
    public static int getNthLowestCard(int player,int nthLowestCard){
        Array<Integer> tempArray = new Array<>();
        tempArray.clear();
        /*add all the pip values of this player's cards to this tempArray*/
        for (int i = player*OptionsStage.cardsEach; i < player*OptionsStage.cardsEach+OptionsStage.cardsEach; i++) {
            /*don't loop through the cardButtonArray in a very safe way,
            * if the player variable above is set to OptionsStage.numberOfPlayers, we can go index out of bounds
            * but we can just stop before we get to that point and everything will work as intended*/
            if(i>=TridentBuildingStage.cardButtonArray.size){
                break;
            }
            tempArray.add((int)TridentBuildingStage.cardButtonArray.get(i).getPip());
        }
        /*sort the array lowest pip value to highest pip value*/
        tempArray.sort();
        /*now in the card button array find the card index position that has the nthLowest pip value */
        int i;
        for ( i = player*OptionsStage.cardsEach; i < player*OptionsStage.cardsEach+OptionsStage.cardsEach; i++) {
            if(TridentBuildingStage.cardButtonArray.get(i).getPip() == tempArray.get(nthLowestCard)){
                break;
            }
        }
        return i;
    }

    /**
     * easiest way to swap cards between players is to swap the values, and the playerindex
     * then sort it again
     * @param cardIndex1
     * @param cardIndex2
     */
    public static void swapCards(int cardIndex1, int cardIndex2){
        /*swap the value and player index of the 2 cards*/
        int value1 = cardButtonArray.get(cardIndex1).value;
        cardButtonArray.get(cardIndex1).setValue(cardButtonArray.get(cardIndex2).value);
        cardButtonArray.get(cardIndex2).setValue(value1);

        /*now we need to sort the array again*/

        Array<Integer> tempArray = new Array<>();
        for(int j=0;j<OptionsStage.numberOfPlayers;j++) {
            tempArray.clear();
            for (int i = 0; i < OptionsStage.cardsEach; i++) {
                tempArray.add(TridentBuildingStage.cardButtonArray.get(j*OptionsStage.cardsEach+i).value);
            }
            tempArray.sort();
            for (int k =0; k < tempArray.size;k++){
                TridentBuildingStage.cardButtonArray.get(j*OptionsStage.cardsEach+k).setValue(tempArray.get(k));
            }
        }
    }

}
