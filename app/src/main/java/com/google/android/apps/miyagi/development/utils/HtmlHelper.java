package com.google.android.apps.miyagi.development.utils;

import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;

/**
 * Created by jerzyw on 24.10.2016.
 */

public class HtmlHelper {

    private static final String HEAD_WITH_CSS_IGNORE_BR_TAGS =
            "<head><style>@font-face {font-family: 'Roboto-Regular';"
                    + "src: url('file:///android_asset/Roboto-Regular.ttf');}"
                    + "body {font-family: 'Roboto-Regular';font-size: 14; line-height: 1.5;"
                    + " text-align: left; color:#757575;} li {position: relative; margin-bottom:3px;}"
                    + " br {display:none;}"
                    + "</style></head>";
    private static final String HEAD_WITH_CSS_WITH_BR_TAGS =
            "<head><style>@font-face {font-family: 'Roboto-Regular';"
                    + "src: url('file:///android_asset/Roboto-Regular.ttf');}"
                    + "body {font-family: 'Roboto-Regular';font-size: 14; line-height: 1.5;"
                    + " text-align: left; color:#757575;} li {position: relative; margin-bottom:3px;}"
                    + " br {display:inline-block;}"
                    + "</style></head>";

    /**
     * Compatibility helper.
     *
     * @param htmlText - text with supported html tags.
     * @return formatted text as Spanned.
     */
    public static Spanned fromHtmlToSpanned(String htmlText) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(htmlText);
        }
    }

    /**
     * Compatibility helper.
     *
     * @param htmlText - text with supported html tags.
     * @return formatted and trailed text.
     */
    public static String fromHtml(String htmlText) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Spanned text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
            return removeTrailingWhitespaces(text);
        } else {
            Spanned text = Html.fromHtml(htmlText);
            return removeTrailingWhitespaces(text);
        }
    }

    /**
     * Compatibility helper.
     *
     * @param htmlText - text with supported html tags.
     * @return formatted and trailed text.
     */
    public static String fromHtmlWithoutBR(String htmlText) {
        htmlText = htmlText.replace("<br>", "");
        return fromHtml(htmlText);
    }

    /**
     * Formats html link.
     *
     * @param url  - http link to be opened
     * @param text - link text to be displayed
     * @return formatted text as Spanned.
     */
    public static Spanned formatHtmlLink(String url, String text) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href=\"");
        builder.append(url);
        builder.append("\">");
        builder.append(text);
        builder.append("</a>");
        return fromHtmlToSpanned(builder.toString());
    }

    private static String removeTrailingWhitespaces(CharSequence text) {
        int lastCharPosition = text.length() - 1;
        // find first non-whitespace character
        while (lastCharPosition > 0 && Character.isWhitespace(text.charAt(lastCharPosition))) {
            --lastCharPosition;
        }
        // return text without trailing whitespaces
        return text.subSequence(0, lastCharPosition + 1).toString();
    }

    /**
     * Binds webview with text adding css formatting.
     */
    public static void bindWebviewWithText(WebView webview, String text, String css) {
        String html = addCssStyle(text, css);
        webview.loadDataWithBaseURL(null, html, Params.MIME_TYPE, Params.ENCODING, null);
    }

    /**
     * Binds webview with text adding css formatting.
     */
    public static void bindWebviewWithText(WebView webview, String text) {
        String html = addCssStyle(text);
        webview.loadDataWithBaseURL(null, html, Params.MIME_TYPE, Params.ENCODING, null);
    }

    public static void bindWebviewWithTextAndBrTags(WebView webview, String text) {
        String html = addCssStyle(text, HEAD_WITH_CSS_WITH_BR_TAGS);
        webview.loadDataWithBaseURL(null, html, Params.MIME_TYPE, Params.ENCODING, null);
    }

    private static String addCssStyle(String data, String css) {
        String htmlData = "<html>" + css + "<body>" + data + "</body></html>";
        return htmlData;
    }

    private static String addCssStyle(String data) {
        String htmlData = "<html>" + HEAD_WITH_CSS_IGNORE_BR_TAGS + "<body>" + data + "</body></html>";
        return htmlData;
    }

    interface Params {
        String MIME_TYPE = "text/html";
        String ENCODING = "utf-8";
    }
}
