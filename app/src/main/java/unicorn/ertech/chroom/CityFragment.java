package unicorn.ertech.chroom;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timur on 08.01.2015.
 */
public class CityFragment extends android.support.v4.app.Fragment {
    private Context context;
    EditText txtSend;
    ImageButton butSmile;
    public int pageNumber;
    RelativeLayout rellay;
    int backColor;
    int msgCount;
    ListView lvChat;
    JSONObject json;
    Timer myTimer;
    boolean firsTime;
    boolean stopTImer = false ;
    List<chatMessage> messages = new ArrayList<chatMessage>();
    String URL = "http://im.topufa.org/index.php";
    String lastID1, lastID2,lastID3,lastID4, msgNum="0", room, outMsg, token, myID,deleted_total="0" ;
    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private ArrayList<HashMap<String, Object>> regionList;
    private static final String TITLE = "message_author"; // Верхний текст
    private static final String DESCRIPTION = "message_body"; // ниже главного
    private static final String ICON = "avatar";  // будущая картинка
    chatAdapter adapter;
    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        pageNumber=1;
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        // messagesNews.add(0,"News");
        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("regSrv")){
            room=Integer.toString(sPref.getInt("regSrv",3296));
        }else{
            room="3296";
        }
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    //room = "3296";
                    if(!stopTImer) {
                        new globalChat2().execute();
                    }
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 4L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_blank2, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        final ImageButton butSend = (ImageButton) view.findViewById(R.id.button22);
        butSmile = (ImageButton) view.findViewById(R.id.butSmile2);
        lvChat = (ListView)view.findViewById(R.id.lvChat2);
        txtSend = (EditText) view.findViewById(R.id.editText1);
        firsTime = true;
        //token = Main.str;
        //room = "10";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        myID = "18";deleted_total = "";

        regionList = new ArrayList<HashMap<String, Object>>();

        adapter = new chatAdapter(messages,getActivity().getApplicationContext());
        HashMap<String, Object> hm;
        lvChat.setAdapter(adapter);
        final TableLayout smileTable = (TableLayout)view.findViewById(R.id.smileTable2);
        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_animation);
        //rellay=(RelativeLayout)view.findViewById(R.id.relLay1);
        //Animation anim2 = AnimationUtils.loadAnimation(getActivity(), )
        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {

                String UserId = adapter.getItem(position).uid;
                String nick = adapter.getItem(position).from;
                Intent i = new Intent(getActivity(),Profile2.class);
                i.putExtra("userId",UserId);
                i.putExtra("token",token);
                i.putExtra("nick",nick);
                i.putExtra("avatar",adapter.getItem(position).picURL);
                //messages.remove(position);
                //adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });
        butSmile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //smileTable.startAnimation(anim);
                if(smileTable.getVisibility()==View.GONE){
                    smileTable.setVisibility(View.VISIBLE);
                }else{
                    smileTable.setVisibility(View.GONE);
                }
                butSend.refreshDrawableState();
                butSmile.refreshDrawableState();
                txtSend.refreshDrawableState();
                //RelativeLayout.LayoutParams params = rellay.getLayoutParams();
                //butSend.setLayoutParams(butSmile.getLayoutParams());
                //butSmile.setTop(butSmile.getTop() - smileTable.getLayoutParams().height);
                //butSend.setTop(butSmile.getTop());
                //txtSend.setTop(butSmile.getTop());
            }
        });
        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                outMsg = txtSend.getText().toString();
                new OutMsg().execute();
                imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                smileTable.setVisibility(View.GONE);
                txtSend.setText("");
            }
        });

        findSmiles(view);
        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private class OutMsg extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "globe_send");
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String status = "";

            try {
                status = json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("false"))
            {
                Toast.makeText(getActivity().getApplicationContext(),"Сообщение успешно добавлено!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class globalChat2 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID2);
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {

            JSONArray deleted = null;
            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("total");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber + "");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID2);
                if (lastID2.equals(lastid)) {
                    lastID2 = json.getString("lastid");
                } else {
                    flag = true;
                    lastID2 = json.getString("lastid");
                    msgNum = json.getString("total");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    deleted_total = json.getString("total-deleted");
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (flag && (!msgNum.equals("0")) || (!deleted_total.equals("0"))) {
                if(!deleted_total.equals("0"))
                {
                    try {
                        s = json.getString("deleted");
                        deleted = new JSONArray(s);
                        if(!deleted_total.equals("")) {
                            for (int i = 0; i < Integer.parseInt(deleted_total); i++) {
                                checkInList(deleted.get(i).toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < Integer.parseInt(msgNum); i++) {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messagcity", messag.toString());
                        if (firsTime) {
                            messages.add(msgCount, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));

                        } else {
                            messages.add(0, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));

                        }
                        msgCount++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("NullPointerException", e.toString());
                    }
                }

                adapter.notifyDataSetChanged();

                firsTime = false;


            }

        }

        }
    }

    @Override
    public void onDestroy(){
        myTimer.cancel();
        Log.e("json", "destroy");
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("regSrv")){
            String tmp = Integer.toString(sPref.getInt("regSrv",3296));
            if(!room.equals(tmp)){
                room=tmp;
                firsTime=true;
                msgCount=0;
                messages.clear();
                lastID2="0";
                adapter.notifyDataSetChanged();
            }
        }
        stopTImer=false;
    }

    @Override
    public void onPause(){
        stopTImer=true;
        super.onPause();
    }

    private void findSmiles(View view){
        s01 = (ImageView) view.findViewById(R.id.s01);
        s01.setOnClickListener(smile_click_listener);
        s02 = (ImageView) view.findViewById(R.id.s02);
        s02.setOnClickListener(smile_click_listener);
        s03 = (ImageView) view.findViewById(R.id.s03);
        s03.setOnClickListener(smile_click_listener);
        s04 = (ImageView) view.findViewById(R.id.s04);
        s04.setOnClickListener(smile_click_listener);
        s05 = (ImageView) view.findViewById(R.id.s05);
        s05.setOnClickListener(smile_click_listener);
        s06 = (ImageView) view.findViewById(R.id.s06);
        s06.setOnClickListener(smile_click_listener);
        s07 = (ImageView) view.findViewById(R.id.s07);
        s07.setOnClickListener(smile_click_listener);
        s08 = (ImageView) view.findViewById(R.id.s08);
        s08.setOnClickListener(smile_click_listener);
        s09 = (ImageView) view.findViewById(R.id.s09);
        s09.setOnClickListener(smile_click_listener);
        s10 = (ImageView) view.findViewById(R.id.s10);
        s10.setOnClickListener(smile_click_listener);
    }

    public void checkInList(String ID) {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).messageID.equals(ID))
            {
                messages.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }
    private View.OnClickListener smile_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.s01:
                    txtSend.append(":)");
                    break;
                case R.id.s02:
                    txtSend.append(":D");
                    break;
                case R.id.s03:
                    txtSend.append(":O");
                    break;
                case R.id.s04:
                    txtSend.append(":(");
                    break;
                case R.id.s05:
                    txtSend.append("*05*");
                    break;
                case R.id.s06:
                    txtSend.append("Z)");
                    break;
                case R.id.s07:
                    txtSend.append("*07*");
                    break;
                case R.id.s08:
                    txtSend.append("*08*");
                    break;
                case R.id.s09:
                    txtSend.append("*09*");
                    break;
                case R.id.s10:
                    txtSend.append("*love*");
                    break;
                default:
                    break;
            }
            txtSend.setText(getSmiledText(getActivity(),txtSend.getText()));
        }
    };

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ":)", R.drawable.s01);
        addPattern(emoticons, ":D", R.drawable.s02);
        addPattern(emoticons, ":O", R.drawable.s03);
        addPattern(emoticons, ":(", R.drawable.s04);
        addPattern(emoticons, "*05*", R.drawable.s05);
        addPattern(emoticons, "Z)", R.drawable.s06);
        addPattern(emoticons, "*07*", R.drawable.s07);
        addPattern(emoticons, "*08*", R.drawable.s08);
        addPattern(emoticons, "*09*", R.drawable.s09);
        addPattern(emoticons, "*love*", R.drawable.s10);
        // ...
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }
}
