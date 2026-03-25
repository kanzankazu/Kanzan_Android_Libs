# kanzankazuLibs — Shared Base Library

External Android library di repo terpisah (`kanzankazu_base_libs`), di-reference oleh project utama (`kanzankazu_itung_itungan`) sebagai sibling directory.

Package root: `com.kanzankazu`

Semua dependency di-expose via `api` (bukan `implementation`), jadi project utama otomatis dapat akses ke semua transitive dependencies.

## Build Config
- `com.android.library`, Kotlin, kapt, parcelize
- compileSdk 34, minSdk 23, targetSdk 33
- ViewBinding + Compose enabled (compiler extension 1.3.1)
- JVM target 1.8

## Package Structure

### `kanzanbase` — Base Classes
Base classes untuk Activity, Fragment, ViewModel, Dialog, Presenter, Repository, Adapter.

```
kanzanbase/
  activity/
    BaseActivityBindingData.kt      # DataBinding Activity
    BaseActivityBindingView.kt      # ViewBinding Activity
    BaseActivityBindingViewV2.kt    # ViewBinding Activity v2
    BaseActivityCompose.kt          # Compose Activity
    BaseActivityStandart.kt         # Standard Activity (no binding)
  adapter/
    BaseFragmentStateAdapter.kt     # FragmentStateAdapter base
  dialog/
    bottom/                         # BottomSheet dialog bases
    standart/                       # Standard dialog bases
  fragment/
    BaseFragmentBindingData.kt      # DataBinding Fragment
    BaseFragmentBindingView.kt      # ViewBinding Fragment
    BaseFragmentBindingViewV2.kt    # ViewBinding Fragment v2
    BaseFragmentCompose.kt          # Compose Fragment
    BaseFragmentStandart.kt         # Standard Fragment
  superall/
    BaseActivitySuper.kt            # Super base Activity
    BaseDialogBottomFragmentSuper.kt
    BaseDialogFragmentSuper.kt
    BaseFragmentSuper.kt            # Super base Fragment
  BaseAdmob.kt                     # AdMob helper
  BaseApp.kt                       # Application base
  BaseAppWidgetProvider.kt          # Widget provider base
  BaseMessagingService.kt           # FCM service base
  BasePreference.kt                 # SharedPreferences base
  BasePresenter.kt / BasePresenterContract.kt / BaseViewPresenter.kt  # MVP pattern
  BaseRepository.kt                 # Repository base
  BaseView.kt                      # View contract
  BaseViewModel.kt                  # ViewModel base
```

### `kanzandatabase` — Database & Network Helpers

```
kanzandatabase/
  firebase/
    FilterCondition.kt              # Query filter conditions
    FilterOperator.kt               # Query filter operators
    realtimedatabase/
      RealtimeDatabase.kt           # FRDB interface
      RealtimeDatabaseImpl.kt       # FRDB implementation
      RealtimeDatabaseImplType.kt   # FRDB type helpers
  retrofit/
    BaseHttpClient.kt               # OkHttp client builder
  room/
    BaseDao.kt                      # Room DAO base
    BaseRepository.kt               # Room repository base
```

### `kanzanmodel` — Shared Models

```
kanzanmodel/
  GeneralCreateUpdateDeleteModel.kt # CRUD timestamp model
  GeneralMemberListModel.kt         # Member list model
  GeneralModel.kt                   # Generic base model
  GeneralOption.kt                  # Option/selection model
  GeneralTypeAlias.kt               # Common type aliases
  KanzanFirebaseNotificationConst.kt # Notification constants
  KanzanFirebaseNotificationModel.kt # Notification data model
```

### `kanzannetwork` — Network Utilities

```
kanzannetwork/
  InternetConnection.kt             # Internet connectivity checker
  NetworkEvent.kt                   # Network event types
  NetworkLiveData.kt                # LiveData for network state
  NetworkStatus.kt                  # Network status enum
  response/
    BaseApiResponse.kt              # Base API response wrapper
    FAuthResult.kt                  # Firebase Auth result wrapper
    FRDBResult.kt                   # Firebase RTDB result wrapper
    baseresponseold/                # Legacy response models
    kanzanbaseresponse/             # Current response models
```

### `kanzanutil` — Utilities & Extension Functions

