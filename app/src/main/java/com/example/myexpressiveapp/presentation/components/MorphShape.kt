package com.example.myexpressiveapp.presentation.components

import androidx.compose.material3.toPath
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import com.example.myexpressiveapp.presentation.theme.ExpressiveUi

// İki MaterialShapes arasında ilerleme değerine (0..1) göre şekil geçişi çizer.
// Ölçekleme ve merkezleme, M3'ün RoundedPolygon.toShape() uygulamasıyla aynıdır.
@ExpressiveUi
class MorphShape(
    private val morph: Morph,
    private val progress: Float,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path = morph.toPath(progress = progress)
        path.transform(Matrix().apply { scale(x = size.width, y = size.height) })
        path.translate(size.center - path.getBounds().center)
        return Outline.Generic(path)
    }
}
