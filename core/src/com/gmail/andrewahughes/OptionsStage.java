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

public class OptionsStage extends Stage {

    boolean visible =false;
    StageInterface stageInterface;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    /*we need to store an array of TriButtons so we can loop through and call the draw method of each
    need to use my custom method of adding buttons in order to make sure they are added to this array*/
    static Array<TriButton> triButtonArray = new Array<TriButton>();

    /*options stage values*/

    /**
     *     need to work out how many cards each player should have maximum
     *     consider how many players there are, if preAndPostGameCards are enabled
     */
    public static int cardsEach=0;
    /**
     *     need to work out how many cards each player should have
     *     consider how many players there are
     */
    public static int nonPrePostGameCardsEach=0;
    /**
     *     need to work out how many tridents each player should have maximum
     *     consider how many players there are, if preAndPostGameCards are enabled
     */
    public static int tridentsEach=0;

    /**
     * this will be used in the gameStage, will be max 8
     */
    public static int nonPrePostGameTridentsEach=0;
    /**
     * the open game mode allows all player's to see each other's cards
     * disable this mode so player's can only see their own cards
     */
    public static boolean openMode = true;
    /**
     * number of players will be either 2 or 3
     */
    public static byte numberOfPlayers = 2;
    public static boolean preAndPostGameCard = false;
    /**
     * used in the game stage when setting up the gameboard
     * max value should be 16, which will give a standard board 4 rows, with 1, 3, 5 then 7 tridents per row
     * value could be as low as 4, and there will be special logic to cater for a value of 8, which will create two
     * board with 4 tridents.
     * you can feed any number in, but if you want it to look symetrical need to pick one of these values,
     * 1,4, 9, 16, 25, 36 etc or as mentioned use one of the multiples of 4 , up to 24, which will create
     * 6  separate boards of 4 triButtons each
     */
    public static int gameBoardSize=0;

    public static final int PREMADETRIDENTS=0;
    public static final int BUILDYOUROWNTRIDENTS=1;
    /**
     * this will alter how the trident building stage works. the options are PREMADETRIDENTS or BUILDYOUROWNTRIDENTS
     */
    public static int tridentBuildingMode=PREMADETRIDENTS;
    /**
     * if set to true, then during the game you can flip and rotate tridents on the
     * gameboard that you placed to change the position of the cards.
     */
    public static boolean allowFlipAndRotate=false;

    public OptionsStage(StageInterface stageInterface, Viewport viewport, SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        this.stageInterface =stageInterface;

        this.shapeRenderer = shapeRenderer;
        this.setViewport(viewport);
        this.spriteBatch = batch;


        viewport.update(WORLDWIDTH, WORLDHEIGHT, true);
        createButtons();
        calculateCardsEach();
        gameBoardSize=16;
    }



    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {
            this.getViewport().apply();

            Gdx.gl.glClearColor(0.7f, 0.5f, 0f, 1);
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
        stageInterface.addTriButton(new TriButton(stageInterface,0,0,false,StageInterface.OPTIONSSTAGE, ButtonEnum.Tri.OPTIONSNEXTSTAGE),triButtonArray,this);
        stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.OPTIONSNEXTSTAGE).setText("Match\nMaking");
        //stageInterface.getTriButton(triButtonArray,ButtonEnum.Tri.OPTIONSNEXTSTAGE).setTridentToTextSize();
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
     * this will be called in the tributton class,
     * @param triButtonIndex this will be the index of the tributton that was clicked, the index is set on creation of the
     *                       triButton and will be the same as it's index in the triButtonArray for this stage
     */
    public void touchLogic(ButtonEnum.Tri triButtonIndex){

        switch(triButtonIndex){
            case OPTIONSNEXTSTAGE: {
                stageInterface.goToStage(StageInterface.MATCHMAKINGSTAGE);
                break;
            }
            default:
                Gdx.app.log("Example", "DEFAULT "+triButtonIndex.value);
                throw new IllegalStateException("Unexpected value: " + triButtonIndex);
        }
    }
    public static void calculateCardsEach(){
        if(numberOfPlayers==2){
            cardsEach=24;
            nonPrePostGameCardsEach=24;
        }
        else{
            cardsEach=15;
            nonPrePostGameCardsEach=15;
        }
        if (preAndPostGameCard){
            cardsEach+=2;
        }
        tridentsEach = (int)Math.ceil(cardsEach/3f);
        nonPrePostGameTridentsEach=tridentsEach;
        if (preAndPostGameCard){
            nonPrePostGameTridentsEach=tridentsEach-1;
        }

    }
}

