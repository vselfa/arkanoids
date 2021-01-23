package com.example.myarkanoid;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MyArkanoid extends AppCompatActivity {

    public static MyArkanoidSurfaceView myArkanoidSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myArkanoidSurfaceView = new MyArkanoidSurfaceView(this);
        setContentView(myArkanoidSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SurfaceView", "onPause");
        // Per evitar l'error a l'eixir de l'aplicació
        if (myArkanoidSurfaceView != null)
            myArkanoidSurfaceView.stopThread();
    }
}