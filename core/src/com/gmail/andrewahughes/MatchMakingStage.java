package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MatchMakingStage extends Stage {



    boolean visible =false;
    float time =0.0f;
    StageInterface stageInterface;
    public MatchMakingStage(StageInterface stageInterface )
    {
        this.stageInterface =stageInterface;


    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            Gdx.gl.glClearColor(1.0f, 1.0f, 0.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            time+=Gdx.graphics.getDeltaTime();
            if(time>1){
                stageInterface.goToStage(StageInterface.DEALSTAGE);
                time=0;

            }
        }
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}