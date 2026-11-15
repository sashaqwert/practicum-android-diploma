package ru.practicum.android.diploma.rootactivity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    @Suppress("LateinitUsage")
    private lateinit var viewBinding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.bottomNavigationView) { v, insets ->
            val layoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = 0
            v.layoutParams = layoutParams
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment,
                R.id.favoriteFragment,
                R.id.developersFragment -> {
                    viewBinding.bottomNavigationView.isVisible = true
                    viewBinding.navDivider.isVisible = true
                }
                else -> {
                    viewBinding.bottomNavigationView.isVisible = false
                    viewBinding.navDivider.isVisible = false
                }
            }
        }

        viewBinding.bottomNavigationView.setupWithNavController(navController)

        // Пример использования access token для HeadHunter API
        // networkRequestExample(accessToken = BuildConfig.API_ACCESS_TOKEN)
    }

    /*private fun networkRequestExample(accessToken: String) {
        // ...
    }

     */

}
