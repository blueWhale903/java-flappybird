package com.hgl.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Random;
import java.util.Vector;

import sun.jvm.hotspot.gc.shared.Space;

public class flappybird extends ApplicationAdapter {
	final int screenWidth = 288;
	final int screenHeight = 512;
	int frameCount = 0;
	SpriteBatch batch;
	OrthographicCamera camera;
	boolean isPlaying = true;
	float birdVelY = 0;
	float fallSpeed = 35;
	float birdAnimSpeed = 10;
	float gap = 110;
	int numberOfPipes = 0;
	int scoringPipe = 0;

	// Objects
	Rectangle bird;
	Rectangle ground;
	Array<Rectangle> topPipes;
	Array<Rectangle> botPipes;

	float lastPipeSpawnTime;
	int score = 0;


	// Textures
	Texture birdTexture;
	Array<Texture> birdTextures;
	Texture background;
	Texture groundTexture;
	Texture botPipeTexture;
	Texture topPipeTexture;
	Texture gameOverTexture;
	Texture[] scoreTexture;

	Random rand = new Random();

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		birdTexture = new Texture("yellowbird-midflap.png");
		birdTextures = new Array<Texture>();
		birdTextures.add(new Texture("yellowbird-upflap.png"));
		birdTextures.add(new Texture("yellowbird-midflap.png"));
		birdTextures.add(new Texture("yellowbird-downflap.png"));
		background = new Texture("background-day.png");
		botPipeTexture = new Texture("pipe.png");
		topPipeTexture = new Texture("pipe.png");
		groundTexture = new Texture("base.png");
		gameOverTexture = new Texture("gameover.png");
		scoreTexture = new Texture[10];
		scoreTexture[0] = new Texture("0.png");
		scoreTexture[1] = new Texture("1.png");
		scoreTexture[2] = new Texture("2.png");
		scoreTexture[3] = new Texture("3.png");
		scoreTexture[4] = new Texture("4.png");
		scoreTexture[5] = new Texture("5.png");
		scoreTexture[6] = new Texture("6.png");
		scoreTexture[7] = new Texture("7.png");
		scoreTexture[8] = new Texture("8.png");
		scoreTexture[9] = new Texture("9.png");

		botPipes = new Array<Rectangle>();
		topPipes = new Array<Rectangle>();

		// Bird
		bird = new Rectangle();
		bird.x = 50;
		bird.y = 320;
		bird.width = 34;
		bird.height = 24;

		// Ground
		ground = new Rectangle();
		ground.x = 0;
		ground.y = 0;
		ground.width = 336;
		ground.height = 112;
	}

	private void updateBird() {
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			birdVelY = 5;
		}
		bird.y += birdVelY;
		birdVelY += -fallSpeed * Gdx.graphics.getDeltaTime();
	}

	private void spawnPipe() {
		Rectangle botPipe = new Rectangle();
		botPipe.x = 336;
		botPipe.y = (float)rand.nextInt(-170, 50);
		botPipe.width = 52;
		botPipe.height = 320;
		botPipes.add(botPipe);

		Rectangle topPipe = new Rectangle();
		topPipe.x = 336;
		topPipe.y = botPipe.y + gap + 320;
		topPipe.width = 52;
		topPipe.height = 320;
		topPipes.add(topPipe);

		numberOfPipes++;

		lastPipeSpawnTime = TimeUtils.nanoTime();
	}

	private void updatePipes() {
		for (Iterator<Rectangle> iter = botPipes.iterator(); iter.hasNext(); ) {
			Rectangle pipe = iter.next();
			pipe.x -= 200 * Gdx.graphics.getDeltaTime();
			if(pipe.x + 52 < 0) {
				numberOfPipes--;
				scoringPipe = 0;
				iter.remove();
			}
			if(pipe.overlaps(bird)) {
				isPlaying = false;
			}
		}

		for (Iterator<Rectangle> iter = topPipes.iterator(); iter.hasNext(); ) {
			Rectangle pipe = iter.next();
			pipe.x -= 200 * Gdx.graphics.getDeltaTime();
			if(pipe.x + 52 < 0) iter.remove();
			if(pipe.overlaps(bird)) {
				isPlaying = false;
			}
		}
	}

	private void restart() {
		topPipes.clear();
		botPipes.clear();
		bird.x = 50;
		bird.y = 320;

		numberOfPipes = 0;
		scoringPipe = 0;
		score = 0;

		lastPipeSpawnTime = 0;
	}

	private Vector<Integer> toListInt(int n) {
		Vector<Integer> res = new Vector<Integer>();
		while (n > 0) {
			int m = n % 10;
			res.add(m);
			n = (int)(n / 10);
		}

		return res;
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		if (isPlaying) {
			updateBird();
			updatePipes();

			if (bird.overlaps(ground)) {
				isPlaying = false;
			}

			if(TimeUtils.nanoTime() - lastPipeSpawnTime > 1000000000) spawnPipe();

			if (botPipes.get(scoringPipe).x < 50) {
				score += 50;

				if (scoringPipe < numberOfPipes - 1) {
					scoringPipe++;
				} else {
					scoringPipe = 0;
				}
			}
		} else {
			updateBird();
			if (bird.overlaps(ground)) {
				birdVelY = 0;
			}
		}

		batch.begin();
		batch.draw(background, 0, 0);
		for (Rectangle botPipe : botPipes) {
			batch.draw(botPipeTexture, botPipe.x, botPipe.y);
		}
		for (Rectangle topPipe : topPipes) {
			Sprite sprite = new Sprite(topPipeTexture);
			sprite.setX(topPipe.x);
			sprite.setY(topPipe.y);
			sprite.flip(false, true);
			sprite.draw(batch);
		}
		batch.draw(groundTexture, ground.x, ground.y);

		if (!isPlaying) {
			batch.draw(gameOverTexture,
					(float) screenWidth /2 - (float)gameOverTexture.getWidth()/2,
					(float) screenHeight /2 - (float)gameOverTexture.getHeight()/2);

			if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
				restart();
				isPlaying = true;
			}

			batch.draw(birdTextures.get(0), bird.x, bird.y);
		} else {
			batch.draw(birdTextures.get((int)(frameCount / birdAnimSpeed) % 3), bird.x, bird.y);
		}
		Vector<Integer> temp = toListInt(score);
		int w = 24 + 30*(temp.size()-1);
		int x = (screenWidth - w) / 2;

		for (Integer i : temp) {
			batch.draw(scoreTexture[i], screenWidth - x - 24, screenHeight / 2 + 100);
			x += 30;
		}

		batch.end();

		frameCount++;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
