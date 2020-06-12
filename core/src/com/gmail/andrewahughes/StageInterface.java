package com.gmail.andrewahughes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public interface StageInterface {
    /**some constants to refer to each game stage*/
    public static final byte TITLESTAGE =0,OPTIONSSTAGE =1, MATCHMAKINGSTAGE = 2, DEALSTAGE =3, TRIDENTBUILDINGSTAGE=4,GAMESTAGE =5, GAMEOVERSTAGE=6;

    void hideAllStages();
    void goToStage(int stage);
    void addTriButton(TriButton triButton, Array array, Stage stage);
    TriButton getTriButton(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonTitleStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonOptionsStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonMatchMakingStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonDealStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonTridentBuildingStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonGameStage(Array array, ButtonEnum.Tri index);
    TriButton getTriButtonGameOverStage(Array array, ButtonEnum.Tri index);
    void addCardButton(CardButton cardButton, Array array, Stage stage);
    CardButton getCardButtonTridentBuildingStage(Array array, ButtonEnum.Card index);
    void handleButtonsTitleTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsOptionsTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsMatchMakingTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsDealTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsTridentBuildingTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsGameTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsGameOverTri(ButtonEnum.Tri triButtonIndex);
    void handleButtonsTridentBuildingCard(ButtonEnum.Card cardButtonIndex);
}
