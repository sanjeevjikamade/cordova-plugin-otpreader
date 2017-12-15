package cordova.plugin.otpreader;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OtpReaderPlugin extends CordovaPlugin {

	final String OTP_DELIMITER = "is";
	SmsReceiver smsReceiver = new SmsReceiver();

	@Override
	public boolean execute(String action,final JSONArray requestParams,final CallbackContext callbackContext) throws JSONException {
		try {
			if(action!=null) {
        		if(action.equalsIgnoreCase("prepare")) {
					try {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

							BaseActivity.internalPermissionListner = new InternalPermissionListner() {

								@Override
								public void permissionNotInvoked() {
									sendErrorCallback("PERMISSION_NOT_INVOKED", callbackContext);
								}

								@Override
								public void permissionGranted() {
									getOTP(callbackContext);
								}

								@Override
								public void permissionDenied() {
									sendErrorCallback("PERMISSION_DENINED", callbackContext);
								}
							};

							Intent intent = new Intent(cordova.getActivity(), BaseActivity.class);
							intent.putExtra("REQUEST_READ_SMS_PERMISSION", true);
							cordova.getActivity().startActivity(intent);

						}else{
							getOTP(callbackContext);
						}
					} catch (Exception ex) {
						Log.e("OtpReaderPlugin::execute:i",ex.toString());
					}
        		}

        		//for unregistering broadcast
				else if(action.equalsIgnoreCase("close")) {
					try {
						if (unregisterBroadcastReceiver()) {
							sendSuccessCallback(callbackContext);
						} else {
							sendErrorCallback("UNREGISTER_ERROR", callbackContext);
						}
					}catch (Exception e){
						Log.e("OtpReaderPlugin::unregister : ",e.toString());
					}
				}
        	}
			return true;
        } catch(Exception ex) {
        	Log.e("OtpReaderPlugin::execute : ",ex.toString());
        }
        return false;
	}


	private void getOTP(final CallbackContext callbackContext){
		if(registerBroadcastReceiver()) {
			SmsReceiver.bindListener(new SmsListener() {
				@Override
				public void messageReceived(String messageText, String sender) {
					try {
						if (messageText != null) {
							String verificationCode = getVerificationCode(messageText, sender);
							if (verificationCode != null) {
								JSONObject json = new JSONObject();
								try {
									json.put("code", verificationCode);
								} catch (Exception e) {
									e.printStackTrace();
								}
								sendSuccessCallback(json, callbackContext);
							} else {
								sendErrorCallback("ERROR_VERIFICATION_CODE_NOT_RECEIVED", callbackContext);
							}
						} else {
							sendErrorCallback("ERROR_MSG_NOT_RECEIVED", callbackContext);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			});
		}else {
			sendErrorCallback("REGISTER_ERROR", callbackContext);
		}
	}

	/*
	* Update following code accorgding to you SMS.
	* Update sender with your sender and message content accordingly
	*/
	private String getVerificationCode(String message, String sender)
	{
		try {
			String code = null;
			if (sender.contains("SENDER") && message.contains("Your OTP")) {
				int index = message.indexOf(OTP_DELIMITER);
				System.out.println(index);
				if (index != -1) {
					int start = index + 3;
					int length = 6;
					code = message.substring(start, start + length);
					return code;
				}
			}else if(sender.contains("ADHAAR") && message.contains("OTP for Aadhaar")){
				int index = message.indexOf(OTP_DELIMITER);
				System.out.println(index);
				if (index != -1) {
					int start = index + 3;
					int length = 6;
					code = message.substring(start, start + length);
					return code;
				}
			}
			return code;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean registerBroadcastReceiver() {
		try {
			cordova.getActivity().registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

			Toast.makeText(cordova.getActivity(), "Registered broadcast receiver", Toast.LENGTH_SHORT).show();

		/*	LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(mMessageReceiver,
					new IntentFilter("my-event"));*/
			//return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean unregisterBroadcastReceiver() {
		try {
			cordova.getActivity().unregisterReceiver(smsReceiver);

			Toast.makeText(cordova.getActivity(), "unregistered broadcst receiver", Toast.LENGTH_SHORT).show();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private void sendErrorCallback(final String errorMsg,final CallbackContext callbackContext) {
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				callbackContext.error(errorMsg);
			}
		});
	}

	private void sendSuccessCallback(final JSONObject jsonObject, final CallbackContext callbackContext) {
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
					callbackContext.sendPluginResult(pluginResult);
				} catch (Exception e) {
					Log.e("exception::successCallback : ",e.toString());
				}
			}
		});
	}

	private void sendSuccessCallback(final CallbackContext callbackContext) {
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
					callbackContext.sendPluginResult(pluginResult);
				} catch (Exception e) {
					Log.e("exception::successCallback : ",e.toString());
				}
			}
		});
	}
	
}

