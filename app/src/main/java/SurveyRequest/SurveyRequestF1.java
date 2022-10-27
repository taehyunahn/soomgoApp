  package SurveyRequest;

import static android.content.ContentValues.TAG;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.soomgodev.R;

import SurveyExpert.ExpertSurveyMainActivity;

public class SurveyRequestF1 extends Fragment {
    SurveyRequestMainActivity surveyRequestMainActivity;
    Button button;
    RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    private static final String TAG = "SurveyRequestF1";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach 동작");
        surveyRequestMainActivity = (SurveyRequestMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach 동작");
        surveyRequestMainActivity = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart 동작");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView 동작");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_request_f1, container, false);

        sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");

        editor = sharedPreferences.edit();

        rb1 = rootView.findViewById(R.id.rb1);
        rb2 = rootView.findViewById(R.id.rb2);
        rb3 = rootView.findViewById(R.id.rb3);
        rb4 = rootView.findViewById(R.id.rb4);
        rb5 = rootView.findViewById(R.id.rb5);
        rb6 = rootView.findViewById(R.id.rb6);
        rb7 = rootView.findViewById(R.id.rb7);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        rb7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(true);
            }
        });

        // 다음 화면 이동 버튼
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveyRequestMainActivity.fragmentChange(2);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause 동작");

        if(rb1.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb1.getText()));
        }
        if(rb2.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb2.getText()));
        }
        if(rb3.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb3.getText()));
        }
        if(rb4.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb4.getText()));
        }
        if(rb5.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb5.getText()));
        }
        if(rb6.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb6.getText()));
        }
        if(rb7.isChecked()){
            editor.putString(userSeq+"age", String.valueOf(rb7.getText()));
        }

        editor.commit();

        Log.i(TAG, "fragment1에서 저장된 값 = " + sharedPreferences.getString(userSeq + "age", ""));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop 동작");
    }

}

