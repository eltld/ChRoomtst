package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetBlackList extends Activity {
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    RelativeLayout topRow;
    Button butBack;
    public static ListView lvBlackList;
    public static List<BlackListItem> blackList = new ArrayList<BlackListItem>();
    public static BlackListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_blacklist);
        topRow=(RelativeLayout)findViewById(R.id.topRow);
        butBack=(Button)findViewById(R.id.setBack);
        lvBlackList=(ListView)findViewById(R.id.lvBlackList);
        adapter = new BlackListAdapter(blackList,this);
        lvBlackList.setAdapter(adapter);
        setColor();

        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        blackList.clear();

        lvBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {

                String UserId = adapter.getItem(position).id;
                String nick = adapter.getItem(position).name;
                Intent i = new Intent(getApplicationContext(),Profile2.class);
                i.putExtra("userId",UserId);
                i.putExtra("token",Main.str);
                i.putExtra("nick",nick);
                i.putExtra("avatar",adapter.getItem(position).avatar);
                startActivity(i);

            }
        });

        new getBlackList().execute();
    }

    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        int col=sPref.getInt(SAVED_COLOR, 0);
            switch (col) {
                case 0:
                    topRow.setBackgroundResource(R.color.green);
                    butBack.setBackgroundResource(R.color.green);
                    break;
                case 1:
                    topRow.setBackgroundResource(R.color.blue);
                    butBack.setBackgroundResource(R.color.blue);
                    break;
                case 2:
                    topRow.setBackgroundResource(R.color.orange);
                    butBack.setBackgroundResource(R.color.orange);
                    break;
                case 3:
                    topRow.setBackgroundResource(R.color.purple);
                    butBack.setBackgroundResource(R.color.purple);
                    break;
                default:
                    break;
            }
    };

    public void closeMe(){
        this.finish();
    }

    public static void removeFromList(String ID)
    {
        for(int i=0; i<blackList.size();i++)
        {
            if(blackList.get(i).id.equals(ID))
            {
                blackList.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private static class getBlackList extends AsyncTask<String, String, JSONObject> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected JSONObject doInBackground(String... args) {
        JSONParser jParser = new JSONParser();

        //ставим нужные нам параметры
        jParser.setParam("token", Main.str);
        jParser.setParam("action", "list_get");
        jParser.setParam("list", "2");
        // Getting JSON from URL
        JSONObject json = jParser.getJSONFromUrl(Main.URL);
        return json;
    }
    @Override
    protected void onPostExecute(JSONObject json) {
        if(json!=null) {
            String status = null;
            String num = null;
            JSONArray arr = null;
            JSONObject messag = null;
            try {
                status = json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("false"))
            {
                try {
                    num = json.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!num.equals("0"))
                {
                    try {
                        arr = json.getJSONArray("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < Integer.parseInt(num); i++) {
                        try {
                            messag = new JSONObject(arr.get(i).toString());
                            BlackListItem p = new BlackListItem(messag.getString("id"), messag.getString("name"), messag.getString("avatar"));
                            blackList.add(0, p);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }

        }

    }
}//конец asyncTask
}