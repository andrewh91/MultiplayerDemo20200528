package com.gmail.andrewahughes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

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

	@Override
	public void create() {

		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		shapeRenderer = new ShapeRenderer();

		/*pass in this (MyGdxGame) as the stage interface so the stage can use the stage interface methods*/
		titleStage = new TitleStage(this);
		optionsStage = new OptionsStage(this);
		matchMakingStage = new MatchMakingStage(this);
		dealStage = new DealStage(this);
		tridentBuildingStage = new TridentBuildingStage(this);
		gameStage = new GameStage(this);
		gameOverStage = new GameOverStage(this);

		goToStage(TITLESTAGE);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(0,
				0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
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
				Gdx.input.setInputProcessor(dealStage);
				break;
			}
			case TRIDENTBUILDINGSTAGE :{
				tridentBuildingStage.setVisible(true);
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
		array.insert(triButton.triButtonIndex.value, triButton);

		stage.addActor(triButton);

	}

	@Override
	public TriButton getTriButton(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonTitleStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonOptionsStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonMatchMakingStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonDealStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonTridentBuildingStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonGameStage(Array array, ButtonEnum.Tri index) {
		/*array hold type object, so we need to cast (TriButton) else incompatible types error*/
		return (TriButton) array.get(index.value);
	}

	@Override
	public TriButton getTriButtonGameOverStage(Array array, ButtonEnum.Tri index) {
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
	public CardButton getCardButtonTridentBuildingStage(Array array, ButtonEnum.Card index) {
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
