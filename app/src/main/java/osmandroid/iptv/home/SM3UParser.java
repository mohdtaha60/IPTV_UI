package osmandroid.iptv.home;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SM3UParser {

    private static final String EXT_M3U = "#EXTM3U";
    private static final String EXT_INF = "#EXTINF:";
    private static final String EXT_PLAYLIST_NAME = "#PLAYLIST";
    private static final String EXT_LOGO = "tvg-logo";
    private static final String EXT_URL = "http://";

    public String convertStreamToString(InputStream is) {
        try {
            return new Scanner(is).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public ArrayList<SimpleM3UParser.M3U_Entry> parseFile(InputStream inputStream) throws FileNotFoundException {
        ArrayList<SimpleM3UParser.M3U_Entry> m3UPlaylist = new ArrayList<>();
        //List<SimpleM3UParser.M3U_Entry> playlistItems = new ArrayList<>();
        String stream = convertStreamToString(inputStream);
        String[] linesArray = stream.split(EXT_INF);
        for (int i = 0; i < linesArray.length; i++) {
            String currLine = linesArray[i];
            if (currLine.contains(EXT_M3U)) {
                //header of file
                if (currLine.contains(EXT_PLAYLIST_NAME)) {
                    String fileParams = currLine.substring(EXT_M3U.length(), currLine.indexOf(EXT_PLAYLIST_NAME));
                    String playListName = currLine.substring(currLine.indexOf(EXT_PLAYLIST_NAME) + EXT_PLAYLIST_NAME.length()).replace(":", "");
                    //m3UPlaylist.setPlaylistName(playListName);
                    //m3UPlaylist.setPlaylistParams(fileParams);
                } else {
                    //m3UPlaylist.setPlaylistName("Noname Playlist");
                    //m3UPlaylist.setPlaylistParams("No Params");
                }
            } else {
                SimpleM3UParser.M3U_Entry playlistItem = new SimpleM3UParser.M3U_Entry();
                String[] dataArray = currLine.split(",");
                if (dataArray[0].contains(EXT_LOGO)) {
                    String duration = dataArray[0].substring(0, dataArray[0].indexOf(EXT_LOGO)).replace(":", "").replace("\n", "");
                    String icon = dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO) + EXT_LOGO.length()).replace("=", "").replace("\"", "").replace("\n", "");
                    playlistItem.setSeconds(0);
                    playlistItem.setTvgLogo(icon);
                } else {
                    String duration = dataArray[0].replace(":", "").replace("\n", "");
                    playlistItem.setSeconds(0);
                    playlistItem.setTvgLogo("");
                }
                try {
                    String url = dataArray[1].substring(dataArray[1].indexOf(EXT_URL)).replace("\n", "").replace("\r", "");
                    String name = dataArray[1].substring(0, dataArray[1].indexOf(EXT_URL)).replace("\n", "");
                    playlistItem.setName(name);
                    playlistItem.setUrl(url);
                } catch (Exception fdfd) {
                    Log.e("Google", "Error: " + fdfd.fillInStackTrace());
                }
                m3UPlaylist.add(playlistItem);
            }
        }

        return m3UPlaylist;
    }
}