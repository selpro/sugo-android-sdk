package io.sugo.android.metrics;

import android.app.ActivityManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ouwenjie
 * @date 2017/1/22
 */

public class SugoPageManager {

    private static SugoPageManager sInstance = new SugoPageManager();
    private HashMap<String, JSONObject> mPageInfos = new HashMap<>();

    private SugoPageManager() {

    }

    public static SugoPageManager getInstance() {
        return sInstance;
    }

    public void setPageInfos(JSONArray pageInfos) {
        if (pageInfos == null) {
            return;
        }

        mPageInfos.clear();
        JSONObject pageObj = null;
        for (int i = 0; i < pageInfos.length(); i++) {
            try {
                pageObj = pageInfos.getJSONObject(i);
                mPageInfos.put(pageObj.optString("page"), pageObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public String getCurrentPageName(String currentPage) {
        String currentPageName = "";
        JSONObject pageObj = getCurrentPageInfo(currentPage);
        if (pageObj != null) {
            currentPageName = pageObj.optString("page_name", "");
        }
        return currentPageName;
    }

    public String getCurrentPageCategory(String currentPage) {
        String categoryName = "";
        JSONObject pageObj = getCurrentPageInfo(currentPage);
        if (pageObj != null) {
            categoryName = pageObj.optString("category", "");
        }
        return categoryName;
    }

    public JSONObject getCurrentPageInfo(String currentPage) {
        if (mPageInfos.size() > 0) {
            return mPageInfos.get(currentPage);
        }
        return null;
    }

    public HashMap<String, JSONObject> getPageInfo() {
        return mPageInfos;
    }

    public String getCurrentPageName(Context context) {
        String currentPageName = "";
        JSONObject pageObj = getCurrentPageInfo(context);
        if (pageObj != null) {
            currentPageName = pageObj.optString("page_name", "");
        }
        return currentPageName;
    }

    public String getCurrentPageCategory(Context context) {
        String categoryName = "";
        JSONObject pageObj = getCurrentPageInfo(context);
        if (pageObj != null) {
            categoryName = pageObj.optString("category", "");
        }
        return categoryName;
    }

    private JSONObject getCurrentPageInfo(Context context) {
        String currentPage = getCurrentPage(context);
        if (mPageInfos.size() > 0) {
            return mPageInfos.get(currentPage);
        }
        return null;
    }

    public String getCurrentPage(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null && !runningTaskInfos.isEmpty()) {
            return runningTaskInfos.get(0).topActivity.getClassName();
        }
        return null;
    }

}
