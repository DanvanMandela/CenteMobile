package com.elmacentemobile.view.composable.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.control.PasswordEnum
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter
import com.elmacentemobile.data.model.converter.LoginDataTypeConverter
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.DisplayHash
import com.elmacentemobile.data.model.user.LoginUserData
import com.elmacentemobile.data.model.user.PendingTransaction
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.data.source.sync.SyncData
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.AppLogger.Companion.instance
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.binding.findActivity
import com.elmacentemobile.view.binding.isOnline
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.binding.navigateBack
import com.elmacentemobile.view.composable.PageState
import com.elmacentemobile.view.composable.landing.PageData
import com.elmacentemobile.view.composable.loading.LoadingPage
import com.elmacentemobile.view.composable.theme.AppBlueColorDark
import com.elmacentemobile.view.composable.theme.AppBlueColorLight
import com.elmacentemobile.view.composable.theme.GhostWhite
import com.elmacentemobile.view.dialog.AlertDialogFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.fragment.auth.AuthFragment
import com.elmacentemobile.view.fragment.auth.bio.BioInterface
import com.elmacentemobile.view.model.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginPage : Fragment(), AppCallbacks {
    private val workerModel: WorkerViewModel by viewModels()
    private val authModel: AuthViewModel by viewModels()
    private val widgetModel: WidgetViewModel by viewModels()
    private val staticModel: StaticDataViewModel by viewModels()
    private val viewMap = HashMap<String, ViewModel>()
    private val serverResponse = MutableLiveData<DynamicAPIResponse?>()
    private val dispose = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setModels()
        setFeedbackTimer()
        autoBio()
    }

    private fun setModels() {
        viewMap["authModel"] = authModel
        viewMap["widgetModel"] = widgetModel
    }

    private fun setFeedbackTimer() {
        var feedback = authModel.storage.feedbackTimer.value ?: 0
        feedback += 1
        authModel.storage.setFeedbackTimer(feedback)
    }

    private fun autoBio() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed(
            { setFingerPrint() },
            animationDuration.toLong()
        )
    }

    private fun setFingerPrint() {
        val state: Boolean = authModel.storage.bio.value ?: false
        if (state) {
            if ((requireActivity() as MainActivity).isBiometric()) {
                (requireActivity() as MainActivity).authenticateTo(object : BioInterface {
                    override fun onPin(pin: String) {
                        authModel.password.value = pin
                        auth(pin)
                    }
                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = staticModel.state.collectAsState()
                val sync = staticModel.synchronizing.collectAsState()
                AnimatedVisibility(
                    visible = state.value == PageState.Ui || state.value == PageState.Error,
                    enter = fadeIn(
                        initialAlpha = 0.4f
                    ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 250))
                ) {
                    Main(
                        pageData = PageData(
                            callbacks = this@LoginPage,
                            viewModel = viewMap
                        )
                    )
                }
                AnimatedVisibility(
                    visible = state.value == PageState.Loading,
                    enter = fadeIn(
                        initialAlpha = 0.4f
                    ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 250))
                ) {
                    LoadingPage(progress = MutableStateFlow(sync.value))
                }
            }
        }
    }

    override fun auth(pin: String?) {
        if (requireContext().isOnline()) {
            authModel.password.value = pin
            staticModel.state.value = PageState.Loading
            workerModel.routeData(viewLifecycleOwner, object : WorkStatus {
                override fun workDone(b: Boolean) {
                    if (b) {
                        dispose.add(
                            authModel.loginAccount(pin, requireActivity())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    success(it)
                                }, {
                                    staticModel.state.value = PageState.Error
                                    it.printStackTrace()
                                })
                        )
                    }
                }

                override fun progress(p: Int) {
                    AppLogger().appLog("Work", "$p")
                }
            })
        } else ShowToast(requireContext(), getString(R.string.no_connection), true)
    }

    private fun success(res: DynamicResponse) {
        AppLogger().appLog("Response", "${res.response}")
        try {
            AppLogger().appLog(
                "Response:Decrypt:", BaseClass.decryptLatest(
                    res.response,
                    authModel.storage.deviceData.value?.device,
                    true,
                    authModel.storage.deviceData.value?.run
                )
            )
            if (res.response.isNullOrBlank() || res.response == "ok") {
                staticModel.state.value = PageState.Error
                showError(getString(R.string.fatal_error))
            } else {
                val response = LoginDataTypeConverter().to(
                    BaseClass.decryptLatest(
                        res.response,
                        authModel.storage.deviceData.value?.device,
                        true,
                        authModel.storage.deviceData.value?.run
                    )
                )
                when (response?.status) {
                    StatusEnum.FAILED.type -> {
                        staticModel.state.value = PageState.Error
                        showError(response.message)
                    }
                    StatusEnum.PIN_CHANGE.type -> {
                        val dynamicAPIResponse = DynamicAPIResponseConverter().to(
                            BaseClass.decryptLatest(
                                res.response,
                                authModel.storage.deviceData.value?.device,
                                true,
                                authModel.storage.deviceData.value?.run
                            )
                        )
                        serverResponse.value = dynamicAPIResponse
                        AppLogger().appLog(
                            "Dynamic:Response:",
                            Gson().toJson(dynamicAPIResponse?.formField)
                        )
                        staticModel.state.value = PageState.Ui
                        getModule(response.formID, response.next)
                    }
                    StatusEnum.TOKEN.type -> auth(authModel.password.value)
                    StatusEnum.DYNAMIC_FORM.type -> {
                        staticModel.state.value = PageState.Ui
                        getModule(response.formID, response.next)
                    }
                    StatusEnum.SUCCESS.type -> {
                        ShowToast(requireContext(), getString(R.string.welcome_back))
                        instance.appLog("AUTH", Gson().toJson(response))
                        saveUserData(response)
                    }
                    StatusEnum.PHONE_REG.type -> {
                        staticModel.state.value = PageState.Ui
                        navigate(authModel.navigationDataSource.navigateToDisclaimer())
                    }
                }

            }
        } catch (e: Exception) {
            staticModel.state.value = PageState.Error
            e.printStackTrace()
        }
    }

    private fun saveUserData(response: LoginUserData) {
        updateActivationData(response)

        if (response.version != null)
            updateWidgets(response.version!!)
        else updateWidgets("R")

        if (response.accounts != null)
            authModel.storage.setAccounts(response.accounts!!.toMutableList())

        if (response.beneficiary != null)
            authModel.storage.setBeneficiary(response.beneficiary!!.toMutableList())

        if (response.modules != null)
            authModel.saveFrequentModule(response.modules)

        if (response.serviceAlerts != null)
            authModel.storage.setAlerts(response.serviceAlerts!!)

        if (response.hideModule != null)
            authModel.storage.setHiddenModule(response.hideModule!!)
        else authModel.storage.removeHiddenModule()

        if (response.disableModule != null)
            authModel.storage.setDisableModule(response.disableModule!!)
        else authModel.storage.removeDisableModule()

        if (response.pendingTrxDisplay != null)
            setPending(response)

    }

    @SuppressLint("NewApi")
    private fun setPending(response: LoginUserData) {
        try {
            response.pendingTrxDisplay?.forEach { map ->
                response.pendingTrxPayload?.forEach { pay ->
                    if (map["PendingUniqueID"] == pay["PendingUniqueID"]) {
                        val forms = response.pendingTrxFormControls
                            ?.filter { f -> f.moduleID == pay["ModuleID"] }!!.toMutableList()
                        val action = response.PendingTrxActionControls
                            ?.filter { a -> a.moduleID == pay["ModuleID"] }!!.toMutableList()

                        val pending = PendingTransaction(
                            display = DisplayHash(map),
                            payload = DisplayHash(pay),
                            form = forms,
                            action = action
                        )
                        widgetModel.savePendingTransaction(pending)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateActivationData(data: LoginUserData) {
        val userData = authModel.storage.activationData.value
        userData?.firstName = data.firstName
        userData?.lastName = data.lastName
        userData?.email = data.emailId
        userData?.imageURL = data.imageURL
        userData?.iDNumber = data.iDNumber
        userData?.loginDate = data.loginDate
        if (data.message != null && !TextUtils.isEmpty(data.message)) {
            AppLogger().appLog(
                "MESSAGE:LOCAL",
                "${authModel.storage.activationData.value?.message}"
            )
            AppLogger().appLog("MESSAGE:REMOTE", "${data.message}")
            userData?.message = data.message
        }
        authModel.storage.setActivationData(userData!!)
    }

    private fun updateWidgets(version: String) {
        authModel.storage.sync.asLiveData().observe(viewLifecycleOwner) { sync ->
            if (authModel.storage.version.value != version) {
                if (sync != null) {
                    staticModel.synchronizing.value = "${sync.percentage}%"
                    AppLogger().appLog("PROGRESS", Gson().toJson(sync))
                    if (sync.work >= 8) {
                        navigate()
                        authModel.storage.setVersion(version)
                        authModel.storage.setSync(SyncData(complete = true, work = 0))
                    } else if (sync.work <= 1) {
                        if (authModel.storage.version.value != "R")
                            workerModel.onWidgetData(viewLifecycleOwner, null)
                        else navigate()
                    }
                }
            } else navigate()

        }
    }

    private fun navigate() {
        Handler(Looper.getMainLooper()).postDelayed({
            authModel.storage.setInactivity(false)
            authModel.storage.setLoginTime(System.currentTimeMillis())
            navigate(authModel.navigationDataSource.navigateToHome())
        }, 3500)
    }

    private fun showError(message: String?) {
        AlertDialogFragment.newInstance(
            DialogData(
                R.string.error,
                message,
                R.drawable.warning_app
            ), childFragmentManager
        )
    }

    override fun navigateUp() {
        navigateBack()
    }

    override fun direction(directions: NavDirections?) {
        navigate(directions = directions!!)
    }

    private fun getModule(module: String?, next: String?) {
        instance.appLog("FORM", "$module")
        instance.appLog("NEXT:", "$next")
        dispose.add(
            widgetModel.getModule(module)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ v: Modules? ->
                    if (v != null) setNextForm(v, next) else AppLogger().appLog(
                        AuthFragment::class.java.simpleName, "No Module"
                    )
                }, { obj -> obj.printStackTrace() })
        )
    }

    private fun setNextForm(v: Modules?, next: String?) {
        instance.appLog("MODULE", Gson().toJson(v))
        dispose.add(
            widgetModel.getFormControl(v!!.moduleID, next)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list: List<FormControl?>? ->
                    setFormNavigation(list, v)
                }, { obj: Throwable -> obj.printStackTrace() })
        )
    }
}


