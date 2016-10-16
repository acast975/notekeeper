package rs.elfak.diplomski.aleksa.notekeeper.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.model.DataMapper;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.Task;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;

/**
 * Created by aleks on 23.9.2016..
 */

public class HttpConnector {

    private Context context;
    private HttpConnector instance;

    static String url = "http://10.66.47.214:1337";

    public static void getUserByEmail(final String email, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/user/getByEmail/" + email, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                    callback.error("No user with email address " + email + " found");
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                        return;
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUserById(final int id, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/user/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                    callback.error("No user  found");
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                        return;
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void register(String username, String email, String password, final Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject();
            userJSON.put("username", username);
            userJSON.put("password", password);
            userJSON.put("email", email);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = userJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/user/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getNotes(String id, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/user/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getNotesFilter(String id, String filter, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/userFiltered/" + id + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getAssignedNotes(int id, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/user/assigned/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getAssignedNotesFiltered(int id, String filter, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/user/assignedFiltered/" + id + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void deleteNote(String id, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("id", id);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/note/deleteById", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void addNote(Note note, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("title", note.getTitle());
            noteJSON.put("time_created", note.getTimeCreated() != null ? note.getTimeCreated().toString().replace(' ', 'T').substring(0, note.getTimeCreated().toString().length() - 2) + "Z" : "null");
            noteJSON.put("user_id", note.getUserId());
            noteJSON.put("assigned_to", note.getAssignedTo());
            noteJSON.put("accepted", "false");
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/note/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.success(response.getInt("insertId"));
                } catch (JSONException exception) {
                    callback.error("Unexpected error occured");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void updateNoteTitle(int noteId, String newTitle, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("title", newTitle);
            noteJSON.put("id", noteId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/note/update/title", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void addTask(Task task, Context context, final TaskVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("text", task.getText());
            taskJSON.put("note_id", task.getNoteId());
            taskJSON.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : null);
            taskJSON.put("checked", task.isChecked() ? 1 : 0);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/task/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.success(response.getInt("insertId"));
                } catch (JSONException exception) {
                    callback.error("Unexpected error occured");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void updateTaskText(int taskId, String newText, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("text", newText);
            taskJSON.put("id", taskId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/task/update/text", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void checkTask(int taskId, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("id", taskId);
            taskJSON.put("sender", (new Session(context).getId()));
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/task/check", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void uncheckTask(int taskId, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("id", taskId);
            taskJSON.put("sender", (new Session(context).getId()));
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/task/uncheck", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void deleteTask(int id, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("id", id);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/task/delete", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getNote(int id, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getTasks(int noteId, Context context, final TaskVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/task/" + noteId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Task> tasks = new ArrayList<Task>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        tasks.add(DataMapper.taskFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(tasks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getTask(int taskId, Context context, final TaskVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/task/byId/" + taskId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Task> tasks = new ArrayList<Task>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        tasks.add(DataMapper.taskFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(tasks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getFriends(int userId, Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/friendship/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getFriendsFiltered(int userId, String filter, Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/friendship/filtered/" + userId + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUsers(int userId, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/user/all/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }

                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUsersFiltered(int userId, String filter, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/user/all/filtered/" + userId + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }

                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getPendingRequests(int userId, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/friendship/pending/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }

                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getPendingRequestsFiltered(int userId, String filter, final Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/friendship/pending/filtered/" + userId + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }

                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void partialFriendship(int friendId1, int friendId2, boolean confirm, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject();
            userJSON.put("friend_1", friendId1);
            userJSON.put("friend_2", friendId2);
            if (confirm) {
                String date = new Timestamp(System.currentTimeMillis()).toString().replace(' ', 'T');
                //date = date.substring(0, date.length() - 2) + "Z";
                userJSON.put("date_created", date);
            }
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = userJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/friendship/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void deleteFriendship(int friendId1, int friendId2, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject();
            userJSON.put("friend_1", friendId1);
            userJSON.put("friend_2", friendId2);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = userJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/friendship/delete", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUsersForShare(int noteId, int userId, Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/share/" + userId + "/" + noteId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getSharedWithUsers(int noteId, int userId, Context context, final UserVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/shared/" + userId + "/" + noteId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<User> users = new ArrayList<User>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(DataMapper.userFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void startSharingNote(int senderId, int noteId, int userId, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject();
            userJSON.put("sender_id", senderId);
            userJSON.put("note_id", noteId);
            userJSON.put("user_id", userId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = userJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/shared_note", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void stopSharingNote(int senderId, int noteId, int userId, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject();
            userJSON.put("sender_id", senderId);
            userJSON.put("note_id", noteId);
            userJSON.put("user_id", userId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = userJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/shared_note/stop", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getSharedNote(int userId, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/shared/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getSharedNoteFiltered(int userId, String filter, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/sharedFiltered/" + userId + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getReceivedNote(int userId, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/user/received/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getReceivedNoteFiltered(int userId, String filter, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/user/receivedFiltered/" + userId + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void cancelReceivedNote(int noteId, int userId, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("noteid", noteId);
            noteJSON.put("userid", userId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/note/cancelReceived", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getSharedNotesWithFriend(int userId, int friendId, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/sharedWithFriend/" + userId + "/" + friendId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getReceivedNotesFromFriend(int userId, int friendId, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/receivedFromFriend/" + userId + "/" + friendId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getAssignedNotesToFriend(int userId, int friendId, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/assignedToFriend/" + userId + "/" + friendId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void setTaskDeadline(int taskId, Timestamp timestamp, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject taskJSON = null;
        try {
            taskJSON = new JSONObject();
            taskJSON.put("deadline", timestamp.toString().replace(' ', 'T'));
            taskJSON.put("id", taskId);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = taskJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/task/deadline", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void sendToken(String token, int id, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("token", token);
            noteJSON.put("id", id);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/api/user/addToken", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occured");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getPendingNotes(int id, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/pending/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void getPendingNotesFiltered(int id, String filter, Context context, final NoteVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/note/pendingFiltered/" + id + "/" + filter, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Note> notes = new ArrayList<Note>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        notes.add(DataMapper.noteFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(notes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void refuseNote(int senderId, int id, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("sender_id", senderId);
            noteJSON.put("id", id);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/note/refuse", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void acceptNote(int senderId, int id, Context context, final CommonVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject noteJSON = null;
        try {
            noteJSON = new JSONObject();
            noteJSON.put("sender_id", senderId);
            noteJSON.put("id", id);
        } catch (JSONException exc) {
            callback.error("Unexpected error occured");
            return;
        }

        final String requestBody = noteJSON.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url + "/api/note/accept", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes();
                } catch (Exception exception) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    callback.error("Unexpected error occurred");
                    return null;
                }
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUpcomingTasks(int userId, Context context, final TaskVolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url + "/api/task/upcoming/" + userId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Task> tasks = new ArrayList<Task>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        tasks.add(DataMapper.taskFromJSON(response.getJSONObject(i)));
                    } catch (JSONException exception) {
                        callback.error("Unexpected error occured");
                    }
                }
                callback.success(tasks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String decoded = null;
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null)
                        decoded = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    callback.error("Unexpected error occurred");
                }
                callback.error(decoded);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}