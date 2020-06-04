package com.gmail.andrewahughes;

public interface StageInterface {
    /**some constants to refer to each game stage*/
    public static final int TITLESTAGE =0,OPTIONSSTAGE =1, MATCHMAKINGSTAGE = 2, DEALSTAGE =3, TRIDENTBUILDINGSTAGE=4,GAMESTAGE =5, GAMEOVERSTAGE=6;

    void hideAllStages();
    void goToStage(int stage);
}
