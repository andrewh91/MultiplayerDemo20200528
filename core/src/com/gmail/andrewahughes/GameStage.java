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

public class GameStage extends Stage {

    boolean visible =false;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();

    public GameStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
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
            Gdx.gl.glClearColor(0.5f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape();
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
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMENEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setText("Game\nOver");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setTridentToTextSize();
        createGameBoard();
        createTridentHand();
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
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0.value).setUpCardButtons(TridentBuildingStage.getCardAtHighlightPos(i *3),TridentBuildingStage.getCardAtHighlightPos(i *3+1),TridentBuildingStage.getCardAtHighlightPos(i *3+2));
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).updatePos(TridentBuildingStage.getCardAtHighlightPos(i *3).getX(),TridentBuildingStage.getCardAtHighlightPos(i *3).getY(),TridentBuildingStage.getCardAtHighlightPos(i *3).orientation);
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).cardsVisible=true;

        }
        /*if using pre and post game cards save them to a trident each too */
        if (OptionsStage.preAndPostGameCard){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).setPreGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3));
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).setPostGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3+1));
        }

    }
    public void reset(){

        /*if using pre and post game cards save them to a trident each too */
        if (OptionsStage.preAndPostGameCard && triButtonArray.size>OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).resetPreGameCard();
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).resetPostGameCard();
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
                stageInterface.goToStage(StageInterface.GAMEOVERSTAGE);
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
}