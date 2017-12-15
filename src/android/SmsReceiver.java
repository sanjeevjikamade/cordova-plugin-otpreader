package cordova.plugin.otpreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

        public static SmsListener mListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle data = intent.getExtras();

                Object[] pdus = (Object[]) data.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String sender = smsMessage.getDisplayOriginatingAddress();

                    //TODO: change message sender
                    if (sender.contains("IBULLS") || sender.contains("ADHAAR")) {
                        String messageBody = smsMessage.getMessageBody();
                        //Pass on the text to our listener.
                        mListener.messageReceived(messageBody, sender);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }


        public static void bindListener(SmsListener listener) {
            mListener = listener;
        }
    }