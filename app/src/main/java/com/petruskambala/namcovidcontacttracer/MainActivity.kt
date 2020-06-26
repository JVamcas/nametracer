package com.petruskambala.namcovidcontacttracer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.petruskambala.namcovidcontacttracer.databinding.NavHeaderMainBinding
import com.petruskambala.namcovidcontacttracer.model.UserType
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthState
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    private lateinit var authModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        authModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        authModel.currentUser.observe(this, Observer {
            it?.apply {
                navView.menu.clear()
                if (it.userType == UserType.PERSONAL)
                    navView.inflateMenu(R.menu.person_menu)
                else navView.inflateMenu(R.menu.place_menu)
            }
        })
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(navView.getHeaderView(0))
        authModel.currentUser.observe(this, Observer {
            navBinding.user = it
            println("Url is "+ it?.photoUrl.toString())
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onSignOut(view: View) {
        drawer_layout.closeDrawer(GravityCompat.START)
        authModel.signOut()
        authModel.authState.observe(this, Observer { authState ->
            if (authState === AuthState.UNAUTHENTICATED) {
                authModel.authState.removeObservers(this)
                Toast.makeText(this@MainActivity, "Logout Successfully!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
