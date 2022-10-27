package SurveyExpert;

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

public class ExpertSurveyFragment1 extends Fragment {

    ExpertSurveyMainActivity expertSurveyMainActivity;
    CheckBox cb_1, cb_2, cb_3, cb_4;
    Button button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        expertSurveyMainActivity = (ExpertSurveyMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        expertSurveyMainActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expert_survey1, container, false);

        sharedPreferences = getActivity().getSharedPreferences("expertInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        cb_1 = rootView.findViewById(R.id.cb_1);
        cb_2 = rootView.findViewById(R.id.cb_2);
        cb_3 = rootView.findViewById(R.id.cb_3);
        cb_4 = rootView.findViewById(R.id.cb_4);


        // 영어 과외 선택
        cb_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_1.isChecked()) {
                    button.setEnabled(true);

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
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

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
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

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
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

                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
                    button.setEnabled(false);
                }
            }
        });

        // 다음 화면 이동 버튼
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cb_1.isChecked()) {
                    Log.i(TAG, tagSharedPreferenceInput + "cb_1 = " + cb_1.getText().toString());
                    editor.putString("cb_1", cb_1.getText().toString());
                }
                if (cb_2.isChecked()) {
                    Log.i(TAG, tagSharedPreferenceInput + "cb_2 = " + cb_2.getText().toString());
                    editor.putString("cb_2", cb_2.getText().toString());
                }
                if (cb_3.isChecked()) {
                    Log.i(TAG, tagSharedPreferenceInput + "cb_3 = " + cb_3.getText().toString());
                    editor.putString("cb_3", cb_3.getText().toString());
                }
                if (cb_4.isChecked()) {
                    Log.i(TAG, tagSharedPreferenceInput + "cb_4 = " + cb_4.getText().toString());
                    editor.putString("cb_4", cb_4.getText().toString());
                }

                editor.commit();

                Log.i(TAG, tagSharedPreferenceOutput + "cb_1 = " +sharedPreferences.getString("cb_1", ""));
                Log.i(TAG, tagSharedPreferenceOutput + "cb_2 = " +sharedPreferences.getString("cb_2", ""));
                Log.i(TAG, tagSharedPreferenceOutput + "cb_3 = " +sharedPreferences.getString("cb_3", ""));
                Log.i(TAG, tagSharedPreferenceOutput + "cb_4 = " +sharedPreferences.getString("cb_4", ""));

                expertSurveyMainActivity.fragmentChange(2);
            }
        });

        return rootView;
    }
}
