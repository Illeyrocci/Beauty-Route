package com.illeyrocci.beautyroute.domain.model

data class Schedule(
    val days: ArrayList<Day> = arrayListOf()
) {
    data class Day(
        val dayStartUnixTime: Long = 0,
        val sections: Array<Appointment?> = Array(96) { null }
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Day

            if (dayStartUnixTime != other.dayStartUnixTime) return false
            if (!sections.contentEquals(other.sections)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = dayStartUnixTime.hashCode()
            result = 31 * result + sections.contentHashCode()
            return result
        }
    }
}
