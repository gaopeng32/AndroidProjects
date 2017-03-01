package com.pgao.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button startButton;
    Button playAgainButton;
    TextView timerTextView;
    TextView questionTextView;
    TextView resultTextView;
    TextView pointsTextView;
    RelativeLayout gameRelativeLayout;
    Random rand;
    ArrayList<Integer> answers;
    int locationOfCorrectAnswer;
    int score;
    int numOfQuestions;



    public void chooseAnswer(View view) {
        // Find the tagged button
        int buttonTagged = Integer.parseInt(view.getTag().toString());
        if (buttonTagged == locationOfCorrectAnswer) {
            // correct
            score++;
            resultTextView.setText("Correct!");
        }
        else {
            resultTextView.setText("Wrong!");
        }

        numOfQuestions++;
        pointsTextView.setText(score + "/" + numOfQuestions);
        generateQuestion();
    }

    public void start(View view) {
        // Hide the start button
        startButton.setVisibility(View.INVISIBLE);

        // Show the game layout
        gameRelativeLayout.setVisibility(View.VISIBLE);

        // Start the game
        resetGame();
    }

    public void playAgain(View view) {
        // Reset the game
        resetGame();
    }

    private void resetGame() {
        // Initial records
        score = 0;
        numOfQuestions = 0;
        timerTextView.setText("30s");
        pointsTextView.setText("0/0");
        resultTextView.setText("");
        enableAnswerButtons();

        // Hide the playagain button
        playAgainButton.setVisibility(View.INVISIBLE);

        // Generate a new question
        generateQuestion();

        // Start the timer
        new CountDownTimer(30000 + 100, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                // Display final results
                resultTextView.setText("Your score: " + score + "/" + numOfQuestions);

                // Update the timer text and bring back the playagain button
                timerTextView.setText("0s");
                playAgainButton.setVisibility(View.VISIBLE);

                // Disable the buttons
                disableAnswerButtons();
            }
        }.start();

    }

    private void disableAnswerButtons() {
        for (int i = 0; i < 4; i++) {
            getButton(i).setClickable(false);
        }
    }

    private void enableAnswerButtons() {
        for (int i = 0; i < 4; i++) {
            getButton(i).setClickable(true);
        }
    }


    private void generateQuestion() {
        // Generate question
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        int a = rand.nextInt(21); // 0 ~ 20
        int b = rand.nextInt(21);
        char x = randArithmeticSymbol(a, b); // arithmetic symbol

        questionTextView.setText(a + " " + x + " " + b);

        // Generate answers
        answers = new ArrayList<Integer>();
        locationOfCorrectAnswer = rand.nextInt(4);
        int correctAnswer = getCorrectAnswer(a, b, x);
        int incorrectAnswer;
        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswer) {
                answers.add(correctAnswer);
            }
            else {
                incorrectAnswer = randIncorrectAnswer(a, b, x);
                while (incorrectAnswer == correctAnswer) {
                    incorrectAnswer = randIncorrectAnswer(a, b, x); // ensure only one correct answer
                }
                answers.add(incorrectAnswer);
            }

            // Update buttons
            getButton(i).setText(Integer.toString(answers.get(i)));
        }
    }

    private char randArithmeticSymbol(int a, int b) {
        String symbols = "+-*/";
        char x = symbols.charAt(rand.nextInt(4));
        while (x == '/' && a % b != 0) {
            x = symbols.charAt(rand.nextInt(4));
        }
        return x;
    }

    private int randIncorrectAnswer(int a, int b, char x) {
        int incorrectAnswer = 0;
        switch (x) {
            case '+':
                incorrectAnswer = rand.nextInt(a + b + 1);
                break;
            case '-':
                incorrectAnswer = rand.nextInt(Math.max(a, b) + 1);
                if (rand.nextBoolean()) {
                    incorrectAnswer = -incorrectAnswer;
                }
                break;
            case '*':
                incorrectAnswer = rand.nextInt(a * b + 1);
                break;
            case '/':
                incorrectAnswer = rand.nextInt(Math.max(a, b) + 1);
                break;
            default:
                Log.i("Error", "Undefined symbol");
                break;
        }
        return incorrectAnswer;
    }

    private int getCorrectAnswer(int a, int b, char x) {
        int answer = 0;
        switch (x) {
            case '+':
                answer = a + b;
                break;
            case '-':
                answer = a - b;
                break;
            case '*':
                answer = a * b;
                break;
            case '/':
                answer = a / b;
                break;
            default:
                Log.i("Error", "Undefined symbol");
                break;
        }
        return answer;
    }


    private Button getButton(int index) {
        int[] buttonIDArray = {R.id.button0, R.id.button1, R.id.button2, R.id.button3};
        Button button = (Button) findViewById(buttonIDArray[index]);
        return button;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        gameRelativeLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
        rand = new Random();


    }
}
