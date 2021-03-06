package org.bxmy.shiftclock;

import java.util.ArrayList;

import org.bxmy.shiftclock.notification.NotificationFutureWatch;
import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;
import org.bxmy.shiftclock.shiftduty.Watch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class EditWatchActivity extends Activity {

    private Watch mWatch;

    private DatePicker mWatchDay;

    private Spinner mComboDuty;

    private TextView mDutyTime;

    private TextView mRealBegin;

    private ToggleButton mToggleBefore;

    private TimePicker mBeforeTime;

    private TextView mRealEnd;

    private ToggleButton mToggleAfter;

    private TimePicker mAfterTime;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_watch);

        bind();

        mWatch = getIntent().getParcelableExtra("watch");
        if (mWatch == null) {
            if (getIntent().hasExtra("day")) {
                int day = getIntent().getIntExtra("day", 0);
                if (day > 0) {
                    mWatch = Watch.createEmptyByDayId(day);
                }
            }
        }

        initWatch();
    }

    private void bind() {
        mWatchDay = (DatePicker) findViewById(R.id.date_watchDay);
        mWatchDay.setEnabled(false);

        mComboDuty = (Spinner) findViewById(R.id.combo_duty);
        ArrayList<String> dutyNames = new ArrayList<String>();
        dutyNames.add("休息");
        for (String duty : ShiftDuty.getInstance().getDutyNames())
            dutyNames.add(duty);

        mDutyTime = (TextView) findViewById((R.id.label_dutyTime));

        ArrayAdapter<String> dutyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dutyNames);
        mComboDuty.setAdapter(dutyAdapter);
        mComboDuty.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if (arg2 == 0) {
                    mDutyTime.setText("");
                    mToggleBefore.setEnabled(false);
                    mBeforeTime.setEnabled(false);
                    mToggleAfter.setEnabled(false);
                    mAfterTime.setEnabled(false);
                } else {
                    Duty duty = ShiftDuty.getInstance().getDutyById(arg2);
                    if (duty != null) {
                        mDutyTime.setText(R.string.label_dutyTime);
                        mDutyTime.append(Util.formatTimeIn24Hours(duty
                                .getStartSecondsInDay()));
                        mDutyTime.append("-");
                        mDutyTime.append(Util.formatTimeIn24Hours(duty
                                .getStartSecondsInDay()
                                + duty.getDurationSeconds()));
                    } else {
                        mDutyTime.setText("");
                    }

                    mToggleBefore.setEnabled(true);
                    mBeforeTime.setEnabled(true);
                    mToggleAfter.setEnabled(true);
                    mAfterTime.setEnabled(true);
                }

                updateRealBegin();
                updateRealEnd();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        mRealBegin = (TextView) findViewById(R.id.label_watchRealBegin);
        mToggleBefore = (ToggleButton) findViewById(R.id.toggle_beforeWatch);
        mToggleBefore.setTextOff("提前");
        mToggleBefore.setTextOn("推迟");
        mToggleBefore.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                updateRealBegin();
            }

        });

        mBeforeTime = (TimePicker) findViewById(R.id.time_beforeWatch);
        mBeforeTime.setIs24HourView(true);
        mBeforeTime.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateRealBegin();
            }

        });

        mRealEnd = (TextView) findViewById(R.id.label_watchRealEnd);
        mToggleAfter = (ToggleButton) findViewById(R.id.toggle_afterWatch);
        mToggleAfter.setTextOff("提前");
        mToggleAfter.setTextOn("推迟");
        mToggleAfter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                updateRealEnd();
            }

        });

        mAfterTime = (TimePicker) findViewById(R.id.time_afterWatch);
        mAfterTime.setIs24HourView(true);
        mAfterTime.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateRealEnd();
            }

        });

        Button ok = (Button) findViewById(R.id.button_ok);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onOK();
            }
        });

        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        Button delete = (Button) findViewById(R.id.button_delete);
        delete.setEnabled(false);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onDelete();
            }

        });
    }

    private void initWatch() {
        if (mWatch == null) {
            mWatchDay.setEnabled(true);
            mWatch = Watch.createEmptyInDays(0, 0);
        }

        if (mWatch.getDutyId() >= 0) {
            Button delete = (Button) findViewById(R.id.button_delete);
            delete.setEnabled(true);
        }

        int dutyId = mWatch.getDutyId();
        if (dutyId < 0)
            dutyId = 0;

        mComboDuty.setSelection(dutyId);

        Util.updateDate(mWatchDay, mWatch.getDayInSeconds(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        updateRealBegin();
                        updateRealEnd();
                    }

                });
        if (mWatch.getBeforeSeconds() > 0) {
            mToggleBefore.setChecked(false);
            Util.updateTime(mBeforeTime, mWatch.getBeforeSeconds());
        } else {
            mToggleBefore.setChecked(true);
            Util.updateTime(mBeforeTime, -mWatch.getBeforeSeconds());
        }

        if (mWatch.getAfterSeconds() > 0) {
            mToggleAfter.setChecked(true);
            Util.updateTime(mAfterTime, mWatch.getAfterSeconds());
        } else {
            mToggleAfter.setChecked(false);
            Util.updateTime(mAfterTime, -mWatch.getAfterSeconds());
        }
    }

    private void onOK() {
        long dayInSeconds = Util.getDate(mWatchDay);
        if (mWatchDay.isEnabled()) {
            if (ShiftDuty.getInstance().watchExistsDay(dayInSeconds)) {
                Toast.makeText(getApplicationContext(),
                        "您选择的日期已经有值班安排！未来或有支持重复排班…", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int dutyId = mComboDuty.getSelectedItemPosition();
        Duty duty = null;
        if (dutyId > 0) {
            duty = ShiftDuty.getInstance().getDutyById(dutyId);
            if (duty == null) {
                Toast.makeText(getApplicationContext(), "班种有误！sorry",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                dutyId = duty.getId();
            }
        }

        int durationSeconds = 0;
        int beforeSeconds = 0;
        int afterSeconds = 0;
        if (duty != null) {
            durationSeconds = duty.getDurationSeconds();
            dayInSeconds += duty.getStartSecondsInDay();
            beforeSeconds = Util.getTime(mBeforeTime);
            if (mToggleBefore.isChecked())
                beforeSeconds = -beforeSeconds;

            afterSeconds = Util.getTime(mAfterTime);
            if (!mToggleAfter.isChecked())
                afterSeconds = -afterSeconds;

            int totalWatchSeconds = beforeSeconds + duty.getDurationSeconds()
                    + afterSeconds;
            if (totalWatchSeconds <= 0) {
                Toast.makeText(getApplicationContext(), "实际上班时间至少要那么一分一秒吧……",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (mWatch.getId() < 0) {
            ShiftDuty.getInstance().newWatch(dutyId, dayInSeconds,
                    durationSeconds, beforeSeconds, afterSeconds);
        } else {
            Watch newWatch = new Watch(mWatch.getId(), dutyId, dayInSeconds,
                    durationSeconds, beforeSeconds, afterSeconds, 0, 0);
            ShiftDuty.getInstance().updateWatch(newWatch);
        }

        int finalDayId = Util.getDayIdOfTime(dayInSeconds);
        NotificationFutureWatch.getInstance().cancel(finalDayId);

        finish();
    }

    private void onCancel() {
        finish();
    }

    private void onDelete() {
        if (mWatch.getDutyId() >= 0) {
            ShiftDuty.getInstance().removeWatch(mWatch.getId());
            finish();
        }
    }

    void updateRealBegin() {
        if (mWatch == null) {
            mRealEnd.setText("");
            return;
        }

        int before = Util.getTime(mBeforeTime);
        if (mToggleBefore.isChecked())
            before = -before;

        int dutyId = mComboDuty.getSelectedItemPosition();
        Duty duty = null;
        if (dutyId > 0) {
            duty = ShiftDuty.getInstance().getDutyById(dutyId);
        }

        if (duty != null) {
            long dayInSeconds = Util.getDate(mWatchDay)
                    + duty.getStartSecondsInDay();

            mRealBegin.setText(R.string.label_watchRealBegin);
            String begin = Util.formatTimeByRelatived(dayInSeconds, -before);
            mRealBegin.append(begin);
        } else {
            mRealBegin.setText("");
        }
    }

    void updateRealEnd() {
        if (mWatch == null) {
            mRealEnd.setText("");
            return;
        }

        int after = Util.getTime(mAfterTime);
        if (!mToggleAfter.isChecked())
            after = -after;

        int dutyId = mComboDuty.getSelectedItemPosition();
        Duty duty = null;
        if (dutyId > 0) {
            duty = ShiftDuty.getInstance().getDutyById(dutyId);
        }

        if (duty != null) {
            long dayInSeconds = Util.getDate(mWatchDay)
                    + duty.getStartSecondsInDay();

            mRealEnd.setText(R.string.label_watchRealEnd);
            String end = Util.formatTimeByRelatived(dayInSeconds,
                    duty.getDurationSeconds() + after);
            mRealEnd.append(end);
        } else {
            mRealEnd.setText("");
        }
    }
}
