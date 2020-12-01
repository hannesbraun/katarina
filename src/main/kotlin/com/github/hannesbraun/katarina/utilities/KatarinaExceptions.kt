package com.github.hannesbraun.katarina.utilities

import java.lang.RuntimeException

/* An exception that is meant to be sent as an error message via Discord*/
open class KatarinaException(message: String) : RuntimeException(message)

class KatarinaGuildOnlyException(message: String) : KatarinaException(message)
class KatarinaParsingException(message: String) : KatarinaException(message)
class KatarinaUnconnectedException(message: String) : KatarinaException(message)
class KatarinaWrongChannelException(message: String) : KatarinaException(message)
class KatarinaNSFWException(message: String) : KatarinaException(message)
class KatarinaCCSException(message: String) : KatarinaException(message)
