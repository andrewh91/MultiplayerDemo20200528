package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
    /*we need an index to help keep track of all the buttons, when adding a new button the
     * index will be incremented so it can be assigned to a variable for that button */
    int index=-1;



    public TitleStage(StageInterface stageInterface)
    {
        this.stageInterface =stageInterface;
        this.spriteBatch =new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(this);

        /*the index position of the button in the array will be stored in this variable when adding the
         * button. this variable can be used later to get a specific button from the array*/
        int EXITBUTTON= stageInterface.addTriButton(new TriButton(200,100,true),triButtonArray,this,index);
        int BUTTON2 = stageInterface.addTriButton(new TriButton(200,100,false),triButtonArray,this,index);
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

    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void clickTridentButton(float x, float y){
        /*for each triButton in this stage*/
        for(int i=0;i<triButtonArray.size;i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if(triButtonArray.get(i).triangleHit(x,y)){
                triButtonArray.get(i).touchLogic(x,y);
                break;
            }


        }
    }

}