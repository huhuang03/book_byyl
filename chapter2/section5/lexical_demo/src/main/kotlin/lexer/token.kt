package lexer

object Tag {
    val NUM = 256
    val ID = 257
    val TRUE = 258
    val FALSE = 259
}

open class Token(val tag: Int) {
    constructor(tag: Char): this(tag.toInt())
}

class Num(val value: Int): Token(Tag.NUM) {
}

class Word(tag: Int, val lexeme: String): Token(tag)

class Lexer {
    var line = 1
    var peek: Char = 0.toChar()
    var words = HashMap<String, Token>()

    init {
        reserve(Word(Tag.TRUE, "true"))
        reserve(Word(Tag.FALSE, "false"))
    }

    private fun reserve(word: Word) {
        words.put(word.lexeme, word)
    }

    fun scan(): Token {
        while (true) {
            peek = next()
            if (Character.isSpaceChar(peek)) continue
            else if (peek == '\n') {
                line++
                continue
            }
            else break
        }

        var rst = handleNum()

        if (rst == null) {
            rst = handleStr()
        }

        if (rst == null) {
            peek = ' '
            rst = Token(peek)
        }
        return rst
    }

    private fun handleNum(): Token? {
        if (Character.isDigit(peek)) {
            var rst = peek.toInt()
            peek = next()
            while (Character.isDigit(peek)) {
                rst *= 10
                rst += peek.toInt()
                peek = next()
            }
            return Num(rst)
        }
        return null
    }

    private fun handleStr(): Token? {
        if (Character.isAlphabetic(peek.toInt())) {
            val sb = StringBuilder()
            peek = next()
            while (Character.isAlphabetic(peek.toInt()) || Character.isDigit(peek)) {
                sb.append(peek)
            }
            val key = sb.toString()
            val value = words[key]
            return if (value == null) {
                val word = Word(Tag.ID, key)
                words.put(key, word)
                word
            } else {
                value
            }
        }
        return null
    }

    private fun next(): Char {
//        if (peek == ' ') {
            return System.`in`.read().toChar()
//        }
    }
}
