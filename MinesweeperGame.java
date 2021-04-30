package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
//        isGameStopped = false;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {

        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {

                if (gameField[y][x].isMine == false) {

                    List<GameObject> result = getNeighbors(gameField[y][x]);
                    int counter = 0;

                    for (int i = 0; i < result.size(); i++) {

                        if (result.get(i).isMine == true) {
                            counter++;
                        }

                    }
                    gameField[y][x].countMineNeighbors = counter;
                }
            }
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped == true) {
            restart();
        }
        else {openTile(x, y);}
    }

    private void openTile(int x, int y) {

        if (isGameStopped == true && countClosedTiles != countMinesOnField) {
            gameOver();
        }

        else if (isGameStopped == true || gameField[y][x].isFlag == true || gameField[y][x].isOpen == true) {
        }

        else if (gameField[y][x].isMine == false && gameField[y][x].countMineNeighbors == 0) {  //Если не мина и нет соседей с минами

                gameField[y][x].isOpen = true;
                setCellColor(x, y, Color.GREEN);
                setCellValue(x, y, "");
                countClosedTiles--;
                score += 5;
                setScore(score);


            List<GameObject> result = getNeighbors(gameField[y][x]);  //Получаем список соседних ячеек

            for (int i = 0; i < result.size(); i++) {                 //Проверяем каждую из них
                int x1 = result.get(i).x;
                int y1 = result.get(i).y;

                openTile(x1, y1);                                 //Ячейка отправляется на перепроверку
            }

        }

        else if (gameField[y][x].isMine == false && gameField[y][x].countMineNeighbors != 0) {  // Если не мина, но есть соседи с миной
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            gameField[y][x].isOpen = true;
            setCellColor(x, y, Color.GREEN);
            countClosedTiles--;
            score += 5;
            setScore(score);
        }

        else if (gameField[y][x].isMine == true && gameField[y][x].isFlag == false) {               //Если в ячейке есть мина

            gameField[y][x].isOpen = true;
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else {}
        if (countClosedTiles == countMinesOnField) {
            win();
        }
    }

    private void markTile (int x, int y) {
        if ((isGameStopped == true) && (countClosedTiles != countMinesOnField)) {
            gameOver();
        }

        else if (isGameStopped == true) {
            win();
        }

        else if (!gameField[y][x].isOpen && countFlags > 0 && !gameField[y][x].isFlag) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        }
        else if (gameField[y][x].isFlag == true) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);

        }
    }

    @Override
    public void onMouseRightClick (int x, int y) {
        markTile(x, y);
    }

    private void gameOver () {
        isGameStopped = true;
        showMessageDialog (Color.AQUA, "GAME OVER", Color.BLACK, 30);
    }

    private void win () {
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "CONGRATULATIONS\nYOU WIN A BIG COCK!!!", Color.BLACK, 30);
    }

    private void restart () {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }
}