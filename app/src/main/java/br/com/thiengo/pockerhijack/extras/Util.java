package br.com.thiengo.pockerhijack.extras;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import br.com.thiengo.pockerhijack.R;
import br.com.thiengo.pockerhijack.domain.Table;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class Util {
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays( context );
    }

    public static int getDpsToPixels( int dp ){
        dp = (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        return dp;
    }

    public static List<Table> getMockData(){
        List<Table> list = new ArrayList<>();
        list.add( new Table( R.drawable.pocker_01, "Casa Blanca (EUA) vs Nuria Muller (RUS)") );
        list.add( new Table( R.drawable.pocker_02, "Hijacke Club House (BRA) vs Trariargos Porto (POR)") );
        list.add( new Table( R.drawable.pocker_03, "Suicide Squad (NOR) vs Quiz Plus (ESP)") );
        list.add( new Table( R.drawable.pocker_04, "Turtles Jordan (ESP) vs Palitos (EUA)") );
        list.add( new Table( R.drawable.pocker_05, "Kiss 22 (AUS) vs Brazucas (BRA)") );
        list.add( new Table( R.drawable.pocker_06, "London Square (ING) vs Deserted (SUE)") );

        return list;
    }
}
