package com.petruskambala.namcovidcontacttracer

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.petruskambala.namcovidcontacttracer.databinding.NavHeaderMainBinding
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthState
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthenticationViewModel
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var authModel: AuthenticationViewModel
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        authModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        authModel.currentAccount.observe(this, Observer {
            it?.apply {
                nav_view.menu.clear()
                if (it.accountType == AccountType.PERSONAL)
                    nav_view.inflateMenu(R.menu.person_menu)
                else nav_view.inflateMenu(R.menu.place_menu)
            }
        })
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment), drawer_layout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)

        val navBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(nav_view.getHeaderView(0))
        authModel.currentAccount.observe(this, Observer { navBinding.user = it })
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

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        val curDestId = navController.currentDestination?.id
        val bundle = Bundle()
        bundle.putString("account", ParseUtil.toJson(authModel.currentAccount.value))
        when (item.itemId) {
            R.id.nav_my_profile -> if (curDestId != R.id.profileFragment)
                navController.navigate(R.id.action_global_profileFragment, bundle)

        }
        return false
    }
}
