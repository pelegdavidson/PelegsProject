package com.example.peleg.pelegsproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.btn_newevent:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_upcoming:

                break;
            case R.id.btn_history:

                break;
            case R.id.btn_logout:

                break;
            case R.id.btn_changeuser:
                break;
        }
    }
}
