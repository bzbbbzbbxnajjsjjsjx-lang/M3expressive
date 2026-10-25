# MyExpressiveApp: Claude Code Bağlam Dosyası

Bu dosya, bu repodaki her değişiklikten önce okunmalıdır. Aşağıdaki sürümler ve kurallar 2026 Ekim itibarıyla geçerlidir. Eski API'lere (XML, ViewBinding, KAPT, targetSdk 36, Compose 1.11 ve altı) geri dönme.

---

## FAZ 1: Temel ve Mimari

## 1.0 Tasarım İlkesi (Değişmez)

Amaç: Sadece görsel olarak etkileyici değil, aynı zamanda kullanımı doğal ve keyifli hissettiren bir arayüz oluşturmak. M3 Expressive'in hareket fiziği, şekil morphing ve vurgulu tipografi gibi özellikleri, etkileşim kalitesini artırmak için işlevsel araçlar olarak kullanılır.

- Material 3, projenin **temelidir**. Expressive, M3'ün yerine geçmez; onun üzerine binen bir katmandır.
- Ayrı bir "material3-expressive" paketi **yoktur**. Tüm Expressive özellikleri `androidx.compose.material3:material3` içinde gelir.
- Stable M3 API'leri (renk rolleri, 15 tipografi rolü, köşe ölçeği, temel bileşenler) her zaman kullanılabilir.
- Expressive API'leri (`MaterialExpressiveTheme`, `MotionScheme.expressive()`, `MaterialShapes`, `HorizontalFloatingToolbar`, `SegmentedListItem` vb.) opt-in gerektirir ve deneysel hattadır.
- Kural: Bir ekran önce stable M3 ile kurulur; Expressive katmanı (şekil morphing, yay fiziği, vurgulu tipografi) üzerine eklenir. Ters sıra yasaktır.

**YANLIŞ örnekler:**
- Yanlış: `material3-expressive` gibi ayrı bir bağımlılık eklemek.
- Yanlış: Expressive bileşeni, stable M3 katmanı kurulmadan doğrudan bir ekrana yazmak.
- Yanlış: `@ExperimentalMaterial3ExpressiveApi` opt-in'ini `ExperimentalOptIns.kt` dışında yazmak.
- Yanlış: `MaterialExpressiveTheme` ile sabit renk, şekil ya da süre yazmak.

### 1.1 Proje Kimliği

- **Proje adı:** MyExpressiveApp (yer tutucu)
- **Hedef:** Compose-first, Material 3 Expressive, yalnızca Android 17 (API 37) ve üzeri cihazlar (kişisel kullanım)
- **minSdk:** 37
- **targetSdk:** 37
- **compileSdk:** 37

minSdk 37 olduğu için alt sürümler için `Build.VERSION.SDK_INT` kontrolü, `@RequiresApi` veya geriye dönük uyumluluk katmanı **yazma**. Bu tür kodlar bu projede gereksizdir ve ölü kod olarak kalır.

### 1.2 Teknoloji Yığını ve Zorunlu Sürümler

| Katman | Teknoloji / Minimum Sürüm | Kritik Not |
|--------|---------------------------|------------|
| Dil | Kotlin **Sürümler libs.versions.toml'da pinlenir; değer Gradle çıktısından alınır.** | Java yasak. |
| UI | Jetpack Compose BOM **Sürümler libs.versions.toml'da pinlenir; değer Gradle çıktısından alınır.** | Compose-first. XML ve ViewBinding yasak. Kütüphane sürümlerini BOM yönetir, Compose modüllerine elle sürüm yazma. |
| Tasarım | Material 3 (kararlı baseline); Expressive API'ler için alpha hattı (pinli) | Expressive API'ler kararlı baseline'da yoktur. Sürüm numaraları `libs.versions.toml`'da tutulur. Bkz. 2.0. |
| Build | **AGP: Sürümler libs.versions.toml'da pinlenir; değer Gradle çıktısından alınır.** | compileSdk 37 ve Compose için gereklidir. |
| DI | Hilt: Dagger Hilt + **androidx.hilt: Sürümler libs.versions.toml'da pinlenir; değer Gradle çıktısından alınır.** | Annotation işleme için **KSP** kullan. KAPT yasak. |
| Mimari | MVVM + Clean Architecture | Bkz. 1.4 |
| Test | Compose **v2 Test API** (`runComposeUiTest`) | v1 API'leri kullanma. Varsayılan coroutine dispatcher: `StandardTestDispatcher`. |

