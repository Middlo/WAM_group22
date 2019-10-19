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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import se.chalmers.cse.dit341.group22.model.Event;
import se.chalmers.cse.dit341.group22.model.Note;
import se.chalmers.cse.dit341.group22.model.Reminder;
import se.chalmers.cse.dit341.group22.model.Task;

public class ScrollingActivity extends AppCompatActivity {

    private String currentKey;

    private ArrayList<Event> events;
    private ArrayList<Task> tasks;
    private ArrayList<Reminder> reminders;

    private Event eventOnDisplay = null;
    private Task taskOnDisplay = null;
    private Reminder reminderOnDisplay = null;

    private boolean connectionExists;

    private TextInputEditText editTitle;
    private TextInputEditText editDescr;
    private TextInputEditText editStart;
    private TextInputEditText editFinish;
    private TextInputEditText editImport;

    private TextInputEditText editTaskTitle;
    private TextInputEditText editTaskDescr;
    private TextInputEditText editTaskDeadline;
    private TextInputEditText editTaskImport;


    private TextInputEditText editRemindTitle;
    private TextInputEditText editRemindMoment;
    private TextInputEditText editRemindMinutes;
    private TextInputEditText editRemindImport;


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
    private String receivedData;

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
        tasks = new ArrayList<>(); //data in the app
        reminders = new ArrayList<>(); //data in the app

        ListView listView = (ListView) findViewById(R.id.listView);    //list for each day

        receivedData = getIntent().getStringExtra("Entity");
        String dataArray = sharedPreferences.getString(receivedData,"");

        System.out.println("received entity is " + receivedData);
        System.out.println("stored data is " + dataArray);

        if(receivedData.equals("events") || receivedData.equals("tasks")){
            String reminderArray = sharedPreferences.getString("reminders","");
            reminders.clear();
            Gson gson = new Gson();

            if(!reminderArray.equals("")){
                Reminder[] foundReminders = gson.fromJson(reminderArray, Reminder[].class);

                reminders.addAll(Arrays.asList(foundReminders));
            }
        }

        Gson gson = new Gson();
        ArrayList list = new ArrayList();   //to add the objects to scrolling list

        switch (receivedData){

            case "events":
                events.clear();
                if(!dataArray.equals("")){
                    Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                    events.addAll(Arrays.asList(foundEvents));

                    System.out.println("In Shared references Events are " + events.size());
                    for (Event event : events) {
                        list.add("Event " + event.getTitle() + ": on " + event.getStartDate().toString().substring(0,11));
                    }
                }
                break;
            case "tasks":
                tasks.clear();
                if(!dataArray.equals("")){
                    Task[] foundTasks = gson.fromJson(dataArray, Task[].class);

                    tasks.addAll(Arrays.asList(foundTasks));

                    System.out.println("In Shared references Tasks are " + tasks.size());

                    for (Task task : tasks) {
                        String txt = task.getTaskTitle();
                        if(txt != null)
                            list.add("Task " + txt);
                        else
                            list.add("Task to be assigned");
                    }
                }
                break;
            case "reminders":
                reminders.clear();
                if(!dataArray.equals("")){
                    Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                    reminders.addAll(Arrays.asList(foundReminders));

                    System.out.println("In Shared references Reminders are " + reminders.size());
                    for (Reminder reminder : reminders) {
                        list.add("Reminder " + reminder.getTopic() + ": on " + reminder.getTargetMoment().toString().substring(0,11));
                    }
                }
                break;
        }

        String Month_REGEX = "([12]\\d{3}-(0[1-9]|1[0-2]))";    //month REGEX

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

