package io.sugo.android.xwalk;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.sugo.android.metrics.SGConfig;
import io.sugo.android.metrics.SugoAPI;
import io.sugo.android.metrics.SugoWebEventListener;

/**
 * Created by Administrator on 2017/2/8.
 */

public class SugoXWalkViewEventListener extends SugoWebEventListener {

    protected static HashSet<XWalkView> sCurrentXWalkView = new HashSet<>();

    SugoXWalkViewEventListener(SugoAPI sugoAPI) {
        super(sugoAPI);
    }

    @Override
    @JavascriptInterface
    @org.xwalk.core.JavascriptInterface
    public void track(String eventId, String eventName, String props) {
        super.track(eventId, eventName, props);
    }

    @Override
    @JavascriptInterface
    @org.xwalk.core.JavascriptInterface
    public void timeEvent(String eventName) {
        super.timeEvent(eventName);
    }

    @Override
    @JavascriptInterface
    @org.xwalk.core.JavascriptInterface
    public boolean isShowHeatMap() {
        return super.isShowHeatMap();
    }

    @Override
    @JavascriptInterface
    @org.xwalk.core.JavascriptInterface
    public int getEventHeatColor(String eventId) {
        return super.getEventHeatColor(eventId);
    }

    public static void bindEvents(String token, JSONArray eventBindings) {
//        eventBindingsMap.put(token, eventBindings);
        if (SugoAPI.editorConnected) {      // 只在连接编辑器模式下操作
            updateXWalkViewInject();
        } else {
            sCurrentXWalkView.clear();
        }
    }

    public static void addCurrentXWalkView(XWalkView currentXWalkView) {
        sCurrentXWalkView.add(currentXWalkView);
        if (SGConfig.DEBUG) {
            Log.d("SugoWebEventListener", "addCurrentXWalkView : " + currentXWalkView.toString());
        }

    }

    public static void updateXWalkViewInject() {
        Iterator<XWalkView> viewIterator = sCurrentXWalkView.iterator();
        while (viewIterator.hasNext()) {
            final XWalkView xWalkView = viewIterator.next();
            if (xWalkView == null) {
                return;
            }
            ((Activity) xWalkView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xWalkView.reload(XWalkView.RELOAD_NORMAL);
                    if (SGConfig.DEBUG) {
                        Log.d("SugoWebEventListener", "XWalkView reload : " + xWalkView.toString());
                    }
                }
            });
        }
    }

    /**
     * 移除 webview 引用，即移除对 Activity 的引用，避免内存泄漏
     */
    public static void cleanUnuseXwalkView(Activity deadActivity) {
        Iterator<XWalkView> xWalkViewIterator = sCurrentXWalkView.iterator();
        XWalkView xWalkView = null;
        List<XWalkView> removeXWVS = new ArrayList<>();
        while (xWalkViewIterator.hasNext()) {
            xWalkView = xWalkViewIterator.next();
            Activity activity = (Activity) xWalkView.getContext();
            if (activity == null || activity == deadActivity || activity.isFinishing() ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
                removeXWVS.add(xWalkView);
            }
        }
        for (XWalkView removeXWV : removeXWVS) {
            sCurrentXWalkView.remove(removeXWV);
            sugoWNReporter.remove(removeXWV);
            if(SGConfig.getInstance(removeXWV.getContext()).isEnablePageEvent()) {
                removeXWV.load("javascript:" + sStayScript, null);
            }
            if (SGConfig.DEBUG) {
                Log.d("SugoWebEventListener", "removeXWalkViewReference : " + removeXWV.toString());
            }
        }
    }

}
