package com.dev.adblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Url过滤工具类。
 *
 * @author Xu, Zewen; Zhang, Yin
 */
public class Filter {

    // **************** 公开变量

    // **************** 私有变量

    /**
     * 常规规则集
     */
    private static HashMap<String, ArrayList<String>> subDomainToRules = new HashMap<String, ArrayList<String>>();

    /**
     * 特殊规则集
     */
    private static ArrayList<String> specialRules = new ArrayList<>();

    /**
     * 常规规则集subDomain对应的规则上限
     */
    private static int maxNumOfRules = 100;

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 查看url是否被包含在规则集
     *
     * @param url
     * @return
     */
    public static boolean Check(String url) {
        if (Check(specialRules, url)) {
            return true;
        } else {
            ArrayList<String> subDomain = UrlToSubDomain(url);
            for (int i = 0; i < subDomain.size(); i++) {
                if (subDomainToRules.containsKey(subDomain.get(i))) {
                    if (Check(Objects.requireNonNull(subDomainToRules.get(subDomain.get(i))), url))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 插入规则
     *
     * @param rule
     * @return
     */
    public static boolean InsertRules(String rule) {
        rule = UrlToDomain(rule);
        ArrayList<String> subDomains = UrlToSubDomain(rule);

        if (subDomains.size() == 0) {
            return false;
        } else {
            int i;
            for (i = 0; i < subDomains.size(); i++) {
                if (!subDomainToRules.containsKey(subDomains.get(i))) {
                    subDomainToRules.put(subDomains.get(i), new ArrayList<String>());
                    Objects.requireNonNull(subDomainToRules.get(subDomains.get(i))).add(rule);
                    return true;
                }
                if (Objects.requireNonNull(subDomainToRules.get(subDomains.get(i))).contains(rule))
                    return true;
                if (Objects.requireNonNull(subDomainToRules.get(subDomains.get(i))).size() > maxNumOfRules)
                    continue;
                subDomainToRules.get(subDomains.get(i)).add(rule);
                return true;
            }
            if (i == subDomains.size())
                specialRules.add(rule);
        }
        return true;
    }

    // **************** 私有方法

    /**
     * 查看url是否被包含在rules中
     *
     * @param rules
     * @param url
     * @return
     */
    private static boolean Check(ArrayList<String> rules, String url) {
        String domain = UrlToDomain(url);
        String[] subDomains = domain.split("\\.");
        for (int i = 0; i < rules.size(); i++) {
            String[] subDomainOfRules = rules.get(i).split("\\.");
            if (subDomainOfRules.length != subDomains.length)
                continue;
            int j;
            for (j = 0; j < subDomains.length; j++) {
                if (subDomainOfRules[j].equals(subDomains[j]) || subDomainOfRules[j].equals("*"))
                    continue;
                else {
                    break;
                }
            }
            if (j == subDomains.length)
                return true;
        }
        return false;
    }

    /**
     * 分离url
     *
     * @param url
     * @return domain
     */
    private static String UrlToDomain(String url) {
        int domainOfStart = 0, domainOfEnd = url.length();
        if (url.contains("http")) {
            domainOfStart = url.indexOf("//") + 2;
        }
        if (url.indexOf("/", domainOfStart) != -1) {
            domainOfEnd = url.indexOf("/", domainOfStart);
        }
        return url.substring(domainOfStart, domainOfEnd);
    }

    /**
     * 切割domain
     *
     * @param url
     * @return
     */
    private static ArrayList<String> UrlToSubDomain(String url) {
        ArrayList<String> result = new ArrayList<>();
        int domainOfStart = 0, domainOfEnd = url.length();
        if (url.contains("http")) {
            domainOfStart = url.indexOf("//") + 2;
        }
        if (url.indexOf("/", domainOfStart) != -1) {
            domainOfEnd = url.indexOf("/", domainOfStart);
        }
        String domain = url.substring(domainOfStart, domainOfEnd);

        String[] subDomains = domain.split("\\.");
        for (int i = 1; i < subDomains.length - 1; i++) {
            if (!subDomains[i].contains("*"))
                result.add(subDomains[i]);
        }
        return result;
    }
}