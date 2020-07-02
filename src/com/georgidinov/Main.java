package com.georgidinov;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {

        String siteUrl = "https://www.mail.bg";

        //== dependency injection for my Web Crawler ==
        Queue<String> queue = new ArrayDeque<>();
        List<String> siteList = new ArrayList<>();

        WebCrawler myCrawler = new WebCrawler(queue, siteList);
        myCrawler.crawl(siteUrl);

    }//end of main method

}//end of class Main
