package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.adMobAds;
import static com.rih.easymoney.Config.appLovinAds;
import static com.rih.easymoney.Config.trueFalseQuizPoint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rih.easymoney.R;
import com.rih.easymoney.quiz.MathQuestions;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TrueFalseMathQuizActivity extends AppCompatActivity {

    private final long timeCountInMilliSeconds = 60000;
    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;
    private Button btn_True, btn_False;
    private int QuestionNumber = 1;
    private int playerScore;
    int QuestionRandomId;
    private int pointsAdded;
    private String theCorrectAnswer;
    private TextView numberOfQuestion, textQuestion;
    String[] theQuestion;
    String logoNameQuestion, questionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false_math_quiz);

        setToolbar();
        setBannerAds();
        initViews();

        btn_True.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCountDownTimer();
                if (logoNameQuestion.equals(theCorrectAnswer))
                {
                    setPoints();
                    delayNextQuestion();
                    CorrectAnswer(btn_True);
                }
                else
                {
                    delayExit();
                    WrongAnswer(btn_True);
                }
            }
        });

        btn_False.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCountDownTimer();
                if (!logoNameQuestion.equals(theCorrectAnswer))
                {
                    setPoints();
                    delayNextQuestion();
                    CorrectAnswer(btn_False);
                }
                else
                {
                    delayExit();
                    WrongAnswer(btn_False);
                }
            }
        });

        getNextQuestion();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_math_true_false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setBannerAds() {
        // Applovin Ads
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                // AppLovin SDK is initialized, start loading ads
                if (appLovinAds.contains("True")) {
                    ShowMaxBannerAd();
                }
            }
        });

        if (adMobAds.contains("True")) {
            MobileAds.initialize(this, initializationStatus -> {
            });
            AdView mAdView = findViewById(R.id.adView);
            @SuppressLint("VisibleForTests")
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void ShowMaxBannerAd() {
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }

    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);
        numberOfQuestion = findViewById(R.id.NumberOf_Question);
        btn_True = findViewById(R.id.btn_Answer_one);
        btn_False = findViewById(R.id.btn_Answer_two);
        textQuestion = findViewById(R.id.textQuestion);
    }


    private void startCountDownTimer()
    {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                progressBarCircle.setProgress(Integer.parseInt(String.valueOf(time)));
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                if (Integer.parseInt(String.valueOf(time)) <= 15)
                {
                    textViewTime.setShadowLayer(5, 1, 1, Color.BLACK);
                    textViewTime.setTextColor(getResources().getColor(R.color.red_A400));
                }
                else {
                    textViewTime.setShadowLayer(1, 1, 1, Color.BLACK);
                    textViewTime.setTextColor(getResources().getColor(R.color.yellow_600));
                }
            }
            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
                setEndGameLose();
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax(60);
        progressBarCircle.setProgress(0);
    }

    private String hmsTimeFormatter(long milliSeconds) {
        @SuppressLint("DefaultLocale")
        String hms = String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    private void setEndGameLose()
    {
        delayExit();
    }

    private void delayNextQuestion()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                QuestionNumber++;
                playerScore++;
                getNextQuestion();
                enableClickButton();
            }
        }, 1500);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getNextQuestion()
    {
        startCountDownTimer();
        setProgressBarValues();
        timerStatus = TimerStatus.STARTED;

        numberOfQuestion.setText("" + getString(R.string.Question) + " " + QuestionNumber);
        setOptionsAnimation();

        QuestionRandomId = randomNumber(1, 71); // Number of questions
        theQuestion = MathQuestions.getMathQuestionAndOptions(QuestionRandomId);

        logoNameQuestion = theQuestion[randomNumber(1,4)];

        questionString =  theQuestion[0];
        theCorrectAnswer = theQuestion[5];


        // Set the Question Text into Textview
        textQuestion.setText(questionString + logoNameQuestion);

        btn_True.setBackground(getDrawable(R.drawable.exit_button));
        btn_False.setBackground(getDrawable(R.drawable.exit_button));
    }

    private void delayExit()
    {
        disableClickButton();

        if (btn_True.getText().toString().equals(theCorrectAnswer))
        {
            CorrectAnswer(btn_True);
        }
        if (btn_False.getText().toString().equals(theCorrectAnswer))
        {
            CorrectAnswer(btn_False);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putInt("theResult", QuestionNumber - 1);
                bundle.putInt("thePoints", pointsAdded);
                bundle.putInt("theCategory", 2);
                Intent outIntent = new Intent(getApplicationContext(), QuizResultActivity.class);
                outIntent.putExtras(bundle);
                startActivity(outIntent);
                finish();
            }
        }, 1500);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void CorrectAnswer(Button button)
    {
        button.setBackground(getDrawable(R.drawable.btn_correct_answer));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void WrongAnswer(Button button)
    {
        button.setBackground(getDrawable(R.drawable.btn_wrong_answer));
    }

    private void enableClickButton()
    {
        btn_True.setClickable(true);
        btn_False.setClickable(true);
    }

    private void disableClickButton()
    {
        btn_True.setClickable(false);
        btn_False.setClickable(false);
    }

    private int randomNumber(int min, int max)
    {
        Random random = new Random();

        return random.nextInt(max - min) + min;
    }

    private void setOptionsAnimation()
    {
        Animation queAnim = AnimationUtils.loadAnimation(this, R.anim.in_right);
        btn_True.startAnimation(queAnim);
        btn_False.startAnimation(queAnim);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountDownTimer();
        finish();
    }

    private void setPoints()
    {
        pointsAdded = pointsAdded + trueFalseQuizPoint;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,getString(R.string.exit_press), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }


}