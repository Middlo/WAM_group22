package se.chalmers.cse.dit341.group22;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import se.chalmers.cse.dit341.group22.model.Event;
import se.chalmers.cse.dit341.group22.model.Reminder;
import se.chalmers.cse.dit341.group22.model.Task;

public class MainActivity extends AppCompatActivity {

    // Field for parameter name
    public static final String HTTP_PARAM = "httpResponse";

    private AlertDialog dialog;
    private ArrayList<Event> events;
    private ArrayList<Task> tasks;
    private ArrayList<Reminder> reminders;
    private String toolBarDate;
    private String selectedDate;
    private String todaysDate = new Date().toString();

    private TextInputEditText editTitle;
    private TextInputEditText editDescr;
    private TextInputEditText editStart;
    private TextInputEditText editFinish;
    private TextInputEditText editImport;

    private TextInputEditText editEventTitleCrt;
    private TextInputEditText editEventDescrCrt;
    private TextInputEditText editEventStartCrt;
    private TextInputEditText editEventFinishCrt;
    private TextInputEditText editEventImportCrt;

    private TextInputEditText editTaskTitleCrt;
    private TextInputEditText editTaskDescrCrt;
    private TextInputEditText editTaskStartCrt;
    private TextInputEditText editTaskImportCrt;

    private TextInputEditText editRemindTitleCrt;
    private TextInputEditText editRemindMomentCrt;
    private TextInputEditText editRemindMinutesCrt;
    private TextInputEditText editRemindImportCrt;
    private TextInputEditText editTReminderFor;

    private int dataCounter = 0;

    private Event eventOnDisplay = null;
    private Task taskOnDisplay = null;
    private Reminder reminderOnDisplay = null;

    Button displayEvents;
    Button displayTasks;
    Button displayReminders;

    private Event eventResult;
    private Task taskResult;
    private Reminder reminderResult;

    boolean connectionExists;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEdit;

