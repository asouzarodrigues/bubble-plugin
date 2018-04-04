/**
 */
package br.com.clarity.bubble;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;

public class BubblePlugin extends CordovaPlugin {
  private static final String TAG = "BubblePlugin";
  private BubblesManager bubblesManager;

  private boolean tryInitialize=false;
  private boolean canDraw=false;

    private static CallbackContext clickCallback;

    private  BubbleLayout bubbleView;

    TextView textView;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.d(TAG, "Initializing BubblePlugin");
  }
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        canDraw=Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(cordova.getContext());
        if(tryInitialize && bubblesManager==null){
            initializeBubblesManager();
        }
    }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if(action.equals("echo")) {
      String phrase = args.getString(0);
      // Echo back the first argument
      Log.d(TAG, phrase);
    } else if(action.equals("getDate")) {
      // An example of returning data back to the web layer
      final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
      callbackContext.sendPluginResult(result);
    }
    else if(action.equals("initializeBubble")) {
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                initializeBubble();
            }
        });
    }
    else if(action.equals("onClick")) {
        clickCallback = callbackContext;
    }
    else if(action.equals("addBubble")) {
        addBubble();
    }
    else if(action.equals("removeBubble")) {
        removeBubble();
    }
    else if(action.equals("updateNotificacao")) {
        final String notify = args.getString(0);
        cordova.getActivity().runOnUiThread(new Runnable() {
                                                public void run() {

                                                    updateNotificacao(notify);
                                                }
                                            });

    }

    return true;
  }


  void initializeBubble(){
      tryInitialize=true;
    try {

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(cordova.getContext())){

        openSettings(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,cordova.getActivity().getPackageName());
      }
      else{
         canDraw = true;
        initializeBubblesManager();
      }
      //Open the current default browswer App Info page

    } catch (ActivityNotFoundException ignored) {
      ignored.printStackTrace();
    }
  }

  private void initializeBubblesManager() {
    if(canDraw){
        int resId = getAppResource("bubble_trash_layout", "layout");
        bubblesManager = new BubblesManager.Builder(cordova.getActivity())
                .setTrashLayout(resId)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {

                    }
                })
                .build();
        bubblesManager.initialize();
    }

  }

  private  void updateNotificacao(String notify){
      if(textView!=null){

          textView.setText(notify);
          if(TextUtils.isEmpty(notify)){
              textView.setVisibility(View.INVISIBLE);
          }
          else{
              textView.setVisibility(View.VISIBLE);
          }
      }
  }
  private void removeBubble(){
      if(bubblesManager!=null && bubbleView!=null) {
          bubblesManager.removeBubble(bubbleView);
      }
  }

  private boolean addBubble() {
    if(bubblesManager!=null && bubbleView==null) {


        int resId = getAppResource("bubble_layout", "layout");
        bubbleView = (BubbleLayout) LayoutInflater.from(cordova.getActivity()).inflate(resId, null);
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
            }
        });
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
               if(clickCallback!=null){
                   PluginResult pluginresult = new PluginResult(PluginResult.Status.OK);
                   pluginresult.setKeepCallback(true);
                   clickCallback.sendPluginResult(pluginresult);
               }
            }
        });
        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);

        textView = (TextView) bubbleView.getChildAt(1);
        textView.setVisibility(View.INVISIBLE);

        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                bubbleView=null;
                textView=null;
            }
        });
        return true;
    }
    else{
        return false;
    }
  }






  private void openSettings(String settingsAction, String packageName) {
    Intent intent = new Intent(settingsAction);
    intent.setData(Uri.parse("package:" + packageName));
    cordova.getActivity().startActivityForResult(intent,999);
  }
  public  int getAppResource(String name, String type) {
    return cordova.getActivity().getResources().getIdentifier(name, type, cordova.getActivity().getPackageName());
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    Log.d(TAG,"resultado" +resultCode);
  }

}
