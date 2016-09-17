package com.example.kohki.withmanager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/***************************
 * Bluetooth通信クラス
 ***************************/
public class BluetoothConnection {
    //Bluetooth通信方式
    private static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //ソケット
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket clientSocket;

    //入出力
    private InputStream in;
    private OutputStream out;

    /***************************
     * Bluetoothデバイスに接続
     *
     * @param targetDevice 接続対象デバイス
     * @return 接続成功時、trueを返す。
     ***************************/
    @SuppressLint("NewApi")
    public boolean connectToServer(BluetoothDevice targetDevice) {
        try {
            //クライアント作成
            clientSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);

            //セキュア？
            //clientSocket = targetDevice.createRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);

            //サーバー側に接続要求
            clientSocket.connect();

            //入出力取得
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
        } catch (IOException e) {
            close(); //close処理
            return false;
        }

        return true;
    }


    /***************************
     * サーバーモードで接続待機
     *
     * @param bluetoothAdapter 自身のBluetoothアダプタ
     * @return 接続成功時、trueを返す。
     ***************************/
    public boolean makeServer(BluetoothAdapter bluetoothAdapter) {
        try{
            //サーバー作成
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth Connection", TECHBOOSTER_BTSAMPLE_UUID);

            // クライアント側からの接続要求待ち。ソケットが返される。
            clientSocket = serverSocket.accept();

            //入出力取得
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
        }
        catch (IOException e) {
            //close処理
            close();
            return false;
        }
        finally{
            //close処理
            close(serverSocket);
            serverSocket = null;
        }

        return true;
    }


    /***************************
     * Close
     ***************************/
    public void close(){
        close(serverSocket);
        serverSocket = null;

        close(in);
        in = null;

        close(out);
        out = null;

        close(clientSocket);
        clientSocket = null;
    }

    /***************************
     * バイト送信
     *
     * @param object シリアライズされた送信データ。
     * @return データ送信成功時、trueを返す。
     ***************************/
    public boolean writeObject(final byte[] buffer) {
        synchronized(out){
            try{
                out.write(buffer);
            }
            catch(Exception e){
                return false;
            }
            return true;
        }
    }


    /***************************
     * バイト受信
     *
     * @return 受信したシリアライズデータを返す。受信失敗時はnullを返す。
     ***************************/
    public int readObject(){
        try{
            return in.read();
        }
        catch(Exception e){
        }
        return 0;
    }



    /***************************
     * データ送信
     *
     * @param b 0～255の送信データ。
     * @return データ送信成功時、trueを返す。
     ***************************/
    public boolean write(int b){
        try{
            out.write(b);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    /***************************
     * データ受信
     *
     * @return 0～255の受信データを返す。データ受信失敗時、-1を返す。
     ***************************/
    public int read(){
        try{
            return in.read();
        }
        catch(Exception e){
            return -1;
        }
    }


    /***************************
     * 入出力のclose処理
     *
     * @param closeable close処理する入出力
     ***************************/
    private synchronized void close(Closeable closeable){
        if(closeable != null){
            try{
                closeable.close();
            }
            catch(IOException e){}
        }
    }
}