@Composable
fun Main(pageData: PageData) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val scope = rememberCoroutineScope()
    val authModel = pageData.viewModel!!["authModel"] as AuthViewModel
    val passwordType = authModel.storage.passwordType.collectAsState()
    val bio = authModel.storage.bio.collectAsState()
    val user = authModel.storage.activationData.collectAsState()
    var boardType by remember { mutableStateOf(KeyboardType.Password) }


    LaunchedEffect(true) {
        scope.launch {
            if (passwordType.value != null)
                boardType = when (authModel.storage.passwordType.value) {
                    PasswordEnum.TEXT_PASSWORD.type -> KeyboardType.Password
                    PasswordEnum.NUMERIC_PASSWORD.type -> KeyboardType.NumberPassword
                    else -> KeyboardType.Password
                }
        }
    }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = AppBlueColorLight,
            contentColor = Color.White,
            navigationIcon = {
                IconButton(onClick = {
                    pageData.callbacks?.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.cent_login),
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    style = MaterialTheme.typography.body1,
                )
            })
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    .width(100.dp)
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontFamily = FontFamily(Font(R.font.poppins_regular)))) {
                        append(stringResource(id = R.string.hello_jane))
                    }
                    append(",")
                    withStyle(style = SpanStyle(fontFamily = FontFamily(Font(R.font.poppins_semi_bold)))) {
                        if (user.value != null)
                            append(" ${user.value?.firstName ?: stringResource(id = R.string.there)}")
                        else append(stringResource(id = R.string.there))
                    }
                }, modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                style = MaterialTheme.typography.h5
            )
            Text(
                text = user.value?.message ?: stringResource(id = R.string.welcome_cente),
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.subtitle2
            )

            TextField(
                value = password,
                label = {
                    Text(
                        text = stringResource(id = R.string.pin),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = Typography().body1
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = boardType,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    password = it
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = GhostWhite,
                    cursorColor = AppBlueColorDark,
                    focusedIndicatorColor = AppBlueColorDark,
                    focusedLabelColor = AppBlueColorDark
                ),
                textStyle = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_medium))
                ),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = if (passwordVisibility)
                                painterResource(id = R.drawable.baseline_visibility_24)
                            else painterResource(id = R.drawable.ic_baseline_visibility_off_24),
                            contentDescription = null
                        )

                    }

                }
            )

            Button(
                onClick = {
                    if (password.isBlank()) {
                        ShowToast(context, context.getString(R.string.pin_required))
                    } else {
                        pageData.callbacks?.auth(password)
                    }
                }, modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppBlueColorDark,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login_securely).uppercase(),
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    style = MaterialTheme.typography.button
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp
                    )
            ) {
                Button(
                    onClick = {
                        pageData.callbacks?.direction(authModel.navigationDataSource.navigateResetPinATM())
                    },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.forgot_pin),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = MaterialTheme.typography.button,
                        textAlign = TextAlign.End,
                        color = AppBlueColorDark

                    )
                }

            }

            if (bio.value != null && bio.value!!)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    IconButton(
                        onClick = {
                            (activity as MainActivity).authenticateTo(object : BioInterface {
                                override fun onPin(pin: String) {
                                    password = pin
                                    pageData.callbacks?.auth(pin)
                                }
                            })
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fingerprint_big),
                            contentDescription = null
                        )
                    }
                }


        }
    }

}


@Preview(showBackground = true)
@Composable
private fun DefaultLandingPreview() {
    Main(PageData())
}
