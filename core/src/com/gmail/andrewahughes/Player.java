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
    /**
     * this will essentially be the hand the player is dealt,
     * this will store a reference to all the cards that have been
     * dealt to this player
     */
    Array<Integer> cardIndexArray = new Array<>();
    Array<CardButton> cardButtonArray = new Array<>();
    Array<TriButton> triButtonArray = new Array<>();
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
    public void drawCardArray(SpriteBatch batch){
        for (int i = 0 ; i < cardButtonArray.size; i++){
            cardButtonArray.get(i).draw(batch,1.0f);
        }

    }
    public void drawCardArrayShape(ShapeRenderer renderer){
        for (int i = 0 ; i < cardButtonArray.size; i++){
            cardButtonArray.get(i).drawShape(renderer);
        }
    }
    public void setPlayerNumber(int playerNumber) {
        playerNumber=playerNumber;
    }

    public void setCards(CardButton cardButton){
        int x = Gdx.graphics.getWidth()/10;
        /*this will distribute the cardArrays vertically based on player number */

        int y = Gdx.graphics.getHeight()/5*playerNumber;
        boolean isPointUp = (int)Math.floor(cardButtonArray.size/3)%2 ==0 ? true : false;
        byte position = (byte)(cardButtonArray.size%3);
        cardButtonArray.add(cardButton);
    }

}
