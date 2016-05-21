package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.concurrent.Semaphore;


public class IHMManager extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture needleImage, cadreImage, bgImage, ligneRefImage, dotImage, dotRougeImage, dotVertImage,
	dotOrangeImage, dotJauneImage;
	private OrthographicCamera camera;
	private Sprite needle, cadre, background;

	private BitmapFont font, noteDisplay;
	private FreeTypeFontGenerator fontGeneratorNote, fontGeneratorButton;

	private Sprite ligneRef; //Nuage de points
	private Array<Rectangle> pitchDelta;

	private double midiValues[] = new double[] {40.0,45.0,50.0,55.0,59.0,64.0};
	private char notes[] = new char[] {'E', 'A', 'D', 'G', 'B', 'e'};
	double midimoyenne= 0.0; //Valeur de la note en midi a affiche
	private int currentFreqRef = -1;
	Float currangle=Float.valueOf(120);

	/* Pour le bouton settings */
	private Skin skin;
	private Stage stage;
	private GdxCallback callback;


	@Override
	public void create () {
		pitchDelta = new Array<Rectangle>();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1080, 1920);
		batch = new SpriteBatch();

		//cadreImage = new Texture(Gdx.files.internal("NewCadranv2.png"));
		cadreImage = new Texture(Gdx.files.internal("CadranTest.png"));
		cadre = new Sprite(cadreImage);
		//cadre.setPosition(50, -175);
		cadre.setPosition(48, -88);
		cadre.setSize(975, 1080);

		bgImage = new Texture(Gdx.files.internal("Background.png"));
		background = new Sprite(bgImage);
		background.setSize(1100, 1950);
		background.setPosition(0, 0);

		needleImage = new Texture(Gdx.files.internal("NewAiguillev2.png"));
		needle = new Sprite(needleImage);
		//needle.setPosition(208, 102);
		needle.setPosition(208, 102);
		needle.setOrigin(327, Float.valueOf("23.5"));

		// TODO peut etre mettre auiguille a plat

		font = new BitmapFont();
		font.setColor(Color.BLACK);

		/* Affichage de la note jouée */
		FreeTypeFontGenerator fontGeneratorNote = new FreeTypeFontGenerator(Gdx.files.internal("DINPro-Cond.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 200;
		noteDisplay = fontGeneratorNote.generateFont(parameter); // font size 12 pixels
		noteDisplay.setColor(Color.WHITE);


		/* partie pour le truc qui defile */
		ligneRefImage = new Texture(Gdx.files.internal("Barrev2.png"));
		ligneRef = new Sprite(ligneRefImage);
		//ligneRef.setPosition(540, 1540 - 350);
		ligneRef.setPosition(540, 1540 - 450);
		ligneRef.setSize(10, 650);
		dotImage = new Texture(Gdx.files.internal("dot.png"));
		dotRougeImage = new Texture(Gdx.files.internal("dotRouge.png"));
		dotVertImage = new Texture(Gdx.files.internal("dotVert.png"));
		dotOrangeImage = new Texture(Gdx.files.internal("dotOrange.png"));
		dotJauneImage = new Texture(Gdx.files.internal("dotJaune.png"));

		/* Bouton Settings */
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Generate text
		fontGeneratorButton = new FreeTypeFontGenerator(Gdx.files.internal("GeosansLight.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter settingsParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		settingsParameter.size = 50;
		BitmapFont bfont = fontGeneratorButton.generateFont(settingsParameter);
		bfont.setColor(Color.WHITE);
		skin.add("default",bfont);

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.CLEAR);
		textButtonStyle.down = skin.newDrawable("white", Color.CLEAR);
		textButtonStyle.checked = skin.newDrawable("white", Color.CLEAR);
		textButtonStyle.over = skin.newDrawable("white", Color.CLEAR);

		textButtonStyle.font = skin.getFont("default");

		skin.add("default", textButtonStyle);

		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton textButton=new TextButton("Settings",textButtonStyle);
		textButton.setPosition(50, 1650);
		stage.addActor(textButton);

		textButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				callback.startSettingsActivity();
			}
		});


		System.out.println("Fin start IHM truc");
	}

	public void update(double midi, int ref) {

		currentFreqRef = ref;
		midimoyenne = midi;
		spawnNotes(midimoyenne);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		background.draw(batch);
		needle.draw(batch);
		cadre.draw(batch); //TODO remettre needle apres cadre et remettre le point du cadre en blanc
		ligneRef.draw(batch);
		font.draw(batch,"Midi : "+ midimoyenne + "", 500, 100);
		font.getData().setScale(2,2); //voir FreeTypeFontParameter

		int midiref=currentFreqRef;
		if(midiref != -1) {
			noteDisplay.draw(batch, "" + notes[midiref], 500, 925);
		}

		double diff;
		for (Rectangle note : pitchDelta) {
			if(note!=null) {
				diff = Math.abs(540 - note.x);
				if (diff < 75)
					batch.draw(dotVertImage, note.x, note.y);
				else if (diff < 150)
					batch.draw(dotJauneImage, note.x, note.y);
				else if (diff < 200)
					batch.draw(dotOrangeImage, note.x, note.y);
				else
					batch.draw(dotRougeImage, note.x, note.y);
			}
		}
		batch.end();
		move(midimoyenne);

		Iterator<Rectangle> iter = pitchDelta.iterator();
		while (iter.hasNext()) {
			Rectangle note = iter.next();
			if (note != null) {
				note.y -= 40 * Gdx.graphics.getDeltaTime();
				if (note.y < 1540 - 400) {
					iter.remove();
				}
			}
		}

		/* Bouton Settings */
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

	}
	private float delta;
	private void spawnNotes(double midiNote) {

		if (currentFreqRef != -1) {
			Rectangle note = new Rectangle();
			delta = (float)(midiNote - midiValues[currentFreqRef]);
			note.x = 540 + 800*(delta);
			//note.y = 1870;
			note.y = 1750;
			note.width = 10;
			note.height = 10;
			pitchDelta.add(note);
		}

	}
	private final Float FrottementConstante=Float.valueOf("0.50");
	private final Float FrottementConstanteEnd=Float.valueOf("0.70");
	private final Float FrottementConstanteStart=Float.valueOf("0.05");


	private void move(double midi) {
		if(midi!=-1) { // Si les valeurs sont correcte on applique le calcul dangle
			float pitch = new Float(midi);
			float angle = -1*((pitch-35)*180/(70-35));
			float delta = angle - currangle;
			angle = currangle + FrottementConstante*delta;

			if(midi<70) {
				currangle = angle;
			}
			else {
				angle = currangle + FrottementConstanteEnd*(180-currangle);
				currangle = angle;
			}
			needle.setRotation(angle);
		}
		else {
			float angle = currangle + FrottementConstanteStart*(0-currangle);
			currangle = angle;
			needle.setRotation(angle);
		}

	}

	@Override
	public void dispose() {
		needleImage.dispose();
		cadreImage.dispose();
		bgImage.dispose();
		ligneRefImage.dispose();
		dotImage.dispose();
		dotRougeImage.dispose();
		dotVertImage.dispose();
		dotOrangeImage.dispose();
		dotJauneImage.dispose();
		font.dispose();

		/* Bouton Settings */
		stage.dispose();
		skin.dispose();

		batch.dispose();
	}

	// Setter for the callback
	public void setCallback(GdxCallback cb) {
		callback = cb;
	}

	public interface GdxCallback {
		public void startSettingsActivity();
	}

}
