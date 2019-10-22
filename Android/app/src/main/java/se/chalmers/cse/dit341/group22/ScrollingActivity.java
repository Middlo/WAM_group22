package se.chalmers.cse.dit341.group22;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import se.chalmers.cse.dit341.group22.model.Event;
import se.chalmers.cse.dit341.group22.model.Reminder;
import se.chalmers.cse.dit341.group22.model.Task;

public class ScrollingActivity extends AppCompatActivity {

    private ArrayList<Event> events;
    private ArrayList<Task> tasks;
    private ArrayList<Reminder> reminders;

    private AlertDialog dialog;

    private Event eventOnDisplay = null;
    private Task taskOnDisplay = null;
    private Reminder reminderOnDisplay = null;

    private Event pendingEvent = null;
    private Task pendingTask = null;
    private Reminder pendingReminder = null;

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
    public static final String DATE_REGEX = "((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])";
    public static final String NUM_REGEX = "^\\d+$";

    private final int APPLY = 0;    //for saving data
    private final int COMMIT = 1;

    private SharedPreferences.Editor mEdit;
    private String receivedData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_AppCompat_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);    //this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
                        list.add("Event " + event.getTitle() + ": on " + convertDateFormat(event.getStartDate()));
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
                        list.add("Reminder " + reminder.getTopic() + ": on " + convertDateFormat(reminder.getTargetMoment()));
                    }
                }
                break;
        }

        String Month_REGEX = "([12]\\d{3}-(0[1-9]|1[0-2]))";    //month REGEX

        if(list.size() > 0){
            //display all objects as a list
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(myListClickListener);
        } else {
            displayMsg("Entity has no elements");
            finish();
        }


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
                if(foundReminder.getReminderFor() != null && foundReminder.getReminderFor().equals(eventId)){
                    reminderFound = true;
                    dismissView(view);
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
        if(eventHasReminder(eventOnDisplay.getId())){
            displayMsg("Event has already a Reminder");
        } else {
            dismissView(view);

            HashMap<String, String> newData = new HashMap<>();

            newData.put("topic", event.getTitle());
            newData.put("targetMoment", convertDateFormat(event.getStartDate()));
            newData.put("reminderFor", event.getId());

            createEntity("reminders", newData);
            eventOnDisplay = null;
        }
    }

    public boolean eventHasReminder(String evtID){
        for(Reminder rem : reminders){
            String id = rem.getReminderFor();
            if(id != null && id.equals(evtID))
                return true;
        }
        return false;
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
            String inputText = editStart.getEditableText().toString().trim();

            if (!patchCorrection(inputText)){
                editStart.setError("Update input");
                return;
            }

            editStart.setError(null);
            eventStart = editStart.getText().toString();
            dataExists = true;

        }

        String eventFinish = null;
        if(editFinish.getText() != null){
            String inputText = editFinish.getEditableText().toString().trim();

            if (!patchCorrection(inputText)){
                editFinish.setError("Update input");
                return;
            }
            editFinish.setError(null);
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

    public void putEvent (android.view.View view){
        boolean dataExists = false;
        boolean shouldContinue = true;
        boolean correctionNeeded = true;

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

            String inputText = editStart.getEditableText().toString().trim();

            if (!putCorrection(inputText)){
                editStart.setError("Update input");
                return;
            }

            editStart.setError(null);
            eventStart = editStart.getText().toString();
            dataExists = true;
        } else {
            shouldContinue = false;
            editStart.setError("Should have a date");
        }

        String eventFinish = null;
        if(editFinish.getText() != null){

            String inputText = editFinish.getEditableText().toString().trim();

            if (!putCorrection(inputText)){
                editFinish.setError("Update input");
                return;
            }
            eventFinish = editFinish.getText().toString();
            dataExists = true;
            editFinish.setError(null);
        } else {
            shouldContinue = false;
            editFinish.setError("Should have a date");
        }

        String eventImport = null;
        if(editImport.getText() != null){
            eventImport = editImport.getText().toString();
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

    public void dismissView (View view){
        dialog.cancel();
        dialog.dismiss();
    }


    public void deleteEvent (View view){
        deleteAnEntity("events", eventOnDisplay.getId());
        events.remove(eventOnDisplay);
        finish();
    }


    private void displayTask(Task task) {

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
            editTaskDeadline.setHint(convertDateFormat(task.getDeadline()));

        editTaskImport = (TextInputEditText) newTaskView.findViewById(R.id.editTaskImport);
        if(task.getImportanceLevel() != null)
            editTaskImport.setHint(task.getImportanceLevel());

        taskOnDisplay = task;

        mBuilder.setView(newTaskView);
        dialog = mBuilder.create();
        dialog.show();

    }


    public void displayTaskReminder (View view){
        if(taskOnDisplay != null){
            boolean reminderFound = false;
            String taskId = taskOnDisplay.getId();
            //System.out.println("event id: " + eventId);
            for(Reminder foundReminder : reminders){
                //System.out.println("parent reminder : " + foundReminder.getReminderFor());
                if(foundReminder.getReminderFor() != null && foundReminder.getReminderFor().equals(taskId)){
                    reminderFound = true;
                    dismissView(view);
                    displayReminder(foundReminder);
                }
            }
            if(!reminderFound){
                displayMsg("Task has no reminder");
            }
        } else {
            displayMsg("Task id is not yet identified ?");
        }
    }

    public void addTaskReminder(View view){
        Task task = taskOnDisplay;
        if(taskHasReminder(taskOnDisplay.getId())){
            displayMsg("Task has a Reminder");
        } else {
            dismissView(view);

            HashMap<String, String> newData = new HashMap<>();

            newData.put("topic", task.getTaskTitle());
            newData.put("targetMoment", convertDateFormat(task.getDeadline()));
            newData.put("reminderFor", task.getId());

            createEntity("reminders", newData);
            taskOnDisplay = null;
        }
    }

    public boolean taskHasReminder(String tskID){
        for(Reminder rem : reminders){
            String id = rem.getReminderFor();
            if(id != null && id.equals(tskID))
                return true;
        }
        return false;
    }

    public void patchTask (android.view.View view){
        boolean dataExists = false;

        String taskTitle = null;
        if(editTaskTitle.getText() != null){
            taskTitle = editTaskTitle.getText().toString();
            dataExists = true;
        }

        String taskDesc = null;
        if(editTaskDescr.getText() != null){
            taskDesc = editTaskDescr.getText().toString();
            dataExists = true;
        }

        String taskDeadline = null;
        if(editTaskDeadline.getText() != null){
            String inputText = editTaskDeadline.getEditableText().toString().trim();

            if (!patchCorrection(inputText)){
                editTaskDeadline.setError("Update input");
                return;
            }

            editTaskDeadline.setError(null);
            taskDeadline = editTaskDeadline.getText().toString();
            dataExists = true;
        }

        String taskImport = null;
        if(editTaskImport.getText() != null){
            taskImport = editTaskImport.getText().toString();
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
            displayError("There is no new Input to Update");
        }
    }

    public void putTask (android.view.View view){
        boolean dataExists = false;
        boolean shouldContinue = true;


        String taskTitle = null;
        if(editTaskTitle.getText() != null){
            taskTitle = editTaskTitle.getText().toString();
            dataExists = true;
        }

        String taskDesc = null;
        if(editTaskDescr.getText() != null){
            taskDesc = editTaskDescr.getText().toString();
            dataExists = true;
        }

        String taskDeadline = null;
        if(editTaskDeadline.getText() != null){
            String inputText = editTaskDeadline.getEditableText().toString().trim();

            if (!putCorrection(inputText)){
                editTaskDeadline.setError("Update input");
                return;
            }

            taskDeadline = editTaskDeadline.getText().toString();
            dataExists = true;
            editTaskDeadline.setError(null);
        } else {
            shouldContinue = false;
            editTaskDeadline.setError("Should have a date");
        }

        String taskImport = null;
        if(editTaskImport.getText() != null){
            taskImport = editTaskImport.getText().toString();
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
        finish();
    }


    private void displayReminder(Reminder reminder) {

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
            editRemindMoment.setHint(convertDateFormat(reminder.getTargetMoment()));

        editRemindMinutes = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindMinutes);
        if(reminder.getRemindBefore() != null)
            editRemindMinutes.setHint(reminder.getRemindBefore());

        editRemindImport = (TextInputEditText) newReminderiew.findViewById(R.id.editRemindImport);
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

        reminderOnDisplay = reminder;

        mBuilder.setView(newReminderiew);
        dialog = mBuilder.create();
        dialog.show();
    }


    public void patchReminder (android.view.View view){
        boolean dataExists = false;

        String remTitle = null;
        if(editRemindTitle.getText() != null){
            remTitle = editRemindTitle.getText().toString();
            dataExists = true;
        }

        String remMoment = null;
        if(editRemindMoment.getText() != null){
            String inputText = editRemindMoment.getEditableText().toString().trim();

            if (!patchCorrection(inputText)){
                editRemindMoment.setError("Update input");
                return;
            }

            editRemindMoment.setError(null);
            remMoment = editRemindMoment.getText().toString();
            dataExists = true;
        }

        String remMinute = null;
        if(editRemindMinutes.getText() != null){
            remMinute = editRemindMinutes.getText().toString();
            dataExists = true;
        }

        String remImport = null;
        if(editRemindImport.getText() != null){
            remImport = editRemindImport.getText().toString();
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
        if(editRemindTitle.getText() != null){
            remTitle = editRemindTitle.getText().toString();
            dataExists = true;
        }

        String remMoment = null;
        if(editRemindMoment.getText() != null){
            String inputText = editRemindMoment.getEditableText().toString().trim();

            if (!putCorrection(inputText)){
                editRemindMoment.setError("Update input");
                return;
            }

            remMoment = editRemindMoment.getText().toString();
            dataExists = true;
            editRemindMoment.setError(null);
        } else {
            shouldContinue = false;
            editRemindMoment.setError("Should have a date");
        }

        String remMinute = null;
        if(editRemindMinutes.getText() != null){
            remMinute = editRemindMinutes.getText().toString();
            dataExists = true;
        }

        String remImport = null;
        if(editRemindImport.getText() != null){
            remImport = editRemindImport.getText().toString();
            dataExists = true;
        }


        String remParent = null;
        if(reminderOnDisplay.getReminderFor() != null){
            remParent = reminderOnDisplay.getReminderFor();
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
        finish();
    }



    public void createEntity(String entityName, HashMap reqBody){

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
                                                getEntity(entityName);
                                                //openAddEventActivity(foundEvent);
                                                displayMsg("Event created successfully");
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
                                            getEntity(entityName);
                                            //openAddTaskActivity(foundTasks);
                                            displayMsg("Task created successfully");
                                        } else {
                                            displayMsg("Task is not created");
                                        }
                                        break;

                                    default:
                                        Reminder foundReminder = gson.fromJson(dataArray, Reminder.class);

                                        if(foundReminder.getId() != null){
                                            //displayMsg("new Rem is " + foundReminder.getId());
                                            success = true;
                                            reminders.add(foundReminder);
                                            getEntity(entityName);
                                            //openAddReminderActivity(foundReminder.getId());
                                            displayMsg("Reminder created successfully");
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

                                        if(foundReminders.getId() != null){
                                            success = true;
                                        }
                                        break;
                                }

                                if(success){
                                    displayMsg("Updated Successfully");
                                    getEntity(entityName);
                                    if(dialog!= null)
                                        dialog.dismiss();
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


    public void putEntity(String entityName, String objectID, HashMap reqBody){

        if(entityName.equals("tasks") || entityName.equals("events") || entityName.equals("reminders") ){
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
                                    if(dialog != null)
                                        dialog.dismiss();
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

    private String convertDateFormat(Date date){
        if(date!= null){
            String input = date.toString();
            String year = input.substring(30);
            String month = getMonth(input.substring(4,7));
            String dateTxt = input.substring(8,10);
            System.out.println(year + "-" + month + "-" + dateTxt);
            return year + "-" + month + "-" + dateTxt;
        } else {
            displayError("Correction needed on Entity with Empty Date");
            return "";
        }

    }

    private String getMonth(String input){
        switch (input){
            case "Jan":
                return "01";

            case "Feb":
                return "02";

            case "Mar":
                return "03";

            case "Apr":
                return "04";

            case "May":
                return "05";

            case "Jun":
                return "06";

            case "Jul":
                return "07";

            case "Aug":
                return "08";

            case "Sep":
                return "09";

            case "Oct":
                return "10";

            case "Nov":
                return "11";

            default:
                return "12";
        }
    }


    public boolean patchCorrection(String input){
        if(input.equals(""))
            return true;
        else if (!input.matches(DATE_REGEX))
            return false;
        else
            return dateExists(input);
    }

    public boolean putCorrection(String input){
        if(input.equals(""))
            return false;
        else if (!input.matches(DATE_REGEX))
            return false;
        else
            return dateExists(input);
    }


    public boolean dateExists (String dateInput){
        int yearNum = Integer.parseInt(dateInput.substring(0,dateInput.indexOf("-")));
        String newString = dateInput.substring((dateInput.indexOf("-")) + 1);
        int monthNum = Integer.parseInt(newString.substring(0,newString.indexOf("-")));
        int dateNum = Integer.parseInt(newString.substring((newString.indexOf("-")) + 1));

        int numberOfDays = YearMonth.of(yearNum, monthNum).lengthOfMonth();

        if(dateNum > numberOfDays)
            return false;
        else
            return true;
    }

}