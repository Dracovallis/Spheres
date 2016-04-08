package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    //You can write your name on the task you prefer
    //TODO: Make enemy spheres spawn from the edges of the screen
    //TODO: Make enemies go to random directions
    //TODO: Make the resizing logic of the nodes/players and npc/
    //TODO: Add a text when the game starts explaining the controls of the game /make the game userfriendly/
    //TODO: Add music/Sound effects


    private static final double SCREEN_WIDTH = 1024;
    private static final double SCREEN_HEIGHT = 768;
    private static final double VELOCITY = 3;
    private static double INITIAL_SCALE = 0.1;
    private static double SCALE_INCREASE_FACTOR = 0.05;
    private static final String HERO_IMAGE_LOC = "sun.png";
    private static final String BACKGROUND_IMAGE_LOC = "starsBackground.jpg";

    private Image heroImage;
    private Image backgroundImage;

    private ImageView hero;
    private ImageView background;

    boolean running;
    boolean goNorth;
    boolean goSouth;
    boolean goEast;
    boolean goWest;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setMaxHeight(SCREEN_HEIGHT);
        stage.setMaxWidth(SCREEN_WIDTH);
        stage.setTitle("Spheres");
        stage.setFullScreen(false);

        heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);
        hero.setScaleX(INITIAL_SCALE);
        hero.setScaleY(INITIAL_SCALE);

        backgroundImage = new Image(BACKGROUND_IMAGE_LOC);
        background = new ImageView(backgroundImage);
        Group arena = new Group(background);
        arena.getChildren().add(hero);

        moveHeroTo(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

        Scene scene = new Scene(arena, SCREEN_WIDTH, SCREEN_HEIGHT);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        goNorth = true;
                        break;
                    case S:
                        goSouth = true;
                        break;
                    case A:
                        goWest = true;
                        break;
                    case D:
                        goEast = true;
                        break;
                    case SHIFT:
                        running = true;
                        break;
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        goNorth = false;
                        break;
                    case S:
                        goSouth = false;
                        break;
                    case A:
                        goWest = false;
                        break;
                    case D:
                        goEast = false;
                        break;
                    case SHIFT:
                        running = false;
                        break;
                }
            }
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                int dx = 0;
                int dy = 0;
                hero.setScaleX(INITIAL_SCALE);
                hero.setScaleY(INITIAL_SCALE);

                if (goNorth) {
                    dy -= VELOCITY;
                }
                if (goSouth) {
                    dy += VELOCITY;
                }
                if (goEast) {
                    dx += VELOCITY;
                }
                if (goWest) {
                    dx -= VELOCITY;
                }
                if (running) {
                    {
                        dx *= 3;
                        dy *= 3;
                    }
                }
                moveHeroBy(dx, dy);
            }
        };
        timer.start();
    }

    private void moveHeroBy(int directionX, int directionY) {
        if (directionX == 0 && directionY == 0) {
            return;
        }

        final double currentX = hero.getBoundsInLocal().getWidth() / 2;
        final double currentY = hero.getBoundsInLocal().getHeight() / 2;

        double destinationX = currentX + hero.getLayoutX() + directionX;
        double destinationY = currentY + hero.getLayoutY() + directionY;

        moveHeroTo(destinationX, destinationY);
    }

    private void moveHeroTo(double destinationX, double destinationY) {
        final double currentX = hero.getBoundsInLocal().getWidth() / 2;
        final double currentY = hero.getBoundsInLocal().getHeight() / 2;

        if (destinationX - currentX >= 0 &&
                destinationX + currentX <= SCREEN_WIDTH &&
                destinationY - currentY >= 0 &&
                destinationY + currentY <= SCREEN_HEIGHT) {
            hero.relocate(destinationX - currentX, destinationY - currentY);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}