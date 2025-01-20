package com.example.mobile_musicapp.models

enum class SleepTimerOption(val title: String, val time: Int) {
    ONE_MINUTE("1 minute", 1),
    FIVE_MINUTES("5 minutes", 5),
    TEN_MINUTES("10 minutes", 10),
    FIFTEEN_MINUTES("15 minutes", 15),
    THIRTY_MINUTES("30 minutes", 30),
    SIXTY_MINUTES("60 minutes", 60);
}
