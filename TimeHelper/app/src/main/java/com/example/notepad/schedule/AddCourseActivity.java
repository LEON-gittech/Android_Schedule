package com.example.notepad.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.notepad.MainActivity;
import com.example.notepad.R;
import com.example.notepad.bean.Course;

public class AddCourseActivity extends AppCompatActivity {

    EditText inputCourseName;
    EditText inputTeacher;
    EditText inputClassRoom;

    Spinner inputDay;
    Spinner inputStart;
    Spinner inputEnd;

    boolean isRevise = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        setFinishOnTouchOutside(true);

        inputCourseName = (EditText) findViewById(R.id.course_name);
        inputTeacher = (EditText) findViewById(R.id.teacher_name);
        inputClassRoom = (EditText) findViewById(R.id.class_room);

        inputDay = (Spinner) findViewById(R.id.week);
        inputStart = (Spinner) findViewById(R.id.classes_begin);
        inputEnd = (Spinner) findViewById(R.id.classes_ends);

        Intent intent = getIntent();
        final Course ReviseCourse = (Course) intent.getSerializableExtra("ReviseCourse");
        isRevise=intent.getBooleanExtra("isRevise",false);

        Button okButton = (Button) findViewById(R.id.button);

        String courseName;
        String teacher;
        String classRoom;
        String day;
        String start;
        String end;

        if(isRevise){
            inputCourseName.setText(ReviseCourse.getCourseName());
            inputClassRoom.setText(ReviseCourse.getClassRoom());
            inputTeacher.setText(ReviseCourse.getTeacher());
            setSpinnerDefaultValue(inputDay,String.valueOf(ReviseCourse.getDay()));
            setSpinnerDefaultValue(inputStart,String.valueOf(ReviseCourse.getStart()));
            setSpinnerDefaultValue(inputEnd,String.valueOf(ReviseCourse.getEnd()));

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String courseName = inputCourseName.getText().toString();
                    String teacher = inputTeacher.getText().toString();
                    String classRoom = inputClassRoom.getText().toString();
                    String day = inputDay.getSelectedItem().toString();
                    String start = inputStart.getSelectedItem().toString();
                    String end = inputEnd.getSelectedItem().toString();

                    if (courseName.equals("") || day.equals("") || start.equals("") || end.equals("")) {
                        Toast.makeText(AddCourseActivity.this, "基本信息不完整", Toast.LENGTH_SHORT).show();
                    }

                    Course newCourse = new Course(courseName, teacher, classRoom,
                            Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));

                    Intent intent = new Intent();
                    intent.putExtra("PreCourse",ReviseCourse);
                    intent.putExtra("newCourse", newCourse);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });

        }else {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseName = inputCourseName.getText().toString();
                    String teacher = inputTeacher.getText().toString();
                    String classRoom = inputClassRoom.getText().toString();
                    String day = inputDay.getSelectedItem().toString();
                    String start = inputStart.getSelectedItem().toString();
                    String end = inputEnd.getSelectedItem().toString();

                    if (courseName.equals("") || day.equals("") || start.equals("") || end.equals("")) {
                        Toast.makeText(AddCourseActivity.this, "基本信息不完整", Toast.LENGTH_SHORT).show();
                    } else {
                        Course course = new Course(courseName, teacher, classRoom,
                                Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));
                        Intent intent = new Intent(AddCourseActivity.this, MainActivity.class);
                        intent.putExtra("course", course);

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }

    }

    private void setSpinnerDefaultValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        int size = apsAdapter.getCount();
        for (int i = 0; i < size; i++) {

            if (TextUtils.equals(value, apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i,true);
                break;
            }
        }
    }

}
