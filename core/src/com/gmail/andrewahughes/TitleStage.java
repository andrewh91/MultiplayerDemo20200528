package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class TitleStage extends Stage {



    boolean visible =false;
    float time =0.0f;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    static Array<CardButton> cardButtonArray = new Array<CardButton>();
    /*we need an index to help keep track of all the buttons, when adding a new button the
     * index will be incremented so it can be assigned to a variable for that button */
    int triButtonArrayIndex=-1;
    int cardButtonArrayIndex=-1;




    public TitleStage(StageInterface stageInterface)
    {
        this.stageInterface =stageInterface;
        this.spriteBatch =new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(this);

        /*when creating new buttons we pass in the enum for that button so the button can store it
        * so it can reference itself later. the add button method also needs this stage
        * to add the actor to the stage and our array of buttons so we can add it to that too
        * when adding to the array the method actually inserts it in the array at the enum.value index
        * this means if we add the buttons out of order it will cause an error, which is good because
        * then i can make sure the buttons are in the correct order*/

        stageInterface.addTriButton(new TriButton(200,200,true,StageInterface.TITLESTAGE, ButtonEnum.TitleStageTri.EXIT),triButtonArray,this,ButtonEnum.TitleStageTri.EXIT.value);
        stageInterface.addTriButton(new TriButton(100,200,false,StageInterface.TITLESTAGE, ButtonEnum.TitleStageTri.OTHER),triButtonArray,this,ButtonEnum.TitleStageTri.OTHER.value);

        stageInterface.addCardButton(new CardButton(300,200,true,CardButton.LEFT,       StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON0),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON0.value);
        stageInterface.addCardButton(new CardButton(300,200,true,CardButton.RIGHT,      StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON1),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON1.value);
        stageInterface.addCardButton(new CardButton(300,200,true,CardButton.VERTICAL,   StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON2),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON2.value);
        stageInterface.addCardButton(new CardButton(275,200,false,CardButton.LEFT,      StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON3),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON3.value);
        stageInterface.addCardButton(new CardButton(275,200,false,CardButton.RIGHT,     StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON4),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON4.value);
        stageInterface.addCardButton(new CardButton(275,200,false,CardButton.VERTICAL,  StageInterface.TITLESTAGE, ButtonEnum.TitleStageCard.BUTTON5),cardButtonArray,this, ButtonEnum.TitleStageCard.BUTTON5.value);
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            /*this is just a simple method to cycle through all stages and make sure they work*/
            time+=Gdx.graphics.getDeltaTime();
            if(time>1){
                //stageInterface.goToStage(StageInterface.OPTIONSSTAGE);
                time=0;

            }
            spriteBatch.begin();
            /*draw all actors of this stage*/
            //drawTriButtons();
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
    public static void touchLogic(ButtonEnum.TitleStageTri triButtonIndex){

        switch(triButtonIndex){
            case EXIT : {
                Gdx.app.log("Example", "EXIT "+triButtonIndex.value);
                break;
            }
            case OTHER: {
                Gdx.app.log("Example", "OTHER "+triButtonIndex.value);
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }


}