```
kanzanutil/
  BaseConst.kt                      # Global constants
  BaseFragmentHelper.kt             # Fragment transaction helpers
  CrashlyticsTimberTree.kt          # Timber tree → Crashlytics
  DynamicFeatureLoader.kt           # Dynamic feature module loader
  EventBusHelper.kt                 # EventBus wrapper
  FontConfig.kt                     # Font configuration
  InAppUpdate.kt / InAppUpdateTest.kt # In-app update helpers
  OnSingleClickListener.kt          # Debounced click listener
  PageList.kt                       # Pagination helper
  PermissionUtil.kt                 # Runtime permission helper
  PictureUtil.kt                    # Camera/gallery picker
  RemoteConfig.kt                   # Firebase Remote Config helper
  RobotTyping.kt / TextTyper.kt / TypingTimer.kt # Typing animation
  RxBus.kt                          # RxJava event bus

  animation/
    AnimationUtils.kt               # Animation helpers
    DismissableAnimation.kt         # Swipe-to-dismiss animation
    RevealAnimationSettings.kt      # Circular reveal settings

  enums/
    CountryLocale.kt                # Country/locale enum
    MessageType.kt                  # Message type enum
    NotifTopics.kt                  # Notification topic enum

  errorHandling/
    GlobalExceptionHandler.kt       # Global uncaught exception handler

  image/
    FileManager.kt                  # File I/O utilities
    ImageCompressor.kt              # Image compression (Zelory Compressor)

  scheduler/
    AppSchedulerProvider.kt         # RxJava scheduler provider
    SchedulerProvider.kt            # Scheduler interface

  kanzanextension/
    # Android & Framework Extensions
    ActivityFragmentContextExt.kt   # Activity/Fragment/Context extensions
    AndroidExt.kt                   # Android system extensions
    AppExtension.kt                 # Application-level extensions
    BaseExt.kt                      # Generic base extensions
    BundleIntentExt.kt              # Bundle & Intent extensions
    CoExt.kt                        # Coroutine extensions
    ContextExt.kt                   # Context extensions
    DateTimeCalendarExt.kt          # Date/Time/Calendar extensions
    DateTimeCalendarObject.kt       # Date/Time utility objects
    DeviceExt.kt                    # Device info extensions
    DialogExt.kt                    # Dialog extensions
    DisplayExt.kt                   # Display/screen extensions
    FirebaseExt.kt                  # Firebase extensions
    FragmentExt.kt                  # Fragment extensions
    IntentExt.kt                    # Intent extensions
    JsonExt.kt                      # JSON/Gson extensions
    LiveDataExt.kt                  # LiveData extensions
    MenuExt.kt                      # Menu extensions
    NavHostFragmentExt.kt           # Navigation extensions
    NetworkExt.kt                   # Network extensions
    NotificationExt.kt              # Notification extensions
    NumberExt.kt                    # Number formatting extensions
    OneSignalExt.kt                 # OneSignal extensions
    OtherExt.kt                     # Miscellaneous extensions
    PendingIntentExt.kt             # PendingIntent extensions
    PermissionExt.kt                # Permission extensions
    RandomExt.kt                    # Random generation extensions
    RemoteViewExt.kt                # RemoteView extensions
    RxExtension.kt                  # RxJava extensions
    ServiceExt.kt                   # Service extensions
    SettingSystemExtension.kt       # System settings extensions
    ThrowableExt.kt                 # Throwable/Exception extensions
    TransitionExt.kt                # Transition animation extensions
    ValidationExtension.kt          # Input validation extensions

    # Type Extensions
    type/
      BooleanExt.kt                 # Boolean extensions
      CollectionExt.kt              # List/Collection extensions
      DoubleExt.kt                  # Double extensions
      EditeableExt.kt               # Editable extensions
      EnumExt.kt                    # Enum extensions
      FloatExt.kt                   # Float extensions
      IntExt.kt                     # Int extensions
      LongExt.kt                    # Long extensions
      MapExt.kt                     # Map extensions
      NumberExt.kt                  # Number extensions
      StringExt.kt                  # String extensions
      TExt.kt                       # Generic T extensions
      UriFileExt.kt                 # Uri/File extensions

    # View Extensions
    view/
      BottomNavigationViewExt.kt    # BottomNavigationView extensions
      CustomUiExt.kt                # Custom UI extensions
      EditTextExtension.kt          # EditText extensions
      ImageViewExtension.kt         # ImageView extensions (Glide)
      RadioExtension.kt             # RadioButton/RadioGroup extensions
      ScrollViewExtension.kt        # ScrollView extensions
      TabLayoutExt.kt               # TabLayout extensions
      TextViewExtension.kt          # TextView extensions
      ToolbarExt.kt                 # Toolbar extensions
      ViewExtension.kt              # Generic View extensions
      ViewPagerExt.kt               # ViewPager extensions
```

### `kanzanwidget` — Custom UI Widgets

