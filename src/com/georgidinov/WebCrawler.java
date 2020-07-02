package com.georgidinov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    //== constants ==
    private static final String SITE_REGEX = "http://(\\w+\\.)*(\\w+)";
    private static final String SEPARATOR = File.separator;
    private static final String LOG_FILE_DIRECTORY = "Files" + SEPARATOR;
    private static final String LOG_FILE_NAME = "sitelist.txt";
    private static final String TARGET_FILE_NAME = "targetsitehtml.txt";
    private static final String TARGET_SITE = "http://radar.bg";
    private final Path logPath =
            FileSystems.getDefault().getPath(LOG_FILE_DIRECTORY + LOG_FILE_NAME);
    private final Path targetPath =
            FileSystems.getDefault().getPath(LOG_FILE_DIRECTORY + TARGET_FILE_NAME);


    //== fields ==
    private final Queue<String> siteQueue;
    private final List<String> visitedSites;
    private final List<String> targetSite;


    //== constructors ==
    public WebCrawler(Queue<String> siteQueue, List<String> visitedSites) {
        this.siteQueue = siteQueue;
        this.visitedSites = visitedSites;
        this.targetSite = new ArrayList<>();
    }//end of constructor


    //== public methods ==
    public void crawl(String siteUrl) {
        System.out.println("\nWeb Crawling Starting From " +
                siteUrl +
                " Using Breadth First Search\n" +
                "Please wait, this may take a moment...");

        this.siteQueue.add(siteUrl);
        this.visitedSites.add(siteUrl);

        while (!siteQueue.isEmpty()) {
            String currentSite = this.siteQueue.remove();
            String htmlDataFromSite = this.parseSiteContent(currentSite);

            if (currentSite.equals(TARGET_SITE)) {
                System.out.println(htmlDataFromSite);
                this.targetSite.addAll(Arrays.asList(htmlDataFromSite.split("\n")));
                this.logFoundSites(targetPath, targetSite);
            }
            this.discoverAndAddNewSites(htmlDataFromSite);
        }

        this.logFoundSites(logPath, this.visitedSites);
        System.out.println("All Found Sites Are Logged In: " + LOG_FILE_DIRECTORY + " directory");
    }//end of method crawl


    //== private methods ==
    private void discoverAndAddNewSites(String htmlDataFromSite) {
        Pattern sitePattern = Pattern.compile(SITE_REGEX);
        Matcher siteMatcher = sitePattern.matcher(htmlDataFromSite);

        while (siteMatcher.find()) {
            String newSite = siteMatcher.group();
            if (!this.visitedSites.contains(newSite)) {
                this.siteQueue.add(newSite);
                this.visitedSites.add(newSite);
            }
        }
    }//end of method discoverAndAddNewSites

    private String parseSiteContent(String siteUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(siteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Firefox");
            connection.setReadTimeout(20_000);

            BufferedReader siteReader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = siteReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed Exception " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Exception in parseSiteContent " + e.getMessage());
        }
        return sb.toString();
    }//end of method parseSiteContent

    private void logFoundSites(Path path, List<String> info) {
        try (BufferedWriter writer =
                     new BufferedWriter(Files.newBufferedWriter(path))) {
            for (String line : info) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Exception while logging visited sites " + e.getMessage());
        }
    }//end of method logFoundSites

}//end of class WebCrawler
