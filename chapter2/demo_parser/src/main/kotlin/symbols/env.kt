package symbols

import com.sun.xml.internal.bind.v2.model.core.ID
import java.util.*

class Symbol {

}

class Env(var prev: Env?) {
    val table = Hashtable<String, Symbol>()

    fun put(word: String, symbol: Symbol) {
        table[word] = symbol
    }

    fun get(word: String): Symbol? {
        var e: Env? = this
        while (e != null) {
            val found = e.table[word]
            if (found != null) return found
            e = e.prev
        }
        return null
    }

}

abstract class Expr {
    abstract fun lvalue(): Expr

    abstract fun rvalue(): Expr
}

class Id: Expr() {
    override fun lvalue(): Expr {
        return this
    }

    override fun rvalue(): Expr {
        return this
    }

}

class Constant: Expr() {
    override fun lvalue(): Expr {
        return this
    }

    override fun rvalue(): Expr {
        return this
    }

}

class Op()

///**
// * @param y 为数组名
// * @param index 为下表
// */
//class Access(val y: ID, val index: Expr): Expr() {
//    override fun rvalue(): Expr {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}

abstract class Stmt {
    fun newlabel(): String { return "" }
    fun emit(str: String) {}
    abstract fun gen()
}

class If(val E: Expr, val S: Stmt): Stmt() {
    private var after: String = newlabel()

    override fun gen() {
        val n = E.rvalue()
        emit("ifFalse " + n.toString() + " goto" + after)
        S.gen()
        emit(after + ":")
    }

}

