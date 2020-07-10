package com.gmail.andrewahughes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MyGdxGame extends ApplicationAdapter  implements  StageInterface{
	SpriteBatch spriteBatch;
	BitmapFont bitmapFont;
	ShapeRenderer shapeRenderer;

	TitleStage titleStage;
	OptionsStage optionsStage;
	MatchMakingStage matchMakingStage;
	DealStage dealStage;
	TridentBuildingStage tridentBuildingStage;
	GameStage gameStage;
	GameOverStage gameOverStage;

	Deck deck;

	static int WORLDWIDTH;
	static int WORLDHEIGHT;
	static OrthographicCamera camera;
	static Viewport viewport;
	float aspectRatio ;
	@Override
	public void create() {

		WORLDHEIGHT=Gdx.graphics.getHeight();
		WORLDWIDTH=Gdx.graphics.getWidth();
		aspectRatio = WORLDHEIGHT/WORLDWIDTH;

		/*this will affect how big the window is in desktop mode*/
		//Gdx.graphics.setWindowedMode(WORLDWIDTH, WORLDHEIGHT);
		/*to change the screen size in desktop only just change it in teh
		* desktopLauncher, something like config.width=500;*/

		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		viewport = new FitViewport(720, 1280, camera); // change this to your needed viewport
        camera.position.set(new Vector2(WORLDWIDTH/2,WORLDHEIGHT/2),0);
        viewport.apply();

		deck = new Deck();
		/*pass in this (MyGdxGame) as the stage interface so the stage can use the stage interface methods*/
		titleStage = new TitleStage(						this, viewport, spriteBatch,shapeRenderer);
		optionsStage = new OptionsStage(					this, viewport, spriteBatch,shapeRenderer);
		matchMakingStage = new MatchMakingStage(			this, viewport, spriteBatch,shapeRenderer);
		dealStage = new DealStage(							this, viewport, spriteBatch,shapeRenderer);
		tridentBuildingStage = new TridentBuildingStage(	this, viewport, spriteBatch,shapeRenderer);
		gameStage = new GameStage(							this, viewport, spriteBatch,shapeRenderer);
		gameOverStage = new GameOverStage(					this, viewport, spriteBatch,shapeRenderer);


		goToStage(TITLESTAGE);
	}

	@Override
	public void render() {
		spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glLineWidth(5);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(0,
				0,
				WORLDWIDTH, WORLDHEIGHT,
				Color.RED, Color.GREEN, Color.BLUE, Color.WHITE);
		shapeRenderer.end();
		/*
		spriteBatch.begin();

		spriteBatch.end();
		*/

		titleStage.draw();
		optionsStage.draw();
		matchMakingStage.draw();
		dealStage.draw();
		tridentBuildingStage.draw();
		gameStage.draw();
		gameOverStage.draw();
	}
	@Override
	public void dispose () {
		spriteBatch.dispose();
		MyServer.dispose();
	}
	@Override
	public void resize(int width, int height) {
		//titleStage.getViewport().update(width,height);
		Gdx.app.log("Example","resize");
		Gdx.app.log("Example","Gdx.graphics.getHeight() ="+Gdx.graphics.getHeight());
		Gdx.app.log("Example","Gdx.graphics.getWidth() ="+Gdx.graphics.getWidth());
		Gdx.app.log("Example","WORLDHEIGHT ="+WORLDHEIGHT);
		Gdx.app.log("Example","WORLDWIDTH ="+WORLDWIDTH);
		camera.update();
		viewport.apply();
	}





	@Override
	public void hideAllStages() {
		titleStage.setVisible(false);
		optionsStage.setVisible(false);
		matchMakingStage.setVisible(false);
		dealStage.setVisible(false);
		tridentBuildingStage.setVisible(false);
		gameStage.setVisible(false);
		gameOverStage.setVisible(false);
	}

	@Override
	public void goToStage(int stage) {
		hideAllStages();
		switch (stage) {
			case TITLESTAGE :{
				titleStage.setVisible(true);
				Gdx.app.log("MyGdxGame","Gdx.graphics.getHeight() ="+Gdx.graphics.getHeight());
				Gdx.app.log("MyGdxGame","Gdx.graphics.getWidth() ="+Gdx.graphics.getWidth());
				Gdx.app.log("MyGdxGame","WORLDHEIGHT ="+WORLDHEIGHT);
				Gdx.app.log("MyGdxGame","WORLDWIDTH ="+WORLDWIDTH);
				Gdx.input.setInputProcessor(titleStage);
				break;
			}
			case OPTIONSSTAGE :{
				optionsStage.setVisible(true);
				Gdx.input.setInputProcessor(optionsStage);
				break;
			}
			case MATCHMAKINGSTAGE :{
				/*when entering the matchmaking stage, create the server - if it's already created it wont bother*/
				MyServer.create();
				matchMakingStage.setVisible(true);
				Gdx.input.setInputProcessor(matchMakingStage);
				break;
			}
			case DEALSTAGE :{
				dealStage.setVisible(true);
				Gdx.app.log("MyGdxGame","DealStage reset");
				dealStage.reset();
				/*because the dealStage needs to acccess the tridentBuildingStage's
				* cardButton array, we must set up the trident stage as well */
				Gdx.app.log("MyGdxGame","TridentBuildingStage reset");
				tridentBuildingStage.reset();
				dealStage.amendCardsForDealStageAnimation();
				Gdx.input.setInputProcessor(dealStage);
				break;
			}
			case TRIDENTBUILDINGSTAGE :{

				tridentBuildingStage.setVisible(true);
				/*all of the tridentStage should have been set up in the deal stage
				* so we just need to amend some values*/
				tridentBuildingStage.amendCardsForTridentBuildingStage();

				Gdx.input.setInputProcessor(tridentBuildingStage);
				break;
			}
			case GAMESTAGE :{
				gameStage.setVisible(true);
				Gdx.input.setInputProcessor(gameStage);
				break;
			}
			case GAMEOVERSTAGE :{
				gameOverStage.setVisible(true);
				Gdx.input.setInputProcessor(gameOverStage);
				break;
			}
		}
	}

	/**	this method is called in any of the game stages to add a new TriButton to the stage
	 * and also keep track of it in that stage's array of triButtons
	 *
	 * @param triButton the new button
	 * @param array the array to add it to
	 * @param stage the current stage so we can add the button as an actor
	 * @return
	 */
	@Override
	public void addTriButton(TriButton triButton, Array array, Stage stage) {
		/*instead of adding the buttons, insert them at the index location, this is important because this way
		* there will be an error if the buttons are added in the wrong order and i can rectify the order,
		* otherwise just using the add method will produce no error even if buttons added in the wrong order
		* but when we come to get the buttons from the array using the enum value, it could get the wrong button*/
		/*i used to pass the ButtonEnum.Tri value in as an argument, but the exact same value is always passed into
		the TriButton constructor*/

		if (triButton.triButtonIndex.value==array.size ) {

			array.insert(triButton.triButtonIndex.value, triButton);
		}
		else{
			/*might have assigned an enum to the new triButton that has already been used, or maybe the value is
			non incremental in the ButtonEnum class*/
			Gdx.app.log("Warning","triButton added with incorrect index: "+triButton.triButtonIndex+" value: "+triButton.triButtonIndex.value+" will cause problems when trying to access it");

		}

		stage.addActor(triButton);

	}

	@Override
	public TriButton getTriButton(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}



	@Override
	public void addCardButton(CardButton cardButton, Array array, Stage stage) {
		/*instead of adding the buttons, insert them at the index location, this is important because this way
		 * there will be an error if the buttons are added in the wrong order and i can rectify the order,
		 * otherwise just using the add method will produce no error even if buttons added in the wrong order
		 * but when we come to get the buttons from the array using the enum value, it could get the wrong button*/
		array.insert(cardButton.cardButtonIndex.value,cardButton);
		stage.addActor(cardButton);
	}
	@Override
	public void addCardButton(CardButton cardButton, Array array, Stage stage,int index) {
		/*i need this method in order to add the TridentbuildingStage's buttons to the cardButtonArrayTridentHand array
		* at the correct index location */
		array.insert(index,cardButton);
		stage.addActor(cardButton);
	}

	@Override
	public CardButton getCardButtonTridentBuildingStage(Array array, ButtonEnum.Card index) {
		/*added this if statement as the tri hand cardbuttons are in a different array to the
		* 52 playing cards but their values increment from those 52 ergo the tri hand card
		* buttons enum values are all over 52*/
		if(index.value>=ButtonEnum.Card.TRIHANDCARD0.value){

			return (CardButton) array.get(index.value-ButtonEnum.Card.TRIHANDCARD0.value);
		}
		return (CardButton) array.get(index.value);
	}

	/**Below are the handleButton *stage* type methods,
	 * when a button is clicked, it will use the stageinterface to call these
	 * methods, depending on which stage has been passed to the button
	 * these method will simply call the touchLogic method of the actual instance of
	 * the stage in question. alternatively i think i would have had to pass an
	 * instance of the stage to every button to be able to access the stage's
	 * no static touchLogic method
	 */
	@Override
	public void handleButtonsTitleTri(ButtonEnum.Tri triButtonIndex) {
		titleStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsOptionsTri(ButtonEnum.Tri triButtonIndex) {
		optionsStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsMatchMakingTri(ButtonEnum.Tri triButtonIndex) {
		matchMakingStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsDealTri(ButtonEnum.Tri triButtonIndex) {
		dealStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsTridentBuildingTri(ButtonEnum.Tri triButtonIndex) {
		tridentBuildingStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsGameTri(ButtonEnum.Tri triButtonIndex) {
		gameStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsGameOverTri(ButtonEnum.Tri triButtonIndex) {
		gameOverStage.touchLogic(triButtonIndex);

	}

	@Override
	public void handleButtonsTridentBuildingCard(ButtonEnum.Card cardButtonIndex) {
		tridentBuildingStage.touchLogicCard(cardButtonIndex);

	}




}
