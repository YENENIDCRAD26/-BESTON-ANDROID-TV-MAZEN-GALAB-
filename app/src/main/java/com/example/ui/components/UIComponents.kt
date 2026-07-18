package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun Badge(label: String, tone: String = "neutral") {
    val bgColor = when (tone) {
        "success" -> SuccessBg
        "info" -> InfoBg
        "warning" -> WarningBg
        "danger" -> DangerBg
        else -> NeutralBg
    }
    val textColor = when (tone) {
        "success" -> SuccessText
        "info" -> InfoText
        "warning" -> WarningText
        "danger" -> DangerText
        else -> NeutralText
    }
    val borderColor = when (tone) {
        "success" -> Color(0xFF34D399)
        "info" -> Color(0xFF38BDF8)
        "warning" -> Color(0xFFF59E0B)
        "danger" -> Color(0xFFEF4444)
        else -> Color(0xFF475569)
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun ActionButton(label: String, onClick: () -> Unit, tone: String = "primary", disabled: Boolean = false) {
    val bgColor = when {
        disabled -> Color.Transparent
        tone == "secondary" -> Color(0xFF0891B2)
        tone == "warning" -> Color(0xFFB45309)
        tone == "danger" -> Color(0xFFB91C1C)
        tone == "ghost" -> Color.Transparent
        else -> Primary
    }
    val contentColor = when {
        disabled -> TextSecondary
        tone == "ghost" -> Color(0xFFDBEAFE)
        else -> Color.White
    }
    
    val modifier = Modifier
        .defaultMinSize(minHeight = 46.dp)
        .let {
            if (tone == "ghost" || disabled) {
                it.border(1.dp, if (disabled) Color(0xFF475569).copy(alpha=0.5f) else Color(0xFF475569), RoundedCornerShape(14.dp))
            } else it
        }

    Button(
        onClick = onClick,
        enabled = !disabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextSecondary
        ),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SectionTitle(eyebrow: String?, title: String, subtitle: String?) {
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        if (eyebrow != null) {
            Text(
                text = eyebrow.uppercase(),
                color = Color(0xFF22D3EE),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Black
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .background(BgCard, RoundedCornerShape(22.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(22.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Transparent)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.58f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
    Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun ProgressBar(value: Int) {
    val progress = (value.coerceIn(0, 100) / 100f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFF1E293B))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(Color(0xFF38BDF8))
        )
    }
}
