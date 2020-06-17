package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Deck {
    /**
     * list of 52 cards iin order index value from 0 to 51
     */
    static Array<Byte> cardArray = new Array<Byte>();
    /**
     * list of the 52 cards, but this time in a random order
     */
    static Array<Byte> randomCardArray = new Array<Byte>();
    /**
     * list of integers from 0 to 51 in order, although it's called
     * random helper the numbers are actually stored in order.
     */
    static private Array<Byte> randomHelper = new Array<Byte>();
    /**
     * the numbers are removed from randomHelper during the shuffle
     * so this is another array to help restore the numbers to it.
     */
    static private Array<Byte> randomHelperReset = new Array<Byte>();

    static private Random random = new Random();
    public Deck(){
        /*add 52 cards*/
        for(byte i =0; i<52;i++){
            cardArray.add(i);
            randomHelperReset.add(i);
        }

        shuffle();

    }

    /**
     * there is an array called randomHelper it contains 0 to 51 in order.
     * this method will remove a random number from that randomHelper array
     * the card in cardArray index location equal to that removed number will
     * be added to the randomCardArray, because the number is removed from the
     * random helper, no cards should be added twice.
     */
    static void shuffle(){
        randomHelper.addAll(randomHelperReset);
        for(int i =0; i<52;i++){
            randomCardArray.add(cardArray.get(randomHelper.removeIndex(random.nextInt(randomHelper.size))));
            Gdx.app.log("Example","card "+randomCardArray.get(randomCardArray.size-1));
        }

    }
}
