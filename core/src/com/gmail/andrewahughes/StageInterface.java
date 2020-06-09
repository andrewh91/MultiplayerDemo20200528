package com.gmail.andrewahughes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public interface StageInterface {
    /**some constants to refer to each game stage*/
    public static final int TITLESTAGE =0,OPTIONSSTAGE =1, MATCHMAKINGSTAGE = 2, DEALSTAGE =3, TRIDENTBUILDINGSTAGE=4,GAMESTAGE =5, GAMEOVERSTAGE=6;

    void hideAllStages();
    void goToStage(int stage);
    int addTriButton(TriButton triButton, Array array, Stage stage,int index);
    TriButton getTriButton(Array array, int index);
    int addCardButton(CardButton cardButton, Array array, Stage stage,int index);
    CardButton getCardButton(Array array, int index);
}
