package com.example.sfzhang.memo;

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
    
    //생성자
    public OneMemo(int tag, String textDate, String textTime,boolean alarm, String mainText) {
        this.tag=tag;
        this.textDate=textDate;
        this.textTime=textTime;
        this.alarm=alarm;
        this.mainText=mainText;
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
}
