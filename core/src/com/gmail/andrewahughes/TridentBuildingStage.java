package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class TridentBuildingStage extends Stage {



    boolean visible =false;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    static Array<CardButton> cardButtonArray = new Array<CardButton>();
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    public TridentBuildingStage(StageInterface stageInterface )
    {
        this.stageInterface =stageInterface;
        this.spriteBatch =new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        createButtons();



    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw all actors of this stage*/
            drawTriButtonsShape();
            drawCardButtonsShape();
            shapeRenderer.end();
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
    void drawCardButtonsShape() {

        for(int i=0;i<cardButtonArray.size;i++) {
            cardButtonArray.get(i).drawShape(shapeRenderer);

        }
    }
    public void setVisible(boolean visible) {
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
        stageInterface.addTriButton(new TriButton(stageInterface,50,250,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setText("Game");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setTridentToTextSize();

        /*card buttons*/
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.LEFT,       StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.RIGHT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING1),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.VERTICAL,   StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING2),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.LEFT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING3),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.RIGHT,     StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING4),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.VERTICAL,  StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING5),cardButtonArray,this);
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
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryCardButtonTouch(float x, float y){
        /*for each cardButton in this stage*/
        for(int i=0;i<cardButtonArray.size;i++) {
            /*if the touch location is in this cardButton's triangle then break the for loop and do the touch logic*/
            if(cardButtonArray.get(i).triangleHit(x,y)){
                cardButtonArray.get(i).touchLogic(x,y);
                break;
            }


        }
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){

        switch(triButtonIndex){

            case TRIDENTBUILDINGNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.GAMESTAGE);
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
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
    }
    public void amendCardsForTridentBuildingStage() {


    }
}
