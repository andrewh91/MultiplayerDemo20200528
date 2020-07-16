package com.gmail.andrewahughes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.gmail.andrewahughes.MyGdxGame.WORLDHEIGHT;
import static com.gmail.andrewahughes.MyGdxGame.WORLDWIDTH;

public class GameStage extends Stage {

    boolean visible =false;
    static StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();

    static Array<CardButton> cardButtonArray = new Array<CardButton>();

    static int highlightPosGameboard=-1;
    static int highlightPosTriHand =-1;

    public GameStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        this.stageInterface =stageInterface;

        this.shapeRenderer = shapeRenderer;
        this.setViewport(viewport);
        this.spriteBatch = batch;
        for (int i = 0; i < OptionsStage.gameBoardSize; i++) {
            cardButtonArray.add(        new CardButton(stageInterface,0,0,true,CardButton.VERTICAL,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25),
                    new CardButton(stageInterface,0,0,true,CardButton.LEFT,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25),
                    new CardButton(stageInterface,0,0,true,CardButton.RIGHT,MyGdxGame.GAMESTAGE,ButtonEnum.Card.TRIHANDCARD25));

        }

        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);
        createButtons();
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {

            this.getViewport().apply();
            Gdx.gl.glClearColor(0.5f, 0.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            /*draw all actors of this stage*/
            drawTriButtons();
            spriteBatch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            /*draw a box around the screen 1280 by 720 WORLDWIDTH, WORLDHEIGHT*/
            shapeRenderer.line(1    ,1      ,719    ,1);
            shapeRenderer.line(719  ,1      ,719    ,1279);
            shapeRenderer.line(719  ,1279   ,1      ,1279);
            shapeRenderer.line(1    ,1279   ,1      ,1);
            /*draw all actors of this stage*/
            drawTriButtonsShape();
            drawHighlightShape();
            shapeRenderer.end();
        }
    }
    void drawTriButtons() {

        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).draw(spriteBatch,1.0f);

        }
    }

    /**
     * draw the trident buttons shape - which should just be it's bounds
     */
    void drawTriButtonsShape() {
        for(int i=0;i<triButtonArray.size;i++) {
            triButtonArray.get(i).drawShape(shapeRenderer);
        }
    }
    void drawHighlightShape() {
        if (highlightPosGameboard>-1) {
            shapeRenderer.setColor(Color.RED);
            triButtonArray.get(highlightPosGameboard).drawShape(shapeRenderer);
            /*use the adjacent variables of the highlighted trident to figure out which trident's
             * need to be highlighted, we will only highlight one card of the adjacent tridents,
             * the trident adjacent to the vertical position will have it's vertical card highlighted, vert - vert
             * but generally the triden tadjacnet to the left position will have it's right card highlighted so left - right
             * and vice versa for the right one - right - left
             * UNLESS the adjacent trident is the same orientation as the current one, in which case  we do left -left, or right - right
             * this will only happen to tridents of the edge, where we've been creative in which one is adjacent */
            boolean sameOrientationLeft = triButtonArray.get(highlightPosGameboard).orientation==triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).orientation;
            boolean sameOrientationRight = triButtonArray.get(highlightPosGameboard).orientation==triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).orientation;
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexVertical).cardButtonArray.get(0).drawHighlightShape(shapeRenderer);
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexLeft).cardButtonArray.get(sameOrientationLeft?2:1).drawHighlightShape(shapeRenderer);
            triButtonArray.get(triButtonArray.get(highlightPosGameboard).adjacentIndexRight).cardButtonArray.get(sameOrientationRight?1:2).drawHighlightShape(shapeRenderer);
            shapeRenderer.setColor(Color.WHITE);
        }
        if (highlightPosTriHand>-1) {
            shapeRenderer.setColor(Color.RED);
            triButtonArray.get(highlightPosTriHand).drawShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(0).drawHighlightShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(1).drawHighlightShape(shapeRenderer);
            triButtonArray.get(highlightPosTriHand).cardButtonArray.get(2).drawHighlightShape(shapeRenderer);
            shapeRenderer.setColor(Color.WHITE);
        }
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void createButtons() {
        /*when creating new buttons we pass in the enum for that button so the button can store it
         * so it can reference itself later. the add button method also needs this stage
         * to add the actor to the stage and our array of buttons so we can add it to that too
         * when adding to the array the method actually inserts it in the array at the enum.value index
         * this means if we add the buttons out of order it will cause an error, which is good because
         * then i can make sure the buttons are in the correct order*/
        createGameBoard();
        createTridentHand();

        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMENEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setText("Game\nOver");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.GAMENEXTSTAGE).setTridentToTextSize();
    }
    public void createGameBoard(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD0),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD3),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD4),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD5),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD6),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD7),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD8),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD9),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD10),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD11),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD12),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD13),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD14),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMEBOARD15),triButtonArray,this);

    }

    /**
     * there will be a max of 8 tridents in the trident array, plus 2 extras for pre and post game card
     */
    public void createTridentHand(){
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND0),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND1),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND2),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND3),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND4),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND5),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND6),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIHAND7),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPREGAMECARD),triButtonArray,this);
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.GAMESTAGE, ButtonEnum.Tri.GAMETRIPOSTGAMECARD),triButtonArray,this);

    }
    /**
     * this will be called in the tributton class, the arguments will be the coordinates relevant to the world
     * not the actor. this will be called if a touch is in a triButton's bounding box but not in it's triangle
     * this method will test all actors on the stage if the touch location hits them
     * @param x this will be the real world x touch position,
     * @param y real world y touch position
     */
    public static void queryTriButtonTouch(float x, float y){

        for(int i=0;i<triButtonArray.size;i++) {
            /*if the touch location is in this triButton's triangle then break the for loop and do the touch logic*/
            if(triButtonArray.get(i).triangleHit(x,y)){
                triButtonArray.get(i).touchLogic(x,y);
                break;
            }


        }
    }
    /**
     * called when moving to the game stage, which should only happen when we have successfully set up
    * trident hand */
    public static void setUp()
    {

        for (int i = 0;i<OptionsStage.nonPrePostGameTridentsEach;i++){
            /*add 3 cards from the trident building stage's cardButtonArrayTridentHand to this tributton
            * note that the first TriButton in this tributtonarray is the button to go to the next stage,
            * so exclude that, then the next 16 buttons are gameboard buttons, exclude them too */
            triButtonArray.get(i + ButtonEnum.Tri.GAMETRIHAND0.value).setUpCardButtons(TridentBuildingStage.getCardAtHighlightPos(i *3),TridentBuildingStage.getCardAtHighlightPos(i *3+1),TridentBuildingStage.getCardAtHighlightPos(i *3+2));
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).updatePos(TridentBuildingStage.getCardAtHighlightPos(i *3).getX(),TridentBuildingStage.getCardAtHighlightPos(i *3).getY(),TridentBuildingStage.getCardAtHighlightPos(i *3).orientation);
            triButtonArray.get(i+ ButtonEnum.Tri.GAMETRIHAND0.value).cardsVisible=true;

        }
        /*if using pre and post game cards save them to a trident each too */
        if (OptionsStage.preAndPostGameCard){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).setPreGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3));
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).setPostGameCard(TridentBuildingStage.cardButtonArray.get(OptionsStage.nonPrePostGameTridentsEach*3+1));
        }

        setUpGameBoard();
        setUpGameBoard();


    }

    /**
     * 16 triButtons will have been created, need to set their position and orientation here
     * there is a static variable of CardButton that should have already been set, this variable
     * dealAnimationRectangleHeight gives us the size of the space we have to work with, screen size minus
     * a bit of a margin on the edge of the screen. the game board might be set up differently depending
     * on some of the OptionsStage variables
     */
    public static void setUpGameBoard(){
        Array<Integer> rows = new Array<>();
        rows.clear();
        int total = 0;
        /*this will fill  the row array with a number of items equal to the number of rows we need
        * and each item in the array will hold the number of tridents in that row*/
        for (int i = 0; total < OptionsStage.gameBoardSize; i=i+2) {
            rows.add(i+1);
            total = total + i+1;
        }
        //float edgeLength=(720-CardButton.dealAnimationRowMargin)/(float)(Math.ceil(rows.get(rows.size-1)/2f));
        float edgeLength = CardButton.edgeLength;
        float originX=720/2f  -(edgeLength*(float)(Math.ceil(rows.get(rows.size-1)/2f)))/2;
        float originY=1280/2f - CardButton.dealAnimationRectangleHeight/2 ;
        float incrementX=edgeLength/2;
        float incrementY=(float)(edgeLength*Math.sin(Math.PI/3));
        int index=0;
        /*for each row...*/
        for (int i = 0; i < rows.size; i++) {
            /*for each trident on the row
            rows.get(i) will equal 1, 3 ,5 , 7 etc - that is the number of tridents on the row*/
            for (int j = 0; j < rows.get(i); j++) {
                getGB(index).setX(originX+ incrementX * (rows.get(rows.size-1)-j - (rows.size-i)) );
                getGB(index).setY(originY+ incrementY * (rows.size-i) );
                //getGB(index).edgeLength=edgeLength;
                getGB(index).updateBounds();
                /*even numbered tridents of any row will be point up*/
                getGB(index).orientation= j%2==0;
                getGB(index).setUpCardButtons(cardButtonArray.get(index*3),cardButtonArray.get(index*3+1),cardButtonArray.get(index*3+2));
                getGB(index).cardButtonArray.get(0).orientation=j%2==0;
                getGB(index).cardButtonArray.get(1).orientation=j%2==0;
                getGB(index).cardButtonArray.get(2).orientation=j%2==0;
                getGB(index).cardButtonArray.get(0).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(0).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(0).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(0).updateBounds();
                getGB(index).cardButtonArray.get(1).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(1).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(1).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(1).updateBounds();
                getGB(index).cardButtonArray.get(2).setX(getGB(index).getX());
                getGB(index).cardButtonArray.get(2).setY(getGB(index).getY());
                //getGB(index).cardButtonArray.get(2).edgeLength = edgeLength;
                //getGB(index).cardButtonArray.get(2).updateBounds();

                /*if this triButton is not on the last row AND not pointup set the vert */
                if (j%2==0&&index>=OptionsStage.gameBoardSize-rows.get(rows.size-1)) {
                    /*special case*/
                    if(OptionsStage.gameBoardSize==16) {
                        if (index==9){
                        getGB(index).adjacentIndexVertical=11;
                        }
                        else if (index==11){
                            getGB(index).adjacentIndexVertical=9;
                        }
                        else if (index==13){
                            getGB(index).adjacentIndexVertical=15;
                        }
                        else if (index==15){
                            getGB(index).adjacentIndexVertical=13;
                        }
                    }
                }
                else{
                    getGB(index).adjacentIndexVertical = j % 2 == 0 ? rows.get(i) + 1 + index :  index-(rows.get(i) - 1);
                }
                /*if not on the left edge, set the left adjacent*/
                if(j>0) {
                    getGB(index).adjacentIndexLeft = index - 1;
                }
                else if(OptionsStage.gameBoardSize==16){
                    /*special case*/
                    if (index==0){
                        getGB(index).adjacentIndexLeft= 1;
                    }
                    else if (index==1){
                        getGB(index).adjacentIndexLeft= 0;
                    }
                    else if (index==4){
                        getGB(index).adjacentIndexLeft= 9;
                    }
                    else if (index==9){
                        getGB(index).adjacentIndexLeft= 4;
                    }
                }
                /*if not on the right edge set the right adjacent*/
                if(j<rows.get(i)-1) {
                    getGB(index).adjacentIndexRight = index + 1;
                }
                else if(OptionsStage.gameBoardSize==16){
                    /*special case*/
                    if (index==0){
                        getGB(index).adjacentIndexRight= 3;
                    }
                    else if (index==3){
                        getGB(index).adjacentIndexRight= 0;
                    }
                    else if (index==8){
                        getGB(index).adjacentIndexRight= 15;
                    }
                    else if (index==15){
                        getGB(index).adjacentIndexRight= 8;
                    }
                }

                index++;
            }
        }
    }

    /**
     * helper method to get game board tributtons with less code, starting with index 0 and ending with index 15
     * @return
     */
    private static TriButton getGB(int gameBoardIndex){
        gameBoardIndex = gameBoardIndex + ButtonEnum.Tri.GAMEBOARD0.value;
        return triButtonArray.get(gameBoardIndex);
    }
    public void reset(){

        /*if using pre and post game cards save them to a trident each too */
        if (OptionsStage.preAndPostGameCard && triButtonArray.size>OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value){
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value).resetPreGameCard();
            triButtonArray.get(OptionsStage.nonPrePostGameTridentsEach+ ButtonEnum.Tri.GAMETRIHAND0.value+1).resetPostGameCard();
        }
    }
    /**
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){

        switch(triButtonIndex){
            case GAMENEXTSTAGE: {
                stageInterface.goToStage(StageInterface.GAMEOVERSTAGE);
                break;
            }
            case GAMEBOARD0: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD1: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD2: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD3: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD4: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD5: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD6: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD7: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD8: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD9: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD10: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD11: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD12: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD13: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD14: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMEBOARD15: {
                highlightPosGameboard=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND0: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND1: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND2: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND3: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND4: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND5: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND6: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            case GAMETRIHAND7: {
                highlightPosTriHand=triButtonIndex.value;
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
}