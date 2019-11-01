package osmandroid.iptv.home;

/*#######################################################
 *
 *   Maintained by Gregor Santner, 2019-
 *   https://gsantner.net/
 *
 *   License: Apache 2.0 / Commercial
 *  https://github.com/gsantner/opoc/#licensing
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/

/*
 * Simple Parser for M3U playlists with some extensions
 * Mainly for playlists with video streams
 */


import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Simple Parser for M3U playlists
 */
@SuppressWarnings({"WeakerAccess", "CaughtExceptionImmediatelyRethrown", "unused", "SpellCheckingInspection", "TryFinallyCanBeTryWithResources"})
public class SimpleM3UParser {
    private final static String EXTINF_TAG = "#EXTINF:";
    private final static String EXTINF_TVG_NAME = "tvg-name=\"";
    private final static String EXTINF_TVG_ID = "tvg-id=\"";
    private final static String EXTINF_TVG_LOGO = "tvg-logo=\"";
    private final static String EXTINF_TVG_EPGURL = "tvg-epgurl=\"";
    private final static String EXTINF_GROUP_TITLE = "group-title=\"";
    private final static String EXTINF_RADIO = "radio=\"";
    private final static String EXTINF_TAGS = "tags=\"";
    private final static String EXTINF_AUDIO_TRACKS = "audio-track=\"";

    private static final String LOG_TAG = "TAG";

    // ########################
    // ##
    // ## Members
    // ##
    // ########################
    private ArrayList<M3U_Entry> _entries;
    private M3U_Entry _lastEntry;

    // Parse m3u file by reading content from file by filepath
    public ArrayList<M3U_Entry> parse(String filepath) throws IOException {
        return parse(new FileInputStream(filepath));
    }

    // Parse m3u file by reading from inputstream
    // Parse one line of m3u




    public ArrayList<M3U_Entry> parse(InputStream inputStream) throws IOException {


        _entries = new ArrayList<>();
        String stream = convertStreamToString(inputStream);
        String[] linesArray = stream.split(EXTINF_TAG);
        Log.d(LOG_TAG, "lines length: "+ linesArray.length);
        for (String extinfLine : linesArray) {

                Log.d(LOG_TAG, "complete line is: "+ extinfLine);
                String[] lines = extinfLine.split("\\r?\\n");

                for (int i=0;i<lines.length;i++) {
                    Log.d(LOG_TAG, "line is: "+ lines[i]);
                    try {
                        if(i==0)
                        parseLine(EXTINF_TAG+lines[i]);
                        else parseLine(lines[i]);
                    }catch (Exception e)
                    {
                        _lastEntry = null;
                        Log.d(LOG_TAG, "parse: "+e.toString());
                    }
                }
        }
/*

        Log.d(LOG_TAG, "Entered buffer:");
        _entries = new ArrayList<>();
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                try {
                    parseLine(line);
                    Log.d(LOG_TAG, "line is : "+line);
                } catch (Exception e) {
                    _lastEntry = null;
                }
            }
        } catch (IOException rethrow) {
            throw rethrow;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        Log.d(LOG_TAG, "Exit buffer:");
        return _entries;



        */return _entries;
    }


    private void parseLine(String line) {
        line = line.trim();

        // EXTINF line
        if (line.startsWith(EXTINF_TAG)) {
            Log.d(LOG_TAG, "contains Extinf tag");
            _lastEntry = parseExtInf(line);
        }
        // URL line (no comment, no empty line(trimmed))
        else if (!line.isEmpty() && !line.startsWith("#")) {
            //Log.d(LOG_TAG, "doesn't contains Extinf tag i.e line 2");
            if (_lastEntry == null) {
                _lastEntry = new M3U_Entry();
            }
            _lastEntry.setUrl(line);
            _entries.add(_lastEntry);
            _lastEntry = null;
        }
        // No useable data -> reset last EXTINF for next entry
        else {
            _lastEntry = null;
        }
    }

