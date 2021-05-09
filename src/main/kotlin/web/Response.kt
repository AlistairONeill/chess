package web

import com.ubertob.kondor.json.ObjectNodeConverter
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Response
import org.http4k.core.with
import org.http4k.format.Jackson
import org.http4k.format.Jackson.json
import org.http4k.lens.string

fun <T: Any> Response.bodyAsJson(converter: ObjectNodeConverter<T>, instance: T) =
    with(
        Body.json().toLens() of Jackson.parse(
            converter.toJson(instance)
        )
    )

fun Response.bodyAsText(body: String) =
    with (
        Body.string(ContentType.TEXT_PLAIN).toLens() of body
    )