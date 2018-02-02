package com.android.palladium.stopwatchapp;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // startTimeを保持するためのメンバ変数
    private long startTime;
    private long elapsedTime = 0l;

    // handlerとRunnableを保持するための変数
    private Handler handler = new Handler();
    private Runnable updateTimer;

    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private TextView timerLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        timerLabel = (TextView) findViewById( R.id.timerLabel );

        setButtonState( true, false, false );
    }

    public void setButtonState(boolean start, boolean stop, boolean reset) {

        startButton.setEnabled(start);
        stopButton.setEnabled(stop);
        resetButton.setEnabled(reset);
    }

    public void startTimer( View v ) {
        // startTimeの取得 ( システム起動してからの時間 (ミリ秒) )
        // 意 elapsed 経過した、経つ
        startTime = SystemClock.elapsedRealtime();

        // 一定時間ごとに現在の経過時間を表示
        /*
        * AndroidではUIに対して割り込み処理をする場合にはHandlerを使うルールとなっている
        * Handlerを使う際、処理をRunnableオブジェクトにしてUIへ投げる、という仕組み
        * Handler -> Runnable( 処理 ) -> UI
        * */
        updateTimer = new Runnable() {
            @Override
            public void run() {
                // 経過時間を計算してTextViewに表示
                long t = SystemClock.elapsedRealtime() - startTime + elapsedTime; // ミリ秒
                SimpleDateFormat sdf = new SimpleDateFormat( "mm:ss.SSS", Locale.US );
                timerLabel.setText( sdf.format( t ) );
                // まだ遅延処理が残っていた時のため
                handler.removeCallbacks( updateTimer );
                handler.postDelayed( updateTimer, 10 );
            }
        };
        // handlerでUIに投げる
        // postDelayed メソッドを遅延実行する処理
        // 意 delayed  遅らせる
        handler.postDelayed( updateTimer, 10 ); // 10ミリ秒後に実行


        // ボタンの操作( スタートボタンを押した後 )
        setButtonState( false, true, false );
    }

    public void stopTimer( View v ) {
        elapsedTime += SystemClock.elapsedRealtime() - startTime;

        // Timerを止めるにはhandlerからrunnableを削除
        handler.removeCallbacks( updateTimer );
        setButtonState( true, false, true );
    }

    public void resetTimer( View v ) {
        elapsedTime = 0l;
        timerLabel.setText( R.string.timer_label );
        setButtonState( true, false, false );
    }
}
