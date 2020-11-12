package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Array;

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

    /**
     * the trident button will have it's own card button array, for the gameStage only
     * the listener will be removed so the card buttons will just be to display the cards
     */
    Array<CardButton> cardButtonArray = new Array<CardButton>();
    /**
     * some tridents on the game stage will be shown but the cards will be hidden
     * this boolean stops the card arry being drawn
     */
    boolean cardsVisible =false;

    /**
     * in the game stage
     */
    boolean preGameCard=false;
    boolean postGameCard=false;
    /**
     * the preGameCard from the trident building stage will be disaplyed on a triButton
     * instead of a card button
     */
    TriButton preGameCardButton;
    TriButton postGameCardButton;

    static float preGameCardX =720/2-130;
    static float preGameCardY =(float)(1280/2-130*Math.sin(Math.PI/3));
    static float postGameCardX =720/2-130/2;
    static float postGameCardY =(float)(1280/2-130*Math.sin(Math.PI/3));

    /**
     * the adjacent indexes are importatnt for the game stage, they will be set in the setUpGameBoard()
     * method of gameStage, this is the way each trident knows which tridents are next to it, -1 means no adjacent
     */
    int adjacentIndexVertical=-1;
    int adjacentIndexRight =-1;
    int adjacentIndexLeft =-1;


    float oldPosX, oldPosY;

    /**
     * this boolean will be set to true when it's confirmed that the position on a gameboard is taken, this will
     * stop it being selected if something is already placed there,
     */
    boolean placed = false;
    /*if the trident is on the trihand and has been placed , then it gets
     * greyed out so we know it's been placed*/
    boolean greyed = false;
    int ownership=-1;
    int previousOwnership=-1;

    /**
     * this will be used when emitting the data to the other player. if player 1 rotates and flips the trident before placing
     * this will keep track of the position of the cards.
     */
    byte rotation = 0 ;

    /**
     * this will be used when emitting the data to the other player. if player 1 rotates and flips the trident before placing
     * this will keep track of the position of the cards.
     */
    boolean flipped =false;

    /**constructor for triButton
     *
     * @param startingX initial x position
     * @param startingY initial y position
     * @param isPointUp orientation boolean, tru means POINTUP false means POINTDOWN
     */
    public TriButton(StageInterface stageInterface, float startingX, float startingY, boolean isPointUp, final byte stageIndex, ButtonEnum.Tri triButtonIndex)
    {
        font.getData().setScale(2);
        //texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        orientation = isPointUp;
        this.stageIndex=stageIndex;
        this.stageInterface =stageInterface;

        this.triButtonIndex=triButtonIndex;
        setX(startingX);
        setY(startingY);
        updateBounds();

        this.addListener(new ClickListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("TriButton", "touch started at (" + x + ", " + y + ")");
                return true;
            }
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        /*if (isVisible()){*/
            /*the x and y arguments will be relative to the actor so a click on the bottom left of the actor will be
             * x = 0 y = 0 regardless of where the actor is on screen, for my purposes it's more useful to use world
             * coordinates because some of my methods are called from outside this class where access to this actor's
             * position might not be available.
             * clicklistner will fire if the rectangle bounding box is hit, need to
             * further calculate if the triangle is hit*/
            /* this makes the coords relevant to the world*/
            touchMessage(x,y);
            x = x + getX();
            y = y + getY();
            if (triangleHit( x, y)) {
                touchLogic( x, y);
            } else {
                otherHit(x,y);

            }
        /*}*/
    }
    }); /*the end of the this.addListener*/


    }

    /**
     * called from the game stage when we move to the game stage, so after we have successfully set up the
     * tridents, this method will transfer the trident build data across
     * @param a
     * @param b
     * @param c
     */
    public void setUpCardButtons(CardButton a, CardButton b, CardButton c){
    cardButtonArray.clear();
    cardButtonArray.add(a,b,c);
        for (int i = 0; i < cardButtonArray.size; i++) {
            cardButtonArray.get(i).removeListener(cardButtonArray.get(i).clickListener);
            cardButtonArray.get(i).setVisible(true);
            cardButtonArray.get(i).setText();
        }

    }
