package cn.poria.common.security.xss.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssFilterUtil {

    // 标签白名单
    private static final Set<String> WHITE_LIST_TAGS = new HashSet<>(
        Arrays.asList("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                      "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3",
                      "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q",
                      "small", "strike", "strong", "sub", "sup", "table", "tbody",
                      "td", "tfoot", "th", "thead", "tr", "u", "ul")
    );

    // 属性白名单
    private static final Set<String> WHITE_LIST_ATTRS = new HashSet<>(
        Arrays.asList("align", "alt", "axis", "bgcolor", "border", "cellpadding",
                      "cellspacing", "class", "color", "cols", "colspan", "datetime",
                      "dir", "height", "href", "id", "lang", "longdesc", "name",
                      "noshade", "nowrap", "rel", "rev", "rows", "rowspan", "rules",
                      "scope", "size", "span", "src", "start", "style", "summary",
                      "tabindex", "target", "title", "type", "valign", "value", "width")
    );

    // 安全的协议
    private static final Set<String> SAFE_PROTOCOLS = new HashSet<>(
        Arrays.asList("http", "https", "mailto", "ftp", "tel", "sms")
    );

    // 事件属性前缀 - 缩小范围，只包含真正的事件处理前缀
    private static final List<String> EVENT_PREFIXES = Arrays.asList(
        "javascript:", "vbscript:", "data:", "about:", "file:"
    );

    // 用于检测完整的script标签
    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile(
        "<script\\s+[^>]*?>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // 用于检测HTML注释中的script
    private static final Pattern COMMENT_SCRIPT_PATTERN = Pattern.compile(
        "<!\\-\\-.*?<script.*?>.*?\\-\\->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // 用于检测HTML标签 - 改进匹配逻辑，只匹配完整标签
    private static final Pattern TAG_PATTERN = Pattern.compile(
        "<(/?)([a-zA-Z]+)\\s*([^>]*)>", Pattern.CASE_INSENSITIVE
    );

    // 用于检测属性
    private static final Pattern ATTR_PATTERN = Pattern.compile(
        "(\\w+)\\s*=\\s*['\"]([^'\"]*)['\"]", Pattern.CASE_INSENSITIVE
    );

    // 用于检测事件处理属性
    private static final Pattern EVENT_ATTR_PATTERN = Pattern.compile(
        "on\\w+\\s*=\\s*['\"][^'\"]*['\"]", Pattern.CASE_INSENSITIVE
    );

    // 过滤XSS
    public static String filterXss(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 先移除明显的script标签
        String result = SCRIPT_TAG_PATTERN.matcher(input).replaceAll("");
        result = COMMENT_SCRIPT_PATTERN.matcher(result).replaceAll("");

        // 处理所有HTML标签
        StringBuilder output = new StringBuilder();
        Matcher tagMatcher = TAG_PATTERN.matcher(result);
        int lastIndex = 0;

        while (tagMatcher.find()) {
            // 添加标签前的文本
            output.append(result, lastIndex, tagMatcher.start());

            // 处理标签
            String tag = tagMatcher.group();
            output.append(cleanTag(tag));

            lastIndex = tagMatcher.end();
        }

        // 添加最后一个标签后的文本
        output.append(result.substring(lastIndex));

        return output.toString();
    }

    // 清理单个标签
    private static String cleanTag(String tag) {
        // 提取标签信息
        Matcher tagMatcher = TAG_PATTERN.matcher(tag);
        if (!tagMatcher.matches()) {
            return ""; // 不应该发生，因为已经通过TAG_PATTERN匹配
        }

        boolean isClosing = !tagMatcher.group(1).isEmpty();
        String tagName = tagMatcher.group(2).toLowerCase();
        String attrs = tagMatcher.group(3);

        // 不在白名单中的标签直接移除
        if (!WHITE_LIST_TAGS.contains(tagName)) {
            return "";
        }

        // 处理结束标签
        if (isClosing) {
            return "</" + tagName + ">";
        }

        // 处理开始标签和自闭合标签
        StringBuilder cleanTag = new StringBuilder();
        cleanTag.append("<").append(tagName);

        if (!attrs.isEmpty()) {
            cleanTag.append(" ").append(cleanAttributes(attrs));
        }

        // 判断是否为自闭合标签
        if (tag.trim().endsWith("/>")) {
            cleanTag.append(" />");
        } else {
            cleanTag.append(">");
        }

        return cleanTag.toString();
    }

    // 清理标签属性
    private static String cleanAttributes(String attrs) {
        StringBuilder cleanAttrs = new StringBuilder();
        Matcher attrMatcher = ATTR_PATTERN.matcher(attrs);

        while (attrMatcher.find()) {
            String attrName = attrMatcher.group(1).toLowerCase();
            String attrValue = attrMatcher.group(2);

            // 检查属性是否在白名单中
            if (!WHITE_LIST_ATTRS.contains(attrName)) {
                continue;
            }

            // 检查属性值是否包含危险协议
            if (isUnsafeAttributeValue(attrName, attrValue)) {
                continue;
            }

            // 添加安全的属性
            cleanAttrs.append(attrName).append("=\"").append(attrValue).append("\" ");
        }

        return cleanAttrs.toString().trim();
    }

    // 检查属性值是否包含危险协议
    private static boolean isUnsafeAttributeValue(String attrName, String attrValue) {
        // 特殊处理href, src等URL属性
        if (attrName.equals("href") || attrName.equals("src") || attrName.equals("background")) {
            if (attrValue == null || attrValue.isEmpty()) {
                return true;
            }

            // 检查协议
            int colonIndex = attrValue.indexOf(':');
            if (colonIndex > 0) {
                String protocol = attrValue.substring(0, colonIndex).toLowerCase();
                return !SAFE_PROTOCOLS.contains(protocol);
            }

            // 检查是否以javascript:等开头
            for (String prefix : EVENT_PREFIXES) {
                if (attrValue.toLowerCase().startsWith(prefix)) {
                    return true;
                }
            }
        }

        // 检查属性值是否包含事件处理函数
        return EVENT_ATTR_PATTERN.matcher(attrValue).find();
    }

    // 判断是否包含XSS
    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 简单检查script标签
        if (SCRIPT_TAG_PATTERN.matcher(input).find()) {
            return true;
        }

        // 检查HTML注释中的script
        if (COMMENT_SCRIPT_PATTERN.matcher(input).find()) {
            return true;
        }

        // 检查所有HTML标签
        Matcher tagMatcher = TAG_PATTERN.matcher(input);
        while (tagMatcher.find()) {
            String tag = tagMatcher.group();
            if (isUnsafeTag(tag)) {
                return true;
            }
        }

        return false;
    }

    // 检查标签是否不安全
    private static boolean isUnsafeTag(String tag) {
        // 提取标签信息
        Matcher tagMatcher = TAG_PATTERN.matcher(tag);
        if (!tagMatcher.matches()) {
            return false; // 不是有效的标签
        }

        boolean isClosing = !tagMatcher.group(1).isEmpty();
        String tagName = tagMatcher.group(2).toLowerCase();
        String attrs = tagMatcher.group(3);

        // 不在白名单中的标签
        if (!WHITE_LIST_TAGS.contains(tagName)) {
            return true;
        }

        // 检查标签属性
        Matcher attrMatcher = ATTR_PATTERN.matcher(attrs);
        while (attrMatcher.find()) {
            String attrName = attrMatcher.group(1).toLowerCase();
            String attrValue = attrMatcher.group(2);

            // 不在白名单中的属性
            if (!WHITE_LIST_ATTRS.contains(attrName)) {
                return true;
            }

            // 不安全的属性值
            if (isUnsafeAttributeValue(attrName, attrValue)) {
                return true;
            }
        }

        return false;
    }
}
