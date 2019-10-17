package se.chalmers.cse.dit341.group22;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import se.chalmers.cse.dit341.group22.model.Calendar;
import se.chalmers.cse.dit341.group22.model.Event;
import se.chalmers.cse.dit341.group22.model.Note;
import se.chalmers.cse.dit341.group22.model.Reminder;
import se.chalmers.cse.dit341.group22.model.Task;


public class MainActivity extends AppCompatActivity {

    // Field for parameter name
    public static final String HTTP_PARAM = "httpResponse";

    private Calendar calendar;
    private ArrayList<Event> events;
    private ArrayList<Task> tasks;
    private ArrayList<Note> notes;
    private ArrayList<Reminder> reminders;
    private String toolBarDate;
    private String selectedDate;
    private String todaysDate = new Date().toString();


    Button displayEvents;


    public MainActivity(){
        events = new ArrayList <>();
        tasks = new ArrayList <>();
        notes = new ArrayList <>();
        reminders = new ArrayList <>();

        //getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init View

        System.out.println("Today is " + todaysDate);
        displayEvents = (Button) findViewById(R.id.displayEvents);


        getData();

        displayEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getData();
                //System.out.println("first event id is: " + events.get(0)._id);
                //displayEvent();
                //System.out.println("Tasks: " + tasks.size() );
                System.out.println("Events: " + events.size() );
                //System.out.println("Notes: " + notes.size() );
                //System.out.println("Reminders " + reminders.size());
                //displayEvent();
                //getText();
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
                            eventString.append("Event id: " + current._id + " with title "
                                    + current.title + " and start Date " + current.startDate + "\n");
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
        getEntity("tasks");
        getEntity("events");
        getEntity("notes");
        getEntity("reminders");
    }


    public void readEventsFromJson(String input)throws Exception {

        Gson gson = new Gson();

        events = gson.fromJson(input, new TypeToken<ArrayList<Event>>(){}.getType());

    }//Eyuell

    public void getEntity(String entityName){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){
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
                                if(!dataArray.equals("[]"))
                                    System.out.println("Response is: "+ dataArray);
                            } catch (JSONException e) {
                                Log.e(this.getClass().toString(), e.getMessage());
                            }

                            switch (entityName){
                                case "tasks":
                                    Task[] foundTasks = gson.fromJson(dataArray, Task[].class);

                                    //tasks = null;
                                    if(foundTasks != null && foundTasks[0]._id != null){
                                        tasks.addAll(Arrays.asList(foundTasks));
                                        System.out.println("found tasks are " + tasks.size());
                                    }
                                    break;

                                case "events":
                                    Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                    //events = null;
                                    if(foundEvents != null && foundEvents[0]._id != null){
                                        events.addAll(Arrays.asList(foundEvents));
                                        System.out.println("found events are " + events.size());
                                        //displayEvent(events.get(0));
                                        //System.out.println("first event id is: " + events.get(0)._id);
                                    }
                                    break;

                                case "notes":
                                    Note[] foundNotes = gson.fromJson(dataArray, Note[].class);

                                    //notes = null;
                                    if(foundNotes != null && foundNotes[0]._id != null){
                                        notes.addAll(Arrays.asList(foundNotes));
                                        if(notes.size() > 0)
                                            System.out.println("found notes are " + notes.size());
                                    }
                                    break;

                                default:
                                    Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                    //reminders = null;
                                    if(foundReminders != null && foundReminders[0]._id != null){
                                        reminders.addAll(Arrays.asList(foundReminders));
                                        System.out.println("found reminders are " + reminders.size());
                                    }
                                    break;
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            displayError("Error! " + error.toString());
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
    }

    public void setEntity(String entityName, Object reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("notes") || entityName.equals("reminders") ){
            if(reqBody != null){
                String url = getString(R.string.server_url) + "/api/" + entityName;

                // This uses Volley (Threading and a request queue is automatically handled in the background)
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

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

                                        if(foundTasks[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "events":
                                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                        if(foundEvents[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "notes":
                                        Note[] foundNotes = gson.fromJson(dataArray, Note[].class);

                                        if(foundNotes[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    default:
                                        Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                        if(foundReminders[0]._id != null){
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

                                        if(foundTasks[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "events":
                                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                        if(foundEvents[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "notes":
                                        Note[] foundNotes = gson.fromJson(dataArray, Note[].class);

                                        if(foundNotes[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    default:
                                        Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                        if(foundReminders[0]._id != null){
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

                                        if(foundTasks[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "events":
                                        Event[] foundEvents = gson.fromJson(dataArray, Event[].class);

                                        if(foundEvents[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    case "notes":
                                        Note[] foundNotes = gson.fromJson(dataArray, Note[].class);

                                        if(foundNotes[0]._id != null){
                                            success = true;
                                        }
                                        break;

                                    default:
                                        Reminder[] foundReminders = gson.fromJson(dataArray, Reminder[].class);

                                        if(foundReminders[0]._id != null){
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


    /*
    //open the displayAddDate activity without any message
    private void displayAllEvents(View view) {

        //Event event = events.get(0);
        // Make an intent to start next activity.
        //Intent i = new Intent(ScrollingActivity.this, DisplayAddDate.class);
        //startActivity(i);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View newEventView = getLayoutInflater().inflate(R.layout.content_scrolling,null);
        mBuilder.setCancelable(true);

        ArrayList list = new ArrayList();   //to add the objects to scrolling list
        for (Event ts : events) {
            list.add(ts.toString());
        }

        //display all objects as a list
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(myListClickListener);

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
    */

    /*
    //open the displayAddDate activity without any message
    private void displayEvent(Event event) {

        //Event event = events.get(0);
        // Make an intent to start next activity.
        //Intent i = new Intent(ScrollingActivity.this, DisplayAddDate.class);
        //startActivity(i);
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

    */


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