Sürümler `gradle/libs.versions.toml` (version catalog) içinde tanımlanır. Sürüm numaralarını `build.gradle.kts` dosyalarına veya Kotlin kaynak kodlarına yazma.

### 1.3 Android 17 (API 37) Davranış Değişiklikleri

Bu kurallara uymak zorunludur:

- **Yönlendirme ve yeniden boyutlandırma kısıtlamaları kaldırıldı.** Büyük ekranlarda (`sw > 600dp`) `screenOrientation`, `setRequestedOrientation` gibi API'ler yok sayılır. Her ekran boyutuna uyan, esnek layout'lar yaz. Sabit boyut veya sabit yönlendirmeye dayanma.
- **Static final alanlar değiştirilemez.** `targetSdk 37` ile çalışan uygulamada yansıma (`reflection`) ile yazma `IllegalAccessException` fırlatır. JNI üzerinden yazma ise uygulamayı çökertir. Bu tür değişiklik yapma.
- **Katı bellek sınırı.** Sistem, uygulamaya bellek sınırı uygular. Büyük bitmap'lerde ve ağır Compose çizimlerinde bellek kullanımına dikkat et. Gerçek kullanımı Android Studio Profiler ile ölç. Sayısal bir sınır formülü varsayma.
- **Yeni `MessageQueue` implementasyonu.** API 37'de `android.os.MessageQueue` kilitsiz (lock-free) bir implementasyona geçti. `MessageQueue` özel alanlarına veya metotlarına yansıma ile erişen kod yazma. Ana iş parçacığı kuyruğuna doğrudan müdahale etme.

### 1.4 Mimari Katmanları ve Bağımlılık Kuralları

MVVM ve Clean Architecture. Bağımlılık yönü tek yönlüdür:

```
presentation  ──►  domain  ◄──  data
```

- **`domain/`:** Saf Kotlin. Android SDK, Compose, Hilt veya Room import edilmez. UseCase, Model ve Repository **arayüzleri** burada yaşar.
- **`data/`:** Repository implementasyonları, DataSource ve DTO'lar. Domain arayüzlerini uygular. DTO'lar domain modeline dönüştürülmeden presentation katmanına çıkmaz.
- **`presentation/`:** Screen (Composable), ViewModel, UiState ve Component. Domain UseCase'lerini çağırır.
- **`di/`:** Hilt modülleri. Bağlama (binding) kuralları burada tanımlanır.

Kurallar:
- Katman ihlali (örneğin `presentation` içinden doğrudan `data` sınıfını kullanmak) kabul edilmez.
- ViewModel, Compose tipi (`State`, `Modifier`, `Color` vb.) göstermez. UI durumu `StateFlow<UiState>` olarak dışa verilir.
- UiState tek bir `data class` veya `sealed interface` olarak modellenir. Birden fazla bağımsız `MutableState` tutma.
- Kullanıcı etkileşimleri ViewModel'e fonksiyon çağrısı olarak iletilir. ViewModel durumu tek yönlü günceller.
- Composable'lar mümkün olduğunca stateless olur (state hoisting). Ekran Composable'ı ViewModel'i alır, alt bileşenler yalnızca parametre alır.

### 1.5 Klasör Yapısı

```text
app/
├── data/          (Repository, DataSource, DTO)
├── domain/        (UseCase, Model, Repository arayüzleri)
├── presentation/  (Screen, ViewModel, UiState, Component)
│   ├── components/
│   ├── theme/       (Color.kt, Type.kt, Shape.kt, Theme.kt, Motion.kt)
│   └── navigation/
└── di/            (Hilt modülleri)
```

Yeni dosya eklerken bu yapıya uy. Yeni bir üst klasör açmadan önce bu dosyayı güncelle.

### 1.6 Compose ve M3 Expressive Kuralları

