package com.websarva.wings.android.asynccountsample;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンの取得
        Button btSync = findViewById(R.id.btSync);
        Button btAsync = findViewById(R.id.btAsync);

        //ボタンにリスナーを登録
        btSync.setOnClickListener(new SyncClickListener());
        btAsync.setOnClickListener(new AsyncClickListener());
    }

    //Logcatにカウント状況を出力するメソッド
    public void CountMethod(String str){
        for(int i= 0; i < 1000; i++){
            System.out.println(str + i);
        }
    }

    //同期ボタン
    private class SyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CountMethod("count1: ");
            CountMethod("count2: ");
        }
    }


    //非同期ボタン
    private class AsyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Receiver receiver = new Receiver();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(receiver);

            CountMethod("ui: ");

            String result ="";

            try{
                //ワーカースレッドからのリターンを待つ
                result = future.get();
                //CountMethod("ui2: ");
            }
            catch (Exception ex){
                Log.w("DEBUG_TAG", "非同期処理の例外発生", ex);
            }

            TextView tvMsg = findViewById(R.id.tvMsg);
            tvMsg.setText(result);

        }


        //ワーカースレッド用のクラスとその処理
        private class Receiver implements Callable<String> {

            @WorkerThread
            @Override
            public String call() {
                CountMethod("worker: ");
                //UIスレッドに渡すデータ
                String result = "ワーカースレッドからの戻り値(文字列)";
                return result;
            }
        }

    }
}