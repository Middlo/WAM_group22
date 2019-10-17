package se.chalmers.cse.dit341.group22;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import se.chalmers.cse.dit341.group22.model.Event;

public class ScrollingActivity extends AppCompatActivity {

    private String currentKey;

    private ArrayList<Event> events;

    private TextInputEditText editTitle;
    private TextInputEditText editDescr;
    private TextInputEditText editStart;
    private TextInputEditText editFinish;
    private TextInputEditText editImport;
    private ListView listView;

    private SharedPreferences sharedPreferences;    //to save on the device
    public String startDate = "startDate";
    public String dataSize = "dataSize";

    public static final String TIME_REGEX = "^([1-9]|0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]$";
    public static final String DATE_REGEX = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
    public static final String NUM_REGEX = "^\\d+$";
    public static final String LAT_REGEX = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$";
    public static final String LON_REGEX = "^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";

    private final int APPLY = 0;    //for saving data
    private final int COMMIT = 1;

    private boolean working = false;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    private int mealBreak; //minutes   setting

    private SharedPreferences.Editor mEdit;

    private Geocoder geocoder;
    private TextInputEditText updateBreakTime;
    private TextInputEditText updateStampIn;
    private TextInputEditText updateStampOut;
    private TextInputEditText updateNoWorkAfter;
    private TextInputEditText updateStartBuffer;
    private TextInputEditText updateFinishBuffer;
    private TextInputEditText updateDistanceGap;
    private TextInputEditText updateTimeInterval;
    private TextInputEditText updateTarLat;
    private TextInputEditText updateTarLon;
    private TextInputEditText updateWIFI;
    private boolean serviceCanStart = true;
    private boolean targetSettingExists = true;

    public void setMealBreak(int mealBreak) {
        this.mealBreak = mealBreak;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_AppCompat_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);    //this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //sharedPreferences = getSharedPreferences(preferences1, MODE_PRIVATE);
        mEdit = sharedPreferences.edit();
        events = new ArrayList<>(); //data in the app
        listView = (ListView) findViewById(R.id.listView);    //list for each day

        //mealBreak = sharedPreferences.getInt("mealBreak", 0);


        //readFromData(); //read backup from app shared preference

        //saveData(COMMIT);




        String Month_REGEX = "([12]\\d{3}-(0[1-9]|1[0-2]))";    //month REGEX

        //intent as a string if this activity is executed from other activity
        //updatedData = getIntent().getStringExtra("Reply");

        //checkForCompleteness();     //duration will be calculated for all objects

        /*
        if (updatedData != null && !updatedData.isEmpty()) {
            String firstPart = extractFirst();  //the first part of string from other activity
            String secondPart;
            if (firstPart.matches(Month_REGEX)) {    //if it is a month
                secondPart = updatedData;       //check the second part of a string
                if (secondPart.equals("remove"))     //remove the whole month
                    removeMonth(firstPart);
                else if (secondPart.equals("total"))  {   //total hours in a month
                    showTotal(firstPart);
                }
            } else {                //It is a date format
                secondPart = extractFirst();    //second part of the string
                String finish = updatedData;     //third part of the string

                int index = retrieveData(firstPart);    //index of date in the sheet object

                if (secondPart.equals("zero") && finish.equals("zero")) {     //remove the date
                    if(firstPart.equals(currentKey)){
                        Toast.makeText(getApplicationContext(),"Active Sheet shall not be removed",Toast.LENGTH_LONG).show();
                    } else {
                        timeSheets.remove(timeSheets.get(index));   //remove from object
                        removeData(firstPart);  // to remove from device
                    }
                } else if (index >= 0) {  //update the sheet for that day
                    TimeSheet currentSheet = timeSheets.get(index);
                    currentSheet.setApprovedStart(secondPart);
                    currentSheet.setApprovedFinish(finish);
                } else {    //if it does not exist, create the object
                    TimeSheet newSheet = new TimeSheet(firstPart);
                    timeSheets.add(newSheet);
                    registerFirstDate(firstPart);   //If it only the first ever date for the device
                    newSheet.setApprovedStart(secondPart);
                    newSheet.setApprovedFinish(finish);
                }
            }
        }
        */

        //checkForCompleteness();     //duration will be calculated for all objects
        //saveData(COMMIT);

        ArrayList list = new ArrayList();   //to add the objects to scrolling list
        for (Event ts : events) {
            list.add(ts.toString());
        }

