package com.android.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;

import com.android.settings.R;


public class StatusBarClockActivity extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_ENABLE = "clock_style";
    private static final String PREF_AM_PM_STYLE = "clock_am_pm_style";
    private static final String PREF_COLOR_PICKER = "clock_color";
    private static final String PREF_ALARM_ENABLE = "alarm";
    private static final String PREF_CLOCK_WEEKDAY = "clock_weekday";
    private static final String PREF_CUSTOM_CARRIER = "custom_carrier";
    private static final String PREF_BATTERY_TEXT = "text_widget";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";


    ListPreference mClockStyle;
    ListPreference mClockAmPmstyle;
    CheckBoxPreference mAlarm;
    ListPreference mClockWeekday;
    Preference mCustomCarrier;
    CheckBoxPreference mEnableBatteryWidget;
    ListPreference mBatteryBar;
    ListPreference mBatteryBarStyle;
    ListPreference mBatteryBarThickness;
    CheckBoxPreference mBatteryBarChargingAnimation;

    String mCustom_Carrier_Text = null;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_clock);

        mClockStyle = (ListPreference) findPreference(PREF_ENABLE);
        mClockStyle.setOnPreferenceChangeListener(this);
        mClockStyle.setValue(Integer.toString(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_CLOCK_STYLE,
                1)));

        mClockAmPmstyle = (ListPreference) findPreference(PREF_AM_PM_STYLE);
        mClockAmPmstyle.setOnPreferenceChangeListener(this);
        mClockAmPmstyle.setValue(Integer.toString(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE,
                2)));


        mAlarm = (CheckBoxPreference) findPreference(PREF_ALARM_ENABLE);
        mAlarm.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_SHOW_ALARM,
                1) == 1);

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System
                .getInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR, 0))
                + "");

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0))
                + "");

        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);
        mBatteryBarThickness.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1))
                + "");

	mEnableBatteryWidget = (CheckBoxPreference) findPreference(PREF_BATTERY_TEXT);
	mEnableBatteryWidget.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_TEXT, 0) == 1);

        mClockWeekday = (ListPreference) findPreference(PREF_CLOCK_WEEKDAY);
        mClockWeekday.setOnPreferenceChangeListener(this);
        mClockWeekday.setValue(Integer.toString(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY,
                0)));

	
	mCustomCarrier = findPreference(PREF_CUSTOM_CARRIER);
        updateCustomCarrier();

    }

   private void updateCustomCarrier() {
	mCustom_Carrier_Text = Settings.System.getString(getContentResolver(),Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustom_Carrier_Text == null) {
            mCustomCarrier.setSummary("Text");
        } else {
            mCustomCarrier.setSummary(mCustom_Carrier_Text);
        }
   }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference) {

        if (preference == mAlarm) {
            Settings.System.putInt(getContentResolver(),Settings.System.STATUSBAR_SHOW_ALARM,((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mCustomCarrier) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Set new custom carrier label");
            alert.setMessage("Custom carrier");

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustom_Carrier_Text != null ? mCustom_Carrier_Text : "");
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getContentResolver(),Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomCarrier();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
	} else if (preference == mEnableBatteryWidget) {
            Settings.System.putInt(getContentResolver(),Settings.System.STATUSBAR_BATTERY_TEXT,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

	} else if (preference == mBatteryBarChargingAnimation) {

            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mClockAmPmstyle) {

            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getContentResolver(),Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, val);

        } else if (preference == mClockStyle) {

            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getContentResolver(),Settings.System.STATUSBAR_CLOCK_STYLE, val);

        } else if (preference == mClockWeekday) {
            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getContentResolver(),Settings.System.STATUSBAR_CLOCK_WEEKDAY, val);

        } else  if (preference == mCustomCarrier) {
            return true;

        } else if (preference == mBatteryBar) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, val);

        } else if (preference == mBatteryBarStyle) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);

        } else if (preference == mBatteryBarThickness) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
        }

        return result;
    }
}
