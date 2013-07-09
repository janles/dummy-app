package com.example.androidsearchcontact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.neuegruppeerstellen.R;

public class ContactFake extends Activity {
	ListView contactsFake;
	ArrayList<ContactInfo> details;
	AdapterView.AdapterContextMenuInfo info;
	ContactInfo contactInfo;
	private final int GREEN = Color.rgb(170, 230, 100);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kontakte_hinzufuegen);
		contactsFake = (ListView) findViewById(R.id.lv_contact);

		details = new ArrayList<ContactInfo>();

		// angeklickte Kontakte farbig machen
		contactsFake
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						boolean selected = view.isSelected();

						if (!selected) {
							parent.getChildAt(position).setBackgroundColor(
									GREEN);
						} else {
							parent.getChildAt(position).setBackgroundColor(-1);
						}
						view.setSelected(!selected);
					}
				});

		contactsFake.setAdapter(new CustomAdapter(details, this));
		registerForContextMenu(contactsFake);

		fillContactList();

	}

	// fuellen der Kontaktliste
	public void fillContactList() {

		ContactInfo contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.alex_eck);
		contactInfo2.setName("Alexandra");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.baris_eck);
		contactInfo2.setName("Baris");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.christiano);
		contactInfo2.setName("Christiano");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.daniel);
		contactInfo2.setName("Daniel");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.marie);
		contactInfo2.setName("Marie");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.sascha_eck);
		contactInfo2.setName("Sascha");
		details.add(contactInfo2);

		contactInfo2 = new ContactInfo();
		contactInfo2.setIcon(R.drawable.yusuf);
		contactInfo2.setName("Yusuf");
		details.add(contactInfo2);

	}

	// enthält die Logik der ListView
	public class CustomAdapter extends BaseAdapter {
		private ArrayList<ContactInfo> data;
		Context context;

		CustomAdapter(ArrayList<ContactInfo> details, Context c) {
			this.data = details;
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

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_contacts, null);
			}

			// alle Komponenten des ".xml" Layouts holen

			ImageView ava = (ImageView) v.findViewById(R.id.Avatar);
			TextView name = (TextView) v.findViewById(R.id.Name);

			contactInfo = data.get(position);

			ava.setImageResource(contactInfo.icon);
			name.setText(contactInfo.name);

			return v;
		}
	}

	public void writeFile() {
		File dir = getFilesDir();
		File file = new File(dir, "Contacts.txt");
		file.delete();
		FileOutputStream fos;
		StringBuilder sb = new StringBuilder();
		try {
			fos = new FileOutputStream(file);
			// for (String contact : contacts) {
			// String tmp = contact + "\n";
			// // fos.write(tmp.getBytes());
			// // fos.flush();
			// sb.append(tmp);

			sb.append("Alexandra\n");
			sb.append("Baris\n");
			sb.append("Sascha\n");
			// }
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveContacts(View v) {
		writeFile();
		finish();
	}

}
