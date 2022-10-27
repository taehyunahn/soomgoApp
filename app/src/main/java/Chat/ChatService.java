package Chat;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.browser.trusted.TokenStore;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.soomgodev.Fragment.Fragment22;
import com.example.soomgodev.Fragment.Fragment4;
import com.example.soomgodev.Fragment.UserMainActivity;
import com.example.soomgodev.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import SurveyRequest.RequestDetail;
import okhttp3.MultipartBody;

public class ChatService extends Service {
    private static final String TAG = "ChatService";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String NOTIFICATION_CHANNEL_ID2 = "10002";

    SharedPreferences already_read_chatroom_seq;
    SharedPreferences.Editor editor;

    public static String NOW_CHAT = ""; // notification 띄울지 여부

    public static Socket member_socket; // 서버와 연결되어있는 소켓 객체
    public static PrintWriter sendWriter;
    public static ChatMember chatMember = new ChatMember(); // 소켓을 통해 서버로 넘길 객체

    int page = 1;

    String chat_room_seq;
    String userName, userSeq, expertSeq, expertName;
    BufferedReader member_bufferedReader;
    PrintWriter member_printWriter;
    Handler handlerHere = new Handler();

    public ChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate 호출");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand 호출");

        new Thread() {
            public void run() {
                int port = 5002;

                try{
                    // 로그인과 동시에 '소켓 생성'
                    Socket socket = new Socket("192.168.56.1", port);
                    member_socket = socket;
                    member_bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    member_printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                    // 소켓연결 스레드
                    ConnectionThread thread = new ConnectionThread(member_socket, member_bufferedReader, member_printWriter);
                    thread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        if (intent != null) {
            userSeq = intent.getStringExtra("userSeq");
            userName = intent.getStringExtra("userName");
            expertName = intent.getStringExtra("expertName");

            Log.i(TAG, "onStartCommand로 현재 로그인한 정보를 전역변수의 값에 넣는다");
            Log.i(TAG, "userSeq = " + userSeq);
            Log.i(TAG, "userName = " + userName);

        } else {
            return Service.START_STICKY; //자동으로 재시작하라는 명령
        }

        return super.onStartCommand(intent, flags, startId);
//        chat_room_seq = intent.getExtras().getString("chat_room_seq"); // 현재 채팅방의 seq 번호
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy 호출");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class ConnectionThread extends Thread {

        Socket socket;
        BufferedReader bufferedReader;
        PrintWriter printWriter;

        public ConnectionThread(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) throws IOException {
            this.socket = socket;
            this.bufferedReader = bufferedReader;
            this.printWriter = printWriter;
        }

        @Override
        public void run() {

            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
            String userSeq = sharedPreferences.getString("userSeq", "");
            String clientOrExpert = sharedPreferences.getString(userSeq + "clientOrExpert", "");
            String userName = sharedPreferences.getString(userSeq + "userName", "");
            String expertName = sharedPreferences.getString(userSeq + "expertName", "");

            Log.i(TAG, "ConnectionThread 에서");
            Log.i(TAG, "clientOrExpert = " + clientOrExpert);
            Log.i(TAG, "userSeq = " + userSeq);
            Log.i(TAG, "userName = " + userName);
            Log.i(TAG, "expertName = " + expertName);



            try {
//                chatMember.setCommand("JOIN"); // 전송 -> 아직 전송이 시작되면 안됨
                if(clientOrExpert.equals("expert")){
                    chatMember.setNickname(expertName); // 닉네임
                } else {
                    chatMember.setNickname(userName); // 닉네임
                }

                chatMember.setUserSeq(userSeq); // 채팅작성자의 seq번호

                Gson gson = new Gson();
                String userJson = gson.toJson(chatMember);

                // 1. (최초 1회) 서버에 데이터 전송 : 1) userName, 2) userSeq
                printWriter.println(userJson);
                printWriter.flush();
                Log.i(TAG, "최초로 서버에 전송한 데이터 : " + userJson);

                // 서버로부터 데이터 수신
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msgFromServer;
                        while(socket.isConnected()){
                            try {
                                msgFromServer = bufferedReader.readLine();
                                Gson gson = new Gson();
                                ChatMember _chatMember = gson.fromJson(msgFromServer, ChatMember.class);

                                try{
                                    if(_chatMember.getOrderType() != null){
                                        // 요청서를 받은 경우에 대한 반응
                                        if(_chatMember.getOrderType().equals("request")){
                                            RequestNotificationThread requestNotificationThread = new RequestNotificationThread(_chatMember);
                                            handlerHere.post(requestNotificationThread);

                                            //견적서를 받은 경우에 대한 반응
                                        } else if (_chatMember.getOrderType().equals("quote")){
                                            QuoteNotificationThread quoteNotificationThread = new QuoteNotificationThread(_chatMember);
                                            handlerHere.post(quoteNotificationThread);

                                        }
                                        // 채팅에 대한 반응
                                        else if (_chatMember.getOrderType().equals("chat")){
                                            // 핸들러를 사용해서 ChatRoomActivity에 위 객체를 리사이클러뷰로 추가하도록 해라!!! 2022.05.20.
                                            ChatRoomActivity.GetMsg chatRoomActivity = new ChatRoomActivity.GetMsg(_chatMember);
                                            if(ChatRoomActivity.mHandler != null){
                                                ChatRoomActivity.mHandler.post(chatRoomActivity);
                                            }

                                            // 채팅방에서 리사이클러뷰를 구동시키라고 명령했듯이, 채팅 목록에서도 리사이클러뷰를 갱신하라고 명령하면 되겠지

                                            Fragment4.RefreshChatRoomListClient fragment4 = new Fragment4.RefreshChatRoomListClient();
                                            if(Fragment4.mHandlerInF4 != null){
                                                Fragment4.mHandlerInF4.post(fragment4);
                                            }

                                            Fragment22.RefreshChatRoomListExpert fragment22 = new Fragment22.RefreshChatRoomListExpert();
                                            if(Fragment22.mHandlerInF22 != null){
                                                Fragment22.mHandlerInF22.post(fragment22);
                                            }

                                            StartNotification startNotification = new StartNotification(_chatMember);
                                            handlerHere.post(startNotification);
                                        }
                                    }
                                }catch (NullPointerException e){

                                }



                            } catch (IOException e ) {
                                closeEverything(bufferedReader, printWriter, socket);
                            }
                        }
                    }
                }).start();


                // 이 역할을 시작하는 시점을 변경해야겠다. -> 채팅방에 접속하는 시점에!
                // 채팅방에 들어갈 때, 이 역할을 주면 어떨까?


                // sendMessage() 역할  ---> 서버로 데이터를 보내야 한다, 보내고 멈춰야 한다.
                while (true){ // 소켓 서버에서 전달해주는 객체를 계속 받음
                    sendWriter = new PrintWriter(socket.getOutputStream(), true);
                    break;
                }

            } catch (Exception e) {
                closeEverything(bufferedReader, printWriter, socket);
            }
        }
    }

    // 예외처리를 위한 함수
    public void closeEverything(BufferedReader bufferedReader, PrintWriter printWriter, Socket socket){
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(printWriter != null) {
                printWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class StartNotification implements Runnable {

        private final ChatMember chatMember;

        public StartNotification(ChatMember chatMember) {
            this.chatMember = chatMember;
        }

        @Override
        public void run() {
            Log.i(TAG, "StartNotification(Runnable) : 실행");

            SharedPreferences sharedPreferences9 = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
            String nameForNoti = chatMember.getNickname();
            String msgForNoti = chatMember.getMessage();
            String chatRoomForNoti = chatMember.getChat_room_seq();
            String recipientForNoti = chatMember.getRecipient();
            String currentRoomNumber = sharedPreferences9.getString(recipientForNoti + "currentRoomNumber", "no");

            String receiverStatus = "";
            if(chatMember.getClientOrExpert() != null){
                if(chatMember.getClientOrExpert().equals("client")){
                    receiverStatus = "expert";
                } else {
                    receiverStatus = "client";
                }
            }

            if(!currentRoomNumber.equals(chatRoomForNoti)){
                Log.i(TAG, "userSeq = " + userSeq);
                Log.i(TAG, "recipientForNoti = " + recipientForNoti);
                if(userSeq.equals(recipientForNoti)){
                    NotificationSomethings(nameForNoti, msgForNoti, chatRoomForNoti, receiverStatus);
                    Log.i(TAG, "NotificationSomethings() 동작했습니다");
                }
            }
        }
    }


    public void NotificationSomethings(String name, String msg, String chat_room_seq, String receiverStatus) {

        // 채널을 생성 및 전달해 줄수 있는 NotificationManager를 생성한다.
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // 이동하려는 액티비티를 작성해준다.
        Intent notificationIntent = new Intent(this, ChatRoomActivity.class);
        // 노티를 눌러서 이동시 전달할 값을 담는다. // 전달할 값을 notificationIntent에 담습니다.
        notificationIntent.putExtra("chat_room_seq", chat_room_seq);
        Log.i(TAG, "NotificationSomethings() 할 때, clientOrExpert 값은? = " + receiverStatus);
        notificationIntent.putExtra("clientOrExpert", receiverStatus);
        notificationIntent.putExtra("peterTest", "값이 전달되는지 확인하기 위한 test 결과 value");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_NO_CREATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(name)
//                .setContentText("상태바 드래그시 보이는 서브타이틀 " + count)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true); // 눌러야 꺼지는 설정

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 우선순위 설정

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작
        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // 고유숫자로 노티피케이션 동작


    }

    public class RequestNotificationThread implements Runnable {

        private final ChatMember chatMember;

        public RequestNotificationThread(ChatMember chatMember) {
            this.chatMember = chatMember;
        }

        @Override
        public void run() {
            Log.i(TAG, "RequestNotificationThread(Runnable) : 실행");

            SharedPreferences sharedPreferences1 = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
            String test = sharedPreferences1.getString("expertSeq", "");
            String nameForNoti = chatMember.getNickname();
            String receiverExpertId = chatMember.getExpertSeq();


            if(test.equals(receiverExpertId)){
                requestNotificationMethod(nameForNoti, receiverExpertId);
                Log.i(TAG, "requestNotificationMethod() 동작했습니다");
            }
        }
    }

    public void requestNotificationMethod(String name, String expertSeq) {

        // 채널을 생성 및 전달해 줄수 있는 NotificationManager를 생성한다.
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // 이동하려는 액티비티를 작성해준다.
        Intent notificationIntent = new Intent(this, RequestDetail.class);
        // 노티를 눌러서 이동시 전달할 값을 담는다. // 전달할 값을 notificationIntent에 담습니다.
        notificationIntent.putExtra("expertId", expertSeq);

//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_NO_CREATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(name + " 고객님께서 견적 요청서를 보내셨습니다.")
//                .setContentText("상태바 드래그시 보이는 서브타이틀 " + count)
//                .setContentText("msg")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true); // 눌러야 꺼지는 설정

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 우선순위 설정

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID2, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // 고유숫자로 노티피케이션 동작


    }


    public class QuoteNotificationThread implements Runnable {

        private final ChatMember chatMember;

        public QuoteNotificationThread(ChatMember chatMember) {
            this.chatMember = chatMember;
        }

        @Override
        public void run() {
            Log.i(TAG, "QuoteNotificationThread(Runnable) : 실행");

            SharedPreferences sharedPreferences1 = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);


            String test = sharedPreferences1.getString("userSeq", "");
            String nameForNoti = chatMember.getNickname();
            String receiverUserId = chatMember.getUserIdWhoSent();
            String chatRoomNumber = chatMember.getChat_room_seq();

            Log.i(TAG, "test = " + test);
            Log.i(TAG, "nameForNoti = " + nameForNoti);
            Log.i(TAG, "receiverUserId = " + receiverUserId);
            Log.i(TAG, "chatRoomNumber = " + chatRoomNumber);


            if(test.equals(receiverUserId)){
                quoteNotificationMethod(nameForNoti, receiverUserId, chatRoomNumber);
                Log.i(TAG, "requestNotificationMethod() 동작했습니다");
            }
        }
    }


    public void quoteNotificationMethod(String name, String userSeq, String chatRoomNumber) {

        Log.i(TAG, "quoteNotificationMethod() 메소드 내부 도착");

        // 채널을 생성 및 전달해 줄수 있는 NotificationManager를 생성한다.
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // 이동하려는 액티비티를 작성해준다.
        Intent notificationIntent = new Intent(this, ChatRoomActivity.class);
        // 노티를 눌러서 이동시 전달할 값을 담는다. // 전달할 값을 notificationIntent에 담습니다.
        notificationIntent.putExtra("expertId", userSeq);
        notificationIntent.putExtra("chat_room_seq", chatRoomNumber);
        notificationIntent.putExtra("clientOrExpert", "client");

        Log.i(TAG, "userSeq = " + userSeq);
        Log.i(TAG, "chatRoomNumber = " + chatRoomNumber);

//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_NO_CREATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(name + " 고수님께서 견적서를 보내셨습니다.")
//                .setContentText("상태바 드래그시 보이는 서브타이틀 " + count)
//                .setContentText("msg")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true); // 눌러야 꺼지는 설정

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 우선순위 설정

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID2, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // 고유숫자로 노티피케이션 동작


    }


}