```
kanzanwidget/
  # XML-based Widgets
  EditTextClearable.java            # EditText with clear button
  EditTextDistance.java             # EditText with distance formatting
  EditTextRupiah.kt                 # EditText with Rupiah formatting
  KanzanEditText.kt                 # Custom EditText
  KanzanEditTextComponent.kt        # EditText component wrapper
  KanzanEditTextRupiahComponent.kt  # Rupiah EditText component
  ImeAction.kt / InputType.kt      # IME action & input type enums
  TextViewNoPadding.kt              # TextView without padding
  RoundedCornersTransformation.kt   # Glide rounded corners

  camera/                           # Camera capture UI
  codescanner/                      # Barcode/QR code scanner (ZXing-based)
  dialog/
    BaseAlertDialog.kt              # Alert dialog base
    BaseInfoDialog.kt               # Info dialog base
    BaseProgressDialog.kt           # Progress dialog base
  multistateview/
    MultiStateView.kt               # Loading/Empty/Error/Content state view
  pinview/
    PinView.kt                      # PIN input view
  qrcode/                           # QR code generator
  recyclerview/
    base/                           # RecyclerView adapter bases
    genericadapter/                 # Generic type-safe adapter
    utils/                          # RecyclerView utilities
    widget/                         # Custom RecyclerView widgets
    KanzanRecyclerviewComponent.kt  # RecyclerView component wrapper
  showcase/                         # Feature showcase/onboarding
  spinner/
    SpinnerExt.kt                   # Spinner extensions
    adapter/                        # Spinner adapters
  textdrawable/
    ColorGenerator.kt               # Random color generator
    TextDrawable.kt                 # Text-to-drawable generator
  toolbar/
    KanzanToolbarComponent.kt       # Toolbar component
    KanzanToolbarTabLayoutComponent.kt # Toolbar + TabLayout component
  viewpager/
    base/                           # ViewPager adapter bases
    widget/                         # Custom ViewPager widgets
    KanzanViewPagerBottomNavView.kt # ViewPager + BottomNav
    KanzanViewPagerTabLayout.kt     # ViewPager + TabLayout

  # Compose Widgets
  compose/
    animation/
      BlurredAnimatedText.kt        # Blurred text animation
      PulseAnimation.kt             # Pulse animation
      TripleOrbitLoadingAnimation.kt # Loading animation
    extension/
      ComposeExt.kt                 # Compose utility extensions
    ui/
      AppTextStyle.kt               # App text styles
      Color.kt                      # Color definitions
      ColorScheme.kt                # Material 3 color scheme
      Dimens.kt                     # Dimension constants
      FontFamily.kt                 # Font family definitions
      Shape.kt                      # Shape definitions
      Theme.kt                      # App theme
      Type.kt                       # Typography
    util/
      internetconnectionobserver/   # Compose-aware network observer
    widget/
      FloatingActionButton.kt       # Custom FAB
      KanzanBarcodeScanner.kt       # Compose barcode scanner
      KanzanBarWidget.kt            # Bar chart widget
      KanzanBaseBottomSheet.kt      # Bottom sheet base
      KanzanBaseButton.kt           # Button base
      KanzanBaseDropdownMenu.kt     # Dropdown menu
      KanzanBaseLazyGeneric.kt      # Generic LazyColumn/LazyRow
      KanzanBaseScreen.kt           # Screen scaffold base
      KanzanBottomAppBar.kt         # Bottom app bar
      KanzanBottomNav.kt            # Bottom navigation
      KanzanIconBadge.kt            # Icon with badge
      KanzanImageLoader.kt          # Image loader (Glide/Coil)
      KanzanPinView.kt              # Compose PIN input
      KanzanSpinner.kt              # Compose spinner/dropdown
      KanzanTextDrawable.kt         # Compose text drawable
      KanzanTextField.kt            # Compose text field
      KanzanToastSnackbar.kt        # Toast/Snackbar helper
      KanzanTopAppBar.kt            # Top app bar
      otp/                          # OTP input widget
```

## Transitive Dependencies (via `api`)

Library ini meng-expose dependency berikut ke project utama:

- Core Android: core-ktx, multidex, appcompat, activity-ktx, fragment-ktx, constraintlayout, recyclerview, swiperefreshlayout, security-crypto
- Lifecycle: viewmodel, livedata, runtime, process, savedstate, extensions, compose integration
- Room: runtime, ktx, rxjava2, guava
- Navigation: fragment-ktx, ui-ktx, compose
- Coroutines: core, android, play-services
- Material Design: material 1.4.0
- Google Play: app-update, feature-delivery
- Play Services: ads, auth, maps
- Firebase: auth, firestore, realtime-database, storage, crashlytics, analytics, remote-config, performance, app-check, core
- Networking: Retrofit (gson, jackson, scalars, rxjava2 adapter), OkHttp (logging-interceptor), Volley
- Reactive: RxJava2, RxKotlin, RxAndroid
- UI: Glide, Lottie, CircleImageView, PhotoView, ExpandableLayout, ShapeImageView, ViewPagerIndicator, QRGenerator
- Media: ExoPlayer (core, ui, okhttp, dash, hls, smoothstreaming)
- Utilities: Timber, Compressor (Zelory), OneSignal, Chucker (debug)
- Compose: foundation, material, material3, ui, runtime, animation, tooling
- Social: Facebook SDK
- Debug: Chucker HTTP inspector

## Catatan Penting

- Semua dependency pakai `api` scope — jangan duplikasi di module lain kecuali perlu versi berbeda
- Library ini adalah fondasi dari semua module di project utama (`kanzankazu_itung_itungan`)
- Saat menambah dependency baru yang bersifat global, pertimbangkan menambahkannya di sini
- Base classes di `kanzanbase` adalah parent class dari base classes di `core:base` project utama
