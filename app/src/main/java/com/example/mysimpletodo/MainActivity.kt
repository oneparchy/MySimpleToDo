package com.example.mysimpletodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()   // Create the list as a variable since we will be assigning it from file
    lateinit var adapter: TaskItemAdapter       // Create the adapter as a variable, add lateinit since we are initializing it later in the code

    // onCreate specifies what to do when the app is launched
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a long click listener for when the user long presses to delete an entry
        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                // 1. Remove item from list
                listOfTasks.removeAt(position)
                // 2. Notify adapter of the change
                adapter.notifyDataSetChanged()
                // 3. Save the list
                saveItems()
            }
        }

        loadItems()     // Load the list of tasks from data file
        val inputTextField = findViewById<EditText>(R.id.addTaskField)  // Map 'inputTextField' to our plain text field created in the layout

        // Format the list as a recycler view
        // 1. Look up recyclerView in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // 2. Assign adapter, passing in the user data & long click listener
        adapter = TaskItemAdapter(listOfTasks, onLongClickListener)
        // 3. Attach adapter to recyclerView
        recyclerView.adapter = adapter
        // 4. Set layout manager to position items
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the button and input field so the user can enter tasks
        findViewById<Button>(R.id.addButton).setOnClickListener {
            // 1. Grab text that user has entered into addTaskField
            val userInputtedTask = inputTextField.text.toString()
            // 2. Add the string to our task list, listOfTasks
            listOfTasks.add(userInputtedTask)
            // 3. Notify adapter that the Recycler data has been updated.
            adapter.notifyItemInserted(listOfTasks.size - 1)
            // 4. Reset addTaskField for new user input
            inputTextField.setText("")
            // 5. Save the list
            saveItems()
        }
    }

    // Save the list data that has been inputted by writing & reading to/from file

    // Create a method to get the file we need
    private fun getDataFile() : File {
        // Every line will represent a task
        return File(filesDir, "data.txt")
    }

    // Load list from the data file, use Try/Catch blocks in case of IO error (file not found, read/write permission denied, etc.)
    private fun loadItems() {
        try {
            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // save list to the data file, use Try/Catch blocks in case of IO error (file not found, read/write permission denied, etc.)
    fun saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listOfTasks)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}