package com.example.neuegruppeerstellen;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class Tab_List_Map extends TabActivity {
	TabSpec tabSpecListe, tabSpecKarte;
	TabHost tabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up notitle
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_list_map);
		final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

		tabHost = getTabHost();

		// Karte Tab
		Intent intentKarte = new Intent().setClass(this,
				MapOverviewActivity.class);
		tabSpecKarte = tabHost.newTabSpec("Karte").setIndicator("Karte")
				.setContent(intentKarte);

		// Liste Tab
		Intent intentListe = new Intent()
				.setClass(this, RegisterActivity.class);
		tabSpecListe = tabHost.newTabSpec("Liste").setIndicator("Liste")
				.setContent(intentListe);

		// nach 3 Sekunden werden die anderen Sachen erst geladen
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				progress.setVisibility(View.GONE);
				// Tabs erstellen
				tabcreate();
			}
		}, 2000);
	}

	public void tabcreate() {
		// alle Tabs hinzufuegen
		tabHost.addTab(tabSpecKarte);
		tabHost.addTab(tabSpecListe);

		// aktuell gewaehlten Tab setzen
		tabHost.setCurrentTabByTag("Liste");
	}
}
