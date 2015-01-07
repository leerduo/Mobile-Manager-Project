package com.ustc.mobilemanager.adapter;

import java.util.List;

import com.ustc.mobilemanager.R;
import com.ustc.mobilemanager.db.dao.BlackNumberDao;
import com.ustc.mobilemanager.domain.BlackNumberInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CallSmsSafeAdapter extends BaseAdapter {

	public List<BlackNumberInfo> infos;

	private BlackNumberDao dao;

	private Context context;
	
	
	

	public CallSmsSafeAdapter(Context context) {

		this.context = context;
		dao = new BlackNumberDao(context);
		infos = dao.findAll();
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.list_item_callsms, null);

			viewHolder = new ViewHolder();

			viewHolder.tv_black_number = (TextView) convertView
					.findViewById(R.id.tv_black_number);
			viewHolder.tv_black_mode = (TextView) convertView
					.findViewById(R.id.tv_black_mode);

			convertView.setTag(viewHolder);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.tv_black_number.setText(infos.get(position).getNumber());

		String mode = infos.get(position).getMode();
		
		if (position < 3) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.btn_register_normal));
		}
//		else {
//			convertView.setBackgroundColor(context.getResources().getColor(R.color.voip_interface_text_color));
//		}


		if ("1".equals(mode)) {
			viewHolder.tv_black_mode.setText("电话拦截");

		} else if ("2".equals(mode)) {
			viewHolder.tv_black_mode.setText("短信拦截");
		} else {
			viewHolder.tv_black_mode.setText("全部拦截");
		}
		
		
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	static class ViewHolder {
		TextView tv_black_number;
		TextView tv_black_mode;

	}

}
