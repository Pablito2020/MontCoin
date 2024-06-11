package com.pablofraile.montcoin.ui.common

import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.composables.ui.Menu
import com.composables.ui.MenuButton
import com.composables.ui.MenuContent
import com.composables.ui.MenuItem
import com.composables.ui.MenuScope
import com.composables.ui.rememberMenuState

@Composable
fun ChevronDown(
    color: Color
): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "ChevronDown",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(color),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 9f)
                lineToRelative(6f, 6f)
                lineToRelative(6f, -6f)
            }
        }.build()
    }
}

data class MenuAction<T>(val text: String, val value: T)


@Composable
fun <T> BoxScope.Menu(
    currentElement: T,
    onElementSelected: (T) -> Unit = {},
    elements: List<MenuAction<T>> = emptyList()
) {
    Menu(
        modifier = Modifier
            .align(Alignment.Center)
            .wrapContentWidth(),
        state = rememberMenuState(expanded = false)
    ) {
        Menu(
            currentElement = currentElement,
            onElementSelected = onElementSelected,
            elements = elements
        )
    }
}


@Composable
fun <T> MenuScope.Menu(
    currentElement: T,
    onElementSelected: (T) -> Unit = {},
    elements: List<MenuAction<T>> = emptyList()
) {
    MenuButton(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Image(
                ChevronDown(
                    MaterialTheme.colorScheme.onPrimaryContainer
                ), null
            )
            MenuContent(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface),
                hideTransition = fadeOut(),
            ) {
                elements.forEach {
                    MenuItem(it, it.value == currentElement) { item ->
                        onElementSelected(item)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> MenuScope.MenuItem(value: MenuAction<T>, isSelected: Boolean, onClick: (T) -> Unit) {
    val color = if (!isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceTint
    MenuItem(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).wrapContentWidth(),
        enabled = !isSelected,
        onClick = { onClick(value.value) })
    {
        BasicText(
            value.text,
            color = { color },
            modifier = Modifier
                .wrapContentHeight()
                .padding(vertical = 10.dp, horizontal = 10.dp)
        )
    }
}