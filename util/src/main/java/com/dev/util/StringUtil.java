package com.dev.util;

import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@KeepNameIfNecessary
public class StringUtil {
    @KeepMemberIfNecessary
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
    @KeepMemberIfNecessary
    public static String makeQueryStringAllRegExp(String str) {
        if (str.trim().length()<=0) {
            return str;
        }
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
    @KeepMemberIfNecessary
    public static StringBuffer removeUTFCharacters(String data) {
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf;
    }
    @KeepMemberIfNecessary
    public static String  unEscapeString(String str) {
        JsonReader reader = new JsonReader(new StringReader(str));
        reader.setLenient(true);
        try {
            if (reader.peek() == JsonToken.STRING) {
                String domStr = reader.nextString();
                if (domStr != null) {
                    return domStr;
                }
            }
        } catch (IOException exception) {
            return "";
        } finally {
            try {
                reader.close();
            }catch (IOException ignored){

            }

        }
        return "";
    }
    @KeepMemberIfNecessary
    public static String getHost(String url){
        String host= Uri.parse(url).getHost();
        if (host==null || host.trim().length()==0){
            return "";
        }
        return host;
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    @KeepMemberIfNecessary
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    @KeepMemberIfNecessary
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
