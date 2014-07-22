package com.example.crapsgame;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String TAG = "BENNY_ACTIVITY";
	
	private List<String>diceImageList;
	private int rollSum;
	private int die1RollPoint;
	private int die2RollPoint;
	private int gameRollPoint;
	private int gameNumber;
	private int wins;
	private int losses;
	private int totalThrows;
	private int totalPlayers;
	private Boolean gameOver;
	private String gameStatus;
	private String imagesFolderName;
	private Random random;
	private Handler handler;
	private Animation previewAnimation;
	private Animation shakeAnimation;
	private SharedPreferences top10Players;
	
	private TextView titleTextView;
	private TextView selectTextView;
	private TextView rollValueTextView;
	private TextView rollPointTextView;
	private TextView gameStatusTextView;
	private TextView numOfGamesValueTextView;
	private TextView numOfRollsValueTextView;
	private TextView numOfWinsValueTextView;
	private TextView numOfLossesValueTextView;
	private TextView playerNameTextView;
	private RadioGroup dieSizeRadioGroup;
	private RadioButton smallRadioButton;
	private RadioButton largeRadioButton;
	private ImageView die1ImageView;
	private ImageView die2ImageView;
	private Button startButton;
	private LinearLayout crapsMainLinearLayout;
	private TableLayout gameStatsTableLayout;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imagesFolderName = "dice_images";
        diceImageList = new ArrayList<String>();
        random = new Random();
        handler = new Handler();
        previewAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        
        
        
        titleTextView = (TextView)findViewById(R.id.titleTextView);
        selectTextView = (TextView)findViewById(R.id.selectTextView);
        rollValueTextView = (TextView)findViewById(R.id.rollValueTextView);
        rollPointTextView = (TextView)findViewById(R.id.rollPointTextView);
        gameStatusTextView = (TextView)findViewById(R.id.gameStatusTextView);
        numOfGamesValueTextView = (TextView)findViewById(R.id.numOfGamesValueTextView);
        numOfRollsValueTextView = (TextView)findViewById(R.id.numOfRollsValueTextView);
        numOfWinsValueTextView = (TextView)findViewById(R.id.numOfWinsValueTextView);
        numOfLossesValueTextView = (TextView)findViewById(R.id.numOfLossesValueTextView);
        playerNameTextView = (TextView)findViewById(R.id.playerNameTextView);
        dieSizeRadioGroup = (RadioGroup)findViewById(R.id.dieSizeRadioGroup);
        smallRadioButton = (RadioButton)findViewById(R.id.smallRadioButton);
        largeRadioButton = (RadioButton)findViewById(R.id.largeRadioButton);
        die1ImageView = (ImageView)findViewById(R.id.die1ImageView);
        die2ImageView = (ImageView)findViewById(R.id.die2ImageView);
        startButton = (Button)findViewById(R.id.startButton);
        crapsMainLinearLayout = (LinearLayout)findViewById(R.id.crapsMainLinearLayout);
        gameStatsTableLayout = (TableLayout)findViewById(R.id.gameStatsTableLayout);
        
        dieSizeRadioGroup.setOnCheckedChangeListener(imageLoaderListener);
        startButton.setOnClickListener(startOrRollListener);
        
        
    }
    
    @Override protected void onResume()
    {
    	super.onResume();
        top10Players = getSharedPreferences("players",MODE_PRIVATE);
        String playerName = top10Players.getString("Challenger"+totalPlayers+"Name", "None");
        if(playerName != "None")
        {
        	Log.e(TAG, "*****Player " + playerName + " retrieved*****");
        	playerNameTextView.setText(playerName);
			Toast.makeText(MainActivity.this, "Player " + playerName + " updated in game stats!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private OnClickListener startOrRollListener = new OnClickListener()
    {

		@Override
		public void onClick(View v) {
			
			Button dynamicButton = (Button)v;
			
			switch(dynamicButton.getText().toString())
			{
			case "Begin Challenge":
				startGame();
				break;
			case "Roll Dice":
				rollDice();
				break;
				
			}
		}
    	
    };
    
    
    private OnCheckedChangeListener imageLoaderListener = new OnCheckedChangeListener()
    {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
		
			loadDieImageFiles();
		}
    	
    };
    
    private OnClickListener replayOrResetListener = new OnClickListener()
    {

		@Override
		public void onClick(View arg0) {
			replayGame();
		}
    	
    };
    
    private void loadDieImageFiles()
    {
    	LinearLayout.LayoutParams imageViewLayoutParams;
    	try
        {
    		String[] imageFiles = getAssets().list(imagesFolderName);
    		switch(dieSizeRadioGroup.getCheckedRadioButtonId())
        	{
        	case R.id.smallRadioButton:
        		Log.e(TAG, "small radio button");
        		if(diceImageList.isEmpty() || !diceImageList.get(0).contains("Small"))
        		{
        			diceImageList.clear();
        			for(String filePath : imageFiles)
        			{
        				if(filePath.contains("Small"))
        					diceImageList.add(filePath);
        			}
        		}
        		imageViewLayoutParams = new LinearLayout.LayoutParams(60, 60);
        		die1ImageView.setLayoutParams(imageViewLayoutParams);
        		die2ImageView.setLayoutParams(imageViewLayoutParams);
        		previewDice();
        		break;
        		
        		case R.id.largeRadioButton:
        			Log.e(TAG, "large radio button");
        			if(diceImageList.isEmpty() || diceImageList.get(0).contains("Small"))
        			{
        				diceImageList.clear();
        				for(String filePath : imageFiles)
        				{
        					if(!filePath.contains("Small"))
        						diceImageList.add(filePath);
        				}
        			}
        			imageViewLayoutParams = new LinearLayout.LayoutParams(100, 100);
            		die1ImageView.setLayoutParams(imageViewLayoutParams);
            		die2ImageView.setLayoutParams(imageViewLayoutParams);
        			previewDice();
        			break;
        		}
        }
    	
        catch(IOException e)
        {
        	Log.e(TAG, "Error loading image files", e);
        }
    }

    private void previewDice()
    {
    	Log.e(TAG, "preview dice");
		InputStream stream;
		
		if(smallRadioButton.isEnabled() || largeRadioButton.isEnabled())
		{
			try
			{
				stream = getAssets().open(imagesFolderName + "/" + diceImageList.get(random.nextInt(6)));
		    	Drawable flag = Drawable.createFromStream(stream, "die preview");
		    	die1ImageView.setImageDrawable(flag);
		    	die2ImageView.setImageDrawable(flag);
		    	die1ImageView.startAnimation(previewAnimation);
		    	die2ImageView.startAnimation(previewAnimation);
		    	
			}
			catch(IOException e)
			{
				Log.e(TAG, "Error generating preview", e);
			}
		}
		else
		{
			try
			{
				Drawable flag;
				
				stream = getAssets().open(imagesFolderName + "/" + diceImageList.get(die1RollPoint));
		    	flag = Drawable.createFromStream(stream, "die preview");
		    	die1ImageView.setImageDrawable(flag);
		    	
		    	stream = getAssets().open(imagesFolderName + "/" + diceImageList.get(die2RollPoint));
		    	flag = Drawable.createFromStream(stream, "die preview");
		    	die2ImageView.setImageDrawable(flag);
		    	
		    	die1ImageView.startAnimation(previewAnimation);
		    	die2ImageView.startAnimation(previewAnimation);
		    	
		    	die1RollPoint += 1;
		    	die2RollPoint += 1;
		    	
		    	Log.e(TAG, "Die 1: " + die1RollPoint + "//Die2 : " + die2RollPoint);
			}
			catch(IOException e)
			{
				Log.e(TAG, "Error generating preview", e);
			}
		}
    		
    }

    private void rollDice()
    {
    	Log.e(TAG, "roll dice");
    	++totalThrows;
    	numOfRollsValueTextView.setText(""+totalThrows);
    	die1RollPoint = random.nextInt(6);
    	die2RollPoint = random.nextInt(6);
    	previewDice();
    	computeRoll();
    	
    }
    
    private void computeRoll()
    {
    	rollValueTextView.setText("");
    	rollValueTextView.setTextColor(Color.BLACK);
    	gameStatusTextView.setText("");
    	rollSum = die1RollPoint + die2RollPoint;
    	handler.postDelayed(
    			new Runnable()
    			{

					@Override
					public void run() {
						rollValueTextView.setText("==> You rolled " + rollSum + "!");
					}
    				
    			}, 3000);
    	
    	if(totalThrows == 1)
    	{
    		Log.e(TAG, "compute first roll");
    		switch(rollSum)
        	{
        	case 7: case 11:
        		Log.e(TAG, "player wins");
        		gameRollPoint = rollSum;
        		gameStatus = "win";
        		updateGameStatus();
        		handler.postDelayed(
						new Runnable()
						{

							@Override
							public void run() {
								
								endGame();
								
							}
							
						}, 4500);
        		break;
        		
        	case 2: case 3: case 12:
        		Log.e(TAG, "player loses");
        		gameRollPoint = rollSum;
        		gameStatus = "lose";
        		updateGameStatus();
        		handler.postDelayed(
						new Runnable()
						{

							@Override
							public void run() {
								
								endGame();
								
							}
							
						}, 4500);
        		break;
        		
        	default:
        		Log.e(TAG, "player must roll point");
        		gameRollPoint = rollSum;
        		gameStatus = "roll";
        		updateGameStatus();
        		break;
        	}
    	}
    	else
    	{
    		Log.e(TAG, "compute roll " + totalThrows);
    		if(rollSum != 7)
    		{
    			if(rollSum == gameRollPoint)
    			{
    				Log.e(TAG, "player rolls point");
    				gameStatus = "win";
    				updateGameStatus();
    				handler.postDelayed(
    						new Runnable()
    						{

								@Override
								public void run() {
									
									endGame();
									
								}
    							
    						}, 4500);
    			}
    			else
    			{
    				gameStatus = "roll";
        			updateGameStatus();
    			}
    		}
    		else
    		{
    			Log.e(TAG, "player rolls 7");
    			gameStatus = "lose";
    			updateGameStatus();
				handler.postDelayed(
						new Runnable()
						{

							@Override
							public void run() {
								
								endGame();
								
							}
							
						}, 4500);
    		}
    	}
    		
    	
    }
    
    private void updateGameStatus()
    {
    	Log.e(TAG, "***game stats updated***");
    	rollPointTextView.setText(""+gameRollPoint);
    	handler.postDelayed(
    			new Runnable()
    			{

					@Override
					public void run() {
						
						switch(gameStatus)
				    	{
				    	case "win":
				    		gameStatusTextView.setTextColor(getResources().getColor(R.color.win));
				    		rollValueTextView.setTextColor(getResources().getColor(R.color.win));
							gameStatusTextView.setText("Challenger WINS!!!");
							gameStatsTableLayout.setBackgroundColor(getResources().getColor(R.color.win));
							++wins;
							numOfWinsValueTextView.setText(""+wins);
							break;
				    	case "lose":
				    		gameStatusTextView.setTextColor(getResources().getColor(R.color.lose));
				    		rollValueTextView.setTextColor(getResources().getColor(R.color.lose));
							gameStatusTextView.setText("House WINS!!!");
							gameStatsTableLayout.setBackgroundColor(getResources().getColor(R.color.lose));
							++losses;
							numOfLossesValueTextView.setText(""+losses);
							break;
				    	case "roll":
				    		gameStatusTextView.setTextColor(getResources().getColor(R.color.cont));
				    		rollValueTextView.setTextColor(getResources().getColor(R.color.cont));
							gameStatusTextView.setText("roll point below to win!");
							rollPointTextView.startAnimation(shakeAnimation);
							break;    		
				    	}
						
					}
    				
    			}, 4000);
    }
    
    

    private void hideDice()
    {
    	Log.e(TAG, "hide dice");
    	die1ImageView.setImageResource(R.drawable.abc_ic_commit_search_api_holo_dark);
    	die2ImageView.setImageResource(R.drawable.abc_ic_commit_search_api_holo_dark);
    }
    
    private void resetGame()
    {
    	Log.e(TAG, "*****GAME RESET*****");
    	gameNumber = 0;
    	wins = 0;
    	losses = 0;
    	
    	titleTextView.setText("Welcome, Craps Challenger!");
    	selectTextView.setText("select die size:");
    	smallRadioButton.setEnabled(true);
    	largeRadioButton.setEnabled(true);
    	smallRadioButton.setChecked(false);
    	largeRadioButton.setChecked(false);
    	startButton.setText("Begin Challenge");
    	startButton.setEnabled(true);
    	gameStatsTableLayout.setBackgroundColor(getResources().getColor(R.color.background_color));
    	
    	numOfGamesValueTextView.setText("");
    	numOfRollsValueTextView.setText("");
    	numOfWinsValueTextView.setText("");
    	numOfLossesValueTextView.setText("");
    	rollValueTextView.setText("");
    	rollPointTextView.setText("");
    	gameStatusTextView.setText("");
    }
    
    private void startGame()
    {
    	Log.e(TAG, "*****GAME START*****");
    	++gameNumber;
    	gameOver = false;
    	rollSum = 0;
    	die1RollPoint = 0;
    	die2RollPoint = 0;
    	gameRollPoint = 0;
    	totalThrows = 0;
    	titleTextView.setText("New Craps Challenge");
    	selectTextView.setText("Size:");
    	smallRadioButton.setEnabled(false);
    	largeRadioButton.setEnabled(false);
    	rollValueTextView.setText("");
    	rollPointTextView.setText("");
    	gameStatusTextView.setText("");
    	numOfGamesValueTextView.setText(""+gameNumber);
    	numOfRollsValueTextView.setText(""+totalThrows);
    	numOfWinsValueTextView.setText(""+wins);
    	numOfLossesValueTextView.setText(""+losses);
    	gameStatsTableLayout.setBackgroundColor(getResources().getColor(R.color.background_color));
    	startButton.setText("Roll Dice");
    	
    	if(!startButton.isEnabled())
    	{
    		startButton.setEnabled(true);
    	}
    	
    	hideDice();
    	
    }

    private void endGame()
    {
    	Log.e(TAG, "*****GAME END*****");
    	gameOver = true;
    	startButton.setEnabled(false);
    	crapsMainLinearLayout.setOnClickListener(replayOrResetListener);
		handler.postDelayed(
    		new Runnable()
    		{

			@Override
			public void run() {
				replayGame();
			}
    		
    	}, 3000);
    }
    
    private void replayGame()
    {
    	Log.e(TAG, "*****REPLAY GAME?*****");
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.replay_title);
    	builder.setMessage(R.string.replay_msg);
    	builder.setCancelable(false);
    	builder.setPositiveButton(R.string.sure,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e(TAG, "*****restart game*****");
						startGame();
						
					}
				});
    	builder.setNeutralButton(R.string.reset,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveGameStats();
					}
				});
    	builder.setNegativeButton(R.string.no_thanks, null);
    	AlertDialog replayDialog = builder.create();
    	replayDialog.show();
    }
    
    private void saveGameStats()
    {
    	Log.e(TAG, "*****SAVE GAME?*****");
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.save_title);
    	builder.setMessage(R.string.save_msg);
    	builder.setCancelable(true);
    	builder.setPositiveButton(R.string.yes,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e(TAG, "*****totalPlayers: " + (++totalPlayers) + "******savegame*****");
						savePlayer();
					}
				});
    	builder.setNegativeButton(R.string.no, null);
    	AlertDialog saveDialog = builder.create();
    	saveDialog.show();
    	
    }
    
    private void savePlayer()
    {
    	Log.e(TAG, "*****SAVE PLAYER*****");
    	Intent getPlayerName = new Intent(MainActivity.this,GameActivity.class);
    	getPlayerName.putExtra("wins", wins);
    	getPlayerName.putExtra("losses", losses);
    	getPlayerName.putExtra("game_number", gameNumber);
    	getPlayerName.putExtra("throws", totalThrows);
    	getPlayerName.putExtra("players", totalPlayers);
    	startActivity(getPlayerName);
    }
}