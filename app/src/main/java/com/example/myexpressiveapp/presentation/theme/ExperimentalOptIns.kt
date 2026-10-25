package com.example.myexpressiveapp.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

// Expressive (deneysel) API opt-in'lerinin tek toplanma noktası.
// Expressive API kullanan declaration'lar bu marker'ı taşır; opt-in gereksinimi böylece
// yalnızca bu dosyadan yayılır.
@ExperimentalMaterial3ExpressiveApi
@Retention(AnnotationRetention.BINARY)
annotation class ExpressiveUi
