package org.twoboysandhats.androidnfcemulator

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var serviceButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    
        val ipText: TextView = findViewById(R.id.ip_text)
        ipText.text = "Server IP: " + getIPAddress()

        serviceButton = findViewById(R.id.service_button)
        serviceButton?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == serviceButton) {
            // Toggle the service
            val serviceIntent: Intent = Intent(this, NFCServerService::class.java)

            if (getServiceState(NFCServerService::class.java)) {
                // Service is running, stop the service
                stopService(serviceIntent)
                serviceButton?.text = resources.getString(R.string.service_on)
            } else {
                // Service is stopped, run the service
                startService(serviceIntent)
                serviceButton?.text = resources.getString(R.string.service_off)
            }
        }
    }

    fun getServiceState(serviceClass: Class<out Any>): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceNames: List<String> = manager.getRunningServices(Integer.MAX_VALUE).map { it.service.className }
        return serviceNames.contains(serviceClass.name)
    }


    fun getIPAddress (): String {
        val wm: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }
}
