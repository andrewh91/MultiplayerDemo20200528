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
    TriButton exitBtn;
    /*we need to store an array of actors so we can loop through and call the draw method of each
    * i expect most actors to be CardButton or TriButton classes*/
    Array<Actor> actors;

    public TitleStage(StageInterface stageInterface)
    {
        this.stageInterface =stageInterface;
        this.spriteBatch =new SpriteBatch();
        this.shapeRenderer = shapeRenderer;
        Gdx.input.setInputProcessor(this);
        exitBtn = new TriButton();
        this.addActor(exitBtn);


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
            drawActors();
            spriteBatch.end();

        }
    }
    void drawActors() {
        actors = this.getActors();
        for(int i=0;i<actors.size;i++) {
            actors.get(i).draw(spriteBatch,1.0f);

        }
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}