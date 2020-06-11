package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CardButton  extends Actor {
    private static final boolean POINTUP = true;
    private static final boolean POINTDOWN = false;
    boolean orientation = POINTDOWN;
    public static final byte VERTICAL = 0;
    public static final byte LEFT = 1;
    public static final byte RIGHT = 2;
    byte position = 0;
    /*the longest edge length, should be the same as the edge length of the trident*/
    float edgeLength = 50.0f;
    /*assuming the isosceles card shape has the longest edge parallel to the horizon
    * the altitude is tan(30) * edgeLength /2    = 0.28867513459481288225457439025098 * edgeLength*/
    float altitude = (float)( Math.tan(Math.PI/6) * edgeLength /2);
    /*the length of the remaining 2 sides is tan(30) * edgeLength  = 0.57735026918962576450914878050196 * edgeLength*/
    float otherLength = (float)( Math.tan(Math.PI/6) * edgeLength);
    float halfEdgeLength = edgeLength/2;
    float tridentAltitude = (float)(edgeLength * Math.sin(Math.PI/3));
    /*the cards x and y coordinate will be the bottom left of the imaginary trident's bounding box
    * this is potentially confusing since a POINTDONW RIGHT card's position is quite far from the
    * drawn object*/
    GlyphLayout glyphLayout = new GlyphLayout();
    SpriteBatch spriteBatch = new SpriteBatch();
    Texture texture;
    byte stageIndex;
    ButtonEnum.TitleStageCard cardButtonIndex;

    /**constructor for triButton
     *
     * @param startingX initial x position
     * @param startingY initial y position
     * @param isPointUp orientation boolean, tru means POINTUP false means POINTDOWN
     * @param stageIndex this will be used in the touchLogic method to figure out which stage this button is in
     */
    public CardButton(float startingX, float startingY, boolean isPointUp, byte position, final byte stageIndex, ButtonEnum.TitleStageCard cardButtonIndex)
    {
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        orientation = isPointUp;
        this.position = position;
        this.stageIndex = stageIndex;

        this.cardButtonIndex=cardButtonIndex;
        setX(startingX);
        setY(startingY);

        setWidth(edgeLength*2);
        setHeight(tridentAltitude);

        this.addListener(new ClickListener() {


            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
                return true;
            }




            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                /*the x and y arguments will be relative to the actor so a click on the bottom left of the actor will be
                 * x = 0 y = 0 regardless of where the actor is on screen, for my purposes it's more useful to use world
                 * coordinates because some of my methods are called from outside this class where access to this actor's
                 * position might not be available.
                 * clicklistner will fire if the rectangle bounding box is hit, need to
                 * further calculate if the triangle is hit*/
                /* this makes the coords relevant to the world*/
                x = x + getX();
                y = y + getY();
                if (triangleHit( x, y)) {
                    touchLogic( x, y);

                } else {

                    Gdx.app.log("Example", "search all actors");
                    TitleStage.queryCardButtonTouch(x,y);
                }
            }
        }); /*the end of the this.addListener*/


    }


    public void draw(Batch batch, float parentAlpha) {
        /*super.draw(batch, parentAlpha);*/
        batch.draw(texture,getX(),getY());


    }
    public void drawShape(ShapeRenderer shapeRenderer) {


        if(orientation==POINTUP) {
            if(position ==LEFT)
            {
                shapeRenderer.triangle(
                        getX() + halfEdgeLength,
                        getY() + altitude,
                        getX() +halfEdgeLength,
                        getY() +altitude+otherLength,
                        getX() ,
                        getY() );
            }
            else if(position ==RIGHT)
            {
                shapeRenderer.triangle(
                        getX()+halfEdgeLength,
                        getY()+altitude,
                        getX() +halfEdgeLength,
                        getY() +altitude+otherLength,
                        getX() +edgeLength,
                        getY() );
            }
            else if(position ==VERTICAL)
            {
                shapeRenderer.triangle(
                        getX()+halfEdgeLength,
                        getY()+altitude,
                        getX() + edgeLength,
                        getY() ,
                        getX(),
                        getY() );
            }

        }
        else if(orientation==POINTDOWN){
            if(position ==LEFT)
            {
                shapeRenderer.triangle(
                        getX()+halfEdgeLength,
                        getY()+otherLength,
                        getX() +halfEdgeLength,
                        getY() ,
                        getX() ,
                        getY()+altitude+otherLength );
            }
            else if(position ==RIGHT)
            {
                shapeRenderer.triangle(
                        getX()+halfEdgeLength,
                        getY()+otherLength,
                        getX() +edgeLength,
                        getY()+altitude+otherLength,
                        getX() +halfEdgeLength,
                        getY() );
            }
            else if(position ==VERTICAL)
            {
                shapeRenderer.triangle(
                        getX()+halfEdgeLength,
                        getY()+otherLength,
                        getX() ,
                        getY() + altitude+otherLength,
                        getX() +edgeLength,
                        getY() + altitude+otherLength);
            }
        }
    }

    /**work out if the triangle has been hit, considering orientation
     *
     */
    public boolean triangleHit(float x, float y) {
        x = x - getX();
        y = y - getY();
        if(orientation==POINTUP) {
            if(position ==LEFT)
            {
                if (x<halfEdgeLength && y < (x*tridentAltitude/halfEdgeLength) && y > (x * altitude/halfEdgeLength)){

                    Gdx.app.log("Example", "in POINTUP   LEFT");
                    return true;
                }
            }
            else if(position ==RIGHT)
            {
                if (x>halfEdgeLength && y < (-x * tridentAltitude/halfEdgeLength) + tridentAltitude *2 && y > (-x * altitude/halfEdgeLength) +otherLength){
                    Gdx.app.log("Example", "in POINTUP   RIGHT");
                    return true;
                }

            }
            else if(position ==VERTICAL)
            {
                if (y>0 && y < (x * altitude/halfEdgeLength) && y < (-x * altitude/halfEdgeLength)+otherLength){
                    Gdx.app.log("Example", "in POINTUP   VERTICAL");
                    return true;
                }

            }
        }
        else if(orientation==POINTDOWN){
            if(position ==LEFT)
            {
                if(x<halfEdgeLength &&  y > (-x * tridentAltitude/halfEdgeLength)+tridentAltitude && y < (-x * altitude/halfEdgeLength)+tridentAltitude){
                    Gdx.app.log("Example", "in POINTDOWN LEFT");
                    return true;
                }
            }
            else if(position ==RIGHT)
            {
                if (x>halfEdgeLength && y > (x*tridentAltitude/halfEdgeLength)-tridentAltitude && y < (x * altitude/halfEdgeLength)+altitude){
                    Gdx.app.log("Example", "in POINTDOWN RIGHT");
                    return true;
                }
            }
            else if(position ==VERTICAL)
            {
                if (y<tridentAltitude && y > (-x*altitude/halfEdgeLength)+tridentAltitude && y > (x*altitude/halfEdgeLength)+altitude){
                    Gdx.app.log("Example", "in POINTDOWN VERTICAL");
                    return true;
                }

            }
        }
        return false;
    }

    /**perform whatever action is required when the button is pressed
     *
     * @param x
     * @param y
     */
    public void touchLogic(float x, float y)
    {

        setX( x);
        setY( y);
        switch(stageIndex)
        {
            /*i think card buttons will only be used in the tridentBuildingStage*/
            /*
            case StageInterface.TITLESTAGE:
            {
                break;
            }
            case StageInterface.OPTIONSSTAGE:
            {
                break;
            }
            case StageInterface.MATCHMAKINGSTAGE:
            {
                break;
            }
            case StageInterface.DEALSTAGE:
            {
                break;
            }
            */
            case StageInterface.TRIDENTBUILDINGSTAGE:
            {
                TridentBuildingStage.touchLogicCard(cardButtonIndex);
                break;
            }
            /*
            case StageInterface.GAMESTAGE:
            {
                break;
            }
            case StageInterface.GAMEOVERSTAGE:
            {
                break;
            }*/

        }
    }
}
