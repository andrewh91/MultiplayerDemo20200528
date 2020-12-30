package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;
import static com.gmail.andrewahughes.StageInterface.DECKBUILDINGSTAGE;

public class DeckBuildingStage extends Stage {


    boolean visible = false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;



    static int selectedTridentInTriHand=0;
    static int selectedTridentInTriHandIncrementCount=0;
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    /**
     * this is a total value of all card in all trident in the player's trident hand,
     * we need to calculate this and show it on screen to help the player stay under their handicap
     */
    static int handValue =0;
    static int handValueX=0;
    static int handValueY=0;

    BitmapFont font = new BitmapFont();
    GlyphLayout glyphLayout = new GlyphLayout();

    /**
     * to prevent the player from just choosing the best card there is a value limit.
     * when you pick your 24 cards their pip values must not exceed this value limit
     * if players agree they can alter the value limit, so they can have a bit of
     * variety in games, play a game were lots of high cards are allowed
     */
    final static int VALUELIMITBASE=168;
    static int proposedValueLimitDiff=0;
    /**
     * the player will specify their own handicap, positive numbers means it will be
     * harder for them and negative will be easier as this number is subtracted from
     * the value limit
     */
    static int proposedHandicap=0;
    static int opponentProposedHandicap=0;

    static int yourElo=0;
    static int opponentElo=0;




    public DeckBuildingStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        this.stageInterface = stageInterface;

        this.shapeRenderer = shapeRenderer;

