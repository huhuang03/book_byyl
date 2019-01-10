fun createStmtBySingleExpr(expr: Expr): Stmt {
    TODO()
}

abstract class Stmt {
    fun newlabel(): String { return "" }
    fun emit(str: String) {}
    abstract fun gen()
}

abstract class Expr {
    abstract fun lvalue(): Expr

    abstract fun rvalue(): Expr
}

class For(val init: Expr, val judge: Expr, val smt: Stmt): Stmt() {

    override fun gen() {
        createStmtBySingleExpr(init).gen()
        val begin = newlabel()
        val end = newlabel()
        emit("$begin:")
        val judge_value = judge.rvalue()
        emit("ifFalse" + judge_value.toString() + " goto " + end)
        smt.gen()
        emit(("goto $begin"))
        emit("$end:")
    }
}
