package com.xzh.bluetoothchatdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by zhenghangxia on 17-5-26.
 */
public class DeviceListActivity extends Activity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private Button scanButton;
    private ArrayAdapter<String> mPairedDevicesAdapter;
    private ArrayAdapter<String> mNewDevicesAdapter;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        setResult(Activity.RESULT_CANCELED);
        initView();

    }

    private void initView() {

        scanButton = (Button) findViewById(R.id.button_scan);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                discoverDevice();
            }
        });

        mPairedDevicesAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        mNewDevicesAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);

        // 已经绑定的设备
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 搜索到的可用设备
        ListView newListView = (ListView) findViewById(R.id.new_devices);
        newListView.setAdapter(mNewDevicesAdapter);
        newListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        // 获取默认的蓝牙adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 获取当前可用的蓝牙设置
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired)
                    .toString();
            mPairedDevicesAdapter.add(noDevices);
        }
    }

    /**
     * 扫描本地可用的设备
     */
    private void discoverDevice() {

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        //如果正在扫描，先停止扫描，再重新扫描
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();

    }

    /**
     * 监听搜索到的设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(!TextUtils.isEmpty(device.getName())){
                        mNewDevicesAdapter.add(device.getName() + "\n"
                                    + device.getAddress());
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found_device).toString();
                    mNewDevicesAdapter.add(noDevices);
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

}