- **Compose-first.** Activity'ler `ComponentActivity` olur ve `setContent {}` kullanır. Fragment, XML layout ve ViewBinding kullanılmaz.
- **Tema:** Tüm tema tanımları `presentation/theme/` altındadır. `Theme.kt` tek giriş noktasıdır. Renk, tipografi, şekil ve hareket (`Motion.kt`) değerleri bu dosyalar dışında hardcode edilmez.
- **Expressive bileşenler:** `HorizontalFloatingToolbar`, `FlexibleTopAppBar`, `SegmentedListItem`, `ButtonGroup`, `M3LoadingIndicator`. Bu bileşenler deneysel hattadır ve `@ExperimentalMaterial3ExpressiveApi` arkasındadır. Tam adı ve parametreleri derleme ile doğrulanmadan kullanılmaz. Tahmin yoluyla import yazma. Opt-in'ler yalnızca presentation/theme/ExperimentalOptIns.kt içinde toplanır.
- **Hareket (Motion):** Animasyon sabitleri `Motion.kt` içinde tanımlanır.
- **Wide Color Gamut ve Mesh Gradient** kullanımı yalnızca Compose API'sinde derleme ile doğrulandıktan sonra eklenir.

### 1.7 Hilt Kuralları

- Uygulama sınıfı `@HiltAndroidApp` ile işaretlenir.
- Activity ve Fragment'lar yerine Composable ekranlarında `hiltViewModel()` kullanılır. Bu fonksiyon `androidx.hilt`, `libs.versions.toml`'da pinlenen sürümle gelir; değer Gradle çıktısından alınır.
- ViewModel'ler `@HiltViewModel` ile işaretlenir ve constructor injection kullanır.
- Bağlama tanımları `di/` altındaki modüllerde yapılır. Repository arayüzü ile implementasyonu arasındaki bağlama `@Binds` ile yapılır.
- Annotation işleme yalnızca KSP ile yapılır.

### 1.8 Build ve Sürüm Kuralları

- Sürümler yalnızca `gradle/libs.versions.toml` içinde tutulur.
- Kotlin yalnızca Kotlin olarak yazılır. Java kaynak dosyası eklenmez.
- Build değişiklikleri (AGP, Kotlin, KSP, Hilt, Compose BOM) tek bir commit'te, tüm bağımlılıkların uyumlu olduğu doğrulanarak yapılır.
- Eski `kapt` plugin'i, `androidx.compose.material` (M2) ve `androidx.appcompat` gibi kütüphaneleri eklemek yasaktır. M3 ve Compose dışında UI kütüphanesi eklenmez.

### 1.9 Faz 1 Kabul Kriterleri

Faz 1, aşağıdaki maddelerin tamamı sağlandığında tamamlanmış sayılır:

- [ ] `./gradlew assembleDebug` başarıyla derlenir.
- [ ] `./gradlew test` ve Compose UI testleri (v2 API) geçer.
- [ ] Uygulama boş bir Home ekranıyla açılır ve Expressive tema uygulanmıştır.
- [ ] Hilt grafiği derlenir ve bir ViewModel enjekte edilebilir.
- [ ] `domain/` paketinde Android veya Compose import'u yoktur.
- [ ] `minSdk`, `targetSdk` ve `compileSdk` değerleri 37'dir.
- [ ] Repoda XML layout, ViewBinding, KAPT veya Java kaynak dosyası yoktur.

---

## FAZ 2: Tasarım Sistemi (Renk, Tipografi, Şekil, Hareket)

### 2.0 Sürüm Politikası

- Sürümler `gradle/libs.versions.toml` içinde **pinlenir**. Değerler Android Studio'nun "Dependency Updates" önerisinden veya `./gradlew :app:dependencies` çıktısından alınır.
- `CLAUDE.md` içinde spesifik alpha sürüm numarası **yazılmaz**. Yalnızca iki kural yazılır: kararlı baseline kullanılır, Expressive API'ler opt-in'dir.
- Sürüm yükseltmesi: önce derle, hata yoksa commit et. Kaynak olarak build çıktısı kullanılır, dokümantasyon değil.
- Opt-in'ler dosya başına `@file:OptIn` olarak yazılır; `ExperimentalOptIns.kt` bunların listesini tutan bir belgedir.

> Doğrulama seviyeleri: ✅ = dokümantasyon düzeyinde doğrulandı (derleme teyidi değil); ⚠️ = derleme ile teyit edilecek; 🔨 = derleme ile teyit edildi.

