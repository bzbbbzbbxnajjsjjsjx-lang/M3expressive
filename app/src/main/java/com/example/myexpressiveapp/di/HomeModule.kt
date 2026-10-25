package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.domain.model.HomeItem
import com.example.myexpressiveapp.domain.usecase.GetHomeItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    // Veri katmanı henüz yok; repository bağlanana kadar sabit örnek veri döner.
    @Provides
    fun provideGetHomeItemsUseCase(): GetHomeItemsUseCase =
        GetHomeItemsUseCase { flowOf(SampleHomeItems) }

    private val SampleHomeItems = listOf(
        HomeItem("1", "Sabah toplantısı", "Ekip senkronizasyonu, 15 dakika", "Çalışma"),
        HomeItem("2", "Proje teslimi", "Raporun son gözden geçirmesi", "Çalışma"),
        HomeItem("3", "Kod inceleme", "İki açık pull request var", "Çalışma"),
        HomeItem("4", "Tasarım taslağı", "Ana ekran için yeni düzen", "Çalışma"),
        HomeItem("5", "Haftalık planlama", "Gelecek haftanın öncelikleri", "Çalışma"),
        HomeItem("6", "Market alışverişi", "Süt, yumurta, ekmek", "Kişisel"),
        HomeItem("7", "Kitap okuma", "Bölüm 4, 20 sayfa", "Kişisel"),
        HomeItem("8", "Arkadaş buluşması", "Cumartesi akşamı, 20:00", "Kişisel"),
        HomeItem("9", "Koşu", "5 km, hafif tempo", "Sağlık"),
        HomeItem("10", "Su hatırlatıcısı", "Günlük hedef: 2 litre", "Sağlık"),
        HomeItem("11", "Uyku düzeni", "Yatma saati 23:30", "Sağlık"),
        HomeItem("12", "Uçuş kontrolü", "Check-in 24 saat önce açılır", "Seyahat"),
        HomeItem("13", "Bagaj listesi", "Şarj aletleri ve ilaçlar", "Seyahat"),
        HomeItem("14", "Otel rezervasyonu", "Onay e-postası bekleniyor", "Seyahat"),
    )
}
