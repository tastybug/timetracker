package com.tastybug.timetracker.gui.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tastybug.timetracker.R;

public class BottomSheetActivity extends ActionBarActivity implements View.OnClickListener {

    BottomSheetBehavior mBottomSheetBehavior;
    TextView textfeld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomsheet);

        View bottomSheet = findViewById( R.id.bottom_sheet );
//        Button button1 = (Button) findViewById( R.id.button_1 );
//        Button button2 = (Button) findViewById( R.id.button_2 );
//        Button button3 = (Button) findViewById( R.id.button_3 );
        textfeld = (TextView) findViewById(R.id.textfeld);

//        button1.setOnClickListener(this);
//        button2.setOnClickListener(this);
//        button3.setOnClickListener(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        setupActionBar();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fragment_project_list, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.button_1: {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.button_2: {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            }
            case R.id.button_3: {
                textfeld.setText(">>" + System.currentTimeMillis());
                break;
            }
        }
    }
}
