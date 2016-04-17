package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    //You can write your name on the task you prefer
    //TODO: Make enemy spheres spawn from the edges of the screen
    //TODO: Make the resizing logic of the nodes/players and npc/
    //TODO: Add a text when the game starts explaining the controls of the game /make the game userfriendly/
    //TODO: Add music/Sound effects

    private static final double SCREEN_WIDTH = 1280;
    private static final double SCREEN_HEIGHT = 1000;
    private static final double VELOCITY = 3;
    private static final double ENEMY_VELOCITY = 1;
    private static final int ENEMY_RESPAWN_FREQUENCY = 100;
    private static final double INITIAL_SCALE = 0.3;
    private static final double SCALE_INCREASE_FACTOR = 0.05;
    private static final String HERO_IMAGE_LOC = "player.png";
    private static final String BACKGROUND_IMAGE_LOC = "starsBackground.jpg";
    private static final String ENEMIES_IMAGE_LOC = "enemies.png";
    private static final String FRIENDS_IMAGE_LOC = "friends.png";
    private static int spawnEnemiesCounter = 0;
    private static double currentScale = INITIAL_SCALE;
    private static int score = 0;
    private static int instructionsCounter = 400;

    private ImageView hero;

    private Image enemyImage;
    private Image friendImage;
    private List<ImageView> enemyList = new ArrayList<>();

    boolean running;
    boolean goNorth;
    boolean goSouth;
    boolean goEast;
    boolean goWest;
    boolean gameOver = false;

    @Override
    public void start(Stage stage) throws Exception {
        //Initializing our window parameters
        stage.setMaxHeight(SCREEN_HEIGHT);
        stage.setMaxWidth(SCREEN_WIDTH);
        stage.setTitle("Spheres");
        stage.setFullScreen(false);

        //Initializing our hero
        Image heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);
        hero.setScaleX(currentScale);
        hero.setScaleY(currentScale);
        moveHeroTo(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

        //Initializing our background
        Image backgroundImage = new Image(BACKGROUND_IMAGE_LOC);
        ImageView background = new ImageView(backgroundImage);

        //Initializing text
        Label scoreText = new Label("Score: " + score);
        scoreText.relocate(SCREEN_WIDTH - 100, 25);
        scoreText.setTextFill(Color.WHITE);
        Label gameInstructions = new Label("W,A,S,D -> Move Sphere\n" +
                "SHIFT -> Sprint\n" +
                "Eat the green spheres!\n" +
                "Avoid being eaten by the red ones!");
        gameInstructions.relocate(20, SCREEN_HEIGHT - 150);
        gameInstructions.setTextFill(Color.WHITE);

//        //Rotating player
//        RotateTransition rt = new RotateTransition(Duration.millis(3000), hero);
//        rt.setByAngle(360);
//        rt.setCycleCount(Timeline.INDEFINITE);
//        rt.setAutoReverse(true);
//        rt.play();

        //Putting everything together in a group and showing it to the player
        Group arena = new Group();
        arena.getChildren().add(background);
        arena.getChildren().add(hero);
        arena.getChildren().add(scoreText);
        arena.getChildren().add(gameInstructions);
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
                        currentScale += 0.1;
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
                    spawnEnemy(arena, currentScale, enemyList);
                }

                if (instructionsCounter != 0) {
                    instructionsCounter--;
                } else {
                    arena.getChildren().remove(gameInstructions);
                }

                int directionX = 0;
                int directionY = 0;
                if (goNorth) {
                    directionY -= VELOCITY;
                }
                if (goSouth) {
                    directionY += VELOCITY;
                }
                if (goEast) {
                    directionX += VELOCITY;
                }
                if (goWest) {
                    directionX -= VELOCITY;
                }
                if (running && (currentScale > INITIAL_SCALE)) {
                    {
                        currentScale -= 0.005;
                        directionX *= 2;
                        directionY *= 2;
                    }
                }
                moveHeroBy(directionX, directionY);

                moveEnemies(enemyList, ENEMY_VELOCITY);
                changeEnemyColor(enemyList);
                collisionChecker(enemyList);

                hero.setScaleX(currentScale);
                hero.setScaleY(currentScale);

                scoreText.setText("Score: " + score);

                if (gameOver) {
                    for (ImageView imageView : enemyList) {
                        arena.getChildren().remove(imageView);
                    }
                    arena.getChildren().remove(hero);
                    scoreText.setText("GAME OVER\nScore: " + score);
                    scoreText.relocate(SCREEN_WIDTH / 2 - scoreText.getWidth() / 2,
                            SCREEN_HEIGHT / 2 - scoreText.getHeight() / 2);
                    scoreText.setScaleX(5);
                    scoreText.setScaleY(5);
                }


            }
        };
        gameLoop.start();
    }

    private void collisionChecker(List<ImageView> enemyList) {
        for (ImageView enemy : enemyList) {
            double collisionTolerance = (hero.getBoundsInParent().getHeight() / 2) * 0.3;

            double heroRadius = hero.getBoundsInParent().getHeight() / 2 - collisionTolerance;
            double heroX = hero.getBoundsInParent().getMinX() + hero.getBoundsInParent().getHeight() / 2;
            double heroY = hero.getBoundsInParent().getMinY() + hero.getBoundsInParent().getHeight() / 2;

            double enemyRadius = enemy.getBoundsInParent().getHeight() / 2 - collisionTolerance;
            double enemyX = enemy.getBoundsInParent().getMinX() + hero.getBoundsInParent().getHeight() / 2;
            double enemyY = enemy.getBoundsInParent().getMinY() + hero.getBoundsInParent().getHeight() / 2;

            boolean thereIsACollision = (enemyX - heroX) * (enemyX - heroX) + (heroY - enemyY) * (heroY - enemyY)
                    <= (enemyRadius + heroRadius) * (enemyRadius + heroRadius);
            boolean enemyIsBigger = enemy.getBoundsInParent().getHeight() > hero.getBoundsInParent().getHeight();
            boolean isVisible = enemy.isVisible();

            if (thereIsACollision && !enemyIsBigger && isVisible && !gameOver) {
                currentScale += SCALE_INCREASE_FACTOR;
                hero.setScaleX(currentScale);
                hero.setScaleY(currentScale);
                enemy.setVisible(false);
                score++;
            } else if (thereIsACollision && enemyIsBigger && isVisible) {
                gameOver = true;
            }
        }
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

        if (destinationX - hero.getBoundsInParent().getWidth() / 2 > 0 &&
                destinationX + hero.getBoundsInParent().getWidth() < SCREEN_WIDTH &&
                destinationY - hero.getBoundsInParent().getHeight() / 2 > 0 &&
                destinationY + hero.getBoundsInParent().getHeight() < SCREEN_HEIGHT) {
            hero.relocate(destinationX - currentX, destinationY - currentY);
        }
    }

    private void spawnEnemy(Group arena, double heroCurrentScale, List<ImageView> enemyList) {
        Random r = new Random();
        double randomScaleFactor = r.nextInt(3) * 0.1;

        ImageView enemy;
        if (r.nextInt(2) == 1) {
            friendImage = new Image(FRIENDS_IMAGE_LOC);
            enemy = new ImageView(friendImage);
            enemy.setScaleX(heroCurrentScale - randomScaleFactor);
            enemy.setScaleY(heroCurrentScale - randomScaleFactor);

//            //Rotating friend
//            RotateTransition rt = new RotateTransition(Duration.millis(2500),enemy);
//            rt.setByAngle(360);
//            rt.setCycleCount(1000000000);
//            rt.setAutoReverse(true);
//            rt.play();

            int randomPlacement = r.nextInt(4);
            if (randomPlacement == 0) {
                enemy.relocate(0, SCREEN_HEIGHT / 2);
            } else if (randomPlacement == 1) {
                enemy.relocate(SCREEN_WIDTH / 2, 0);
            } else if (randomPlacement == 2) {
                enemy.relocate(SCREEN_WIDTH, SCREEN_HEIGHT / 2);
            } else if (randomPlacement == 3) {
                enemy.relocate(SCREEN_WIDTH / 2, SCREEN_HEIGHT);
            }

            arena.getChildren().add(enemy);
            enemyList.add(enemy);
        } else {
            enemyImage = new Image(ENEMIES_IMAGE_LOC);
            enemy = new ImageView(enemyImage);
            enemy.setScaleX(heroCurrentScale + randomScaleFactor);
            enemy.setScaleY(heroCurrentScale + randomScaleFactor);

//            //Rotating enemy
//            RotateTransition rt = new RotateTransition(Duration.millis(2500),enemy);
//            rt.setByAngle(360);
//            rt.setCycleCount(1000000000);
//            rt.setAutoReverse(true);
//            rt.play();

            int randomPlacement = r.nextInt(4);
            if (randomPlacement == 0) {
                enemy.relocate(0, 0);
            } else if (randomPlacement == 1) {
                enemy.relocate(SCREEN_WIDTH, SCREEN_HEIGHT);
            } else if (randomPlacement == 2) {
                enemy.relocate(SCREEN_WIDTH, 0);
            } else if (randomPlacement == 3) {
                enemy.relocate(0, SCREEN_HEIGHT);
            }

            arena.getChildren().add(enemy);
            enemyList.add(enemy);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}