package com.nhatran241.autodanhbai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nhatran241.autodanhbai.admin.AdminActivity;
import com.nhatran241.autodanhbai.client.ClientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Admin(View view) {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    public void Client(View view) {
        startActivity(new Intent(this, ClientActivity.class));
        finish();
    }
}