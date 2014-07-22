package com.example.crapsgame;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	
	private static final String TAG = "BENNY_GAME_ACTIVITY";

	//private ArrayList<String>fileNameList = getIntent().getExtras().getStringArrayList("filenames");
	//String dieSize = getIntent().getExtras().getString("die_size");
	
	//CharSequence dieSize = getIntent().getExtras().getCharSequence(Intent.EXTRA_TEXT);
	private int wins;
	private int losses;
	private int totalThrows;
	private int totalGames;
	private int player;
	
	private SharedPreferences top10Players;
	
	private TextView playerTotalGamesTextView;
	private TextView playerHighestRollsTextView;
	private TextView playerLowestRollsTextView;
	private TextView playerWinsLossesTextView;
	private EditText playerNameEditText;
	private Button saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		playerTotalGamesTextView = (TextView)findViewById(R.id.playerTotalGamesTextView);
		playerHighestRollsTextView = (TextView)findViewById(R.id.playerHighestRollsTextView);
		playerLowestRollsTextView = (TextView)findViewById(R.id.playerLowestRollsTextView);
		playerWinsLossesTextView = (TextView)findViewById(R.id.playerWinsLossesTextView);
		
		playerNameEditText = (EditText)findViewById(R.id.playerNameEditText);
		saveButton = (Button)findViewById(R.id.saveButton);
		
		top10Players = getSharedPreferences("players",MODE_PRIVATE);
		
		saveButton.setOnClickListener(savePlayerListener);
		
		loadSummary();
	}
	
	private OnClickListener savePlayerListener = 
	new OnClickListener(){

		@Override
		public void onClick(View arg0) {

			String playerTag = "Challenger" + player;
			SharedPreferences.Editor preferencesEditor = top10Players.edit();
			if(playerNameEditText.getText().length() <= 0)
				Toast.makeText(GameActivity.this, "Please enter your name", Toast.LENGTH_SHORT);
			else
			{
				String playerName = playerNameEditText.getText().toString();
				preferencesEditor.putString(playerTag+"Name", playerName);
				preferencesEditor.putInt(playerTag+"Wins", wins);
				preferencesEditor.putInt(playerTag+"Losses", losses);
				preferencesEditor.putInt(playerTag+"Throws", totalThrows);
				preferencesEditor.putInt(playerTag+"Games", totalGames);


			}
			
		}
		
	};
	
	private void loadSummary()
	{
		Log.e(TAG, "****loading player summary****");
		wins = getIntent().getIntExtra("wins", -1);
		losses = getIntent().getIntExtra("losses", -1);
		totalGames = getIntent().getIntExtra("game_number", -1);
		totalThrows = getIntent().getIntExtra("throws", -1);
		player = getIntent().getIntExtra("players", -1);
		
		Log.e(TAG, "***loaded " + wins + " wins, " + losses + "losses, " + totalGames + "games in total and " + totalThrows + " throws");
				
		playerTotalGamesTextView.setText("Total games: " + totalGames);
		playerHighestRollsTextView.setText("Highest rolls: " + totalThrows);
		playerLowestRollsTextView.setText("Lowest rolls: " + totalThrows);
		playerWinsLossesTextView.setText("Wins: " + wins + " Losses: " + losses);
		
	}
}
