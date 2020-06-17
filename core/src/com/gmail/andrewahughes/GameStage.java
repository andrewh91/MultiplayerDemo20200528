package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class GameStage extends Stage {

    boolean visible =false;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();

    public GameStage(StageInterface stageInterface )
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

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.5f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
        stageInterface.addTriButton(new TriButton(stageInterface,50,250,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMENEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setText("GameOver");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setTridentToTextSize();

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