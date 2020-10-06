package com.smeet.todolist

import Model.Data
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var fabbtn: FloatingActionButton? = null

    //Firebase
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var recyclerView: RecyclerView? = null
    private var post_key: String? = null
    private var name: String? = null
    private var description: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "To Do List"
        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth!!.currentUser
        val uid = mUser!!.uid
        mDatabase = FirebaseDatabase.getInstance().reference.child("All Data").child(uid)

        //Recycler View..
        recyclerView = findViewById(R.id.recyclerid)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(layoutManager)
        fabbtn = findViewById(R.id.fabadd)
        fabbtn.setOnClickListener(View.OnClickListener { AddData() })
    }

    private fun AddData() {
        val mydialog = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val myview = inflater.inflate(R.layout.inputlayout, null)

//        recyclerView = findViewById(R.id.recyclerid);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setHasFixedSize(true);
//       // adapter = new MyAdapter();
//        recyclerView.setAdapter(adapter);
        mydialog.setView(myview)
        val dialog = mydialog.create()
        dialog.setCancelable(false)
        val name = myview.findViewById<View>(R.id.name) as EditText
        val description = myview.findViewById<View>(R.id.description) as EditText
        val btnCancel = myview.findViewById<Button>(R.id.btnCancel)
        val btnSave = myview.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val mName = name.text.toString().trim { it <= ' ' }
            val mDescription = description.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(mName)) {
                name.error = "Required Field.."
            }
            if (TextUtils.isEmpty(mDescription)) {
                description.error = "Required Field.."
            }
            val id = mDatabase!!.push().key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(mName, mDescription, id, mDate)
            mDatabase!!.child(id!!).setValue(data)
            Toast.makeText(applicationContext, "Data Saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        val adapter: FirebaseRecyclerAdapter<Data, MyViewHolder> = object : FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data::class.java,
                R.layout.itemlayoutdesign,
                MyViewHolder::class.java,
                mDatabase
        ) {
            override fun populateViewHolder(viewHolder: MyViewHolder, model: Data, position: Int) {
                viewHolder.setName(model.name)
                viewHolder.setDescription(model.description)
                viewHolder.setDate(model.date)
                viewHolder.mView.setOnClickListener {
                    post_key = getRef(position).key
                    name = model.name
                    description = model.description
                    updateData()
                }
            }
        }
        recyclerView!!.adapter = adapter
    }

    class MyViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setName(name: String?) {
            val Mname = mView.findViewById<TextView>(R.id.name_item)
            Mname.text = name
        }

        fun setDescription(description: String?) {
            val Mdescription = mView.findViewById<TextView>(R.id.description_item)
            Mdescription.text = description
        }

        fun setDate(date: String?) {
            val Mdate = mView.findViewById<TextView>(R.id.date_item)
            Mdate.text = date
        }
    }

    fun updateData() {
        val mydialog = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val myview = inflater.inflate(R.layout.update_data, null)
        mydialog.setView(myview)
        val dialog = mydialog.create()
        //  dialog.setCancelable(false);
        val mName = myview.findViewById<EditText>(R.id.name)
        val mDescription = myview.findViewById<EditText>(R.id.description)
        mName.setText(name)
        mName.setSelection(name!!.length)
        mDescription.setText(description)
        mDescription.setSelection(description!!.length)
        val btnDelete = myview.findViewById<Button>(R.id.btnDelete)
        val btnUpdate = myview.findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            name = mName.text.toString().trim { it <= ' ' }
            description = mDescription.text.toString().trim { it <= ' ' }
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(name, description, post_key, mDate)
            mDatabase!!.child(post_key!!).setValue(data)
            dialog.dismiss()
        }
        btnDelete.setOnClickListener {
            mDatabase!!.child(post_key!!).removeValue()
            dialog.dismiss()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mAuth!!.signOut()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}