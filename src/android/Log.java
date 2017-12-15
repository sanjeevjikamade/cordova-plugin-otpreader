package cordova.plugin.otpreader;


public class Log {

	public static void e(String tag,String message) {
		try {
			//android.util.Log.e(tag,message);
		} catch(Exception ex) {
			android.util.Log.e("Error Log.e : ",ex.toString());
		}
	}
	
	public static void d(String tag,String message) {
		try {
			//android.util.Log.d(tag,message);
		} catch(Exception ex) {
			android.util.Log.d("Error Log.d : ",ex.toString());
		}
	}
	
	public static void i(String tag,String message) {
		try {
			//android.util.Log.i(tag,message);
		} catch(Exception ex) {
			android.util.Log.i("Error Log.i : ",ex.toString());
		}
	}
	
}
