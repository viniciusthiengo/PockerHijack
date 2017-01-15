package br.com.thiengo.pockerhijack.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.thiengo.pockerhijack.R;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class TableAdapter extends BaseAdapter {
    private List<Table> tables;
    private LayoutInflater inflater;


    public TableAdapter(Context context, List<Table> tables){
        inflater = LayoutInflater.from(context);
        this.tables = tables;
    }

    @Override
    public int getCount() {
        return tables.size();
    }

    @Override
    public Object getItem(int i) {
        return tables.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if( view == null ){
            view = inflater.inflate(R.layout.item_table, null, false);
            holder = new ViewHolder();
            view.setTag( holder );
            holder.setViews( view );
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        holder.setData( tables.get( i ) );

        return view;
    }

    private static class ViewHolder{
        private ImageView ivTable;
        private TextView tvNameTable;

        private void setViews( View view ){
            ivTable = (ImageView) view.findViewById(R.id.iv_table);
            tvNameTable = (TextView) view.findViewById(R.id.tv_name_table);
        }

        private void setData( Table table ){
            ivTable.setImageResource( table.getImage() );
            tvNameTable.setText( table.getLabel() );
        }
    }
}
