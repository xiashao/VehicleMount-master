package car.ccut.com.vehicle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import car.ccut.com.vehicle.R;


public class ListViewAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<Map<String, Object>> listItems; // 商品信息集合
	private ArrayList<String> addSongIds;// 将要添加的歌曲的id
	private ArrayList<String> songIds;// 全部歌曲的id
	private ArrayList<String> pl_songIds;// 列表歌曲的id
	private int res;// 适配器对应的xml文件资源
	private boolean mBusy = false;
	private ImageShow mImageLoader;// 异步加载图片类

	public ListViewAdapter(Context context, List<Map<String, Object>> listItems, int res) {
		this.context = context;
		this.listItems = listItems;
		addSongIds = new ArrayList<String>();
		songIds = new ArrayList<String>();
		pl_songIds = new ArrayList<String>();
		this.res = res;
		mImageLoader = new ImageShow(context);
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(res, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.songName.setText((String) listItems.get(position).get("songName"));
		holder.singerName.setText((String) listItems.get(position).get("singerName"));
		String urlId;
		if (pl_songIds.size() > 0) {
			urlId = pl_songIds.get(position);
		} else
			urlId = songIds.get(position);
		holder.albumPicture.setImageResource(R.mipmap.audioplayernoartwork);

		if (!mBusy) {
			mImageLoader.DisplayImage(urlId, holder.albumPicture, false);
		} else {
			mImageLoader.DisplayImage(urlId, holder.albumPicture, true);
		}

		return view;
	}

	static class ViewHolder {
		TextView songName;
		TextView singerName;
		ImageView albumPicture;// 专辑图片

		public ViewHolder(View view) {
			super();
			this.songName = (TextView) view.findViewById(R.id.songName);
			this.singerName = (TextView) view.findViewById(R.id.singerName);
			this.albumPicture = (ImageView) view.findViewById(R.id.albumPicture);
		}
	}

	public List<Map<String, Object>> getListItems() {
		return listItems;
	}

	public void setListItems(List<Map<String, Object>> listItems) {
		this.listItems = listItems;
	}

	public ArrayList<String> getAddSongIds() {
		return addSongIds;
	}

	public void setAddSongIds(ArrayList<String> addSongIds) {
		this.addSongIds = addSongIds;
	}

	public ArrayList<String> getSongIds() {
		return songIds;
	}

	public void setSongIds(ArrayList<String> songIds) {
		this.songIds = songIds;
	}

	public ArrayList<String> getPl_songIds() {
		return pl_songIds;
	}

	public void setPl_songIds(ArrayList<String> pl_songIds) {
		this.pl_songIds = pl_songIds;
	}
}