package com.example.crapsgame;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi") public class GameActivity extends Activity {
	
	private static final String TAG = "BENNY_GAME_ACTIVITY";

	//private ArrayList<String>fileNameList = getIntent().getExtras().getStringArrayList("filenames");
	//String dieSize = getIntent().getExtras().getString("die_size");
	
	//CharSequence dieSize = getIntent().getExtras().getCharSequence(Intent.EXTRA_TEXT);
	private int wins;
	private int losses;
	private int totalThrows;
	private int totalGames;
	private int totalPlayers;
	
	private String playerTag;
	
	private SharedPreferences top10Players;
	private Handler handler;
	
	private LinearLayout playerGameSummaryLinearLayout;
	private TextView playerSummaryTextView;
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
		playerSummaryTextView = (TextView)findViewById(R.id.playerSummaryTextView);
		
		playerGameSummaryLinearLayout = (LinearLayout)findViewById(R.id.playerGameSummaryLinearLayout);
		
		playerNameEditText = (EditText)findViewById(R.id.playerNameEditText);
		saveButton = (Button)findViewById(R.id.saveButton);
		
		top10Players = getSharedPreferences("players",MODE_PRIVATE);
		
		handler = new Handler();
		
		saveButton.setOnClickListener(savePlayerListener);
		playerNameEditText.addTextChangedListener(playerNameTextWatcher);
		
		playerGameSummaryLinearLayout.setVisibility(4);
		
		loadSummary();
	}
	
	private OnClickListener savePlayerListener = 
	new OnClickListener(){

		@Override
		public void onClick(View arg0) {

			SharedPreferences.Editor preferencesEditor = top10Players.edit();
			if(playerNameEditText.getText().length() <= 0)
				Toast.makeText(GameActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
			else
			{
				String playerName = playerNameEditText.getText().toString();
				preferencesEditor.putString(playerTag+"Name", playerName);
				preferencesEditor.putInt(playerTag+"Wins", wins);
				preferencesEditor.putInt(playerTag+"Losses", losses);
				preferencesEditor.putInt(playerTag+"Throws", totalThrows);
				preferencesEditor.putInt(playerTag+"Games", totalGames);
				preferencesEditor.putInt("totalPlayers", totalPlayers);
				preferencesEditor.apply();
				Log.e(TAG, "****player saved****");
				playerSummaryTextView.setText("Challenger " + playerName + " Summary:");
				Toast.makeText(GameActivity.this, "Player saved successfully", Toast.LENGTH_SHORT).show();
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						finish();
					}
					
				}, 3000);
			}
			
		}
		
	};
	
	private TextWatcher playerNameTextWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			playerGameSummaryLinearLayout.setVisibility(0);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private void loadSummary()
	{
		Log.e(TAG, "****loading player summary****");
		wins = getIntent().getIntExtra("wins", -1);
		losses = getIntent().getIntExtra("losses", -1);
		totalGames = getIntent().getIntExtra("game_number", -1);
		totalThrows = getIntent().getIntExtra("throws", -1);
		totalPlayers = getIntent().getIntExtra("players", -1);
		playerTag = getIntent().getStringExtra("player");
		
		Log.e(TAG, "***loaded " + wins + " wins, " + losses + "losses, " + totalGames + "games in total and " + totalThrows + " throws");
				
		playerTotalGamesTextView.setText("Total games: " + totalGames);
		playerHighestRollsTextView.setText("Highest rolls: " + totalThrows);
		playerLowestRollsTextView.setText("Lowest rolls: " + totalThrows);
		playerWinsLossesTextView.setText("Wins: " + wins + " Losses: " + losses);
		
	}
}