public void touchMessage(float x, float y){
    Gdx.app.log("TriButton", "touch up in "+triButtonIndex+" visible? "+isVisible()+" width "+(int)getWidth() + " height "+(int)getHeight()+" absolute x " + (int)getX() + " absolute y " + (int)getY()+" relative x " + (int)x + " relative y " + (int)y );

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
            drawCardButtons(batch,parentAlpha);
            drawPreAndPostGameCardButtons(batch,parentAlpha);
        }

    }
    public void drawCardButtons(Batch batch,float parentAlpha){
        if(cardsVisible){
            for (int i=0;i<cardButtonArray.size;i++){
                cardButtonArray.get(i).draw(batch,parentAlpha);
            }
        }
    }

    public void drawPreAndPostGameCardButtons(Batch batch,float parentAlpha){
        if(preGameCard && postGameCard){
            preGameCardButton.draw(batch,parentAlpha);
            postGameCardButton.draw(batch,parentAlpha);
        }
    }
    public void drawShape(ShapeRenderer shapeRenderer) {
        if (isVisible()) {


            Color saveColour = shapeRenderer.getColor();

            if (ownership==-1){
                shapeRenderer.setColor(Color.WHITE);
            }
            else if (ownership==0){
                shapeRenderer.setColor(Color.valueOf( "#E7B521"));
            }
            else if (ownership==1){
                shapeRenderer.setColor(Color.valueOf("#B521E7"));
            }
            else if (ownership==2){
                shapeRenderer.setColor(Color.valueOf("#21E7B5"));
            }
            if (greyed){
                shapeRenderer.setColor(Color.GRAY);
            }

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
            shapeRenderer.setColor(saveColour);
        }
    }

    public void drawShapeFilled(ShapeRenderer shapeRenderer) {
        if (isVisible()) {
            drawCardButtonsShape(shapeRenderer);
            drawPreAndPostGameCardButtonsShape(shapeRenderer);
            Color saveColour = shapeRenderer.getColor();

            if (ownership==-1){
                shapeRenderer.setColor(Color.WHITE);
            }
            else if (ownership==0){
                shapeRenderer.setColor(Color.valueOf( "#E7B521"));
            }
            else if (ownership==1){
                shapeRenderer.setColor(Color.valueOf("#B521E7"));
            }
            else if (ownership==2){
                shapeRenderer.setColor(Color.valueOf("#21E7B5"));
            }
            if (greyed){
                shapeRenderer.setColor(Color.GRAY);
            }
            if(orientation==POINTUP) {
                shapeRenderer.circle(getX()+halfEdgeLength, getY()+altitude/3, edgeLength/10);
            }
            else if(orientation==POINTDOWN) {
                shapeRenderer.circle(getX()+halfEdgeLength, getY()+altitude*2/3, edgeLength/10);
            }
            shapeRenderer.setColor(saveColour);

        }
    }
    public void drawCardButtonsShape(ShapeRenderer shapeRenderer){
        if(cardsVisible){
            for (int i=0;i<cardButtonArray.size;i++){
                cardButtonArray.get(i).drawShapeFilled(shapeRenderer);
            }
        }
    }
    public void drawPreAndPostGameCardButtonsShape(ShapeRenderer shapeRenderer){
        if(preGameCard && postGameCard){
            preGameCardButton.drawShape(shapeRenderer);
            postGameCardButton.drawShape(shapeRenderer);
        }
    }
    /**work out if the triangle has been hit, considering orientation
     *
     */
    public boolean triangleHit(float x, float y) {
        if (isVisible()) {
            Gdx.app.log("TriButton", "test if trianglehit, triButtonIndex " + triButtonIndex);
            x = x - getX();
            y = y - getY();
            if (orientation == POINTUP && y > 0 && y < (x * altitude / halfEdgeLength) && y < (-x * altitude / halfEdgeLength) + altitude * 2) {
                Gdx.app.log("TriButton", "hit");
                return true;
            } else if (orientation == POINTDOWN && y < altitude && y > (-x * altitude / halfEdgeLength) + altitude && y > (x * altitude / halfEdgeLength) - altitude) {
                Gdx.app.log("TriButton", "hit");
                return true;
            }
            Gdx.app.log("TriButton", "miss");
            return false;
        }
        /*return false if not visible */
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
    public void setXPos(float x){
        oldPosX =getX();
        setX(x);
    }
    public void setYPos(float y){
        oldPosY =getY();
        setY(y);
    }
    /*this will be called in the deal stage after the card hand position has been determined,
    specifically after the setTridentHandHeight() method is called
    * it will set the x and y pos of the buttons that relate to suits, like the autobuild select suit button,
    * or the select wild card suit button*/
    public void setButtonToSuitPos(int suit){
        setY(1280-(+CardButton.dealAnimationTridentHandHeight+CardButton.dealAnimationTridentHeight+(CardButton.dealAnimationTridentHeight+CardButton.dealAnimationRowMargin) *suit));
    }
    public Vector2 getPos(){
        return new Vector2(getX(),getY());
    }

    public void setPreGameCard(CardButton cardButton){
        preGameCard=true;
        preGameCardButton= new TriButton(stageInterface, preGameCardX,preGameCardY,true,MyGdxGame.GAMESTAGE,ButtonEnum.Tri.GAMETRIPREGAMECARD);
        preGameCardButton.setText("PreGame\nCard\n"+cardButton.getPip());
    }
    public void setPostGameCard( CardButton cardButton){
        postGameCard=true;
        postGameCardButton= new TriButton(stageInterface, postGameCardX,postGameCardY,true,MyGdxGame.GAMESTAGE,ButtonEnum.Tri.GAMETRIPOSTGAMECARD);
        postGameCardButton.setText("PostGame\nCard\n"+cardButton.getPip());
    }

    public void resetPreGameCard(){
        preGameCard=false;
        preGameCardButton=null;
    }
    public void resetPostGameCard(){
        postGameCard=false;
        postGameCardButton=null;
    }

    /**
     * this will be called in the game stage, when the cards from the trident building stage are added to the
     * trident hand in the gamestage, the position of the tident hand tridents will be set to that of the
     * cards, orienentation will also be set
     */
    public void updatePos(float x, float y, boolean pointUp){
        setX(x);
        setY(y);
        orientation=pointUp;
        for (int i = 0 ; i < cardButtonArray.size;i++){
            cardButtonArray.get(i).setX(x);
            cardButtonArray.get(i).setY(y);
            cardButtonArray.get(i).orientation=pointUp;
        }
    }

    /**
     * Place the selected trident into the empty gameboard trident position
     * this will take on the values of the selected trident,
     */
    public void place(int gameboardIndex, TriButton triButton){
        for (int i =0; i < 3; i++){
            this.cardButtonArray.get(i).value=triButton.cardButtonArray.get(i).value;
            this.cardButtonArray.get(i).text=triButton.cardButtonArray.get(i).text;
            this.cardButtonArray.get(i).colour=triButton.cardButtonArray.get(i).colour;
            this.cardButtonArray.get(i).playerIndex=triButton.cardButtonArray.get(i).playerIndex;
            cardsVisible=true;
            this.ownership=triButton.ownership;
        }
        GameStage.evaluateBattle(gameboardIndex);
    }
    /**
     * cancel any tridents that have been placed in the gameboard and not confirmed
     */
    public void cancelPlace(){
        for (int i =0; i < 3; i++){
            this.cardButtonArray.get(i).value=-1;
            this.cardButtonArray.get(i).text="";
            this.cardButtonArray.get(i).colour= Color.WHITE;
            this.cardButtonArray.get(i).playerIndex=-1;
            cardsVisible=false;
            this.ownership=previousOwnership;
            this.rotation=0;
            this.flipped=false;

        }

    }

    /**
     * ownership will change temporarily during evaluateBattle, if the ownership changes
     * permanently due to confirming a battle, or it being changed during setup, then use this method
     */
    public void confirmOwnership()
    {
        previousOwnership=ownership;

    }
}
