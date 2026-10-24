# MyExpressiveApp: Claude Code Bağlam Dosyası

Bu dosya, bu repodaki her değişiklikten önce okunmalıdır. Aşağıdaki sürümler ve kurallar 2026 Ekim itibarıyla geçerlidir. Eski API'lere (XML, ViewBinding, KAPT, targetSdk 36, Compose 1.11 ve altı) geri dönme.

---

## FAZ 1: Temel ve Mimari

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
| Dil | Kotlin **2.2.0+** | Java yasak. Hilt 1.4.0 en az KGP 2.2.0 gerektirir. |
| UI | Jetpack Compose BOM **2026.08.00** (Compose 1.12 hedefi) | Compose-first. XML ve ViewBinding yasak. Kütüphane sürümlerini BOM yönetir, Compose modüllerine elle sürüm yazma. |
| Tasarım | Material 3 **1.5.0** (Expressive API'leri) | Expressive bileşenleri kullanmadan önce M3 1.5.0 API yüzeyinde adlarını ve imzalarını doğrula. |
| Build | **AGP 9.2.0+** | compileSdk 37 ve Compose için gereklidir. |
| DI | Hilt: Dagger Hilt + **androidx.hilt 1.4.0** | Annotation işleme için **KSP** kullan. KAPT yasak. |
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
- **Expressive bileşenler:** `ExpressiveFloatingToolbar`, `FlexibleTopAppBar`, `ExpressiveListItem`, `ButtonGroup`, `M3LoadingIndicator`. Bu bileşenlerin tam adı ve parametreleri M3 1.5.0'da doğrulanmadan kullanılmaz. Tahmin yoluyla import yazma.
- **Hareket (Motion):** Animasyon sabitleri `Motion.kt` içinde tanımlanır.
- **Wide Color Gamut ve Mesh Gradient** kullanımı yalnızca Compose 1.12 API'sinde doğrulandıktan sonra eklenir.

### 1.7 Hilt Kuralları

- Uygulama sınıfı `@HiltAndroidApp` ile işaretlenir.
- Activity ve Fragment'lar yerine Composable ekranlarında `hiltViewModel()` kullanılır. Bu fonksiyon `androidx.hilt` 1.4.0 ile gelir.
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
