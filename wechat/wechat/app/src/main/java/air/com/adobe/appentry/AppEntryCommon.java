package air.com.adobe.appentry;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.openapi.Constants;

/**
 * Created by muskong on 2017/7/21.
 *
 * 此方法主要实现Android的生命周期onNewIntent和onCreate
 */

public class AppEntryCommon extends AppEntry {

    /**
     * onNewIntent
     * @param aIntent
     */
    @Override
    protected void onNewIntent(Intent aIntent) {
        super.onNewIntent(aIntent);

        Log.i(Constants.tag, " air.com.adobe.appentry:onNewIntent ");
    }

    /**
     * oncreate
     * @param savedInstanceState
     * @param persistentState
     */
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Log.i(Constants.tag, " air.com.adobe.appentry:onCreate ");
    }
}
