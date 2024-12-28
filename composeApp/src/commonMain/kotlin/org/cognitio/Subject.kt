package org.cognitio


import androidx.compose.ui.graphics.Color

enum class Subject(val bgColor: Color, val textColor: Color) {
    MATHEMATICS(Color(0x1AFF386B), Color(0xFFFF386B)),
    COMPUTER_SCIENCE(Color(0x1A2DD6F5), Color(0xFF3DD6F5)),
    CHEMISTRY(Color(0x1AFFF238), Color(0xFFFFF238)),
    PHYSICS(Color(0x1A3DF549), Color(0xFF3DF549)),
    BIOLOGY(Color(0x1A803DF5), Color(0xFF803DF5)),
    GEOGRAPHY(Color(0x1AFF5733), Color(0xFFFF5733)),
    HISTORY(Color(0x1A1ABC9C), Color(0xFF1ABC9C)),
    ECONOMICS(Color(0x1A33FFBD), Color(0xFF33FFBD)),
    CIVICS(Color(0x1A4287F5), Color(0xFF4287F5)),
    ENGLISH(Color(0x1AF5A623), Color(0xFFF5A623)),
    OTHER(Color(0xFFE0E0E0), Color(0xFF757575));
}


fun mapSubjectNameToEnum(subjectName: String): Subject {
    return enumValues<Subject>().find { it.name.equals(subjectName, ignoreCase = true) } ?: Subject.OTHER
}
