package ru.hse.cs.java2020.task03;

import org.glassfish.grizzly.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Tracker {
    private static final String BASE_URL = "https://api.tracker.yandex.net/v2/";
    private HttpURLConnection con = null;
    private static final String COUNT_JOBS_ON_PAGE = "3";


    private void initConnection(String token, String orgId) {
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "OAuth " + token);
        con.setRequestProperty("X-Org-Id", orgId);
    }

    public PageJobRet requestPageJobs(String generatedUrl, String token, String orgId) throws MalformedURLException {
        URL url;
        PageJobRet pageJobRet = new PageJobRet();

        if (generatedUrl != null) {
            url = new URL(generatedUrl);
        } else {
            String param = "?scrollType=sorted&perScroll=" + COUNT_JOBS_ON_PAGE;
            url = new URL(BASE_URL + "issues/_search" + param);
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            initConnection(token, orgId);

            String jsonInputString = "{\"filter\": {\"assignee\": \"me()\"} }";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    JSONArray jsonarray = new JSONArray(responseLine);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String key = jsonobject.getString("key");
                        String name = jsonobject.getString("summary");
                        pageJobRet.addJobsInfo(new Pair<>(key, name));
                    }
                }

                if (con.getHeaderField("Link").indexOf("next") == -1) {
                    pageJobRet.setNextPage(null);
                } else {
                    Integer from = con.getHeaderField("Link").indexOf("<");
                    from++;
                    Integer to = con.getHeaderField("Link").indexOf(">");
                    String np = con.getHeaderField("Link").substring(from, to);
                    pageJobRet.setNextPage(np);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return pageJobRet;
    }

    public void createTask(String jsonString, String token, String orgId) throws MalformedURLException {
        URL url = new URL(BASE_URL + "issues/");

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            initConnection(token, orgId);

            System.out.println(jsonString);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonString.getBytes();
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    System.out.println(responseLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
    }

    public TaskInfo getTask(String taskId, String token, String orgId) throws MalformedURLException {
        URL url = new URL(BASE_URL + "issues/" + taskId);
        TaskInfo taskInfoRet = new TaskInfo();

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            initConnection(token, orgId);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    JSONObject jsonobject = new JSONObject(responseLine);

                    taskInfoRet.setName(jsonobject.getString("summary"));
                    taskInfoRet.setAuthor(jsonobject.getJSONObject("createdBy").getString("display"));

                    if (!jsonobject.isNull("description")) {
                        Document doc = Jsoup.parse(jsonobject.getString("description"));
                        taskInfoRet.setDescription(doc.body().text());
                    }


                    if (!jsonobject.isNull("assignee")) {
                        taskInfoRet.setExecutor(jsonobject.getJSONObject("assignee").getString("display"));
                    }
                    if (!jsonobject.isNull("followers")) {
                        JSONArray jsonarray = jsonobject.getJSONArray("followers");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            taskInfoRet.addWatcher(jsonarray.getJSONObject(i).getString("display"));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            url = new URL(BASE_URL + "issues/" + taskId + "/comments");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            initConnection(token, orgId);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    JSONArray jsonarray = new JSONArray(responseLine);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        taskInfoRet.addComment(jsonarray.getJSONObject(i).getString("text"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return taskInfoRet;
    }

    public String getSelfUid(String token, String orgId) throws MalformedURLException {
        URL url = new URL(BASE_URL + "myself");
        String ret = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            initConnection(token, orgId);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    JSONObject jsonarray = new JSONObject(responseLine);
                    ret = String.valueOf(jsonarray.getInt("uid"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return ret;
    }
}
