package com.bousmah.meteoapp_zayd.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bousmah.meteoapp_zayd.domain.model.Weather
import com.bousmah.meteoapp_zayd.presentation.components.CurrentWeather
import com.bousmah.meteoapp_zayd.presentation.components.WeatherDetails
import com.bousmah.meteoapp_zayd.presentation.components.WeatherForecast
import kotlin.math.sin

// Gradient based on weather
fun weatherGradient(icon: String): List<Color> = when {
    icon.contains("01") -> listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    icon.contains("02") || icon.contains("03") || icon.contains("04") ->
        listOf(Color(0xFF2C3E50), Color(0xFF3D5A80), Color(0xFF5B7BA8))
    icon.contains("09") || icon.contains("10") ->
        listOf(Color(0xFF0D1B2A), Color(0xFF1B2838), Color(0xFF2E4057))
    icon.contains("11") ->
        listOf(Color(0xFF1A0533), Color(0xFF2D1B69), Color(0xFF11998E))
    icon.contains("13") ->
        listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF), Color(0xFFC4E0E5))
    icon.contains("50") ->
        listOf(Color(0xFF3D4A5C), Color(0xFF5C6B7A), Color(0xFF8AA0B0))
    icon.endsWith("d") ->
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    else ->
        listOf(Color(0xFF0D0D1A), Color(0xFF1A1A3E), Color(0xFF0D2137))
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val gradientColors = state.weather?.icon?.let { weatherGradient(it) }
        ?: listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))

    val c1 by animateColorAsState(gradientColors[0], tween(1500), label = "")
    val c2 by animateColorAsState(gradientColors[1], tween(1500), label = "")
    val c3 by animateColorAsState(gradientColors[2], tween(1500), label = "")

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val orbOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c1, c2, c3)))
            .drawBehind { drawOrbs(orbOffset, c3) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item {
                Spacer(Modifier.height(56.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.onSearchQueryChange(it)
                    },
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.loadWeatherInfo(searchQuery)
                        }
                        focusManager.clearFocus()
                    },
                    isFocused = isSearchFocused,
                    onFocusChange = { isSearchFocused = it }
                )

                Spacer(Modifier.height(24.dp))
            }

            if (state.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.error?.let { error ->
                item { ErrorCard(error) }
            }

            state.weather?.let { weather ->
                item { CurrentWeather(weather) }
                item { Spacer(Modifier.height(8.dp)) }
                item { WeatherDetails(weather) }
                item { Spacer(Modifier.height(8.dp)) }
                item { WeatherForecast(state.forecast) }
            }
        }
    }
}

fun DrawScope.drawOrbs(offset: Float, color: Color) {
    val w = size.width
    val h = size.height

    val y1 = h * 0.2f + sin(offset * 2 * Math.PI.toFloat()) * 30f
    val y2 = h * 0.6f + sin((offset + 0.5f) * 2 * Math.PI.toFloat()) * 20f

    drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(w * 0.8f, y1),
            radius = 220f
        ),
        center = Offset(w * 0.8f, y1),
        radius = 220f
    )

    drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(w * 0.15f, y2),
            radius = 180f
        ),
        center = Offset(w * 0.15f, y2),
        radius = 180f
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit
) {
    val borderAlpha by animateFloatAsState(if (isFocused) 1f else 0.3f, label = "")

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(1.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, contentDescription = "Search")
            Spacer(Modifier.width(12.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { onFocusChange(it.isFocused) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            "Search city...",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                    innerTextField()
                }
            )

                if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                }
            }
        }
    }
}

@Composable
fun ErrorCard(error: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Red.copy(alpha = 0.2f))
            .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Warning, contentDescription = "Error")
            Spacer(Modifier.width(8.dp))
            Text(error)
        }
    }
}