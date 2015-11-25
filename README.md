# Android Doze Mode Test


I changed the Android `SubscribeAtBoot` project to reply if a message contains “doze mode test”. It helped me to discovery if emulator with Android 6.0 in Doze Mode is working.

`PubnubService.java`
``` java
@Override
public void successCallback(String channel, Object message) {
	notifyUser(channel + " " + message.toString());
    if(message.toString().toLowerCase().contains("doze mode test"))
    {
    	pubnub.publish(channel, "Hey DM I am alive!", publishCallback);
	}
}
```

I changed the `HelloWorldActivity.java` to show user a `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`

``` java
if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
{
	Intent intent = new Intent();
	String packageName = getPackageName();
	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
    	intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
		intent.setData(Uri.parse("package:" + packageName));
		startActivity(intent);
	}
}
```

I tested using an emulator with `SubscribeAtBoot` on Android 6.0 and another emulator with `DozeModeTest` on Android 4.4.2.


### Put Android emulator in Doze Mode

If you want to test two projects using emulators, you need to execute commands below to put Android 6.0 emulator in Doze Mode

1. Run prompt command with Administrator privileges
2. Go to android-sdk\platform-tools
3. adb -s emulator_name shell dumpsys deviceidle disable
4. adb -s emulator_name shell dumpsys deviceidle enable
5. adb -s emulator_name shell dumpsys battery unplug
6. Turn the screen off
7. adb -s emulator_name shell dumpsys deviceidle step => output will be Stepped to: IDLE_PENDING
8. adb -s emulator_name shell dumpsys deviceidle step => output will be Stepped to: SENSING
9. adb -s emulator_name shell dumpsys deviceidle step => output will be Stepped to: LOCATING
10. adb -s emulator_name shell dumpsys deviceidle step => output will be Stepped to: IDLE
11. adb -s emulator_name shell dumpsys deviceidle step => output will be Stepped to: IDLE_MAINTENANCE