### 2.1 Renk Sistemi (✅ Doğrulanmış)

- **Dinamik renk (Dynamic Color):** Standart M3 API'leriyle kullanılır. `minSdk 37` olduğu için koşulsuz etkindir; fallback palet yazılmaz.
- **Tonal paletler:** `primary`, `secondary`, `tertiary`, `neutral`, `neutralVariant`, `error`. Her biri 0–100 ton adımına sahiptir.
- **Kural:** Hex kodu yasaktır. Renkler her zaman `MaterialTheme.colorScheme` rolleri üzerinden kullanılır.
- **Zorunlu roller:** `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `secondary`, `tertiary`, `surface`, `surfaceContainerLow`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest`, `outline`, `error`.
- **Yüzey derinliği:** Gölge yerine `surfaceContainer*` seviyeleri ve `tonalElevation` kullanılır.

### 2.2 Tipografi (✅ Baseline / ⚠️ Emphasized derleme ile teyit)

- **Baseline ölçek:** 15 rol (`displayLarge` → `labelSmall`). ✅ Standart M3.
- **Emphasized ölçek:** 15 rolün bir kalınlık adımı yüksek varyantları. ✅ Kavramsal olarak M3 Expressive'te mevcut. ⚠️ Compose'daki birebir erişim yolu (ör. `MaterialTheme.typography.emphasized...`) derleme ile teyit edilecek.
- **Değişken font ekseni `ROND`:** ✅ `M3EVariableFontAxes` üzerinden erişilebilir. ⚠️ Projede kullanılacaksa derleme ile doğrulanacak.
- **Kural:** `MaterialTheme.typography` dışında sabit `fontSize` veya `fontWeight` yazılmaz.
- Vurgulu (emphasized) tipografi stilleri, bilgi hiyerarşisini güçlendirmek ve kullanıcının dikkatini önemli eylemlere (ör. 'kaydı başlat') veya bilgilere (ör. okunmamış mesajlar) çekmek için kullanılır. (API erişim yolu —ör. `MaterialTheme.typography.displayLargeEmphasized`— derleme ile teyit edilecek.)

### 2.3 Şekil Sistemi (✅ MaterialShapes doğrulandı)

- **Köşe ölçeği:** `extraSmall` (4dp), `small` (8dp), `medium` (12dp), `large` (16dp), `extraLarge` (28dp), `full` (pill). ✅ Standart M3.
- **`MaterialShapes`:** `Cookie4Sided`, `Cookie6Sided`, `Cookie9Sided`, `Cookie12Sided`, `ClamShell`, `Flower`, `Clover4Leaf`, `Clover8Leaf` vb. ✅ Mevcut; `RoundedPolygon` döndürür.
- **`Morph`:** Şekiller arası geçiş için sınıf mevcut. ⚠️ Bileşen bazlı bağlama kodu (ör. FAB morph) ilgili bileşenin API'sinden teyit edilecek.
- **Opt-in:** `MaterialShapes` kullanımı `@ExperimentalMaterial3ExpressiveApi` gerektirir. ✅ Doğrulandı. Opt-in'ler yalnızca presentation/theme/ExperimentalOptIns.kt içinde toplanır.
- **Kural:** `MaterialTheme.shapes` dışında sabit `RoundedCornerShape(dp)` yazılmaz. M3 Expressive şekilleri bu kuralın dışındadır.
- MaterialShapes ile gelen 35 dekoratif şekil ve yerleşik morph animasyonu, özellikle durum değişikliklerini (ör. bir butonun seçili hale gelmesi) görsel olarak anlaşılır kılmak için kullanılır.

### 2.4 Hareket Fiziği (✅ MotionScheme / ⚠️ token adları)

Stabil M3'teki sabit süreli animasyonlar yerine, yay tabanlı (spring-based) bir sistem kullanılır. Bu sayede etkileşimler daha canlı, akıcı ve doğal hissettirilir. Konum/boyut/şekil değişimlerinde spatial spring (aşırıya kaçabilir), renk/opaklık değişimlerinde ise effects spring (kritik sönümlemeli) kullanılır.

