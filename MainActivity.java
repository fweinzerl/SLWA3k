package com.example.schnitzel.testapplication;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //actually constant but not
    public static int screenWidth;
    public static int screenHeight;

    //table rows from upper half
    private TableLayout resultsTable;
    //private TableRow[] results = new TableRow[3];
    private TableRow[] input = new TableRow[2];
    private Button btnRound;
    private Button btnFinish;

    private static int noP = 3; //number of players
    private static int fixedColumnWidth1 = 10; //1st
    private static int fixedColumnWidthn = 90/noP-1; //n-th
    private static int fixedRowHeight = 9;//percent of screenheight

    private int[][] points = new int[60/noP][noP];
    private int curRound = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.title_screen);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    public void startGame(View v){
        String nbPlayers = ((EditText)findViewById(R.id.nbPlayers)).getText().toString();
        try{
            noP = Integer.parseInt(nbPlayers);
            if(noP < 1){
                simpleToast("Dann brauchsch ja gar ned spielen!");
                return;
            }
            if(noP > 6){
                simpleToast("Des sin a bissle viel O.O");
                return;
            }
            fixedColumnWidthn = 90/noP-1; //n-th
            points = new int[60/noP][noP];
        }catch(NumberFormatException e){}

        curRound = 1;

        setContentView(R.layout.content_main);
        initGame();
    }

    public void initGame(){
        TableRow.LayoutParams tableLayoutParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //header
        TableRow row = new TableRow(this);
        resultsTable = (TableLayout) findViewById(R.id.resultsTable);
        //row.setLayoutParams(tableLayoutParams);
        row.setGravity(Gravity.CENTER);
        row.setBackgroundColor(Color.rgb(230, 230, 255));
        row.addView(makeTextView("Rd", fixedColumnWidth1, fixedRowHeight));
        for(int i = 0; i < noP; i++) {
            row.addView(makeBorderVert(Color.BLACK, 1, fixedRowHeight));
            row.addView(makeEditText("Sp" + (i + 1), fixedColumnWidthn, fixedRowHeight, InputType.TYPE_CLASS_TEXT));
        }
        resultsTable.addView(row);

        //table body (results)
        for(int j = 0; j < 3; j++) {
            row = new TableRow(this);
            //row.setLayoutParams(tableLayoutParams);
            row.setGravity(Gravity.CENTER);
            row.setBackgroundColor(Color.WHITE);
            row.addView(makeTextView("-", fixedColumnWidth1, fixedRowHeight));
            for (int i = 0; i < noP; i++) {
                row.addView(makeBorderVert(Color.BLACK, 1, fixedRowHeight));
                row.addView(makeTextView("-", fixedColumnWidthn, fixedRowHeight));
            }
            resultsTable.addView(row);
        }

        //space
        row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        row.addView(makeTextView("", fixedColumnWidth1, 14));
        resultsTable.addView(row);

        //input area
        input[0] = new TableRow(this);
        input[0].setBackgroundColor(Color.WHITE);
        input[0].addView(makeTextView("s:", fixedColumnWidth1, fixedRowHeight));
        for(int i = 0; i < noP; i++) {
            input[0].addView(makeBorderVert(Color.WHITE, 1, fixedRowHeight));
            input[0].addView(makeEditText("", fixedColumnWidthn, fixedRowHeight, InputType.TYPE_CLASS_NUMBER));
        }
        resultsTable.addView(input[0]);

        input[1] = new TableRow(this);
        input[1].setBackgroundColor(Color.WHITE);
        input[1].addView(makeTextView("m:", fixedColumnWidth1, fixedRowHeight));
        for(int i = 0; i < noP; i++) {
            input[1].addView(makeBorderVert(Color.WHITE, 1, fixedRowHeight));
            input[1].addView(makeEditText("", fixedColumnWidthn, fixedRowHeight, InputType.TYPE_CLASS_NUMBER));
        }
        resultsTable.addView(input[1]);

        //buttons
        btnRound = (Button)findViewById(R.id.btn_round);
        btnRound.setText("Runde 1");

        btnFinish = (Button)findViewById(R.id.btn_finish);
        btnFinish.setText("Game Over");
    }

    public void nextRound(View v){
        int rowMax = -100000, rowMin = 100000;

        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);
        row.setBackgroundColor(Color.WHITE);
        row.addView(makeTextView(String.valueOf(curRound), fixedColumnWidth1, fixedRowHeight));
        for(int i = 1; i < noP+1; i++) {
            row.addView(makeBorderVert(Color.BLACK, 1, fixedRowHeight));
            int curPoints = (curRound == 1)?0:points[curRound-2][i-1];//TODO
            int predicted = 0;
            int made = 0;
            try {
                predicted = Integer.parseInt(((EditText) input[0].getChildAt(2 * i)).getText().toString());
                made = Integer.parseInt(((EditText) input[1].getChildAt(2 * i)).getText().toString());
            }catch(NumberFormatException e){
                simpleToast("Gib do was gscheids ei! Jetzt isch zspät...");
            }
            if(predicted == made)
                curPoints += 20 + made * 10;
            else
                curPoints -= Math.abs(made-predicted) * 10;
            row.addView(makeTextView(String.valueOf(curPoints), fixedColumnWidthn, fixedRowHeight));

            if(i == 1) rowMax = rowMin = curPoints;
            else if(rowMax < curPoints) rowMax = curPoints;
            else if(rowMin > curPoints) rowMin = curPoints;

            points[curRound-1][i-1] = curPoints;
        }

        for(int i = 0; i < noP; i++){
            int green = (points[curRound-1][i]-rowMin) * 255 / (rowMax-rowMin);
            row.getChildAt(i*2+2).setBackgroundColor(Color.rgb(255-green, 255, 255-green));
        }

        resultsTable.removeViewAt(1);
        resultsTable.addView(row, 3);

        if(curRound == points.length){
            LinearLayout ll = (LinearLayout)findViewById(R.id.container_btns);
            ll.removeViewAt(1);
            ll.removeViewAt(1);
        }else {
            btnRound.setText("Runde " + (++curRound));
        }
        resetInputs();
    }

    public void finishGame(View v){
        setContentView(R.layout.title_screen);
    }

    private void resetInputs(){
        for(int i = 0; i < input.length; i++)
            for(int j = 0; j < input[i].getChildCount(); j++){
                if(input[i].getChildAt(j) instanceof EditText)
                    ((EditText)input[i].getChildAt(j)).setText("");
            }
    }

    private void simpleToast(String text){
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private TextView makeTextView(String text, int widthInPercentOfScreenWidth, int heightInPercentOfScreenHeight){
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(20);
        tv.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        tv.setHeight(heightInPercentOfScreenHeight * screenHeight / 100);
        return tv;
    }

    private EditText makeEditText(String text, int widthInPercentOfScreenWidth, int heightInPercentOfScreenHeight, int inputType) {
        EditText et = new EditText(this);
        et.setText(text);
        et.setTextColor(Color.BLACK);
        et.setRawInputType(inputType);
        et.setTextSize(20);
        et.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        et.setHeight(heightInPercentOfScreenHeight * screenHeight / 100);
        return et;
    }

    private TextView makeBorderVert(int color, int widthInPercentOfScreenWidth, int heightInPercentOfScreenHeight) {
        TextView tv = new TextView(this);
        tv.setBackgroundColor(color);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(20);
        tv.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        tv.setHeight(heightInPercentOfScreenHeight * screenHeight / 100);
        return tv;
    }
}
