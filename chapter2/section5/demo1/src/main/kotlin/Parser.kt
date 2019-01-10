class Parser {
    var lookahead = -1;

    init {
        lookahead = System.`in`.read()
    }

    fun expr() {
        term()
        while (true) {
            when (lookahead) {
                '+'.toInt() -> {
                    match('+'); term(); System.out.write('+'.toInt())
                }
                '-'.toInt() -> {
                    match('-'); term(); System.out.write('-'.toInt())
                }
                else -> return
            }
        }
    }

    fun term() {
        if (Character.isDigit(lookahead.toChar())) {
            System.out.write(lookahead); match(lookahead)
        }
        else throw Error("syntax error")
    }

    fun match(t: Char) {
        match(t.toInt())
    }

    fun match(t: Int) {
        if (lookahead == t) lookahead = System.`in`.read()
        else throw Error("syntax error")
    }

}

fun main(args: Array<String>) {
    val parse = Parser()
    parse.expr(); System.out.write('\n'.toInt())
}