- **MotionScheme standard:** ✅ (stable M3). **MotionScheme.expressive():** ⚠️ (deneysel hat, opt-in gerektirir).
- **`defaultSpatialSpec()` ve `defaultEffectsSpec()`:** ✅ Doğrulandı. Spatial spring overshoot'a izin verir; effects spring critically damped'dır.
- **Kural:** Doğrusal (linear) animasyon yasaktır. Konum, boyut ve şekil için spatial spring; renk ve opaklık için effects spring kullanılır.
- **⚠️ Spesifik token adları ve emphasized easing:** Bu projede kullanılacaksa derleme ile teyit edilecek. Teyit edilmeden `CLAUDE.md`'ye token adı yazılmaz.

### 2.5 Tema İskeleti

- **`MaterialExpressiveTheme`:** ⚠️ Mevcut (deneysel hatta; derleme ile teyit edilecek). Expressive API'ler `@ExperimentalMaterial3ExpressiveApi` opt-in'i gerektirir.
- **Opt-in konumu:** Tüm opt-in'ler yalnızca `presentation/theme/ExperimentalOptIns.kt` içinde toplanır.
- **Parametreler:** `typography`, `shapes` ve `motionScheme` tema içinde tanımlanır.
- **Dinamik renk:** `minSdk 37` nedeniyle koşulsuz kullanılır.

```kotlin
// İskelet. API adları derleme ile doğrulanmalıdır.
@Composable
fun MyExpressiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = if (darkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
```

### 2.6 Doğrulama Tablosu

| Öğe | Durum | Not |
|-----|-------|-----|
| Dinamik renk API'leri ve renk rolleri | ✅ | Standart M3 |
| 15 baseline tipografi rolü | ✅ | Standart M3 |
| Köşe ölçeği | ✅ | 4/8/12/16/28/pill |
| `MaterialShapes` ve şekil adları | ✅ | Cookie*, ClamShell, Flower, Clover* |
| `MotionScheme` (`standard`/`expressive`) | standard ✅ / expressive ⚠️ | expressive deneysel, opt-in gerektirir |
| `MaterialExpressiveTheme` | ⚠️ (deneysel hatta; derleme ile teyit edilecek) | Opt-in gerektirir |
| Emphasized tipografi erişim yolu | ⚠️ | Derleme ile teyit |
| `ROND` ekseni kullanımı | ⚠️ | Derleme ile teyit |
| Morph bileşen bağlama kodu | ⚠️ | Bileşen API'sine göre |
| Spesifik spring token adları | ⚠️ | Derleme ile teyit |
| Sürüm numaraları | ⚠️ | `libs.versions.toml` ve Gradle çıktısından; `CLAUDE.md`'ye yazılmaz |

---

## FAZ 3: Bileşen Kuralları

## 3.1 Stable M3 Bileşen Kataloğu (Öncelikli)

Varsayılan olarak önce stable M3 bileşenleri kullanılır. Expressive bileşenler yalnızca stable M3 karşılığı yoksa veya tasarım gereği özellikle istenmişse tercih edilir. Kural: "Önce stable M3, yetmezse Expressive."

**Eylemler**
- Button, ElevatedButton, FilledTonalButton, OutlinedButton, TextButton
- FloatingActionButton, SmallFloatingActionButton, LargeFloatingActionButton, ExtendedFloatingActionButton
- IconButton, IconToggleButton, FilledIconButton, FilledTonalIconButton, OutlinedIconButton

**Kapsayıcılar**
- Card, ElevatedCard, OutlinedCard
- BottomSheetScaffold, ModalBottomSheet
- AlertDialog
- Badge, BadgedBox

**Gezinme**
- TopAppBar (ve varyantları), BottomAppBar
- NavigationBar, NavigationBarItem
- NavigationRail, NavigationRailItem
- ModalNavigationDrawer, PermanentNavigationDrawer, DismissibleNavigationDrawer
- Scaffold

**Seçim**
- Checkbox, TriStateCheckbox
- Switch
- RadioButton
- DropdownMenu, DropdownMenuItem
- FilterChip, AssistChip, InputChip, SuggestionChip (ve elevated varyantları)

**Metin Girişi**
- TextField, OutlinedTextField
- ExposedDropdownMenuBox

**Diğer**
- ListItem
- HorizontalDivider, VerticalDivider
- Snackbar
- DatePicker, DatePickerDialog, DateRangePicker

