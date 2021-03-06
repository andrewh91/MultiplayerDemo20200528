package com.gmail.andrewahughes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public interface StageInterface {
    /**some constants to refer to each game stage*/
    public static final byte TITLESTAGE =0,OPTIONSSTAGE =1, MATCHMAKINGSTAGE = 2, DEALSTAGE =3, TRIDENTBUILDINGSTAGE=4,DECKBUILDINGSTAGE=5,GAMESTAGE =6, GAMEOVERSTAGE=7;

    void hideAllStages();
    void goToStage(int stage);
    void addTriButton(TriButton triButton, Array array, Stage stage);
    TriButton getTriButton(Array array, ButtonEnum.Tri index);
    void addCardButton(CardButton cardButton, Array array, Stage stage);
    void addCardButton(CardButton cardButton, Array array, Stage stage,int index);
    CardButton getCardButtonTridentBuildingStage(Array array, ButtonEnum.Card index);
    void handleButtonsTitleTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsOptionsTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsMatchMakingTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsDealTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsTridentBuildingTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsDeckBuildingTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsGameTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsGameOverTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsTridentBuildingCard(ButtonEnum.Card cardButtonIndex);
}
