package com.example.composetypingtoolbarself

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetypingtoolbarself.ui.theme.ComposeTypingToolbarselfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTypingToolbarselfTheme {
                TypingToolBar()
            }
        }
    }
}

data class AnnotationStyle(
    val text: Char,
    val style: SpanStyle
)

@Composable
fun TypingToolBar() {
    var text by remember { mutableStateOf(TextFieldValue(AnnotatedString(""))) }
    var textList = remember { mutableStateListOf<AnnotationStyle>() }

    var isUnderline by remember { mutableStateOf(false) }
    var isBold by remember { mutableStateOf(false) }
    var isH1 by remember { mutableStateOf(false) }
    var isH2 by remember { mutableStateOf(false) }

    fun getStyle(): SpanStyle {
        return SpanStyle(
            fontWeight = if (isBold) FontWeight.Bold else null,
            textDecoration = if (isUnderline) TextDecoration.Underline else null,
            fontSize = when {
                isH1 -> 30.sp
                isH2 -> 23.sp
                else -> 18.sp
            }
        )

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                text,
                placeholder = { Text("Type something here...") },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxSize()
                    .weight(1f),
                onValueChange = { newValue ->
                    val cursor = newValue.selection
                    val newText = newValue.annotatedString
                    val oldText = text.annotatedString

                    if (newText.length > oldText.length && cursor.start == cursor.end) {
                        if (cursor.start < newText.length) {
                            textList.add(
                                cursor.start - 1,
                                AnnotationStyle(newText.text[cursor.start - 1], getStyle())
                            )
                        } else {
                            textList.add(
                                AnnotationStyle(
                                    newText.text.last(),
                                    getStyle()
                                )
                            )
                        }
                    }

                    if (newText.length < oldText.length && cursor.start == cursor.end) {
                        textList.removeAt(cursor.start)
                    }

                    text = newValue.copy(annotatedString = buildAnnotatedString {
                        textList.forEachIndexed { index, item ->
                            append(item.text)
                            addStyle(item.style, index, index + 1)
                        }
                    })
                })
            Card(
                modifier = Modifier
                    .imePadding()
                    .animateContentSize()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(
                            if (WindowInsets.ime
                                    .asPaddingValues()
                                    .calculateBottomPadding() > 0.dp
                            ) PaddingValues(bottom = 0.dp) else WindowInsets.navigationBars.asPaddingValues()
                        )
                        .padding(5.dp)
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ToolKitButton(isBold, R.drawable.bold, label = null) {
                                isBold = !isBold
                            }
                            ToolKitButton(isUnderline, R.drawable.underline, label = null) {
                                isUnderline = !isUnderline
                            }
                            ToolKitButton(isH1, icon = null, label = "H1") {
                                isH1 = !isH1
                                if (isH2) isH2 = false
                            }
                            ToolKitButton(isH2, icon = null, label = "H2") {
                                isH2 = !isH2
                                if (isH1) isH1 = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolKitButton(active: Boolean, icon: Int?, label: String?, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = if (active) IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) else IconButtonDefaults.iconButtonColors()
    ) {
        icon?.let {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
        }
        label?.let {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}