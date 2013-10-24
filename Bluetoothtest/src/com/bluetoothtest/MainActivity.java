package com.bluetoothtest;

import java.io.File;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;




public class MainActivity extends Activity {
	
	private static final int REQUEST_ENABLE_BT = 1;
	
    ListView listDevicesFound;
	Button btnScanDevice;
	Button send;
	TextView stateBluetooth;
	BluetoothAdapter bluetoothAdapter;
	public int i;
	ArrayAdapter<String> btArrayAdapter;
	BluetoothDevice device;
	String address;
	String tag="test";
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(tag,"oncreat");
        btnScanDevice = (Button)findViewById(R.id.scandevice);
        send = (Button)findViewById(R.id.send);
        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        listDevicesFound = (ListView)findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        
        CheckBlueToothState();
        
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        
        send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e(tag,"clicked on send");
				String filePath = Environment.getExternalStorageDirectory().toString() + "/bluetooth/IMG-20130917-WA0001.jpg";

				ContentValues values = new ContentValues();
				values.put(BluetoothShare.URI, Uri.fromFile(new File(filePath)).toString());
				values.put(BluetoothShare.DESTINATION, address);
				values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
				Long ts = System.currentTimeMillis();
				values.put(BluetoothShare.TIMESTAMP, ts);
				Uri contentUri = getContentResolver().insert(BluetoothShare.CONTENT_URI, values);
				Log.e("send clicked","send"+filePath+""+address);
				
			}       	
        	
        });
        
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
	}

	private void CheckBlueToothState(){
    	if (bluetoothAdapter == null){
        	stateBluetooth.setText("Bluetooth NOT support");
        }else{
        	if (bluetoothAdapter.isEnabled()){
        		if(bluetoothAdapter.isDiscovering()){
        			stateBluetooth.setText("Bluetooth is currently in device discovery process.");
        		}else{
        			stateBluetooth.setText("Bluetooth is Enabled.");
        			btnScanDevice.setEnabled(true);
        		}
        	}else{
        		stateBluetooth.setText("Bluetooth is NOT Enabled!");
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}
        }
    }
    
    private Button.OnClickListener btnScanDeviceOnClickListener= new Button.OnClickListener(){
    	
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			i=1;
			btArrayAdapter.clear();
			bluetoothAdapter.startDiscovery();
			//BluetoothDevice device;
			
		}};
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQUEST_ENABLE_BT){
			CheckBlueToothState();
		}
	}
   
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.e(tag,"scan called");
			String action = intent.getAction();
			if(i==1){
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            btArrayAdapter.notifyDataSetChanged();
	            address=device.getAddress();
	            i++;
			}
	        }
		}};
    
}
