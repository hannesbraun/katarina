package com.github.hannesbraun.katarina.utilities

import java.lang.RuntimeException

open class KatarinaException(message : String) : RuntimeException(message)

class KatarinaGuildOnlyException(message : String) : KatarinaException(message)
class KatarinaParsingException(message: String) : KatarinaException(message)
class KatarinaUnconnectedException(message : String) : KatarinaException(message)
