package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TriButton extends Actor {
    private static boolean POINTUP = true;
    private static boolean POINTDOWN = false;
    boolean orientation = POINTDOWN;
    float edgeLength = 50.0f;
    /*the altitude of an equilateral triangle will always be edgelength * 0.86602540378443864676372317075294 = sin(60)*/
    float altitude = (float)(edgeLength * Math.sin(Math.PI/3));
    float halfEdgeLength = edgeLength/2;
    float halfAltitude = altitude/2;
    BitmapFont font = new BitmapFont();
    /*glyph layout helps provide some extra data on the text which helps centre it properly*/
    GlyphLayout glyphLayout = new GlyphLayout();
    SpriteBatch spriteBatch = new SpriteBatch();
    Texture texture;
     byte stageIndex;
    ButtonEnum.TitleStageTri triButtonIndex;
    /**constructor for triButton
     *
     * @param startingX initial x position
     * @param startingY initial y position
     * @param isPointUp orientation boolean, tru means POINTUP false means POINTDOWN
     */
    public TriButton(float startingX, float startingY, boolean isPointUp, final byte stageIndex, ButtonEnum.TitleStageTri triButtonIndex)
    {
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        orientation = isPointUp;
        this.stageIndex=stageIndex;

        this.triButtonIndex=triButtonIndex;
        setX(startingX);
        setY(startingY);
        setWidth(edgeLength);
        setHeight(altitude);

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
            TitleStage.queryTriButtonTouch(x,y);
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
            shapeRenderer.triangle(
                    getX(),
                    getY(),
                    getX() + halfEdgeLength,
                    getY() + altitude,
                    getX() + edgeLength,
                    getY());
        }
        else if(orientation==POINTDOWN){
            shapeRenderer.triangle(
                    getX(),
                    getY() + altitude,
                    getX() + edgeLength,
                    getY() + altitude,
                    getX() + halfEdgeLength,
                    getY());
        }
    }

    /**work out if the triangle has been hit, considering orientation
     *
     */
    public boolean triangleHit(float x, float y) {
        x = x - getX();
        y = y - getY();
        if(orientation == POINTUP && y>0 && y < (x *altitude/halfEdgeLength) && y < ( - x * altitude/halfEdgeLength) + altitude*2     ){
            return true;
        }
        else if(orientation == POINTDOWN && y < altitude && y > ( - x * altitude/halfEdgeLength) + altitude && y > (x *altitude/halfEdgeLength) - altitude  ){
            return true;
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
            case StageInterface.TITLESTAGE:
            {
                TitleStage.touchLogic(triButtonIndex);
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
            case StageInterface.TRIDENTBUILDINGSTAGE:
            {
                TridentBuildingStage.touchLogic(triButtonIndex);
                break;
            }
            case StageInterface.GAMESTAGE:
            {
                break;
            }
            case StageInterface.GAMEOVERSTAGE:
            {
                break;
            }

        }
    }
}
