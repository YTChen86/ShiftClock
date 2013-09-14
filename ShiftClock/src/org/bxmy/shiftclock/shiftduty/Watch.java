package org.bxmy.shiftclock.shiftduty;

import java.util.Date;

import android.util.Log;

public class Watch {

    private int mId;

    /**
     * 对应的班种。-1 表示未设置（内存状态，不写入数据库）；0 表示休息；大于 0 表示按某班种上班
     */
    private int mDutyId;

    /**
     * 值班所在天
     */
    private long mDayInSeconds;

    /**
     * 是否提前值班。0 表示不提前，正数表示提前的时间
     */
    private int mBeforeSeconds;

    /**
     * 是否延后落班。0 表示不延后，正数表示延后的时间
     */
    private int mAfterSeconds;

    public static Watch createEmptyInDays(int days) {
        Date date = new Date();
        date = new Date(date.getTime() + days * 86400L * 1000L);
        date = new Date(date.getYear(), date.getMonth(), date.getDate());
        return new Watch(-1, -1, date.getTime() / 1000, 0, 0);
    }

    public Watch(int id) {
        this.mId = id;
    }

    public Watch(int id, int dutyId, long dayInSeconds, int beforeSeconds,
            int afterSeconds) {
        this.mId = id;
        setDutyId(dutyId);
        setDayInSeconds(dayInSeconds);
        setBeforeSeconds(beforeSeconds);
        setAfterSeconds(afterSeconds);
    }

    public int getId() {
        return this.mId;
    }

    public int getDutyId() {
        return this.mDutyId;
    }

    public void setDutyId(int dutyId) {
        this.mDutyId = dutyId;
    }

    public long getDayInSeconds() {
        return this.mDayInSeconds;
    }

    public void setDayInSeconds(long dayInSeconds) {
        if (dayInSeconds % 86400 != 0)
            Log.d("shiftclock", "Watch dayInSeconds is not %86400: "
                    + dayInSeconds);

        this.mDayInSeconds = dayInSeconds;
    }

    public int getBeforeSeconds() {
        return this.mBeforeSeconds;
    }

    public void setBeforeSeconds(int beforeSeconds) {
        if (beforeSeconds < 0)
            beforeSeconds = 0;

        this.mBeforeSeconds = beforeSeconds;
    }

    public int getAfterSeconds() {
        return this.mAfterSeconds;
    }

    public void setAfterSeconds(int afterSeconds) {
        if (afterSeconds < 0)
            afterSeconds = 0;

        this.mAfterSeconds = afterSeconds;
    }

}
