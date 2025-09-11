package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView tvScore, tvTimer;
    private Button btnStart, btnClick;
    private int score = 0;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("点击游戏");
        }

        tvScore = findViewById(R.id.tv_score);
        tvTimer = findViewById(R.id.tv_timer);
        btnStart = findViewById(R.id.btn_start);
        btnClick = findViewById(R.id.btn_click);

        btnStart.setOnClickListener(v -> startGame());
        btnClick.setOnClickListener(v -> {
            score++;
            tvScore.setText("得分: " + score);
        });
    }

    private void startGame() {
        score = 0;
        tvScore.setText("得分: 0");
        btnClick.setEnabled(true);

        if (timer != null) timer.cancel();

        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("剩余时间: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("游戏结束! 最终得分: " + score);
                btnClick.setEnabled(false);
            }
        }.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
