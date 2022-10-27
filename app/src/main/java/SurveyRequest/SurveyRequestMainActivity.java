package SurveyRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.soomgodev.R;

import SurveyExpert.ExpertSurveyFragment1;
import SurveyExpert.ExpertSurveyFragment2;
import SurveyExpert.ExpertSurveyFragment3;
import SurveyExpert.ExpertSurveyFragment4;

// 견적 요청서의 세부 사항을 선택하거나 작성하는 Activity → Fragment로 구성할 예정
public class SurveyRequestMainActivity extends AppCompatActivity {

    Fragment surveyRequestF1;
    Fragment surveyRequestF2;
    Fragment surveyRequestF3;
    Fragment surveyRequestF4;
    Fragment surveyRequestF5;
    Fragment surveyRequestF6;
    Fragment surveyRequestF7;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_request_main);
        getSupportActionBar().hide();

        surveyRequestF1 = new SurveyRequestF1();
        surveyRequestF2 = new SurveyRequestF2();
        surveyRequestF3 = new SurveyRequestF3();
        surveyRequestF4 = new SurveyRequestF4();
        surveyRequestF5 = new SurveyRequestF5();
        surveyRequestF6 = new SurveyRequestF6();
        surveyRequestF7 = new SurveyRequestF7();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");

        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, surveyRequestF1).commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scrollView, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

    // 인덱스를 통해 해당되는 프래그먼트를 띄운다.
    public void fragmentChange(int index){
        if(index == 1){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF1).commit();
        }
        else if(index == 2){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF2).commit();
        }
        else if(index == 3){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF3).commit();
        }
        else if(index == 4){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF4).commit();
        }
        else if(index == 5){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF5).commit();
        }
        else if(index == 6){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF6).commit();
        }
        else if(index == 7){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, surveyRequestF7).commit();
        }

    }
}