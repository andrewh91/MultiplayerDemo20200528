package com.gmail.andrewahughes;

import com.badlogic.gdx.utils.Array;

/**
 * PremadeDeck will contain the hard coded info for premadeTridents
 * a deck is 52 cards, 4 suits 13 cards in each suit
 * a trident is 3 cards
 * so i can use 12 cards from each suit to make 4 tridents for each suit,
 * then i'll have 16 tridents and 4 cards left over
 *      the unused cards could form a final 17th trident which would be unique because it would have
 *      cards from more than one suit. but i might ust stick with 16 for now
 * the 4 tridents form each suit should ahve an equal amount of point up and point down tridents
 * each player can choose about 8 tridents from this pool of tridents, they need to
 * pick an equal amount of point up and point down tridents
 * there's nothing to stop players from picking the same tridents as each other
 * the handicap can still come into play so that you can't just pick the 8 best ones
 *
 * the data i need to store here will just be a list of the values in order, so the first 3 values will
 * make up the first trident in the first suit, the vertical , left and then right cards
 * and that will be point down, each successive one will
 * alternate between point up and down  the final 3 values with be the 46th, 47th and 48th cards
 * the 4 cards not stored will be unused.
 *
 * the 4 suits are nature , light, demon and dark, the suits don't really matter but they will have
 * themes - obviously they will have different pictures in keeping with their theme, but also each suit
 * will have characteristics, they will have a preference to which card position is strongest and weakest,
 * and a preference for how most of the tridents are set up, be it spear bident or trident etc
 * and i can set this up so that, typically, one suit has a advantage over the next sort of like rock paper scissors.
 * nature beats light beats demon beats dark beats nature , but this is just a sort of thematic rule i made for myself,
 * nothing to actually enforce it
 */
public class PremadeDeck {

    static Array<Integer> d01 = new Array<Integer>();
    /*will contain the name of each trident*/
    static Array<String> d01Contents = new Array<String>();

    public PremadeDeck()
    {
        d01.clear();
        /*               vert     left     right                 name*/
        d01.add(    na(1), na(2), na(3)) ;d01Contents.add("Sapling");
        d01.add(    na(4), na(5), na(10));d01Contents.add("Wolf");
        d01.add(    na(12),na(8), na(13));d01Contents.add("Forest");
        d01.add(    na(6), na(9), na(11));d01Contents.add("Bear");
        /*               vert     left     right                 name*/
        d01.add(    li(6), li(4), li(2)) ;d01Contents.add("Spark");
        d01.add(    li(8), li(9), li(7 ));d01Contents.add("Eruption");
        d01.add(    li(13),li(12),li(11));d01Contents.add("Solar Flare");
        d01.add(    li(10),li(5), li(3));d01Contents.add("Lightning");
        /*               vert     left     right                 name*/
        d01.add(    de(1), de(5), de(2)) ;d01Contents.add("Imp");
        d01.add(    de(3), de(10),de(8)) ;d01Contents.add("Demon");
        d01.add(    de(6 ),de(13),de(11));d01Contents.add("Behemoth");
        d01.add(    de(4), de(12),de(9)) ;d01Contents.add("Hell Beast");
        /*               vert     left     right                 name*/
        d01.add(    da(7), da(2), da(4)) ;d01Contents.add("Fungus");
        d01.add(    da(6), da(5), da(3)) ;d01Contents.add("Miasma");
        d01.add(    da(11),da(8), da(10));d01Contents.add("Eclipse");
        d01.add(    da(13), da(12),da(9));d01Contents.add("Blackhole");
    }

    /*these method will just help get the value of the suit, from the pip value
    * just so that it's more readable when i'm declaring the decks */
    private static int na(int i)
    {
        return i-1;
    }
    private static int li(int i)
    {
        return i+13-1;
    }
    private static int de(int i)
    {
        return i+26-1;
    }
    private static int da(int i)
    {
        return i+39-1;
    }
}
