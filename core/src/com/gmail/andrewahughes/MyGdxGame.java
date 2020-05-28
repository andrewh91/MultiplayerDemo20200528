package com.gmail.andrewahughes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private Socket socket;
	String id;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		connectSocket();
		configSocketEvents();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}

	public void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
			System.out.println("connected");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			/*when something connects to the server*/
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
			}
		}).on("socketID", new Emitter.Listener() {
			/*the event listener will listen for an event called "socketID"*/
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					Gdx.app.log("SocketIO", "my ID: " + id);
				} catch(JSONException e){
					Gdx.app.log("SocketIO","Error getting ID" +e);
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					Gdx.app.log("SocketIO", "New Player connected: " + id);
				} catch(JSONException e){
					Gdx.app.log("SocketIO","Error getting new Player ID" +e);
				}
			}
		});

	}




	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
