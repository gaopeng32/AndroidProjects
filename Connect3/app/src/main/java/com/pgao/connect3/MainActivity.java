package com.pgao.connect3;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int activePlayer = 0; // 0 for yellow, 1 for red
    private int[] gamesState = {2, 2, 2, 2, 2, 2, 2, 2, 2}; // 2 for unplayed
    private int[][] winningPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};
    boolean gameIsActive = true;


    public void dropIn(View view) {
        // Get the tapped cubic
        ImageView counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        // If the cubic has not been tapped before and the game is active
        if (gamesState[tappedCounter] == 2 && gameIsActive) {
            // Mark the tagged cubic
            gamesState[tappedCounter] = activePlayer;

            // Animation
            counter.setTranslationY(-1000f); // move to top of the screen

            if (activePlayer == 0) {
                counter.setImageResource(R.drawable.yellow);
                activePlayer = 1;
            }
            else {
                counter.setImageResource(R.drawable.red);
                activePlayer = 0;
            }

            counter.animate().translationYBy(1000f).rotation(360).setDuration(300); // bring the image back

            // Check win
            for (int[] winningPosition: winningPositions) {
                if (checkWin(winningPosition)) {
                    resetGame(gamesState[winningPosition[0]]);
                }
            }

            // Check draw
            if (checkDraw()) {
                resetGame(2); // no winner
            }
        }
    }

    private boolean checkWin(int[] winningPosition) {
        return gamesState[winningPosition[0]] == gamesState[winningPosition[1]]
                && gamesState[winningPosition[1]] == gamesState[winningPosition[2]]
                && gamesState[winningPosition[0]] != 2;
    }

    private boolean checkDraw(){
        for (int counterState: gamesState) {
            if (counterState == 2) {
                return false;
            }
        }
        return true;
    }

    private void resetGame(int winner) {
        // Get the winner
        gameIsActive = false;
        String winnerMessageText = "";
        int backgroundColor;

        if (winner == 0) {
            winnerMessageText = "Yellow has won!";
            backgroundColor = Color.YELLOW;
        }
        else if (winner == 1) {
            winnerMessageText = "Red has won!";
            backgroundColor = Color.RED;
        }
        else {
            winnerMessageText = "It's a draw";
            backgroundColor = Color.GREEN;
        }

        // Display winner message
        TextView winnerMessage = (TextView)findViewById(R.id.winnerMessage);
        winnerMessage.setText(winnerMessageText);

        // Pop-up the "play again" button
        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.VISIBLE);
        layout.setBackgroundColor(backgroundColor);
    }


    public void playAgain(View view) {
        // Reset the game state
        activePlayer = 0;
        for (int i = 0; i < gamesState.length; i++) {
            gamesState[i] = 2;
        }
        gameIsActive = true;

        // Reset the layout
        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.INVISIBLE);

        // Reset images
        GridLayout gridlayout = (GridLayout)findViewById((R.id.gridLayout));
        for (int i = 0; i < gridlayout.getChildCount(); i++) {
            ImageView imageView = (ImageView)gridlayout.getChildAt(i);
            imageView.setImageResource(0);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