    public MainActivity(){
        events = new ArrayList <>();
        tasks = new ArrayList <>();
        reminders = new ArrayList <>();

        //getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Init View

        System.out.println("Today is " + todaysDate);
        displayEvents = (Button) findViewById(R.id.displayEvents);
        displayTasks = (Button) findViewById(R.id.displayTasks);
        displayReminders = (Button) findViewById(R.id.displayReminders);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEdit = sharedPreferences.edit();

        getData();

        displayEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mEdit.putString("events", "");
                //mEdit.apply();
                getEntity("events");

                if(events.size() > 0){
                    //System.out.println("stored for scroll is " + sharedPreferences.getString("reminders", ""));
                    Intent i = new Intent(MainActivity.this, ScrollingActivity.class);
                    i.putExtra("Entity", "events");
                    startActivity(i);
                } else if(connectionExists){
                    displayError("There are no Events to Display");
                } else {
                    getData();
                }
            }
        });

        displayTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mEdit.putString("tasks", "");
                //mEdit.apply();
                getEntity("tasks");

                if(tasks.size() > 0){
                    //System.out.println("stored for scroll is " + sharedPreferences.getString("reminders", ""));
                    Intent i = new Intent(MainActivity.this, ScrollingActivity.class);
                    i.putExtra("Entity", "tasks");
                    startActivity(i);
                } else if(connectionExists){
                    displayError("There are no Tasks to Display");
                } else {
                    displayMsg("Fetching data...");
                    getData();
                }

            }
        });

        displayReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mEdit.putString("reminders", "");
                //mEdit.apply();
                getEntity("reminders");

                if(reminders.size() > 0){
                    //System.out.println("stored for scroll is " + sharedPreferences.getString("reminders", ""));
                    Intent i = new Intent(MainActivity.this, ScrollingActivity.class);
                    i.putExtra("Entity", "reminders");
                    startActivity(i);
                } else if(connectionExists){
                    displayError("There are no Reminders to Display");
                } else {
                    displayMsg("Fetching data...");
                    getData();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //menu created
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //when items of menu are selected
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();  //get the id of item selected

        switch (id){
            case R.id.action_events:
                HashMap<String, String> newEvent = new HashMap<>();
                newEvent.put("title", "To be assigned");
                newEvent.put("startDate", convertDateFormat(new Date()));
                newEvent.put("endDate", convertDateFormat(new Date()));

                createEntity("events", newEvent);

                break;
            case R.id.action_tasks:
                HashMap<String, String> newTask = new HashMap<>();
                newTask.put("taskTitle", "To be assigned");
                newTask.put("deadline", convertDateFormat(new Date()));

                createEntity("tasks", newTask);

                break;
            case R.id.action_reminders:
                HashMap<String, String> newReminder = new HashMap<>();
                newReminder.put("topic", "To be given");
                newReminder.put("targetMoment", convertDateFormat(new Date()));

                createEntity("reminders", newReminder);

                break;
            default:    //
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //open
    private void openAddEventActivity(Event event) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.create_event,null);
        mBuilder.setCancelable(true);

        TextView id = (TextView) newEventView.findViewById(R.id.entityIDCrt);
        String text = "Event id: " + event.getId();
        id.setText(text);

        editEventTitleCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventTitleCrt);
        if(!event.getTitle().equals(""))
            editEventTitleCrt.setHint(event.getTitle());

        editEventDescrCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventDescrCrt);
        if(event.getDescription() != null)
            editEventDescrCrt.setHint(event.getDescription());

        editEventStartCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventStartCrt);
        if(event.getStartDate() != null)
            editEventStartCrt.setHint(convertDateFormat(event.getStartDate()));

        editEventFinishCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventFinishCrt);
        if(event.getEndDate() != null)
            editEventFinishCrt.setHint(convertDateFormat(event.getEndDate()));

        editEventImportCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventImportCrt);
        if(event.getImportanceLevel() != null)
            editEventImportCrt.setHint(event.getImportanceLevel());

        eventOnDisplay = event;

        mBuilder.setView(newEventView);
        dialog = mBuilder.create();
        dialog.show();
    }


    public void patchEvent (android.view.View view){
        boolean dataExists = false;

        String eventTitle = null;
        if(editEventTitleCrt.getText() != null){
            eventTitle = editEventTitleCrt.getText().toString();
            dataExists = true;
        }

        String eventDesc = null;
        if(editEventDescrCrt.getText() != null){
            eventDesc = editEventDescrCrt.getText().toString();
            dataExists = true;
        }

        String eventStart = null;
        if(editEventStartCrt.getText() != null){
            eventStart = editEventStartCrt.getText().toString();
            dataExists = true;
        }

        String eventFinish = null;
        if(editEventFinishCrt.getText() != null){
            eventFinish = editEventFinishCrt.getText().toString();
            dataExists = true;
        }

        String eventImport = null;
        if(editEventImportCrt.getText() != null){
            eventImport = editEventImportCrt.getText().toString();
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

    public void putEvent (android.view.View view){
        boolean dataExists = false;
        boolean shouldContinue = true;

        String eventTitle = null;
        if(editEventTitleCrt.getText() != null){
            eventTitle = editEventTitleCrt.getText().toString();
            dataExists = true;
        }

        String eventDesc = null;
        if(editEventDescrCrt.getText() != null){
            eventDesc = editEventDescrCrt.getText().toString();
            dataExists = true;
        }

        String eventStart = null;
        if(editEventStartCrt.getText() != null){
            eventStart = editEventStartCrt.getText().toString();
            dataExists = true;
            editEventStartCrt.setError(null);
        } else {
            shouldContinue = false;
            editEventStartCrt.setError("Should have a date");
        }

        String eventFinish = null;
        if(editEventFinishCrt.getText() != null){
            eventFinish = editEventFinishCrt.getText().toString();
            dataExists = true;
            editEventFinishCrt.setError(null);
        } else {
            shouldContinue = false;
            editEventFinishCrt.setError("Should have a date");
        }

        String eventImport = null;
        if(editEventImportCrt.getText() != null){
            eventImport = editEventImportCrt.getText().toString();
            dataExists = true;
        }

        if(dataExists && shouldContinue){
            HashMap<String, String> newData = new HashMap<>();
            if(eventTitle != null)
                newData.put("title", eventTitle);
            else
                newData.put("title", "");

            if(eventDesc != null)
                newData.put("description", eventDesc);
            else
                newData.put("description", "");


            newData.put("startDate", eventStart);

            newData.put("endDate", eventFinish);

            if(eventImport != null)
                newData.put("importanceLevel", eventImport);
            else
                newData.put("importanceLevel", "");

            putEntity("events", eventOnDisplay.getId(), newData);

            eventOnDisplay = null;
        } else {
            displayError("There is Input error");
        }
    }


    public void deleteEvent (View view){
        deleteAnEntity("events", eventOnDisplay.getId());
        events.remove(eventOnDisplay);
    }


    //open
    private void openAddTaskActivity(Task task) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newTaskView = getLayoutInflater().inflate(R.layout.create_task,null);
        mBuilder.setCancelable(true);

        TextView id = (TextView) newTaskView.findViewById(R.id.taskIDCrt);
        String text = "Task id: " + task.getId();
        id.setText(text);

        editTaskTitleCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskTitleCrt);
        if(!task.getTaskTitle().equals(""))
            editTaskTitleCrt.setHint(task.getTaskTitle());

        editTaskDescrCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskDescrCrt);
        if(task.getTaskDescription() != null)
            editTaskDescrCrt.setHint(task.getTaskDescription());

        editTaskStartCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskStartCrt);
        if(task.getDeadline() != null)
            editTaskStartCrt.setHint(convertDateFormat(task.getDeadline()));

        editTaskImportCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskImportCrt);
        if(task.getImportanceLevel() != null)
            editTaskImportCrt.setHint(task.getImportanceLevel());

        taskOnDisplay = task;

        mBuilder.setView(newTaskView);
        dialog = mBuilder.create();
        dialog.show();
    }


    private void openAddReminderActivity(String remID) {
        Reminder reminder = null;

        for(Reminder rem: reminders){
            if(rem.getId().equals(remID))
                reminder = rem;
        }

        if(reminder != null){
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View newReminderView = getLayoutInflater().inflate(R.layout.create_reminder,null);
            mBuilder.setCancelable(true);

            TextView id = (TextView) newReminderView.findViewById(R.id.remindIDCrt);
            String text = "Reminder id: " + reminder.getId();
            id.setText(text);

            editRemindTitleCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindTitleCrt);
            if(!reminder.getTopic().equals(""))
                editRemindTitleCrt.setHint(reminder.getTopic());

            editRemindMomentCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindMomentCrt);
            if(reminder.getTargetMoment() != null)
                editRemindMomentCrt.setHint(convertDateFormat(reminder.getTargetMoment()));

            editRemindMinutesCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindMinutesCrt);
            if(reminder.getRemindBefore() != null)
                editRemindMinutesCrt.setHint(reminder.getRemindBefore());

            editRemindImportCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindImportCrt);
            if(reminder.getImportanceLevel() != null)
                editRemindImportCrt.setHint(reminder.getImportanceLevel());

            editTReminderFor = (TextInputEditText) newReminderView.findViewById(R.id.editTReminderFor);
            if(reminder.getReminderFor() != null)
                editTReminderFor.setHint(reminder.getReminderFor());

            reminderOnDisplay = reminder;

            mBuilder.setView(newReminderView);
            dialog = mBuilder.create();
            dialog.show();
        } else {
            displayMsg("Success");
        }

    }


    /*
    public void onClickGetEvents (View view) {
        // Get the text view in which we will show the result.
        final TextView mEventView = findViewById(R.id.dashboardText);

        String url = getString(R.string.server_url) + "/api/events";

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
                            dataArray = response.getString("events");
                        } catch (JSONException e) {
                            Log.e(this.getClass().toString(), e.getMessage());
                        }

                        StringBuilder eventString = new StringBuilder();
                        eventString.append("This is the list of my events: \n");

                        Event[] events = gson.fromJson(dataArray, Event[].class);

                        //for (Event current : events) {
                          //  eventString.append("Event is " + current.color + " at "
                            //        + current.position + "\n");
                        //}

                        mEventView.setText(eventString.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mEventView.setText("Error! " + error.toString());
                    }
                });

        // The request queue makes sure that HTTP requests are processed in the right order.
        queue.add(jsonObjectRequest);
    }

     */

    /*
    public void getText(){
        final TextView textView = (TextView) findViewById(R.id.dashboardText);
        // ...

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://online-calendar22.herokuapp.com/api/events";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText("Response is: "+ response.substring(0,500));
                        System.out.println("Response is: "+ response.substring(0,500));

                        try {
                            readEventsFromJson(response);
                            System.out.println("Events are " + events.size());
                        } catch (Exception e) {
                            Log.e(this.getClass().toString(), e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

     */

    public void getEvents(){

        // Get the text view in which we will show the result.
        //final TextView mEventView = findViewById(R.id.dashboardText);

        String url = getString(R.string.server_url) + "/api/events";

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
                            dataArray = response.getString("events");
                        } catch (JSONException e) {
                            Log.e(this.getClass().toString(), e.getMessage());
                        }

                        StringBuilder eventString = new StringBuilder();
                        eventString.append("This is the list of my events: \n");

                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                        events = null;
                        for (Event current : foundEvents) {

                            events.add(current);
                            eventString.append("Event id: " + current.getId() + " with title "
                                    + current.getTitle() + " and start Date " + current.getStartDate() + "\n");
                        }

                        //mEventView.setText(eventString.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //mEventView.setText("Error! " + error.toString());
                    }
                });

        // The request queue makes sure that HTTP requests are processed in the right order.
        queue.add(jsonObjectRequest);

    }

    public void getData(){
        if(dataCounter == 0)
            displayMsg("Fetching data...");

        mEdit.putString("events", "");
        mEdit.putString("tasks", "");
        mEdit.putString("reminders", "");
        mEdit.apply();

        getEntity("events");
        //if(connectionExists) {
            getEntity("tasks");
            getEntity("reminders");
        //}

    }


    public void readEventsFromJson(String input)throws Exception {

        Gson gson = new Gson();

        events = gson.fromJson(input, new TypeToken<ArrayList<Event>>(){}.getType());

    }//Eyuell

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

                                if(dataArray != null ){
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
                                    if(dataCounter++ == 0){
                                        displayMsg("Data fetched");
                                    }

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

    public void displayError(String text){
        Toast.makeText(MainActivity.this,text, Toast.LENGTH_LONG).show();
        System.out.println(text);
    }

    public void displayMsg(String text){
        Toast.makeText(MainActivity.this,text, Toast.LENGTH_SHORT).show();
    }

    public void createEntity(String entityName, HashMap reqBody){
        eventResult = null;
        taskResult = null;
        reminderResult = null;

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName;

                JSONObject parameters = new JSONObject(reqBody);

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

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
                                        try{
                                            Event foundEvent = gson.fromJson(dataArray, Event.class);

                                            if(foundEvent.getId() != null){
                                                success = true;
                                                eventResult = foundEvent;
                                                getEntity(entityName);
                                                openAddEventActivity(foundEvent);
                                                //displayMsg("Event created successfully");
                                            } else {
                                                displayMsg("Event is not created");
                                            }
                                        } catch (Exception err){
                                            displayError(err.toString());
                                        }
                                        break;

                                    case "tasks":
                                        Task foundTasks = gson.fromJson(dataArray, Task.class);

                                        if(foundTasks.getId() != null){
                                            success = true;
                                            taskResult =  foundTasks;
                                            getEntity(entityName);
                                            openAddTaskActivity(foundTasks);
                                            //displayMsg("Task created successfully");
                                        } else {
                                            displayMsg("Task is not created");
                                        }
                                        break;

                                    default:
                                        Reminder foundReminder = gson.fromJson(dataArray, Reminder.class);

                                        if(foundReminder.getId() != null){
                                            //displayMsg("new Rem is " + foundReminder.getId());
                                            success = true;
                                            reminderResult = foundReminder;
                                            reminders.add(foundReminder);
                                            getEntity(entityName);
                                            openAddReminderActivity(foundReminder.getId());
                                            //displayMsg("Reminder created successfully");
                                        } else {
                                            displayMsg("Reminder is not created");
                                        }
                                        break;
                                }

                                if(!success){
                                    displayError(singleEntity + " is not created");
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

                                        if(foundReminders.getId()!= null){
                                            success = true;
                                        }
                                        break;
                                }

                                if(success){
                                    displayMsg("Updated Successfully");
                                    getEntity(entityName);
                                    dialog.dismiss();
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


    public void putEntity(String entityName, String objectID, HashMap reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName + "/" + objectID;

                JSONObject parameters = new JSONObject(reqBody);

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {

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
                                    dialog.dismiss();
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


    public void deleteAnEntity(String entityName, String objectID){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("reminders") ){

            String url = getString(R.string.server_url) + "/api/" + entityName + "/" + objectID;

            // This uses Volley (Threading and a request queue is automatically handled in the background)
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // GSON allows to parse a JSON string/JSONObject directly into a user-defined class
                            Gson gson = new Gson();

                            String dataArray = null;

                            try {
                                dataArray = response.getString("message");
                            } catch (JSONException e) {
                                Log.e(this.getClass().toString(), e.getMessage());
                            }

                            String foundMessage = gson.fromJson(dataArray, String.class);

                            if(foundMessage.equals("Success")){
                                displayMsg("Updated Successfully");
                                getEntity(entityName);
                                if(dialog != null)
                                    dialog.dismiss();
                                eventOnDisplay = null;
                                taskOnDisplay = null;
                                reminderOnDisplay = null;
                            } else {
                                displayError(objectID + " is not deleted");
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
            displayError("Wrong entity name");
        }

    }

/*
    public void deleteAllEntity(String entityName){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){

            String url = getString(R.string.server_url) + "/api/" + entityName;

            // This uses Volley (Threading and a request queue is automatically handled in the background)
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // GSON allows to parse a JSON string/JSONObject directly into a user-defined class
                            Gson gson = new Gson();

                            String dataArray = null;

                            try {
                                dataArray = response.getString("message");
                            } catch (JSONException e) {
                                Log.e(this.getClass().toString(), e.getMessage());
                            }

                            String foundMessage = gson.fromJson(dataArray, String.class);

                            if(foundMessage.equals("Success")){
                                getEntity(entityName);
                            } else {
                                displayError(" Deleting all is not successful");
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
            displayError("Wrong entity name");
        }

    }


 */

    //open the activity without any message
    private void displayEvent(Event event) {

        //Event event = events.get(0);
        // Make an intent to start next activity.
        //Intent i = new Intent(ScrollingActivity.this, DisplayAddDate.class);
        //startActivity(i);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.display_event_main,null);
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
            editStart.setHint(convertDateFormat(event.getStartDate()));

        editFinish = (TextInputEditText) newEventView.findViewById(R.id.editEventFinish);
        if(event.getEndDate() != null)
            editFinish.setHint(convertDateFormat(event.getEndDate()));

        editImport = (TextInputEditText) newEventView.findViewById(R.id.editEventImport);
        if(event.getImportanceLevel() != null)
            editImport.setHint(event.getImportanceLevel());

        eventOnDisplay = event;

        mBuilder.setView(newEventView);
        dialog = mBuilder.create();
        dialog.show();
    }


    public void displayEventReminder (View view){
        if(eventOnDisplay != null){
            boolean reminderFound = false;
            String eventId = eventOnDisplay.getId();
            for(Reminder foundReminder : reminders){
                if(foundReminder.getReminderFor().equals(eventId)){
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


    public void addEventReminder(View view){
        Event event = eventOnDisplay;
        dismissView(view);

        HashMap<String, String> newData = new HashMap<>();

        newData.put("topic", event.getTitle());
        newData.put("targetMoment", convertDateFormat(event.getStartDate()));
        newData.put("reminderFor", event.getId());

        createEntity("reminders", newData);
        eventOnDisplay = null;
    }


    public void addTaskReminder(View view){
        Task task = taskOnDisplay;
        dismissView(view);

        HashMap<String, String> newData = new HashMap<>();

        newData.put("topic", task.getTaskTitle());
        newData.put("targetMoment", convertDateFormat(task.getDeadline()));
        newData.put("reminderFor", task.getId());

        createEntity("reminders", newData);
        taskOnDisplay = null;
    }


    public void patchTask (android.view.View view){
        boolean dataExists = false;

        String taskTitle = null;
        if(editTaskTitleCrt.getText() != null){
            taskTitle = editTaskTitleCrt.getText().toString();
            dataExists = true;
        }

        String taskDesc = null;
        if(editTaskDescrCrt.getText() != null){
            taskDesc = editTaskDescrCrt.getText().toString();
            dataExists = true;
        }

        String taskDeadline = null;
        if(editTaskStartCrt.getText() != null){
            taskDeadline = editTaskStartCrt.getText().toString();
            dataExists = true;
        }

        String taskImport = null;
        if(editTaskImportCrt.getText() != null){
            taskImport = editTaskImportCrt.getText().toString();
            dataExists = true;
        }

        if(dataExists){
            HashMap<String, String> newData = new HashMap<>();
            if(taskTitle != null)
                newData.put("taskTitle", taskTitle);

            if(taskDesc != null)
                newData.put("taskDescription", taskDesc);

            if(taskDeadline != null)
                newData.put("deadline", taskDeadline);

            if(taskImport != null)
                newData.put("importanceLevel", taskImport);

            patchEntity("tasks", taskOnDisplay.getId(), newData);

            taskOnDisplay = null;
        } else {
            displayError("There is Input error");
        }
    }


    public void putTask (android.view.View view){
        boolean dataExists = false;
        boolean shouldContinue = true;


        String taskTitle = null;
        if(editTaskTitleCrt.getText() != null){
            taskTitle = editTaskTitleCrt.getText().toString();
            dataExists = true;
        }

        String taskDesc = null;
        if(editTaskDescrCrt.getText() != null){
            taskDesc = editTaskDescrCrt.getText().toString();
            dataExists = true;
        }

        String taskDeadline = null;
        if(editTaskStartCrt.getText() != null){
            taskDeadline = editTaskStartCrt.getText().toString();
            dataExists = true;
            editTaskStartCrt.setError(null);
        } else {
            shouldContinue = false;
            editTaskStartCrt.setError("Should have a date");
        }

        String taskImport = null;
        if(editTaskImportCrt.getText() != null){
            taskImport = editTaskImportCrt.getText().toString();
            dataExists = true;
        }


        if(dataExists && shouldContinue){
            HashMap<String, String> newData = new HashMap<>();
            if(taskTitle != null)
                newData.put("taskTitle", taskTitle);
            else
                newData.put("taskTitle", "");

            if(taskDesc != null)
                newData.put("taskDescription", taskDesc);
            else
                newData.put("taskDescription", "");

            newData.put("deadline", taskDeadline);

            if(taskImport != null)
                newData.put("importanceLevel", taskImport);
            else
                newData.put("importanceLevel", "");

            putEntity("tasks", taskOnDisplay.getId(), newData);

            taskOnDisplay = null;
        } else {
            displayError("There is Input error");
        }
    }


    public void deleteTask (View view){
        deleteAnEntity("tasks", taskOnDisplay.getId());
        tasks.remove(taskOnDisplay);
    }


    public void dismissView (View view){
        dialog.cancel();
        dialog.dismiss();
    }


    private void displayReminder(Reminder reminder) {

        //Toast.makeText(getApplicationContext(),"Display is under progress", Toast.LENGTH_LONG).show();

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newReminderiew = getLayoutInflater().inflate(R.layout.display_reminder_main,null);
        mBuilder.setCancelable(true);

        TextView id2 = (TextView) newReminderiew.findViewById(R.id.remindID);
        String text = "Reminder id: " + reminder.getId();
        id2.setText(text);

        TextInputEditText editRemindTitle = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindTitle);
        if(!reminder.getTopic().equals(""))
            editRemindTitle.setHint(reminder.getTopic());

        TextInputEditText editRemindMoment = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMoment);
        if(reminder.getTargetMoment() != null)
            editRemindMoment.setHint(convertDateFormat(reminder.getTargetMoment()));

        TextInputEditText editRemindMinutes = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMinutes);
        if(reminder.getRemindBefore() != null)
            editRemindMinutes.setHint(reminder.getRemindBefore());

        TextInputEditText editRemindImport = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindImport);
        if(reminder.getImportanceLevel() != null)
            editRemindImport.setHint(reminder.getImportanceLevel());

        TextView parent = (TextView) newReminderiew.findViewById(R.id.reminderFor);
        String text1 = reminder.getReminderFor();
        String text2 = "Parent id: ";
        if(text1 != null)
            text2 = text2 + text1;
        else
            text2 = text2 + "              ";

        parent.setText(text2);

        mBuilder.setView(newReminderiew);
        dialog = mBuilder.create();
        dialog.show();

    }


    public void patchReminder (android.view.View view){
        boolean dataExists = false;

        String remTitle = null;
        if(editRemindTitleCrt.getText() != null){
            remTitle = editRemindTitleCrt.getText().toString();
            dataExists = true;
        }

        String remMoment = null;
        if(editRemindMomentCrt.getText() != null){
            remMoment = editRemindMomentCrt.getText().toString();
            dataExists = true;
        }

        String remMinute = null;
        if(editRemindMinutesCrt.getText() != null){
            remMinute = editRemindMinutesCrt.getText().toString();
            dataExists = true;
        }

        String remImport = null;
        if(editRemindImportCrt.getText() != null){
            remImport = editRemindImportCrt.getText().toString();
            dataExists = true;
        }

        String remParent = null;
        if(editTReminderFor.getText() != null){
            remParent = editTReminderFor.getText().toString();
            dataExists = true;
        }

        if(dataExists){
            HashMap<String, String> newData = new HashMap<>();
            if(remTitle != null)
                newData.put("topic", remTitle);

            if(remMoment != null)
                newData.put("targetMoment", remMoment);

            if(remMinute != null)
                newData.put("remindBefore", remMinute);

            if(remImport != null)
                newData.put("importanceLevel", remImport);

            if(remParent != null)
                newData.put("reminderFor", remParent);

            patchEntity("reminders", reminderOnDisplay.getId(), newData);

            reminderOnDisplay = null;
        } else {
            displayError("There is no new Input to Update");
        }
    }

    public void putReminder (android.view.View view){
        boolean dataExists = false;
        boolean shouldContinue = true;

        String remTitle = null;
        if(editRemindTitleCrt.getText() != null){
            remTitle = editRemindTitleCrt.getText().toString();
            dataExists = true;
        }

        String remMoment = null;
        if(editRemindMomentCrt.getText() != null){
            remMoment = editRemindMomentCrt.getText().toString();
            dataExists = true;
            editRemindMomentCrt.setError(null);
        } else {
            shouldContinue = false;
            editRemindMomentCrt.setError("Should have a date");
        }

        String remMinute = null;
        if(editRemindMinutesCrt.getText() != null){
            remMinute = editRemindMinutesCrt.getText().toString();
            dataExists = true;
        }

        String remImport = null;
        if(editRemindImportCrt.getText() != null){
            remImport = editRemindImportCrt.getText().toString();
            dataExists = true;
        }

        String remParent = null;
        if(editTReminderFor.getText() != null){
            remParent = editTReminderFor.getText().toString();
            dataExists = true;
        }

        if(dataExists && shouldContinue){
            HashMap<String, String> newData = new HashMap<>();
            if(remTitle != null)
                newData.put("topic", remTitle);
            else
                newData.put("topic", "");

            newData.put("targetMoment", remMoment);

            if(remMinute != null)
                newData.put("remindBefore", remMinute);
            else
                newData.put("remindBefore", "");

            if(remImport != null)
                newData.put("importanceLevel", remImport);
            else
                newData.put("importanceLevel", "");

            if(remParent != null)
                newData.put("reminderFor", remParent);
            else
                newData.put("reminderFor", "");

            putEntity("reminders", reminderOnDisplay.getId(), newData);

            reminderOnDisplay = null;
        } else {
            displayError("There is Input error");
        }
    }


    public void deleteReminder (View view){
        deleteAnEntity("reminders", reminderOnDisplay.getId());
        reminders.remove(reminderOnDisplay);
    }


    private String convertDateFormat(Date date){
        String input = date.toString();
        String year = input.substring(30);
        String dateTxt = input.substring(4,11);
        return dateTxt + year;
    }
}
