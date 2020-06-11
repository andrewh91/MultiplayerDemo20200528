package com.gmail.andrewahughes;

public class ButtonEnum {
    /*i need an enum for every button. when buttons are created they are passed a unique enum so that button can
    reference itself. the enum will also be used to get that button from the button array
    * it's important to add the buttons to the array at the enum index position so that if the buttons are added
    * out of order then we will get an error message and we cna fix it, otherwise there would be no error but when 
    * we try and get the button with the enum as the index then it will get the wrong button
    */
static int titleStageTri=0;
    enum TitleStageTri {
        EXIT  ,
        OTHER ;
        int value;
        TitleStageTri() {
            this.value=titleStageTri++;
        }
    }
static int titleStageCard = 0;
    enum TitleStageCard {
        BUTTON0 ,
        BUTTON1 ,
        BUTTON2 ,
        BUTTON3 ,
        BUTTON4 ,
        BUTTON5 ;

        int value;
        TitleStageCard( ) {
            this.value =titleStageCard++;
        }
    }
}
