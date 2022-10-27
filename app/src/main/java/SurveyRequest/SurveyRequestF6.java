package SurveyRequest;

import static android.content.ContentValues.TAG;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soomgodev.NetworkStatus;
import com.example.soomgodev.R;

import SurveyExpert.ExpertSurveyFragment3;


public class SurveyRequestF6 extends Fragment {
    SurveyRequestMainActivity surveyRequestMainActivity;
    Button button;
    EditText et_address;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String data;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_request_f6, container, false);

        sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq","");

        editor = sharedPreferences.edit();


        // 다음 화면 이동 버튼
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surveyRequestMainActivity.fragmentChange(7);
            }
        });

        et_address = rootView.findViewById(R.id.et_address);
        et_address.setFocusable(false);
        et_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "주소설정페이지 - 주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // WebView가있는 페이지로 넘어갈 Intent를 설정
                    Log.i("주소설정페이지", "주소입력창 클릭");
//                    Intent i = new Intent(getContext(), getContext());
//                    // 주소결과
//                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);

                    Bundle bundle = new Bundle();
                    bundle.putString("fromFrag1", "안태현");
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    SurveyRequestF66 fragment66 = new SurveyRequestF66();
                    fragment66.setArguments(bundle);
                    transaction.replace(R.id.scrollView, fragment66);
                    transaction.commit(); // 저장

                }else {
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(getArguments() != null) {
            data = getArguments().getString("data");
            Log.i("test", "data:" + data);
            et_address.setText(data);
            button.setEnabled(true);

        }


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause 동작");
        String address = et_address.getText().toString();
        editor.putString(userSeq+"addressInfo", address);
        editor.commit();

        Log.i(TAG, "fragment6에서 저장된 값 = " + sharedPreferences.getString(userSeq + "addressInfo", ""));

    }


}