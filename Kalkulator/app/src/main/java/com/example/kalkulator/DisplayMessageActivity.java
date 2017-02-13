package com.example.kalkulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {
    TextView textView;
    String history = "NO";
    final static String DELETE_FLAG = "DELETED?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.HISTORY_MESSAGE);
        textView = (TextView)findViewById(R.id.textView2);
        textView.setTextSize(40);
        textView.setText(message);
    }
    public void toMain(View w)
    {
        Log.i("Button: ","Back");
        Intent intent = new Intent();
        intent.putExtra(DELETE_FLAG, history);
        setResult(RESULT_OK,intent);
        finish();
    }
    public void clearHistory(View w)
    {
        Log.i("Button: ","Delete history");
        textView.setText("");
        history ="YES";

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(DELETE_FLAG,history);
        setResult(RESULT_OK,intent);
        finish();
    }
}