Kaynak: androidx.compose.material3 API referansı. Bu liste dışındaki bileşenler gerekiyorsa önce stable M3'te karşılığı olup olmadığı kontrol edilir.

Bu bileşenler varsayılan tercihtir. Expressive bileşenler (HorizontalFloatingToolbar, SegmentedListItem, ButtonGroup vb.) yalnızca stable M3 karşılığı yoksa veya tasarım gereği etkileşim kalitesini artırmak amacıyla bilinçli olarak istenmişse kullanılır.

### 3.2 Tercih Edilen M3 Expressive Bileşenleri

- **HorizontalFloatingToolbar:** Ekrandaki birincil eylem grubu için. Tek eylem için kullanılmaz; `FloatingActionButton` kullan.
- **FlexibleTopAppBar:** Kaydırmayla daralan büyük başlıklı ekranlar için. Sabit başlıklı kısa ekranlarda kullanılmaz.
- **ButtonGroup:** 2–5 ilişkili eylem veya seçenek için. Tek buton için kullanılmaz.
- **M3LoadingIndicator:** Süresi belirsiz yüklemeler için. İlerlemesi bilinen işlemlerde kullanılmaz; ilerleme göstergesi kullan.
- **SegmentedListItem:** Gruplanmış liste öğeleri için (ilk, orta, son köşe şekilleri). Tek öğelik listede veya gruplanmamış listede kullanılmaz.
- **MaterialShapes:** Dekoratif veya seçili durum şekilleri için (ör. morph). İşlevsel yüzeylerde (kart, buton, alan) `MaterialTheme.shapes` kullanılır.

Expressive bileşenler `@ExperimentalMaterial3ExpressiveApi` gerektirir. Opt-in'ler yalnızca `presentation/theme/ExperimentalOptIns.kt` içinde toplanır.

### 3.3 Yasaklar

- Material 2 görünümlü bileşen kullanılmaz (`androidx.compose.material`).
- Sabit renk, şekil veya süre yazılmaz; token kullanılır.
- Expressive bileşen, Expressive olmayan temayla karıştırılmaz.

### 3.4 Doğrulama

- API adları ve sürümler `libs.versions.toml` ve derleme çıktısından teyit edilir.
- `CLAUDE.md`'ye spesifik sürüm numarası yazılmaz.

---

## FAZ 4: Kod Üretim Kuralları

### 4.1 Kod Üretim Kuralları

- Her istekte tek bir Composable veya bileşen üretilir.
- Composable iş mantığı içermez; yalnızca `UiState` çizer.
- ViewModel, UseCase çağırır ve `StateFlow<UiState>` üretir.
- Her Composable için `@Preview` eklenir.
- Dosya başına en fazla 300 satır.
- Sabit hex renk, sabit dp ve sabit ms süre yasaktır; token kullanılır.

### 4.2 Standart Prompt Şablonu

```text
Jetpack Compose ile [bileşen] oluştur.
- Şekil: [MaterialTheme.shapes token]
- Renk: [MaterialTheme.colorScheme rolü]
- Hareket: [MotionScheme spec]
- Tipografi: [MaterialTheme.typography rolü]
- Opt-in: gerekliyse ExperimentalOptIns.kt
- Yapma: [varsa yasaklar]
```

### 4.3 Kontrol Listesi (Her Kod Üretiminden Sonra)

- [ ] Hex renk yok
- [ ] Renkler `MaterialTheme.colorScheme` üzerinden
- [ ] Şekiller: işlevsel yüzeyler `MaterialTheme.shapes`; dekoratif/seçili durum şekilleri `MaterialShapes` (Expressive katmanı).
- [ ] Animasyonlar spring tabanlı (linear yok)
- [ ] Opt-in'ler yalnızca `ExperimentalOptIns.kt` içinde
- [ ] `@Preview` var
- [ ] Dosya 300 satırın altında
- [ ] Composable içinde iş mantığı yok

### 4.4 Sürüm ve API Doğrulama

- `CLAUDE.md`'ye spesifik sürüm numarası yazılmaz.
- API adları ve sürümler `libs.versions.toml` ve derleme çıktısından teyit edilir.
- Doğrulanamayan API adı kullanılmaz; ⚠️ ile işaretlenir.
