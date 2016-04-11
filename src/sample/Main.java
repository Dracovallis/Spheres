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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private static final double ENEMY_VELOCITY = 1;
    private static final int ENEMY_RESPAWN_FREQUENCY = 100;
    private static double INITIAL_SCALE = 0.3;
    private static double SCALE_INCREASE_FACTOR = 0.05;
    private static final String HERO_IMAGE_LOC = "player.png";
    private static final String BACKGROUND_IMAGE_LOC = "starsBackground.jpg";
    private static final String ENEMIES_IMAGE_LOC = "enemies.png";
    private static final String FRIENDS_IMAGE_LOC = "friends.png";
    private static int spawnEnemiesCounter = 0;

    private Image heroImage;
    private Image backgroundImage;

    private ImageView hero;
    private ImageView background;

    private Image enemyImage;
    private Image friendImage;
    private ImageView enemy;
    private List<ImageView> enemyList = new ArrayList<>();

    boolean running;
    boolean goNorth;
    boolean goSouth;
    boolean goEast;
    boolean goWest;

    @Override
    public void start(Stage stage) throws Exception {
        //Initializing our window parameters
        stage.setMaxHeight(SCREEN_HEIGHT);
        stage.setMaxWidth(SCREEN_WIDTH);
        stage.setTitle("Spheres");
        stage.setFullScreen(false);

        //Initializing our hero
        heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);
        hero.setScaleX(INITIAL_SCALE);
        hero.setScaleY(INITIAL_SCALE);
        moveHeroTo(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

        //Initializing our background
        backgroundImage = new Image(BACKGROUND_IMAGE_LOC);
        background = new ImageView(backgroundImage);

        //Putting everything together in a group and showing it to the player
        Group arena = new Group();
        arena.getChildren().add(background);
        arena.getChildren().add(hero);
        Scene scene = new Scene(arena, SCREEN_WIDTH, SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();

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
                    case O:
                        INITIAL_SCALE += 0.1;
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

        //Everything that is needed to be updated on the stage must be put here
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                spawnEnemiesCounter++;

                if (spawnEnemiesCounter == ENEMY_RESPAWN_FREQUENCY) {
                    spawnEnemiesCounter = 0;
                    spawnEnemy(arena, INITIAL_SCALE, enemyList);
                }

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
                moveEnemies(enemyList, ENEMY_VELOCITY);
                changeEnemyColor(enemyList);

                //TODO: make this work properly
//                for (ImageView imageView : enemyList) {
//                    boolean isBiggerThanYou = imageView.getScaleX() > hero.getScaleX();
//                    boolean areIntersecting = (imageView.getLayoutBounds().intersects(hero.getLayoutBounds()));
//
//                    if (areIntersecting && !isBiggerThanYou) {
//                        hero.setScaleX(INITIAL_SCALE += SCALE_INCREASE_FACTOR);
//                        hero.setScaleY(INITIAL_SCALE += SCALE_INCREASE_FACTOR);
//                        imageView.setVisible(false);
//                    }
//                }
            }
        };
        gameLoop.start();
    }

    private void changeEnemyColor(List<ImageView> enemyList) {
        for (ImageView imageView : enemyList) {
            boolean isBiggerThanYou = imageView.getScaleX() > hero.getScaleX();
            if (!isBiggerThanYou) {
                imageView.setImage(friendImage);
            } else {
                imageView.setImage(enemyImage);
            }
        }
    }

    private void moveEnemies(List<ImageView> enemyList, double enemySpeed) {
        double destinationX = hero.getLayoutX();
        double destinationY = hero.getLayoutY();

        for (ImageView npc : enemyList) {

            double enemyCurrentPosX = npc.getLayoutX();
            double enemyCurrentPosY = npc.getLayoutY();
            boolean isBiggerThanYou = npc.getScaleX() > hero.getScaleX();

            if (isBiggerThanYou) {
                if (enemyCurrentPosX >= destinationX) {
                    enemyCurrentPosX -= enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                } else {
                    enemyCurrentPosX += enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                }

                if (enemyCurrentPosY >= destinationY) {
                    enemyCurrentPosY -= enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                } else {
                    enemyCurrentPosY += enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                }
            } else {
                if ((enemyCurrentPosX >= destinationX) &&
                        (enemyCurrentPosX + npc.getBoundsInLocal().getWidth() < SCREEN_WIDTH)) {
                    enemyCurrentPosX += enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                } else if ((enemyCurrentPosX < destinationX) && (enemyCurrentPosX > 0)) {
                    enemyCurrentPosX -= enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                }

                if ((enemyCurrentPosY >= destinationY) &&
                        (enemyCurrentPosY + npc.getBoundsInLocal().getHeight() < SCREEN_HEIGHT)) {
                    enemyCurrentPosY += enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                } else if ((enemyCurrentPosY < destinationY) && (enemyCurrentPosY > 0)) {
                    enemyCurrentPosY -= enemySpeed;
                    npc.relocate(enemyCurrentPosX, enemyCurrentPosY);
                }
            }
        }
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

        if (destinationX > 0 &&
                destinationX < SCREEN_WIDTH &&
                destinationY > 0 &&
                destinationY < SCREEN_HEIGHT) {
            hero.relocate(destinationX - currentX, destinationY - currentY);
        }
    }

    private void spawnEnemy(Group arena, double heroCurrentScale, List<ImageView> enemyList) {
        Random r = new Random();
        double randomScaleFactor = r.nextInt(3) * 0.1;

        if (r.nextInt(2) == 1) {
            enemyImage = new Image(ENEMIES_IMAGE_LOC);
            enemy = new ImageView(enemyImage);
            enemy.setScaleX(heroCurrentScale + randomScaleFactor);
            enemy.setScaleY(heroCurrentScale + randomScaleFactor);
            arena.getChildren().add(enemy);
            enemyList.add(enemy);

        } else {
            friendImage = new Image(FRIENDS_IMAGE_LOC);
            enemy = new ImageView(friendImage);
            enemy.setScaleX(heroCurrentScale - randomScaleFactor);
            enemy.setScaleY(heroCurrentScale - randomScaleFactor);
            arena.getChildren().add(enemy);
            enemyList.add(enemy);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}