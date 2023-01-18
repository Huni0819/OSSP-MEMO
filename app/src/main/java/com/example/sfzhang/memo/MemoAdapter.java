package com.example.sfzhang.memo;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sf Zhang on 2016/12/20.
 */
public class MemoAdapter extends ArrayAdapter<OneMemo> {

    private int resourceId;
    //테마 색상 저장 변수
    int[] color = {Color.parseColor("#F5EFA0"), Color.parseColor("#8296D5"), Color.parseColor("#95C77E"), Color.parseColor("#F49393"), Color.parseColor("#FFFFFF")};

    //생성자
    public MemoAdapter(Context context, int resource, List<OneMemo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OneMemo memo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        //메모 테마 색상 표현 이미지
        ImageView tag = (ImageView) view.findViewById(R.id.tag);
        //작성 날짜
        TextView textDate = (TextView) view.findViewById(R.id.textDate);
        //작성 시간
        TextView textTime = (TextView) view.findViewById(R.id.textTime);
        //알람 이미지
        ImageView alarm = (ImageView) view.findViewById(R.id.alarm);
        //메모 내용
        TextView mainText = (TextView) view.findViewById(R.id.mainText);

        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);

        //적절한 color를 tag에 설정
        if (memo.getTag() < color.length)
            tag.setBackgroundColor(color[memo.getTag()]);
        textDate.setText(memo.getTextDate());
        textTime.setText(memo.getTextTime());
        //알람을 설정했을때에 이미지 보이게
        if (memo.getAlarm()) {
            alarm.setVisibility(View.VISIBLE);
        } else {
            alarm.setVisibility(View.GONE);
        }
        //작성 내용을 읽어와서 기본화면에서 보이게
        mainText.setText(memo.getMainText());

        ImageView lockImage = (ImageView) view.findViewById(R.id.lock);
        if(memo.getLock_num() == 0) {
            lockImage.setVisibility(View.INVISIBLE);
        } else {
            lockImage.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
