// Source: https://www.youtube.com/watch?v=db8h1gDgt6M

package org.acme;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloFX extends Application {

	/*
	// Really nice effect!
	private static final double MAX_ATTRACT_DISTANCE = 350;
	private static final double MIN_ATTRACT_DISTANCE = 0.1;
	private static final double FORCE = 5000;
	*/

	private static final double MAX_ATTRACT_DISTANCE = 250;
	private static final double MIN_ATTRACT_DISTANCE = 1.1;
	private static final double FORCE = 5000;

	 private GraphicsContext g;
	 private double mouseX, mouseY;
	 private List<Particle> particles = new ArrayList<>();

	@Override
	public void start(Stage stage) throws Exception {
		var scene = new Scene(createContent());
		scene.setOnMouseMoved(e -> {
			mouseX = e.getX();
			mouseY = e.getY();
		});

		stage.setScene(scene);
		stage.show();
	}

	private Parent createContent() {
		for (int y = 0; y < 720 / 10; y++) {
			for (int x = 0; x < 1280 / 10; x++) {
				particles.add(new Particle(x * 10, y * 10, Color.BLUE));
			}
		}
		var canvas = new Canvas(1280, 720);
		g = canvas.getGraphicsContext2D();

		AnimationTimer timer = new AnimationTimer(){
			@Override
			public void handle(long now) {
				onUpdate();
			}
		};
		timer.start();

		var pane = new Pane(canvas);
		pane.setPrefSize(1280, 720);

		return pane;
	}

	protected void onUpdate() {
		g.clearRect(0, 0, 1280, 720);
		var cursorPos = new Point2D(mouseX, mouseY);
		particles.forEach(p -> {
			p.update(cursorPos);
			g.setFill(p.color);
			g.fillOval(p.x - 1, p.y - 1, 2, 2);
		});
	}

	private static class Particle {
		double x, y, originalX, originalY;
		Color color, originalColor;

		public Particle(double x, double y, Color color) {
			this.x = originalX = x;
			this.y = originalY = y;
			this.color = originalColor = color;
		}

		void update(Point2D cursorPos) {
			var distance = cursorPos.distance(x, y);

			if (distance > MAX_ATTRACT_DISTANCE) {
				x = originalX;
				y = originalY;
				color = originalColor;
			} else if (distance < MIN_ATTRACT_DISTANCE) {
				x = cursorPos.getX();
				y = cursorPos.getY();
			} else {
				var vector = cursorPos.subtract(x, y);
				var scaledLength = FORCE * 1 / distance;
				vector = vector.normalize().multiply(scaledLength);

				x = originalX + vector.getX();
				y = originalY + vector.getY();

				color = Color.RED;
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
