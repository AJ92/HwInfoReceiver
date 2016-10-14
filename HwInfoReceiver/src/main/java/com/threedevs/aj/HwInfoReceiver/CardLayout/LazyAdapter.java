package com.threedevs.aj.HwInfoReceiver.CardLayout;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.threedevs.aj.HwInfoReceiver.CustomGauge;
import com.threedevs.aj.HwInfoReceiver.R;

public class LazyAdapter extends ArrayAdapter<RowItem> {

    Context context;

    public LazyAdapter(Context context, int resourceId, List<RowItem> items){
        super(context, resourceId, items);
        this.context = context;
    }

    public class ViewHolder{
        CustomGauge gauge;
        TextView title;
        TextView attrib1;
        TextView attrib2;
        TextView attrib3;
        LinearLayout card;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.card     = (LinearLayout)    convertView.findViewById(R.id.card);
            holder.gauge    = (CustomGauge)     convertView.findViewById(R.id.gauge);
            holder.title    = (TextView)        convertView.findViewById(R.id.title);
            holder.attrib1  = (TextView)        convertView.findViewById(R.id.attrib1);
            holder.attrib2  = (TextView)        convertView.findViewById(R.id.attrib2);
            holder.attrib3  = (TextView)        convertView.findViewById(R.id.attrib3);
            
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        holder.gauge.setTitle(rowItem.getUnit());
        //holder.gauge.setMinValue(rowItem.getMinValue());
        //holder.gauge.setMaxValue(rowItem.getMaxValue());
        holder.gauge.setValue(rowItem.getCurrentValue());

        holder.title.setText(rowItem.getTitle());
        holder.attrib1.setText(rowItem.getAttrib1());
        holder.attrib2.setText(rowItem.getAttrib2());
        holder.attrib3.setText(rowItem.getAttrib3());

        //set the gauge to the Item...
        rowItem.setGauge(holder.gauge);
        
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
        holder.card.startAnimation(animation);
        
        
        return convertView;
    }
}
