package SurveyExpert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.soomgodev.R;

public class ExpertSurveyMainActivity extends AppCompatActivity {

    Fragment ExpertSurveyFragment1;
    Fragment ExpertSurveyFragment2;
    Fragment ExpertSurveyFragment3;
    Fragment ExpertSurveyFragment4;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_survey_main);
        getSupportActionBar().hide();

        ExpertSurveyFragment1 = new ExpertSurveyFragment1();
        ExpertSurveyFragment2 = new ExpertSurveyFragment2();
        ExpertSurveyFragment3 = new ExpertSurveyFragment3();
        ExpertSurveyFragment4 = new ExpertSurveyFragment4();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");

        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, ExpertSurveyFragment1).commit();


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
                    .replace(R.id.scrollView, ExpertSurveyFragment1).commit();
        }
        else if(index == 2){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, ExpertSurveyFragment2).commit();
        }
        else if(index == 3){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, ExpertSurveyFragment3).commit();
        }
        else if(index == 4){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scrollView, ExpertSurveyFragment4).commit();
        }
    }
}