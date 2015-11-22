import com.stderr.jmetagraphx.{MethodCall, ASMClassVisitor}
import org.scalatest.FunSuite

class ASMClassVisitorSuite extends FunSuite {

  test("a test") {
    val in = classOf[String].getResourceAsStream("/java/lang/String.class")
    ASMClassVisitor.visit(in)
    assert(MethodCall.toSeq.nonEmpty)
  }

}