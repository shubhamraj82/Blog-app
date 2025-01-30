package com.example.blogapp
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.Adapter.PostAdapter
import com.example.blogapp.Model.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postModelList: MutableList<PostModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)


        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true


        recyclerView.layoutManager = layoutManager

        postModelList = ArrayList()


        loadPosts()
    }

    private fun loadPosts() {
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postModelList.clear()
                for (ds in dataSnapshot.children) {
                    val postModel = ds.getValue(PostModel::class.java)
                    postModel?.let { postModelList.add(it) }
                }
                postAdapter = PostAdapter(this@HomeActivity, postModelList)
                recyclerView.adapter = postAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "" + databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_add_post -> {
                startActivity(Intent(this, AddPostActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}