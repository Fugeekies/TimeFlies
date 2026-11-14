package io.github.fugeekies.timeflies.data.events

/** Lower-cases [text] and removes the accents of Latin letters, so that "écran" matches "Ecran". */
internal fun fold(text: String): String {
    val builder = StringBuilder(text.length)
    for (char in text.lowercase()) {
        when (char) {
            'à', 'á', 'â', 'ã', 'ä', 'å' -> builder.append('a')
            'ç' -> builder.append('c')
            'è', 'é', 'ê', 'ë' -> builder.append('e')
            'ì', 'í', 'î', 'ï' -> builder.append('i')
            'ñ' -> builder.append('n')
            'ò', 'ó', 'ô', 'õ', 'ö' -> builder.append('o')
            'ù', 'ú', 'û', 'ü' -> builder.append('u')
            'ý', 'ÿ' -> builder.append('y')
            'œ' -> builder.append("oe")
            'æ' -> builder.append("ae")
            '’' -> builder.append('\'')
            else -> builder.append(char)
        }
    }
    return builder.toString()
}
