package com.pubnub.examples.pubnubExample10;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class DozeModeTest extends Activity {

	Pubnub pubnub;
	String PUBLISH_KEY = "demo-36";
	String SUBSCRIBE_KEY = "demo-36";
	String CIPHER_KEY = "";
	String SECRET_KEY = "";
	String ORIGIN = "pubsub";
	String AUTH_KEY;
	String UUID;
	Boolean SSL = false;
	String channel = "dozemodetest";

	TextView txtStatus;
	TextView txtTimer;
	Button btnTest;
	boolean firstTime = true;
	Thread trf = null;
	boolean stopSending = true;
	boolean finalizeTrf = false;
	Time last = new Time();

	private void notifyUser(Object message) {
		try {
			if (message instanceof JSONObject) {
				final JSONObject obj = (JSONObject) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
						Log.i("Received msg : ", String.valueOf(obj));
					}
				});

			} else if (message instanceof String) {
				final String obj = (String) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj, Toast.LENGTH_LONG).show();
						if (obj.contains("Hey DM I am alive!")) {
							String text = obj.substring(obj.indexOf("class java.lang.String : ") + 25);
							btnTest.setBackgroundColor(getResources().getColor(R.color.dozemodeok));
							Time tReceive = new Time();
							tReceive.setToNow();
							txtStatus.setText(tReceive.toString() + "-" + text
									+ System.getProperty("line.separator") + txtStatus.getText().toString());
						}

						Log.i("Received msg : ", obj.toString());
					}
				});

			} else if (message instanceof JSONArray) {
				final JSONArray obj = (JSONArray) message;
				this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
						Log.i("Received msg : ", obj.toString());
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finalizeTrf = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (finalizeTrf) {
			CreateThread();
		}
	}

	private void CreateThread() {
		trf = new Thread(new Runnable() {
			@Override
			public void run() {
				last.setToNow();
				Time now = new Time();
				while (!finalizeTrf) {
					now.setToNow();
					final long diff = (now.toMillis(false) - last.toMillis(false)) / 1000;

					if (!stopSending) {
						runOnUiThread(new Runnable() {
							public void run() {
								txtTimer.setText(String.valueOf(diff));
							}
						});
					}

					if (diff > 30) {
						last.setToNow();

						if (!stopSending) {
							Callback publishCallback = new Callback() {
								@Override
								public void successCallback(String channel, Object message) {
									notifyUser("PUBLISH : " + message);
								}

								@Override
								public void errorCallback(String channel, PubnubError error) {
									notifyUser("PUBLISH : " + error);
								}
							};

							String message = getResources().getString(R.string.testdozemode);

							try {
								pubnub.publish(channel, message, publishCallback);
							} catch (Exception e) {
							}
						}
					}

					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}

				int j = 0;
			}
		});

		// trf.setDaemon(true);
		trf.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doze_mode_test);

		txtStatus = (TextView) findViewById(R.id.txtStatus);
		btnTest = (Button) findViewById(R.id.btnTest);
		txtTimer = (TextView) findViewById(R.id.txtTimer);

		btnTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				test();
			}
		});

		init();

		this.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				pubnub.disconnectAndResubscribe();

			}

		}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		CreateThread();

	}

	private void test() {
		if (firstTime) {
			firstTime = false;
			subscribe();
		}

		if (btnTest.getText().toString() == getResources().getString(R.string.start)) {
			btnTest.setBackgroundColor(getResources().getColor(R.color.dozemodeok));
			btnTest.setText(getResources().getString(R.string.stop));
			last.setToNow();
			stopSending = false;
		} else {
			btnTest.setBackgroundColor(getResources().getColor(R.color.dozemode));
			btnTest.setText(getResources().getString(R.string.start));
			pubnub.unsubscribe(channel);
			firstTime = true;
			stopSending = true;
		}
	}

	private void init() {

		pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, SECRET_KEY, CIPHER_KEY, SSL);
		pubnub.setCacheBusting(false);
		pubnub.setOrigin(ORIGIN);
		pubnub.setAuthKey(AUTH_KEY);

	}

	private void subscribe() {
		try {
			pubnub.subscribe(channel, new Callback() {
				@Override
				public void connectCallback(String channel, Object message) {
					notifyUser("SUBSCRIBE : CONNECT on channel:" + channel + " : " + message.getClass() + " : "
							+ message.toString());
				}

				@Override
				public void disconnectCallback(String channel, Object message) {
					notifyUser("SUBSCRIBE : DISCONNECT on channel:" + channel + " : " + message.getClass() + " : "
							+ message.toString());
				}

				@Override
				public void reconnectCallback(String channel, Object message) {
					notifyUser("SUBSCRIBE : RECONNECT on channel:" + channel + " : " + message.getClass() + " : "
							+ message.toString());
				}

				@Override
				public void successCallback(String channel, Object message) {
					notifyUser("SUBSCRIBE : " + channel + " : " + message.getClass() + " : " + message.toString());
				}

				@Override
				public void errorCallback(String channel, PubnubError error) {
					notifyUser("SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
				}
			});

		} catch (Exception e) {
			notifyUser("ERROR " + channel + " : " + e.toString());
		}

	}

}
