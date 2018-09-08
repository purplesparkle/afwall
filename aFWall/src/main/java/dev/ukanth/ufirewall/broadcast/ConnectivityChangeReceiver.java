/**
 * Detect the connectivity changes (for roaming and LAN subnet changes)
 * <p>
 * Copyright (C) 2011-2012  Umakanthan Chandran
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Umakanthan Chandran
 * @version 1.0
 */
package dev.ukanth.ufirewall.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dev.ukanth.ufirewall.log.Log;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private ConnectivityReceiverListener mConnectivityReceiverListener;

    public static final String TAG = "AFWall";

    // These are marked "@hide" in WifiManager.java
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";

    public ConnectivityChangeReceiver(ConnectivityReceiverListener listener) {
        mConnectivityReceiverListener = listener;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(WIFI_AP_STATE_CHANGED_ACTION)) {
            int newState = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
            int oldState = intent.getIntExtra(EXTRA_PREVIOUS_WIFI_AP_STATE, -1);
            Log.d(TAG, "OS reported AP state change: " + oldState + " -> " + newState);
        }
        // NOTE: this gets called for wifi/3G/tether/roam changes but not VPN connect/disconnect
        // This will prevent applying rules when the user disable the option in preferences. This is for low end devices
        if (intent.getAction().equals(WIFI_AP_STATE_CHANGED_ACTION)) {
            int newState = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
            int oldState = intent.getIntExtra(EXTRA_PREVIOUS_WIFI_AP_STATE, -1);
            Log.d(TAG, "OS reported AP state change: " + oldState + " -> " + newState);
        }
        Log.i(TAG, "Network change captured.");
        mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}