package com.example.sfzhang.memo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    //모든 메모를 저장할 목록
    private final List<OneMemo> memolist=new ArrayList<>();

    MemoAdapter adapter;

    //main ListView
    ListView lv;

    //alarm clock
    int BIG_NUM_FOR_ALARM=100;

    //액티비티 생성 함수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //여기서 출력에 해당하는 레이아웃 정보 가져옴
        setContentView(R.layout.activity_main);
        
        //id값에 해당하는 툷바를 activity_main에서 가져옴
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        //DB연결
        Connector.getDatabase();
        loadHistoryData();

        adapter=new MemoAdapter(MainActivity.this, R.layout.memo_list, memolist);
        lv= findViewById(R.id.list);
        //리스트 lv에 adapter 적용
        lv.setAdapter(adapter);
        
        //리스트에서 선택 될 시
        lv.setOnItemClickListener(this);
        //리스트에서 길게 누를 시
        lv.setOnItemLongClickListener(this);

    }
    
    //메뉴 레이아웃의 내용을 툴바에 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //메뉴의 아이템이 선택 되었을때
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            //함수 호출
            onAdd();
        }
        return true;
    }
    
    private void loadHistoryData() {
        //DB에 저장된 메모 목록을 읽어옴
        List<Memo> memoes= LitePal.findAll(Memo.class);
        
        //DB에 메모가 없으면
        if(memoes.size()==0) {
            //초기 설정 실행
            initializeLitePal();
            //초기 설정으로 인해 생성된 메모 읽어오기
            memoes = LitePal.findAll(Memo.class);
        }
        
        //메모 목록 출력
        for(Memo record:memoes) {
            Log.d("MainActivity", "current num: " + record.getNum());
            Log.d("MainActivity", "id: " + record.getId());
            Log.d("MainActivity", "getAlarm: " + record.getAlarm());
            int tag = record.getTag();
            String textDate = record.getTextDate();
            String textTime = record.getTextTime();
            boolean alarm = record.getAlarm().length() > 1 ? true : false;
            String mainText = record.getMainText();
            OneMemo temp = new OneMemo(tag, textDate, textTime, alarm, mainText);
            memolist.add(temp);
        }

    }
    
    //리스트에서 아이템이 선택 되었을 때 실행 함수
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //edit 액티비티로 정보를 넘겨줄 인텐트 생성
        Intent it=new Intent(this,Edit.class);

        //해당 포지션의 메모를 읽어옴
        Memo record=getMemoWithNum(position);

        //수정할 메모의 변수값들을 INTENT에 넣음
        transportInformationToEdit(it, record);
        
        //데이터 전달 및 POSITION에 해당하는 메모 EDIT 액티비티 시작
        startActivityForResult(it,position);
    }

    //아이템을 오랫동안 눌렀을때 - 메모 삭제
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //저장된 메모 목록의 크기 가져옴
        int n=memolist.size();
        
        //삭제할 메모에 알람이 추가 되어있으면 알람 취소
        if(memolist.get(position).getAlarm()) {
            cancelAlarm(position);
        }
        //메모 목록에서 position에 해당하는 메모 삭제
        memolist.remove(position);
        //아답터 업데이트
        adapter.notifyDataSetChanged();

        //position을 문자열 변환
        String whereArgs = String.valueOf(position); //why not position ?
        //DB에서 메모 삭제
        LitePal.deleteAll(Memo.class, "num = ?", whereArgs);
        
        //삭제한 메모 다음부터 값을 1씩 감소시켜 정렬 후 DB 업데이트
        for(int i=position+1; i<n; i++) {
            ContentValues temp = new ContentValues();
            temp.put("num", i-1);
            String where = String.valueOf(i);
            LitePal.updateAll(Memo.class, temp, "num = ?", where);
        }

        return true;
    }

    //sub액티비티에서 main액티비티로 돌아올때 동작하는 함수. intent를 통해서 응답코드값을 전송받음
    protected void onActivityResult(int requestCode, int resultCode, Intent it) {
        super.onActivityResult(requestCode, resultCode, it);
        if (resultCode == RESULT_OK) {
            updateLitePalAndList(requestCode, it);
        }
    }

    //Edit.class가 반환하는 "num" 메모에 따라 데이터베이스 및 메모리 목록 업데이트
    private void updateLitePalAndList(int requestCode, Intent it) {

        int num=requestCode;
        int tag=it.getIntExtra("tag",0);

        //캘린더에서 값 읽어오기
        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);
        
        //intent에서 알람정보 가져오기
        String alarm=it.getStringExtra("alarm");
        //intent에서 메모 내용 가져오기
        String mainText=it.getStringExtra("mainText");

        boolean gotAlarm = alarm.length() > 1 ? true : false;
        //읽어온 값으로 새로운 메모 생성
        OneMemo new_memo = new OneMemo(tag, current_date, current_time, gotAlarm, mainText);
        
        //메모목록에 num값이 없으면
        if((requestCode+1)>memolist.size()) {
            // 새로운 메모 DB에 추가
            addRecordToLitePal(num, tag, current_date, current_time, alarm, mainText);

            //새로운 메모 메모 목록에 추가
            memolist.add(new_memo);
        }
        //존재하면
        else {
            //이전에 알람이 존재하면 알람 삭제
            if(memolist.get(num).getAlarm()) {
                cancelAlarm(num);
            }

            //메모 업데이트
            ContentValues temp = new ContentValues();
            temp.put("tag", tag);
            temp.put("textDate", current_date);
            temp.put("textTime", current_time);
            temp.put("alarm", alarm);
            temp.put("mainText", mainText);
            String where = String.valueOf(num);
            LitePal.updateAll(Memo.class, temp, "num = ?", where);
            
            memolist.set(num, new_memo);
        }
        //만약 사용자가 알람을 설정하면
        if(gotAlarm) {
            loadAlarm(alarm, requestCode, 0);
        }
        
        //아답터 업데이트
        adapter.notifyDataSetChanged();
    }

    //초기 내용 설정 함수
    private void initializeLitePal() {
        Calendar c=Calendar.getInstance();
        String textDate=getCurrentDate(c);
        String textTime=getCurrentTime(c);

        //두개의 메모를 DB에 추가
        addRecordToLitePal(0,0,textDate,textTime,"","click to edit");
        addRecordToLitePal(1,1,textDate,textTime,"","long click to delete");
    }

    //날짜 정보 가져오기
    private String getCurrentDate(Calendar c){
        return c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH);
    }

    //시간정보 가져오기
    private String getCurrentTime(Calendar c){
        String current_time="";
        if(c.get(Calendar.HOUR_OF_DAY)<10) current_time=current_time+"0"+c.get(Calendar.HOUR_OF_DAY);
        else current_time=current_time+c.get(Calendar.HOUR_OF_DAY);

        current_time=current_time+":";

        if(c.get(Calendar.MINUTE)<10) current_time=current_time+"0"+c.get(Calendar.MINUTE);
        else current_time=current_time+c.get(Calendar.MINUTE);

        return current_time;
    }

    //DB에 메모를 추가하는 함수
    private void addRecordToLitePal(int num, int tag, String textDate, String textTime, String alarm, String mainText) {
        Memo record=new Memo();
        record.setNum(num);
        record.setTag(tag);
        record.setTextDate(textDate);
        record.setTextTime(textTime);
        record.setAlarm(alarm);

        record.setMainText(mainText);
        record.save();
    }
    
    //수정된 정보를 전달하는 함수
    private void transportInformationToEdit(Intent it, Memo record) {
        it.putExtra("num",record.getNum());
        it.putExtra("tag",record.getTag());
        it.putExtra("textDate",record.getTextDate());
        it.putExtra("textTime",record.getTextTime());
        it.putExtra("alarm",record.getAlarm());
        it.putExtra("mainText",record.getMainText());
    }

    //DB 추가 함수
    public void onAdd() {
        Intent it=new Intent(this,Edit.class);

        int position = memolist.size();

        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);

        it.putExtra("num",position);
        it.putExtra("tag",0);
        it.putExtra("textDate",current_date);
        it.putExtra("textTime",current_time);
        it.putExtra("alarm","");
        it.putExtra("mainText","");

        //EDIT 액티비티 시작
        startActivityForResult(it,position);
    }

    //num값에 해당하는 메모 가져오기
    private Memo getMemoWithNum(int num) {
        String whereArgs = String.valueOf(num);
        Memo record= LitePal.where("num = ?", whereArgs).findFirst(Memo.class);
        return record;
    }

    //***********************************load or cancel alarm************************************************************************************
    //*****************BUG  SOLVED*************************
    //still have a bug as I know:
    //after deleting a memo, the "num" changes, then the cancelAlarm may have some trouble (it do not cancel actually)
    //establishing a hash table may solve this problem
    //SOLVED through adding id
    //******************************************

    //set an alarm clock according to the "alarm"
    private void loadAlarm(String alarm, int num, int days) {
        int alarm_hour=0;
        int alarm_minute=0;
        int alarm_year=0;
        int alarm_month=0;
        int alarm_day=0;

        int i=0, k=0;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_year=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_month=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
        alarm_day=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
        alarm_hour=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        alarm_minute=Integer.parseInt(alarm.substring(k));

        Memo record=getMemoWithNum(num);

        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        Intent intent = new Intent(MainActivity.this, OneShotAlarm.class);
        intent.putExtra("alarmId",record.getId()+BIG_NUM_FOR_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(
                MainActivity.this, record.getId()+BIG_NUM_FOR_ALARM, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.add(Calendar.SECOND, 5);

        Calendar alarm_time = Calendar.getInstance();
        alarm_time.set(alarm_year,alarm_month-1,alarm_day,alarm_hour,alarm_minute);

        int interval = 1000 * 60 * 60 * 24 *days;

        //백그라운드에서도 동작하게 해줌
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //if(interval==0)
        am.set(AlarmManager.RTC_WAKEUP, alarm_time.getTimeInMillis(), sender);
    }

    //알람 취소 함수
    private void cancelAlarm(int num) {
        //메모 읽어오기
        Memo record=getMemoWithNum(num);

        Intent intent = new Intent(MainActivity.this,
                OneShotAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                MainActivity.this, record.getId()+BIG_NUM_FOR_ALARM, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    //********************************************************************************************************************************
}

