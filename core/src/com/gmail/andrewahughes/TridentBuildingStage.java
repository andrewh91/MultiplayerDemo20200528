package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;


import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;

public class TridentBuildingStage extends Stage {

    boolean visible =false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /**
     * this is an array of 52 card buttons, one for each card in the game, it's used in deal stage, trident stage and game stage
     */
    static Array<CardButton> cardButtonArray = new Array<CardButton>();
    /**
     * this is an array of card buttons that make up the trident hand in the TridentBuildingStage only
     */
    static Array<CardButton> cardButtonArrayTridentHand = new Array<CardButton>();
    static Array<TriButton> triButtonArray = new Array<TriButton>();
    /**
     * this array will help us know which cards in the trident hand have been filled
     * variable will be false if no card has been assigned to this position in the
     * player's trident hand
     */
    static Array<Boolean> triHandCardFilledArray = new Array<Boolean>();
    /**
     * only one card in the trident hand can be highlighted at once,
     * this int will determine which one. note,
     */
    static int highlightPos=0;
    /**
     * the autobuild methods will produce a whole 3 card trident, so i need to make sure
     * there is space in the hand array to accomodate it, this value will be calulated
     * everytime the hand array is modified. it will be either -1 indicating there is no free
     * trident or a value divisible by 3 indicating the start of the next free trident
     */
    static int tridentHighlightPos=0;
    /**
     * advanceHighlightPosition is called recursively if the next highlight position
     * is not available, it would be called infinitely if no positions are available
     * if not for this counter which breaks the loop if all positions are filled
     */
    static int highlightPosCounter=0;

    /*used in the autobuild*/
    static final int SUITNATURE =0;
    static final int SUITLIGHT  =1;
    static final int SUITDEMON  =2;
    static final int SUITDARK   =3;
    static final int SUITNONE   =-1;

    /*used in the autobuild*/
    static final int PIPHI  =12;
    static final int PIPMED =7;
    static final int PIPLOW =0;

    /*used in the autobuild*/
    ButtonEnum.Card cardIndex1;
    ButtonEnum.Card cardIndex2;
    ButtonEnum.Card cardIndex3;

    ButtonEnum.Card cardIndex1a;
    ButtonEnum.Card cardIndex2a;
    ButtonEnum.Card cardIndex3a;

    /*used in the autobuild*/
    int autoBuildSelectedPip1;
    int autoBuildSelectedPip2;
    int autoBuildSelectedPip3;

    /*used in the autobuild*/
    int autoBuildSelectedSuit1=SUITNONE;
    int autoBuildSelectedSuit2=SUITNONE;

    /*used in the autobuild*/
    /**
     * if the autobuild selects 3 cards when you choose the pip option, but then when
     * you choose the suit option, that suit doesn't have enough cards, we need to set
     * this flag to cancel out of it
     */
    boolean haveViableCards=true;
    /**
     * this will be set to true when the autobuild button is clicked, it will indicate that we are halfway through
     * setting up a autobuild trident, and set to false again on confirming the autobuild.
     * if another card is clicked when autoBuildOpen is true then we should cancel the autobuild
     */
    boolean autoBuildOpen = false;

    public TridentBuildingStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        this.stageInterface =stageInterface;

        this.shapeRenderer = shapeRenderer;

