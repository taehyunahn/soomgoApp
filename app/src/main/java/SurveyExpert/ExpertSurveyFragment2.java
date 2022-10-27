package SurveyExpert;

import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soomgodev.NetworkStatus;
import com.example.soomgodev.R;

public class ExpertSurveyFragment2 extends Fragment {

    Button button;
    ExpertSurveyMainActivity expertSurveyMainActivity;
    EditText et_address;
    private static final String TAG = "ExpertSurveyFragment2";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private String data;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expert_survey2, container, false);

        sharedPreferences = getActivity().getSharedPreferences("expertInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, tagSharedPreferenceInput + "address = " + et_address.getText().toString());
                editor.putString("address", et_address.getText().toString());
                editor.commit();
                Log.i(TAG, tagSharedPreferenceOutput + "address = " + sharedPreferences.getString("address", ""));



                expertSurveyMainActivity.fragmentChange(4);
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
                    ExpertSurveyFragment3 fragment3 = new ExpertSurveyFragment3();
                    fragment3.setArguments(bundle);
                    transaction.replace(R.id.scrollView, fragment3);
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

//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        Log.i("test", "onActivityResult");
//
//        switch (requestCode) {
//            case SEARCH_ADDRESS_ACTIVITY:
//                if (resultCode == RESULT_OK) {
//                    String data = intent.getExtras().getString("data");
//                    if (data != null) {
//                        Log.i("test", "data:" + data);
//                        edit_addr.setText(data);
//                    }
//                }
//                break;
//        }
//    }

}
