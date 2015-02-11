package unicorn.ertech.chroom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timur on 08.01.2015.
 */
public class AdsFragment extends Fragment {
        private Context context;
        public int pageNumber;
        Button butSmile;
        EditText txtSend;
        int msgCount;
        ListView lvChat;
        JSONObject json;
        Timer myTimer;
        boolean firsTime;
        String URL = "http://im.topufa.org/index.php";
        String lastID1, lastID2,lastID3,lastID4, msgNum, room, outMsg, token, myID;
        List<chatMessage> messages = new ArrayList<chatMessage>();
        private ArrayList<HashMap<String, Object>> citiList;
        final String SAVED_CITY = "city";
        chatAdapter adapter;

        /** Handle the results from the voice recognition activity. */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        pageNumber=0;
        token = Main.str;
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_CITY)){
            room=Integer.toString(sPref.getInt(SAVED_CITY,11));
        }else{
            room="11";
        }
        // messagesNews.add(0,"News");
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                Log.e("tokenBeforeRequest", token);
                if (isNetworkAvailable()) {
                            //room = "11";
                            new globalChat1().execute();
                }
                else
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 2L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_blank, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        Button butSend = (Button) view.findViewById(R.id.button2);
        butSmile = (Button) view.findViewById(R.id.butSmile);
        lvChat = (ListView)view.findViewById(R.id.lvChat);
        txtSend = (EditText) view.findViewById(R.id.sendText);
        firsTime = true;
        token = Main.str;
        //room = "11";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        //myID = "1";
        //listArr = new ArrayList<String>();
        citiList = new ArrayList<HashMap<String, Object>>();

        ///lstAdptr = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listArr);
        adapter = new chatAdapter(messages,getActivity().getApplicationContext());

        HashMap<String, Object> hm;

        lvChat.setAdapter(adapter);
        //tvPage.setText("Page " + pageNumber);
        //tvPage.setBackgroundColor(backColor);

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

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        //room = "11";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);

                txtSend.setText("");
                //lstAdptr.notifyDataSetChanged();
            }
        });

        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopupWindow();
            }
        });
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
            //jParser.setParam("userid", myID);
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("receivedjson", "2222");
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

    private class globalChat1 extends AsyncTask<String, String, JSONObject> {
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
            jParser.setParam("lastid", lastID1);
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("sendjson", "2222");
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
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
                Log.e("lastID", lastID1);
                if (lastID1.equals(lastid)) {
                    lastID1 = json.getString("lastid");
                } else {
                    flag = true;
                    lastID1 = json.getString("lastid");
                    msgNum = json.getString("total");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (flag && Integer.parseInt(msgNum) != 0) {
                Log.e("sendjson", "3333loop");
                for (int i = 0; i < Integer.parseInt(msgNum); i++) {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messagads", messag.toString());
                        if (firsTime) {
                            messages.add(msgCount, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"("+messag.getString("age")+")", messag.getString("message"), messag.getString("avatar")));

                        } else {
                            messages.add(0, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"("+messag.getString("age")+")", messag.getString("message"), messag.getString("avatar")));
                        }
                        msgCount++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("NullPointerException", e.toString());
                    }
                }
                Log.e("sendjson", "loop4");

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


    private PopupWindow pwindo;
    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private void initiatePopupWindow() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View view = inflater.inflate(R.layout.fragment_blank, container, false);
            View layout = inflater.inflate(R.layout.smile_popup,  null, false);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int window_width = metricsB.widthPixels;
            int window_height = metricsB.heightPixels;
            pwindo = new PopupWindow(layout, window_width, window_height, true);
            pwindo.showAsDropDown(butSmile,0,-window_height);
            Button smileCancel = (Button) layout.findViewById(R.id.button3);
            smileCancel.setOnClickListener(smile_click_listener);
            layout.setOnClickListener(smile_click_listener);
            s01 = (ImageView) layout.findViewById(R.id.s01);
            s01.setOnClickListener(smile_click_listener);
            s02 = (ImageView) layout.findViewById(R.id.s02);
            s02.setOnClickListener(smile_click_listener);
            s03 = (ImageView) layout.findViewById(R.id.s03);
            s03.setOnClickListener(smile_click_listener);
            s04 = (ImageView) layout.findViewById(R.id.s04);
            s04.setOnClickListener(smile_click_listener);
            s05 = (ImageView) layout.findViewById(R.id.s05);
            s05.setOnClickListener(smile_click_listener);
            s06 = (ImageView) layout.findViewById(R.id.s06);
            s06.setOnClickListener(smile_click_listener);
            s07 = (ImageView) layout.findViewById(R.id.s07);
            s07.setOnClickListener(smile_click_listener);
            s08 = (ImageView) layout.findViewById(R.id.s08);
            s08.setOnClickListener(smile_click_listener);
            s09 = (ImageView) layout.findViewById(R.id.s09);
            s09.setOnClickListener(smile_click_listener);
            s10 = (ImageView) layout.findViewById(R.id.s10);
            s10.setOnClickListener(smile_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
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
                    pwindo.dismiss();
                    break;
            }
            txtSend.setText(getSmiledText(getActivity(),txtSend.getText()));
        }
    };


    public void onResume(){
        super.onResume();
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_CITY)){
            String tmp = Integer.toString(sPref.getInt(SAVED_CITY,11));
            if(!room.equals(tmp)){
                room=tmp;
                lastID1="0";
                adapter.notifyDataSetChanged();
            }
        }
        /*myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                Log.e("tokenBeforeRequest", token);
                if (isNetworkAvailable()) {
                    room = "1";
                    new globalChat1().execute();
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 2L * 1000);*/
    }
    /*
    @Override
    public void onPause(){
        myTimer.cancel();
        super.onPause();
    }*/

    //
    //Ниже часть, связанная с отображением смайлов в edittext
    //

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
