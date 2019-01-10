// 拓展2.6.5节中的语法分析器，使它能够识别浮点数，比如2.、3.14和.5等
package lexer

object Tag {
    val NUM = 256
    val ID = 257
    val TRUE = 258
    val FALSE = 259
    val RELATION_OP= 260
}

open class Token(val tag: Int) {
    constructor(tag: Char): this(tag.toInt())
}

class Num(val value: Double): Token(Tag.NUM) {
}

class Word(tag: Int, val lexeme: String): Token(tag)

class Lexer {
    var line = 1
    var peek: Char = 0.toChar()
    var words = HashMap<String, Token>()

    init {
        reserve(Word(Tag.TRUE, "true"))
        reserve(Word(Tag.FALSE, "false"))
        reserve(Word(Tag.RELATION_OP, ">"))
        reserve(Word(Tag.RELATION_OP, ">="))
        reserve(Word(Tag.RELATION_OP, "<"))
        reserve(Word(Tag.RELATION_OP, "<="))
        reserve(Word(Tag.RELATION_OP, "=="))
        reserve(Word(Tag.RELATION_OP, "!="))
    }

    /**
     * 解析结果
     * @param remain 没有解析的词
     * @param token 解析生成的词
     */
    data class ParseRstItem(val remain: Char, val token: Token?)

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

    /**
     * @param pre 上一个解析剩下的词
     * @return 如果解析成功，返回
     */
    private fun scanRelationOperator(pre: Char?): ParseRstItem {
        val current = pre?: next()

        if (current == '>' || current == '<' || current == '!' || current == '=') {
            if (current == '>' || current == '<') {
                val next = next()
                return if (next == '=') {
                    ParseRstItem(' ', words[current.toString() + "="])
                } else {
                    putBack(next)
                    ParseRstItem(' ', words[current.toString()])
                }
            } else if (current == '=') {
                val next = next()
                return if (next == '=') {
                    ParseRstItem(' ', words["=="])
                } else {
                    putBack(next)
                    ParseRstItem(' ', words["="])
                }
            } else if (current == '!') {
                val next = next()
                return if (next == '=') {
                    ParseRstItem(' ', words["!="])
                } else {
                    putBack(next)
                    ParseRstItem(current, null)
                }
            }
        } else {
            return ParseRstItem(current, null)
        }
        return ParseRstItem(current, null)
    }

    private fun scanNum(pre: Char?): ParseRstItem {
        val current = pre?: next()
        var integerPart = 0
        var decimalPart = 0.1
        var decimalLevel = 0

        if (current != '.') {
            var next = next()
            integerPart = current.toInt()
            while (Character.isDigit(next)) {
                integerPart *= 10
                integerPart += next.toInt()
                next = next()
            }
            putBack(next)
        }

        var next = next()
        if (next == '.') {
            next = next()
            while (Character.isDigit(next)) {
                decimalPart *= 10
                decimalPart += next.toInt()
                decimalLevel *= 10

                next = next()
            }
            putBack(next)
        }

        if (decimalLevel > 0) {
            decimalPart = decimalPart / decimalLevel
        }

        return ParseRstItem(' ', Num(integerPart + decimalPart))
    }

    fun scan(): Token {
        skipSpace()
        var current = skipComment()?: next()


        val rst = scanRelationOperator(current)
        if (rst.token != null) return rst.token

        current = rst.remain


        if (Character.isDigit(current) || current == '.') {
            return scanNum(current).token!!
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
