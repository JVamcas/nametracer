package com.petruskambala.namcovidcontacttracer

import android.content.Intent
import android.net.Uri
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
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var accountModel: AccountViewModel
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        accountModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment), drawer_layout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)

        val navBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(nav_view.getHeaderView(0))
        accountModel.currentAccount.observe(this, Observer {
            navBinding.user = it
            it?.apply {
                nav_view.menu.clear()
                if (it.accountType == AccountType.PERSONAL)
                    nav_view.inflateMenu(R.menu.person_menu)
                else nav_view.inflateMenu(R.menu.place_menu)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onSignOut(view: View) {
        drawer_layout.closeDrawer(GravityCompat.START)
        accountModel.signOut()
        accountModel.authState.observe(this, Observer { authState ->
            if (authState === AccountViewModel.AuthState.UNAUTHENTICATED) {
                accountModel.authState.removeObservers(this)
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
        bundle.putString("account", ParseUtil.toJson(accountModel.currentAccount.value))

        when (item.itemId) {
            R.id.nav_my_profile -> if (curDestId != R.id.profileFragment)
                navController.navigate(R.id.action_global_profileFragment, bundle)
            R.id.about_developer -> if (curDestId != R.id.aboutDeveloperFragment)
                navController.navigate(R.id.action_global_aboutDeveloperFragment, bundle)
            R.id.nav_visits -> if (curDestId != R.id.placeVisitedFragment)
                navController.navigate(R.id.action_global_placeVisitedFragment, Bundle().apply {
                    putString(Const.PERSON_ID, accountModel.currentAccount.value?.id)
                })

            R.id.possible_contacts ->{
                if (curDestId != R.id.placeVisitorsFragment) {
                    bundle.putString(Const.PERSON_ID, accountModel.currentAccount.value!!.id)
                    navController.navigate(R.id.action_global_placeVisitorsFragment, bundle)
                }
            }

            R.id.share_app -> {

            }
            R.id.follow_on_twitter -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.twitter_url))
                })
            }
            R.id.follow_on_linked_in -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.linkedin_url))
                })
            }
        }
        return false
    }
}
