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

public class TriButton extends Actor {

    BitmapFont font = new BitmapFont();
    /*glyph layout helps provide some extra data on the text which helps centre it properly*/
    GlyphLayout glyphLayout = new GlyphLayout();
    SpriteBatch spriteBatch = new SpriteBatch();
    Texture texture;
    private ShapeRenderer renderer = new ShapeRenderer();
    public TriButton()
    {
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        setX(0.0f);
        setY(0.0f);
        setWidth(50.0f);
        setHeight(100.0f);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                /*the x and y are relative to the actor*/
                setX(getX()+x);
                setY(getY()+y);

            }
        }); /*the end of the this.addListener*/


    }


    public void draw(Batch batch, float parentAlpha) {
        /*super.draw(batch, parentAlpha);*/
        batch.draw(texture,getX(),getY());


    }
}
