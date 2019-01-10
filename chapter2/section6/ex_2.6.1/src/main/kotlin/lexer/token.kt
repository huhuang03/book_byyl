// 1） 以//开始的注释，包括从它开始到这一行的结尾的所有字符。
// 2） 以/*开始的注释，包括从它到最后第一次出现的字符序列*/之间的
//     所有字符
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

    private fun skipSpace() {
        var current = ' '
        while (true) {
            current = next()
            if (Character.isSpaceChar(current)) continue
            else if (current == '\n') {
                line++
                continue
            }
            else break
        }
        putBack(current)
    }

    private fun skipComment(): Char? {
        val cur = skipSingleLineComment()
        return skipMultiLineComment(cur)
    }

    private fun skipSingleLineComment(): Char? {
        val current = next()
        if (current == '/') {
            val next = next()
            if (next == '/') {
                while (next() != '\n') {
                    continue
                }
                return null
            } else {
                putBack(next)
            }
        }
        return current
    }

    private fun skipMultiLineComment(pre: Char?): Char? {
        if (pre == '/') {
            val next = next()
            if (next == '*') {
                var lastSecond = next()
                var last = next()
                while (!(lastSecond == '*' && last == '/')) {
                    lastSecond = last
                    last = next
                }
                return null
            } else {
                putBack(next)
            }
        }
        return pre
    }

    fun scan(): Token {
        skipSpace()
        var current = skipComment()?: next()


        if (Character.isDigit(current)) {
            var rst = current.toInt()
            current = next()
            while (Character.isDigit(current)) {
                rst *= 10
                rst += current.toInt()
                current = next()
            }
            putBack(current)
            return Num(rst)
        } else if (Character.isAlphabetic(current.toInt())) {
            val sb = StringBuilder()
            current = next()
            while (Character.isAlphabetic(current.toInt()) || Character.isDigit(current)) {
                sb.append(current)
            }
            putBack(current)
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

        putBack(current)
        return Token(' ')
    }

    private fun next(): Char {
        return if (peek == ' ') {
            System.`in`.read().toChar()
        } else {
            val rst = peek
            peek = ' '
            rst
        }
    }

    private fun putBack(c: Char) {
        if (peek != ' ') {
            throw IllegalStateException("Buffer peek is full")
        }
        peek = c
    }

}
