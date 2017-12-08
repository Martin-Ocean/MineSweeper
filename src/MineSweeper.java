/*
* Program name: MineSweeper.java
* Author: Ming Gong
* Date: Dec 8th, 2017
* Description: Minesweeper game with original rules.
* */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;



public class MineSweeper extends Application {
    private static final int rows = 30;
    private static final int columns = 30;
    private static int cellSize = 30;
    private static board board;
    private static Scene scene;
    Label scoreLabel = new Label("SCORE");
    Label score = new Label ("0");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
         scene = new Scene(gameBoard());
         primaryStage.setScene(scene);
         primaryStage.getIcons().add(new Image("https://github.com/Martin-Ocean/my-avatar/blob/master/myAvatar%20200x200.png"));
         primaryStage.show();
    }

    private Parent win() {
        Pane root = new Pane();
        VBox vb = new VBox(10);
        Label words = new Label("Good Job, Your Score is:");
        Label finalScore = new Label(score.getText());
        Label again = new Label("Would you like to start over?");
        Button btn = new Button("Yes");
        btn.setOnMouseClicked(e -> scene.setRoot(gameBoard()));
        vb.getChildren().addAll(words,finalScore,again,btn);
        vb.setMargin(words,new Insets(50,50,20,50));
        vb.setMargin(btn, new Insets(20,50,50,50));
        vb.setAlignment(Pos.CENTER);
        root.getChildren().add(vb);
        return root;
    }

    private Parent lost() {
        Pane root = new Pane();
        VBox vb = new VBox(10);
        Label words = new Label("Would you like to restart?");
        Button btn = new Button("Yes");
        btn.setOnMouseClicked(e -> scene.setRoot(gameBoard()));
        vb.getChildren().addAll(words,btn);
        vb.setMargin(words,new Insets(50,50,20,50));
        vb.setMargin(btn, new Insets(20,50,50,50));
        vb.setAlignment(Pos.CENTER);
        root.getChildren().add(vb);
        return root;
    }

    private Parent gameBoard() {
        BorderPane bp = new BorderPane();


        Pane root = new Pane();
        root.setPrefSize(900, 900);
        board = new board(rows, columns, root, .2);

        VBox vb = new VBox(10);
        Button quit = new Button("Quit");
        quit.setOnMouseClicked(e -> (((Stage) quit.getScene().getWindow())).close());
        vb.getChildren().addAll(scoreLabel,score,quit);
        vb.setMargin(scoreLabel, new Insets(20,20,0,20));
        vb.setMargin(score, new Insets(0,20,20,20));
        vb.setAlignment(Pos.CENTER);



        bp.setCenter(root);
        bp.setRight(vb);
        return bp;
    }




    class board {
        private int columnCells, rowCells;
        private double difficulty;
        private Pane root;
        private cell[][] board;

        public board (int columnCells, int rowCells, Pane root, Double difficulty) {
            this.columnCells = columnCells;
            this.rowCells = rowCells;
            this.root = root;
            this.difficulty = difficulty;
            this.board = new  cell[rowCells][columnCells];
            populateBoard();
            editLabel();
        }

        void populateBoard () {
            for (int row= 0; row < rowCells; row++) {
                for (int col = 0; col < columnCells; col++) {
                    cell cell = new cell(row,col, Math.random() < difficulty, 0);
                    board[row][col] = cell;
                    root.getChildren().add(cell);
                }
            }
        }

        void editLabel() {
            for (int i = 0; i < rowCells; i++) {
                for (int j = 0; j < columnCells; j++) {
                    int count = 0;
                    int row = i - 1;
                    int col = j - 1;
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 3; l++) {
                            if (row >= 0 && row+k < rows && col >= 0 && col +l< columns) {
                                if (board[row + k][col + l].hasBomb) {
                                    count++;
                                }
                            }
                        }
                    }

                    if (board[i][j].hasBomb) {
                        count--;
                    }

                    if (count > 0 && !(board[i][j].hasBomb)) {
                        board[i][j].surBombs = count;
                        board[i][j].cellText.setText(String.valueOf(count));
                    }
                }
            }
        }
    }


    class cell extends StackPane {
        private int row, col, y, surBombs;
        private boolean hasBomb;
        private boolean solved;


        private Rectangle rectangle = new Rectangle(cellSize-.3,cellSize-.3);
        private Label cellText = new Label();

        public boolean isSolved() {
            return solved;
        }

        public cell(int row, int col, boolean hasBomb, int surBombs) {
            this.row = row;
            this.col = col;
            this.hasBomb = hasBomb;
            this.surBombs = surBombs;
            this.solved = false;
            rectangle.setStroke(Color.WHEAT);
            rectangle.setStrokeWidth(.15);
            rectangle.setFill(Color.GREY);
            cellText.setText(hasBomb ? "B" : "");
            cellText.setVisible(false);

            getChildren().addAll(rectangle, cellText);
            setTranslateX(row * 30);
            setTranslateY(col * 30);

            setOnMouseClicked(e -> {
                solve();
                score();
                if (finish()) win();
            });

        }


        void solve() {

            if (solved)
                return;

            if (hasBomb) {
                score.setText("-1");
                scene.setRoot(lost());
                return;
            }

            solved = true;
            cellText.setVisible(true);
            rectangle.setFill(null);

            if (cellText.getText().isEmpty()) {
                int row = this.row-1, col = this.col-1;

                for (int i = 0; i < 3 ; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (row >= 0 && row+i < rows && col >= 0 && col+j < columns) {
                            cell cell = board.board[row+i][col+j];
                            cell.solve();
                        }
                    }
                }
            }
        }
    }

    private void score() {
        int score = 0, row = board.board.length, col = board.board[0].length;
        cell[][] board = MineSweeper.board.board;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j].isSolved()) {
                    score++;
                }
            }
        }
        this.score.setText(String.valueOf(score));
    }

    private boolean finish() {
        boolean win = true;
        int row = board.board.length, col = board.board[0].length;
        cell[][] board = MineSweeper.board.board;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j].hasBomb) {
                    win = false;
                }
            }
        }
        return win;
    }
}