        this.setViewport(viewport);
        this.spriteBatch = batch;


        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);

        createButtons();

    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons(spriteBatch);
            drawCardButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape(shapeRenderer);
            drawCardButtonsShape();
            drawCardButtonHighlightShape();
            shapeRenderer.end();
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
    void drawCardButtonsShape() {

        for(int i=0;i<cardButtonArray.size;i++) {
            cardButtonArray.get(i).drawShape(shapeRenderer);

        }
        for(int i=0;i<cardButtonArrayTridentHand.size;i++) {
            cardButtonArrayTridentHand.get(i).drawShape(shapeRenderer);

        }
    }
    void drawCardButtonHighlightShape() {
        if(highlightPos>-1) {
            cardButtonArrayTridentHand.get(highlightPos).drawHighlightShape(shapeRenderer);
        }

    }
    void drawCardButtons() {

        for(int i=0;i<cardButtonArray.size;i++) {
            cardButtonArray.get(i).draw(spriteBatch,1.0f);

        }
        for(int i=0;i<cardButtonArrayTridentHand.size;i++) {
            cardButtonArrayTridentHand.get(i).draw(spriteBatch,1.0f);

        }
    }
    public void setVisible(boolean visible) {
        /*i call the drawTriButtons method of this stage in DEALSTAGE, which will draw all
        * triButtons from this stage, but i don't want to draw the TRIDENTBUILDINGNEXTSTAGE
        * button, so i've set it to invisible, but here i will set it to visible again. it
        * will be set to invisible witht eh rest of the stage whenever we move to a stage
        * that is not this*/
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setVisible(visible);
        this.visible = visible;
    }
    public void createButtons(){
        /*when creating new buttons we pass in the enum for that button so the button can store it
         * so it can reference itself later. the add button method also needs this stage
         * to add the actor to the stage and our array of buttons so we can add it to that too
         * when adding to the array the method actually inserts it in the array at the enum.value index
         * this means if we add the buttons out of order it will cause an error, which is good because
         * then i can make sure the buttons are in the correct order*/
        /*trident buttons*/

        /*this is the player's trident hand*/
        createPlayerTridentHand();


        /*the auto build button, this will expand into more buttons*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setText("Auto\nBuild");


        /*the first set of 7 tributtons, will determine the pip value of the 3 cards selected*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).setText("Med\nSpear");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).setText("Low\nSpear");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED).setText("Med\nBident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW).setText("Low\nBident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*2)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI).setText("Hi\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setText("Med\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setText("Low\nTrident");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setVisible(false);


        /*the second set of 6 tributtons, will determine the suit of the 3 cards selected*/
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setText("Suit\nNature");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))+ 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT).setText("Suit\nLight");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON).setText("Suit\nDemon");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).setText("Suit\nDark");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).setText("Suit\nAny");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).setVisible(false);


        /*3rd set of buttons rotate, flip and confirm */
        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),true,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE).setText("Rotate");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE).setVisible(false);
        stageInterface.addTriButton(new TriButton(stageInterface,720-130-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).setText("Flip");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,720-(130*1.5f)-10,1280/2 - 130 * (float)(Math.sin(Math.PI/3))- 130 * (float)(Math.sin(Math.PI/3)),false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setText("Confirm");
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setVisible(false);

        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setText("Game");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGNEXTSTAGE).setTridentToTextSize();

        resetAutoBuild();

        /*card buttons, these are created in setup cards instead*/
        /*
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.LEFT,       StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING0),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.RIGHT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING1),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,300,200,true,CardButton.VERTICAL,   StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING2),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.LEFT,      StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING3),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.RIGHT,     StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING4),cardButtonArray,this);
        stageInterface.addCardButton(new CardButton(stageInterface,275,200,false,CardButton.VERTICAL,  StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Card.TRIDENTBUILDING5),cardButtonArray,this);

         */
}
/*the maximum number of tridents will be 8 in the players' trident hand,
* plus a space for the pre and post game cards*/
void createPlayerTridentHand(){
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY1),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY2),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY3),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY4),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY5),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY6),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY7),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY8),triButtonArray,this);
    stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.TRIDENTBUILDINGSTAGE, ButtonEnum.Tri.TRIDENTBUILDINGPLAYERTRIDENTARRAY9),triButtonArray,this);
    /*set all buttons to invisible*/
    resetPlayerTridentHand();
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
        triButtonArray.get(i).drawMirror=true;
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
        }
        /*this is for the card buttons that make up the trident array */
        for(int j=0;j<3;j++){

            if(i*3+j<OptionsStage.cardsEach){
                cardButtonArrayTridentHand.get(i*3+j).setVisible(true);
                cardButtonArrayTridentHand.get(i*3+j).edgeLength=w;
                cardButtonArrayTridentHand.get(i*3+j).updateBounds();
                cardButtonArrayTridentHand.get(i*3+j).setX(CardButton.dealAnimationRectangleDealX + w/2 * (i));
                cardButtonArrayTridentHand.get(i*3+j).setY(CardButton.dealAnimationRectangleHeight + CardButton.dealAnimationRectangleDealY-h*2);
                cardButtonArrayTridentHand.get(i*3+j).orientation=i%2==1?true:false;
                cardButtonArrayTridentHand.get(i*3+j).position=(byte)(j);
                /*if this is the pre and post game card move it right a bit */
                if(OptionsStage.preAndPostGameCard && i==OptionsStage.tridentsEach-1){
                    cardButtonArrayTridentHand.get(i*3+j).setX(CardButton.dealAnimationRectangleDealX + w/2 * (i)+10);
                }
            }
            /*this will be the undealt cards*/
            else if (i*3+j<cardButtonArrayTridentHand.size){
                cardButtonArrayTridentHand.get(i*3+j).setVisible(false);
                cardButtonArrayTridentHand.get(i*3+j).setX(-720);
                cardButtonArrayTridentHand.get(i*3+j).setY(-1280);
            }
        }


    }
}
    static void resetPlayerTridentHand() {
        for(int i = 0 ; i < triButtonArray.size;i++) {
            triButtonArray.get(i).setVisible(false);
            triButtonArray.get(i).drawMirror=false;
        }
    }
    static void resetAutoBuild(){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(true);
        setAutoBuildPipVisibility(false);
        setAutoBuildSuitVisibility(false);
        setAutoBuildRotFlipConfirmVisibility(false);
        for (int i =0; i< triButtonArray.size;i++){
            triButtonArray.get(i).updateBounds();
        }
    }
    static void setAutoBuildPipVisibility(boolean visible){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARMED).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSPEARLOW).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTMED). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGBIDENTLOW). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTHI). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTMED).setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGTRIDENTLOW).setVisible(visible);
    }
    static void setAutoBuildSuitVisibility(boolean visible){
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT0NATURE).setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT1LIGHT). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT2DEMON). setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT3DARK).  setVisible(visible);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGSUIT4ANY).   setVisible(visible);
    }

    static void setAutoBuildRotFlipConfirmVisibility(boolean visible) {
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDROTATE). setVisible(visible);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDFLIP).   setVisible(visible);
        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILDCONFIRM).setVisible(visible);
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
        if(touchHandled) {
            for (int i = 0; i < cardButtonArrayTridentHand.size; i++) {
                /*if the touch location is in this trihand card button then break the for loop and do the touch logic*/
                if (cardButtonArrayTridentHand.get(i).triangleHit(x, y)) {
                    cardButtonArrayTridentHand.get(i).touchLogic(x, y);
                    Gdx.app.log("tridentbuildingStage","queryTriButtonTouch,cardButtonArrayTridentHand.touchLogic");
                    break;
                }
            }
        }
    }

    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a cardButton's bounding box but not in it's triangle
     * this method will test all actors in the array if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryCardButtonTouch(float x, float y){
        /*for each cardButton in this stage*/
        boolean touchHandled=false;
        for(int i=0;i<cardButtonArray.size;i++) {
            /*if the touch location is in this cardButton's triangle then break the for loop and do the touch logic*/
            if(cardButtonArray.get(i).triangleHit(x,y)){
                cardButtonArray.get(i).touchLogic(x,y);
                touchHandled=true;
                Gdx.app.log("tridentbuildingStage","queryCardButtonTouch,cardButtonArray.touchLogic");
                break;
            }
        }
        if(touchHandled==false) {
            for (int i = 0; i < cardButtonArrayTridentHand.size; i++) {
                /*if the touch location is in this cardButton's triangle then break the for loop and do the touch logic*/
                if (cardButtonArrayTridentHand.get(i).triangleHit(x, y)) {
                    cardButtonArrayTridentHand.get(i).touchLogic(x, y);
                    touchHandled = true;
                    Gdx.app.log("tridentbuildingStage","queryCardButtonTouch,cardButtonArrayTridentHand.touchLogic");
                    break;
                }
            }
        }
        /*if the touch wasn't in any of the cards in the card array, check the tributtons in the triarray*/
        if(touchHandled==false){
            queryTriButtonTouch(x,y);
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

            case TRIDENTBUILDINGNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.GAMESTAGE);
                break;
            }
            case TRIDENTBUILDINGAUTOBUILD: {
                /*if there is a free trident available in the hand */
                if(tridentHighlightPos>-1){
                    autoBuildOpen=true;
                    /*reset these cardIndex variables */
                    cardIndex1=null;
                    cardIndex2=null;
                    cardIndex3=null;
                    /*the autobuild buttons will expand the auto build trident buttons*/
                    stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(false);
                    setAutoBuildPipVisibility(true);
                    /*highlight the start of the first empty trident */
                    highlightPos=tridentHighlightPos;

                }

                break;
            }
            case TRIDENTBUILDINGSPEARMED: {
                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPHI;
                autoBuildSelectedPip2 = PIPMED;
                autoBuildSelectedPip3 = PIPMED;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);

                break;
            }
            case TRIDENTBUILDINGSPEARLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);

                autoBuildSelectedPip1 = PIPHI;
                autoBuildSelectedPip2 = PIPLOW;
                autoBuildSelectedPip3 = PIPLOW;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGBIDENTMED: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPMED;
                autoBuildSelectedPip2 = PIPHI;
                autoBuildSelectedPip3 = PIPHI;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGBIDENTLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPLOW;
                autoBuildSelectedPip2 = PIPHI;
                autoBuildSelectedPip3 = PIPHI;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGTRIDENTHI: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPHI;
                autoBuildSelectedPip2 = PIPHI;
                autoBuildSelectedPip3 = PIPHI;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGTRIDENTMED: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPMED;
                autoBuildSelectedPip2 = PIPMED;
                autoBuildSelectedPip3 = PIPMED;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGTRIDENTLOW: {

                setAutoBuildSuitVisibility(true);
                setAutoBuildPipVisibility(false);
                autoBuildSelectedPip1 = PIPLOW;
                autoBuildSelectedPip2 = PIPLOW;
                autoBuildSelectedPip3 = PIPLOW;
                findPip(SUITNONE,SUITNONE,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);
                break;
            }
            case TRIDENTBUILDINGSUIT0NATURE: {
                autoBuildSelectedSuit1=SUITNATURE;
                clickSuit();
                break;
            }
            case TRIDENTBUILDINGSUIT1LIGHT: {
                autoBuildSelectedSuit1=SUITLIGHT;
                clickSuit();
                break;
            }
            case TRIDENTBUILDINGSUIT2DEMON: {
                autoBuildSelectedSuit1=SUITDEMON;
                clickSuit();
                break;
            }
            case TRIDENTBUILDINGSUIT3DARK: {
                autoBuildSelectedSuit1=SUITDARK;
                clickSuit();
                break;
            }
            case TRIDENTBUILDINGSUIT4ANY: {
                autoBuildSelectedSuit1=SUITNONE;
                autoBuildSelectedSuit2=SUITNONE;
                clickSuit();
                break;
            }
            case TRIDENTBUILDINGAUTOBUILDROTATE: {
                break;
            }
            case TRIDENTBUILDINGAUTOBUILDFLIP: {

                break;
            }
            case TRIDENTBUILDINGAUTOBUILDCONFIRM: {
                autoBuildOpen=false;
                stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setVisible(true);
                setAutoBuildRotFlipConfirmVisibility(false);
                break;
            }
                default:
                Gdx.app.log("TRIDENTBUILDINGSTAGE", "DEFAULT "+triButtonIndex);
                //throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }

    /**
     * just a helper method because all autobuild suit buttons do a similar thing
     */
    public void clickSuit(){
        /*save the card indexes in another variable as they are about to be set again
         * but i'm not finished with these values yet, these are the cards that have just been set
         * in the pip autobuild*/
        cardIndex1a=cardIndex1;
        cardIndex2a=cardIndex2;
        cardIndex3a=cardIndex3;
        /*this will move the 3 cards we just made when clicking the pip autobuild back to the card hand
        * we need to do this before calling findpip, so that the suit hand has all it's cards back before
        * calling findpip*/
        touchCard(cardIndex3);
        touchCard(cardIndex2);
        touchCard(cardIndex1);
        /**/
        findPip(autoBuildSelectedSuit1,autoBuildSelectedSuit2,autoBuildSelectedPip1,autoBuildSelectedPip2,autoBuildSelectedPip3);
        /*findPip() will have set the cardIndex1 2 and 3 and haveViableCdrds, if the suit has viable
        * cards then add them to the tri hand, otherwise add the 3 cards we just removed back
        * into the tri hand */

        /*only move to the next stage if clicking this button was actually viable -
         * meaning the suit has at least 3 cards to make a trident with*/
        if (haveViableCards) {

            setAutoBuildRotFlipConfirmVisibility(true);
            setAutoBuildSuitVisibility(false);
            /*this will add 3 cards of this suit to the trident hand*/
            touchCard(cardIndex1);
            touchCard(cardIndex2);
            touchCard(cardIndex3);
        }
        else{
            cardIndex1=cardIndex1a;
            cardIndex2=cardIndex2a;
            cardIndex3=cardIndex3a;
            touchCard(cardIndex1);
            touchCard(cardIndex2);
            touchCard(cardIndex3);
        }
    }
    /**
     * when autobuild button is clicked the autoBuildOpen boolean is set to true, if a card is clicked
     * when the autoBuildOpen is true then set it to false and cancel the autobuild
     */
    public void cancelAutoBuild(){
        if (autoBuildOpen) {
            autoBuildOpen = false;
            /*if the cardindex 1 2 and 3  . cardButtonIndex is not null then that means we have just added
             * these cards to the tri hand array, if we are cancelling the click them again to put them back where
             * they came from */
            if (cardIndex1 != null && cardIndex2 != null && cardIndex3 != null) {
                touchCard(cardIndex1);
                touchCard(cardIndex2);
                touchCard(cardIndex3);

                cardIndex1 = null;
                cardIndex2 = null;
                cardIndex3 = null;
            }
            resetAutoBuild();
        }
    }
    /**
     * this will be called in the card class, if a card button has actually been clicked
     * @param cardButtonIndex this will be the index of the cardbutton that was clicked, the index is set on creation of the
     *                       cardButton and will be the same as it's index in the cardButtonArray for this stage
     */
    public void touchLogicCard(ButtonEnum.Card cardButtonIndex){
        Gdx.app.log("TRIDENTBUILDINGSTAGE"," card button array clicked, button "+cardButtonIndex);
         Gdx.app.log("TRIDENTBUILDINGSTAGE","x "+(int)stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).getX()+ " y "+(int)stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).getY()+" enum" +cardButtonIndex+"/ "+stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).cardButtonIndex+" player index "+ MyServer.player.index+" card index "+ stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).playerIndex+ " edgelength "+stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).edgeLength+ " visible "+stageInterface.getCardButtonTridentBuildingStage(cardButtonArray,cardButtonIndex).isVisible());

        /*regardless of which card was clicked, if we are in the middle of an autoBuild we need to cancel it*/
        cancelAutoBuild();

        switch(cardButtonIndex) {

            case TRIHANDCARD0:
            case TRIHANDCARD1:
            case TRIHANDCARD2:
            case TRIHANDCARD3:
            case TRIHANDCARD4:
            case TRIHANDCARD5:
            case TRIHANDCARD6:
            case TRIHANDCARD7:
            case TRIHANDCARD8:
            case TRIHANDCARD9:
            case TRIHANDCARD10:
            case TRIHANDCARD11:
            case TRIHANDCARD12:
            case TRIHANDCARD13:
            case TRIHANDCARD14:
            case TRIHANDCARD15:
            case TRIHANDCARD16:
            case TRIHANDCARD17:
            case TRIHANDCARD18:
            case TRIHANDCARD19:
            case TRIHANDCARD20:
            case TRIHANDCARD21:
            case TRIHANDCARD22:
            case TRIHANDCARD23:
            case TRIHANDCARD24:
            case TRIHANDCARD25:
             {
                touchTriHandCardButton(cardButtonIndex);
                break;
            }
            case TRIDENTBUILDING0:
            case TRIDENTBUILDING1:
            case TRIDENTBUILDING2:
            case TRIDENTBUILDING3:
            case TRIDENTBUILDING4:
            case TRIDENTBUILDING5:
            case TRIDENTBUILDING6:
            case TRIDENTBUILDING7:
            case TRIDENTBUILDING8:
            case TRIDENTBUILDING9:
            case TRIDENTBUILDING10:
            case TRIDENTBUILDING11:
            case TRIDENTBUILDING12:
            case TRIDENTBUILDING13:
            case TRIDENTBUILDING14:
            case TRIDENTBUILDING15:
            case TRIDENTBUILDING16:
            case TRIDENTBUILDING17:
            case TRIDENTBUILDING18:
            case TRIDENTBUILDING19:
            case TRIDENTBUILDING20:
            case TRIDENTBUILDING21:
            case TRIDENTBUILDING22:
            case TRIDENTBUILDING23:
            case TRIDENTBUILDING24:
            case TRIDENTBUILDING25:
            case TRIDENTBUILDING26:
            case TRIDENTBUILDING27:
            case TRIDENTBUILDING28:
            case TRIDENTBUILDING29:
            case TRIDENTBUILDING30:
            case TRIDENTBUILDING31:
            case TRIDENTBUILDING32:
            case TRIDENTBUILDING33:
            case TRIDENTBUILDING34:
            case TRIDENTBUILDING35:
            case TRIDENTBUILDING36:
            case TRIDENTBUILDING37:
            case TRIDENTBUILDING38:
            case TRIDENTBUILDING39:
            case TRIDENTBUILDING40:
            case TRIDENTBUILDING41:
            case TRIDENTBUILDING42:
            case TRIDENTBUILDING43:
            case TRIDENTBUILDING44:
            case TRIDENTBUILDING45:
            case TRIDENTBUILDING46:
            case TRIDENTBUILDING47:
            case TRIDENTBUILDING48:
            case TRIDENTBUILDING49:
            case TRIDENTBUILDING50:
            case TRIDENTBUILDING51:
            {
                Gdx.app.log("TridentBuildingStage","touch playing card "+cardButtonIndex);
                touchCard(cardButtonIndex);
                break;
            }
        }


    }

    /**
     * called in the trident building stage when the player touches a card in the
     * trident hand
     */
    public void touchTriHandCardButton(ButtonEnum.Card cardButtonEnum){
        int index = cardButtonEnum.value-ButtonEnum.Card.TRIHANDCARD0.value;
        /*if the position in the trihand has not been filled, highlight it */
        if(triHandCardFilledArray.get(index)==false){
            highlightPos=index;
        }
    }

    /**
     * called in the trident building stage when the player touches one of the cards in the
     * cardhand, this will move the card to the position defined by the highlighted card in the
     * trihand
     */
    public void touchCard(ButtonEnum.Card cardButtonEnum) {
        if (cardButtonEnum!=null) {
            int index = cardButtonEnum.value;
            /*if the card has not already been moved, move it  */
            if (cardButtonArray.get(index).inTriHand == false) {
                cardButtonArray.get(index).inTriHand = true;

                cardButtonArray.get(index).setXPos(cardButtonArrayTridentHand.get(highlightPos).getX());
                cardButtonArray.get(index).setYPos(cardButtonArrayTridentHand.get(highlightPos).getY());
                cardButtonArray.get(index).oldOrientation = cardButtonArray.get(index).orientation;
                cardButtonArray.get(index).oldPosition = cardButtonArray.get(index).position;
                cardButtonArray.get(index).orientation = cardButtonArrayTridentHand.get(highlightPos).orientation;
                cardButtonArray.get(index).position = cardButtonArrayTridentHand.get(highlightPos).position;
                cardButtonArray.get(index).highlightPos = highlightPos;
                triHandCardFilledArray.set(highlightPos, true);
                cardButtonArrayTridentHand.get(highlightPos).setVisible(false);
                findNextEmptyTrident();
                /**
                 * because we just filled this highlight position, advance the position
                 */
                advanceHighlightPos();
            }
            /*if the card is already in the tri hand array, move it back to the cardhand array */
            else {
                cardButtonArray.get(index).resetPos();
                cardButtonArray.get(index).inTriHand = false;
                highlightPos = cardButtonArray.get(index).highlightPos;
                triHandCardFilledArray.set(highlightPos, false);
                cardButtonArrayTridentHand.get(highlightPos).setVisible(true);
                findNextEmptyTrident();
                highlightPosCounter = 0;
            }
        }
    }
    public void advanceHighlightPos(){
        highlightPos++;
        if (highlightPos>=OptionsStage.cardsEach){
            highlightPos=0;
        }
        highlightPosCounter++;
        /*if the highlight counter is higher than the amount of cards then we must have
        * filled all the spaces, so set the highlight to -1 so none are highlighted*/
        if(highlightPosCounter>=OptionsStage.cardsEach){
            highlightPos= -1;
        }
        /*if this new position is filled, try the next one */
        else if (triHandCardFilledArray.get(highlightPos) && highlightPosCounter<OptionsStage.cardsEach){
            advanceHighlightPos();
        }
        else if (triHandCardFilledArray.get(highlightPos)==false){
            highlightPosCounter=0;
        }

    }

    /**this method must be called everytime we modify the trihand
     * the autobuild methods will create a full 3 card trident, but only if there is space in the trident hand
     * when amending the trident hand i should check where the next free trident starts
     */
    public void findNextEmptyTrident(){
        /*starting at the start of the trident containing the highlight position, for every 3rd card,
         which will be the first card in each trident, excluding the pre and post game cards*/
        boolean foundEmptyTrident=false;
        for (int i=0;i<OptionsStage.nonPrePostGameCardsEach;i=i+3){
            int index = ((int)Math.floor(highlightPos/3f)*3+i);
            /*basically this says if the index is pointing to the pre and post game cards, then skip this one*/
            if(index>OptionsStage.nonPrePostGameCardsEach && index <OptionsStage.cardsEach){
            }
            /* if the index is under nonPrePostGameCardsEach then we are looking the the hand array,
             * if index is over nonPrePostGameCardsEach then we have looked at all the values
             * between the highlightPos and the end of the array, but we need to mod the index
             * to search between the start of the array and the highlightPos, */
            else {
                /*if the index exceeds the size of the hand array then mod it,
                 * this can happen because we aren't starting the index at 0, it starts at the highlight position*/
                index = index % triHandCardFilledArray.size;
                /*if the card at this index is not filled, check the next 2 indexes
                 * if there are any, to see if the
                 * entire trident is not filled, if all 3 are empty, this index position is
                 * our next empty trident, break the for loop*/
                if(index+2<OptionsStage.nonPrePostGameCardsEach){
                    if (triHandCardFilledArray.get(index) == false && triHandCardFilledArray.get(index + 1) == false && triHandCardFilledArray.get(index + 2) == false) {
                        foundEmptyTrident = true;
                        tridentHighlightPos = index;
                        stageInterface.getTriButton(triButtonArray, ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setText("Auto\nBuild");
                        break;
                    }
                }
            }

        }
        /*after the for loop, if we didn't find an empty trident make sure
        tridentHighlightPos is -1* */
        if(foundEmptyTrident==false){
            tridentHighlightPos=-1;
            stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.TRIDENTBUILDINGAUTOBUILD).setText("Hand\nFull");
        }
        Gdx.app.log("TridentBuildingStage","tridentHighlightPos"+tridentHighlightPos);
    }
    /**
     * to be used in the auto build methods, this will find the index of a card in the hand
     * array that has not already been used, that belongs to this player
     * that belongs to one of the specified suits (if specified)
     * that has the pip value closest to that specified
     * @param suit1 options are SUITNATURE,SUITLIGHT,SUITDEMON,SUITDARK or SUITNONE
     * @param suit2 options are SUITNATURE,SUITLIGHT,SUITDEMON,SUITDARK or SUITNONE
     * @param targetValue1 options are PIPHIGH, PIPMED or PIPLOW
     * @param targetValue2 options are PIPHIGH, PIPMED or PIPLOW
     * @param targetValue3 options are PIPHIGH, PIPMED or PIPLOW
     */
    public void findPip(int suit1, int suit2, int targetValue1, int targetValue2, int targetValue3){
        /*we will find all viable cards and add their pip values to this array*/
        Array<CardButton> tempArrayCard =new Array<>();
        Array<Integer> tempArrayPip =new Array<>();
        haveViableCards=true;

        for(int i=0;i<tempArrayCard.size;i++) {
            Gdx.app.log("TridentBuildingStage", "tempArrayCard pip " + tempArrayCard.get(i).getPip() + " tempArrayCard suit " + tempArrayCard.get(i).getSuit());
            Gdx.app.log("TridentBuildingStage", "tempArrayPip pip " + tempArrayPip.get(i));
        }
        tempArrayPip.clear();
        tempArrayCard.clear();
        Gdx.app.log("TridentBuildingStage", "cleared tempArrayCard ");

        for(int i=0;i<tempArrayCard.size;i++) {
            Gdx.app.log("TridentBuildingStage", "cleared tempArrayCard pip " + tempArrayCard.get(i).getPip() + " tempArrayCard suit " + tempArrayCard.get(i).getSuit());
            Gdx.app.log("TridentBuildingStage", "cleared tempArrayPip pip " + tempArrayPip.get(i));
        }
        if (suit1==SUITNONE && suit2 ==SUITNONE){
            /*no suit specified select all cards, that belong to this player and are not already in the tri hand  */
            for(int i=0;i<cardButtonArray.size;i++){
                if (cardButtonArray.get(i).inTriHand==false && cardButtonArray.get(i).playerIndex == MyServer.player.index){
                    tempArrayPip.add((int) cardButtonArray.get(i).getPip());
                    tempArrayCard.add( cardButtonArray.get(i));
                    Gdx.app.log("TridentBuildingStage","add cards: suit"+cardButtonArray.get(i).getSuit()+" pip "+cardButtonArray.get(i).getPip());

                }
            }
        }
        /*else if one or both of the suits have been specified*/
        else{
            if(suit1 != SUITNONE){
                for(int i=0;i<cardButtonArray.size;i++){
                    if (cardButtonArray.get(i).inTriHand==false && cardButtonArray.get(i).playerIndex == MyServer.player.index && cardButtonArray.get(i).getSuit()==suit1){
                        tempArrayPip.add((int) cardButtonArray.get(i).getPip());
                        tempArrayCard.add( cardButtonArray.get(i));
                        Gdx.app.log("TridentBuildingStage","add cards: suit "+suit1+" pip "+cardButtonArray.get(i).getPip());
                    }
                }
            }
            if(suit2 != SUITNONE){
                for(int i=0;i<cardButtonArray.size;i++){
                    if (cardButtonArray.get(i).inTriHand==false && cardButtonArray.get(i).playerIndex == MyServer.player.index && cardButtonArray.get(i).getSuit()==suit2){
                        tempArrayPip.add((int) cardButtonArray.get(i).getPip());
                        tempArrayCard.add( cardButtonArray.get(i));
                    }
                }
            }
        }
        for(int i=0;i<tempArrayCard.size;i++) {
            Gdx.app.log("TridentBuildingStage", "tempArrayCard pip " + tempArrayCard.get(i).getPip() + " tempArrayCard suit " + tempArrayCard.get(i).getSuit());
            Gdx.app.log("TridentBuildingStage", "tempArrayPip pip " + tempArrayPip.get(i));
        }
        /*now our temp array should have all the cards that are candidates for our autobuild trident*/
        /*we need at least 3 cards to proceed*/
        if(tempArrayPip.size>2){
            /*sort the array */
            tempArrayPip.sort();
            /*find a card in the tempArrayCard that has a pip value equal to the highest pip value
            * in tempArrayPip*/
            if (targetValue1==PIPHI){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(tempArrayPip.size-1)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex1 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex1 "+cardIndex1);
                        tempArrayPip.removeIndex(tempArrayPip.size-1);
                        break;
                    }
                }
            }
            else if (targetValue1==PIPMED){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get((int)Math.floor(tempArrayPip.size/2))){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex1 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex1 "+cardIndex1);
                        tempArrayPip.removeIndex((int)Math.floor(tempArrayPip.size/2));
                        break;
                    }
                }
            }
            else if (targetValue1==PIPLOW){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(0)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex1 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex1 ");
                        tempArrayPip.removeIndex(0);
                        break;
                    }
                }
            }
            if (targetValue2==PIPHI){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(tempArrayPip.size-1)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex2 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex2 "+cardIndex2);
                        tempArrayPip.removeIndex((tempArrayPip.size-1));
                        break;
                    }
                }
            }
            else if (targetValue2==PIPMED){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get((int)Math.floor(tempArrayPip.size/2))){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex2 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex2 "+cardIndex2);
                        tempArrayPip.removeIndex((int)Math.floor(tempArrayPip.size/2));
                        break;
                    }
                }
            }
            else if (targetValue2==PIPLOW){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(0)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex2 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex2 "+cardIndex2);
                        tempArrayPip.removeIndex(0);
                        break;
                    }
                }
            }
            if (targetValue3==PIPHI){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(tempArrayPip.size-1)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex3 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex3 "+cardIndex3);
                        tempArrayPip.removeIndex(tempArrayPip.size-1);
                        break;
                    }
                }
            }
            else if (targetValue3==PIPMED){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get((int)Math.floor(tempArrayPip.size/2))){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex3 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex3 "+cardIndex3);
                        tempArrayPip.removeIndex((int)Math.floor(tempArrayPip.size/2));
                        break;
                    }
                }
            }
            else if (targetValue3==PIPLOW){
                for(int i =0; i<tempArrayCard.size;i++)
                {
                    if(tempArrayCard.get(i).getPip()==tempArrayPip.get(0)){
                        Gdx.app.log("TridentBuildingStage","suit "+tempArrayCard.get(i).getSuit()+" pip "+tempArrayCard.get(i).getPip());
                        cardIndex3 = tempArrayCard.removeIndex(i).cardButtonIndex;
                        Gdx.app.log("TridentBuildingStage","cardindex3 "+cardIndex3);
                        tempArrayPip.removeIndex(0);
                        break;
                    }
                }
            }
            Gdx.app.log("TridentBuildingStage","findPip() results: "+cardIndex1+", "+cardIndex2+", "+cardIndex3);

        }
        /*if there are not enough cards, then reset the cardIndexes*/
        else{
            haveViableCards=false;
        }
    }

    public void reset(){
        cardButtonArray.clear();
        setUpCards();
        setUpCardsTridentHand();
        /*make all the trident buttons invisible*/
        resetPlayerTridentHand();
        resetAutoBuild();
        highlightPosCounter=0;
        highlightPos=0;
    }
    /*set up all the cards, they should be given an enum each,
    some of the initial values don't matter much since they will be set
    when allocated to one of the players */
    public void setUpCards() {
        /*set up all cards*/
        int i=0;
        for (ButtonEnum.Card cardEnum : ButtonEnum.Card.values()) {
            System.out.println(cardEnum);
            stageInterface.addCardButton(new CardButton(stageInterface, 0, 0, true, (byte) 0, stageInterface.TRIDENTBUILDINGSTAGE, cardEnum), cardButtonArray, this);
            i++;
            if(i>51){
                break;
            }
        }
        Gdx.app.log("TridentBuildingStage","cardButtonArray size :"+cardButtonArray.size);
        /*i need these to be not null, just set them here, they will be reset later anyway*/
        cardIndex1=cardButtonArray.get(0).cardButtonIndex;
        cardIndex2=cardIndex1;
        cardIndex3=cardIndex1;
        cardIndex1a=cardIndex1;
        cardIndex2a=cardIndex1;
        cardIndex3a=cardIndex1;
    }
    /*set up all the cards in the tridenthand, they should be given an enum each,
    pos and orientation should be set up later
     the value won't be set, although a card with a value may be moved so that it is on top of this */
    public void setUpCardsTridentHand() {
        /*set up all cards*/
        cardButtonArrayTridentHand.clear();
        triHandCardFilledArray.clear();
        int i=0;
        /*for every enum of which the first 52 relate to the 52 cards, the next 26 relate to the trident hand card buttons */
        for (ButtonEnum.Card cardEnum : ButtonEnum.Card.values()) {
            if(i<52){
                i++;
            }
            else {
                System.out.println(cardEnum);
                stageInterface.addCardButton(new CardButton(stageInterface, 0, 0, true, (byte) 0, stageInterface.TRIDENTBUILDINGSTAGE, cardEnum), cardButtonArrayTridentHand, this,cardEnum.value-ButtonEnum.Card.TRIHANDCARD0.value);
                triHandCardFilledArray.add(false);
                i++;
                if (i > 52 + 26) {
                    break;
                }
            }
        }
        Gdx.app.log("TridentBuildingStage","cardButtonArray size :"+cardButtonArray.size);
    }
    public void amendCardsForTridentBuildingStage() {


    }

    /**
     *
     *  this method is designed for use in the del stage, the deal stage will need methods to get teh lowest, second lowest etc
     *  card deal to a specified player, these methods are just for clarity and convenience
     *  the cards are dealt to the player in order lowest to highest, but i'm after the highest pip
     *  for example king of suit 1 is index 12, ace of suit 2 is index 13, king is higher pip but lower index
     *
     * @param player which player's card are we getting, 0, 1 or 2 (2 for 3 player game only)
     * @param nthLowestCard 0 will get the lowest card, OptionsStage.cardsEach-1 will get the highest
     * @return returns the index position of the card in teh cardButtonArray
     */
    public static int getNthLowestCard(int player,int nthLowestCard){
        Array<Integer> tempArray = new Array<>();
        tempArray.clear();
        /*add all the pip values of this player's cards to this tempArray*/
        for (int i = player*OptionsStage.cardsEach; i < player*OptionsStage.cardsEach+OptionsStage.cardsEach; i++) {
            /*don't loop through the cardButtonArray in a very safe way,
            * if the player variable above is set to OptionsStage.numberOfPlayers, we can go index out of bounds
            * but we can just stop before we get to that point and everything will work as intended*/
            if(i>=TridentBuildingStage.cardButtonArray.size){
                break;
            }
            tempArray.add((int)TridentBuildingStage.cardButtonArray.get(i).getPip());
        }
        /*sort the array lowest pip value to highest pip value*/
        tempArray.sort();
        /*now in the card button array find the card index position that has the nthLowest pip value */
        int i;
        for ( i = player*OptionsStage.cardsEach; i < player*OptionsStage.cardsEach+OptionsStage.cardsEach; i++) {
            if(TridentBuildingStage.cardButtonArray.get(i).getPip() == tempArray.get(nthLowestCard)){
                break;
            }
        }
        return i;
    }

    /**
     * easiest way to swap cards between players is to swap the values, and the playerindex
     * then sort it again
     * @param cardIndex1
     * @param cardIndex2
     */
    public static void swapCards(int cardIndex1, int cardIndex2){
        /*swap the value and player index of the 2 cards*/
        int value1 = cardButtonArray.get(cardIndex1).value;
        cardButtonArray.get(cardIndex1).setValue(cardButtonArray.get(cardIndex2).value);
        cardButtonArray.get(cardIndex2).setValue(value1);

        /*now we need to sort the array again*/

        Array<Integer> tempArray = new Array<>();
        for(int j=0;j<OptionsStage.numberOfPlayers;j++) {
            tempArray.clear();
            for (int i = 0; i < OptionsStage.cardsEach; i++) {
                tempArray.add(TridentBuildingStage.cardButtonArray.get(j*OptionsStage.cardsEach+i).value);
            }
            tempArray.sort();
            for (int k =0; k < tempArray.size;k++){
                TridentBuildingStage.cardButtonArray.get(j*OptionsStage.cardsEach+k).setValue(tempArray.get(k));
            }
        }
    }

}
