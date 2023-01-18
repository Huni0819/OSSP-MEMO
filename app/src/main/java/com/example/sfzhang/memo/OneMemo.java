package com.example.sfzhang.memo;

import android.widget.CheckBox;

/**
 * Created by sf Zhang on 2016/12/20.
 */
//한 메모의 값 저장
public class OneMemo {
    private int tag;
    private String textDate;
    private String textTime;
    private boolean alarm;
    private String mainText;
    private int lock_num;

    //생성자
    public OneMemo(int tag, String textDate, String textTime,boolean alarm, String mainText, int lock_num) {
        this.tag=tag;
        this.textDate=textDate;
        this.textTime=textTime;
        this.alarm=alarm;
        this.mainText=mainText;
        this.lock_num=lock_num;
    }

    //get
    public int getTag(){
        return tag;
    }
    public String getTextDate(){
        return textDate;
    }
    public String getTextTime(){
        return textTime;
    }
    public boolean getAlarm(){ return alarm; }
    public String getMainText(){
        return mainText;
    }
    public int getLock_num() {return lock_num; }

    //set
    public void setTag(int tag){
        this.tag=tag;
    }
    public void setTextDate(String textDate){
        this.textDate=textDate;
    }
    public void setTextTime(String textTime){
        this.textTime=textTime;
    }
    public void setAlarm(boolean alarm){
        this.alarm=alarm;
    }
    public void setMainText(String mainText){
        this.mainText=mainText;
    }
    public void setLock_num(int lock_num) { this.lock_num=lock_num; }
}