            if(info.contains("Event"))
                displayEvent(events.get(position));
            else if(info.contains("Task"))
                displayTask(tasks.get(position));
            else if(info.contains("Reminder"))
                displayReminder(reminders.get(position));

        }
    };


    private Event getEvent (String input){
        String id = input.substring(0, 24);

        for (Event ts : events) {
            if(ts.getId().equals(id))
                return ts;
        }
        return null;
    }


    private void displayEvent(Event event) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.display_event,null);
        mBuilder.setCancelable(true);

        //TextView header = (TextView) newEventView.findViewById(R.id.headerTxt);

        TextView id = (TextView) newEventView.findViewById(R.id.entityID);
        String text = "Event id: " + event.getId();
        id.setText(text);

        editTitle = (TextInputEditText) newEventView.findViewById(R.id.editEventTitle);
        if(!event.getTitle().equals(""))
            editTitle.setHint(event.getTitle());

        editDescr = (TextInputEditText) newEventView.findViewById(R.id.editEventDescr);
        if(event.getDescription() != null)
            editDescr.setHint(event.getDescription());

        editStart = (TextInputEditText) newEventView.findViewById(R.id.editEventStart);
        if(event.getStartDate() != null)
            editStart.setHint(event.getStartDate().toString().substring(0,11));

        editFinish = (TextInputEditText) newEventView.findViewById(R.id.editEventFinish);
        if(event.getEndDate() != null)
            editFinish.setHint(event.getEndDate().toString().substring(0,11));

        editImport = (TextInputEditText) newEventView.findViewById(R.id.editEventImport);
        if(event.getImportanceLevel() != null)
            editImport.setHint(event.getImportanceLevel());

        eventOnDisplay = event;
        mBuilder.setView(newEventView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    private void displayTask(Task task) {

        //Toast.makeText(getApplicationContext(),"Display is under progress", Toast.LENGTH_LONG).show();

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newTaskView = getLayoutInflater().inflate(R.layout.display_task,null);
        mBuilder.setCancelable(true);

        //TextView header = (TextView) newEventView.findViewById(R.id.headerTxt);

        TextView id = (TextView) newTaskView.findViewById(R.id.taskID);
        String text = "Task id: " + task.getId();
        id.setText(text);

        editTaskTitle = (TextInputEditText) newTaskView.findViewById(R.id.editTaskTitle);
        if(!task.getTaskTitle().equals(""))
            editTaskTitle.setHint(task.getTaskTitle());

        editTaskDescr = (TextInputEditText) newTaskView.findViewById(R.id.editTaskDescr);
        if(task.getTaskDescription() != null)
            editTaskDescr.setHint(task.getTaskDescription());

        editTaskDeadline = (TextInputEditText) newTaskView.findViewById(R.id.editTaskStart);
        if(task.getDeadline() != null)
            editTaskDeadline.setHint(task.getDeadline().toString().substring(0,11));

        editTaskImport = (TextInputEditText) newTaskView.findViewById(R.id.editTaskImport);
        if(task.getImportanceLevel() != null)
            editTaskImport.setHint(task.getImportanceLevel());

        mBuilder.setView(newTaskView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

    }


    public void displayEventReminder (View view){
        if(eventOnDisplay != null){
            boolean reminderFound = false;
            String eventId = eventOnDisplay.getId();
            System.out.println("event id: " + eventId);
            for(Reminder foundReminder : reminders){
                System.out.println("parent reminder : " + foundReminder.getReminderFor());
                if(foundReminder.getReminderFor() != null && foundReminder.getReminderFor().equals(eventId)){
                    System.out.println("Event reminder founddddddddddddddddddddddddddd");
                    reminderFound = true;
                    displayReminder(foundReminder);
                }
            }
            if(!reminderFound){
                displayMsg("Event has no reminder");
            }
        } else {
            displayMsg("Event id is not yet identified ?");
        }
    }

    public void patchEvent (android.view.View view){
        boolean dataExists = false;

        String eventTitle = null;
        if(editTitle.getText() != null){
            eventTitle = editTitle.getText().toString();
            dataExists = true;
        }

        String eventDesc = null;
        if(editDescr.getText() != null){
            eventDesc = editDescr.getText().toString();
            dataExists = true;
        }

        String eventStart = null;
        if(editStart.getText() != null){
            eventStart = editStart.getText().toString();
            dataExists = true;
        }

        String eventFinish = null;
        if(editFinish.getText() != null){
            eventFinish = editFinish.getText().toString();
            dataExists = true;
        }

        String eventImport = null;
        if(editImport.getText() != null){
            eventImport = editImport.getText().toString();
            dataExists = true;
        }

        if(dataExists){
            HashMap<String, String> newData = new HashMap<>();
            if(eventTitle != null)
                newData.put("title", eventTitle);

            if(eventDesc != null)
                newData.put("description", eventDesc);

            if(eventStart != null)
                newData.put("startDate", eventStart);

            if(eventFinish != null)
                newData.put("endDate", eventFinish);

            if(eventImport != null)
                newData.put("importanceLevel", eventImport);

            patchEntity("events", eventOnDisplay.getId(), newData);

            eventOnDisplay = null;
        } else {
            displayError("There is no new Input to Update");
        }
    }

    public void putEvent (View view){

    }

    public void dismissView (View view){
        finish();
        //Intent i = new Intent(view.getContext(), ScrollingActivity.class);
        //startActivity(i);
    }

    private void displayReminder(Reminder reminder) {

        //Toast.makeText(getApplicationContext(),"Display is under progress", Toast.LENGTH_LONG).show();

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newReminderiew = getLayoutInflater().inflate(R.layout.display_reminder,null);
        mBuilder.setCancelable(true);

        TextView id2 = (TextView) newReminderiew.findViewById(R.id.remindID);
        String text = "Reminder id: " + reminder.getId();
        id2.setText(text);

        editRemindTitle = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindTitle);
        if(!reminder.getTopic().equals(""))
            editRemindTitle.setHint(reminder.getTopic());

        editRemindMoment = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMoment);
        if(reminder.getTargetMoment() != null)
            editRemindMoment.setHint(reminder.getTargetMoment().toString().substring(0,11));

        editRemindMinutes = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMinutes);
        if(reminder.getRemindBefore() != null)
            editRemindMinutes.setHint(reminder.getRemindBefore());

        editRemindImport = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindImport);
        if(reminder.getImportanceLevel() != null)
            editRemindImport.setHint(reminder.getImportanceLevel());

        TextView parent = (TextView) newReminderiew.findViewById(R.id.reminderFor);
        String text2 = "Parent id: " + reminder.getReminderFor();
        parent.setText(text2);

        mBuilder.setView(newReminderiew);
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

    public void patchEntity(String entityName, String objectID, HashMap reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName + "/" + objectID;

                JSONObject parameters = new JSONObject(reqBody);

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PATCH, url, parameters, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // GSON allows to parse a JSON string/JSONObject directly into a user-defined class
                                Gson gson = new Gson();

                                String dataArray = null;

                                String singleEntity = entityName.substring(0,(entityName.length() - 1));
                                try {
                                    dataArray = response.getString(singleEntity);
                                } catch (JSONException e) {
                                    Log.e(this.getClass().toString(), e.getMessage());
                                }

                                boolean success = false;

                                switch (entityName){
                                    case "events":
                                        Event foundEvents = gson.fromJson(dataArray, Event.class);

                                        if(foundEvents.getId() != null){
                                            success = true;
                                        }
                                        break;

                                    case "tasks":
                                        Task foundTasks = gson.fromJson(dataArray, Task.class);

                                        if(foundTasks.getId() != null){
                                            success = true;
                                        }
                                        break;


                                    default:
                                        Reminder foundReminders = gson.fromJson(dataArray, Reminder.class);

                                        if(foundReminders.getId() != null){
                                            success = true;
                                        }
                                        break;
                                }

                                if(success){
                                    displayMsg("Updated Successfully");
                                    getEntity(entityName);
                                    finish();
                                } else {
                                    displayError(singleEntity + " is not created successfully");
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                displayError("Error! " + error.toString());
                                //mTaskView.setText("Error! " + error.toString());
                            }
                        });

                // The request queue makes sure that HTTP requests are processed in the right order.
                queue.add(jsonObjectRequest);
            } else {
                displayError("Request body is empty");
            }

        } else {
            displayError("Wrong entity name");
        }

    }

    public void displayError(String text){
        Toast.makeText(ScrollingActivity.this,text, Toast.LENGTH_LONG).show();
        System.out.println(text);
    }

    public void displayMsg(String text){
        Toast.makeText(ScrollingActivity.this,text, Toast.LENGTH_SHORT).show();
    }


    public void getEntity(String entityName){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("reminders") ){
            //String url = getString(R.string.server_url) + "/api/" + entityName;

            String url = getString(R.string.server_url) + "/api/" + entityName;

            System.out.println("url is " + url);
            // This uses Volley (Threading and a request queue is automatically handled in the background)
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            // GSON allows to parse a JSON string/JSONObject directly into a user-defined class
                            Gson gson = new Gson();

                            String dataArray = null;

                            try {
                                dataArray = response.getString(entityName);
                                //if(dataArray != null)

                                connectionExists = true;

                                if(!dataArray.equals("[]")){
                                    System.out.println("Response is: "+ dataArray);
                                    mEdit.putString(entityName, dataArray);
                                    mEdit.apply();
                                    mEdit.commit();
                                } else
                                    return;
                            } catch (JSONException e) {
                                Log.e(this.getClass().toString(), e.getMessage());
                            }

                            switch (entityName){
                                case "events":
                                    Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                    events.clear();
                                    if(foundEvents != null && dataArray.toString().contains("_id")){
                                        events.addAll(Arrays.asList(foundEvents));
                                        System.out.println("found events are " + events.size());
                                        //displayEvent(events.get(0));
                                        //System.out.println("first event id is: " + events.get(0)._id);
                                    }
                                    break;

                                case "tasks":
                                    Task[] foundTasks = gson.fromJson(dataArray, Task[].class);

                                    tasks.clear();
                                    if(foundTasks != null && dataArray.toString().contains("_id")){
                                        tasks.addAll(Arrays.asList(foundTasks));
                                        System.out.println("found tasks are " + tasks.size());
                                    }
                                    break;

                                default:
                                    Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                    reminders.clear();
                                    if(foundReminders != null && dataArray.toString().contains("_id")){
                                        reminders.addAll(Arrays.asList(foundReminders));
                                        System.out.println("found reminders are " + reminders.size());
                                    }
                                    break;
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.toString().contains("NoConnectionError")){
                                connectionExists = false;
                                displayError("There is no Connection");
                            } else {
                                displayError("Error! " + error.toString());
                            }

                            //mTaskView.setText("Error! " + error.toString());
                            System.out.println(" Error! " + error.toString());
                        }
                    });

            // The request queue makes sure that HTTP requests are processed in the right order.
            queue.add(jsonObjectRequest);
        } else {
            displayError("Wrong entity name");
        }

    }
}