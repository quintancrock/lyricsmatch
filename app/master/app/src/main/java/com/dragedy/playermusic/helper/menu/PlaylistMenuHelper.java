package com.dragedy.playermusic.helper.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.dragedy.playermusic.R;
import com.dragedy.playermusic.dialogs.DeletePlaylistDialog;
import com.dragedy.playermusic.dialogs.RenamePlaylistDialog;
import com.dragedy.playermusic.helper.MusicPlayerRemote;
import com.dragedy.playermusic.loader.PlaylistSongLoader;
import com.dragedy.playermusic.model.AbsCustomPlaylist;
import com.dragedy.playermusic.model.Playlist;
import com.dragedy.playermusic.model.Song;
import com.dragedy.playermusic.util.PlaylistsUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PlaylistMenuHelper {
    public static final int MENU_RES = R.menu.menu_item_playlist;

    public static boolean handleMenuClick(@NonNull AppCompatActivity activity, @NonNull final Playlist playlist, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                MusicPlayerRemote.openQueue(new ArrayList<>(getPlaylistSongs(activity, playlist)), 0, true);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(new ArrayList<>(getPlaylistSongs(activity, playlist)));
                return true;
            case R.id.action_rename_playlist:
                RenamePlaylistDialog.create(playlist.id).show(activity.getSupportFragmentManager(), "RENAME_PLAYLIST");
                return true;
            case R.id.action_delete_playlist:
                DeletePlaylistDialog.create(playlist).show(activity.getSupportFragmentManager(), "DELETE_PLAYLIST");
                return true;
            case R.id.action_save_playlist:
                @SuppressLint("ShowToast") final Toast toast = Toast.makeText(activity, R.string.saving_to_file, Toast.LENGTH_SHORT);
                new AsyncTask<Context, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        toast.show();
                    }

                    @Override
                    protected String doInBackground(Context... params) {
                        try {
                            return String.format(params[0].getString(R.string.saved_playlist_to), PlaylistsUtil.savePlaylist(params[0], playlist));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return String.format(params[0].getString(R.string.failed_to_save_playlist), e);
                        }
                    }

                    @Override
                    protected void onPostExecute(String string) {
                        super.onPostExecute(string);
                        if (toast != null) {
                            toast.setText(string);
                            toast.show();
                        }
                    }
                }.execute(activity.getApplicationContext());
                return true;
        }
        return false;
    }

    @NonNull
    private static ArrayList<? extends Song> getPlaylistSongs(@NonNull Activity activity, Playlist playlist) {
        return playlist instanceof AbsCustomPlaylist ?
                ((AbsCustomPlaylist) playlist).getSongs(activity) :
                PlaylistSongLoader.getPlaylistSongList(activity, playlist.id);
    }
}
