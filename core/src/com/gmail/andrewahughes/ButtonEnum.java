package com.gmail.andrewahughes;

public class ButtonEnum {
    /*i need an enum for every button. when buttons are created they are passed a unique enum so that button can
    reference itself. the enum will also be used to get that button from the button array
    * it's important to add the buttons to the array at the enum index position so that if the buttons are added
    * out of order then we will get an error message and we can fix it, otherwise there would be no error but when
    * we try and get the button with the enum as the index then it will get the wrong button
    */
    enum Tri {
        TITLEEXIT                   (0),
        TITLEOTHER                  (1),
        TITLENEXTSTAGE              (2),

        /*buttons for a new stage must begin again at value 0*/
        OPTIONSNEXTSTAGE            (0),

        MATCHMAKINGNEXTSTAGE        (0),

        DEALNEXTSTAGE               (0),
        DEALBEGINDEAL               (1),

        TRIDENTBUILDINGNEXTSTAGE    (0),

        GAMENEXTSTAGE               (0),

        GAMEOVERNEXTSTAGE           (0);

        final int value;
        Tri(int i) {
            this.value= i;
        }
    }
    static int card = 0;
    enum Card {
        TRIDENTBUILDING0,
        TRIDENTBUILDING1,
        TRIDENTBUILDING2,
        TRIDENTBUILDING3,
        TRIDENTBUILDING4,
        TRIDENTBUILDING5,
        TRIDENTBUILDING6,
        TRIDENTBUILDING7,
        TRIDENTBUILDING8,
        TRIDENTBUILDING9,
        TRIDENTBUILDING10,
        TRIDENTBUILDING11,
        TRIDENTBUILDING12,
        TRIDENTBUILDING13,
        TRIDENTBUILDING14,
        TRIDENTBUILDING15,
        TRIDENTBUILDING16,
        TRIDENTBUILDING17,
        TRIDENTBUILDING18,
        TRIDENTBUILDING19,
        TRIDENTBUILDING20,
        TRIDENTBUILDING21,
        TRIDENTBUILDING22,
        TRIDENTBUILDING23,
        TRIDENTBUILDING24,
        TRIDENTBUILDING25,
        TRIDENTBUILDING26,
        TRIDENTBUILDING27,
        TRIDENTBUILDING28,
        TRIDENTBUILDING29,
        TRIDENTBUILDING30,
        TRIDENTBUILDING31,
        TRIDENTBUILDING32,
        TRIDENTBUILDING33,
        TRIDENTBUILDING34,
        TRIDENTBUILDING35,
        TRIDENTBUILDING36,
        TRIDENTBUILDING37,
        TRIDENTBUILDING38,
        TRIDENTBUILDING39,
        TRIDENTBUILDING40,
        TRIDENTBUILDING41,
        TRIDENTBUILDING42,
        TRIDENTBUILDING43,
        TRIDENTBUILDING44,
        TRIDENTBUILDING45,
        TRIDENTBUILDING46,
        TRIDENTBUILDING47,
        TRIDENTBUILDING48,
        TRIDENTBUILDING49,
        TRIDENTBUILDING50,
        TRIDENTBUILDING51;

        int value;
        Card( ) {
            this.value =card++;
        }
    }
}
