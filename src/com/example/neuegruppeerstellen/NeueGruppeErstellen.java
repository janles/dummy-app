package com.example.neuegruppeerstellen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import DateTimePicker.DateTimePicker;
import DateTimePicker.DateTimePicker.DateWatcher;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidsearchcontact.ContactFake;

public class NeueGruppeErstellen extends Activity implements DateWatcher {
	final Context context = this;
	private ArrayList<String> kontakteListe = new ArrayList<String>();
	public static String allGroups = "allGroups.txt"; // hier werden
														// Gruppennamen
														// gespeichert
	private static final String CONTACTS_FILE_NAME = "Contacts.txt";
	private ArrayAdapter listenAdapter;

	// Timer Picker
	private TextView ausgewaehlte_uhrzeit;
	private Calendar mCalen;
	private int hourOfDay;
	private int minute;
	private String ankunftszeit = "";
	private static final int Time_PICKER_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.neue_gruppe_erstellen);
		File file = new File(CONTACTS_FILE_NAME);
		if (file.exists()) {
			readFromFile(CONTACTS_FILE_NAME);
		}

		bindListAdapter();

		// Timer Picker
		ausgewaehlte_uhrzeit = (TextView) findViewById(R.id.tv_uhrzeit_auswahl);
		mCalen = Calendar.getInstance();
		hourOfDay = mCalen.get(Calendar.HOUR_OF_DAY);
		minute = mCalen.get(Calendar.MINUTE);
	}

	private String readFromFile(String filename) {
		String TAG = MeineGruppen.class.getName();
		String result = "";
		// Liste leeren, damit die Gruppen nicht nur angehangen werden
		kontakteListe.clear();
		try {
			InputStream inputStream = openFileInput(filename);
			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
					// hier wird jede Zeile eingefügt
					// leeren Eintrag entfernen
					if (receiveString.length() != 0)
						kontakteListe.add(receiveString);
				}
				inputStream.close();
				result = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
//			Log.e(TAG, "File not found: " + e.toString());
		} catch (IOException e) {
//			Log.e(TAG, "Can not read file: " + e.toString());
		}
		return result;
	}

	public void bindListAdapter() {
		listenAdapter = new ArrayAdapter<String>(
				this,
				// android.R.layout.simple_list_item_1, android.R.id.text1,
				// kontakteListe) {
				R.layout.gruppe_erstellen_list_item,
				R.id.groupContactDisplayName, kontakteListe) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View v = convertView;
				if (v == null) {
					LayoutInflater vi = (LayoutInflater) NeueGruppeErstellen.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					// v = vi.inflate(android.R.layout.simple_list_item_1,
					// null);
					v = vi.inflate(R.layout.gruppe_erstellen_list_item, null);
				}
				// TextView contactName = (TextView) v.findViewById
				// (android.R.id.text1);
				TextView contactName = (TextView) v
						.findViewById(R.id.groupContactDisplayName);
				contactName.setText(kontakteListe.get(position));

				// TEST
				Button delete = (Button) v
						.findViewById(R.id.buttonDeleteContactFromGroup);
				delete.setFocusable(false);
				delete.setTag(position);

				delete.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						Integer index = (Integer) v.getTag();
						String removedObject = kontakteListe.remove(index
								.intValue());
						remove(removedObject);
						notifyDataSetChanged();
						String fileOutput = makeListIntoString(kontakteListe);
						writeToFile(CONTACTS_FILE_NAME, fileOutput, false);

					}
				});

				// TEST ENDE
				return v;
			}
		};
		ListView lv_kontakte = (ListView) findViewById(R.id.lv_kontakte);
		lv_kontakte.setAdapter(listenAdapter);
	}

	private String makeListIntoString(ArrayList<String> strings) {
		StringBuilder sb = new StringBuilder();
		for (String temp : strings) {
			sb.append(temp + " \n");
		}
		return sb.toString();
	}

	// Button "Gruppe speichern und zum Startbildschirm zurueckkehren"
	public void gruppeSpeichern(View view) {
		// Eingabe vom Gruppennamen und Fahrtziel auslesen
		EditText gruppenname_feld = (EditText) findViewById(R.id.gruppe_anzeigen_gruppenname);
		EditText fahrtziel_feld = (EditText) findViewById(R.id.gruppe_anzeigen_fahrtziel);
		String gruppenname = gruppenname_feld.getText().toString();
		String fahrtziel = fahrtziel_feld.getText().toString();

		// wenn alle Eingabefelder ausgefuellt wurden, dann Textdateien
		// schreiben
		if (isInputCorrect(gruppenname, fahrtziel, ankunftszeit)) {
			saveNewGroup(gruppenname, fahrtziel, ankunftszeit);
		}
	}

	// allGroups.txt - enthaelt alle Gruppennamen
	// "gruppenname.txt" - enthaelt "gruppenname", "fahrtziel", "ankunftszeit",
	// ausgewaehlte Kontakte
	private void saveNewGroup(String gruppenname, String fahrtziel,
			String ankunftszeit) {
		String output = gruppenname + "\n" + fahrtziel + "\n" + ankunftszeit
				+ "\n";
		Toast.makeText(this, "Gruppe erfolgreich gespeichert",
				Toast.LENGTH_SHORT).show();

		// Daten in den internen Speicher schreiben
		// (Pfad: data/data/com.example.neuegruppeerstellen/files)
		// Gruppenname in Uebersichtsdatei speichern
		writeToFile(allGroups, gruppenname + "\n", true);

		// fuer jede Gruppe eigene Textdatei erstellen
		writeToFile(gruppenname + ".txt", output, true);

		// Contacts auslesen und in 'GruppenName.txt' einfügen
		// Kontakte müssen erneut ausgelesen werden
		if (new File(CONTACTS_FILE_NAME).exists()) {
			readFromFile(CONTACTS_FILE_NAME);
			deleteSpecificFile(CONTACTS_FILE_NAME);
		}
		for (String name : kontakteListe) {
			writeToFile(gruppenname + ".txt", name + "\n", true);
		}

		// Activity beenden
		finish();
	}

	public void writeToFile(String filename, String data, boolean append) {
		String TAG = MeineGruppen.class.getName();
		try {
			FileOutputStream fos;
			if (append) {
				fos = openFileOutput(filename, Context.MODE_APPEND);
			} else {
				deleteSpecificFile(filename);
				File writeTo = createSpecificFile(filename);
				fos = new FileOutputStream(writeTo);
			}
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, "File write failed: " + e.toString());
		}
	}

	private void deleteSpecificFile(String filename) {
		File directory = getFilesDir();
		File deleteMe = new File(directory, filename);
		if (deleteMe.exists()) {
			deleteMe.delete();
		}
	}

	private File createSpecificFile(String filename) {
		File directory = getFilesDir();
		File newFile = new File(directory, filename);
		return newFile;
	}

	// "Plus"-Button / Mitglieder hinzufuegen
	public void kontakteAuswaehlen(View view) {
		// ListView updaten
		if (new File(CONTACTS_FILE_NAME).exists()) {
			readFromFile(CONTACTS_FILE_NAME);
		}

		// Telefonbuch aufrufen
		// Intent in = new Intent(getApplicationContext(),
		// ContactSearchActivity.class);
		Intent in = new Intent(getApplicationContext(), ContactFake.class);
		startActivity(in);
	}

	// Timer Picker
	@SuppressWarnings("deprecation")
	// TODO: 2 beim Namen loeschen!!
	public void uhrzeitEinstellen2(View view) {
		// oeffnet einen Dialog
		showDialog(Time_PICKER_ID);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Time_PICKER_ID:
			return new TimePickerDialog(this, TimePickerListener, hourOfDay,
					minute, true);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener TimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		// while dialog box is closed, below method is called.
		public void onTimeSet(TimePicker view, int hour, int minute) {
			mCalen.set(Calendar.HOUR_OF_DAY, hour);
			mCalen.set(Calendar.MINUTE, minute);

			// angegebene Zeit holen
			hourOfDay = mCalen.get(Calendar.HOUR_OF_DAY);
			minute = mCalen.get(Calendar.MINUTE);

			// angegebene Zeit anzeigen
			ankunftszeit = pad(hourOfDay) + ":" + pad(minute);
			ausgewaehlte_uhrzeit.setText(ankunftszeit);
		}
	};

	// ggf. fuehrende Nullen hinzufuegen
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public void onResume() {
		super.onResume();
		readFromFile("Contacts.txt");
		bindListAdapter();
	}

	public void fahrtStarten(View view) {
		// Eingabe vom Gruppennamen und Fahrtziel auslesen
		EditText gruppenname_feld = (EditText) findViewById(R.id.gruppe_anzeigen_gruppenname);
		EditText fahrtziel_feld = (EditText) findViewById(R.id.gruppe_anzeigen_fahrtziel);
		String gruppenname = gruppenname_feld.getText().toString();
		String fahrtziel = fahrtziel_feld.getText().toString();

		// wenn alle Eingabefelder ausgefuellt wurden, dann Textdateien
		// schreiben
		if (isInputCorrect(gruppenname, fahrtziel, ankunftszeit)) {
			saveNewGroup(gruppenname, fahrtziel, ankunftszeit);
			Intent in = new Intent(getApplicationContext(), Tab_List_Map.class);
			startActivity(in);
		}
	}

	/**
	 * Prüfen, ob Gruppenname, Fahrtziel und Ankunftszeit angegeben wurden
	 * 
	 * @param gruppenname
	 * @param fahrtziel
	 * @param ankunftszeit
	 * @return false, wenn eine Eingabe fehlt <br>
	 *         true, wenn alles korrekt ist
	 */
	private boolean isInputCorrect(String gruppenname, String fahrtziel,
			String ankunftszeit) {
		// Fehlerhafte Eingaben abfangen
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// Gruppenname leer
		if ((gruppenname.length() <= 0)) {
			alertDialogBuilder.setTitle("Gruppenname leer!");

			// Festlegen der Dialognachricht
			alertDialogBuilder
					.setMessage("Bitte geben Sie einen Gruppennamen an.")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// hier koennte man einstellen, dass bei
									// einem klick die aktuelle
									// Activity beendet wird
								}
							});
			// Benachrichtigungsdialog erstellen
			AlertDialog alertDialog = alertDialogBuilder.create();
			// Dialog anzeigen
			alertDialog.show();
			return false;
		}
		// Fahrtziel leer
		else if ((fahrtziel.length() <= 0)) {
			alertDialogBuilder.setTitle("Fahrtziel leer!");
			alertDialogBuilder
					.setMessage("Bitte geben Sie ein Fahrtziel an.")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

			return false;
		}
		// Ankunftszeit leer (wirklich noetig?)
		else if (ankunftszeit.length() <= 0) {
			alertDialogBuilder.setTitle("Ankunftszeit leer!");
			alertDialogBuilder
					.setMessage("Bitte geben Sie eine Ankunftszeit an.")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

			return false;
		}

		// korrekte Eingaben
		else {
			return true;
		}
	}

	public void uhrzeitEinstellen(View view) {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		mDateTimePicker.setDateChangedListener(this);

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.clearFocus();
						// TODO Auto-generated method stub
						// englische Schreibweise
						// String result_string = mDateTimePicker.getMonth() +
						// "/"
						// + String.valueOf(mDateTimePicker.getDay())
						// + "/"
						// + String.valueOf(mDateTimePicker.getYear())
						// + "  "
						// + String.valueOf(pad(mDateTimePicker.getHour()))
						// + ":"
						// + String.valueOf(pad(mDateTimePicker.getMinute()));

						// deutsche Schreibweise
						String result_string = String.valueOf(mDateTimePicker
								.getDay())
								+ ". "
								+ mDateTimePicker.getMonth()
								+ " "
								+ String.valueOf(mDateTimePicker.getYear())
								+ "  "
								+ String.valueOf(pad(mDateTimePicker.getHour()))
								+ ":"
								+ String.valueOf(pad(mDateTimePicker
										.getMinute()));

						// if(mDateTimePicker.getHour() > 12) result_string =
						// result_string + "PM";
						// else result_string = result_string + "AM";
						// edit_text.setText(result_string);

						// Ausgabe
						ankunftszeit = result_string;
						ausgewaehlte_uhrzeit.setText(ankunftszeit);

						mDateTimeDialog.dismiss();
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked

		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	public void onDateChanged(Calendar c) {
		Log.e("",
				"" + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH)
						+ " " + c.get(Calendar.YEAR));
	}
}
