package com.gmail.andrewahughes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite {
    Vector2 previousPosition;
    /*this will be the player's number, will be 0 , 1 or 2
    * this value will be asssigned to the cardButtons and TriButtons
    * associated with this player*/
    byte index ;
    /**
     * this will essentially be the hand the player is dealt,
     * this will store a reference to all the cards that have been
     * dealt to this player
     */
    /*this is just so the player know if it's player 1 2 or 3*/
    private byte playerNumber=0;
    public Player(Texture texture){
        super(texture);
        previousPosition = new Vector2(getX(), getY());

    }

    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }

    /**
     * i could set the index on player creation but it's possible for
     * players to leave and join numberous times so it's best to
     * set it after we've got our players confirmed
     * @param index
     */
    public void setIndex(byte index) {
        this.index=index;
    }



}
