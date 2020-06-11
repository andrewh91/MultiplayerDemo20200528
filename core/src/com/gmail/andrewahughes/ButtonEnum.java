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

        OPTIONSNEXTSTAGE            (0),

        MATCHMAKINGNEXTSTAGE        (0),

        DEALNEXTSTAGE               (0),

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
        TRIDENTBUILDING5;

        int value;
        Card( ) {
            this.value =card++;
        }
    }
}
