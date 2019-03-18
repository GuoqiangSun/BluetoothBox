package com.bazooka.bluetoothbox.ui.activity;

import android.app.AlarmManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actions.ibluz.manager.BluzManager;
import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IAlarmManager;
import com.actions.ibluz.manager.IBluzManager;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private IAlarmManager alarmManager;
    private Button btnQuery;
    private Button btntow;
    private Button btnOne;
    private boolean[] date = new boolean[]{false, false, false, false, false, false, false};
    private Button btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BluzManagerUtils.getInstance().setMode(BluzManagerData.FuncMode.ALARM);
        alarmManager = BluzManagerUtils.getInstance().getIBluzManager().getAlarmManager(new BluzManagerData.OnManagerReadyListener() {
            @Override
            public void onReady() {
                alarmManager.getList();
                if (alarmManager.getList().size()==0){
                    Log.i("aaaa", "没有数据");
                }else {
                    Log.i("aaaa", "state = " + alarmManager.getList().get(0).state);
                }
                Log.i("aaaa", "state = " + alarmManager.getList().get(0).state);
            }
        });
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnOne = (Button) findViewById(R.id.btn_set_one);
        btntow = (Button) findViewById(R.id.btn_set_tow);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnOne.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btntow.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                alarmManager.getList();
                if (alarmManager.getList().size()==0){
                    Log.i("aaaa", "没有数据");
                }else {
                    Log.i("aaaa", "state = " + alarmManager.getList().get(0).state);
                }

                break;
            case R.id.btn_set_one:
                BluzManagerData.AlarmEntry entry = new BluzManagerData.AlarmEntry();
                entry.state = true;
                entry.index = 0;
                entry.hour = 10;
                entry.minute = 30;
                entry.repeat = date;
                entry.title = "2";
                entry.ringId = 1;
                entry.ringType = 3;
                alarmManager.set(entry);
                break;
            case R.id.btn_set_tow:
                BluzManagerData.AlarmEntry entry1 = new BluzManagerData.AlarmEntry();
                entry1.state = false;
                entry1.index = 0;
                entry1.hour = 10;
                entry1.minute = 30;
                entry1.repeat = date;
                entry1.title = "2";
                entry1.ringId = 1;
                entry1.ringType = 3;
                alarmManager.set(entry1);
                break;
            case R.id.btn_delete:
                BluzManagerData.AlarmEntry entry2 = new BluzManagerData.AlarmEntry();
                entry2.state = false;
                entry2.index = 0;
                entry2.hour = 10;
                entry2.minute = 30;
                entry2.repeat = date;
                entry2.title = "2";
                entry2.ringId = 1;
                entry2.ringType = 3;
                alarmManager.remove(entry2);

        }
    }
}
