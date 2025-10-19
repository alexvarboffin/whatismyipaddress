package com.hsalf.smileyrating.helper

class SmileyActiveIndicator {
    private val indicators: MutableSet<TouchActiveIndicator> = HashSet<TouchActiveIndicator>()

    val isActive: Boolean
        get() {
            for (indicator in indicators) {
                if (indicator.isBeingTouched()) {
                    return true
                }
            }
            return false
        }

    fun bind(indicator: TouchActiveIndicator?) {
        indicators.add(indicator!!)
    }
}
