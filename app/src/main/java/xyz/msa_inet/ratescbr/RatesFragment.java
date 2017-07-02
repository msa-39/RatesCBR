package xyz.msa_inet.ratescbr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import android.widget.DatePicker;
//import java.util.Calendar;
//import android.app.DatePickerDialog;
//import android.text.format.DateUtils;

public class RatesFragment extends Fragment {
    TextView xmlRates;
//    String ratesDate;
    String xmlcontentText = null;
    String baseURL;// = "http://www.cbr.ru/scripts/XML_daily.asp";
    String loadURL;// = baseURL;
//    Calendar dateOnly=Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rates, container, false);

        xmlRates = (TextView) view.findViewById(R.id.xmlRates);
//        ratesDate = (DatePicker) view.findViewById(R.id.dateRates);
//        TextView cLable = (TextView) view.findViewById(R.id.cLabel);

//        cLable.setText(cLable.getText().toString()+" "+ dateOnly.getTime().toString());

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(super.getContext());
        baseURL = prefs.getString("baseURL", "http://www.cbr.ru/scripts/XML_daily.asp");
        loadURL = baseURL;

        new LoadXMLTask().execute(loadURL);

        return view;
    }
    private class LoadXMLTask extends AsyncTask <String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

                String xmlcontent;
                try{
                    xmlcontent = getXMLRates(path[0]);
                }
                catch (IOException ex){
                    xmlcontent = ex.getMessage();
                }

                return xmlcontent;
        }
        @Override
        protected void onPostExecute(String xmlcontent) {

                xmlcontentText=xmlcontent;
                xmlRates.setText(xmlcontent);
                Toast.makeText(getActivity(), "Курсы загружены", Toast.LENGTH_SHORT).show();
        }

        private String getXMLRates(String path) throws IOException {
                BufferedReader reader = null;
                HttpURLConnection c;
                try {
                    URL url=new URL(path);
                        c = (HttpURLConnection) url.openConnection();
                        c.setRequestMethod("GET");
                        c.setReadTimeout(10000);
                        c.connect();
                        reader = new BufferedReader(new InputStreamReader(c.getInputStream(),"windows-1251"));

                    StringBuilder buf = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line + "\n");
                    }
                    return(buf.toString());
                }
                finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
        }
    }
}
