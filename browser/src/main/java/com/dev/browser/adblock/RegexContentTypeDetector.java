package com.dev.browser.adblock;

import org.adblockplus.libadblockplus.FilterEngine;

import java.util.regex.Pattern;

public class RegexContentTypeDetector
{
    private static final Pattern RE_JS = Pattern.compile("\\.js(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_CSS = Pattern.compile("\\.css(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_IMAGE = Pattern.compile("\\.(?:gif|png|jpe?g|bmp|ico)(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_FONT = Pattern.compile("\\.(?:ttf|woff)(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_HTML = Pattern.compile("\\.html?(?:\\?.+)?", Pattern.CASE_INSENSITIVE);

    /**
     * Detects ContentType for given URL
     * @param url URL
     * @return ContentType or `null` if not detected
     */
    public FilterEngine.ContentType detect(final String url)
    {
        FilterEngine.ContentType contentType = null;
        if (RE_JS.matcher(url).find())
        {
            contentType = FilterEngine.ContentType.SCRIPT;
        }
        else if (RE_CSS.matcher(url).find())
        {
            contentType = FilterEngine.ContentType.STYLESHEET;
        }
        else if (RE_IMAGE.matcher(url).find())
        {
            contentType = FilterEngine.ContentType.IMAGE;
        }
        else if (RE_FONT.matcher(url).find())
        {
            contentType = FilterEngine.ContentType.FONT;
        }
        else if (RE_HTML.matcher(url).find())
        {
            contentType = FilterEngine.ContentType.SUBDOCUMENT;
        }
        return contentType;
    }
}
