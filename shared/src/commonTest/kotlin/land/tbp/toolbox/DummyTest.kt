package land.tbp.toolbox

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DummyTest : StringSpec() {
    init {
        "dummy test" {
			println("inside dummy test")
			1 shouldBe 1
        }
    }
}
