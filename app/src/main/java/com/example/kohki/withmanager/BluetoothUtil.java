package com.example.kohki.withmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public class BluetoothUtil {
    //Bluetoothアダプター取得
    private final BluetoothAdapter btAdapter;
    private final Set<BluetoothDevice> btDevices;

    public BluetoothUtil(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevices = btAdapter == null ? null : btAdapter.getBondedDevices();
    }

    public boolean isSpported(){
        return btAdapter != null;
    }

    public boolean isEnabled(){
        return isSpported() && btAdapter.isEnabled();
    }

    public int getPairingCount(){
        return isSpported() ? btDevices.size() : -1;
    }

    public BluetoothDevice[] getDevices(){
        if(!isSpported())
            return null;

        int i = 0;
        BluetoothDevice[] devices = new BluetoothDevice[btDevices.size()];
        for (BluetoothDevice device : btDevices){
            devices[i] = device;
            i++;
        }

        return devices;
    }

    public String[] getDeviceNames(){
        if(!isSpported())
            return null;

        int i = 0;
        String[] deviceNames = new String[btDevices.size()];
        for (BluetoothDevice device : btDevices){
            deviceNames[i] = device.getName();
            i++;
        }

        return deviceNames;
    }
}
