package com.example.neuegruppeerstellen;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.exlap.DataElement;
import de.exlap.DataListener;
import de.exlap.DataObject;
import de.exlap.ExlapClient;
import de.exlap.ExlapException;
import de.exlap.Url;
import de.exlap.UrlList;
import de.exlap.discovery.DiscoveryListener;
import de.exlap.discovery.DiscoveryManager;
import de.exlap.discovery.ServiceDescription;

public class RegisterActivity extends Activity implements DataListener,
		DiscoveryListener {

	ListView msgList; // Model
	// enthält die tatsächlichen Daten (Mitglieder)
	HashMap<String, MessageDetails> detailsMap = new HashMap<String, MessageDetails>();
	ArrayList<MessageDetails> details;
	AdapterView.AdapterContextMenuInfo info;
	MessageDetails msg;
	ArrayList<Double> averageSpeed = new ArrayList<Double>();
	TextView tv_my_speed, tv_my_avg_speed;

	double averageSpeedSum = 0;
	ExlapClient ec;
	DataListener dataListener = this;

	// Liste mit Fake ETA-Daten; pro Mitglied eine Liste
	final int LIST_SIZE = 1000;
	List<String> eta_for_ich = new ArrayList<String>();
	List<String> eta_for_sascha = new ArrayList<String>();
	List<String> eta_for_alex = new ArrayList<String>();
	List<String> eta_for_baris = new ArrayList<String>();

	int MIN_WAIT_TIME = 1000; // 1 Sekunde
	int MAX_WAIT_TIME = 2000; // 3 Sekunden

	View v;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_main);
		msgList = (ListView) findViewById(R.id.MessageList);

		// Felder um Exlapt Daten anzuzeigen
		tv_my_speed = (TextView) findViewById(R.id.tv_my_speed_output);
		tv_my_avg_speed = (TextView) findViewById(R.id.tv_my_avg_speed_output);

		details = new ArrayList<MessageDetails>();
		msgList.setAdapter(new CustomAdapter(details, this));
		registerForContextMenu(msgList);

		for (int i = 0; i < 10; i++) {
			averageSpeed.add(50.0);
		}

		// THREAD_POOL_EXECUTOR = bis zu 5 Async-Tasks gleichzeitig erlaubt!
		ShowDialogAsyncTask showDialogAsyncTask = new ShowDialogAsyncTask();
		showDialogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class ShowDialogAsyncTask extends AsyncTask<Void, Integer, Void> {

		// wird als erstes im Async-Task aufgerufen
		// hier können Variablen initialisiert werden
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			fillMemberData(); // Mitglieder hinzufügen

			// EXLAP
			startService("socket://192.168.0.101:28500");
		}

		private void fillMemberData() {
			MessageDetails msgDetails = new MessageDetails();
			msgDetails.setIcon(R.drawable.jan_eck);
			msgDetails.setName("Ich");
			msgDetails.setEta("01:10");
			eta_for_ich = generateEtaList(LIST_SIZE, 1, 10);
			msgDetails.setDistToGoal("56 km");
			msgDetails.setDistToMe("12 km");
			msgDetails.setAverageSpeed("146 km/h");
			msgDetails.setSpeed("89 km/h");
			details.add(msgDetails);
			detailsMap.put("Ich", msgDetails);

			msgDetails = new MessageDetails();
			msgDetails.setIcon(R.drawable.alex_eck);
			msgDetails.setName("Alexandra");
			msgDetails.setEta("02:15");
			eta_for_alex = generateEtaList(LIST_SIZE, 2, 15);
			msgDetails.setDistToGoal("56 km");
			msgDetails.setDistToMe("12 km");
			msgDetails.setAverageSpeed("234 km/h");
			msgDetails.setSpeed("123 km/h");
			details.add(msgDetails);
			detailsMap.put("Alexandra", msgDetails);

			msgDetails = new MessageDetails();
			msgDetails.setIcon(R.drawable.baris_eck);
			msgDetails.setName("Baris");
			msgDetails.setEta("02:15");
			eta_for_baris = generateEtaList(LIST_SIZE, 2, 15);
			msgDetails.setDistToGoal("56 km");
			msgDetails.setDistToMe("12 km");
			msgDetails.setAverageSpeed("169 km/h");
			msgDetails.setSpeed("121 km/h");
			details.add(msgDetails);
			detailsMap.put("Baris", msgDetails);

			msgDetails = new MessageDetails();
			msgDetails.setIcon(R.drawable.sascha_eck);
			msgDetails.setName("Sascha");
			msgDetails.setEta("03:03");
			eta_for_sascha = generateEtaList(LIST_SIZE, 3, 3);
			msgDetails.setDistToGoal("310 km");
			msgDetails.setDistToMe("0 km");
			msgDetails.setAverageSpeed("0 km/h");
			msgDetails.setSpeed("0 km/h");
			details.add(msgDetails);
			detailsMap.put("Sascha", msgDetails);
		}

		// Hauptaufgabe (in unserem Fall warten)
		@Override
		protected Void doInBackground(Void... params) {
			SystemClock.sleep(3000); // am Anfang (neuer Intent geöffnet) warten

			for (int i = 0; i < eta_for_sascha.size(); i++) {
				publishProgress(i);

				// zufällige Wartezeit
				int rand = (int) (Math.random()
						* (MAX_WAIT_TIME - MIN_WAIT_TIME) + MIN_WAIT_TIME);
				SystemClock.sleep(rand);
			}
			return null;
		}

		// hier kann das UI geupdated werden
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			if (!eta_for_sascha.isEmpty()) {
				String marieETA = eta_for_sascha.remove(0);
				MessageDetails marie = detailsMap.get("Sascha"); // Sascha
				marie.setEta(marieETA);

				String claudiaETA = eta_for_alex.remove(0);
				MessageDetails claudia = detailsMap.get("Alexandra"); // Alex
				claudia.setEta(claudiaETA);

				String beneETA = eta_for_baris.remove(0);
				MessageDetails bene = detailsMap.get("Baris"); // Baris
				bene.setEta(beneETA);

				String ichETA = eta_for_ich.remove(0);
				MessageDetails ich = detailsMap.get("Ich"); // Ich
				ich.setEta(ichETA);

				details.clear();

				// sortieren, "Ich" aber außen vor lassen, also vorerst nicht adden
				details.add(marie);
				details.add(claudia);
				details.add(bene);
				details = sortByEta(details);
				details.add(0, ich); // nachdem die Liste sortiert wurde, "Ich" an den Anfang der
										// Liste packen

				// Liste updaten
				msgList.setAdapter(new CustomAdapter(details,
						RegisterActivity.this));
			}

			// Durchschnittsgeschwindigkeit
			if (!averageSpeed.isEmpty()) {
				averageSpeed.remove(0);
				averageSpeedSum = 0;
				if (!value.isEmpty()) {
					averageSpeed.add(Double.parseDouble(value));

					for (Double speed : averageSpeed) {
						averageSpeedSum += speed;
					}

					DecimalFormat formater = new DecimalFormat("#0.00");
					tv_my_speed.setText(formater.format(Double
							.parseDouble(value)) + " kmh");
					tv_my_avg_speed.setText(formater.format(averageSpeedSum)
							+ " kmh");

					averageSpeedSum = 0;
				}
			}
		}

		// in Minuten umrechnen zum besseren Vergleichen
		private ArrayList<MessageDetails> sortByEta(
				ArrayList<MessageDetails> details) {
			MessageDetails details0 = details.get(0);
			String[] etaTime0 = details0.getEta().split(":");
			NameETA eta0 = new NameETA(Integer.parseInt(etaTime0[0]) * 60
					+ Integer.parseInt(etaTime0[1]), details0.getName());

			MessageDetails details1 = details.get(1);
			String[] etaTime1 = details1.getEta().split(":");
			NameETA eta1 = new NameETA(Integer.parseInt(etaTime1[0]) * 60
					+ Integer.parseInt(etaTime1[1]), details1.getName());

			MessageDetails details2 = details.get(2);
			String[] etaTime2 = details2.getEta().split(":");
			NameETA eta2 = new NameETA(Integer.parseInt(etaTime2[0]) * 60
					+ Integer.parseInt(etaTime2[1]), details2.getName());

			ArrayList<NameETA> tmp = new ArrayList<NameETA>();
			tmp.add(eta0);
			tmp.add(eta1);
			tmp.add(eta2);

			tmp = sort(tmp);
			details.clear();

			for (NameETA nameETA : tmp) {
				if (nameETA.getName().equals(details0.getName())) {
					details.add(details0);
				} else if (nameETA.getName().equals(details1.getName())) {
					details.add(details1);
				} else {
					details.add(details2);
				}
			}

			return details;
		}

		private ArrayList<NameETA> sort(ArrayList<NameETA> arr) {
			NameETA temp;
			for (int i = 0; i < arr.size() - 1; i++) {

				for (int j = 1; j < arr.size() - i; j++) {
					if (arr.get(j - 1).getMinutes() > arr.get(j).getMinutes()) {
						temp = arr.get(j - 1);
						arr.set(j - 1, arr.get(j));
						arr.set(j, temp);
					}
				}
			}
			return arr;
		}

		// wird ausgeführt nachdem die Aufgabe abgeschlossen wurde
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	// enthält die Logik der ListView
	public class CustomAdapter extends BaseAdapter {
		private ArrayList<MessageDetails> data;
		Context context;

		CustomAdapter(ArrayList<MessageDetails> data, Context c) {
			this.data = data;
			this.context = c;
		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return data.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_item_message, null);
			}

			// alle Komponenten des "list_item_message.xml" Layouts holen
			ImageView imageView = (ImageView) v.findViewById(R.id.icon);
			TextView nameView = (TextView) v.findViewById(R.id.name);
			TextView etaView = (TextView) v.findViewById(R.id.eta);
			ImageButton button_info = (ImageButton) v
					.findViewById(R.id.button_info);
			ImageButton button_anruf = (ImageButton) v
					.findViewById(R.id.button_anruf);

			msg = data.get(position);
			imageView.setImageResource(msg.icon);
			nameView.setText(msg.name);

			// Marie ist noch nicht losgefahren, also ETA dauerhaft 00:00 + kein arrived anzeigen
			if (msg.eta.equals("00:00")) {
				etaView.setText("arrived");
				v.setBackgroundResource(R.drawable.bg_light_green);
			} else if (msg.name.equals("Ich")) {
				v.setBackgroundResource(R.drawable.bg_orange);
				etaView.setText("ETA: " + msg.eta);
			} else {
				etaView.setText("ETA: " + msg.eta);
			}

			// Info-Button implementieren
			button_info.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					// aktuell ausgewähltes Item holen (muss pro Klick gemacht
					// werden)
					msg = data.get(position);
					String name = msg.getName();

					// VERSION 1
					String averageSpeed = msg.getAverageSpeed(); // z.B. xxx kmh
					String speed = msg.getSpeed(); // z.B. xxx kmh
					String distToGoal = msg.getDistToGoal(); // z.B. xx km
					String distToMe = msg.getDistToMe(); // z.B. xx km

					// if (!name.equals("Marie")) {
					// neue Fakedaten setzen; vorher in int zerlegen (da ursprünglich String
					// vorliegen)
					String[] averageSpeedSplit = averageSpeed.split(" ");
					int curAvgSpeed = Integer.parseInt(averageSpeedSplit[0]);

					String[] speedSplit = speed.split(" ");
					int curSpeed = Integer.parseInt(speedSplit[0]);

					String[] distToGoalSplit = distToGoal.split(" ");
					int curDistToGoal = Integer.parseInt(distToGoalSplit[0]);

					String[] distToMeSplit = distToMe.split(" ");
					int curDistToMe = Integer.parseInt(distToMeSplit[0]);

					// Zufallszahl zwischen 5 und 25
					int randomSpeed = (int) (Math.random() * 25 + 5);
					// Zufallszahl zwischen 1 und 3
					int randomDistance = (int) (Math.random() * 3 + 1);

					// Wert addieren oder subtrahieren? 50% / 50%
					Random random = new Random();
					int nextInt = random.nextInt(2);

					// addieren
					if (nextInt == 1) {
						curAvgSpeed += randomSpeed;
						curSpeed += randomSpeed;
						// Distanz zum Ziel die ganze Zeit verringern
						curDistToGoal -= randomDistance;
						curDistToMe += randomDistance;
					}
					// subtrahieren
					else {
						curAvgSpeed -= randomSpeed;
						curSpeed -= randomSpeed;
						// Distanz zum Ziel die ganze Zeit verringern
						curDistToGoal -= randomDistance;
						curDistToMe -= randomDistance;
					}

					// maximal 280 kmh schnell
					curAvgSpeed = curAvgSpeed > 280 ? 280 : curAvgSpeed;
					// keine negative Durchschnittsgeschwindigkeit
					curAvgSpeed = curAvgSpeed < 0 ? 0 : curAvgSpeed;
					// gleiches gilt für die Geschwindigkeit
					curSpeed = curSpeed > 280 ? 280 : curSpeed;
					curSpeed = curSpeed < 0 ? 0 : curSpeed;

					// keine negative Distanzen
					curDistToGoal = curDistToGoal < 0 ? 0 : curDistToGoal;
					curDistToMe = curDistToMe < 0 ? 0 : curDistToMe;

					msg.setAverageSpeed(curAvgSpeed + " km/h");
					msg.setSpeed(curSpeed + " km/h");
					msg.setDistToGoal(curDistToGoal + " km");
					msg.setDistToMe(curDistToMe + " km");

					// ALERT DIALOG
					AlertDialog.Builder alertadd = new AlertDialog.Builder(
							RegisterActivity.this);
					LayoutInflater factory = LayoutInflater
							.from(RegisterActivity.this);
					final View view2 = factory.inflate(
							R.layout.register_activity_dialog, null);
					TextView aktGeschwindigkeit = (TextView) view2
							.findViewById(R.id.aktGeschwindigkeit);
					TextView durchschGeschwindigkeit = (TextView) view2
							.findViewById(R.id.durchschGeschwindigkeit);
					TextView abstandZiel = (TextView) view2
							.findViewById(R.id.abstandZiel);
					TextView abstandZuMir = (TextView) view2
							.findViewById(R.id.abstandZuMir);

					aktGeschwindigkeit.setText(msg.getSpeed() + " km/h");
					// zu VERSION 2
					// durchschGeschwindigkeit.setText(formater
					// .format(averageSpeedSum / averageSpeed.size())
					// + " kmh");

					alertadd.setTitle("Infos für " + name);

					// zu VERSION 1
					aktGeschwindigkeit.setText(speed);
					durchschGeschwindigkeit.setText(averageSpeed);
					abstandZiel.setText(distToGoal);
					abstandZuMir.setText(distToMe);

					alertadd.setView(view2);
					alertadd.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dlg,
										int sumthin) {

								}
							});
					alertadd.show();
				}
			});

			// Anruf-Button implementieren
			button_anruf.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:01634781201"));
					startActivity(callIntent);
				}
			});

			msgList.setSelection(position);

			return v;

		}
	}

	// monitor phone call activities
	private class PhoneCallListener extends PhoneStateListener {
		private boolean isPhoneCalling = false;
		String LOG_TAG = "LOGGING 123";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");
				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended,
				// need detect flag from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) {
					Log.i(LOG_TAG, "restart app");

					// restart app
					// Intent i = getBaseContext().getPackageManager()
					// .getLaunchIntentForPackage(
					// getBaseContext().getPackageName());
					// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//
					// startActivity(i);

					Log.i(LOG_TAG, "restart app");
					// restart app
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}
			}
		}
	}

	/**
	 * erzeugt eine Liste mit Zeiten, die 1 Minute Abstand haben
	 * 
	 * @param size
	 * @param hour_begin
	 * @param minute_begin
	 * @return
	 */
	private static List<String> generateEtaList(int size, int hour_begin,
			int minute_begin) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < size; i++) {
			String temp = "";

			// fertig; angekommen; ETA: 00:00
			if (hour_begin <= 0 && minute_begin <= 0) {
				// fill array with arrived
				int j = 0;
				while (j < size - i) {
					j++;
					result.add("00:00");
				}
				break;
			}

			// ggf. führende Nullen bei Stunde hinzufügen
			if (hour_begin < 10)
				temp += "0" + hour_begin;
			else
				temp += hour_begin;

			temp += ":";
			// ggf. führende Nullen bei Minute hinzufügen
			if (minute_begin < 10)
				temp += "0" + minute_begin;
			else
				temp += minute_begin;

			result.add(temp);

			// Zahl zwischen 1 und 5 generieren
			Random rnd = new Random();
			int rand = (int) (Math.random() * (5 - 1) + 1);

			// zu 25% Zeit addieren
			if (rnd.nextInt(100) < 25) {
				minute_begin += rand;
			}
			// zu 75% Zeit subtrahieren
			else {
				minute_begin -= rand;
			}

			// Minutenzahl korrigieren, wenn kleiner 0 oder größer 60
			if (minute_begin <= 0) {
				hour_begin--;
				minute_begin = 59;
			} else if (minute_begin >= 60) {
				minute_begin = 0;
				hour_begin++;
			}

			// Stundenzahl korrigieren
			if (hour_begin < 0) {
				hour_begin = 0;
				minute_begin = 0;
			}
		}
		return result;
	}

	// EXLAP
	String value = ""; // DisplayedVehicleSpeed
	private Handler carInfoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DataObject dataObject = (DataObject) msg.obj;
			for (int i = 0; i < dataObject.size(); i++) {
				DataElement dataElement = dataObject.getElement(i);
				if (dataElement.getState() == 1) {
					// Check and work with incoming messages
					value = dataElement.getValue().toString().equals("") ? "null"
							: dataElement.getValue().toString();
					// Filter DisplayedVehicleSpeed values and log them.
					if (dataElement.getName().contentEquals(
							"DisplayedVehicleSpeed")) {
						Log.i("Incoming element value",
								"DisplayedVehicleSpeed: " + value);
					}
				}
			}
		}
	};

	public void startService(final String address) {
		Thread service = new Thread(new Runnable() {
			public void run() {
				Log.i("ConnectionHelper", ("Trying to connect to: " + address));
				ec = new ExlapClient(address);
				ec.addDataListener(dataListener);
				ec.connect();
				if (ec.isConnected()) {
					Log.i("ConnectionHelper", "isConnected on Port" + address);
					subscribeAll();
				} else
					Log.i("ConnectionHelper", "Unable to connect to: "
							+ address);
			}

		});
		service.start();
	}

	public void subscribeAll() {
		try {
			Log.i("ConnectionHelper", ("subscribing"));
			UrlList urlList = ec.getDir("*");
			@SuppressWarnings("unchecked")
			Enumeration<Url> elements = urlList.elements();
			while (elements.hasMoreElements()) {
				Url url = elements.nextElement();
				if (url.getType() == Url.TYPE_OBJECT) {
					String urlName = url.getName();
					System.out.print("Interface on \"" + urlName + "\"...");
					Log.i("ConnectionHelper", (" DONE. Interface=" + ec
							.getInterface(urlName).toString()));
					System.out.print("Subscribe to: \"" + urlName + "\"...");
					ec.subscribeObject(urlName, 100);
					Log.i("ConnectionHelper", (" DONE."));
				}
			}
		} catch (Exception e) {
		}
	}

	public void unsubscribeAll() throws IllegalArgumentException, IOException,
			ExlapException {
		Log.i("ConnectionHelper", ("unsubscribing"));
		if (ec.isConnected()) {
			UrlList urlList = ec.getDir("*");
			@SuppressWarnings("unchecked")
			Enumeration<Url> elements = urlList.elements();
			while (elements.hasMoreElements()) {
				Url url = elements.nextElement();
				if (url.getType() == Url.TYPE_OBJECT) {
					String urlName = url.getName();
					ec.unsubscribeObject(urlName);
					Log.i("ConnectionHelper", (" UNSUBSCRIBE DONE."));
				}
			}
		}

	}

	public void discoveryEvent(int eventType,
			ServiceDescription serviceDescription) {
		switch (eventType) {
		case DiscoveryListener.SERVICE_CHANGED:
			Log.i("ConnectionHelper",
					("CHANGED:" + serviceDescription.toString()));
			break;
		case DiscoveryListener.SERVICE_GONE:
			Log.i("ConnectionHelper",
					("GONE   :" + serviceDescription.toString()));
			break;
		case DiscoveryListener.SERVICE_NEW:
			startService(serviceDescription.getAddress());
			break;
		default:
			break;
		}
	}

	public void discoveryFinished(boolean discoverySuccessfull) {
		Log.i("ConnectionHelper", ("Discovery finished or terminated"));
	}

	// This will connect to the first visible EXLAP Server which has autodiscovery enabled
	public void performDiscovery() throws IOException {
		DiscoveryManager disco = new DiscoveryManager(
				DiscoveryManager.SCHEME_SOCKET);
		try {
			disco.discoverServices(this, null, true);
		} catch (Exception e) {
			Log.i("ConnectionHelper", ("ERROR. Root cause: " + e.getMessage()));
		}
	}

	@Override
	public void onData(DataObject dataObject) {
		// SHOW EVERY INCOMING DATA MESSAGE:
		// Log.i("ConnectionHelper",(">>> Got <Dat/>: " + dataObject.toString()));
		if (dataObject != null) {
			carInfoHandler.sendMessage(carInfoHandler.obtainMessage(1,
					dataObject));
		}
	}

	public void killClient() {
		ec.shutdown();
	}

	public boolean isConnected() {
		return ec.isConnected();
	}
}