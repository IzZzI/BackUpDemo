package com.test.backupdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.test.backupdemo.util.BackupUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button backUpButton = findViewById(R.id.btn_backup);
        backUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BackupUtils.backup("com.ss.android.ugc.aweme", "douyin");
                        //备份信息请看日志，不要狂点!!! 日志过滤可以用Backup过滤
                    }
                }).start();

            }
        });
        Button restoreButton = findViewById(R.id.btn_restore);
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BackupUtils.restore("com.ss.android.ugc.aweme", "douyin");
                    }
                }).start();

            }
        });
    }
}
