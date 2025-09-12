package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvScore, tvTimer, tvInstruction, tvLevel;
    private Button btnStart, btnRed, btnGreen, btnBlue, btnYellow, btnBack;
    private int score = 0;
    private int level = 1;
    private int targetColor = 0;
    private CountDownTimer gameTimer;
    private Handler colorHandler;
    private Random random;
    private boolean gameRunning = false;
    private long colorShowTime = 2500; // 初始显示时间2.5秒
    private int combo = 0;
    private int correctAnswersInLevel = 0; // 当前等级答对的题目数
    private final int ANSWERS_PER_LEVEL = 5; // 每级需要答对5题才能升级

    private final int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    private final String[] colorNames = {"红色", "绿色", "蓝色", "黄色"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        random = new Random();
        colorHandler = new Handler();
        setupClickListeners();
    }

    private void initViews() {
        tvScore = findViewById(R.id.tv_score);
        tvTimer = findViewById(R.id.tv_timer);
        tvInstruction = findViewById(R.id.tv_instruction);
        tvLevel = findViewById(R.id.tv_level);

        btnStart = findViewById(R.id.btn_start);
        btnRed = findViewById(R.id.btn_red);
        btnGreen = findViewById(R.id.btn_green);
        btnBlue = findViewById(R.id.btn_blue);
        btnYellow = findViewById(R.id.btn_yellow);
        btnBack = findViewById(R.id.btn_back);

        // 初始状态
        tvScore.setText("得分: 0");
        tvLevel.setText("等级 1 (0/" + ANSWERS_PER_LEVEL + ")");
        tvInstruction.setText("准备好了吗？挑战你的反应速度！");
        tvTimer.setText("60秒反应速度挑战");

        setColorButtonsEnabled(false);
        resetButtonColors();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnStart.setOnClickListener(v -> {
            if (!gameRunning) {
                startGame();
            }
        });

        btnRed.setOnClickListener(v -> checkAnswer(0));
        btnGreen.setOnClickListener(v -> checkAnswer(1));
        btnBlue.setOnClickListener(v -> checkAnswer(2));
        btnYellow.setOnClickListener(v -> checkAnswer(3));
    }

    private void startGame() {
        // 重置游戏状态
        score = 0;
        level = 1;
        combo = 0;
        correctAnswersInLevel = 0;
        colorShowTime = 2500;
        gameRunning = true;

        tvScore.setText("得分: 0");
        tvLevel.setText("等级 1 (0/" + ANSWERS_PER_LEVEL + ")");
        tvInstruction.setText("游戏开始！准备...");
        btnStart.setEnabled(false);
        btnStart.setText("游戏中...");

        setColorButtonsEnabled(true);

        // 60秒游戏计时器
        gameTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimer.setText("剩余: " + seconds + "秒");
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();

        // 3秒准备时间后开始第一题
        colorHandler.postDelayed(this::nextColorChallenge, 1000);
    }

    private void nextColorChallenge() {
        if (!gameRunning) return;

        targetColor = random.nextInt(4);
        resetButtonColors();
        tvInstruction.setText("快点击: " + colorNames[targetColor] + "!");
        randomizeButtonColors();

        // 设置超时
        colorHandler.postDelayed(this::timeOut, colorShowTime);
    }

    private void resetButtonColors() {
        int grayColor = Color.parseColor("#CCCCCC");
        btnRed.setBackgroundColor(grayColor);
        btnGreen.setBackgroundColor(grayColor);
        btnBlue.setBackgroundColor(grayColor);
        btnYellow.setBackgroundColor(grayColor);
    }

    private void randomizeButtonColors() {
        Integer[] shuffledColors = {0, 1, 2, 3};
        for (int i = shuffledColors.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Integer temp = shuffledColors[i];
            shuffledColors[i] = shuffledColors[j];
            shuffledColors[j] = temp;
        }

        btnRed.setBackgroundColor(colors[shuffledColors[0]]);
        btnGreen.setBackgroundColor(colors[shuffledColors[1]]);
        btnBlue.setBackgroundColor(colors[shuffledColors[2]]);
        btnYellow.setBackgroundColor(colors[shuffledColors[3]]);

        btnRed.setTag(shuffledColors[0]);
        btnGreen.setTag(shuffledColors[1]);
        btnBlue.setTag(shuffledColors[2]);
        btnYellow.setTag(shuffledColors[3]);
    }

    private void checkAnswer(int buttonIndex) {
        if (!gameRunning) return;

        // 取消超时处理
        colorHandler.removeCallbacksAndMessages(null);

        Button[] buttons = {btnRed, btnGreen, btnBlue, btnYellow};
        int clickedColor = (Integer) buttons[buttonIndex].getTag();

        if (clickedColor == targetColor) {
            // 答对了
            combo++;
            correctAnswersInLevel++;

            // 立即禁用所有颜色按钮，防止连续点击
            setColorButtonsEnabled(false);

            // 计算得分：基础分 + 等级奖励 + 连击奖励
            int basePoints = 10;
            int levelBonus = (level - 1) * 5;
            int comboBonus = combo > 1 ? (combo - 1) * 3 : 0;
            int totalPoints = basePoints + levelBonus + comboBonus;

            score += totalPoints;
            tvScore.setText("得分: " + score);

            // 显示得分信息
            String scoreText = "正确! +" + totalPoints + "分";
            if (combo > 1) {
                scoreText += " (连击x" + combo + ")";
            }
            tvInstruction.setText(scoreText);

            // 检查是否可以升级
            if (correctAnswersInLevel >= ANSWERS_PER_LEVEL) {
                levelUp();
            } else {
                // 更新等级进度
                tvLevel.setText("等级 " + level + " (" + correctAnswersInLevel + "/" + ANSWERS_PER_LEVEL + ")");
            }

            // 答对后短暂停顿0.8秒，然后继续下一题
            colorHandler.postDelayed(() -> {
                if (gameRunning) {
                    setColorButtonsEnabled(true);  // 重新启用按钮
                    nextColorChallenge();  // 开始新的一题
                }
            }, 800);

        } else {
            // 答错了
            combo = 0;
            setColorButtonsEnabled(false);  // 禁用按钮
            tvInstruction.setText("错误! 应该是: " + colorNames[targetColor]);

            // 答错后停顿1.2秒，让玩家看清楚正确答案，然后开始新题
            colorHandler.postDelayed(() -> {
                if (gameRunning) {
                    setColorButtonsEnabled(true);  // 重新启用按钮
                    nextColorChallenge();  // 开始新的一题
                }
            }, 1200);
        }
    }

    private void timeOut() {
        if (!gameRunning) return;

        combo = 0;
        setColorButtonsEnabled(false);  // 禁用按钮
        tvInstruction.setText("太慢了! 应该点: " + colorNames[targetColor]);

        colorHandler.postDelayed(() -> {
            if (gameRunning) {
                setColorButtonsEnabled(true);  // 重新启用按钮
                nextColorChallenge();  // 开始新的一题
            }
        }, 1200);
    }

    private void levelUp() {
        level++;
        correctAnswersInLevel = 0;

        // 每升一级，反应时间减少150ms，但不少于1秒
        colorShowTime = Math.max(1000, colorShowTime - 150);

        tvLevel.setText("等级 " + level + " (0/" + ANSWERS_PER_LEVEL + ")");

        // 升级提示
        String levelUpMsg = "升级! 等级 " + level;
        if (colorShowTime > 1000) {
            levelUpMsg += " - 速度加快!";
        } else {
            levelUpMsg += " - 极限速度!";
        }

        Toast.makeText(this, levelUpMsg, Toast.LENGTH_SHORT).show();
        tvInstruction.setText("🎉 " + levelUpMsg);

        // 升级后稍微停顿一下再继续，期间按钮保持禁用状态
        setColorButtonsEnabled(false);
        colorHandler.postDelayed(() -> {
            if (gameRunning) {
                setColorButtonsEnabled(true);  // 重新启用按钮
                nextColorChallenge();  // 开始新的一题
            }
        }, 1500);
    }

    private void setColorButtonsEnabled(boolean enabled) {
        btnRed.setEnabled(enabled);
        btnGreen.setEnabled(enabled);
        btnBlue.setEnabled(enabled);
        btnYellow.setEnabled(enabled);
    }

    private void endGame() {
        gameRunning = false;
        colorHandler.removeCallbacksAndMessages(null);

        setColorButtonsEnabled(false);
        btnStart.setEnabled(true);
        btnStart.setText("再来一局");

        // 根据得分和等级给出评价
        String evaluation;
        if (level >= 8 && score >= 400) {
            evaluation = "🏆 反应之王! 你的手速无人能敌!";
        } else if (level >= 6 && score >= 300) {
            evaluation = "⚡ 闪电反应! 非常出色!";
        } else if (level >= 4 && score >= 200) {
            evaluation = "🎯 反应敏捷! 表现不错!";
        } else if (level >= 2 && score >= 100) {
            evaluation = "👍 还不错! 继续练习会更好!";
        } else {
            evaluation = "💪 多练习练习，你一定会进步的!";
        }

        tvTimer.setText("时间到!");
        tvInstruction.setText(evaluation);
        tvLevel.setText("最终等级: " + level + " | 得分: " + score);

        resetButtonColors();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        if (colorHandler != null) {
            colorHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
