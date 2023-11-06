package com.dre.routes

import com.dre.model.Customer
import com.dre.model.UserSession
import com.dre.model.customerStorage
import com.dre.model.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.customerRouting() {
    authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
        route("/customer") {
            get {
                if (customerStorage.isNotEmpty()) {
                    call.respond(customerStorage)
                } else {
                    call.respondText("No Customer found", status = HttpStatusCode.OK)
                }
            }

            // ? denotes the path param id is optional, otherwise it will return 404 http code
            get("{id?}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.NotFound
                )

                val someQueryParamExample = call.parameters["pokemon"]
                someQueryParamExample?.let {
                    println("The pokemon is $it")
                }

                val customer = customerStorage.find { it.id == id } ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
                call.respond(customer)
            }

            post {
                /*
                * call.receive integrates with the configured Content Negotiation plugin. Calling it with the generic
                * parameter Customer automatically deserializes the JSON request body into a Kotlin Customer object
                * */
                val customer = call.receive<Customer>()
                customerStorage.add(customer)
                call.respondText("Customer stored correctly", status = HttpStatusCode.OK)
            }

            delete("{id?}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (customerStorage.removeIf { it.id == id }) {
                    call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
                } else {
                    call.respondText("Not Found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}