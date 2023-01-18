package com.example.sfzhang.memo;

import org.litepal.crud.LitePalSupport;

/**
 * Created by sf Zhang on 2016/12/20.
 */

public class Memo extends LitePalSupport {
    private int num;
    private int tag;
    private String textDate;
    private String textTime;
    private String alarm;
    private String mainText;
    private int id;
    private int lock_num;

    //getter
    public int getNum(){
        return num;
    }
    public int getTag(){
        return tag;
    }
    public String getTextDate(){
        return textDate;
    }
    public String getTextTime(){
        return textTime;
    }
    public String getAlarm(){
        return alarm;
    }
    public String getMainText(){
        return mainText;
    }
    public int getId() { return id; }
    public int getLock_num(){ return lock_num; }

    //setter
    public void setNum(int num) {
        this.num=num;
    }
    public void setTag(int tag){
        this.tag=tag;
    }
    public void setTextDate(String textDate){
        this.textDate=textDate;
    }
    public void setTextTime(String textTime){
        this.textTime=textTime;
    }
    public void setAlarm(String alarm){
        this.alarm=alarm;
    }
    public void setMainText(String mainText){
        this.mainText=mainText;
    }
    public void setId(int id){ this.id=id; }
    public void setLock_num(int lock_num) { this.lock_num=lock_num; }
}
