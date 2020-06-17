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

public class CardButton  extends Actor {
    StageInterface stageInterface;
    private static final boolean POINTUP = true;
    private static final boolean POINTDOWN = false;
    /**
     * true is POINT UP
     */
    boolean orientation = POINTDOWN;
    public static final byte VERTICAL = 0;
    public static final byte LEFT = 1;
    public static final byte RIGHT = 2;
    byte position = VERTICAL;
    /*the longest edge length, should be the same as the edge length of the trident*/
    static float edgeLength = 50.0f;
    /*assuming the isosceles card shape has the longest edge parallel to the horizon
    * the altitude is tan(30) * edgeLength /2    = 0.28867513459481288225457439025098 * edgeLength*/
    static float altitude = (float)( Math.tan(Math.PI/6) * edgeLength /2);
    /*the length of the remaining 2 sides is tan(30) * edgeLength  = 0.57735026918962576450914878050196 * edgeLength*/
    static float otherLength = (float)( Math.tan(Math.PI/6) * edgeLength);
    static float halfEdgeLength = edgeLength/2;
    static float tridentAltitude = (float)(edgeLength * Math.sin(Math.PI/3));
    /*the cards x and y coordinate will be the bottom left of the imaginary trident's bounding box
    * this is potentially confusing since a POINTDONW RIGHT card's position is quite far from the
    * drawn object*/
    BitmapFont font = new BitmapFont();
    static BitmapFont fadeFont = new BitmapFont();
    /*glyph layout helps provide some extra data on the text which helps centre it properly*/
    GlyphLayout glyphLayout = new GlyphLayout();
    String text;
    /*the text margin will be a gap between the text and the edge of the tributton*/
    float textMargin=0.05f;
    SpriteBatch spriteBatch = new SpriteBatch();
    Texture texture;
    byte stageIndex;
    ButtonEnum.Card cardButtonIndex;

    static float dealAnimationRectangleDisplayX =0;
    static float dealAnimationRectangleDisplayY =0;
    static float dealAnimationRectangleDealX =0;
    static float dealAnimationRectangleDealY =0;
    static float dealAnimationRectangleWidth=Gdx.graphics.getWidth();
    static float dealAnimationRectangleHeight=Gdx.graphics.getHeight();
    static float dealAnimationTridentEdgeLength=50f;

    static float dealAnimationRowMargin=10;

    static float dealAnimationTridentHandHeight =0;
    static float dealAnimationTridentHeight =0;
    static float dealAnimationTridentWidth =0;

    float dealAnimationPositionX;
    float dealAnimationPositionY;
    /**
     * this is just used to help position the cardButtons in the setDealAnimationPosition method
     */
    static byte dealAnimationSuit =-1;
    static byte dealAnimationPreviousSuit =0;

    byte value;

    /*when the cards are dealt to the player, all that happens is this
    * variable is set to the player's index, which will be used in the
    * touch logic, deafult is 0 so we know it doesn't have a player,
    * otherwise value will be 1, 2 or 3*/
    byte playerIndex =0;

