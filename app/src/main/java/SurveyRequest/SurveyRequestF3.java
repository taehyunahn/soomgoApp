package SurveyRequest;

import static android.content.ContentValues.TAG;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.soomgodev.R;


public class SurveyRequestF3 extends Fragment {
    SurveyRequestMainActivity surveyRequestMainActivity;
    CheckBox cb_1, cb_2, cb_3, cb_4, cb_5, cb_6, cb_7 ;
    Button button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        surveyRequestMainActivity = (SurveyRequestMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        surveyRequestMainActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_request_f3, container, false);

        sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq","");

        editor = sharedPreferences.edit();

        cb_1 = rootView.findViewById(R.id.cb_1);
        cb_2 = rootView.findViewById(R.id.cb_2);
        cb_3 = rootView.findViewById(R.id.cb_3);
        cb_4 = rootView.findViewById(R.id.cb_4);
        cb_5 = rootView.findViewById(R.id.cb_5);
        cb_6 = rootView.findViewById(R.id.cb_6);
        cb_7 = rootView.findViewById(R.id.cb_7);


        // 영어 과외 선택
        cb_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_1.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        // 비즈니스 영어 선택
        cb_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_2.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                } else if (!cb_2.isChecked()){
                    editor.remove("cb_2");
                    editor.commit();
                    Log.i(TAG, tagSharedPreferenceOutput + "cb_2 삭제 후 = " + cb_2.getText().toString());
                }
            }
        });

        // 화상영어/전화영어 회화 선택
        cb_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_3.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        // TOEIC/Speaking/writing 과외 선택
        cb_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_4.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        cb_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_5.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        cb_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_6.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        cb_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_7.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked() && !cb_5.isChecked() && !cb_6.isChecked() && !cb_7.isChecked()){
                    button.setEnabled(false);
                }
            }
        });


        // 다음 화면 이동 버튼
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveyRequestMainActivity.fragmentChange(4);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause 동작");

        StringBuilder sb = new StringBuilder();
        if(cb_1.isChecked()){
            sb.append(cb_1.getText().toString()+", ");
        }
        if(cb_2.isChecked()){
            sb.append(cb_2.getText().toString()+", ");
        }
        if(cb_3.isChecked()){
            sb.append(cb_3.getText().toString()+", ");
        }
        if(cb_4.isChecked()){
            sb.append(cb_4.getText().toString()+", ");
        }
        if(cb_5.isChecked()){
            sb.append(cb_5.getText().toString()+", ");
        }
        if(cb_6.isChecked()){
            sb.append(cb_6.getText().toString()+", ");
        }
        if(cb_7.isChecked()){
            sb.append(cb_7.getText().toString()+", ");
        }

        String result = sb.toString();
        String day = result.substring(0, result.length() - 2);


        editor.putString(userSeq+"day", day);
        editor.commit();

        Log.i(TAG, "fragment3에서 저장된 값 = " + sharedPreferences.getString(userSeq + "day", ""));

    }



}