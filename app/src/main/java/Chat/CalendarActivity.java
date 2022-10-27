package Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.soomgodev.R;

public class CalendarActivity extends AppCompatActivity {

    public CalendarView calendarView;
    public Button save_Btn;
    public String dateString = null;
    String chatRoomNumber, finalPrice;


    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    private static final String TAG = "CalendarActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getSupportActionBar().hide();

        calendarView = findViewById(R.id.calendarView);
        save_Btn = findViewById(R.id.save_Btn);


        // (onCreate 안에)
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");


        Intent intent = getIntent();
        chatRoomNumber = intent.getStringExtra("chatRoomNumber");
        finalPrice = intent.getStringExtra("finalPrice");

        long now = System.currentTimeMillis();
        calendarView.setDate(now);

        // 캘린더를 띄운다.
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                save_Btn.setVisibility(View.VISIBLE);
                dateString = String.format("%d-%d-%d", year, month + 1, dayOfMonth);
            }
        });

        // 저장하기 버튼
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_Btn.setVisibility(View.INVISIBLE);

                editor.putString(userSeq+"selectedDate", dateString);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), DealActivity.class);
//                intent.putExtra("selectedDate", dateString);
                intent.putExtra("chatRoomNumber", chatRoomNumber);
                intent.putExtra("finalPrice", finalPrice);
                Log.i(TAG, "finalPrice = " + finalPrice);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });
    }
}