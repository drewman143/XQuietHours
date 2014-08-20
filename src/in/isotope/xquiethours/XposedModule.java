/*
 * Copyright (C) 2014 Deepak Srivastav for XQuietHours Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.isotope.xquiethours;

import android.app.Notification;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.getAdditionalStaticField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalStaticField;

public class XposedModule implements IXposedHookLoadPackage {

	private QuietHoursHelper getHelper() {
		QuietHoursHelper helper = (QuietHoursHelper) getAdditionalStaticField(
				QuietHoursHelper.class, "QuietHoursHelper");
		if (helper == null) {
			Log.i("Helper is null", "Helper is null");
		}
		return helper;
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedBridge.log("Loaded app :" + lpparam.packageName);

		// initialize helper class
		setAdditionalStaticField(QuietHoursHelper.class, "QuietHoursHelper",
				new QuietHoursHelper());

		// hook notification method
		XposedHelpers.findAndHookMethod(
				"com.android.server.NotificationManagerService", null,
				"enqueueNotificationWithTag", String.class, String.class,
				String.class, int.class, Notification.class, int[].class,
				int.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						try {
							QuietHoursHelper helper = getHelper();
							if (helper.isQuietHoursEnabled()
									&& helper.isInQuietHours()) {
								Notification n = (Notification) param.args[4];
								if (n.extras
										.containsKey("gbIgnoreNotification"))
									return;

								if (helper.isNoVibrateEnabled()) {
									// do not vibrate
									n.defaults &= ~Notification.DEFAULT_VIBRATE;
									n.vibrate = new long[] { 0 };
								}

								if (helper.isMuteSoundEnabled()) {
									// do not make sound
									n.defaults &= ~Notification.DEFAULT_SOUND;
									n.sound = null;
									n.flags &= ~Notification.FLAG_INSISTENT;
								}

								if (helper.isNoLedEnabled()) {
									// led
									n.ledOffMS = 0;
									n.ledOnMS = 0;
									n.flags &= ~Notification.FLAG_SHOW_LIGHTS;
								}
							}
						} catch (Throwable t) {
							XposedBridge.log(t);
						}
					}

					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {

					}
				});
	}

}