package br.com.thiengo.pockerhijack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.thiengo.pockerhijack.extras.Util;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView tvNotification = (TextView) findViewById(R.id.tv_notification_text);
        Button btNotification = (Button) findViewById(R.id.bt_notification);

        if( Util.isSystemAlertPermissionGranted( this ) ){
            tvNotification.setText("As notificações em bolha estão ativadas para esse aplicativo.");
            btNotification.setVisibility(View.GONE);
        }
        else{
            tvNotification.setText("As notificações em bolha não estão ativadas ainda. Para obter mais desse aplicativo, ative-as clicando no botão abaixo e atualizando as configurações que serão apresentadas.");
            btNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void callAndroidSettings( View view ){
        String packageName = getPackageName();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        startActivityForResult(intent, 558);
    }
}
