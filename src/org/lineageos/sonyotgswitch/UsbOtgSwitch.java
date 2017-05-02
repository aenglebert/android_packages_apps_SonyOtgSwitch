/*
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.sonyotgswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import cyanogenmod.providers.CMSettings;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.util.Log;

public class UsbOtgSwitch extends TileService {
    private static boolean mUsbOtg = false;

    @Override
    public void onStartListening() {
        super.onStartListening();
        refresh();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
	if(mUsbOtg)
	{
		disableUsbOtg();
	        Log.d("OtgSwitch", "disable");
	}
	else 
	{
		enableUsbOtg();
	        Log.d("OtgSwitch", "enable");
	}
        refresh();
    }

    /* enableUsbOtg()
     * 
     * Enable usb otg by setting /sys/module/qpnp_smbcharger_extension/parameters/force_id_polling_on to 1,
     * this force usb otg detection to stay enabled once activated.
     * Then start the dection by setting /sys/module/qpnp_smbcharger_extension/parameters/start_id_polling to 1.
     */
    public static void enableUsbOtg() {
	    PrintWriter startWriter = null;
	    PrintWriter forceWriter = null;
	    try {
		    FileOutputStream forcePollingOutStream = new FileOutputStream("/sys/module/qpnp_smbcharger_extension/parameters/force_id_polling");
		    FileOutputStream startPollingOutStream = new FileOutputStream("/sys/module/qpnp_smbcharger_extension/parameters/start_id_polling");
		    forceWriter = new PrintWriter(new OutputStreamWriter(forcePollingOutStream));
		    startWriter = new PrintWriter(new OutputStreamWriter(startPollingOutStream));
		    forceWriter.println("1");
		    startWriter.println("1");
    	    } catch (Exception e) {
	    } finally {
		    if (forceWriter != null)
			    forceWriter.close();
		    if (startWriter != null)
			    startWriter.close();
	    }
	    mUsbOtg = true;
        }


    /* disableUsbOtg()
     * 
     * Disable usb otg by setting /sys/module/qpnp_smbcharger_extension/parameters/force_id_polling to 0,
     * this force usb otg detection disable once the timeout is done.
     * Also set the the timeout to 1ms for a faster disable
     */
    public static void disableUsbOtg() {
	    PrintWriter timeoutWriter = null;
	    PrintWriter forceWriter = null;
	    try {
		    FileOutputStream timeoutOutStream = new FileOutputStream("/sys/module/qpnp_smbcharger_extension/parameters/id_polling_timeout");
		    FileOutputStream forcePollingOutStream = new FileOutputStream("/sys/module/qpnp_smbcharger_extension/parameters/force_id_polling");
		    timeoutWriter = new PrintWriter(new OutputStreamWriter(timeoutOutStream));
		    forceWriter = new PrintWriter(new OutputStreamWriter(forcePollingOutStream));
		    timeoutWriter.println("1");
		    forceWriter.println("0");
    	    } catch (Exception e) {
	    } finally {
		    if (timeoutWriter != null)
			    timeoutWriter.close();
		    if (forceWriter != null)
			    forceWriter.close();
	    }
	    mUsbOtg = false;
        }


    private void refresh() {
        if (mUsbOtg) {
            getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_usb_otg_on));
            getQsTile().setState(Tile.STATE_ACTIVE);
        } else {
            getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_usb_otg_off));
            getQsTile().setState(Tile.STATE_INACTIVE);
        }
        getQsTile().updateTile();
    }

}
