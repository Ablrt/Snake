package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;


public class Game extends Application {
    private static final int WINDOW_HEIGHT = 800;
    private static final int WINDOW_WIDTH = 800;
    private static final int UNIT_SIZE = 40;
    private static int delay = 100;
    private static boolean alive;
    private char direction = 'L';
    private ArrayList<Rectangle> snake = new ArrayList<>();
    private Rectangle apple;
    private int count = 0;

    public int getCount(){
        return count;
    }


    private Stage window;
    private Group layout;

    private AnimationTimer timer = new AnimationTimer() {
        long lastUpdate = 0;
        @Override

        public void handle(long now) {
            if(now - lastUpdate >= delay * 1000000) {
                move();
                lastUpdate = now;
            }
        }
    };

    private EventHandler<KeyEvent> keyListener = new EventHandler<KeyEvent>() {

        // Change the direction of the snake with the arrow keys

        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.LEFT && direction != 'R'){
                direction = 'L';
            }
            if(event.getCode() == KeyCode.UP && direction != 'D'){
                direction = 'U';
            }
            if(event.getCode() == KeyCode.RIGHT && direction != 'L'){
                direction = 'R';
            }
            if(event.getCode() == KeyCode.DOWN && direction != 'U'){
                direction = 'D';
            }
            event.consume();
        }
    };

    // Method to check collisions with the apple and the body of the snake
    private boolean collision(Rectangle a, Rectangle b){
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    // Method checking if the snake has hit the wall
    public boolean wallCollision(){
        return (snake.get(0).getX() < 0 || snake.get(0).getX() > WINDOW_WIDTH - UNIT_SIZE
                || snake.get(0).getY() < 0 || snake.get(0).getY() > WINDOW_HEIGHT - UNIT_SIZE);
    }

    public Rectangle createSnakeBlock(double x, double y){
        Rectangle block = new Rectangle(x,y,UNIT_SIZE,UNIT_SIZE);
        block.setFill(Color.GREEN);
        block.setArcWidth(12);
        block.setArcHeight(12);
        return block;
    }

    public void move(){
        if(alive){
            double x = snake.get(0).getX();
            double y = snake.get(0).getY();
            double xOld,yOld;

            if(wallCollision()){
                alive = false;
                return;
            }

            if (direction == 'L') {
                snake.get(0).setX(x - UNIT_SIZE);
            }
            if (direction == 'U') {
                snake.get(0).setY(y - UNIT_SIZE);
            }
            if (direction == 'R') {
                snake.get(0).setX(x + UNIT_SIZE);
            }
            if (direction == 'D') {
                snake.get(0).setY(y + UNIT_SIZE);
            }
            for (int i = 1; i < snake.size(); i++) {
                xOld = snake.get(i).getX();
                yOld = snake.get(i).getY();
                snake.get(i).setX(x);
                snake.get(i).setY(y);
                x = xOld;
                y = yOld;
            }
            if(wallCollision()){
                alive = false;
                snake.get(0).setX(x);
                snake.get(0).setY(y);
                timer.stop();
                GameOver.display(window, count);
                return;
            }

            checkLife();
            eat(x,y);
        }
    }

    public void eat(double x, double y){
        if(collision(snake.get(0), apple)){
            count++;
            Rectangle tail = createSnakeBlock(x,y);
            snake.add(tail);
            generateApple();
            layout.getChildren().add(snake.get(snake.size() - 1));
        }
    }

    public void checkLife(){
        for (int i = 1; i < snake.size(); i++) {
            if(collision(snake.get(0), snake.get(i))) {
                alive = false;
                timer.stop();
                GameOver.display(window, count);
            }
        }
    }

    public void generateApple(){
        // Store all cells of the board in a hashmap
        HashMap<String, Integer> cells = new HashMap<>();
        for (int i = 0; i <= WINDOW_WIDTH - UNIT_SIZE; i += UNIT_SIZE) {
            for (int j = 0; j <= WINDOW_HEIGHT - UNIT_SIZE; j += UNIT_SIZE)
                cells.put(i + " " + j, 1);

        }

        // Remove the cells are a occupied with snake's body from the hashmap
        for (Rectangle s : snake)
            cells.remove((int)s.getX() + " " + (int)s.getY());

        // Wining condition
        if(cells.size() == 0)
            System.out.println("End");

        // Get a random cell position from the hashmap and put the apple there
        Random rd = new Random();
        int n = rd.nextInt(cells.size());
        Iterator keyIter = cells.keySet().iterator();
        while (n != 0){
            keyIter.next();
            n--;
        }
        createApple((String)keyIter.next());
    }

    // change the x and y coordinates of the apple
    public void createApple(String xy){
        String[] temp = xy.split(" ");
        int x = Integer.parseInt(temp[0]);
        int y = Integer.parseInt(temp[1]);
        apple.setX(x);
        apple.setY(y);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        alive = true;
        window = primaryStage;
        initialize();

        timer.start();

        //Set the scene
        Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT, Color.BLACK);
        scene.setOnKeyPressed(keyListener);

        window.setTitle("Snake");
        window.setScene(scene);
        window.show();
    }

    // Initializing the first elements of the game
    public void initialize(){
        int startingX = WINDOW_WIDTH/2;
        int startingY = WINDOW_HEIGHT/2;

        // The first 3 blocks of the snake
        snake.add(createSnakeBlock(startingX,startingY));
        snake.add(createSnakeBlock(startingX + UNIT_SIZE, startingY));
        snake.add(createSnakeBlock(startingX + 2*UNIT_SIZE, startingY));

        // Creating apple
        apple = new Rectangle(240,160, UNIT_SIZE,UNIT_SIZE);
        apple.setFill(Color.RED);
        apple.setArcHeight(40);
        apple.setArcWidth(40);

        layout = new Group();
        layout.getChildren().addAll(snake);
        layout.getChildren().add(apple);
    }

    public static void main(String args) {
        launch(args);
    }
}