    /**constructor for triButton
     *
     * @param startingX initial x position
     * @param startingY initial y position
     * @param isPointUp orientation boolean, tru means POINTUP false means POINTDOWN
     * @param stageIndex this will be used in the touchLogic method to figure out which stage this button is in
     */
    public CardButton(StageInterface stageInterface,float startingX, float startingY, boolean isPointUp, byte position, final byte stageIndex, ButtonEnum.Card cardButtonIndex)
    {
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        orientation = isPointUp;
        this.position = position;
        this.stageIndex = stageIndex;
        this.stageInterface = stageInterface;

        this.cardButtonIndex=cardButtonIndex;
        setX(startingX);
        setY(startingY);
        updateBounds();
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
                    TridentBuildingStage.queryCardButtonTouch(x,y);
                }
            }
        }); /*the end of the this.addListener*/


    }


    public void draw(Batch batch, float parentAlpha) {
        /*super.draw(batch, parentAlpha);*/
        /*batch.draw(texture,getX(),getY());*/
        if(isVisible()) {
            if (text != null) {
                /*pass the font and the text string into glyphLayout, then we can access the height and width of the text
                 * useful for proper positioning of the text  */
                glyphLayout.setText(fadeFont, text);
                if(position==VERTICAL){
                    /*draw the text centred in the x axis, and at the top if it's POINTDOWN and at the bottom if it's POINTUP*/
                    fadeFont.draw(batch, text, getX() + halfEdgeLength - glyphLayout.width / 2, getY() + (orientation ? +tridentAltitude * textMargin + glyphLayout.height : +tridentAltitude * (1 - textMargin)));
                }
                if(position==LEFT){
                    fadeFont.draw(batch, text, getX() + edgeLength/3 - glyphLayout.width / 2, getY() + (orientation ? +altitude + glyphLayout.height: tridentAltitude - altitude ));
                }
                if(position==RIGHT){
                    fadeFont.draw(batch, text, getX() + edgeLength/3*2 - glyphLayout.width / 2, getY() + (orientation ? +altitude + glyphLayout.height: tridentAltitude - altitude ));
                }
            }
        }


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

    /**
     * this will be called in the dealStage, will be a simple animation to move all
     * cardButtons to the centre of the screen so they overlap
     */
    public void overlapAnimation(float percentage){
        if(percentage<=0.5f){
            setX(getX()+((dealAnimationRectangleDisplayX +dealAnimationRectangleWidth/2 ) - getX())*(percentage*2));
        }
        else if(percentage<1) {
            setX(dealAnimationRectangleDisplayX +dealAnimationRectangleWidth/2 );
            setY(getY() + ((dealAnimationRectangleDisplayY + dealAnimationRectangleHeight / 2) - getY()) * (percentage-0.5f)*2);
        }
        else{
            setX(dealAnimationRectangleDisplayX +dealAnimationRectangleWidth/2 );
            setY(dealAnimationRectangleDisplayY +dealAnimationRectangleHeight/2 );
        }
    }
    /**
     * this will be called in the dealStage, will be a simple animation to move all
     * cardButtons to the player's card hand, that position must have previously
     * been set in setDealAnimationPosition method
     */
    public void moveToPositionAnimation(float percentage){
        /*first we need to work out the correct position for this cardButton based
        * on the playerIndex and the index - as well as the numberOfPlayers*/

        if(percentage<=0.5f){
            setX(getX()+((dealAnimationPositionX) - getX())*(percentage*2));
        }
        else if(percentage<1) {
            setX(dealAnimationPositionX);
            setY(getY() + ((dealAnimationPositionY) - getY()) * (percentage-0.5f)*2);
        }
        else{
            setX(dealAnimationPositionX);
            setY(dealAnimationPositionY);
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
                stageInterface.handleButtonsTridentBuildingCard(cardButtonIndex);
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

    /**
     * this will set a rectangle in which the cardButtons are arranged during the deal stage animation
     * default values are edge of screen
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void setDealAnimationRectangle(float x,float y,float width,float height){
        dealAnimationRectangleDisplayX =x;
        dealAnimationRectangleDisplayY =y;
        dealAnimationRectangleDealX =x;
        dealAnimationRectangleDealY =y;
        dealAnimationRectangleWidth=width;
        dealAnimationRectangleHeight=height;
        /*also work out how larger a trident edgeLength this allows us to use
        * The height of the defined rectangle / 5 /0.86 will equal the edgeLength,
            Or the width of the defined rectangle /2.5 will be the edge length,
            We need to use whichever is smaller
            */
        dealAnimationTridentEdgeLength = (float)(dealAnimationRectangleHeight/5/Math.sin(Math.PI/3));
        float altEdgeLength = dealAnimationRectangleWidth/2.5f;
        if(altEdgeLength<dealAnimationTridentEdgeLength){
            dealAnimationTridentEdgeLength=altEdgeLength;
        }
        edgeLength=dealAnimationTridentEdgeLength;
        /*now work out how to centre the grid of tridents*/
        float triGridWidth = dealAnimationTridentEdgeLength*2.5f;
        float triGridHeight = (float)(dealAnimationTridentEdgeLength * 5 * Math.sin(Math.PI/3));
        dealAnimationRectangleDisplayX = dealAnimationRectangleDisplayX + dealAnimationRectangleWidth/2f - triGridWidth/2f;
        dealAnimationRectangleDisplayY = dealAnimationRectangleDisplayY + dealAnimationRectangleHeight/2f - triGridHeight/2f;
    }

    /**
     * this is a helper method to set up the vertical, left or right position,
     * the x and y position, and orientation, for when the cards are displayed in the dealStage
     * the app should appear in portrait mode and be roughly 720 by 1280
     */
    public void setDealAnimationValues(int index){
        /*the size of the cards will have been amended before calling this method
        * updateBounds has already been called, but need to call setWidth and
        * setHeight non statically so do it here */
        setWidth(edgeLength*2);
        setHeight(tridentAltitude);

        int tridentIndex = (int)Math.floor(index/3);
        setX(dealAnimationRectangleDisplayX +(tridentIndex % 4) * (dealAnimationTridentEdgeLength / 2));
        setY(dealAnimationRectangleDisplayY +(float)(dealAnimationRectangleHeight- ((Math.floor(tridentIndex/4)+1)*dealAnimationTridentEdgeLength*Math.sin(Math.PI/3))));
        /*if on an even row, 0, 2 or 4*/
        if(Math.floor(tridentIndex/4)%2==0){
            /*if on an even column, 0, 2 then POINTUP*/
            if(tridentIndex%2==0){
                orientation = POINTUP;
            }
            /*if on an odd column 1, 3 then POINTDOWN*/
            else {
                orientation = POINTDOWN;
            }
        }
        /*if on an odd row 1, 3*/
        else{
            /*if on an odd column, 1, 3 then  POINTUP*/
            if(tridentIndex%2==1){
                orientation = POINTUP;
            }
            /*if on an even column, 0, 2 then POINTDOWN*/
            else {
                orientation = POINTDOWN;
            }
        }
        /*index mod 3 will always give the appropriate VERTICAL, LEFT or RIGHT VALUE*/
        position = (byte)(index%3);
        /*set the text to the index value, this should really be the suit and pip of
        * the card, either way this is just the initial value for when the cards are
        * all displayed in order */
        text = ""+index;

    }

    /**
     * this will be called in the deal method after the card buttons
     * have been assigned a playerIndex, this method will work out where
     * the cardButton's position on in the dealAnimationRectangle should be
     * so that it appears in the player's card hand, the method
     * moveToPositionAnimation will then move the cardButton to that location
     */
    public void setDealAnimationPosition(){
        /*if the cardbutton belongs to the current player*/
        if(playerIndex==MyServer.player.index){
            /*need to reserve space at the top of the rectangle for trident hand*/
                dealAnimationSuit=(byte)(dealAnimationPreviousSuit==getSuit()?dealAnimationSuit+1:-1);
                dealAnimationPreviousSuit=getSuit();dealAnimationPositionY = Gdx.graphics.getHeight()-(+dealAnimationTridentHandHeight+(dealAnimationTridentHeight+dealAnimationRowMargin) *getSuit());
                dealAnimationPositionX = (float)(dealAnimationRectangleDealX +(dealAnimationTridentWidth*0.5f*Math.floor(dealAnimationSuit/3)));
                orientation= getSuit()%2==0 ? (Math.floor(dealAnimationSuit/3)%2==0?POINTUP:POINTDOWN) : (Math.floor(dealAnimationSuit/3)%2==0?POINTDOWN:POINTUP);
                position=(byte)(dealAnimationSuit%3);
        }
        else if(playerIndex==(MyServer.player.index-1%3)){
            dealAnimationPositionX = -Gdx.graphics.getWidth();
        }
        else if(playerIndex==(MyServer.player.index+1%3)){
            dealAnimationPositionX = Gdx.graphics.getWidth()*2;
        }
    }


    /**
     * this will be called in deal stage, it will be equal to the height
     * of the trident hand plus row margin *2 + dealAnimationRectangleY
     */
    public static void setTridentHandHeight(){
        dealAnimationTridentWidth =dealAnimationRectangleWidth/5f;
        edgeLength=dealAnimationTridentWidth;
        updateBounds();
        dealAnimationTridentHeight =(float)(dealAnimationTridentWidth*Math.sin(Math.PI/3));
        dealAnimationTridentHandHeight = dealAnimationRectangleDealY + dealAnimationTridentHeight *3+dealAnimationRowMargin*2;
    }

    /**
     * called in the dealStage, once we have created a random list of values 0-51
     * we can assign one to this cardButton
     */
    public void setValue(byte value){
        this.value = value;
    }

    /**
     * this method will just work out what suit the card is based on the value
     * suit 0,1,2 or 3
     */
    public byte getSuit(){
        return (byte)Math.floor(this.value/13);
    }
    /**
     * this method will just work out what pip the card is based on the value
     * pip from 0 to 12
     */
    public byte getPip(){
        return (byte)(this.value%13);
    }
    /**
     * called in the dealStage, once the random values have already been assigned to
     * the cardButtons, we can call this to actually set the text to match the value
     * this text will display the suit and pip value of the card, this should be
     * replaced by actual pictures eventually
     *
     * this will be called at the end of the overlap method
     *
     * before this method is called the default text will just be the index
     */
    public void setText(){
        this.text=""+this.value;

    }
    public void setPlayerIndex(byte index){
        playerIndex=index;
    }

    public static void updateBounds() {

        /*assuming the isosceles card shape has the longest edge parallel to the horizon
         * the altitude is tan(30) * edgeLength /2    = 0.28867513459481288225457439025098 * edgeLength*/
        altitude = (float)( Math.tan(Math.PI/6) * edgeLength /2);
        /*the length of the remaining 2 sides is tan(30) * edgeLength  = 0.57735026918962576450914878050196 * edgeLength*/
        otherLength = (float)( Math.tan(Math.PI/6) * edgeLength);
        halfEdgeLength = edgeLength/2;
        tridentAltitude = (float)(edgeLength * Math.sin(Math.PI/3));

    }

}
