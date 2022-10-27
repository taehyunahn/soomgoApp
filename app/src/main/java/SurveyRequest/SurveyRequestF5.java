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
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.soomgodev.R;


public class SurveyRequestF5 extends Fragment {
    SurveyRequestMainActivity surveyRequestMainActivity;
    RadioButton rb1, rb2;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_request_f5, container, false);

        sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq","");

        editor = sharedPreferences.edit();


        rb1 = rootView.findViewById(R.id.rb1);
        rb2 = rootView.findViewById(R.id.rb2);



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

        // 다음 화면 이동 버튼
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surveyRequestMainActivity.fragmentChange(6);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause 동작");

        if(rb1.isChecked()){
            editor.putString(userSeq+"gender", String.valueOf(rb1.getText()));
        }
        if(rb2.isChecked()){
            editor.putString(userSeq+"gender", String.valueOf(rb2.getText()));
        }

        editor.commit();

        Log.i(TAG, "fragment5에서 저장된 값 = " + sharedPreferences.getString(userSeq + "gender", ""));

    }


}