            //display all objects as a list
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(myListClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(broadcastReceiver, new IntentFilter(MyService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //readFromData();
    }

    @Override
    protected void onDestroy() {
        /*
        saveData(COMMIT);
        //System.out.println("meal breakkkkkkkkkkkk " + sharedPreferences.getInt("mealBreak", 30));
        //showInfo(sharedPreferences.getString(currentKey,""));
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        if( retrieveData(currentKey) < 0)
            readFromData();
        intent.putExtra("currentDay", timeSheets.get(retrieveData(currentKey)).toString());
        stopService(intent);
        */
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //menu created
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //when items of menu are selected
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*
        int id = item.getItemId();  //get the id of item selected

        switch (id){
            case R.id.action_addDate:   //id add date is selected
                openAddDateActivity();
                break;
            case R.id.action_removeMonth:   //if removing a month is needed
                openRemoveMonthActivity();
                break;
            case R.id.action_totalHour: //if total hours of a month is needed
                openShowTotalHourActivity();
                break;
            case R.id.action_settings: //settings
                openSettingsActivity();
                break;
            default:    //
                return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    //if an object is clicked
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String info = ((TextView) view).getText().toString();

            Event foundEvent = getEvent(info);

            displayEvent(foundEvent);
            /*
            // Make an intent to start next activity with a string as a message
            Intent i = new Intent(ScrollingActivity.this, DisplayTimeSheet.class);
            i.putExtra("Details", info);
            startActivity(i);
            */

        }
    };


    private Event getEvent (String input){
        String id = input.substring(0, 24);

        for (Event ts : events) {
            if(ts._id.equals(id))
                return ts;
        }
        return null;
    }

    //open the displayAddDate activity without any message
    private void displayEvent(Event event) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.display_event,null);
        mBuilder.setCancelable(true);

        //TextView header = (TextView) newEventView.findViewById(R.id.headerTxt);

        TextView id = (TextView) newEventView.findViewById(R.id.entityID);
        String text = "Event Code: " + event._id;
        id.setText(text);

        editTitle = (TextInputEditText) newEventView.findViewById(R.id.editEventTitle);
        if(!event.title.equals(""))
            editTitle.setHint(event.title);

        editDescr = (TextInputEditText) newEventView.findViewById(R.id.editEventDescr);
        if(event.description != null)
            editDescr.setHint(event.description);

        editStart = (TextInputEditText) newEventView.findViewById(R.id.editEventStart);
        if(event.startDate != null)
            editStart.setHint(event.startDate.toString().substring(0,10));

        editFinish = (TextInputEditText) newEventView.findViewById(R.id.editEventFinish);
        if(event.endDate != null)
            editFinish.setHint(event.endDate.toString().substring(0,10));

        editImport = (TextInputEditText) newEventView.findViewById(R.id.editEventImport);
        if(event.importanceLevel != null)
            editImport.setHint(event.importanceLevel);

        mBuilder.setView(newEventView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    /**
     * reads data from shared preferences of the device
     * */
    /*
    public void readFromData(){

        LocalDate today = LocalDate.now();

        int savedSize = sharedPreferences.getInt(dataSize,0);

        if (savedSize > 0){
            String firstDate = sharedPreferences.getString(startDate,"");

            if(firstDate.equals(""))
                firstDate = thisMonthFirstDay(today + "");


            LocalDate begin = LocalDate.parse(firstDate);

            long duration = ChronoUnit.DAYS.between(begin, today) + 1;

            for(int i = 0; i < duration; i ++){
                LocalDate day = begin.plusDays(i);
                String foundData = sharedPreferences.getString((day + ""),"");
                if(foundData.length() > 0){
                    addToSheets(foundData);
                }
            }
        }
    }

     */


    /**
     * Depending on temporary or permanent saving mode, data is saved on the
     * shared preferences of the device
     * */
    /*
    public void saveData(int savingType){
        String infoString;
        String strDate;

        if(timeSheets.size() != 0){
            Collections.sort(timeSheets);
            int size = timeSheets.size();
            mEdit.putInt(dataSize,size);

            for(int i = 0; i < size; i++){
                TimeSheet sheet = timeSheets.get(i);
                if(i == 0){
                    strDate = sheet.getDate();
                    mEdit.putString(startDate,strDate);
                }

                infoString = sheet.toString();

                mEdit.putString(sheet.getDate(),infoString);
                mEdit.apply();
            }
            if(savingType == COMMIT)
                mEdit.commit();
        }
    }

     */

}