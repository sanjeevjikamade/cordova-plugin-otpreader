package cordova.plugin.otpreader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vipul on 2/18/2017.
 */

public class BaseActivity extends AppCompatActivity {

    final private static int REQUEST_CODE_ASK_PERMISSIONS_READ_SMS = 4881;

    public static InternalPermissionListner internalPermissionListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ISDKUtils.setStatusBar(BaseActivity.this);

        Bundle activityBundle = this.getIntent().getExtras();

        if (activityBundle != null) {
            if(activityBundle.getBoolean("REQUEST_READ_SMS_PERMISSION")){
                checkIfReadCallLogPermissionAcquired(internalPermissionListner);
                return;
            }
        }
    }

    public void checkIfReadCallLogPermissionAcquired(InternalPermissionListner internalPermissionListner) {
        try {

            if(internalPermissionListner == null) {
                BaseActivity.this.finish();
                return;
            }

            this.internalPermissionListner = internalPermissionListner;

            if(android.os.Build.VERSION.SDK_INT >= 23) {
                requestCallLogPermission(this.internalPermissionListner);
            } else {
                BaseActivity.this.finish();
                internalPermissionListner.permissionGranted();
            }
        } catch(Exception ex) {
            Log.e("BaseActivity::checkIfSendSMSPermissionAcquired : ",ex.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkIfUserGrantedPermissionsCallLogs() {
        try {
            int hasReadSMSPermissionGranted = checkSelfPermission(Manifest.permission.READ_SMS);
            int hasReceiveSMSPermissionGranted = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int hasSendSMSPermissionGranted = checkSelfPermission(Manifest.permission.SEND_SMS);

            if(hasSendSMSPermissionGranted != PackageManager.PERMISSION_GRANTED || hasReadSMSPermissionGranted != PackageManager.PERMISSION_GRANTED || hasReceiveSMSPermissionGranted != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        } catch(Exception ex) {
            Log.e("BA::cIUGP : ",ex.toString());
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCallLogPermission(InternalPermissionListner internalPermissionListner) {
        try {
            if(internalPermissionListner == null) {
                return;
            }

            if(!checkIfUserGrantedPermissionsCallLogs()) {
                requestPermissions(new String[] {Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_ASK_PERMISSIONS_READ_SMS);
            } else {
                BaseActivity.this.finish();
                internalPermissionListner.permissionGranted();
            }
        } catch(Exception ex) {
            Log.e("requestSMSPermission : ",ex.toString());
            BaseActivity.this.finish();
            internalPermissionListner.permissionDenied();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if(internalPermissionListner!=null) {
                switch (requestCode) {

                    case REQUEST_CODE_ASK_PERMISSIONS_READ_SMS:
                        if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            BaseActivity.this.finish();
                            this.internalPermissionListner.permissionGranted();
                        } else if(grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            //if(shouldAskPermissionDialogStatus()) {
                            showPermissionDialog("India Bulls requires access to this permission",
                                    new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //disablePermissionDialogStatus();
                                            requestPermissions(new String[] {Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_ASK_PERMISSIONS_READ_SMS);
                                        }
                                    },
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            BaseActivity.this.finish();
                                            BaseActivity.this.internalPermissionListner.permissionDenied();
                                        }
                                    });
                            /*} else {
                                BaseActivity.this.internalPermissionListner.permissionDenied();
                            }*/
                        } else {
                            BaseActivity.this.finish();
                            BaseActivity.this.internalPermissionListner.permissionNotInvoked();
                        }
                        break;

                    default:
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        } catch(Exception ex) {
            Log.e("Error ::onRequestPermissionsResult : ",ex.toString());
        }
    }

    private void showPermissionDialog(String message, DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }

}