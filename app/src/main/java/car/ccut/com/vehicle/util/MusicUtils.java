package car.ccut.com.vehicle.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import java.util.ArrayList;
import java.util.List;

import car.ccut.com.vehicle.bean.Mp3;


public class MusicUtils {

	private static List<String> mMusicList = new ArrayList<String>();
	public static ArrayList<String> al_playlist = new ArrayList<String>();
	private static List<Mp3> playList = new ArrayList<Mp3>();
	private static String[] mCols = new String[] { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
	/**
	 * 清空歌曲列表中的全部歌曲，plid为列表id
	 */
	public static void clearPlaylist(Context context, long plid) {
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
		context.getContentResolver().delete(uri, null, null);
		return;
	}

	public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
	}

	public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
			}
			return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}
	/**
	 * 得到歌曲列表中的全部歌曲，plid为列表id
	 */
	public static ArrayList<Mp3> getSongListForPlaylist(Context context, long plid) {
		final String[] ccols = new String[] { MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ARTIST,
				MediaStore.Audio.Playlists.Members.AUDIO_ID,MediaColumns.DATA };

		Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid), ccols, null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

		if (cursor != null) {
			ArrayList<Mp3> list = getSongListForCursor(context, cursor);
			cursor.close();
			return list;
		}
		return null;
	}

	public static ArrayList<Mp3> getSongListForCursor(Context context, Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		int len = cursor.getCount();
		long[] list = new long[len];
		cursor.moveToFirst();
		int id = -1, title = -1, artist = -1;
		int allSongIndex = -1;
		int url = -1;
		try {
			allSongIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
			id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID);
			title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE);
			artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST);
			url = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		} catch (IllegalArgumentException ex) {
			id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
		}
		ArrayList<Mp3> songs = new ArrayList<Mp3>();
		songs.clear();
		for (int i = 0; i < len; i++) {
			long song_id = cursor.getLong(id);
			long allSongId = cursor.getLong(allSongIndex);
			String song_url = cursor.getString(url);
			String tilte = cursor.getString(title);
			String song_artist = cursor.getString(artist);

			Mp3 song = new Mp3();
			song.setSqlId(Integer.parseInt(song_id + ""));
			song.setName(tilte);
			song.setSingerName(song_artist);
			song.setAllSongIndex(allSongId);
			song.setUrl(song_url);
			songs.add(song);

			cursor.moveToNext();
		}
		return songs;
	}

	private static ContentValues[] sContentValuesCache = null;

	private static void makeInsertItems(long[] ids, int offset, int len, int base) {
		if (offset + len > ids.length) {
			len = ids.length - offset;
		}
		if (sContentValuesCache == null || sContentValuesCache.length != len) {
			sContentValuesCache = new ContentValues[len];
		}
		for (int i = 0; i < len; i++) {
			if (sContentValuesCache[i] == null) {
				sContentValuesCache[i] = new ContentValues();
			}
			sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
			sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
		}
	}

	/**
	 * 得到媒体库中的全部歌曲
	 */
	public static List<Mp3> getAllSongs(Context context) {
		Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaColumns.DATA },
				MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
		try {
			if (c == null || c.getCount() == 0) {
				return null;
			}
			int len = c.getCount();

			ArrayList<Mp3> list = new ArrayList<Mp3>();
			int id = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
			int title = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			int name = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			int url = c.getColumnIndex(MediaColumns.DATA);
			for (int i = 0; i < len; i++) {
				Mp3 mp3 = new Mp3();
				c.moveToNext();
				mp3.setSqlId(Integer.parseInt(c.getLong(id) + ""));
				mp3.setName(c.getString(title));
				mp3.setSingerName(c.getString(name));
				mp3.setUrl(c.getString(url));

				list.add(mp3);
			}

			return list;
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	/**
	 * 得到所有歌手的名字列表
	 */
	public static List<String> MusicSingerList(Context context) {
		mMusicList.clear();
		if (mMusicList.size() == 0) {
			ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				// 获取全部歌曲
				Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				if (null == cursor) {
					return null;
				}
				if (cursor.moveToFirst()) {
					do {
						String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
						if ("<unknown>".equals(singer)) {
							singer = "unknow artist";
						}
						String name = cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME));
						String sbr = name.substring(name.length() - 3, name.length());
						// lac也是一种格式
						if (sbr.equals("mp3") || sbr.endsWith("lac")) {
							if (!mMusicList.contains(singer)) {
								mMusicList.add(singer);
							}
						}
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return mMusicList;
	}
	/**
	 * 通过歌手名字，得到该歌手的所有歌曲
	 */
	public static List<Mp3> MusicMp3ListbySinger(Context context, String Name) {
		List<Mp3> singerMp3 = new ArrayList<Mp3>();
		if (singerMp3.size() == 0) {
			ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				// 获取全部歌曲
				Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				if (null == cursor) {
					return null;
				}
				if (cursor.moveToFirst()) {
					do {
						String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
						if ("<unknown>".equals(singer)) {
							singer = "unknow artist";
						}
						if (singer.equals(Name)) {
							int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
							String title = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
							String url = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
							String name = cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME));
							String sbr = name.substring(name.length() - 3, name.length());
							int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
							// Log.e("--------------", sbr);
							if (sbr.equals("mp3") || sbr.endsWith("lac")) {
								Mp3 mp3 = new Mp3();
								mp3.setDuration(duration);
								mp3.setName(title);
								mp3.setPictureID(id);
								mp3.setUrl(url);
								singerMp3.add(mp3);
							}
						}
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return singerMp3;
	}
	/**
	 * 通过歌曲id，找到其所对应的专辑图片路径，这个方法把注释去掉就能用
	 */
	public static String getAlbumArt(Context context, int trackId) {// trackId是音乐的id
		String mUriTrack = "content://media/external/audio/media/#";
		String[] projection = new String[] { "album_id" };
		String selection = "_id = ?";
		String[] selectionArgs = new String[] { Integer.toString(trackId) };
		Cursor cur = context.getContentResolver().query(Uri.parse(mUriTrack),
				projection, selection, selectionArgs, null);
		int album_id = 0;
		if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
			cur.moveToNext();
			album_id = cur.getInt(0);
		}
		cur.close();
		cur = null;

		if (album_id < 0) {
			return null;
		}
		String mUriAlbums = "content://media/external/audio/albums";
		projection = new String[] { "album_art" };
		cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" +
				Integer.toString(album_id)), projection, null, null, null);

		String album_art = null;
		if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
			cur.moveToNext();
			album_art = cur.getString(0);
		}
		cur.close();
		cur = null;
		return album_art;

	}

	/**
	 * 得到专辑中的所有歌曲
	 */
	public static List<Mp3> MusicMp3ListbyAlbum(Context context, String Album) {
		List<Mp3> albumMp3 = new ArrayList<Mp3>();
		if (albumMp3.size() == 0) {
			ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				// 获取全部歌曲
				Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				if (null == cursor) {
					return null;
				}
				if (cursor.moveToFirst()) {
					do {
						String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
						if ("<unknown>".equals(album)) {
							album = "unknow album";
						}
						if (album.equals(Album)) {
							String title = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
							int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
							String url = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
							String name = cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME));
							String sbr = name.substring(name.length() - 3, name.length());
							int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
							// Log.e("--------------", sbr);
							if (sbr.equals("mp3") || sbr.endsWith("lac")) {
								Mp3 mp3 = new Mp3();
								mp3.setDuration(duration);
								mp3.setName(title);
								mp3.setPictureID(id);
								mp3.setUrl(url);
								albumMp3.add(mp3);
							}
						}
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return albumMp3;
	}

	/**
	 * 得到所有歌曲列表的列表名字
	 */
	public static List<String> PlaylistList(Context context) {

		List<String> listSongs = new ArrayList<String>();
		if (listSongs.size() == 0) {
			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mCols, null, null, MediaStore.Audio.Playlists._ID + " desc");
			al_playlist.clear();
			int len = cursor.getCount();
			int id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
			int name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);

			for (int i = 0; i < len; i++) {
				cursor.moveToNext();
				long id_temp = cursor.getLong(id);
				String temp = cursor.getString(name);
				al_playlist.add(id_temp + "");
				listSongs.add(temp);
			}
		}
		return listSongs;
	}

	/**
	 * 通过歌曲列表名找到列表id
	 */
	public static long getPlayListId(Context context, String listName) {
		long listId = -1;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mCols, null, null, MediaStore.Audio.Playlists._ID + " desc");
		al_playlist.clear();
		int len = cursor.getCount();
		int id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
		int name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);

		for (int i = 0; i < len; i++) {
			cursor.moveToNext();
			long id_temp = cursor.getLong(id);
			String temp = cursor.getString(name);
			if (listName.equals(temp)) {
				listId = id_temp;
			}
		}
		return listId;
	}

	public static List<Mp3> getPlayList() {
		return playList;
	}

	public static void setPlayList(List<Mp3> playList) {
		MusicUtils.playList = playList;
	}

	public static ArrayList<String> getAl_playlist() {
		return al_playlist;
	}

	public static void setAl_playlist(ArrayList<String> al_playlist) {
		MusicUtils.al_playlist = al_playlist;
	}

}
