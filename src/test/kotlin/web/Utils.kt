package web

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.http4k.core.Response
import org.http4k.core.Status

fun hasStatus(status: Status) = object: BaseMatcher<Response>() {
    override fun describeTo(description: Description?) { }

    override fun matches(actual: Any?): Boolean {
        val response = actual as? Response ?: return false
        return response.status == status
    }
}

fun hasBody(body: String) = object: BaseMatcher<Response>() {
    override fun describeTo(description: Description?) { }

    override fun matches(actual: Any?): Boolean {
        val response = actual as? Response ?: return false
        return response.bodyString() == body
    }
}