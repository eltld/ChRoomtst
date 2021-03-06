package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 04.01.2015.
 */
public class Search extends FragmentActivity {
    SearchParam frag1;
    SearchMain frag2;
    SearchRandom frag3;
    ShareNetworks frag4;
    FragmentTransaction fTrans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("searchCreate",)
        setContentView(R.layout.search_main);
        frag1 = new SearchParam();
        frag2 = new SearchMain();
        frag3 = new SearchRandom();
        frag4 = new ShareNetworks();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, frag2);
        //fTrans.addToBackStack(null);
        fTrans.commit();
        Log.i("search", "create");

    }

    public void startParam(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, frag1);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void startRandom(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, frag3);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void startShare(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, frag4);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onResume(){
        super.onResume();
        boolean tmp = frag2.isAdded();
        Log.i("searchResume",Boolean.toString(tmp));
        if(frag2==null){
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fTrans.remove(frag2);
            fTrans.replace(R.id.frgmCont, frag2);
            //fTrans.addToBackStack(null);
            fTrans.commitAllowingStateLoss();
        }
    }

    @Override
    public void onPause(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.remove(frag2);
        super.onPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("search", "start");
    }
}
