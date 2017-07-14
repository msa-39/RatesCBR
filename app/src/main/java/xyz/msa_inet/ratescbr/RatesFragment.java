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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.text.TextUtils;

public class RatesFragment extends Fragment {
    TextView xmlRates;
    String ratesDateSTR;
    String xmlcontentText = null;
    String baseURL;// = "http://www.cbr.ru/scripts/XML_daily.asp";
    String loadURL;// = baseURL;

    TextView txtDate;

    public void loadXML (String dSTR) {
        loadURL = baseURL + "?date_req=" + dSTR;
        new LoadXMLTask().execute(loadURL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rates, null); //container, true);

        xmlRates = (TextView) view.findViewById(R.id.xmlRates);

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
//                xmlRates.setText(xmlcontentText);
            String toTexView = null;
            String tmp = "";
                Toast.makeText(getActivity(), "Курсы загружены", Toast.LENGTH_SHORT).show();
            try {
                XmlPullParser xpp = prepareXpp(xmlcontentText);

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        // начало документа
                        case XmlPullParser.START_DOCUMENT:
                            toTexView = "START_DOCUMENT";
                            break;
                        // начало тэга
                        case XmlPullParser.START_TAG:
                            toTexView += "\n START_TAG: name = " + xpp.getName()
                                    + ", depth = " + xpp.getDepth() + ", attrCount = "
                                    + xpp.getAttributeCount();
                            tmp = "";
                            for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                tmp = tmp + xpp.getAttributeName(i) + " = "
                                        + xpp.getAttributeValue(i) + ", ";
                            }
                            if (!TextUtils.isEmpty(tmp))
                                toTexView += "\n Attributes: " + tmp;
                            break;
                        // конец тэга
                        case XmlPullParser.END_TAG:
                            toTexView += "\n END_TAG: name = " + xpp.getName();
                            break;
                        // содержимое тэга
                        case XmlPullParser.TEXT:
                            toTexView += "\n text = " + xpp.getText();
                            break;

                        default:
                            break;
                    }
                    // следующий элемент
                    xpp.next();
                }
                toTexView += "\n END_DOCUMENT";

            } catch (XmlPullParserException e) {
                toTexView +=  e.getLocalizedMessage().toString();
            } catch (IOException e) {
                toTexView += e.getLocalizedMessage().toString();
            }
            xmlRates.setText(toTexView);
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

        XmlPullParser prepareXpp(String cxml) throws XmlPullParserException {
            // получаем фабрику
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // включаем поддержку namespace (по умолчанию выключена)
            factory.setNamespaceAware(true);
            // создаем парсер
            XmlPullParser xpp = factory.newPullParser();
            // даем парсеру на вход Reader
            xpp.setInput(new StringReader(cxml));
            return xpp;
        }

    }
}
