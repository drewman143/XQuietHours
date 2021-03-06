package in.isotope.xquiethours;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TimePickerDialog;
import android.content.Context;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class QuietHourPreference extends Preference implements
		View.OnClickListener {

	private static final int DIALOG_START_TIME = 1;
	private static final int DIALOG_END_TIME = 2;

	private TextView mStartTimeText;
	private TextView mEndTimeText;
	private int mStartTime;
	private int mEndTime;

	private ToggleButton[] buttons = new ToggleButton[7];

	private ToggleButton noSound;
	private ToggleButton noVibe;
	private ToggleButton noLED;

	public QuietHourPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		View startTimeLayout = view.findViewById(R.id.start_time);
		if ((startTimeLayout != null)
				&& startTimeLayout instanceof LinearLayout) {
			startTimeLayout.setOnClickListener(this);
		}

		View endTimeLayout = view.findViewById(R.id.end_time);
		if ((endTimeLayout != null) && endTimeLayout instanceof LinearLayout) {
			endTimeLayout.setOnClickListener(this);
		}

		mStartTimeText = (TextView) view.findViewById(R.id.start_time_text);
		mEndTimeText = (TextView) view.findViewById(R.id.end_time_text);
		DateFormatSymbols symbols = new DateFormatSymbols();
		String[] dayNames = symbols.getShortWeekdays();

		ToggleButton sunday = (ToggleButton) view.findViewById(R.id.SUN);
		sunday.setText(dayNames[1]);
		sunday.setTextOn(dayNames[1]);
		sunday.setTextOff(dayNames[1]);

		if (null != sunday && sunday instanceof ToggleButton) {
			sunday.setOnClickListener(this);
			buttons[0] = sunday;
		}

		ToggleButton monday = (ToggleButton) view.findViewById(R.id.MON);
		monday.setText(dayNames[2]);
		monday.setTextOn(dayNames[2]);
		monday.setTextOff(dayNames[2]);
		if (null != monday && monday instanceof ToggleButton) {
			monday.setOnClickListener(this);
			buttons[1] = monday;
		}

		ToggleButton tuesday = (ToggleButton) view.findViewById(R.id.TUE);
		tuesday.setText(dayNames[3]);
		tuesday.setTextOn(dayNames[3]);
		tuesday.setTextOff(dayNames[3]);
		if (null != tuesday && tuesday instanceof ToggleButton) {
			tuesday.setOnClickListener(this);
			buttons[2] = tuesday;
		}

		ToggleButton wednesday = (ToggleButton) view.findViewById(R.id.WED);
		wednesday.setText(dayNames[4]);
		wednesday.setTextOn(dayNames[4]);
		wednesday.setTextOff(dayNames[4]);
		if (null != wednesday && wednesday instanceof ToggleButton) {
			wednesday.setOnClickListener(this);
			buttons[3] = wednesday;
		}

		ToggleButton thursday = (ToggleButton) view.findViewById(R.id.THU);
		thursday.setText(dayNames[5]);
		thursday.setTextOn(dayNames[5]);
		thursday.setTextOff(dayNames[5]);
		if (null != thursday && thursday instanceof ToggleButton) {
			thursday.setOnClickListener(this);
			buttons[4] = thursday;
		}

		ToggleButton friday = (ToggleButton) view.findViewById(R.id.FRI);
		friday.setText(dayNames[6]);
		friday.setTextOn(dayNames[6]);
		friday.setTextOff(dayNames[6]);
		if (null != friday && friday instanceof ToggleButton) {
			friday.setOnClickListener(this);
			buttons[5] = friday;
		}

		ToggleButton saturday = (ToggleButton) view.findViewById(R.id.SAT);
		saturday.setText(dayNames[7]);
		saturday.setTextOn(dayNames[7]);
		saturday.setTextOff(dayNames[7]);
		if (null != saturday && saturday instanceof ToggleButton) {
			saturday.setOnClickListener(this);
			buttons[6] = saturday;
		}

		ToggleButton noLedButton = (ToggleButton) view.findViewById(R.id.led);
		if (null != noLedButton && noLedButton instanceof ToggleButton) {
			noLedButton.setOnClickListener(this);
			noLED = noLedButton;
		}

		ToggleButton noSoundButton = (ToggleButton) view
				.findViewById(R.id.sound);
		if (null != noSoundButton && noSoundButton instanceof ToggleButton) {
			noSoundButton.setOnClickListener(this);
			noSound = noSoundButton;
		}

		ToggleButton noVibeButton = (ToggleButton) view.findViewById(R.id.vibe);
		if (null != noVibeButton && noVibeButton instanceof ToggleButton) {
			noVibeButton.setOnClickListener(this);
			noVibe = noVibeButton;
		}

		// get saved values
		updateSavedValue();
		updatePreferenceViews();
	}

	private void init() {
		setLayoutResource(R.layout.quiet_hour_preference);
	}

	private void saveValue() {
		String str = mStartTime + "|" + mEndTime;

		List<Integer> daysOfWeekList = new ArrayList<Integer>();
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].isChecked()) {
				daysOfWeekList.add(i);
			}
		}
		
		JSONObject preference1 = new JSONObject();
		try {
			preference1.put(QuietHoursHelper.KEY_QUIET_HOURS_TIME_RANGE, str);
			preference1.put(QuietHoursHelper.KEY_DAYS_OF_WEEK, new JSONArray(
					daysOfWeekList));
			preference1.put(QuietHoursHelper.KEY_NO_VIBE, noVibe.isChecked());
			preference1.put(QuietHoursHelper.KEY_MUTE_SOUND,
					noSound.isChecked());
			preference1.put(QuietHoursHelper.KEY_NO_LED, noLED.isChecked());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		persistString(preference1.toString());
	}

	private void updateSavedValue() {
		try {
			String currentTimeValue = "0|0";

			JSONObject preference1 = new JSONObject(getPersistedString(""));

			currentTimeValue = preference1
					.getString(QuietHoursHelper.KEY_QUIET_HOURS_TIME_RANGE);
			String[] split = currentTimeValue.split("\\|");
			if (split.length == 2) {
				try {
					mStartTime = Integer.parseInt(split[0]);
					mEndTime = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
					mStartTime = 0;
					mEndTime = 0;
				}
			} else {
				mStartTime = 0;
				mEndTime = 0;
			}

			JSONArray dayOfWeek = preference1
					.getJSONArray(QuietHoursHelper.KEY_DAYS_OF_WEEK);
			for (int i = 0; i < dayOfWeek.length(); i++) {
				buttons[dayOfWeek.getInt(i)].setChecked(true);
			}

			noLED.setChecked(preference1
					.getBoolean(QuietHoursHelper.KEY_NO_LED));
			noSound.setChecked(preference1
					.getBoolean(QuietHoursHelper.KEY_MUTE_SOUND));
			noVibe.setChecked(preference1
					.getBoolean(QuietHoursHelper.KEY_NO_VIBE));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updatePreferenceViews() {
		if (mStartTimeText != null) {
			mStartTimeText.setText(returnTime(mStartTime));
		}
		if (mEndTimeText != null) {
			mEndTimeText.setText(returnTime(mEndTime));
		}
	}

	public void setStartTime(int time) {
		mStartTime = time;
		updatePreferenceViews();
	}

	public void setEndTime(int time) {
		mEndTime = time;
		updatePreferenceViews();
	}

	public void setTimeRange(int stime, int etime) {
		mStartTime = stime;
		mEndTime = etime;
		updatePreferenceViews();
	}

	public int getStartTime() {
		return (mStartTime);
	}

	public int getEndTime() {
		return (mEndTime);
	}

	@Override
	public void onClick(android.view.View v) {
		if (v != null) {
			if (R.id.start_time == v.getId()) {
				TimePicker(DIALOG_START_TIME);
			} else if (R.id.end_time == v.getId()) {
				TimePicker(DIALOG_END_TIME);
			} else {
				saveValue();
			}
		}
	}

	private void TimePicker(final int key) {
		int hour;
		int minutes;
		int value = (key == DIALOG_START_TIME ? mStartTime : mEndTime);

		if (value < 0) {
			Calendar calendar = Calendar.getInstance();
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minutes = calendar.get(Calendar.MINUTE);
		} else {
			hour = value / 60;
			minutes = value % 60;
		}

		Context context = getContext();
		TimePickerDialog dlg = new TimePickerDialog(context,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker v, int hours, int minutes) {
						int time = hours * 60 + minutes;
						if (key == DIALOG_START_TIME) {
							mStartTime = time;
							mStartTimeText.setText(returnTime(time));
						} else {
							mEndTime = time;
							mEndTimeText.setText(returnTime(time));
						}
						callChangeListener(this);
						saveValue();
					}
				}, hour, minutes, DateFormat.is24HourFormat(context));
		dlg.show();
	}

	private String returnTime(int t) {
		if (t < 0) {
			return "";
		}

		int hr = t;
		int mn = t;

		hr = hr / 60;
		mn = mn % 60;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hr);
		cal.set(Calendar.MINUTE, mn);
		Date date = cal.getTime();
		return DateFormat.getTimeFormat(getContext()).format(date);
	}
}