    private M3U_Entry parseExtInf(String line) {
        Log.d(LOG_TAG, "Entered parseExtInf:");
        M3U_Entry curEntry = new M3U_Entry();
        StringBuilder buf = new StringBuilder(20);
        if (line.length() < EXTINF_TAG.length() + 1) {
            Log.d(LOG_TAG, "parseExtInf empty line: 0");
            return curEntry;
        }

        // Strip tag
        line = line.substring(EXTINF_TAG.length());

        // Read seconds (may end with comma or whitespace)
        while (line.length() > 0) {
            char c = line.charAt(0);
            if (Character.isDigit(c) || c == '-' || c == '+') {
                buf.append(c);
                line = line.substring(1);
            } else {
                break;
            }
        }
        if (buf.length() == 0 || line.isEmpty()) {
            Log.d(LOG_TAG, "parseExtInf empty line: ");
            return curEntry;
        }
        curEntry.setSeconds(Integer.valueOf(buf.toString()));

        // tvg tags
        while (!line.isEmpty() && !line.startsWith(",")) {
            Log.d(LOG_TAG, "parseExtInf entered while: ");
            line = line.trim();
            if (line.startsWith(EXTINF_TVG_NAME) && line.length() > EXTINF_TVG_NAME.length()) {
                line = line.substring(EXTINF_TVG_NAME.length());
                int i = line.indexOf("\"");
                curEntry.setTvgName(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_LOGO) && line.length() > EXTINF_TVG_LOGO.length()) {
                line = line.substring(EXTINF_TVG_LOGO.length());
                int i = line.indexOf("\"");
                curEntry.setTvgLogo(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_EPGURL) && line.length() > EXTINF_TVG_EPGURL.length()) {
                line = line.substring(EXTINF_TVG_EPGURL.length());
                int i = line.indexOf("\"");
                curEntry.setTvgEpgUrl(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_RADIO) && line.length() > EXTINF_RADIO.length()) {
                line = line.substring(EXTINF_RADIO.length());
                int i = line.indexOf("\"");
                curEntry.setIsRadio(Boolean.parseBoolean(line.substring(0, i)));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_GROUP_TITLE) && line.length() > EXTINF_GROUP_TITLE.length()) {
                line = line.substring(EXTINF_GROUP_TITLE.length());
                int i = line.indexOf("\"");
                curEntry.setGroupTitle(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_ID) && line.length() > EXTINF_TVG_ID.length()) {
                line = line.substring(EXTINF_TVG_ID.length());
                int i = line.indexOf("\"");
                curEntry.setTvgId(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TAGS) && line.length() > EXTINF_TAGS.length()) {
                line = line.substring(EXTINF_TAGS.length());
                int i = line.indexOf("\"");
                curEntry.setTags(line.substring(0, i).split(","));
                line = line.substring(i + 1);
            }

            if (line.startsWith(EXTINF_AUDIO_TRACKS) && line.length() > EXTINF_AUDIO_TRACKS.length()) {
                line = line.substring(EXTINF_AUDIO_TRACKS.length());
                int i = line.indexOf("\"");
                //curEntry.setTvgEpgUrl(line.substring(0, i));
                line = line.substring(i + 1);
            }
        }

        // Name
        line = line.trim();
        if (line.length() > 1 && line.startsWith(",")) {
            line = line.substring(1);
            line = line.trim();
            if (!line.isEmpty()) {
                curEntry.setName(line);
            }
        }
        Log.d(LOG_TAG, "Exit parseExtInf:");
        return curEntry;
    }



    public String convertStreamToString(InputStream is) {
        try {
            return new Scanner(is).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }


    /**
     * Data class for M3U Entries with getters & setters
     */
    public static class M3U_Entry {
        private String _tvgName, _name;
        private String _tvgLogo;
        private String _tvgEpgUrl;
        private String _tvgId;
        private String _groupTitle;
        private String _url;
        private String[] _tags = new String[0];
        private int _seconds = -1;
        private boolean _isRadio = false;

        public void setTvgName(String value) {
            _tvgName = value;
        }

        public String getName() {
            return _tvgName != null ? _tvgName : _name;
        }

        public void setName(String value) {
            _name = value;
        }

        public String getTvgLogo() {
            return _tvgLogo;
        }

        public void setTvgLogo(String value) {
            _tvgLogo = value;
        }

        public String getUrl() {
            return _url;
        }

        public void setUrl(String value) {
            _url = value;
        }

        public int getSeconds() {
            return _seconds;
        }

        public void setSeconds(int value) {
            _seconds = value;
        }

        public String getTvgEpgUrl() {
            return _tvgEpgUrl;
        }

        public void setTvgEpgUrl(String value) {
            _tvgEpgUrl = value;
        }

        public boolean isRadio() {
            return _isRadio;
        }

        public String getTvgId() {
            return _tvgId;
        }

        public void setTvgId(String value) {
            _tvgId = value;
        }

        public String getGroupTitle() {
            return _groupTitle;
        }

        public void setGroupTitle(String value) {
            _groupTitle = value;
        }

        public void setIsRadio(boolean value) {
            _isRadio = value;
        }

        public String[] getTags() {
            return _tags;
        }

        public void setTags(String[] value) {
            _tags = value;
        }

        @Override
        public String toString() {
            return getName() + " " + getUrl();
        }
    }

}

    /*

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ///  Examples
    //
    public static class Examples {
        public static List<M3U_Entry> example() {
            try {
                SimpleM3UParser simpleM3UParser = new SimpleM3UParser();
                File moviesFolder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES);
                return simpleM3UParser.parse(new File(moviesFolder, "streams.m3u").getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        public static List<M3U_Entry> exampleWithLogoRewrite() {
            List<M3U_Entry> playlist = new ArrayList<>();
            try {
                SimpleM3UParser simpleM3UParser = new SimpleM3UParser();
                File moviesFolder = new File(new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES), "liveStreams");
                File logosFolder = new File(moviesFolder, "Senderlogos");
                File streams = new File(moviesFolder, "streams.m3u");
                for (M3U_Entry entry : simpleM3UParser.parse(streams.getAbsolutePath())) {
                    if (entry.getTvgLogo() != null) {
                        String logo = new File(logosFolder, entry.getTvgLogo()).getAbsolutePath();
                        entry.setTvgLogo(logo);
                    }
                    playlist.add(entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return playlist;
        }

        public static void startStreamPlaybackInVLC(Activity activity, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setDataAndTypeAndNormalize(Uri.parse(url), "video/*");
            intent.setPackage("org.videolan.vlc");
            activity.startActivity(intent);
        }
    }



     */