class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.item_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController( navController )

//        appBarConfiguration = AppBarConfiguration( setOf( R.id.frame_search, R.id.frame_favorite, R.id.frame_settings) )
        appBarConfiguration = AppBarConfiguration( navController.graph )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}




//////////////////////////////////////////////
메인에서 사용되는 다른 사용법
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate( layoutInflater )}

    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        setBottomNav()

        changeSelectedItemId( R.id.fragment_home )
    }

    private fun setBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController( navController )

        val appBarConfiguration = AppBarConfiguration( setOf( R.id.fragment_home, R.id.fragment_info, R.id.fragment_setting) )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        mNavController = navController

        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.fragment_info || destination.id == R.id.fragment_info_child) {
                binding.toolbar.visibility = View.GONE
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    fun changeSelectedItemId( id: Int) {
        binding.bottomNavigationView.selectedItemId = id
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

