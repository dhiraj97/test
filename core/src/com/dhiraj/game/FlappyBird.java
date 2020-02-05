package com.dhiraj.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;

	Texture backgroundImage;
	Texture birdImages[];
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;


	Circle birdCircle;

	Rectangle topTubeRectangles[];
	Rectangle bottomTubeRectangles[];

	BitmapFont font;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	float gravity = 2;
	float gap = 600;
	float maxPipeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float tubeX[] = new float[numberOfTubes];
	float tubeOffset[] = new float[numberOfTubes];
	float distanceBetweenTubes;
	int score = 0;
	int scoringTube = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		backgroundImage = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birdImages = new Texture[2];
		birdImages[0] = new Texture("bird.png");
		birdImages[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxPipeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame()
	{
		birdY = Gdx.graphics.getHeight()/2 - birdImages[0].getHeight() / 2;

		for(int i = 0;i < numberOfTubes; i++)
		{
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + (i * distanceBetweenTubes) + 1000;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}
	@Override
	public void render () {

		//draw the background first so that other objects does not overlap
		batch.begin();
		batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2)
			{
				score++;
				Gdx.app.log("Score: ", String.valueOf(score));
				if(scoringTube < numberOfTubes - 1)
				{
					scoringTube++;
				}
				else{
					scoringTube = 0;
				}
			}


			if(Gdx.input.justTouched())
			{
				velocity = -30;

			}

			for(int i = 0;i < numberOfTubes; i++)
			{
				if(tubeX[i] < - topTube.getWidth())
				{
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() / 2 - gap - 200);

				}
				else
				{
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube,tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(), bottomTube.getHeight());

			}

			if(birdY > 0)
			{
				velocity = velocity + gravity;
				birdY -= velocity;
			}
			else
			{
				gameState = 2;
			}
		}
		else if(gameState == 0) {
			if(Gdx.input.justTouched())
			{
				gameState = 1;

			}
		}
		else if(gameState == 2)
		{
			batch.draw(gameOver,Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
			if(Gdx.input.justTouched())
			{
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}
		batch.draw(birdImages[flapState], Gdx.graphics.getWidth() / 2 - birdImages[flapState].getWidth() / 2, birdY);
		font.draw(batch,String.valueOf(score),10, Gdx.graphics.getHeight() * 0.1f);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birdImages[flapState].getHeight() / 2, birdImages[flapState].getWidth() / 2);

		for(int i = 0;i < numberOfTubes; i++)
		{
			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i]))
			{
				gameState = 2;

			}
		}
		batch.end();
		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundImage.dispose();
	}
}
