package org.twoboysandhats.androidnfcemulator

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.IBinder

import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class NFCServerService: Service() {

    var server: NFCServer? = null

    override fun onBind(intent: Intent?): IBinder? {
        // We don't need to report anything back to an activity that binds us
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        server = NFCServer(this, 8080)
        try {
            server?.start()
        } catch (e: IOException) {
            println("Server startup error")
        }

        return START_STICKY // We'll be started and stopped at will
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
    }
}

class NFCServer(val service: Service, val port: Int): NanoHTTPD(port) {
    override fun serve(session: IHTTPSession?): Response {
        // Grab the parameters
        val parameters = session?.parameters
        val uriString = parameters?.get("uri")?.get(0)

        try {
            // create the Ndef message
            val ndefMessage = NdefMessage(NdefRecord.createUri(uriString))

            // create our NFC intent
            val nfcIntent: Intent = Intent(NfcAdapter.ACTION_NDEF_DISCOVERED)
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            nfcIntent.setData(Uri.parse(uriString))
            nfcIntent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, arrayOf(ndefMessage))

            service.startActivity(nfcIntent)

        } catch(e: Exception) {
            // just leave it.
            return newFixedLengthResponse("Error: ${e.message}")
        }

        return newFixedLengthResponse("")
    }
}