        this.setViewport(viewport);
        this.spriteBatch = batch;


        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);

        font.getData().setScale(2);
    }
    /*called everytime we go to the deck building stage*/
    public void reset()
    {

        createButtons();



    }

    /*the player's trident array will be created initially, but we might want to set
     * the position and visibility. this will be called after the create buttons method to make them all invisible,
     * this is so that the DEALSTAGE can call the draw method of the TRIDENTBUILDINGSTAGE while only drawing the
     * buttons we want, */
    static void updatePlayerTridentHand(){
        float w = CardButton.dealAnimationRectangleWidth/5;
        float h = (float) (w * Math.sin(Math.PI/3));

        /*for each trident*/
        for(int i = 0 ; i < OptionsStage.tridentsEach;i++){
            triButtonArray.get(i).setVisible(true);

            triButtonArray.get(i).edgeLength=w;
            triButtonArray.get(i).updateBounds();
            triButtonArray.get(i).drawMirror=false;
            triButtonArray.get(i).setX(CardButton.dealAnimationRectangleDealX + w/2 * (i));
            triButtonArray.get(i).setY(CardButton.dealAnimationRectangleHeight + CardButton.dealAnimationRectangleDealY-h*2);
            triButtonArray.get(i).orientation=i%2==1?true:false;

            /*if we are playing with pre and post game cards, and the we have looped round
             * to the last trident in this loop, don't draw the mirror, makes no sense for
             * a pre and post game card to ahve a mirror, also set the x slightly further right */
            if(OptionsStage.preAndPostGameCard && i==OptionsStage.tridentsEach-1){
                triButtonArray.get(i).drawMirror=false;
                /*need to do the same for the trihandcards*/
                triButtonArray.get(i).setX(CardButton.dealAnimationRectangleDealX + w/2 * (i)+10);
                triButtonArray.get(i).setY(-1000);
                handValueX=(int)(CardButton.dealAnimationRectangleDealX + w/2 * (i+1));
                handValueY=(int)(CardButton.dealAnimationRectangleHeight + CardButton.dealAnimationRectangleDealY-h*1.5);
            }
        }
    }

    /**
     * the available tridents will have been set up with no position, we need to give them position
     * and visibility and orientation, but importantly, orientation needs to come from the premadeDeck class
     * and each suit might not necessarily use alternative orientation
     */
    static void updateAvailableTridents()
    {
        float w = CardButton.dealAnimationRectangleWidth/5;
        float h = (float) (w * Math.sin(Math.PI/3));
        /*for the 17 tridents after the tridents set up for the trident hand*/
        for(int i = OptionsStage.tridentsEach ; i < OptionsStage.tridentsEach + 16;i++){
            triButtonArray.get(i).setVisible(true);

            triButtonArray.get(i).edgeLength=w;
            triButtonArray.get(i).updateBounds();
            triButtonArray.get(i).drawMirror=false;
            triButtonArray.get(i).setX(CardButton.dealAnimationRectangleDealX + w * ((i -OptionsStage.tridentsEach)%4));
            triButtonArray.get(i).setY(CardButton.dealAnimationRectangleHeight + CardButton.dealAnimationRectangleDealY-h*2 - (h+CardButton.dealAnimationRectangleDealY)*(float)(Math.floor((i -OptionsStage.tridentsEach)/4)+1));
            triButtonArray.get(i).orientation=(i-OptionsStage.tridentsEach)%2==1?true:false;

            triButtonArray.get(i).setUpCardButtons(new CardButton(stageInterface,triButtonArray.get(i).getX(),triButtonArray.get(i).getY(),triButtonArray.get(i).orientation,(byte)0,DECKBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0),new CardButton(stageInterface,triButtonArray.get(i).getX(),triButtonArray.get(i).getY(),triButtonArray.get(i).orientation,(byte)1,DECKBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0),new CardButton(stageInterface,triButtonArray.get(i).getX(),triButtonArray.get(i).getY(),triButtonArray.get(i).orientation,(byte)2,DECKBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0));
            triButtonArray.get(i).cardButtonArray.get(0).value = MyGdxGame.premadeDeck.d01.get((int)Math.floor((i -OptionsStage.tridentsEach)*3)+0);
            triButtonArray.get(i).cardButtonArray.get(1).value = MyGdxGame.premadeDeck.d01.get((int)Math.floor((i -OptionsStage.tridentsEach)*3)+1);
            triButtonArray.get(i).cardButtonArray.get(2).value = MyGdxGame.premadeDeck.d01.get((int)Math.floor((i -OptionsStage.tridentsEach)*3)+2);
            triButtonArray.get(i).cardButtonArray.get(0).setText();
            triButtonArray.get(i).cardButtonArray.get(1).setText();
            triButtonArray.get(i).cardButtonArray.get(2).setText();
            triButtonArray.get(i).cardButtonArray.get(0).setX(triButtonArray.get(i).getX());
            triButtonArray.get(i).cardButtonArray.get(1).setX(triButtonArray.get(i).getX());
            triButtonArray.get(i).cardButtonArray.get(2).setX(triButtonArray.get(i).getX());
            triButtonArray.get(i).cardButtonArray.get(0).setY(triButtonArray.get(i).getY());
            triButtonArray.get(i).cardButtonArray.get(1).setY(triButtonArray.get(i).getY());
            triButtonArray.get(i).cardButtonArray.get(2).setY(triButtonArray.get(i).getY());

            CardButton.edgeLength = triButtonArray.get(i).edgeLength;

            triButtonArray.get(i).cardButtonArray.get(0).updateBounds();
            triButtonArray.get(i).cardButtonArray.get(1).updateBounds();
            triButtonArray.get(i).cardButtonArray.get(2).updateBounds();
            triButtonArray.get(i).cardsVisible=true;
        }
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawTriButtonsShapeFilled(shapeRenderer);
            shapeRenderer.end();

            //Gdx.gl.glLineWidth(3);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape(shapeRenderer);
            shapeRenderer.end();
            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons(spriteBatch);

            glyphLayout.setText(font,handValue+"");
            font.draw(spriteBatch   , handValue+"",handValueX,handValueY);

            glyphLayout.setText(font,"Value Limit: "+proposedValueLimitDiff+"");
            font.draw(spriteBatch   , "Value Limit: "+proposedValueLimitDiff+"",720/3-65-65-glyphLayout.width/2,260+160);
            glyphLayout.setText(font,"Your Handicap: "+proposedHandicap+"");
            font.draw(spriteBatch   , "Your Handicap: "+proposedHandicap+"",720/3*2-65-65-glyphLayout.width/2,260+160);
            glyphLayout.setText(font,"Opp. Handicap: "+opponentProposedHandicap+"");
            font.draw(spriteBatch   , "Opp. Handicap: "+opponentProposedHandicap+"",720/3*2-65-65-glyphLayout.width/2,260-120);
            glyphLayout.setText(font,"Your ELO: "+yourElo+"");
            font.draw(spriteBatch   , "Your ELO: "+yourElo+"",720-65-65-glyphLayout.width/2,260+160);
            glyphLayout.setText(font,"Opp. ELO: "+opponentElo+"");
            font.draw(spriteBatch   , "Opp. ELO: "+opponentElo+"",720-65-65-glyphLayout.width/2,260-120);


            spriteBatch.end();
        }
    }

    static void  drawTriButtons(SpriteBatch spriteBatch) {
        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).draw(spriteBatch,1.0f);

        }
    }
    /**
     * draw the trident buttons shape - which should just be it's bounds
     */
    static void drawTriButtonsShape(ShapeRenderer shapeRenderer) {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShape(shapeRenderer);

        }
    }
    static void drawTriButtonsShapeFilled(ShapeRenderer shapeRenderer) {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShapeFilled(shapeRenderer);

        }
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void createButtons(){
        /*when creating new buttons we pass in the enum for that button so the button can store it
         * so it can reference itself later. the add button method also needs this stage
         * to add the actor to the stage and our array of buttons so we can add it to that too
         * when adding to the array the method actually inserts it in the array at the enum.value index
         * this means if we add the buttons out of order it will cause an error, which is good because
         * then i can make sure the buttons are in the correct order
         *
         * also note that any buttons created when the tridentbuildingstage is initialised will be set to invisible
         * in the reset method which is called before we can see the tridentbuilding stage, so set them to visible
         * in the reset method if needed*/
        /*trident buttons*/

        /*this is the player's trident hand*/


        /*the maximum number of tridents will be 8 in the players' trident hand,
         */
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY1), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY2), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY3), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY4), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY5), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY6), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY7), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY8), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERTRIDENTARRAY9), triButtonArray,this);

        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY1), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY2), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY3), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY4), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY5), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY6), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY7), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY8), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY9), triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY10),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY11),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY12),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY13),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY14),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY15),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY16),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY17),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY18),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,-1000,-1000,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY19),triButtonArray,this);



        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGNEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DECKBUILDINGNEXTSTAGE).setText("Game");

        stageInterface.addTriButton(new TriButton(stageInterface,720/3-65,280,true,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGLIMITUP),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DECKBUILDINGLIMITUP).setText("Limit Up");
        stageInterface.addTriButton(new TriButton(stageInterface,720/3-65,280-130,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGLIMITDOWN),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DECKBUILDINGLIMITDOWN).setText("Limit Down");
        stageInterface.addTriButton(new TriButton(stageInterface,720/3*2-65,280,true,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGHANDICAPUP),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DECKBUILDINGHANDICAPUP).setText("Handicap Up");
        stageInterface.addTriButton(new TriButton(stageInterface,720/3*2-65,280-130,false,StageInterface.DECKBUILDINGSTAGE, ButtonEnum.Tri.DECKBUILDINGHANDICAPDOWN),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.DECKBUILDINGHANDICAPDOWN).setText("Handicap Down");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setTridentToTextSize();

        /*this will set the position of the tridents*/
        updatePlayerTridentHand();

        updateAvailableTridents();

    }


    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryTriButtonTouch(float x, float y){
        /*could also do for all cardButtonArray but cardButtons shouldn't be placed close to TriButtons anyway*/
        /*for each triButton in this stage*/
        boolean touchHandled=false;
        for(int i=0;i<triButtonArray.size;i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if(triButtonArray.get(i).triangleHit(x,y)){
                triButtonArray.get(i).touchLogic(x,y);
                touchHandled=true;
                Gdx.app.log("tridentbuildingStage","queryTriButtonTouch,triButtonArray.touchLogic");
                break;
            }
        }
        /*shouldn't need to use this, i don't think tridents will be split into their cards on this stage
        if(touchHandled) {
            for (int i = 0; i < cardButtonArrayTridentHand.size; i++) {
                if (cardButtonArrayTridentHand.get(i).triangleHit(x, y)) {
                    cardButtonArrayTridentHand.get(i).touchLogic(x, y);
                    Gdx.app.log("tridentbuildingStage","queryTriButtonTouch,cardButtonArrayTridentHand.touchLogic");
                    break;
                }
            }
        }

         */
    }

    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a cardButton's bounding box but not in it's triangle
     * this method will test all actors in the array if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryCardButtonTouch(float x, float y){

        /*should be ok to leave this empty
         */
    }

    /**
     * this method will set the highlighted trident value to the index of the next trident in the
     * players trident hand, so that the selected available trident will move there.
     * the argument specifies the orientation of the trident we are looking for
     * @return will return false if there is not a free trident in the trident hand that matches the
     * orientation of the argument
     */
    public static boolean selectNextTriHandTrident(boolean orientation)
    {
        boolean success;
        /*we need this counter to stop us infinitely incrementing */
        if(selectedTridentInTriHandIncrementCount>OptionsStage.tridentsEach)
        {
            Gdx.app.log("DECKBUILDINGSTAGE","selectedTridentInTriHandIncrementCount is too high "+selectedTridentInTriHandIncrementCount+" tested all spaces in trident hand");
            success=false;
        }
        /*if the current trihand trident is not already used and is the correct orientation*/
        else
        {
            if (triButtonArray.get(selectedTridentInTriHand).greyed==false&&triButtonArray.get(selectedTridentInTriHand).orientation==orientation)
            {
                Gdx.app.log("DECKBUILDINGSTAGE","found suitable trident space "+selectedTridentInTriHand);
                selectedTridentInTriHandIncrementCount=0;
                success= true;
            }
            /*we will have to increment the selectedTridentInTriHand and try again*/
            else
            {
                Gdx.app.log("DECKBUILDINGSTAGE","increment and retry");
                selectedTridentInTriHand++;
                selectedTridentInTriHandIncrementCount++;
                selectedTridentInTriHand=selectedTridentInTriHand%(OptionsStage.tridentsEach-1);
                success = selectNextTriHandTrident(orientation);
            }
        }
        return success;
    }
    /**
     * if you click a trident that's part of the available tridents, and it has not already been used
     * put it in the trident hand if there is an available space and it will not exceed the handicap
     * @param index this will be the index of the touched button in the trident array
     */
    public static void selectAvailableTrident(int index)
    {
        if(triButtonArray.get(index).greyed==true)
        {
            Gdx.app.log("DECKBUILDINGSTAGE","Trident not available");

        }
        else {
            /*if the trident can be added to the trident hand*/

            selectedTridentInTriHandIncrementCount=0;
            if (selectNextTriHandTrident(triButtonArray.get(index).orientation)) {
                triButtonArray.get(index).greyed=true;
                triButtonArray.get(selectedTridentInTriHand).greyed=true;
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray = triButtonArray.get(index).cardButtonArray;
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(0).setX(triButtonArray.get(selectedTridentInTriHand).getX());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(0).setY(triButtonArray.get(selectedTridentInTriHand).getY());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(1).setX(triButtonArray.get(selectedTridentInTriHand).getX());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(1).setY(triButtonArray.get(selectedTridentInTriHand).getY());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(2).setX(triButtonArray.get(selectedTridentInTriHand).getX());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(2).setY(triButtonArray.get(selectedTridentInTriHand).getY());
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(0).setText();
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(1).setText();
                triButtonArray.get(selectedTridentInTriHand).cardButtonArray.get(2).setText();
                triButtonArray.get(selectedTridentInTriHand).cardsVisible=true;
                triButtonArray.get(index).cardsVisible=false;
                triButtonArray.get(selectedTridentInTriHand).availableTridentIndex=index;
                Gdx.app.log("DECKBUILDINGSTAGE","Trident added to hand");
                updateTotalValue();
            }
            else
            {
                Gdx.app.log("DECKBUILDINGSTAGE","Trident won't fit in trident hand");

            }
        }
    }
    public static void deselectAvailableTrident(int index)
    {
        if (triButtonArray.get(index).greyed) {
            triButtonArray.get(index).greyed = false;
            triButtonArray.get(index).cardsVisible = false;
            triButtonArray.get(triButtonArray.get(index).availableTridentIndex).greyed = false;
            triButtonArray.get(triButtonArray.get(index).availableTridentIndex).cardsVisible = true;
            selectedTridentInTriHand=index;
            triButtonArray.get(index).cardButtonArray.get(0).setX(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getX());
            triButtonArray.get(index).cardButtonArray.get(0).setY(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getY());
            triButtonArray.get(index).cardButtonArray.get(1).setX(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getX());
            triButtonArray.get(index).cardButtonArray.get(1).setY(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getY());
            triButtonArray.get(index).cardButtonArray.get(2).setX(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getX());
            triButtonArray.get(index).cardButtonArray.get(2).setY(triButtonArray.get(triButtonArray.get(index).availableTridentIndex).getY());


            Gdx.app.log("DECKBUILDINGSTAGE", "deselected trident in trident hand, and the trident in available tridents");
            updateTotalValue();
        }
        else {
            Gdx.app.log("DECKBUILDINGSTAGE", "can't deselect, it wasn't selected");
        }

    }
    /**
     * this method will add up the value of the 3 cards on each trident in the trident hand
     */
    public static void updateTotalValue()
    {
        handValue =0;
        for (int i = 0;i<OptionsStage.tridentsEach;i++)
        {
            if (triButtonArray.get(i).greyed==true)
            {
                handValue +=triButtonArray.get(i).cardButtonArray.get(0).getPip();
                handValue +=triButtonArray.get(i).cardButtonArray.get(1).getPip();
                handValue +=triButtonArray.get(i).cardButtonArray.get(2).getPip();
            }
        }
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){
        Gdx.app.log("TRIDENTBUILDINGSTAGE"," tri button array clicked, button "+triButtonIndex);
        /*
        for (int i=0;i<cardButtonArray.size;i++){
            Gdx.app.log("TRIDENTBUILDINGSTAGE"," card "+i+ " x "+(int)cardButtonArray.get(i).getX()+ " y "+(int)cardButtonArray.get(i).getY()+" enum" +cardButtonArray.get(i).cardButtonIndex+" player index "+ MyServer.player.index+" card index "+ cardButtonArray.get(i).playerIndex+" visible "+ cardButtonArray.get(i).isVisible());
        }
        for (int i=0;i<cardButtonArrayTridentHand.size;i++){
            Gdx.app.log("TRIDENTBUILDINGSTAGE"," tri hand card "+i+ " x "+(int)cardButtonArrayTridentHand.get(i).getX()+ " y "+(int)cardButtonArrayTridentHand.get(i).getY()+" enum" +cardButtonArrayTridentHand.get(i).cardButtonIndex+" player index "+ MyServer.player.index+" card index "+ cardButtonArrayTridentHand.get(i).playerIndex+" visible "+ cardButtonArrayTridentHand.get(i).isVisible());
        }*/

        switch(triButtonIndex){

            case DECKBUILDINGNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.GAMESTAGE);
            break;
            }
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY1:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY2:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY3:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY4:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY5:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY6:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY7:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY8:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY9:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY10:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY11:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY12:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY13:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY14:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY15:
            case DECKBUILDINGPLAYERAVAILABLETRIDENTARRAY16:
            {
                selectAvailableTrident(triButtonIndex.value);
                break;
            }
            case DECKBUILDINGPLAYERTRIDENTARRAY1:
            case DECKBUILDINGPLAYERTRIDENTARRAY2:
            case DECKBUILDINGPLAYERTRIDENTARRAY3:
            case DECKBUILDINGPLAYERTRIDENTARRAY4:
            case DECKBUILDINGPLAYERTRIDENTARRAY5:
            case DECKBUILDINGPLAYERTRIDENTARRAY6:
            case DECKBUILDINGPLAYERTRIDENTARRAY7:
            case DECKBUILDINGPLAYERTRIDENTARRAY8:
            {
                deselectAvailableTrident(triButtonIndex.value);
                break;
            }
            case DECKBUILDINGLIMITUP:
            {
                proposedValueLimitDiff++;
                if (proposedValueLimitDiff>84)
                {
                    proposedValueLimitDiff=84;
                }
                break;
            }
            case DECKBUILDINGLIMITDOWN:
            {
                proposedValueLimitDiff--;
                if (proposedValueLimitDiff<-84)
                {
                    proposedValueLimitDiff=-84;
                }

                break;
            }
            case DECKBUILDINGHANDICAPUP:
            {
                proposedHandicap++;
                if (proposedHandicap>42)
                {
                    proposedHandicap=42;
                }
                /*TODO emit the handicap here*/
                break;
            }
            case DECKBUILDINGHANDICAPDOWN:
            {
                proposedHandicap--;
                if (proposedHandicap<-42)
                {
                    proposedHandicap=-42;
                }
                /*TODO emit the handicap here*/
                break;
            }
            default:
                Gdx.app.log("DECKBUILDINGSTAGE", "DEFAULT "+triButtonIndex+ " pos " + triButtonArray.get(triButtonIndex.value).getX());
                //throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
}