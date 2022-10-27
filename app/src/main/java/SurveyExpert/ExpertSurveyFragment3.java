package SurveyExpert;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.soomgodev.R;

public class ExpertSurveyFragment3 extends Fragment {

    Button button;
    ExpertSurveyMainActivity expertSurveyMainActivity;
    private WebView webView;
    String url = serverAddress + "daum.html";
    private String result;
    private static final String TAG = "ExpertSurveyFragment3";


    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {

            Bundle bundle = new Bundle();
            bundle.putString("data", data);
            //java

//            String userNameFromServer = jsonResponse.getString("userName");

            Log.i(TAG, tagServerToClient + "data = " +  data);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ExpertSurveyFragment2 fragment2 = new ExpertSurveyFragment2();
            fragment2.setArguments(bundle);
            transaction.replace(R.id.scrollView, fragment2);
            transaction.commit(); // 저장

        }
    }

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expert_survey3, container, false);

        result = getArguments().getString("fromFrag1");
        Log.e("test", result);

        webView = (WebView) rootView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        webView.loadUrl(url);

        return rootView;

    }
}
