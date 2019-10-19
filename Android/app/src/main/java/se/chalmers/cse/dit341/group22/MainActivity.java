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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import se.chalmers.cse.dit341.group22.model.Calendar;
import se.chalmers.cse.dit341.group22.model.Event;
import se.chalmers.cse.dit341.group22.model.Note;
import se.chalmers.cse.dit341.group22.model.Reminder;
import se.chalmers.cse.dit341.group22.model.Task;


public class MainActivity extends AppCompatActivity {

    // Field for parameter name
    public static final String HTTP_PARAM = "httpResponse";

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
                //getEntity("events");

                if(events.size() > 0){
                    //System.out.println("stored for scroll is " + sharedPreferences.getString("reminders", ""));
                    Intent i = new Intent(MainActivity.this, ScrollingActivity.class);
                    i.putExtra("Entity", "events");
                    startActivity(i);
                } else if(connectionExists){
                    displayError("There are no Events to Display");
                } else {
                    displayMsg("Fetching data...");
                    getData();
                }
            }
        });

        displayTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mEdit.putString("tasks", "");
                //mEdit.apply();
                //getEntity("tasks");

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
                //getEntity("reminders");

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
                newEvent.put("startDate", (new Date()).toString().substring(0,11));
                newEvent.put("endDate", (new Date()).toString().substring(0,11));

                createEntity("events", newEvent);

                break;
            case R.id.action_tasks:
                HashMap<String, String> newTask = new HashMap<>();
                newTask.put("taskTitle", "To be assigned");
                newTask.put("deadline", (new Date()).toString().substring(0,11));

                createEntity("tasks", newTask);

                break;
            case R.id.action_reminders:
                HashMap<String, String> newReminder = new HashMap<>();
                newReminder.put("topic", "To be given");
                newReminder.put("targetMoment", (new Date()).toString().substring(0,11));

                createEntity("reminders", newReminder);

                break;
            default:    //
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //open
    private void openAddEventActivity(Event event) {
        // Make an intent to start next activity.
        //Intent i = new Intent(ScrollingActivity.this, DisplayAddDate.class);
        //startActivity(i);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.create_event,null);
        mBuilder.setCancelable(true);


        TextView id = (TextView) newEventView.findViewById(R.id.entityIDCrt);
        String text = "Event id: " + event.getId();
        id.setText(text);

        TextInputEditText editEventTitleCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventTitleCrt);
        if(!event.getTitle().equals(""))
            editEventTitleCrt.setHint(event.getTitle());

        TextInputEditText editEventDescrCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventDescrCrt);
        if(event.getDescription() != null)
            editEventDescrCrt.setHint(event.getDescription());

        TextInputEditText editEventStartCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventStartCrt);
        if(event.getStartDate() != null)
            editEventStartCrt.setHint(event.getStartDate().toString().substring(0,11));

        TextInputEditText editEventFinishCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventFinishCrt);
        if(event.getEndDate() != null)
            editEventFinishCrt.setHint(event.getEndDate().toString().substring(0,11));

        TextInputEditText editEventImportCrt = (TextInputEditText) newEventView.findViewById(R.id.editEventImportCrt);
        if(event.getImportanceLevel() != null)
            editEventImportCrt.setHint(event.getImportanceLevel());

        mBuilder.setView(newEventView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    //open
    private void openAddTaskActivity(Task task) {
        // Make an intent to start next activity.
        //Intent i = new Intent(ScrollingActivity.this, DisplayAddDate.class);
        //startActivity(i);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newTaskView = getLayoutInflater().inflate(R.layout.create_task,null);
        mBuilder.setCancelable(true);


        TextView id = (TextView) newTaskView.findViewById(R.id.taskIDCrt);
        String text = "Task id: " + task.getId();
        id.setText(text);

        TextInputEditText editTaskTitleCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskTitleCrt);
        if(!task.getTaskTitle().equals(""))
            editTaskTitleCrt.setHint(task.getTaskTitle());

        TextInputEditText editTaskDescrCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskDescrCrt);
        if(task.getTaskDescription() != null)
            editTaskDescrCrt.setHint(task.getTaskDescription());

        TextInputEditText editTaskStartCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskStartCrt);
        if(task.getDeadline() != null)
            editTaskStartCrt.setHint(task.getDeadline().toString().substring(0,11));

        TextInputEditText editTaskImportCrt = (TextInputEditText) newTaskView.findViewById(R.id.editTaskImportCrt);
        if(task.getImportanceLevel() != null)
            editTaskImportCrt.setHint(task.getImportanceLevel());

        mBuilder.setView(newTaskView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    private void openAddReminderActivity(Reminder reminder) {

        //Toast.makeText(getApplicationContext(),"Display is under progress", Toast.LENGTH_LONG).show();

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newReminderView = getLayoutInflater().inflate(R.layout.create_reminder,null);
        mBuilder.setCancelable(true);

        TextView id = (TextView) newReminderView.findViewById(R.id.remindIDCrt);
        String text = "Reminder id: " + reminder.getId();
        id.setText(text);

        TextInputEditText editRemindTitleCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindTitleCrt);
        if(!reminder.getTopic().equals(""))
            editRemindTitleCrt.setHint(reminder.getTopic());

        TextInputEditText editRemindMomentCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindMomentCrt);
        if(reminder.getTargetMoment() != null)
            editRemindMomentCrt.setHint(reminder.getTargetMoment().toString().substring(0,11));

        TextInputEditText editRemindMinutesCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindMinutesCrt);
        if(reminder.getRemindBefore() != null)
            editRemindMinutesCrt.setHint(reminder.getRemindBefore());

        TextInputEditText editRemindImportCrt = (TextInputEditText) newReminderView.findViewById(R.id.editRemindImportCrt);
        if(reminder.getImportanceLevel() != null)
            editRemindImportCrt.setHint(reminder.getImportanceLevel());

        TextInputEditText editTReminderFor = (TextInputEditText) newReminderView.findViewById(R.id.editTReminderFor);
        if(reminder.getReminderFor() != null)
            editTReminderFor.setHint(reminder.getReminderFor());

        mBuilder.setView(newReminderView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    /*
    public void onClickNewActivity (View view) {
        TextView mEventView = findViewById(R.id.dashboardText);

        // Starts a new activity, providing the text from my HTTP text field as an input
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(HTTP_PARAM, mEventView.getText().toString());
        startActivity(intent);
    }

     */

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
                                            Event foundEvents = gson.fromJson(dataArray, Event.class);

                                            if(foundEvents.getId() != null){
                                                success = true;
                                                eventResult = foundEvents;
                                                openAddEventActivity(foundEvents);
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
                                            openAddTaskActivity(foundTasks);
                                            //displayMsg("Task created successfully");
                                        } else {
                                            displayMsg("Task is not created");
                                        }
                                        break;


                                    default:
                                        Reminder foundReminders = gson.fromJson(dataArray, Reminder.class);

                                        if(foundReminders.getId() != null){
                                            success = true;
                                            reminderResult = foundReminders;
                                            openAddReminderActivity(foundReminders);
                                            //displayMsg("Reminder created successfully");
                                        } else {
                                            displayMsg("Reminder is not created");
                                        }
                                        break;
                                }

                                if(success){
                                    getEntity(entityName);
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

    public void patchEntity(String entityName, String objectID, Object reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName + "/" + objectID;

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PATCH, url, null, new Response.Listener<JSONObject>() {

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
                                    case "tasks":
                                        Task[] foundTasks = gson.fromJson(dataArray, Task[].class);

                                        if(foundTasks[0].getId() != null){
                                            success = true;
                                        }
                                        break;

                                    case "events":
                                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                        if(foundEvents[0].getId() != null){
                                            success = true;
                                        }
                                        break;

                                    default:
                                        Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                        if(foundReminders[0].getId()!= null){
                                            success = true;
                                        }
                                        break;
                                }

                                if(success){
                                    getEntity(entityName);
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


    public void putEntity(String entityName, String objectID, Object reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName + "/" + objectID;

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {

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
                                    case "tasks":
                                        Task[] foundTasks = gson.fromJson(dataArray, Task[].class);

                                        if(foundTasks[0].getId() != null){
                                            success = true;
                                        }
                                        break;

                                    case "events":
                                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                        if(foundEvents[0].getId() != null){
                                            success = true;
                                        }
                                        break;

                                    default:
                                        Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                        if(foundReminders[0].getId() != null){
                                            success = true;
                                        }
                                        break;
                                }

                                if(success){
                                    getEntity(entityName);
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

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){

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
                                getEntity(entityName);
                            } else {
                                displayError(objectID + " is not deleted successfully");
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


    //open the displayAddDate activity without any message
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
            editStart.setHint(event.getStartDate().toString().substring(0,11));

        editFinish = (TextInputEditText) newEventView.findViewById(R.id.editEventFinish);
        if(event.getEndDate() != null)
            editFinish.setHint(event.getEndDate().toString().substring(0,11));

        editImport = (TextInputEditText) newEventView.findViewById(R.id.editEventImport);
        if(event.getImportanceLevel() != null)
            editImport.setHint(event.getImportanceLevel());

        mBuilder.setView(newEventView);
        final AlertDialog dialog = mBuilder.create();
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

    public void putEvent (android.view.View view){

    }

    public void patchEvent (View view){

    }

    public void dismissView (View view){
        finish();
        //Intent i = new Intent(view.getContext(), ScrollingActivity.class);
        //startActivity(i);
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
            editRemindMoment.setHint(reminder.getTargetMoment().toString().substring(0,11));

        TextInputEditText editRemindMinutes = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMinutes);
        if(reminder.getRemindBefore() != null)
            editRemindMinutes.setHint(reminder.getRemindBefore());

        TextInputEditText editRemindImport = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindImport);
        if(reminder.getImportanceLevel() != null)
            editRemindImport.setHint(reminder.getImportanceLevel());

        TextView parent = (TextView) newReminderiew.findViewById(R.id.reminderFor);
        String text2 = "Parent id: " + reminder.getReminderFor();
        parent.setText(text2);

        mBuilder.setView(newReminderiew);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

    }


   /* //after saving button
    public void patchEvent(android.view.View v){
        if(!validateDate() | !validateStart() | !validateFinish() | !validateImportance()){
            return;
        }

        if(editTitle.)
        String newTitle = editTitle;
        editDescr;
        editStart;
        editFinish;
        editImport;

        String forward = date + " " + approvedSt + " " + approvedFn + " ";

        finish();
        Intent i = new Intent(v.getContext(), ScrollingActivity.class);
        i.putExtra("Reply", forward);
        startActivity(i);
    }*/


    /*//YearMonth yearMonth = YearMonth.of(year,month);
    public boolean validateDate(){

        String inputText = editStart.getEditableText().toString().trim();

        if(inputText.isEmpty()){
            editStart.setError("Field is empty");
            return false;
        } else if (!inputText.matches(DATE_REGEX)){
            editDate.setError("Use valid format YYYY-MM-DD");
            return false;
        } else if (!dateExists(inputText)){
            editDate.setError("Invalid date");
            return false;
        } else {
            editDate.setError(null);
            date = inputText;
            return true;
        }
    }*/
}
