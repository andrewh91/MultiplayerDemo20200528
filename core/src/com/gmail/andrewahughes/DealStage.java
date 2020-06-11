package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class DealStage extends Stage {



    boolean visible =false;
    float time =0.0f;
    StageInterface stageInterface;
    public DealStage(StageInterface stageInterface )
    {
        this.stageInterface =stageInterface;


    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            Gdx.gl.glClearColor(0.0f, 1.0f, 0.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            time+=Gdx.graphics.getDeltaTime();
            if(time>1){
                stageInterface.goToStage(StageInterface.TRIDENTBUILDINGSTAGE);
                time=0;

            }
        }
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public static void touchLogic(int triButtonIndex){

    }
}