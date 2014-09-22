package com.example.UserScheduling;

import java.util.StringTokenizer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TimeTableActivity extends ActionBarActivity {

	SQLiteDatabase db;
	static int screenWidth;
	static int screenHeight;
	static int day = 0;
	static int time2;
	GridView timeTable;
	int viewWidth;
	float viewHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);

		TimeTableAdapter adapter = new TimeTableAdapter(this);
		timeTable = (GridView) findViewById(R.id.timetable);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		adapter.addItem(new TableItem("버튼", 0, 0));
		adapter.addItem(new TableItem("월", 0, 0));
		adapter.addItem(new TableItem("화", 0, 0));
		adapter.addItem(new TableItem("수", 0, 0));
		adapter.addItem(new TableItem("목", 0, 0));
		adapter.addItem(new TableItem("금", 0, 0));
		
		int time = 8;
		for (int i = 0; i < 66; i++) {
			day = i % 6;
			if (i % 6 == 0) {
				adapter.addItem(new TableItem((time++) + "시", 0, 0));
			} else
				adapter.addItem(new TableItem("", time, day));
		}

		timeTable.setAdapter(adapter);

	}
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.timetable);

		TimeTableAdapter adapter = new TimeTableAdapter(this);
		timeTable = (GridView) findViewById(R.id.timetable);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		adapter.addItem(new TableItem("버튼", 0, 0));
		adapter.addItem(new TableItem("월", 0, 0));
		adapter.addItem(new TableItem("화", 0, 0));
		adapter.addItem(new TableItem("수", 0, 0));
		adapter.addItem(new TableItem("목", 0, 0));
		adapter.addItem(new TableItem("금", 0, 0));
		
		int time = 8;
		for (int i = 0; i < 66; i++) {
			day = i % 6;
			if (i % 6 == 0) {
				adapter.addItem(new TableItem((time++) + "시", 0, 0));
			} else
				adapter.addItem(new TableItem("", time, day));
		}

		timeTable.setAdapter(adapter);

	}
	public void onWindowFocusChanged(boolean hasFocus) 
	{
		super.onWindowFocusChanged(hasFocus);

		GridView timeTable = (GridView) findViewById(R.id.timetable);

		viewWidth = timeTable.getWidth() / 6;
		viewHeight = timeTable.getHeight() / 12;

		createDataBase("timeplus.db");
		createTable("base");
		queryData1("base");
	}
	String numberTwo(int s)
	{
		String st=String.valueOf(s);
		if(s<10)
		{
			st="0"+st;
	    }
		return st;
	}
	void createDataBase(String name) {
		db = openOrCreateDatabase(name, MODE_WORLD_WRITEABLE, null);
	}

	void createTable(String name) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name
				+ "(subject text,professor text,classroom text,"
				+ "starttime text,finishtime text,day integer,color integer)");
	}

	void queryData1(String name) {
		String sql = "Select day, subject, starttime, finishtime, color from "
				+ name;

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToNext();
				int day=cursor.getInt(0);
				String subject = cursor.getString(1);
				String starttime=cursor.getString(2);
				String finishtime=cursor.getString(3);
				int color=cursor.getInt(4);
				StringTokenizer st=new StringTokenizer(starttime,":");
				int startHours=Integer.parseInt(st.nextToken());
				int startMinute=Integer.parseInt(st.nextToken());
				st=new StringTokenizer(finishtime,":");
				int finishHours=Integer.parseInt(st.nextToken());
				int finishMinute=Integer.parseInt(st.nextToken());
				int duration=caldur(startHours,startMinute,finishHours,finishMinute);
				Log.d("sfd",day+" "+subject+" "+startHours+" "+startMinute);
				setSubjectName(day-1, subject, startHours, startMinute, duration, color);
			}
		}
	}
	
	Data_LP queryData2(String name,int startHours, int startMinute) {
		Data_LP data=null;
		String startTime=startHours+":"+numberTwo(startMinute);
		String sql = "Select subject, professor,color ,day, classroom,starttime,finishtime from "
				+ name+" where starttime = "+"'"+startTime+"'";
		Log.d("s",sql);

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToNext();
				String subject = cursor.getString(0);
				String professor=cursor.getString(1);
				int color=cursor.getInt(2);
				int day=cursor.getInt(3);
				String classroom=cursor.getString(4);
				String starttime=cursor.getString(5);
				String finishtime=cursor.getString(6);
				data=new Data_LP(subject,professor,color,day,classroom,starttime,finishtime);
				
			}
		}
		return data;
		}

	private int caldur(int startHours, int startMinute, int finishHours,
			int finishMinute) {
		// TODO Auto-generated method stub
		int duration;
		if(finishMinute>startMinute)
			duration=(finishMinute-startMinute)+60*(finishHours-startHours);
		else
			duration=(60+finishMinute-startMinute)+60*(finishHours-startHours-1);
		return duration;
	}

	public void setSubjectName(int dayOfWeek, String SubjectName,
			final int startHours, final int startMinute, float durationMinute, int color) {

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

		TextView subjectNameView = new TextView(this);
		subjectNameView.setText(SubjectName);
		subjectNameView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Data_LP data;
				data=queryData2("base",startHours,startMinute);
				Intent intent = new Intent(getApplicationContext(),
						MainActivity_LP.class);
				intent.putExtra("mode", 1);
				intent.putExtra("subject", data.subjectName);
				intent.putExtra("professor",data.professorName);
				intent.putExtra("color", data.color);
				intent.putExtra("classroom",data.classNumber);
				intent.putExtra("day",data.day);
				intent.putExtra("starttime",data.startTime);
				intent.putExtra("finishtime", data.finishTime);
				startActivity(intent);
			}

		});

		// 색 설정
		if (color == 1)
			subjectNameView.setBackgroundColor(Color.rgb(255, 0, 0));
		else if (color == 2)
			subjectNameView.setBackgroundColor(Color.rgb(0, 255, 0));
		else if (color == 3)
			subjectNameView.setBackgroundColor(Color.rgb(0, 0, 255));
		else if (color == 4)
			subjectNameView.setBackgroundColor(Color.rgb(255, 255, 0));
		else if (color == 5)
			subjectNameView.setBackgroundColor(Color.rgb(255, 0, 255));
		else if (color == 6)
			subjectNameView.setBackgroundColor(Color.rgb(0, 255, 255));

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		// 요일 설정
		params.leftMargin = viewWidth + (dayOfWeek * viewWidth) + 1;

		// 시간에 따른 높이 설정
		double aMinuteHeight = viewHeight / 60;
		params.height = (int) (aMinuteHeight * durationMinute);

		// 한 과목의 최대 넓이, 줄 수, 가운데 정렬 설정
		subjectNameView.setMaxWidth(viewWidth - 2);
		subjectNameView.setWidth(viewWidth - 2);
		subjectNameView.setGravity(Gravity.CENTER_HORIZONTAL);
		subjectNameView.setMaxLines(2);

		// 시작 위치 설정.
		params.topMargin = (int) (viewHeight + ((startHours - 8) * viewHeight)
				+ caculateHeight(startMinute) + 2);
		if (startHours >= 15)
			params.topMargin += 5;

		subjectNameView.setLayoutParams(params);
		layout.addView(subjectNameView);
	}

	public int caculateHeight(float minute) {
		int answer = 0;
		for (int i = 1; i <= minute; i++) {
			if (i % 2 == 0)
				answer += 2;
			else
				answer += 1;
		}

		return answer;
	}
}