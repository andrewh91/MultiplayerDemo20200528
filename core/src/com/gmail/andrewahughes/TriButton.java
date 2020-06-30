package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TriButton extends Actor {
    StageInterface stageInterface;
    private static boolean POINTUP = true;
    private static boolean POINTDOWN = false;
    boolean orientation = POINTDOWN;

    boolean drawMirror=false;
    float edgeLength = 130;
    /*the altitude of an equilateral triangle will always be edgelength * 0.86602540378443864676372317075294 = sin(60)*/
    float altitude = (float)(edgeLength * Math.sin(Math.PI/3));
    float halfEdgeLength = edgeLength/2;
    float halfAltitude = altitude/2;
    /*this is just used in the centred method*/
    private boolean centred=false;

    BitmapFont font = new BitmapFont();
    /*glyph layout helps provide some extra data on the text which helps centre it properly*/
    GlyphLayout glyphLayout = new GlyphLayout();
    String text;
    /*the text margin will be a gap between the text and the edge of the tributton*/
    float textMargin=0.05f;

    SpriteBatch spriteBatch = new SpriteBatch();
    Texture texture;
    byte stageIndex;
    ButtonEnum.Tri triButtonIndex;
    /**constructor for triButton
     *
     * @param startingX initial x position
     * @param startingY initial y position
     * @param isPointUp orientation boolean, tru means POINTUP false means POINTDOWN
     */
    public TriButton(StageInterface stageInterface, float startingX, float startingY, boolean isPointUp, final byte stageIndex, ButtonEnum.Tri triButtonIndex)
    {
        font.getData().setScale(2);
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        orientation = isPointUp;
        this.stageIndex=stageIndex;
        this.stageInterface =stageInterface;

        this.triButtonIndex=triButtonIndex;
        setX(startingX);
        setY(startingY);
        updateBounds();

        this.addListener(new ClickListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
                return true;
            }
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (isVisible()){
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
                otherHit(x,y);

            }
        }
    }
    }); /*the end of the this.addListener*/


    }




    public void draw(Batch batch, float parentAlpha) {
        /*super.draw(batch, parentAlpha);*/
        //batch.draw(texture,getX(),getY());
        if(isVisible()) {
            if (text != null) {
                /*pass the font and the text string into glyphLayout, then we can access the height and width of the text
                 * useful for proper positioning of the text  */
                glyphLayout.setText(font, text);
                /*draw the text centred in the x axis, and at the top if it's POINTDOWN and at the bottom if it's POINTUP*/
                font.draw(batch, text, getX() + halfEdgeLength - glyphLayout.width / 2, getY() + (orientation ? +altitude * textMargin + glyphLayout.height : +altitude * (1 - textMargin)));

            }
        }

    }
    public void drawShape(ShapeRenderer shapeRenderer) {
        if (isVisible()) {
            if (orientation == POINTUP) {
                shapeRenderer.triangle(
                        getX(),
                        getY(),
                        getX() + halfEdgeLength,
                        getY() + altitude,
                        getX() + edgeLength,
                        getY());
            } else if (orientation == POINTDOWN) {
                shapeRenderer.triangle(
                        getX(),
                        getY() + altitude,
                        getX() + edgeLength,
                        getY() + altitude,
                        getX() + halfEdgeLength,
                        getY());
            }
            /*to help the player realise how orientation works
            * i want to draw a mirror image of the trident hand*/
            if(drawMirror){
                if (orientation == POINTDOWN) {
                    shapeRenderer.triangle(
                            getX(),
                            getY()+altitude,
                            getX() + halfEdgeLength,
                            getY() + altitude+altitude,
                            getX() + edgeLength,
                            getY()+altitude);
                } else if (orientation == POINTUP) {
                    shapeRenderer.triangle(
                            getX(),
                            getY() + altitude-altitude,
                            getX() + edgeLength,
                            getY() + altitude-altitude,
                            getX() + halfEdgeLength,
                            getY()-altitude);
                }

            }
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

    public void otherHit(float x, float y) {

        switch(this.stageIndex) {
            case StageInterface.TITLESTAGE: {
                TitleStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.OPTIONSSTAGE:
            {
                OptionsStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.MATCHMAKINGSTAGE:
            {
                MatchMakingStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.DEALSTAGE:
            {
                DealStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.TRIDENTBUILDINGSTAGE:
            {
                TridentBuildingStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.GAMESTAGE:
            {
                GameStage.queryTriButtonTouch(x, y);
                break;
            }
            case StageInterface.GAMEOVERSTAGE:
            {
                GameOverStage.queryTriButtonTouch(x, y);
                break;
            }
        }
    }
    /**perform whatever action is required when the TriButton is pressed
     *this is called when the clicklistener fires because we touched an actor's bounding box
     * and then either it was confirmed the touch was withing this actor's triangle, or
     * it was not in the triangle and all other actors in the same array were queried and
     * the touch is in one of their triangles,
     * this method calls the handleButtons method overloaded in MyGdxGame, which
     * in turn calls the touchLogic method of the instance of the relevant stage
     * @param x
     * @param y
     */
    public void touchLogic(float x, float y)
    {

        //setX( x);
        //setY( y);
        switch(this.stageIndex)
        {
            case StageInterface.TITLESTAGE:
            {
                stageInterface.handleButtonsTitleTri(triButtonIndex);
                break;
            }
            case StageInterface.OPTIONSSTAGE:
            {
                stageInterface.handleButtonsOptionsTri(triButtonIndex);
                break;
            }
            case StageInterface.MATCHMAKINGSTAGE:
            {
                stageInterface.handleButtonsMatchMakingTri(triButtonIndex);
                break;
            }
            case StageInterface.DEALSTAGE:
            {
                stageInterface.handleButtonsDealTri(triButtonIndex);
                break;
            }
            case StageInterface.TRIDENTBUILDINGSTAGE:
            {
                stageInterface.handleButtonsTridentBuildingTri(triButtonIndex);
                break;
            }
            case StageInterface.GAMESTAGE:
            {
                stageInterface.handleButtonsGameTri(triButtonIndex);
                break;
            }
            case StageInterface.GAMEOVERSTAGE:
            {
                stageInterface.handleButtonsGameOverTri(triButtonIndex);
                break;
            }

        }
    }
    public void setText(String text){
        this.text=text;
    }

    /**
     * increase or decrease the size of the trident to match the text that is provided as
     * an argument, note the text provided in the argument will not be set to the triButton
     */
    public void setTridentToTextSize(String alttext){
        if (alttext != null) {
            glyphLayout.setText(font,alttext);
            edgeLength = ((glyphLayout.width*(float)Math.sin(Math.PI/3)  + glyphLayout.height) / (1-textMargin))/(float)Math.sin(Math.PI/3);
            /*need the bounding box to take on the new values */
            updateBounds();
        }

    }
    /**
     * increase or decrease the size of the trident to match the text that it has been given
     */
    public void setTridentToTextSize(){
        if (text != null) {
            glyphLayout.setText(font,text);
            edgeLength = ((glyphLayout.width*(float)Math.sin(Math.PI/3)  + glyphLayout.height) / (1-textMargin))/(float)Math.sin(Math.PI/3);
            /*need the bounding box to take on the new values */
            updateBounds();
        }

    }

    /**
     * changing the trident size to fit the text will mean the trident's right edge
     * and top edge will move, but the bottom left will stay the same, this method
     * will instead make all edges expand or shrink so that the centre remains the same
     *
     */
    public void setTridentToTextSizeRecentre(){

        float oldWidth = getWidth();
        float oldHeight = getHeight();
        setTridentToTextSize();
        setX(getX()-(getWidth()-oldWidth)/2);
        setY(getY()-(getWidth()-oldHeight)/2);

    }
    /*
    public void setTextToTridentSize(){
        if (text != null) {
            glyphLayout.setText(font,text);
            font.getData().setScale(edgeLength /glyphLayout.width);
            /*need the bounding box to take on the new values *//*
            updateBounds();
        }
    }*/

    /**
     * triButtons are drawn at an x and y pos, the bottom left of the triButton
     * will be at that pos, this method will shift the x and y pos down and
     * left so that the triButton becomes centred on the original x and y
     * I could do this in the constructor but the triButton might not have its
     * final width and height at that point
     */
    public void centre(){
        if(centred==false){
            setX(getX()-getWidth()/2);
            setY(getY()-getHeight()/2);
            centred=true;
        }

    }
    public void updateBounds() {
        /*the altitude of an equilateral triangle will always be edgelength * 0.86602540378443864676372317075294 = sin(60)*/
        altitude = (float)(edgeLength * Math.sin(Math.PI/3));
        halfEdgeLength = edgeLength/2;
        halfAltitude = altitude/2;
        setWidth(edgeLength);
        setHeight(altitude);
    }
    public void setPos(float x, float y){
        setX(x);
        setY(y);
    }
    public Vector2 getPos(){
        return new Vector2(getX(),getY());
    }


}
