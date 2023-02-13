1. 아래의 youtube를 통해서 내용을 작성함.
     https://www.youtube.com/watch?v=D3JCtaK8LSU

2. 권한을 보여주는 Util을 만든다.
/////////////////////////////////////////////////////////////////////////
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss : () -> Unit, 
    onOkClick: () -> Unit, 
    onGotoAppSettingsClick: () -> Unit,
    modifier : Modifier = Modifier 
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(
                    text = if(isPermanentlyDeclined) {
                        "Grant permission"
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            if(isPermanentlyDeclined) {
                                onGotoAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text("Permission required")
        },
        text = {
            Text( text = permissionTextProvider.getDescritpion(isPermanentlyDeclined))
        },
        modifier = modifier
    )

}

interface PermissionTextProvider {
    fun getDescritpion(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider: PermissionTextProvider {
    override fun getDescritpion(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "is seeems.. you permenantly  Camera declined.. goto app settings."
        } else {
            "This app needs access to your camera to ... "
        }
    }

}

class RecordAudioPermissionTextProvider: PermissionTextProvider {
    override fun getDescritpion(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "is seeems.. you permenantly Record Audio declined.. goto app settings."
        } else {
            "This app needs access to your Record Audio to ... "
        }
    }

}

class PhoneCallPermissionTextProvider: PermissionTextProvider {
    override fun getDescritpion(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "is seeems.. you permenantly phone call declined.. goto app settings."
        } else {
            "This app needs access to your phone call to ... "
        }
    }

}
/////////////////////////////////////////////////////////////////////////

3. ViewModel을 생성한다.
class MainViewModel: ViewModel() {

    // [CAMERA, ]
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    // reversed되어 있어서 제일위에 있는 거니깐 이것을 없앤다.
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}

4. 실제로 위의 두 함수를 사용하는 MainActivity를 작성한다.
@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : ComponentActivity() {

    private val permissionsToRequest = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewPermissionTestTheme {
                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        Log.d("klog", "onCreate: isGranted ==> ${isGranted}")
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.CAMERA,
                            isGranted = isGranted
                        )

                    }
                )

                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            viewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        cameraPermissionResultLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }) {
                        Text( "Request one permission")

                    }
                    Spacer( Modifier.height(16.dp))
                    Button(onClick = {
                        multiplePermissionResultLauncher.launch(
                            permissionsToRequest
                        )
                    }) {
                        Text( "Request multiple permission")

                    }
                }

                dialogQueue.reversed()
                    .forEach { permission ->
                        PermissionDialog (
                            permissionTextProvider = when(permission) {
                                Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                                Manifest.permission.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
                                Manifest.permission.CALL_PHONE -> PhoneCallPermissionTextProvider()
                                else -> return@forEach

                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf( permission )
                                )
                            },
                            onGotoAppSettingsClick = ::openAppSettings,
                        )
                    }
            }
        }
    }
}


fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null )
    ).also(::startActivity)
}

5. 권한에 관련된 코드 작성은 위의 코드를 보고 향후 작업을 진행하면 될 것 같다.

// 끝..

