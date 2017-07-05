package com.example.mp.qrcode;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {

    TextView myTextViewResult;
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v)
    {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result)
    {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage("Aguarde carregamento de descrição");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();*/
        setContentView(R.layout.activity_main);
        myTextViewResult = (TextView)findViewById(R.id.txResult);
        //myTextViewResult.setText("Resultado: " + result.getText());
        myTextViewResult.setText("Resultado: carregando descrição");
        myTextViewResult.announceForAccessibility("Aguarde. Buscando descrição");

        new DownloadDescription().execute(result.getText());


    }

    private class DownloadDescription extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {



            String connectionString =
                    "jdbc:jtds:sqlserver://mctpucrs.database.windows.net:1433" + ";" +
                            "DatabaseName=mctpucrsFree" + ";" +
                            "user=museuAdmin@mctpucrs" + ";" +
                            "password=R@ulSeix@s";
            Connection connection = null;  // For making the connection
            Statement statement = null;    // For the SQL statement
            ResultSet resultSet = null;    // For the result set, if applicable
            try
            {

                // Ensure the SQL Server driver class is available.
                //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                // Establish the connection.
                connection = DriverManager.getConnection(connectionString);

                /*DatabaseMetaData dbm = connection.getMetaData();
                ResultSet rs = null;
                rs = dbm.getTables(null, null, "%", new String[] { "TABLE" });
                String retorno = "";
                while (rs.next()) { retorno = retorno +", " + rs.getString("TABLE_NAME"); }
                return retorno;*/
                String sql = "Select * from Spot where QRCode = "+params[0];
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
                int count;
                if(resultSet.next())
                {
                    String test = resultSet.getString("Description");
                    return test;

                }
            }
            catch (Exception ex)
            {
                return "Resultado: " + ex.toString();
            }
            return "não localizado";
        }


        protected void onPostExecute(String result) {
            myTextViewResult.setText(result);
            myTextViewResult.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        }
    }

}
